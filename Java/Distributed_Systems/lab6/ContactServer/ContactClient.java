package lab6.ContactServer;

import java.rmi.Naming;

public class ContactClient {
    public static void main(String[] args) {
        try {
            String name = "//localhost/ContactServer";
            DirectoryOperations look_op = (DirectoryOperations) Naming.lookup(name);
            System.out.println(look_op.searchNumber("ΓιάννηςΠαπαδόπουλος").getNumber());
            System.out.println(look_op.insertContact("Kalisj", "aofgnoe;", "8638495019"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
