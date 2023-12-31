import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterDriverForm extends JDialog{
    private JPanel PnlRegisterDriver;
    private JTextField tfNom;
    private JButton BtnAddDriver;
    private JTextField tfPrenom;
    private JTextField tfMail;
    private JPasswordField pfMotdepasse;

    public Utilisateur livreur;

    public RegisterDriverForm(JFrame parent) {
        super(parent);
        setTitle("Créer un compte");
        setContentPane(PnlRegisterDriver);
        setMinimumSize(new Dimension(450,474));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        BtnAddDriver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Boolean isSaved = enregistrerLivreur();
                if (isSaved) {ListDrivers pnlDriver = new ListDrivers(null);}
            }
        });

        setVisible(true);
    }

    private Utilisateur ajouterUtilisateurDb(String nom, String prenom, String mail, String mdp){
        Utilisateur user = null;
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);


            String sql = "INSERT INTO utilisateur (nom, prenom, mail, mdp, roleU)" +
                    "VALUES (?, ?, ?, ?, 2)";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, nom);
            pStm.setString(2, prenom);
            pStm.setString(3, mail);
            pStm.setString(4, mdp);
            // Insert
            int addLine = pStm.executeUpdate();
            if(addLine > 0){
                user = new Utilisateur();
                user.nom = nom;
                user.prenom = prenom;
                user.email = mail;
                user.mdp = mdp;
                user.role = 2;
            }

            c.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    private Boolean enregistrerLivreur() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String mail = tfMail.getText();
        String mdp = String.valueOf(pfMotdepasse.getPassword());
        if(nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || mdp.isEmpty()){
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun champ ne peut être vide",
                    "Réessayez",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        livreur = ajouterUtilisateurDb(nom, prenom, mail, mdp);
        if(livreur != null){
            dispose();
            return true;
        }

        JOptionPane.showMessageDialog(this,
                "Impossible d'enregistrer le compte",
                "Réessayez",
                JOptionPane.ERROR_MESSAGE);

        return false;
    }

    public static void main(String[] args) {
        RegisterDriverForm registrationForm = new RegisterDriverForm( null);
        Utilisateur livreur = registrationForm.livreur;

        if(livreur != null){
            System.out.println("Livreur enregistré");
        }
        else{
            System.out.println("Enregistrement annulé");
        }
    }
}
