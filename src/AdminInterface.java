import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminInterface extends JFrame {
    private JPanel PnlAdmin;
    private JButton BtnListDrivers;
    private JButton BtnListVehicles;
    private JButton BtnListOrders;
    private JButton BtnListUsers;

    public AdminInterface(){
        setTitle("Administration");
        setContentPane(PnlAdmin);
        setMinimumSize(new Dimension(450,474));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setVisible(true);

        BtnListUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListUsersInterface pnlUsers = new ListUsersInterface(AdminInterface.this);
            }
        });

        BtnListDrivers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListDrivers pnlDrivers= new ListDrivers(AdminInterface.this);
            }
        });
    }

    public static void main(String[] args){
        AdminInterface pnlAdmin = new AdminInterface();
    }
}
