import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterVehicleForm extends JDialog {
    private JPanel PnlRegisterVehicle;
    private JTextField tfImmatriculation;
    private JRadioButton radioCar;
    private JRadioButton radioMoto;
    private JButton BtnAddVehicle;

    public Vehicle vehicle;

    public RegisterVehicleForm(JFrame parent) {
        super(parent);
        setTitle("Créer un compte");
        setContentPane(PnlRegisterVehicle);
        setMinimumSize(new Dimension(450,474));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        BtnAddVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Boolean isSaved = enregistrerVehicule();
                if (isSaved) {ListVehicles pnlVehicles = new ListVehicles(null);}
            }
        });

        setVisible(true);
    }

    private Vehicle ajouterVehicleDb(String immatriculation, Integer typeVehicle){
        Vehicle v = null;
        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            String sql = "INSERT INTO vehicule (nImmatriculation, type)" +
                    "VALUES (?, ?)";
            PreparedStatement pStm = c.prepareStatement(sql);
            pStm.setString(1, immatriculation);
            pStm.setInt(2, typeVehicle);
            // Insert
            int addLine = pStm.executeUpdate();
            if(addLine > 0){
                v = new Vehicle();
                v.immatriculation = immatriculation;
                v.typeVehicle = typeVehicle;
            }

            c.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    private Boolean enregistrerVehicule() {
        String immatriculation = tfImmatriculation.getText();
        Boolean isCar = radioCar.isSelected();

        Integer typeVehicule = isCar ? 1 : 2;

        if(immatriculation.isEmpty()){
            JOptionPane.showMessageDialog(
                    this,
                    "Aucun champ ne peut être vide",
                    "Réessayez",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(immatriculation.length() != 7){
            JOptionPane.showMessageDialog(
                    this,
                    "Le numéro d'immatriculation doit contenir 7 caractères",
                    "Réessayez",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        vehicle = ajouterVehicleDb(immatriculation, typeVehicule);
        if(vehicle != null){
            dispose();
            return true;
        }

        JOptionPane.showMessageDialog(this,
                "Impossible d'enregistrer le véhicule",
                "Réessayez",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public static void main(String[] args) {
        RegisterVehicleForm registrationForm = new RegisterVehicleForm( null);
        Vehicle vehicle = registrationForm.vehicle;

        if(vehicle != null){
            System.out.println("Véhicule enregistré");
        }
        else{
            System.out.println("Enregistrement annulé");
        }
    }
}
