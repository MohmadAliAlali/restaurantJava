package restaurant.customerGUI;

import restaurant.database.DataBaseCustomer;
import restaurant.gui.MainFrame;
import restaurant.model.User;
import javax.swing.*;
import java.awt.*;


public class LoginDialogCustomer extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean succeeded;

    public LoginDialogCustomer(Frame parent) {
        super(parent, "login", true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbUsername = new JLabel("user name: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        usernameField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(usernameField, cs);

        JLabel lbPassword = new JLabel("password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        passwordField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(passwordField, cs);


        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> login());

        JButton btnRegister = new JButton("register");
        btnRegister.addActionListener(e -> showRegisterDialog());

        JButton btnCancel = new JButton("cancel");
        btnCancel.addActionListener(e -> dispose());

        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnRegister);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

    }


    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = DataBaseCustomer.authenticateUser(username, password);
        if (user != null) {
            succeeded = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "User name or password is false",
                    "error in login",
                    JOptionPane.ERROR_MESSAGE);
            usernameField.setText("");
            passwordField.setText("");
            succeeded = false;
        }
    }

    private void showRegisterDialog() {
        CustomerMain mainFrame = (CustomerMain) SwingUtilities.getWindowAncestor(this);
        RegisterDialogCustomer registerDialog = new RegisterDialogCustomer(mainFrame);
        registerDialog.setVisible(true);
    }
    public String getUser(){
        return usernameField.getText();
    }
    public boolean isSucceeded() {
        return succeeded;
    }
}
