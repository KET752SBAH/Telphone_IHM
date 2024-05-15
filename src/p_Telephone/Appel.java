package p_Telephone;

import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author ket752sbah
 */
public class Appel {
    
    private String numeroCible;
    private String type;

    
    private int duree;
    private int cout;
    private LocalDateTime date;
    
    public Appel(String numeroCible, String type, int duree, int cout, LocalDateTime date) {
        this.numeroCible = numeroCible;
        this.type = type;
        this.duree = duree;
        this.cout = cout;
        this.date = date;
    }

    

    public void setNumeroCible(String numeroCible) {
        this.numeroCible = numeroCible;
    }

    

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
            return type;
        }
    public int getDuree() {
        return duree;
    }
    public String getNumeroCible() {
        return numeroCible;
    }
    public int getCout() {
            return cout;
        }
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public void setDuree(int duree) {
        this.duree = duree;
    }

    

    public void setCout(int cout) {
        this.cout = cout;
    }
    
}
