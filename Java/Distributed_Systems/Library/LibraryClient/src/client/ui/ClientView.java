/**
 * Νικόλαος Κατσαρός
 * 3212023075
 **/
package client.ui;

public interface ClientView {

    // Προσθήκη κειμένου
    void appendText(String text);

    // Εμφάνιση error
    void showError(String message);
}