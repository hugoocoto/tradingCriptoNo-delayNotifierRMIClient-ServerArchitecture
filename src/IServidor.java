import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServidor extends Remote {
    /*
     * Add a buy alert. If the price of ACCION is lower than PRICE send a
     * notification to RMI ICliente at ADDRESS.
     */
    void addBuyAlert(String address, String accion, Float price)
            throws RemoteException;

    /*
     * Add a sell alert. If the price of ACCION is greater than PRICE send a
     * notification to RMI ICliente at ADDRESS.
     */
    void addSellAlert(String address, String accion, Float price)
            throws RemoteException;
}
