import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ListDrivers extends JFrame {
    private JPanel PnlDrivers;
    private JTable driversTable;
    private JButton BtnAddDriver;

    public ListDrivers(JFrame Parent){
        setTitle("Users list");
        setContentPane(PnlDrivers);
        setMinimumSize(new Dimension(700,600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        createTable();
        getDriversList();

        addDeleteBtn();

        BtnAddDriver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterDriverForm pnlEnregistrement = new RegisterDriverForm(ListDrivers.this);
                dispose();
            }
        });
        setVisible(true);
    }

    private void createTable(){

        driversTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Id", "Nom", "Prénom", "Mail", ""}
        ));

    }


    private void getDriversList(){
        DefaultTableModel model = (DefaultTableModel) driversTable.getModel();
        model.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            Statement s = c.createStatement();

            // Resultat
            ResultSet resultSet = s.executeQuery("SELECT idUtilisateur, nom, prenom, mail FROM utilisateur WHERE roleU = 2;");

            while (resultSet.next()) {
                model.addRow((new Object[]{
                        resultSet.getInt("idUtilisateur"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("mail"),
                        "Delete"
                }));
            }

            s.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    private void deleteDriver(int id){
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            String sql = "DELETE FROM utilisateur WHERE idUtilisateur = ?;";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, String.valueOf(id));


            int i = pStm.executeUpdate();

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return;
    }

    private void addDeleteBtn(){

        // Code récupéré sur le site : https://tips4java.wordpress.com/2009/07/12/table-button-column/
        Action delete = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {

                JTable table = (JTable)e.getSource();

                int modelRow = Integer.valueOf( e.getActionCommand() );

                // Delete user in database
                deleteDriver((int) table.getModel().getValueAt(modelRow, 0));

                // Delete row
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);

            }
        };
        ButtonColumn buttonColumn = new ButtonColumn(driversTable, delete, 4);
    }

    public static void main(String[] args) {
        ListDrivers my_interface = new ListDrivers(null);
    }
}
