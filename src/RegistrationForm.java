import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog {

    private JTextField tfNom;
    private JTextField tfPrenom;
    private JPasswordField pfMotDePasse;
    private JButton btnConfirmer;
    private JButton btnAnnuller;
    private JPanel PnlEnregistrement;
    private JTextField tfEmail;
    private JTextField tfAdresse;

    public RegistrationForm(JFrame parent) {
         super(parent);
         setTitle("Créer un compte");
         setContentPane(PnlEnregistrement);
         setMinimumSize(new Dimension(450,474));
         setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
         setLocationRelativeTo(parent);
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnConfirmer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enregistrerClient();
                if(parent.getClass().getName() == "ListUsersInterface"){
                    dispose();
                    ListUsersInterface listUsers = new ListUsersInterface(null);
                }
                else{
                    dispose();
                    OrderForm pnlCommande = new OrderForm();
                }
            }
        });
        btnAnnuller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void enregistrerClient() {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String email = tfEmail.getText();
        String mdp = String.valueOf(pfMotDePasse.getPassword());
        if(nom.isEmpty() || prenom.isEmpty() || email.isEmpty() ||mdp.isEmpty()){
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun champ ne peut être vide",
                    "Réessayez",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        client = ajouterClientDb(nom, prenom, email, mdp);
        if(client != null){
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "Impossible d'enregistrer le compte",
                    "Réessayez",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    public Utilisateur client;
    private Utilisateur ajouterClientDb(String nom, String prenom, String email, String mdp) {
        Utilisateur client = null;
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            // Connexion établie
            System.out.println("Init connexion");
            Statement s = c.createStatement();
            String sql = "INSERT INTO utilisateur (nom, prenom, mail, mdp, roleU)" +
                    "VALUES (?, ?, ?, ?, 1)";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, nom);
            pStm.setString(2, prenom);
            pStm.setString(3, email);
            pStm.setString(4, mdp);
            // Insert
            int addLine = pStm.executeUpdate();
            if(addLine > 0){
                client = new Utilisateur();
                client.nom = nom;
                client.prenom = prenom;
                client.email = email;
                client.mdp = mdp;
                client.role = 1;
            }
            s.close();
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return client;
    }


    public static void main(String[] args) {
        RegistrationForm registrationForm = new RegistrationForm( null);
        Utilisateur client = registrationForm.client;
        if(client != null){
            System.out.println("Client enregistré");
        }
        else{
            System.out.println("Enregistrement annulé");
        }
    }
}
