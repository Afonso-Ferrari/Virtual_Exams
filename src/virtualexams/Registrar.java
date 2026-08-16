package virtualexams;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Registrar extends JFrame {

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel nomeLabel, emailLabel, senhaLabel, confirmaSenhaLabel, tipoUsuarioLabel, cpfLabel, especialidadeLabel, crmLabel;
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmaSenha;
    private JComboBox<String> cmbTipoUsuario;
    private JTextField txtCpf;
    private JTextField txtEspecialidade;
    private JTextField txtCrm;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final Color COR_FUNDO_PAINEL_CLARO = new Color(245, 245, 245);
    private final Color COR_BOTAO_VERDE = new Color(70, 150, 70);
    private final Color COR_BOTAO_CINZA = new Color(150, 150, 150);
    private final String ICON_RESOURCE_PATH = "/app_icon.jpg"; 

    private int currentFontSize = 14;

    public Registrar() {
        initComponents();
        this.setLocationRelativeTo(null); 
        this.setTitle("Virtual Exams - Cadastro de Usuário");
        this.setIcon(); 
        updateFonts(this.currentFontSize); 
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
        mainPanel = new JPanel();
        titleLabel = new JLabel();
        nomeLabel = new JLabel();
        txtNome = new JTextField();
        emailLabel = new JLabel();
        txtEmail = new JTextField();
        senhaLabel = new JLabel();
        txtSenha = new JPasswordField();
        confirmaSenhaLabel = new JLabel();
        txtConfirmaSenha = new JPasswordField();
        tipoUsuarioLabel = new JLabel();
        cmbTipoUsuario = new JComboBox<>();
        cpfLabel = new JLabel();
        txtCpf = new JTextField();
        especialidadeLabel = new JLabel();
        txtEspecialidade = new JTextField();
        crmLabel = new JLabel();
        txtCrm = new JTextField();
        btnRegistrar = new JButton();
        btnCancelar = new JButton();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanel.setBackground(COR_FUNDO_PAINEL_CLARO);
        mainPanel.setLayout(null); 

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COR_AZUL_LOGO);
        titleLabel.setText("Cadastro de Usuário");
        mainPanel.add(titleLabel);
        titleLabel.setBounds(20, 20, 460, 30);

        int currentY = 70;
        int labelWidth = 150;
        int fieldWidth = 450;
        int fieldHeight = 30;
        int labelHeight = 20;
        int spacing = 10;
        int smallSpacing = 5;

        nomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nomeLabel.setText("Nome Completo:");
        mainPanel.add(nomeLabel);
        nomeLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtNome);
        txtNome.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setText("Email:");
        mainPanel.add(emailLabel);
        emailLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtEmail);
        txtEmail.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        senhaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        senhaLabel.setText("Senha:");
        mainPanel.add(senhaLabel);
        senhaLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtSenha);
        txtSenha.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        confirmaSenhaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmaSenhaLabel.setText("Confirmar Senha:");
        mainPanel.add(confirmaSenhaLabel);
        confirmaSenhaLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtConfirmaSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtConfirmaSenha);
        txtConfirmaSenha.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        tipoUsuarioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tipoUsuarioLabel.setText("Tipo de Usuário:");
        mainPanel.add(tipoUsuarioLabel);
        tipoUsuarioLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        cmbTipoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTipoUsuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Selecione", "Médico", "Paciente"}));
        cmbTipoUsuario.addActionListener(this::cmbTipoUsuarioActionPerformed);
        mainPanel.add(cmbTipoUsuario);
        cmbTipoUsuario.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        cpfLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cpfLabel.setText("CPF:");
        mainPanel.add(cpfLabel);
        cpfLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtCpf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtCpf);
        txtCpf.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        especialidadeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        especialidadeLabel.setText("Especialidade:");
        mainPanel.add(especialidadeLabel);
        especialidadeLabel.setBounds(20, currentY, labelWidth, labelHeight);
        especialidadeLabel.setVisible(false);
        int medicoFieldsY = currentY + labelHeight + smallSpacing;

        txtEspecialidade.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtEspecialidade);
        txtEspecialidade.setBounds(20, medicoFieldsY, fieldWidth, fieldHeight);
        txtEspecialidade.setVisible(false);
        medicoFieldsY += fieldHeight + spacing;

        crmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        crmLabel.setText("CRM:");
        mainPanel.add(crmLabel);
        crmLabel.setBounds(20, medicoFieldsY, labelWidth, labelHeight);
        crmLabel.setVisible(false);
        medicoFieldsY += labelHeight + smallSpacing;

        txtCrm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtCrm);
        txtCrm.setBounds(20, medicoFieldsY, fieldWidth, fieldHeight);
        txtCrm.setVisible(false);
        medicoFieldsY += fieldHeight + spacing + 20; 

        btnRegistrar.setBackground(COR_BOTAO_VERDE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);
        mainPanel.add(btnRegistrar);
        btnRegistrar.setBounds(20, medicoFieldsY, 100, 40);

        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBackground(COR_BOTAO_CINZA);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        mainPanel.add(btnCancelar);
        btnCancelar.setBounds(130, medicoFieldsY, 100, 40);
        currentY = medicoFieldsY + 40 + 20; 

        mainPanel.setPreferredSize(new java.awt.Dimension(500, currentY));
        getContentPane().add(mainPanel);
        pack(); 
    }

    private void cmbTipoUsuarioActionPerformed(java.awt.event.ActionEvent evt) {
        String tipoUsuario = cmbTipoUsuario.getSelectedItem().toString();
        boolean isMedico = "Médico".equals(tipoUsuario);

        especialidadeLabel.setVisible(isMedico);
        txtEspecialidade.setVisible(isMedico);
        crmLabel.setVisible(isMedico);
        txtCrm.setVisible(isMedico);
        pack();
    }

    // USO DO DAO E VALIDACOES
    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String cpf = txtCpf.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirmaSenha = new String(txtConfirmaSenha.getPassword());
        String tipoUsuario = cmbTipoUsuario.getSelectedItem().toString();
        String especialidade = txtEspecialidade.getText().trim();
        String crm = txtCrm.getText().trim();

        if (ValidacoesUtil.isCampoVazio(nome, email, cpf, senha) || cmbTipoUsuario.getSelectedIndex() == 0) {
            showWarningDialog("Campos Obrigatórios", "Todos os campos comuns são obrigatórios.");
            return;
        }

        if (!ValidacoesUtil.isEmailValido(email)) {
            showWarningDialog("Email Inválido", "Por favor, digite um email válido.");
            return;
        }

        if (!senha.equals(confirmaSenha)) {
            showErrorDialog("Senhas Diferentes", "As senhas não coincidem.");
            txtSenha.requestFocus();
            return;
        }

        boolean isMedico = "Médico".equals(tipoUsuario);
        if (isMedico && ValidacoesUtil.isCampoVazio(especialidade, crm)) {
            showWarningDialog("Campos Obrigatórios", "Especialidade e CRM são obrigatórios para médicos.");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();
            if (dao.registrar(nome, email, senha, cpf, tipoUsuario, especialidade, crm)) {
                showInfoDialog("Cadastro Concluído", "Usuário cadastrado com sucesso!");
                this.dispose();
                new Login().setVisible(true);
            }
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                showErrorDialog("Erro de Cadastro", "Email ou CPF já cadastrado no sistema.");
            } else {
                showErrorDialog("Erro de Banco de Dados", "Erro crítico: " + e.getMessage());
            }
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        if (!txtNome.getText().trim().isEmpty() || !txtEmail.getText().trim().isEmpty() || new String(txtSenha.getPassword()).length() > 0) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Deseja realmente cancelar o cadastro? Os dados não serão salvos.",
                    "Confirmar Cancelamento",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }
        this.dispose();
        new Login().setVisible(true); 
    }

    public void updateFonts(int fontSize) {
        this.currentFontSize = fontSize;
        Font newFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newBoldFont = new Font("Segoe UI", Font.BOLD, fontSize);
        Font newTitleFont = new Font("Segoe UI", Font.BOLD, fontSize + 8);

        titleLabel.setFont(newTitleFont);
        nomeLabel.setFont(newFont);
        txtNome.setFont(newFont);
        emailLabel.setFont(newFont);
        txtEmail.setFont(newFont);
        senhaLabel.setFont(newFont);
        txtSenha.setFont(newFont);
        confirmaSenhaLabel.setFont(newFont);
        txtConfirmaSenha.setFont(newFont);
        tipoUsuarioLabel.setFont(newFont);
        cmbTipoUsuario.setFont(newFont);
        cpfLabel.setFont(newFont);
        txtCpf.setFont(newFont);
        especialidadeLabel.setFont(newFont);
        txtEspecialidade.setFont(newFont);
        crmLabel.setFont(newFont);
        txtCrm.setFont(newFont);

        btnRegistrar.setFont(new Font(btnRegistrar.getFont().getName(), Font.BOLD, fontSize));
        btnCancelar.setFont(new Font(btnCancelar.getFont().getName(), Font.PLAIN, fontSize));

        mainPanel.revalidate();
        mainPanel.repaint();
        pack(); 
    }

    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}