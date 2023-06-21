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
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        btnConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfLogin.getText();
                String mdp = String.valueOf(pfMdp.getPassword());
                /*if(email.isEmpty() ||mdp.isEmpty()){
                    JOptionPane.showMessageDialog(
                            this,
                            "Aucun champ ne peut être vide",
                            "Réessayez",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }*/
                client = getAuthentificationClient(email, mdp);
                if(client != null){
                    dispose();
                }
               /* else {
                    JOptionPane.showMessageDialog(this,
                            "Impossible d'enregistrer le compte",
                            "Réessayez",
                            JOptionPane.ERROR_MESSAGE);
                }*/
            }
        });
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
    }
}
