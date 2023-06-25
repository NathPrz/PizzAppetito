import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.*;
import java.sql.*;
import java.time.format.*;

public class DeliveryForm extends JDialog {
    private JButton buttonVoiture1;
    private JButton buttonVoiture2;
    private JButton buttonMoto;
    private JPanel deliveryPanel;

    private LocalDateTime deliveryDate;
    private int selectedVehicleId;

    public DeliveryForm(OrderForm parent) {
        super(parent);
        setTitle("Livraison");
        setContentPane(deliveryPanel);
        setMinimumSize(new Dimension(450, 474));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buttonVoiture1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedVehicleId = 1;
                showDeliveryDateDialog();
            }
        });

        buttonVoiture2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedVehicleId = 3;
                showDeliveryDateDialog();
            }
        });

        buttonMoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedVehicleId = 2;
                showDeliveryDateDialog();
            }
        });

        setVisible(true);
    }

    private void showDeliveryDateDialog() {
        String input = JOptionPane.showInputDialog(DeliveryForm.this, "Entrez la date de livraison (format : yyyy-mm-dd HH:mm:ss) :");
        if (input != null && !input.isEmpty()) {
            try {
                deliveryDate = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                updateConduitTable(); // Appeler la méthode pour mettre à jour la table "conduit"
                dispose();
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(DeliveryForm.this, "Format de date invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(DeliveryForm.this, "Veuillez entrer une date de livraison.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateConduitTable() {
        int idLivreur = OrderForm.idLivreur;

        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String query = "INSERT INTO conduit (idLivreur, idVehicule, dateOccupation, dateRendu) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idLivreur);
            statement.setInt(2, selectedVehicleId);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(4, Timestamp.valueOf(deliveryDate));
            statement.executeUpdate();

            statement.close();
            connection.close();

            System.exit(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
