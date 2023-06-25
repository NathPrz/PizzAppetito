import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListOrders extends JDialog{
    private JPanel PnlOrders;
    private JTable DeliveringOrdersTable;
    private JTable DeliveredOrdersTable;

    public ListOrders(JFrame parent){
        super(parent);
        setTitle("Users list");
        setContentPane(PnlOrders);
        setMinimumSize(new Dimension(700,600));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        createTables();
        getOrdersList();
        setVisible(true);
    }


    private void createTables(){

        DeliveredOrdersTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Id", "Date Commande", "Pizza", "Taille", "Prix", "Client", "Livreur", "Temps de livraison"}
        ));

        DeliveringOrdersTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Id", "Date Commande", "Pizza", "Taille", "Prix", "Client", "Livreur", "Temps écoulé"}
        ));

    }

    private void getOrdersList(){
        DefaultTableModel enCoursDeLivraison = (DefaultTableModel) DeliveringOrdersTable.getModel();
        DefaultTableModel livrees = (DefaultTableModel) DeliveredOrdersTable.getModel();

        enCoursDeLivraison.setRowCount(0);
        livrees.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            Statement s = c.createStatement();

            // Commandes qui sont en cours de livraison
            String sql_livraison =
                    "SELECT " +
                        "cmd.idCommande, cmd.dateCommande, cmd.taille, cmd.prixFinal, CONCAT(u.nom, ' ' , u.prenom) AS 'client'," +
                        "CONCAT(livr.nom, ' ', livr.prenom) AS 'livreur', p.nom AS 'pizza', TIMEDIFF(cmd.dateLivraison, cmd.dateCommande) AS 'temps'  " +
                    "FROM commande AS cmd, utilisateur AS u, utilisateur AS livr, pizza AS p " +
                    "WHERE cmd.idUtilisateur = u.idUtilisateur " +
                    "AND cmd.idLivreur = livr.idUtilisateur " +
                    "AND cmd.idPizza = p.idPizza " +
                    "AND cmd.dateLivraison IS NULL";
            // Resultat
            ResultSet resultSet1 = s.executeQuery(sql_livraison);

            while (resultSet1.next()) {
                enCoursDeLivraison.addRow((new Object[]{
                        resultSet1.getInt("idCommande"),
                        resultSet1.getDate("dateCommande"),
                        resultSet1.getString("pizza"),
                        resultSet1.getInt("taille"),
                        resultSet1.getFloat("prixFinal"),
                        resultSet1.getString("client"),
                        resultSet1.getString("livreur"),
                        resultSet1.getString("temps"),
                }));
            }

            // Commandes qui sont en cours de livraison
            String sql_livrees =
                    "SELECT " +
                        "cmd.idCommande, cmd.dateCommande, cmd.taille, cmd.prixFinal, CONCAT(u.nom, ' ' , u.prenom) AS 'client'," +
                        "CONCAT(livr.nom, ' ', livr.prenom) AS 'livreur', p.nom AS 'pizza', TIMEDIFF(NOW(), cmd.dateCommande) AS 'temps' " +
                    "FROM commande AS cmd, utilisateur AS u, utilisateur AS livr, pizza AS p " +
                    "WHERE cmd.idUtilisateur = u.idUtilisateur " +
                    "AND cmd.idLivreur = livr.idUtilisateur " +
                    "AND cmd.idPizza = p.idPizza " +
                    "AND cmd.dateLivraison IS NOT NULL " +
                    "LIMIT 15";

            // Resultat
            ResultSet resultSet2 = s.executeQuery(sql_livrees);

            while (resultSet2.next()) {
                livrees.addRow((new Object[]{
                        resultSet2.getInt("idCommande"),
                        resultSet2.getDate("dateCommande"),
                        resultSet2.getString("pizza"),
                        resultSet2.getInt("taille"),
                        resultSet2.getFloat("prixFinal"),
                        resultSet2.getString("client"),
                        resultSet2.getString("livreur"),
                        resultSet2.getString("temps")
                }));
            }

            s.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    public static void main(String[] args) {
        ListOrders my_interface = new ListOrders(null);
    }
}
