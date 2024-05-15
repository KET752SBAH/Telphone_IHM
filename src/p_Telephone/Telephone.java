/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package p_Telephone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author ket752sbah
 */
public class Telephone extends javax.swing.JFrame {

    private String nom ;
    private String numero;
    
    
    private String heureH;
    private String dateH;
    
    private boolean enAppel;
    private boolean initiateur;
    public boolean enregis;
    
    private Telephone telAppel;
    private Timer timer;
    private int seconds;
    private int minutes;
    
    private static Clip clip;
    
    //délcaration (initialisation) d'une liste de contact and Appel 
    private final ArrayList<Contact> listeContacts = new ArrayList<>();
    public final ArrayList<Appel> listeAppels = new ArrayList<>();
       
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return this.getTitle();
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    private String numeroCible;

//************** Gestion du temps ************
    private int duree;
    private int cout;
    private Date date;
    String type ="";
    
    public int getDuree() {
        return duree;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNumeroCible() {
        return numeroCible;
    }
    public int getCout() {
            return cout;
        }
    
    String resultatFinal = "";
    
    public Telephone() {
        initComponents();
        panelAppel.setVisible(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        //Taille auto
        this.pack();
        
        ((javax.swing.text.AbstractDocument) resultat.getDocument()).setDocumentFilter(new ChiffresSeulementFilter());
    

        // Variables pour stocker la date et l'heure
        //String heure = "";
        //String date = "";

        JFrame frame = new JFrame("Date et Heure");
        JLabel label = new JLabel();

        // Créer un Timer qui se déclenche toutes les secondes (1000 millisecondes)
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Obtenir la date actuelle
                Date now = new Date();
                
                // Formater la date en format souhaité (ici, HH:mm:ss dd-MM-yyyy)
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                v_heure.setText(dateFormat.format(now)); // Stocker l'heure dans la variable heure

                SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
                v_date.setText(dateFormat2.format(now)); // Stocker la date dans la variable date
                
                // Mettre à jour le texte de l'étiquette avec la date et l'heure actuelles
                //label.setText("Heure: " + heure + " | Date: " + date);
            }
        });

        // Démarrer le Timer
        timer.start();

    }
    
    
    
    //declaration de la methode getChiffre()
    private void getChiffre(int nb){
     resultatFinal = resultat.getText();
    resultatFinal += nb;
    resultat.setText(resultatFinal);
    }
    
    
    private class ChiffresSeulementFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String sb = "";
            for (int i = 0; i < string.length(); i++) {
                if (Character.isDigit(string.charAt(i))) {
                    sb += (string.charAt(i));
                    resultatFinal += sb;
                }
            }
            super.insertString(fb, offset, resultatFinal, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if (Character.isDigit(text.charAt(i))) {
                    sb.append(text.charAt(i));
                }
            }
            super.replace(fb, offset, length, sb.toString(), attrs);
        }
    }
    
    public void appeler(String numeroCible, Telephone telephoneCible) {
        if(numeroCible.equals(this.getNumero())){
            JOptionPane.showMessageDialog(rootPane, "Impossible de s'auto appeler", "Erreur", JOptionPane.WARNING_MESSAGE);
        }else{
            panelTel.setVisible(false);
            panelAppel.setVisible(true);
            enAppel = true;
            initiateur = true;

            labelEncours.setText("Appel sortant");
            labelTimer.setText("En attente...");
            this.playSound("sone.wav");
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    seconds++;
                    if (seconds == 60) {
                        seconds = 0;
                        minutes++;
                    }

                    DecimalFormat df = new DecimalFormat("00");
                    labelTimer.setText(df.format(minutes) + ":" + df.format(seconds));
                }
            });
            for(Telephone e: CreateTelephone.listeTel){
                if(e.getNumero().equals(numeroCible)){
                    int choix = JOptionPane.showConfirmDialog(e, "Décrocher l'appel ?", "Appel entrant de "+this.getNumero(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    
                    if(choix == JOptionPane.YES_OPTION){
                        e.recevoirAppel(this.getNumero());
                        this.telAppel = e;
                        e.telAppel = this;
                    }
                    timer.start();
                    clip.stop();
                    playSound("appal.wav");
                }
            }
            for(Contact e: listeContacts){
                if(e.getNumero().equals(numeroCible))
                    labelNom.setText(e.getNom());   
            }
            labelNumero.setText(numeroCible);
        }
        
    }
    
    public void recevoirAppel(String numeroAppelant) {
        enAppel = true;
        panelTel.setVisible(false);
        panelAppel.setVisible(true);
        //this.playSound("sone.wav");
        this.initiateur = false;
        
        labelEncours.setText("Appel entrant");
        for(Contact e: listeContacts){
            if(e.getNumero().equals(numeroAppelant))
                labelNom.setText(e.getNom());
        }
        labelNumero.setText(numeroAppelant);
        
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                if (seconds == 60) {
                    seconds = 0;
                    minutes++;
                }

                DecimalFormat df = new DecimalFormat("00");
                labelTimer.setText(df.format(minutes) + ":" + df.format(seconds));
            }
        });
        timer.start();
    }

    public void raccrocher(){
        clip.close();
        int duree = (minutes*60) + seconds;
        int couts = duree*2;
        LocalDateTime dateTime =  LocalDateTime.now();
        
        if(this.initiateur == true){
            Appel ini = new Appel(this.telAppel.getNumero(),"Sortant",duree,couts, dateTime);
            this.listeAppels.add(ini);
            Appel recep = new Appel(this.getNumero(),"Entrant",duree,0, dateTime);
            telAppel.listeAppels.add(recep);
        }else if(this.initiateur == false){
                Appel ini = new Appel(this.telAppel.getNumero(),"Entrant",duree,0, dateTime);
                this.listeAppels.add(ini);
                Appel recep = new Appel(this.getNumero(),"Sortant",duree,couts, dateTime);
                telAppel.listeAppels.add(recep);
            }
        
        this.timer.stop();
        this.minutes = 0;
        this.seconds = 0;
        this.labelTimer.setText("00:0");
        this.enAppel = false;
        this.panelAppel.setVisible(false);
        this.panelTel.setVisible(true);
        this.enregListe();
        telAppel.timer.stop();
        telAppel.minutes = 0;
        telAppel.seconds = 0;
        telAppel.labelTimer.setText("00:0");
        telAppel.enAppel = false;
        telAppel.panelAppel.setVisible(false);
        telAppel.panelTel.setVisible(true);
        telAppel.enregListe();
    }
    
    public void sargerAppels(String semin){
        if(enregis==true){
            try {
        FileReader fichier = new FileReader(semin);
        BufferedReader reader = new BufferedReader(fichier);

        String numeroCible;
        String type;
        int duree;
        int cout;
        LocalDateTime date;

        String line;
        while ((line = reader.readLine()) != null) {
            numeroCible = line;
            type = reader.readLine();
            duree = Integer.parseInt(reader.readLine());
            cout = Integer.parseInt(reader.readLine());
            date = LocalDateTime.parse(reader.readLine());

            Appel appel = new Appel(numeroCible, type, duree, cout, date);
            listeAppels.add(appel);
        }
            reader.close();
            System.out.println("Contenu du fichier lu avec succès.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void viderFichier(){
     //String cheminFichier = "chemin/vers/le/fichier.txt";
     String cheminFichier = "db_"+this.getTitle()+".txt";
        try {
            // Ouvrir le fichier en mode écriture
            FileWriter fileWriter = new FileWriter(cheminFichier);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Réduire la taille du fichier à 0
            File fichier = new File(cheminFichier);
            RandomAccessFile randomAccessFile = new RandomAccessFile(fichier, "rw");
            randomAccessFile.setLength(0);
            randomAccessFile.close();

            // Fermer le fichier
            bufferedWriter.close();

            System.out.println("Contenu du fichier vidé avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void stopSound(){
        if(clip != null && clip.isRunning()){
            clip.stop();
        }
    }
    
    public void enregListe(){
        String cheminFichier = "db_"+this.getNumero()+".txt";
        try {
            FileWriter fichier = new FileWriter(cheminFichier);
            BufferedWriter writer = new BufferedWriter(fichier);

            for (Appel appel : this.listeAppels) {
                writer.write(appel.getNumeroCible());
                writer.newLine();
                writer.write(appel.getType());
                writer.newLine();
                writer.write(Integer.toString(appel.getDuree()));
                writer.newLine();
                writer.write(Integer.toString(appel.getCout()));
                writer.newLine();
                writer.write(appel.getDate().toString());
                writer.newLine();
            }

            writer.close();
            System.out.println("Contenu de la liste écrit dans le fichier.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelAppel = new javax.swing.JPanel();
        labelTimer = new javax.swing.JLabel();
        labelEncours = new javax.swing.JLabel();
        labelNom = new javax.swing.JLabel();
        labelNumero = new javax.swing.JLabel();
        btnRaccrocher = new javax.swing.JButton();
        panelTel = new javax.swing.JPanel();
        resultat = new javax.swing.JTextField();
        n1 = new javax.swing.JButton();
        n2 = new javax.swing.JButton();
        n3 = new javax.swing.JButton();
        n4 = new javax.swing.JButton();
        n5 = new javax.swing.JButton();
        n6 = new javax.swing.JButton();
        n7 = new javax.swing.JButton();
        n8 = new javax.swing.JButton();
        n9 = new javax.swing.JButton();
        n0 = new javax.swing.JButton();
        n10 = new javax.swing.JButton();
        btnAppeler = new javax.swing.JButton();
        btnSupprimer = new javax.swing.JButton();
        btnEnregistrer = new javax.swing.JButton();
        btnContacts = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnMessage = new javax.swing.JButton();
        v_heure = new javax.swing.JLabel();
        v_date = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 143));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelAppel.setBackground(new java.awt.Color(255, 255, 255));

        labelTimer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        labelEncours.setText("  ");

        labelNom.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelNom.setText("Nom Inconnu");

        labelNumero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelNumero.setText("Numero");

        btnRaccrocher.setBackground(new java.awt.Color(102, 153, 255));
        btnRaccrocher.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-telephone-26_red.png"))); // NOI18N
        btnRaccrocher.setBorderPainted(false);
        btnRaccrocher.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRaccrocher.setOpaque(false);
        btnRaccrocher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRaccrocherMouseClicked(evt);
            }
        });
        btnRaccrocher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRaccrocherActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAppelLayout = new javax.swing.GroupLayout(panelAppel);
        panelAppel.setLayout(panelAppelLayout);
        panelAppelLayout.setHorizontalGroup(
            panelAppelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAppelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(panelAppelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAppelLayout.createSequentialGroup()
                        .addComponent(labelNom, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAppelLayout.createSequentialGroup()
                        .addComponent(btnRaccrocher, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))))
            .addGroup(panelAppelLayout.createSequentialGroup()
                .addGroup(panelAppelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAppelLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(labelNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelAppelLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(panelAppelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelEncours, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelAppelLayout.setVerticalGroup(
            panelAppelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAppelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(labelEncours, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelNom, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(labelNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRaccrocher, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelTel.setBackground(new java.awt.Color(102, 0, 255));

        resultat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        resultat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resultatActionPerformed(evt);
            }
        });

        n1.setText("1");
        n1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n1ActionPerformed(evt);
            }
        });

        n2.setText("2");
        n2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n2ActionPerformed(evt);
            }
        });

        n3.setText("3");
        n3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n3ActionPerformed(evt);
            }
        });

        n4.setText("4");
        n4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n4ActionPerformed(evt);
            }
        });

        n5.setText("5");
        n5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n5ActionPerformed(evt);
            }
        });

        n6.setText("6");
        n6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n6ActionPerformed(evt);
            }
        });

        n7.setText("7");
        n7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n7ActionPerformed(evt);
            }
        });

        n8.setText("8");
        n8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n8ActionPerformed(evt);
            }
        });

        n9.setText("9");
        n9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n9ActionPerformed(evt);
            }
        });

        n0.setText("*");
        n0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n0ActionPerformed(evt);
            }
        });

        n10.setText("0");
        n10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n10ActionPerformed(evt);
            }
        });

        btnAppeler.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-phone-50.png"))); // NOI18N
        btnAppeler.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnAppeler.setBorderPainted(false);
        btnAppeler.setOpaque(false);
        btnAppeler.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAppelerMouseClicked(evt);
            }
        });
        btnAppeler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAppelerActionPerformed(evt);
            }
        });

        btnSupprimer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-cl-button-48.png"))); // NOI18N
        btnSupprimer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSupprimerMouseClicked(evt);
            }
        });

        btnEnregistrer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-new-contact-48.png"))); // NOI18N
        btnEnregistrer.setBorderPainted(false);
        btnEnregistrer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEnregistrerMouseClicked(evt);
            }
        });

        btnContacts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-call-list-50.png"))); // NOI18N
        btnContacts.setBorderPainted(false);
        btnContacts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnContactsMouseClicked(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-callback-50.png"))); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        jButton2.setText("#");

        btnMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icons8-message-24.png"))); // NOI18N
        btnMessage.setBorderPainted(false);
        btnMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMessageActionPerformed(evt);
            }
        });

        v_heure.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        v_heure.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        v_heure.setText("12H:33mn");

        v_date.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        v_date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        v_date.setText("13-05-2003");

        javax.swing.GroupLayout panelTelLayout = new javax.swing.GroupLayout(panelTel);
        panelTel.setLayout(panelTelLayout);
        panelTelLayout.setHorizontalGroup(
            panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTelLayout.createSequentialGroup()
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(n0, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n7, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMessage)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTelLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(n8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(n10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnContacts, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnAppeler, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)))
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(n9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnregistrer, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(panelTelLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(v_heure, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(resultat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelTelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTelLayout.createSequentialGroup()
                                .addComponent(n1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(n2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelTelLayout.createSequentialGroup()
                                .addComponent(n4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(n5, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(n6, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(n3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelTelLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(v_date, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelTelLayout.setVerticalGroup(
            panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(v_heure)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_date)
                .addGap(20, 20, 20)
                .addComponent(resultat, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(n2)
                    .addComponent(n3)
                    .addComponent(n1))
                .addGap(18, 18, 18)
                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(n6)
                    .addComponent(n5)
                    .addComponent(n4))
                .addGap(18, 18, 18)
                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelTelLayout.createSequentialGroup()
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(n9)
                            .addComponent(n8)
                            .addComponent(n7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(n10)
                            .addComponent(n0)
                            .addComponent(jButton2))
                        .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTelLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(btnMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAppeler, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(btnSupprimer, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(panelTelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnContacts, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnregistrer, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(panelAppel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelAppel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRaccrocherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRaccrocherMouseClicked
            raccrocher();
    }//GEN-LAST:event_btnRaccrocherMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        CreateTelephone.listeTel.remove(this);
    }//GEN-LAST:event_formWindowClosing

    private void btnMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMessageActionPerformed
        // TODO add your handling code here:
        Messages msg = new Messages();
        msg.setVisible(true);
        
        msg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_btnMessageActionPerformed

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        if(listeAppels.isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Aucun Appel !");
        }else{
            afficheAppel afc = new afficheAppel(getNumeroCible(), type, getDuree(), getCout(), date);
            afc.setVisible(true);
            afc.setTitle(this.getNumero());
            JTextArea textAreaAppel = afc.getTextAreaAppel();

            StringBuilder textAffichage = new StringBuilder();
            for(Appel app : listeAppels){
                String type = app.getType();
                int duree = app.getDuree();
                int cout = app.getCout();
                LocalDateTime date = app.getDate();

                textAffichage.append("Numero : ").append(app.getNumeroCible())
                .append(", Type : ").append(type)
                .append(", Duree : ").append(duree)
                .append(", Cout : ").append(cout)
                .append(", Date : ").append(date)
                .append("\n\n");
            }
            textAreaAppel.setText(textAffichage.toString());
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void btnContactsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnContactsMouseClicked
        if(listeContacts.isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Aucun Contact !");
        }else{
            AfficheContact afc = new AfficheContact(getNom(),getNumero());
            afc.setVisible(true);
            afc.setTitle(this.getNumero());
            JTextArea textAreaContact = afc.getTextAreaContact();

            StringBuilder textAffichage = new StringBuilder();
            for(Contact c : listeContacts){
                textAffichage.append("Nom : ").append(c.getNom()).append("\nNumero : ").append(c.getNumero()).append("\n\n");

            }
            textAreaContact.setText(textAffichage.toString());
        }
    }//GEN-LAST:event_btnContactsMouseClicked

    private void btnEnregistrerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEnregistrerMouseClicked
        String nomContact = JOptionPane.showInputDialog(rootPane, "Nom Contact", "Création d'un contact", JOptionPane.QUESTION_MESSAGE);
        Contact c =new Contact(nomContact, resultat.getText());
        listeContacts.add(c);
    }//GEN-LAST:event_btnEnregistrerMouseClicked

    private void btnSupprimerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSupprimerMouseClicked
        resultatFinal = resultat.getText();
        if (!resultat.getText().isEmpty()) {
            resultatFinal = resultat.getText().substring(0, resultatFinal.length() - 1);
        }
        resultat.setText(resultatFinal);
    }//GEN-LAST:event_btnSupprimerMouseClicked

    private void btnAppelerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAppelerMouseClicked
        appeler(resultat.getText(), this);
    }//GEN-LAST:event_btnAppelerMouseClicked

    private void n10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_n10ActionPerformed

    private void n0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n0ActionPerformed
        getChiffre(0);
    }//GEN-LAST:event_n0ActionPerformed

    private void n9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n9ActionPerformed
        getChiffre(9);
    }//GEN-LAST:event_n9ActionPerformed

    private void n8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n8ActionPerformed
        getChiffre(8);
    }//GEN-LAST:event_n8ActionPerformed

    private void n7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n7ActionPerformed
        getChiffre(7);
    }//GEN-LAST:event_n7ActionPerformed

    private void n6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n6ActionPerformed
        getChiffre(6);
    }//GEN-LAST:event_n6ActionPerformed

    private void n5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n5ActionPerformed
        getChiffre(5);
    }//GEN-LAST:event_n5ActionPerformed

    private void n4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n4ActionPerformed
        getChiffre(4);
    }//GEN-LAST:event_n4ActionPerformed

    private void n3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n3ActionPerformed
        getChiffre(3);
    }//GEN-LAST:event_n3ActionPerformed

    private void n2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n2ActionPerformed
        getChiffre(2);
    }//GEN-LAST:event_n2ActionPerformed

    private void n1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n1ActionPerformed
        //appel de la methode getChiffre()
        getChiffre(1);
    }//GEN-LAST:event_n1ActionPerformed

    private void resultatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resultatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_resultatActionPerformed

    private void btnAppelerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppelerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAppelerActionPerformed

    private void btnRaccrocherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRaccrocherActionPerformed
        // TODO add your handling code here:
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }//GEN-LAST:event_btnRaccrocherActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Telephone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Telephone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Telephone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Telephone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Telephone().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAppeler;
    private javax.swing.JButton btnContacts;
    private javax.swing.JButton btnEnregistrer;
    private javax.swing.JButton btnMessage;
    private javax.swing.JButton btnRaccrocher;
    private javax.swing.JButton btnSupprimer;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel labelEncours;
    private javax.swing.JLabel labelNom;
    private javax.swing.JLabel labelNumero;
    private javax.swing.JLabel labelTimer;
    private javax.swing.JButton n0;
    private javax.swing.JButton n1;
    private javax.swing.JButton n10;
    private javax.swing.JButton n2;
    private javax.swing.JButton n3;
    private javax.swing.JButton n4;
    private javax.swing.JButton n5;
    private javax.swing.JButton n6;
    private javax.swing.JButton n7;
    private javax.swing.JButton n8;
    private javax.swing.JButton n9;
    private javax.swing.JPanel panelAppel;
    private javax.swing.JPanel panelTel;
    private javax.swing.JTextField resultat;
    private javax.swing.JLabel v_date;
    private javax.swing.JLabel v_heure;
    // End of variables declaration//GEN-END:variables
}
