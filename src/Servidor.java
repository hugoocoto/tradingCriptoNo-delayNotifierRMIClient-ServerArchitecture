import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Servidor extends UnicastRemoteObject implements IServidor {

    private class Alert {
        public String name;
        public Float price;
        public ICliente client;

        public Alert(String s, Float p, ICliente c) {
            name = s;
            price = p;
            client = c;
        }
    }

    private Parser p;
    private HashMap<String, Float> prices;
    private ArrayList<Alert> buyAlerts;
    private ArrayList<Alert> sellAlerts;
    private static final int RMI_PORT = 1099;
    private static final Integer WAIT_INTERVAL = 60; // seconds

    public void start() {
        mainloop();
        p.close();
    }

    public void mainloop() {
        for (;;) {
            prices = p.parse();
            print();
            check_notifications();
            try {
                wait(1000 * WAIT_INTERVAL);
            } catch (Exception e) {
            }
        }
    }

    public void print() {
        for (String name : prices.keySet()) {
            System.out.println("[" + name + "] " + prices.get(name));
        }
    }

    public void check_notifications() {
        check_buy_notifications();
        check_sell_notifications();
    }

    public void check_sell_notifications() {
        Iterator<Alert> iter = sellAlerts.iterator();
        while (iter.hasNext()) {
            Alert a = iter.next();
            if (prices.get(a.name) == null) {
                iter.remove();
            } else if (prices.get(a.name) >= a.price) {
                try {
                    a.client.NotifySellAlert(a.name, a.price);
                } catch (RemoteException e) {
                    System.out.println(e);
                } finally {
                    iter.remove();
                }
            }
        }
    }

    public void check_buy_notifications() {
        Iterator<Alert> iter = buyAlerts.iterator();
        while (iter.hasNext()) {
            Alert a = iter.next();
            if (prices.get(a.name) == null) {
                iter.remove();
            } else if (prices.get(a.name) <= a.price) {
                try {
                    a.client.NotifyBuyAlert(a.name, a.price);
                } catch (RemoteException e) {
                    System.out.println(e);
                } finally {
                    iter.remove();
                }
            }
        }
    }

    @Override
    public void addBuyAlert(String address, String accion, Float price) throws RemoteException {
        try {
            synchronized (buyAlerts) {
                buyAlerts.add(new Alert(accion, price, (ICliente) Naming.lookup(address)));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void addSellAlert(String address, String accion, Float price) throws RemoteException {
        try {
            sellAlerts.add(new Alert(accion, price, (ICliente) Naming.lookup(address)));
        } catch (Exception e) {
        }
    }

    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();

        } catch (RemoteException e) {
            System.out.println("RMI registry cannot be located at port " + RMIPortNum);
            LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum);
        }
    }

    private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
        System.out.println("Registry " + registryURL + " contains: ");
        String[] names = Naming.list(registryURL);
        for (String name : names) {
            System.out.println(name);
        }
    }

    public Servidor() throws RemoteException {
        super();
        p = new Parser();
        prices = new HashMap<>();
        buyAlerts = new ArrayList<>();
        sellAlerts = new ArrayList<>();
        servir(this, RMI_PORT);
    }

    public void servir(IServidor s, Integer puerto) {
        try {
            startRegistry(puerto);
            String registryURL = "rmi://localhost:" + puerto + "/Servidor";
            Naming.rebind(registryURL, s);
        } catch (Exception e) {
            System.out.println("servir: " + e);
        }
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(RMI_PORT);
            Servidor servidor = new Servidor();
            Naming.rebind("Servidor", servidor);
            System.out.println("Servidor RMI listo en puerto " + RMI_PORT);
            servidor.start();

        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
