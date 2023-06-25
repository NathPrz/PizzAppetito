import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog{
    private JPanel PnlLogin;
    private JTextField tfLogin;
    private JPasswordField pfMdp;
    private JButton btnConnexion;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Connexion");
        setContentPane(PnlLogin);
        setMinimumSize(new Dimension(450,474));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfLogin.getText();
                String mdp = String.valueOf(pfMdp.getPassword());

                client = getAuthentificationClient(email, mdp);
                if(client != null){
                    dispose();
                    if(client.role == 1)
                    {
                        //Pour Lancement de fenetre commande
                        OrderForm pnlCommande = new OrderForm(parent);
                    } else if (client.role == 0) {
                        //Pour Lancement de fenetre admin
                        AdminInterface pnlAdmin = new AdminInterface();
                    }else{
                        DeliveryForm pnlLivreur = new DeliveryForm(parent);
                    }
                }
                else{
                    JOptionPane.showMessageDialog(
                            LoginForm.this,
                            "Email ou mot de passe incorrecte.",
                            "Réessayez",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        setVisible(true);
    }

    public static Utilisateur client;
    private Utilisateur getAuthentificationClient(String email, String mdp) {
        Utilisateur client = null;
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            // Connexion établie
            Statement s = c.createStatement();
            String sql = "SELECT * FROM utilisateur WHERE mail=? AND mdp=?";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, email);
            pStm.setString(2, mdp);

            // Resultat
            ResultSet resultSet = pStm.executeQuery();
            if(resultSet.next()){
                client = new Utilisateur();
                client.id = resultSet.getInt("idUtilisateur");
                client.nom = resultSet.getString("nom");
                client.prenom = resultSet.getString("prenom");
                client.email = resultSet.getString("mail");
                client.mdp = resultSet.getString("mdp");
                client.solde = resultSet.getFloat("solde");
                client.nbPizzas = resultSet.getInt("nbPizzas");
                client.role = resultSet.getInt("roleU");
            }
            s.close();
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return client;
    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        Utilisateur client = loginForm.client;
        if (client!= null){
            System.out.println("Auth valide");
            System.out.println("Client " + client.nom + client.prenom);
        }
        else{
            System.out.println("Erreur d'authentification");
        }

    }
}
