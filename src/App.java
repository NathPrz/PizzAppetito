import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {
    private JPanel MainPanel;
    private JButton btnEnregistrement;
    private JButton btnLogin;

    public App() {
        setTitle("PizzAppetito");
        setContentPane(MainPanel);
        setMinimumSize(new Dimension(450,574));
        ImageIcon icon = new ImageIcon("pizza-100.png");
        setIconImage(icon.getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnEnregistrement.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationForm pnlEnregistrement = new RegistrationForm(App.this);
            }
        });
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginForm pnlConnexion = new LoginForm(App.this);
            }
        });
        setVisible(true);
    }
    public static void main(String[] args) {
        App app = new App();
    }
}

