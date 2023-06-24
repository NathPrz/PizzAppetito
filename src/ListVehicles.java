import com.mysql.cj.protocol.Resultset;

import javax.management.relation.RelationSupport;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ListVehicles extends JFrame {
    private JPanel PnlVehicles;
    private JTable carTable;
    private JButton BtnAddVehicle;
    private JTable motoTable;

    public ListVehicles(JFrame parent){
        setTitle("Users list");
        setContentPane(PnlVehicles);
        setMinimumSize(new Dimension(700,600));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setVisible(true);

        createTables();
        getCarsList();

        addDeleteBtn();

        BtnAddVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterVehicleForm pnlEnregistrement = new RegisterVehicleForm(ListVehicles.this);
                dispose();
            }
        });
    }


    private void createTables(){
        carTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Id", "Immatriculation", ""}
        ));

        motoTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Id", "Immatriculation", ""}
        ));
    }

    private void getCarsList(){
        DefaultTableModel cars = (DefaultTableModel) carTable.getModel();
        cars.setRowCount(0);

        DefaultTableModel motos = (DefaultTableModel) motoTable.getModel();
        motos.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            Statement s = c.createStatement();


            ResultSet resultSet1 = s.executeQuery("SELECT idVehicule, nImmatriculation FROM vehicule WHERE type = 1;");


            // Resultat
            while (resultSet1.next()) {
                cars.addRow((new Object[]{
                        resultSet1.getInt("idVehicule"),
                        resultSet1.getString("nImmatriculation"),
                        "Delete"
                }));
            }

            //pStm.setInt(1, 2);
            ResultSet resultSet2 = s.executeQuery("SELECT idVehicule, nImmatriculation FROM vehicule WHERE type = 2;");

            // Resultat
            while (resultSet2.next()) {
                motos.addRow((new Object[]{
                        resultSet2.getInt("idVehicule"),
                        resultSet2.getString("nImmatriculation"),
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

    private void deleteVehicle(int id){
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            String sql = "DELETE FROM vehicule WHERE idVehicule = ?;";
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

                System.out.println(table);

                // Delete user in database
                deleteVehicle((int) table.getModel().getValueAt(modelRow, 0));

                // Delete row
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);

            }
        };
        ButtonColumn carButtonColumn = new ButtonColumn(carTable, delete, 2);
        ButtonColumn motoButtonColumn = new ButtonColumn(motoTable, delete, 2);
    }

    public static void main(String[] args) {
        ListVehicles my_interface = new ListVehicles(null);
    }

}
