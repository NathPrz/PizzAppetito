import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderForm extends JDialog {
    private JButton buttonMargarita;
    private JButton buttonVegetariana;
    private JButton buttonCalzone;
    private JButton buttonTartufo;
    private JButton buttonMediterranea;
    private JButton buttonFromaggi;
    private JPanel orderPanel;
    private JTextField tfSolde;
    private JTextField tfNbPizzas;

    public OrderForm(JFrame parent) {
        super(parent);
        setTitle("Commande");
        setContentPane(orderPanel);
        setMinimumSize(new Dimension(550,750));
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buttonMargarita.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder("Margarita");
            }
        });
        buttonVegetariana.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder("Vegetariana");

            }
        });
        buttonFromaggi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder("Quattro Fromaggi");
            }
        });
        buttonCalzone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder("Calzone");
            }
        });
        buttonTartufo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder("Tartufo");
            }
        });
        buttonMediterranea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder("Mediterranea");
            }
        });

        tfSolde.setText(String.valueOf(LoginForm.client.solde));
        tfNbPizzas.setText(String.valueOf(LoginForm.client.nbPizzas));

        setVisible(true);
    }

    private void placeOrder(String pizzaName) {
        Utilisateur client = LoginForm.client;

        float pizzaPrice = getPizzaPrice(pizzaName);
        float totalPrice;

        String pizzaSize = JOptionPane.showInputDialog(OrderForm.this,
                "Choisissez la taille de la pizza : \n " +
                        "Naine = N | Humaine = H  | Ogresse = O",
                "Choix de taille",
                JOptionPane.PLAIN_MESSAGE);

        if (pizzaSize == null || pizzaSize.isEmpty()) {
            return; // Sortir si la taille n'est pas sélectionnée
        }

        switch (pizzaSize.toUpperCase()) {
            case "N":
                totalPrice = pizzaPrice - (pizzaPrice/3);
                break;
            case "H":
                totalPrice = pizzaPrice;
                break;
            case "O":
                totalPrice = pizzaPrice + (pizzaPrice/3);
                break;
            default:
                JOptionPane.showMessageDialog(
                        OrderForm.this,
                        "Taille de pizza invalide.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
        }

        // Vérifier le solde du client
        if (client.solde < totalPrice) {
            float rechargeAmount = Float.parseFloat(JOptionPane.showInputDialog(OrderForm.this,
                    "Votre solde est insuffisant pour passer la commande. Veuillez le recharger :",
                    "Recharge de solde",
                    JOptionPane.PLAIN_MESSAGE));

            if (rechargeAmount <= 0) {
                JOptionPane.showMessageDialog(OrderForm.this,
                        "Montant de recharge invalide.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mise à jour du solde du client
            try {
                Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
                String updateQuery = "UPDATE utilisateur SET solde = solde + ? WHERE mail = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setFloat(1, rechargeAmount);
                updateStatement.setString(2, client.email);
                updateStatement.executeUpdate();

                // Mise à jour du solde local du client
                client.solde += rechargeAmount;

                JOptionPane.showMessageDialog(OrderForm.this,
                        "Super ! Vous pouvez maintenant passer votre commande !",
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);

                // Repasser la commande
                placeOrder(pizzaName);

                updateStatement.close();
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(OrderForm.this,
                        "Erreur lors de la recharge de solde.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }

            return;
        }

        // Incrémenter le nombre de pizzas du client
        client.nbPizzas++;

        // 10e commande = pizza gratuite
        if (client.nbPizzas == 10) {
            updateNbPizzasOnly(client);

            JOptionPane.showMessageDialog(
                    OrderForm.this,
                    "Félicitations! Votre pizza est gratuite!",
                    "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

            totalPrice = 0f;
            updateTableCommande(client, pizzaName, pizzaSize, totalPrice);

            return; //
        }

        // Mise à jour de la table Utilisateur
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String updateQuery = "UPDATE utilisateur SET nbPizzas = ?, solde = solde - ? WHERE mail = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, client.nbPizzas);
            preparedStatement.setFloat(2, totalPrice);
            preparedStatement.setString(3, client.email);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();

            updateTableCommande(client, pizzaName, pizzaSize, totalPrice);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    OrderForm.this,
                    "Erreur lors de la commande.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNbPizzasOnly(Utilisateur client) {
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String updateQuery = "UPDATE utilisateur SET nbPizzas = ? WHERE mail = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setInt(1, client.nbPizzas);
            preparedStatement.setString(2, client.email);
            preparedStatement.executeUpdate();

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    OrderForm.this,
                    "Erreur lors de la mise à jour du nombre de pizzas.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableCommande(Utilisateur client, String pizzaName, String pizzaSize, float totalPrice) {
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            int idLivreur = getRandomLivreurId();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateCommande = now.format(formatter);
            String dateLivraison = now.format(formatter);

            String insertQuery = "INSERT INTO Commande (dateCommande, dateLivraison, taille, prixFinal, idUtilisateur, idLivreur, idPizza) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, dateCommande);
            insertStatement.setString(2, dateLivraison);
            insertStatement.setInt(3, getPizzaSizeValue(pizzaSize));
            insertStatement.setFloat(4, totalPrice);
            insertStatement.setInt(5, getIdUtilisateur(client));
            insertStatement.setInt(6, idLivreur);
            insertStatement.setInt(7, getIdPizza(pizzaName));
            insertStatement.executeUpdate();

            JOptionPane.showMessageDialog(
                    OrderForm.this,
                    "Commande passée avec succès!",
                    "Confirmation",
                    JOptionPane.INFORMATION_MESSAGE);


            // Afficher le résumé de la commande
            String deliveryPersonName = getDeliveryPersonName(idLivreur);
            StringBuilder summaryBuilder = new StringBuilder();
            summaryBuilder.append("Résumé de votre commande \n");
            summaryBuilder.append("Pizza : ").append(pizzaName).append("\n");
            summaryBuilder.append("Prix total : ").append(totalPrice).append(" €").append("\n");
            summaryBuilder.append("Livreur en charge : ").append(deliveryPersonName).append("\n");

            JOptionPane.showMessageDialog(OrderForm.this,
                    summaryBuilder.toString(),
                    "Résumé de la commande",
                    JOptionPane.INFORMATION_MESSAGE);

            System.exit(0);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    OrderForm.this,
                    "Erreur lors de la mise à jour de la table Commande.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private float getPizzaPrice(String pizzaName) {
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String selectQuery = "SELECT prix FROM pizza WHERE nom = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, pizzaName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getFloat("prix");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private int getIdUtilisateur(Utilisateur client) {
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String selectQuery = "SELECT idUtilisateur FROM utilisateur WHERE mail = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, client.email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idUtilisateur");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    private int getIdPizza(String pizzaName) {
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String selectQuery = "SELECT idPizza FROM pizza WHERE nom = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, pizzaName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idPizza");
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    private int getRandomLivreurId() {
        Random random = new Random();
        int[] livreurId = getDriversIds();

        int randomIndex = random.nextInt(livreurId.length);
        return livreurId[randomIndex];
    }
    private int[] getDriversIds(){
        int[] driversIds = null;

        try {
            Connection c = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);

            // Connexion établie
            Statement s = c.createStatement();

            // Récupérer le nombre de conducteurs
            ResultSet countResultSet = s.executeQuery("SELECT COUNT(idUtilisateur) as count FROM utilisateur WHERE roleU = 2;");
            countResultSet.next();
            int count = countResultSet.getInt("count");

            // Initialiser le tableau avec la taille appropriée
            driversIds = new int[count];

            // Récupérer les idUtilisateur des conducteurs
            ResultSet resultSet = s.executeQuery("SELECT idUtilisateur FROM utilisateur WHERE roleU = 2;");
            int i = 0;
            while (resultSet.next()) {
                driversIds[i] =  resultSet.getInt("idUtilisateur");
                i++;
            }

            s.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return driversIds;
    }
    private int getPizzaSizeValue(String pizzaSize) {
        switch (pizzaSize.toUpperCase()) {
            case "N":
                return 1;
            case "H":
                return 2;
            case "O":
                return 3;
            default:
                return -1;
        }
    }

    private String getDeliveryPersonName(int idLivreur) {
        try {
            Connection connection = DriverManager.getConnection(DBCredentials.db_URL, DBCredentials.userName, DBCredentials.motDPasse);
            String selectQuery = "SELECT nom, prenom FROM Utilisateur WHERE idUtilisateur = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, idLivreur);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                return nom + " " + prenom;
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {

    }
}
