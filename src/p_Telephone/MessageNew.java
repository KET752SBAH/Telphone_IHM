/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package p_Telephone;

import java.time.LocalDateTime;

/**
 *
 * @author ket752sbah
 */
public class MessageNew {
    private String numeroD;
    private String messageD;
    
    public MessageNew(String numeroD, String messageD, LocalDateTime date){
        this.messageD = messageD;
        this.numeroD = numeroD;
    }
    
    public String getNumeroD(){
        return numeroD;
    }
    
    public void setNumeroD(String numberD){
        this.numeroD = numberD;
    }
   
    public String getMessage(){
        return messageD;
    }
}
