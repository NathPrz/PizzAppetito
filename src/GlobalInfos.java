import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


public class GlobalInfos extends JDialog {
    private JPanel PnlGlobalInfos;
    private JLabel textChiffreAffaires;
    private JTable pizzaMaxTable;
    private JTable IngredientsFavTable;
    private JLabel nbRetards;
    private JLabel nomLivreur;
    private JTable pizzaMinTable;
    private JLabel meilleurClient;
    private JLabel depenses;


    public GlobalInfos(JFrame parent){
        super(parent);
        setTitle("Users list");
        setContentPane(PnlGlobalInfos);
        setMinimumSize(new Dimension(700,600));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getChiffreAffaires();

        createStatsTable();

        getPizzasStats();
        getIngredientsFav();

        getMauvaisLivreur();
        getMeilleurClient();

        setVisible(true);

    }

    private void getChiffreAffaires(){
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            Statement s = c.createStatement();

            ResultSet resultset = s.executeQuery("SELECT SUM(prixFinal) AS 'total' FROM commande;");

            while (resultset.next()) {
                textChiffreAffaires.setText(resultset.getString("total") + " €");
            }

            s.close();
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    private void getMauvaisLivreur(){

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            Statement s = c.createStatement();

            String sql = "SELECT COUNT(cmd.idCommande) AS nbRetards, idLivreur, CONCAT(l.nom, ' ', l.prenom) AS 'livreur' " +
                    "FROM commande AS cmd, utilisateur AS l " +
                    "WHERE cmd.idLivreur = l.idUtilisateur " +
                    "AND TIMEDIFF(dateLivraison, dateCommande) > '00:05' " +
                    "GROUP by l.idUtilisateur " +
                    "ORDER BY nbRetards  " +
                    "DESC LIMIT 1";

            ResultSet resultset = s.executeQuery(sql);

            while (resultset.next()) {
                nbRetards.setText(resultset.getString("nbRetards"));
                nomLivreur.setText(resultset.getString("livreur"));
            }

            s.close();
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    private void createStatsTable(){

        pizzaMinTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Pizza la - commandée"}
        ));

        pizzaMaxTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Pizza la + commandée"}
        ));

        IngredientsFavTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Ingrédients favoris"}
        ));

    }

    private void getPizzasStats(){
        DefaultTableModel pizzasMin = (DefaultTableModel) pizzaMinTable.getModel();
        DefaultTableModel pizzasMax = (DefaultTableModel) pizzaMaxTable.getModel();

        pizzasMin.setRowCount(0);
        pizzasMax.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            Statement s = c.createStatement();

            String sqlPizzaMin = "SELECT p.nom FROM pizza AS p, commande AS cmd WHERE p.idPizza = cmd.idPizza GROUP BY p.idPizza HAVING COUNT(cmd.idCommande) =\n" +
                    "(SELECT MIN(COUNTS.c) FROM \n" +
                    "(SELECT COUNT(idCommande) AS c FROM commande AS cmd, pizza AS p WHERE cmd.idPizza = p.idPizza GROUP BY p.idPizza) AS COUNTS);";

            ResultSet resultset1 = s.executeQuery(sqlPizzaMin);

            while (resultset1.next()) {
                pizzasMin.addRow((new Object[]{
                        resultset1.getString("nom")
                }));
            }

            String sqlPizzaMax = "SELECT p.nom FROM pizza AS p, commande AS cmd WHERE p.idPizza = cmd.idPizza GROUP BY p.idPizza HAVING COUNT(cmd.idCommande) =\n" +
                    "(SELECT MAX(COUNTS.c) FROM \n" +
                    "(SELECT COUNT(idCommande) AS c FROM commande AS cmd, pizza AS p WHERE cmd.idPizza = p.idPizza GROUP BY p.idPizza) AS COUNTS);";

            ResultSet resultset2 = s.executeQuery(sqlPizzaMax);

            while (resultset2.next()) {
                pizzasMax.addRow((new Object[]{
                    resultset2.getString("nom")
                }));
            }

            s.close();
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return;



    }

    private void getIngredientsFav(){
        DefaultTableModel ingredientsFav = (DefaultTableModel) IngredientsFavTable.getModel();
        ingredientsFav.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            Statement s = c.createStatement();

            String sql = "SELECT i.nom FROM ingredient AS i, compose AS cmp, commande AS cmd WHERE i.idIngredient = cmp.idIngredient AND cmp.idPizza = cmd.idPizza\n" +
                "GROUP BY i.idIngredient HAVING COUNT(cmp.idIngredient) =\n" +
                "(SELECT MAX(COUNTS.c) FROM\n" +
                "(SELECT COUNT(*) AS c FROM ingredient AS i, compose AS cmp, commande AS cmd WHERE i.idIngredient = cmp.idIngredient AND cmp.idPizza = cmd.idPizza\n" +
                "GROUP BY i.idIngredient) AS COUNTS);\n";

            ResultSet resultset = s.executeQuery(sql);

            while (resultset.next()) {
                ingredientsFav.addRow((new Object[]{
                        resultset.getString("nom")
                }));
            }

            s.close();
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    private void getMeilleurClient(){
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            Statement s = c.createStatement();

            String sql = "SELECT CONCAT(u.nom, ' ', u.prenom) AS 'client', SUM(prixFinal) AS 'depenses' FROM utilisateur AS u, commande AS cmd " +
                    "WHERE cmd.idUtilisateur = u.idUtilisateur GROUP BY u.idUtilisateur HAVING depenses = \n" +
                    "(SELECT MAX(SUMS.s) FROM\n" +
                    "(SELECT SUM(prixFinal) AS s FROM commande GROUP BY idUtilisateur) AS SUMS);";

            ResultSet resultset = s.executeQuery(sql);

            while (resultset.next()) {
                meilleurClient.setText(resultset.getString("client"));
                depenses.setText(resultset.getString("depenses") + " €");
            }

            s.close();
            c.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return;
    }

    public static void main(String[] args) {
        GlobalInfos my_interface = new GlobalInfos(null);
    }

}
