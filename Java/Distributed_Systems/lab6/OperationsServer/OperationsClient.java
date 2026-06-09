package lab6.OperationsServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class OperationsClient {
    public static void main(String[] args) {
        try {
            String name = "//localhost/OperationsServer";
            Operations look_op = (Operations) Naming.lookup(name);
            look_op.setNum(15, 20);
            int result = look_op.sum();
            System.out.println(result);
        } catch (MalformedURLException | NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
