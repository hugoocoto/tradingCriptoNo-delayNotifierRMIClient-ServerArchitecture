import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Cliente extends UnicastRemoteObject implements ICliente {
    private final static String SERVER_HOST = "localhost";
    private final static Integer SERVER_PORT = 1099;
    private IServidor servidor;

    private Integer puerto = 1100;
    private ArrayList<String> notifications;

    public Cliente() throws RemoteException {
        super();
        notifications = new ArrayList<>();
        servir(this, puerto);
        if (!connect()) {
            System.out.println("Can not connect to server");
            return;
        }
        System.out.println("Can not connect to server");
    }

    public boolean connect() {
        try {
            servidor = getServer("rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/Servidor");
            return true;

        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            System.out.println(e);
            return false;
        }
    }

    public void servir(ICliente cliente, Integer puerto) {

        try {
            startRegistry(puerto);
            String registryURL = "rmi://localhost:" + puerto + "/Cliente";
            Naming.rebind(registryURL, cliente);
        } catch (Exception e) {
            System.out.println("servir: " + e);
        }
    }

    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();

        } catch (RemoteException e) {

            System.out.println("RMI registry cannot be located at port " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum);
        }
    }

    private IServidor getServer(String host) throws MalformedURLException, RemoteException, NotBoundException {
        return (IServidor) Naming.lookup(host);
    }

    @Override
    public void NotifyBuyAlert(String accion, Float price) throws RemoteException {
        notifications.add("BUY! " + accion + "is at " + price + "!");
        notifyAll();
    }

    @Override
    public void NotifySellAlert(String accion, Float price) throws RemoteException {
        notifications.add("SELL! " + accion + "is at " + price + "!");
        notifyAll();
    }

    public static void main(String[] args) {
        try {
            Integer port = 1100;
            java.rmi.registry.LocateRegistry.createRegistry(port);
            Cliente cliente = new Cliente();
            Naming.rebind("Cliente", cliente);
            System.out.println("Servidor RMI listo en puerto " + port);
            cliente.start();

        } catch (Exception e) {
            System.out.println("Error iniciando cliente: " + e);
        }
    }

    public ArrayList<String> getNotifications() {
        return notifications;
    }
}
