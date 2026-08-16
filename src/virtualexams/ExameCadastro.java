package virtualexams;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ExameCadastro extends JFrame {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Principal parentScreen;
    private File selectedFile; 
    private final int medicoIdLogado;

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel nomeLabel, tipoLabel, dataLabel, resultadoLabel, arquivoLabel, pacienteLabel;
    private JTextField txtNome;
    private JComboBox<String> cmbTipo;
    private JTextField txtData;
    private JTextArea txtResultado;
    private JTextField txtArquivo;
    private JButton btnSelecionarArquivo;
    private JComboBox<PacienteItem> cmbPaciente;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private JScrollPane scrollResultado;

    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final Color COR_FUNDO_PAINEL_CLARO = new Color(245, 245, 245);
    private final Color COR_BOTAO_VERDE = new Color(70, 150, 70);
    private final Color COR_BOTAO_CINZA = new Color(150, 150, 150);
    private final Color COR_TEXTO_CAMPO_DESABILITADO = new Color(100, 100, 100);
    private final Color COR_FUNDO_CAMPO_DESABILITADO = new Color(230, 230, 230);
    private final String ICON_RESOURCE_PATH = "/app_icon.jpg"; 

    public ExameCadastro(Principal parent) {
        this.parentScreen = parent;
        this.medicoIdLogado = Usuario.getInstance().getUserId(); 
        initComponents();
        this.setLocationRelativeTo(parent); 
        this.setTitle("Virtual Exams - Cadastro de Exame");
        this.setIcon(); 
        loadPacientes();
        updateFonts(parentScreen.getCurrentFontSize());
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

    private static class PacienteItem {
        private final int id;
        private final String nome;

        public PacienteItem(int id, String nome) {
            this.id = id;
            this.nome = nome;
        }
        public int getId() { return id; }
        @Override public String toString() { return nome; }
    }

    private void initComponents() {
        mainPanel = new JPanel();
        titleLabel = new JLabel();
        nomeLabel = new JLabel();
        txtNome = new JTextField();
        tipoLabel = new JLabel();
        cmbTipo = new JComboBox<>();
        dataLabel = new JLabel();
        txtData = new JTextField();
        resultadoLabel = new JLabel();
        scrollResultado = new JScrollPane();
        txtResultado = new JTextArea();
        arquivoLabel = new JLabel();
        txtArquivo = new JTextField();
        btnSelecionarArquivo = new JButton();
        pacienteLabel = new JLabel();
        cmbPaciente = new JComboBox<>();
        btnSalvar = new JButton();
        btnCancelar = new JButton();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanel.setBackground(COR_FUNDO_PAINEL_CLARO);
        mainPanel.setLayout(null); 

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COR_AZUL_LOGO);
        titleLabel.setText("Cadastro de Exame");
        mainPanel.add(titleLabel);
        titleLabel.setBounds(20, 20, 460, 30);

        int currentY = 70;
        int labelWidth = 250;
        int fieldWidth = 450;
        int fieldHeight = 30;
        int labelHeight = 20;
        int spacing = 10;
        int smallSpacing = 5;

        nomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nomeLabel.setText("Nome do Exame:");
        mainPanel.add(nomeLabel);
        nomeLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtNome);
        txtNome.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        tipoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tipoLabel.setText("Tipo de Exame:");
        mainPanel.add(tipoLabel);
        tipoLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        cmbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Sangue", "Imagem", "Urina", "Fezes", "Outro"}));
        mainPanel.add(cmbTipo);
        cmbTipo.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        dataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataLabel.setText("Data de Realização (dd/mm/aaaa):");
        mainPanel.add(dataLabel);
        dataLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtData);
        txtData.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        resultadoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultadoLabel.setText("Resultado/Observações:");
        mainPanel.add(resultadoLabel);
        resultadoLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtResultado.setColumns(20);
        txtResultado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtResultado.setRows(5);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        scrollResultado.setViewportView(txtResultado);
        mainPanel.add(scrollResultado);
        scrollResultado.setBounds(20, currentY, fieldWidth, 100);
        currentY += 100 + spacing;

        arquivoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        arquivoLabel.setText("Arquivo (PDF/XML):");
        mainPanel.add(arquivoLabel);
        arquivoLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtArquivo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtArquivo.setEditable(false);
        txtArquivo.setBackground(COR_FUNDO_CAMPO_DESABILITADO);
        txtArquivo.setForeground(COR_TEXTO_CAMPO_DESABILITADO);
        txtArquivo.setText("(Opcional)");
        mainPanel.add(txtArquivo);
        txtArquivo.setBounds(20, currentY, 350, fieldHeight);
        btnSelecionarArquivo.setText("Selecionar");
        btnSelecionarArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSelecionarArquivo.addActionListener(this::btnSelecionarArquivoActionPerformed);
        mainPanel.add(btnSelecionarArquivo);
        btnSelecionarArquivo.setBounds(380, currentY, 90, fieldHeight);
        currentY += fieldHeight + spacing;

        pacienteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pacienteLabel.setText("Paciente:");
        mainPanel.add(pacienteLabel);
        pacienteLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        cmbPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(cmbPaciente);
        cmbPaciente.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing + 20; 

        btnSalvar.setBackground(COR_BOTAO_VERDE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(this::btnSalvarActionPerformed);
        mainPanel.add(btnSalvar);
        btnSalvar.setBounds(20, currentY, 100, 40);

        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBackground(COR_BOTAO_CINZA);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        mainPanel.add(btnCancelar);
        btnCancelar.setBounds(130, currentY, 100, 40);
        currentY += 40 + 20; 

        mainPanel.setPreferredSize(new java.awt.Dimension(500, currentY));
        getContentPane().add(mainPanel);
        pack(); 
    }

    private void loadPacientes() {
        try (Connection conn = ConexaoDB.getConnection()) {
            if (conn == null) {
                showErrorDialog("Erro de Conexão", "Não foi possível conectar ao banco de dados.");
                return;
            }
            String sql = "SELECT p.id, p.nome FROM tb_Paciente p " +
                         "JOIN tb_Medico_Paciente mp ON p.id = mp.paciente_id " +
                         "WHERE mp.medico_id = ? ORDER BY p.nome";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, this.medicoIdLogado);
                try (ResultSet rs = stmt.executeQuery()) {
                    cmbPaciente.removeAllItems();
                    cmbPaciente.addItem(new PacienteItem(0, "Selecione um paciente")); 
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nome = rs.getString("nome");
                        cmbPaciente.addItem(new PacienteItem(id, nome));
                    }
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao carregar lista de pacientes: " + e.getMessage());
        }
    }

    private void btnSelecionarArquivoActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecionar Arquivo de Exame (PDF ou XML)");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de Exame (PDF, XML)", "pdf", "xml");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            txtArquivo.setText(selectedFile.getName());
            txtArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
        } else {
            selectedFile = null;
            txtArquivo.setText("(Opcional)");
            txtArquivo.setFont(new Font("Segoe UI", Font.ITALIC, 12)); 
        }
    }

    // USO DO DAO E VALIDACOES
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
        String nomeExame = txtNome.getText().trim();
        String dataStr = txtData.getText().trim();
        Object selectedPacienteObj = cmbPaciente.getSelectedItem();

        if (ValidacoesUtil.isCampoVazio(nomeExame, dataStr)) {
            showWarningDialog("Campo Obrigatório", "Nome do exame e data são obrigatórios.");
            txtNome.requestFocus();
            return;
        }

        Date dataRealizacao = null;
        try {
            dataRealizacao = dateFormat.parse(dataStr);
        } catch (ParseException e) {
            showWarningDialog("Data Inválida", "Formato de data inválido. Use dd/mm/aaaa.");
            txtData.requestFocus();
            return;
        }
        if (!(selectedPacienteObj instanceof PacienteItem) || ((PacienteItem) selectedPacienteObj).getId() == 0) {
            showWarningDialog("Campo Obrigatório", "Selecione um paciente válido.");
            cmbPaciente.requestFocus();
            return;
        }

        String tipoExameSelecionado = cmbTipo.getSelectedItem().toString();
        String resultadoObs = txtResultado.getText().trim();
        PacienteItem pacienteSelecionado = (PacienteItem) selectedPacienteObj;
        int pacienteId = pacienteSelecionado.getId();

        byte[] arquivoBytes = null;
        String nomeOriginalArquivo = null;
        String tipoArquivo = null;

        if (selectedFile != null) {
            try {
                arquivoBytes = Files.readAllBytes(selectedFile.toPath());
                nomeOriginalArquivo = selectedFile.getName();
                tipoArquivo = getFileExtension(nomeOriginalArquivo);
            } catch (IOException e) {
                showErrorDialog("Erro de Leitura", "Erro ao ler o arquivo: " + e.getMessage());
                return; 
            }
        }

        try {
            ExameDAO exameDAO = new ExameDAO();
            if (exameDAO.salvar(nomeExame, tipoExameSelecionado, dataRealizacao, resultadoObs, arquivoBytes, nomeOriginalArquivo, tipoArquivo, pacienteId, medicoIdLogado)) {
                showInfoDialog("Cadastro Concluído", "Exame cadastrado com sucesso!");
                this.dispose(); 
                if (parentScreen != null) {
                    parentScreen.refreshExamesList(); 
                }
            }
        } catch (SQLException ex) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao salvar exame: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    public void updateFonts(int fontSize) {
        Font newFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newBoldFont = new Font("Segoe UI", Font.BOLD, fontSize);
        Font newTitleFont = new Font("Segoe UI", Font.BOLD, fontSize + 8);
        Font newItalicFont = new Font("Segoe UI", Font.ITALIC, fontSize - 2);
        Font newButtonFont = new Font("Segoe UI", Font.PLAIN, fontSize - 2);

        titleLabel.setFont(newTitleFont);
        nomeLabel.setFont(newFont);
        txtNome.setFont(newFont);
        tipoLabel.setFont(newFont);
        cmbTipo.setFont(newFont);
        dataLabel.setFont(newFont);
        txtData.setFont(newFont);
        resultadoLabel.setFont(newFont);
        txtResultado.setFont(newFont);
        arquivoLabel.setFont(newFont);
        txtArquivo.setFont(selectedFile == null ? newItalicFont : newButtonFont); 
        btnSelecionarArquivo.setFont(newButtonFont);
        pacienteLabel.setFont(newFont);
        cmbPaciente.setFont(newFont);

        btnSalvar.setFont(new Font(btnSalvar.getFont().getName(), Font.BOLD, fontSize));
        btnCancelar.setFont(new Font(btnCancelar.getFont().getName(), Font.PLAIN, fontSize));

        mainPanel.revalidate();
        mainPanel.repaint();
        pack(); 
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
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