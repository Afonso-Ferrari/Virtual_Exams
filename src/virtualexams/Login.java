package virtualexams;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {

    private JButton btnLogin;
    private JButton btnRegister;
    private JComboBox<String> cmbUserType;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final String LOGO_RESOURCE_PATH = "/1000762800.jpg"; 
    private final String ICON_RESOURCE_PATH = "/app_icon.jpg"; 

    public Login() {
        this.initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Virtual Exams - Login");
        this.setIcon(); 
        this.pack(); 
        this.setMinimumSize(this.getSize()); 
    }

    private void setIcon() {
        try {
            InputStream iconStream = getClass().getResourceAsStream(ICON_RESOURCE_PATH);
            if (iconStream != null) {
                setIconImage(ImageIO.read(iconStream));
            }
        } catch (IOException e) {
            System.err.println("Erro de IO ao carregar o ícone da aplicação: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        JLabel emailLabel = new JLabel();
        this.txtEmail = new JTextField();
        JLabel passwordLabel = new JLabel();
        this.txtPassword = new JPasswordField();
        JLabel userTypeLabel = new JLabel();
        this.cmbUserType = new JComboBox<>();
        this.btnLogin = new JButton();
        this.btnRegister = new JButton();
        JLabel logoLabel = new JLabel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); 

        int logoWidth = 200;
        int logoHeight = 60;
        try {
            InputStream logoStream = getClass().getResourceAsStream(LOGO_RESOURCE_PATH);
            if (logoStream != null) {
                ImageIcon originalIcon = new ImageIcon(ImageIO.read(logoStream));
                int originalWidth = originalIcon.getIconWidth();
                int originalHeight = originalIcon.getIconHeight();
                logoHeight = (originalWidth > 0) ? (originalHeight * logoWidth) / originalWidth : 60;
                Image scaledImage = originalIcon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                throw new IOException("Recurso da logo não encontrado");
            }
        } catch (Exception e) {
            logoLabel.setText("Virtual Exams");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(COR_AZUL_LOGO);
            logoHeight = 60;
        }
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setPreferredSize(new Dimension(logoWidth, logoHeight));

        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setText("Email:");

        this.txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtEmail.getPreferredSize().height));

        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setText("Senha:");

        this.txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtPassword.getPreferredSize().height));

        userTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTypeLabel.setText("Tipo de Usuário:");

        this.cmbUserType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.cmbUserType.setModel(new DefaultComboBoxModel<>(new String[]{"Médico", "Paciente"}));
        this.cmbUserType.setMaximumSize(new Dimension(Integer.MAX_VALUE, cmbUserType.getPreferredSize().height));

        this.btnLogin.setBackground(COR_AZUL_LOGO);
        this.btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        this.btnLogin.setForeground(Color.WHITE);
        this.btnLogin.setText("Entrar");
        this.btnLogin.addActionListener(this::btnLoginActionPerformed);
        this.btnLogin.setPreferredSize(new Dimension(120, 40)); 

        this.btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.btnRegister.setText("Cadastrar");
        this.btnRegister.setForeground(COR_AZUL_LOGO);
        this.btnRegister.setBackground(Color.WHITE);
        this.btnRegister.setBorder(javax.swing.BorderFactory.createLineBorder(COR_AZUL_LOGO));
        this.btnRegister.addActionListener(this::btnRegisterActionPerformed);
        this.btnRegister.setPreferredSize(new Dimension(120, 40)); 

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(logoLabel)
            .addComponent(emailLabel, GroupLayout.Alignment.LEADING)
            .addComponent(txtEmail)
            .addComponent(passwordLabel, GroupLayout.Alignment.LEADING)
            .addComponent(txtPassword)
            .addComponent(userTypeLabel, GroupLayout.Alignment.LEADING)
            .addComponent(cmbUserType)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnLogin)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRegister))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(logoLabel)
            .addGap(25) 
            .addComponent(emailLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED) 
            .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(15) 
            .addComponent(passwordLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(15)
            .addComponent(userTypeLabel)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cmbUserType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addGap(30) 
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnLogin)
                .addComponent(btnRegister))
        );

        getContentPane().add(mainPanel);
    }

    private void btnLoginActionPerformed(ActionEvent evt) {
        String email = this.txtEmail.getText().trim();
        String password = new String(this.txtPassword.getPassword());
        String userType = this.cmbUserType.getSelectedItem().toString();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (this.authenticateUser(email, password, userType)) {
            this.openMainScreen();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Email, senha ou tipo de usuário incorretos.", "Falha no Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnRegisterActionPerformed(ActionEvent evt) {
        this.dispose();
        Registrar registrarScreen = new Registrar();
        registrarScreen.setVisible(true);
    }

    // USO DO DAO
    private boolean authenticateUser(String email, String password, String userType) {
        try {
            UsuarioDAO dao = new UsuarioDAO();
            return dao.autenticar(email, password, userType);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar credenciais: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void openMainScreen() {
        Principal principal = new Principal();
        principal.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, "Não foi possível definir o Look and Feel Nimbus.", ex);
        }

        EventQueue.invokeLater(() -> {
            if (ConexaoDB.testConnection()) {
                new Login().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Não foi possível conectar ao banco de dados.\nVerifique as configurações e se o servidor MySQL está ativo.",
                        "Erro Crítico de Conexão",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}