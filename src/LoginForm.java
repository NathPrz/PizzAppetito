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
                    //Pour Lancement de fenetre commande
                    // OrderForm pnlCommande = new OrderForm(parent);
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

    public Client client;
    private Client getAuthentificationClient(String email, String mdp) {
        Client client = null;
        final String db_URL = "jdbc:mysql://localhost/pizzAppetito?serverTimezone=UTC";
        final String userName = "root";
        final String motDPasse = "root";
        try {
            Connection c = DriverManager.getConnection(db_URL, userName, motDPasse);
            // Connexion établie
            Statement s = c.createStatement();
            String sql = "SELECT * FROM client WHERE mail=? AND mdp=?";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, email);
            pStm.setString(2, mdp);

            // Resultat
            ResultSet resultSet = pStm.executeQuery();
            if(resultSet.next()){
                client = new Client();
                client.nom = resultSet.getString("nom");
                client.prenom = resultSet.getString("prenom");
                client.email = resultSet.getString("mail");
                client.mdp = resultSet.getString("mdp");
                client.solde = resultSet.getFloat("solde");
                client.nbPizzas = resultSet.getInt("nbPizzas");
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
        Client client = loginForm.client;
        if (client!= null){
            System.out.println("Auth valide");
            System.out.println("Client " + client.nom + client.prenom);
        }
        else{
            System.out.println("Erreur d'authentification");
        }

    }
}
