
package p_Telephone;

/**
 *
 * @author ket752sbah
 */
public class Contact {
    private String nom;
    private String numero;
    
    public Contact(String nom, String numero) {
        this.nom = nom;
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public String getNumero() {
        return numero;
    }
}
