import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Pipe.SourceChannel;
import java.rmi.RemoteException;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;

public class UI {
    private BufferedReader br;
    private String nombre;
    private String clave;
    private Integer puerto;
    private boolean should_quit = false;
    private String prompt = "\033[K >> ";
    private Cliente cliente;
    private String state = "";

    public UI() throws RemoteException {
        br = new BufferedReader(new InputStreamReader(System.in));
        cliente = new Cliente(Integer.parseInt(ask("Puerto: "))); // handle this
        StartUpdatelistener();
        mainloop();
    }

    private void StartUpdatelistener() {
        new Thread(() -> {
            while (true) {
                try {
                    synchronized (cliente) {
                        cliente.wait();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                refresh();
            }
        }).start();
    }

    private String ask(String prompt) {
        try {
            System.out.print(prompt);
            return br.readLine();

        } catch (IOException e) {
            // System.out.println(e);
            return "";
        }
    }

    private String ask() {
        return ask(prompt);
    }

    private void refresh() {
        synchronized (this) {
            System.out.print("\033[s");
            System.out.print("\033[H");
            System.out.println("\033[H\033[KIBEX notifications");
            displayListNoNum(cliente.getNotifications(), 10);
            System.out.print("\033[u");
        }
    }

    private void displayListNoNum(ArrayList<String> list, Integer max) {
        Integer nmax = Math.min(list.size(), max);
        for (int i = list.size() - nmax; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
        for (int i = nmax; i < max; i++) {
            System.out.println("");
        }
    }

    private void mainloop() {
        clearScreen();
        for (;;) {
            synchronized (this) {
                System.out.println("\033[H\033[KIBEX notifications");
                displayListNoNum(cliente.getNotifications(), 10);
                System.out.println("('buy'/'sell' NAME 'at' PRICE)");
            }
            String resp = ask();

            if (resp.isBlank()) {
                continue;
            }

            if (cliente == null ||
                    resp.trim().toLowerCase().equals("exit") ||
                    resp.trim().toLowerCase().equals("quit") ||
                    resp.trim().toLowerCase().equals("q")) {
                break;
            }

            String[] sresp = resp.trim().split(" ");
            if (sresp.length != 4 || !sresp[2].equals("at")) {
                cliente.writeNotification("Invalid: " + resp);
                continue;
            }

            Float price;
            try {
                price = Float.parseFloat(sresp[3]);
            } catch (NumberFormatException e) {
                cliente.writeNotification("Invalid action: " + sresp[0]);
                continue;
            }

            switch (sresp[0].toLowerCase().toCharArray()[0]) {
                case 'b':
                    cliente.addBuyAlert(sresp[1], price);
                    break;
                case 's':
                    cliente.addSellAlert(sresp[1], price);
                    break;
                default:
                    cliente.writeNotification("Invalid action: " + sresp[0]);
                    break;
            }

        }
    }

    private Integer getInteger(String s, Integer min, Integer max) {
        return getInteger(s, min, max, -1);
    }

    private Integer getInteger(String s, Integer min, Integer max, Integer def) {
        try {
            if (s == null || s.isEmpty())
                return def;
            Integer n = Integer.parseInt(s);
            if (n < min || n > max)
                return def;
            return n;
        } catch (Exception e) {
            return def;
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
    }

    private boolean ask_sn(String prompt) {
        try {
            System.out.print(prompt + " (S/n) ");
            char resp = br.readLine()
                    .toLowerCase().toCharArray()[0];
            return resp == 's' || resp == 'y';

        } catch (IOException e) {
            // System.out.println(e);
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            new UI();
        } catch (RemoteException e) {
            // System.out.println(e);
        }
    }
}
