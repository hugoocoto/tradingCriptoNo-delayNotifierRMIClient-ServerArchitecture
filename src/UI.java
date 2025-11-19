import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Pipe.SourceChannel;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.ArrayList;

public class UI {
    private BufferedReader br;
    private String nombre;
    private String clave;
    private Integer puerto;
    private boolean should_quit = false;
    private String prompt = " >> ";
    private Cliente cliente;
    private String state = "";

    public UI() throws RemoteException {
        br = new BufferedReader(new InputStreamReader(System.in));
        cliente = new Cliente();
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
            System.out.println(e);
            return "";
        }
    }

    private String ask() {
        return ask(prompt);
    }

    private void refresh() {
        System.out.print("\033[s");
        System.out.print("\033[H");
        System.out.println("\033[H\033[KIBEX notifications");
        displayListNoNum(cliente.getNotifications(), 10);
        System.out.print("\033[u");
    }

    private void displayListNoNum(ArrayList<String> list, Integer max) {
        max = Math.min(list.size(), max);
        for (int i = list.size() - max; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    private void mainloop() {
        clearScreen();
        while (true) {
            System.out.println("\033[H\033[KIBEX notifications");
            displayListNoNum(cliente.getNotifications(), 10);
            String resp = ask();
            if (resp.isEmpty())
                continue;
            if (cliente == null ||
                    resp.toLowerCase().equals("exit") ||
                    resp.toLowerCase().equals("quit") ||
                    resp.toLowerCase().equals("q")) {
                break;
            }

            String[] sresp = resp.split(" ");
            // "buy"/"sell" NAME "at" PRICE
            if (sresp.length != 4) {
                cliente.writeNotification();
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
            System.out.println(e);
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            new UI();
        } catch (RemoteException e) {
            System.out.println(e);
        }
    }
}
