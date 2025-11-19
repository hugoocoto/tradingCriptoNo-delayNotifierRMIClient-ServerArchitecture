import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICliente extends Remote {

    /*
     * Send (recive) a notification about a buy alert. The price of ACCION is
     * lower than the price set in the server. PRICE is the actual price.
     */
    void NotifyBuyAlert(String accion, Float price)
            throws RemoteException;

    /*
     * Send (recive) a notification about a sell alert. The price of ACCION is
     * greater than the price set in the server. PRICE is the actual price.
     */
    void NotifySellAlert(String accion, Float price)
            throws RemoteException;
}
