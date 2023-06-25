import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DeliveryForm extends JDialog{
    private JPanel pnlLivraison;
    private JTable tCommandes;

    public DeliveryForm(JFrame parent) {
        super(parent);
        setTitle("Livreur");
        setContentPane(pnlLivraison);
        setMinimumSize(new Dimension(450,474));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Utilisateur livreur = LoginForm.client;
        createTables();
        getOrdersList();
        addLivréeBtn();
        setVisible(true);
    }
    private void createTables() {

        tCommandes.setModel(new DefaultTableModel(
                null,
                new String[]{"Id Commande", "Date Commande", "Pizza", "Client", "Adresse", ""}
        ));

    }

    private void getOrdersList(){
        Utilisateur livreur = LoginForm.client;

        DefaultTableModel commandes = (DefaultTableModel) tCommandes.getModel();

        commandes.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            Statement s = c.createStatement();

            // Commandes qui sont en cours de livraison
            String sql_livraison =
                    "SELECT " +
                            "cmd.idCommande, cmd.dateCommande, CONCAT(u.nom, ' ' , u.prenom) AS 'client'," +
                            "u.adresse As 'adresse'," +
                            "p.nom AS 'pizza' " +
                            "FROM commande AS cmd, utilisateur AS u, pizza AS p " +
                            "WHERE cmd.idUtilisateur = u.idUtilisateur " +
                            "AND cmd.idPizza = p.idPizza " +
                            "AND cmd.idLivreur = ? " ;

            PreparedStatement pStm = c.prepareStatement(sql_livraison);
            pStm.setString(1, String.valueOf(livreur.id));
            // Resultat
            ResultSet resultSet1 = pStm.executeQuery();

            while (resultSet1.next()) {
                commandes.addRow((new Object[]{
                        resultSet1.getInt("idCommande"),
                        resultSet1.getDate("dateCommande"),
                        resultSet1.getString("pizza"),
                        resultSet1.getString("client"),
                        resultSet1.getString("adresse"),
                        "Livréé"
                }));
            }

            s.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLivréeBtn(){

        // Code récupéré sur le site : https://tips4java.wordpress.com/2009/07/12/table-button-column/
        Action livrer = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {

                JTable table = (JTable)e.getSource();

                int modelRow = Integer.valueOf( e.getActionCommand() );

                // Delete user in database
                livrerCommande((int) table.getModel().getValueAt(modelRow, 0));

                // Delete row
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);

            }
        };
        ButtonColumn buttonColumn = new ButtonColumn(tCommandes, livrer, 5);
    }

    private void livrerCommande(int id){
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateLiv= now.format(formatter);
            // Connexion établie
            String sql = "UPDATE commande SET dateLivraison = ? WHERE idCommande = ?;";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, dateLiv);
            pStm.setString(2, String.valueOf(id));


            int i = pStm.executeUpdate();

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
