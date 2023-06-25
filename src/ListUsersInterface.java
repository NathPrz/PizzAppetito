import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.AbstractAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ListUsersInterface extends JDialog {
    private JPanel PnlUsers;
    private JLabel Title;
    private JTable usersTable;
    private JButton BtnAddUser;

    public ListUsersInterface(JFrame parent){
        super(parent);
        setTitle("Users list");
        setContentPane(PnlUsers);
        setMinimumSize(new Dimension(700,600));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        createTable();
        getUsersList();

        addDeleteBtn();
        BtnAddUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationForm pnlEnregistrement = new RegistrationForm(parent);
                dispose();
            }
        });
        setVisible(true);
    }

    private void createTable(){

        usersTable.setModel(new DefaultTableModel(
                null,
                new String[]{"Id", "Nom", "Prénom", "Mail", "Adresse", "Solde", "Nombre de pizzas commandées", ""}
        ));

    }

    private void getUsersList() {

        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setRowCount(0);

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            Statement s = c.createStatement();

            // Resultat
            ResultSet resultSet = s.executeQuery("SELECT idUtilisateur, nom, prenom, mail, adresse, solde, nbPizzas FROM utilisateur WHERE roleU = 1;");

            while (resultSet.next()) {
                model.addRow((new Object[]{
                        resultSet.getInt("idUtilisateur"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("mail"),
                        resultSet.getString("adresse"),
                        resultSet.getFloat("solde"),
                        resultSet.getInt("nbPizzas"),
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

    private void deleteUser(int id){
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
                deleteUser((int) table.getModel().getValueAt(modelRow, 0));

                // Delete row
                ((DefaultTableModel)table.getModel()).removeRow(modelRow);



            }
        };
        ButtonColumn buttonColumn = new ButtonColumn(usersTable, delete, 7);
    }

    public void addUser(Utilisateur client){
        System.out.println("add user");
        System.out.println(client.nom);

        DefaultTableModel model =  (DefaultTableModel) usersTable.getModel();

        model.addRow((new Object[]{
                client.nom,
                client.prenom,
                client.email,
                client.adresse,
                client.solde,
                client.nbPizzas,
                "Delete"
        }));

        return;
    }

    public static void main(String[] args) {
        ListUsersInterface my_interface = new ListUsersInterface(null);
    }
}
