package virtualexams;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image; // Importação para ícone
import java.awt.Toolkit; // Importação para ícone
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL; // Importação para ícone
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; // Importação para Date
import java.util.List;
import java.util.logging.Level; // Importação para Logger
import java.util.logging.Logger; // Importação para Logger
import javax.imageio.ImageIO; // Importação para ícone
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField; // Importação para data formatada
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField; // Importação para campos de texto
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager; // Importação adicionada
import javax.swing.UnsupportedLookAndFeelException; // Importação adicionada
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter; // Importação para máscara

// Classe Principal com ícone personalizado
public class Principal extends JFrame {

    // --- Variáveis de Instância --- //
    private CardLayout cardLayout;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato do banco
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final Color COR_FUNDO_PAINEL_CLARO = new Color(245, 245, 245);
    private final Color COR_BOTAO_VERDE = new Color(70, 150, 70);
    private final Color COR_BOTAO_VERMELHO = new Color(200, 80, 80);
    private final String ICON_RESOURCE_PATH = "/app_icon.jpg"; // Ícone na raiz do classpath

    // Componentes da UI
    private JButton btnConfig;
    private JButton btnDashboard;
    private JButton btnExames;
    private JButton btnLogout;
    private JButton btnPacientes;
    private JButton btnMensagens;
    private JButton btnConsultas;
    private JButton btnMeusMedicos;
    private JLabel lblUserName;
    private JLabel lblUserType;
    private JPanel panelConfig;
    private JPanel panelContent;
    private JPanel panelDashboard;
    private JPanel panelExames;
    private JPanel panelPacientes;
    private JPanel panelMensagens;
    private JPanel panelConsultas;
    private JPanel panelMeusMedicos;
    private JTable tblExames;
    private JTable tblConsultas;
    private JTable tblMensagens;
    private JList<PacienteListItem> listPacientes;
    private JList<MedicoListItem> listMedicos;
    private JSlider sliderFontSize;
    private JLabel lblFontSizeValue;
    private int currentFontSize = 14;

    // Componentes do Painel de Configurações (Edição de Dados)
    private JTextField txtNome;
    private JTextField txtEmail;
    private JFormattedTextField txtDataNascimento;
    private JTextField txtTelefone;
    private JTextField txtEndereco;
    private JButton btnSalvarDados;

    // Dados do usuário logado
    private int userIdLogado;
    private String userTypeLogado;
    private String userNameLogado;

    // Constantes colunas Exames
    private static final int COL_EXAME_ID = 0;
    private static final int COL_EXAME_NOME = 1;
    private static final int COL_EXAME_TIPO = 2;
    private static final int COL_EXAME_DATA = 3;
    private static final int COL_EXAME_PACIENTE = 4;
    private static final int COL_EXAME_MEDICO = 5;
    private static final int COL_EXAME_STATUS = 6;
    private static final int COL_EXAME_HAS_FILE = 7;
    private static final int COL_EXAME_FILENAME = 8;

    // --- Construtor --- //
    public Principal() {
        this.setupUserInfo();
        this.initComponents();
        this.btnMensagens.setVisible(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Virtual Exams - Sistema de Gerenciamento de Exames");
        this.setIcon(); // Define o ícone da janela
        this.cardLayout = (CardLayout) this.panelContent.getLayout();
        this.adjustUIBasedOnUserType();
        this.loadInitialData();
        updateFonts(this.currentFontSize);
    }

    // Método para definir o ícone da janela
    private void setIcon() {
        try {
            InputStream iconStream = getClass().getResourceAsStream(ICON_RESOURCE_PATH);
            if (iconStream != null) {
                setIconImage(ImageIO.read(iconStream));
            } else {
                System.err.println("Erro ao carregar o ícone da aplicação: " + ICON_RESOURCE_PATH + " não encontrado no classpath (pasta src).");
            }
        } catch (IOException e) {
            System.err.println("Erro de IO ao carregar o ícone da aplicação: " + e.getMessage());
        }
    }

    // --- Métodos de Inicialização da UI --- //
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        JPanel sidebarPanel = new JPanel();
        this.lblUserName = new JLabel();
        this.lblUserType = new JLabel();
        this.btnDashboard = new JButton();
        this.btnExames = new JButton();
        this.btnPacientes = new JButton();
        this.btnConsultas = new JButton();
        this.btnMeusMedicos = new JButton();
        this.btnMensagens = new JButton();
        this.btnConfig = new JButton();
        this.btnLogout = new JButton();
        this.panelContent = new JPanel();
        this.panelDashboard = new JPanel();
        this.panelExames = new JPanel();
        this.panelPacientes = new JPanel();
        this.panelConsultas = new JPanel();
        this.panelMeusMedicos = new JPanel();
        this.panelMensagens = new JPanel();
        this.panelConfig = new JPanel();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(950, 650));
        this.setPreferredSize(new Dimension(950, 650));
        mainPanel.setLayout(new BorderLayout());

        // --- Painel Lateral ---
        sidebarPanel.setBackground(COR_AZUL_LOGO);
        sidebarPanel.setPreferredSize(new Dimension(220, 650));
        sidebarPanel.setLayout(null);

        this.lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        this.lblUserName.setForeground(Color.WHITE);
        this.lblUserName.setHorizontalAlignment(JLabel.CENTER);
        sidebarPanel.add(this.lblUserName);
        this.lblUserName.setBounds(10, 20, 200, 25);

        this.lblUserType.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        this.lblUserType.setForeground(new Color(200, 200, 200));
        this.lblUserType.setHorizontalAlignment(JLabel.CENTER);
        sidebarPanel.add(this.lblUserType);
        this.lblUserType.setBounds(10, 45, 200, 20);

        int buttonY = 100;
        int buttonHeight = 40;
        int buttonSpacing = 10;

        this.btnDashboard.setText("Dashboard");
        this.setupSidebarButton(this.btnDashboard, buttonY);
        this.btnDashboard.addActionListener(e -> showPanel("dashboard"));
        sidebarPanel.add(this.btnDashboard);
        buttonY += buttonHeight + buttonSpacing;

        this.btnExames.setText("Exames");
        this.setupSidebarButton(this.btnExames, buttonY);
        this.btnExames.addActionListener(e -> {
            loadExamesData();
            showPanel("exames");
        });
        sidebarPanel.add(this.btnExames);
        buttonY += buttonHeight + buttonSpacing;

        this.btnPacientes.setText("Meus Pacientes");
        this.setupSidebarButton(this.btnPacientes, buttonY);
        this.btnPacientes.addActionListener(e -> {
            loadPacientesData();
            showPanel("pacientes");
        });
        sidebarPanel.add(this.btnPacientes);

        this.btnConsultas.setText("Minhas Consultas");
        this.setupSidebarButton(this.btnConsultas, buttonY);
        this.btnConsultas.addActionListener(e -> {
            loadConsultasData();
            showPanel("consultas");
        });
        sidebarPanel.add(this.btnConsultas);
        int nextButtonYForPatient = buttonY + buttonHeight + buttonSpacing;

        this.btnMeusMedicos.setText("Meus Médicos");
        this.setupSidebarButton(this.btnMeusMedicos, nextButtonYForPatient);
        this.btnMeusMedicos.addActionListener(e -> {
            loadMedicosData();
            showPanel("meus_medicos");
        });
        sidebarPanel.add(this.btnMeusMedicos);

        buttonY = userTypeLogado.equals("Médico") ? (buttonY + buttonHeight + buttonSpacing) : (nextButtonYForPatient + buttonHeight + buttonSpacing);

        this.btnMensagens.setText("Mensagens");
        this.setupSidebarButton(this.btnMensagens, buttonY);
        this.btnMensagens.addActionListener(e -> {
            loadMensagensData();
            showPanel("mensagens");
        });
        sidebarPanel.add(this.btnMensagens);
        buttonY += buttonHeight + buttonSpacing;

        this.btnConfig.setText("Configurações");
        this.setupSidebarButton(this.btnConfig, buttonY);
        this.btnConfig.addActionListener(e -> {
            if (userTypeLogado.equals("Paciente")) {
                loadPacienteDataForConfig(); // Carrega dados antes de mostrar
            }
            showPanel("config");
        });
        sidebarPanel.add(this.btnConfig);

        this.btnLogout.setText("Sair");
        this.setupSidebarButton(this.btnLogout, 550);
        this.btnLogout.setBackground(COR_BOTAO_VERMELHO);
        this.btnLogout.addActionListener(this::btnLogoutActionPerformed);
        sidebarPanel.add(this.btnLogout);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // --- Painel de Conteúdo ---
        this.panelContent.setLayout(new CardLayout());
        setupDashboardPanel();
        setupExamesPanel();
        setupPacientesPanel();
        setupConsultasPanel();
        setupMeusMedicosPanel();
        setupMensagensPanel();
        setupConfigPanel(); // Configura o painel de configurações
        this.panelContent.add(this.panelDashboard, "dashboard");
        this.panelContent.add(this.panelExames, "exames");
        this.panelContent.add(this.panelPacientes, "pacientes");
        this.panelContent.add(this.panelConsultas, "consultas");
        this.panelContent.add(this.panelMeusMedicos, "meus_medicos");
        this.panelContent.add(this.panelMensagens, "mensagens");
        this.panelContent.add(this.panelConfig, "config");

        mainPanel.add(this.panelContent, BorderLayout.CENTER);
        this.getContentPane().add(mainPanel);
        this.pack();
    }

    private void setupSidebarButton(JButton button, int y) {
        button.setBackground(COR_AZUL_LOGO.brighter());
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBounds(10, y, 200, 40);
    }

    private void setupDashboardPanel() {
        this.panelDashboard.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelDashboard.setLayout(null);
        JLabel lblDashboardTitle = new JLabel("Dashboard");
        lblDashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblDashboardTitle.setForeground(COR_AZUL_LOGO);
        lblDashboardTitle.setBounds(20, 20, 200, 30);
        this.panelDashboard.add(lblDashboardTitle);
        JLabel lblDashboardDesc = new JLabel("Bem-vindo ao Virtual Exams. Selecione uma opção no menu lateral.");
        lblDashboardDesc.setFont(new Font("Segoe UI", Font.PLAIN, currentFontSize));
        lblDashboardDesc.setBounds(20, 60, 600, 20);
        this.panelDashboard.add(lblDashboardDesc);
    }

    private void setupExamesPanel() {
        this.panelExames.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelExames.setLayout(null);
        JLabel lblExamesTitle = new JLabel("Gerenciamento de Exames");
        lblExamesTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblExamesTitle.setForeground(COR_AZUL_LOGO);
        lblExamesTitle.setBounds(20, 20, 350, 30);
        this.panelExames.add(lblExamesTitle);

        JButton btnNovoExame = new JButton("Novo Exame");
        btnNovoExame.setBackground(COR_BOTAO_VERDE);
        btnNovoExame.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNovoExame.setForeground(Color.WHITE);
        btnNovoExame.setBounds(20, 60, 150, 40);
        btnNovoExame.addActionListener(this::btnNovoExameActionPerformed);
        this.panelExames.add(btnNovoExame);

        JScrollPane scrollExames = new JScrollPane();
        this.tblExames = new JTable();
        this.tblExames.setFont(new Font("Segoe UI", Font.PLAIN, currentFontSize));
        this.tblExames.setRowHeight(currentFontSize + 6);
        this.tblExames.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nome", "Tipo", "Data", "Paciente", "Médico", "Status", "TemArquivo", "NomeArquivo"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case COL_EXAME_ID: return Integer.class;
                    case COL_EXAME_DATA: return String.class;
                    case COL_EXAME_HAS_FILE: return Boolean.class;
                    default: return String.class;
                }
            }
        });
        this.tblExames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tblExames.setAutoCreateRowSorter(true);

        TableColumnModel columnModel = this.tblExames.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(COL_EXAME_FILENAME));
        columnModel.removeColumn(columnModel.getColumn(COL_EXAME_HAS_FILE));
        columnModel.getColumn(COL_EXAME_ID).setPreferredWidth(40);
        columnModel.getColumn(COL_EXAME_NOME).setPreferredWidth(180);
        columnModel.getColumn(COL_EXAME_TIPO).setPreferredWidth(80);
        columnModel.getColumn(COL_EXAME_DATA).setPreferredWidth(80);
        columnModel.getColumn(COL_EXAME_PACIENTE).setPreferredWidth(120);
        columnModel.getColumn(COL_EXAME_MEDICO).setPreferredWidth(120);
        columnModel.getColumn(COL_EXAME_STATUS).setPreferredWidth(80);

        scrollExames.setViewportView(this.tblExames);
        scrollExames.setBounds(20, 120, 680, 400);
        this.panelExames.add(scrollExames);

        JButton btnVerExame = new JButton("Visualizar Detalhes");
        styleSecondaryButton(btnVerExame);
        btnVerExame.setBounds(20, 530, 150, 30);
        btnVerExame.addActionListener(this::btnVerExameActionPerformed);
        this.panelExames.add(btnVerExame);

        JButton btnAbrirArquivo = new JButton("Abrir/Baixar Arquivo");
        styleSecondaryButton(btnAbrirArquivo);
        btnAbrirArquivo.setBounds(180, 530, 160, 30);
        btnAbrirArquivo.addActionListener(this::btnAbrirArquivoActionPerformed);
        this.panelExames.add(btnAbrirArquivo);

        JButton btnEditarExame = new JButton("Editar");
        styleSecondaryButton(btnEditarExame);
        btnEditarExame.setBounds(350, 530, 100, 30);
        btnEditarExame.addActionListener(this::btnEditarExameActionPerformed);
        this.panelExames.add(btnEditarExame);

        JButton btnExcluirExame = new JButton("Excluir");
        btnExcluirExame.setBounds(460, 530, 100, 30);
        btnExcluirExame.setBackground(COR_BOTAO_VERMELHO);
        btnExcluirExame.setForeground(Color.WHITE);
        btnExcluirExame.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExcluirExame.addActionListener(this::btnExcluirExameActionPerformed);
        this.panelExames.add(btnExcluirExame);
    }

    private void setupPacientesPanel() {
        this.panelPacientes.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelPacientes.setLayout(null);
        JLabel lblPacientesTitle = new JLabel("Meus Pacientes");
        lblPacientesTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblPacientesTitle.setForeground(COR_AZUL_LOGO);
        lblPacientesTitle.setBounds(20, 20, 300, 30);
        this.panelPacientes.add(lblPacientesTitle);

        JScrollPane scrollPacientes = new JScrollPane();
        this.listPacientes = new JList<>();
        this.listPacientes.setFont(new Font("Segoe UI", Font.PLAIN, currentFontSize));
        this.listPacientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPacientes.setViewportView(this.listPacientes);
        scrollPacientes.setBounds(20, 60, 680, 460);
        this.panelPacientes.add(scrollPacientes);

        JButton btnVerPaciente = new JButton("Ver Detalhes");
        styleSecondaryButton(btnVerPaciente);
        btnVerPaciente.setBounds(20, 530, 150, 30);
        btnVerPaciente.addActionListener(this::btnVerPacienteActionPerformed);
        this.panelPacientes.add(btnVerPaciente);
    }

    private void setupConsultasPanel() {
        this.panelConsultas.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelConsultas.setLayout(null);
        JLabel lblConsultasTitle = new JLabel("Minhas Consultas");
        lblConsultasTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblConsultasTitle.setForeground(COR_AZUL_LOGO);
        lblConsultasTitle.setBounds(20, 20, 300, 30);
        this.panelConsultas.add(lblConsultasTitle);

        JScrollPane scrollConsultas = new JScrollPane();
        this.tblConsultas = new JTable();
        this.tblConsultas.setFont(new Font("Segoe UI", Font.PLAIN, currentFontSize));
        this.tblConsultas.setRowHeight(currentFontSize + 6);
        this.tblConsultas.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Data/Hora", "Médico", "Especialidade", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        this.tblConsultas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tblConsultas.setAutoCreateRowSorter(true);
        scrollConsultas.setViewportView(this.tblConsultas);
        scrollConsultas.setBounds(20, 60, 680, 460);
        this.panelConsultas.add(scrollConsultas);

        JButton btnVerConsulta = new JButton("Ver Detalhes");
        styleSecondaryButton(btnVerConsulta);
        btnVerConsulta.setBounds(20, 530, 150, 30);
        btnVerConsulta.addActionListener(this::btnVerConsultaActionPerformed);
        this.panelConsultas.add(btnVerConsulta);
    }

    private void setupMeusMedicosPanel() {
        this.panelMeusMedicos.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelMeusMedicos.setLayout(null);
        JLabel lblMedicosTitle = new JLabel("Meus Médicos");
        lblMedicosTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMedicosTitle.setForeground(COR_AZUL_LOGO);
        lblMedicosTitle.setBounds(20, 20, 300, 30);
        this.panelMeusMedicos.add(lblMedicosTitle);

        JScrollPane scrollMedicos = new JScrollPane();
        this.listMedicos = new JList<>();
        this.listMedicos.setFont(new Font("Segoe UI", Font.PLAIN, currentFontSize));
        this.listMedicos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollMedicos.setViewportView(this.listMedicos);
        scrollMedicos.setBounds(20, 60, 680, 460);
        this.panelMeusMedicos.add(scrollMedicos);

        JButton btnVerMedico = new JButton("Ver Detalhes");
        styleSecondaryButton(btnVerMedico);
        btnVerMedico.setBounds(20, 530, 150, 30);
        btnVerMedico.addActionListener(this::btnVerMedicoActionPerformed);
        this.panelMeusMedicos.add(btnVerMedico);
    }

    private void setupMensagensPanel() {
        this.panelMensagens.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelMensagens.setLayout(null);
        JLabel lblMensagensTitle = new JLabel("Mensagens");
        lblMensagensTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMensagensTitle.setForeground(COR_AZUL_LOGO);
        lblMensagensTitle.setBounds(20, 20, 300, 30);
        this.panelMensagens.add(lblMensagensTitle);

        JScrollPane scrollMensagens = new JScrollPane();
        this.tblMensagens = new JTable();
        this.tblMensagens.setFont(new Font("Segoe UI", Font.PLAIN, currentFontSize));
        this.tblMensagens.setRowHeight(currentFontSize + 6);
        this.tblMensagens.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Data/Hora", "De", "Assunto", "Lida"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class;
                return String.class;
            }
        });
        this.tblMensagens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tblMensagens.setAutoCreateRowSorter(true);
        scrollMensagens.setViewportView(this.tblMensagens);
        scrollMensagens.setBounds(20, 60, 680, 460);
        this.panelMensagens.add(scrollMensagens);

        JButton btnLerMensagem = new JButton("Ler Mensagem");
        styleSecondaryButton(btnLerMensagem);
        btnLerMensagem.setBounds(20, 530, 150, 30);
        btnLerMensagem.addActionListener(this::btnLerMensagemActionPerformed);
        this.panelMensagens.add(btnLerMensagem);

        JButton btnNovaMensagem = new JButton("Nova Mensagem");
        btnNovaMensagem.setBackground(COR_BOTAO_VERDE);
        btnNovaMensagem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNovaMensagem.setForeground(Color.WHITE);
        btnNovaMensagem.setBounds(180, 530, 150, 30);
        btnNovaMensagem.addActionListener(this::btnNovaMensagemActionPerformed);
        this.panelMensagens.add(btnNovaMensagem);
    }

    private void setupConfigPanel() {
        this.panelConfig.setBackground(COR_FUNDO_PAINEL_CLARO);
        this.panelConfig.setLayout(null);
        JLabel lblConfigTitle = new JLabel("Configurações");
        lblConfigTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblConfigTitle.setForeground(COR_AZUL_LOGO);
        lblConfigTitle.setBounds(20, 20, 300, 30);
        this.panelConfig.add(lblConfigTitle);

        // --- Configurações de Fonte ---
        JLabel lblFontSize = new JLabel("Tamanho da Fonte:");
        lblFontSize.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFontSize.setBounds(20, 70, 150, 25);
        this.panelConfig.add(lblFontSize);

        this.sliderFontSize = new JSlider(SwingConstants.HORIZONTAL, 10, 24, currentFontSize);
        this.sliderFontSize.setBounds(170, 70, 300, 25);
        this.sliderFontSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentFontSize = sliderFontSize.getValue();
                lblFontSizeValue.setText(String.valueOf(currentFontSize));
                updateFonts(currentFontSize);
            }
        });
        this.panelConfig.add(this.sliderFontSize);

        this.lblFontSizeValue = new JLabel(String.valueOf(currentFontSize));
        this.lblFontSizeValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        this.lblFontSizeValue.setBounds(480, 70, 40, 25);
        this.panelConfig.add(this.lblFontSizeValue);

        // --- Edição de Dados do Paciente (Só visível para Paciente) ---
        int formY = 120;
        int labelWidth = 150;
        int fieldWidth = 350;
        int fieldHeight = 25;
        int spacing = 10;

        JLabel lblNome = new JLabel("Nome Completo:");
        lblNome.setBounds(20, formY, labelWidth, fieldHeight);
        this.panelConfig.add(lblNome);
        this.txtNome = new JTextField();
        this.txtNome.setBounds(180, formY, fieldWidth, fieldHeight);
        this.panelConfig.add(this.txtNome);
        formY += fieldHeight + spacing;

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(20, formY, labelWidth, fieldHeight);
        this.panelConfig.add(lblEmail);
        this.txtEmail = new JTextField();
        this.txtEmail.setBounds(180, formY, fieldWidth, fieldHeight);
        this.panelConfig.add(this.txtEmail);
        formY += fieldHeight + spacing;

        JLabel lblDataNascimento = new JLabel("Data de Nascimento:");
        lblDataNascimento.setBounds(20, formY, labelWidth, fieldHeight);
        this.panelConfig.add(lblDataNascimento);
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            this.txtDataNascimento = new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            System.err.println("Erro ao criar máscara de data: " + e.getMessage());
            this.txtDataNascimento = new JFormattedTextField(); // Fallback
        }
        this.txtDataNascimento.setBounds(180, formY, 100, fieldHeight);
        this.panelConfig.add(this.txtDataNascimento);
        formY += fieldHeight + spacing;

        JLabel lblTelefone = new JLabel("Telefone:");
        lblTelefone.setBounds(20, formY, labelWidth, fieldHeight);
        this.panelConfig.add(lblTelefone);
        this.txtTelefone = new JTextField();
        this.txtTelefone.setBounds(180, formY, 150, fieldHeight);
        this.panelConfig.add(this.txtTelefone);
        formY += fieldHeight + spacing;

        JLabel lblEndereco = new JLabel("Endereço:");
        lblEndereco.setBounds(20, formY, labelWidth, fieldHeight);
        this.panelConfig.add(lblEndereco);
        this.txtEndereco = new JTextField();
        this.txtEndereco.setBounds(180, formY, fieldWidth, fieldHeight);
        this.panelConfig.add(this.txtEndereco);
        formY += fieldHeight + spacing + 20; // Espaço extra antes do botão

        this.btnSalvarDados = new JButton("Salvar Alterações");
        this.btnSalvarDados.setBackground(COR_BOTAO_VERDE);
        this.btnSalvarDados.setForeground(Color.WHITE);
        this.btnSalvarDados.setFont(new Font("Segoe UI", Font.BOLD, 14));
        this.btnSalvarDados.setBounds(180, formY, 200, 40);
        this.btnSalvarDados.addActionListener(this::btnSalvarDadosActionPerformed);
        this.panelConfig.add(this.btnSalvarDados);

        // Esconde os campos de edição se não for paciente
        boolean isPaciente = userTypeLogado.equals("Paciente");
        lblNome.setVisible(isPaciente);
        txtNome.setVisible(isPaciente);
        lblEmail.setVisible(isPaciente);
        txtEmail.setVisible(isPaciente);
        lblDataNascimento.setVisible(isPaciente);
        txtDataNascimento.setVisible(isPaciente);
        lblTelefone.setVisible(isPaciente);
        txtTelefone.setVisible(isPaciente);
        lblEndereco.setVisible(isPaciente);
        txtEndereco.setVisible(isPaciente);
        btnSalvarDados.setVisible(isPaciente);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.BLACK);
    }

    // --- Métodos de Controle e Lógica --- //
    private void setupUserInfo() {
        Usuario user = Usuario.getInstance();
        this.userIdLogado = user.getUserId();
        this.userTypeLogado = user.getUserType();
        this.userNameLogado = user.getUserName();
    }

    private void adjustUIBasedOnUserType() {
        this.lblUserName.setText(this.userNameLogado);
        this.lblUserType.setText(this.userTypeLogado);

        if (this.userTypeLogado.equals("Médico")) {
            this.btnConsultas.setVisible(false);
            this.btnMeusMedicos.setVisible(false);
            this.btnPacientes.setVisible(true);
        } else if (this.userTypeLogado.equals("Paciente")) {
            this.btnPacientes.setVisible(false);
            this.btnConsultas.setVisible(true);
            this.btnMeusMedicos.setVisible(true);
        } else {
            // Admin ou outro tipo - mostrar tudo ou ajustar conforme necessário
            this.btnPacientes.setVisible(true);
            this.btnConsultas.setVisible(true);
            this.btnMeusMedicos.setVisible(true);
        }
        // Ajusta visibilidade dos campos de config no setupConfigPanel
    }

    private void showPanel(String panelName) {
        this.cardLayout.show(this.panelContent, panelName);
    }

    private void loadInitialData() {
        loadExamesData();
        // Carregar outros dados iniciais se necessário (consultas, pacientes, etc.)
        showPanel("dashboard"); // Mostrar dashboard inicialmente
    }

    private void loadExamesData() {
        DefaultTableModel model = (DefaultTableModel) this.tblExames.getModel();
        model.setRowCount(0); // Limpa a tabela

        String sql;
        if (userTypeLogado.equals("Médico")) {
            sql = "SELECT e.id, e.nome, e.tipo, e.data_realizacao, p.nome as paciente_nome, m.nome as medico_nome, e.status, "
                    + "(e.arquivo_blob IS NOT NULL) as has_file, e.nome_arquivo_original "
                    + "FROM tb_Exame e "
                    + "JOIN tb_Paciente p ON e.paciente_id = p.id "
                    + "JOIN tb_Medico m ON e.medico_id = m.id "
                    + "WHERE e.medico_id = ? ORDER BY e.data_realizacao DESC";
        } else if (userTypeLogado.equals("Paciente")) {
            sql = "SELECT e.id, e.nome, e.tipo, e.data_realizacao, p.nome as paciente_nome, m.nome as medico_nome, e.status, "
                    + "(e.arquivo_blob IS NOT NULL) as has_file, e.nome_arquivo_original "
                    + "FROM tb_Exame e "
                    + "JOIN tb_Paciente p ON e.paciente_id = p.id "
                    + "JOIN tb_Medico m ON e.medico_id = m.id "
                    + "WHERE e.paciente_id = ? ORDER BY e.data_realizacao DESC";
        } else {
            // Admin ou outro tipo pode ver todos
            sql = "SELECT e.id, e.nome, e.tipo, e.data_realizacao, p.nome as paciente_nome, m.nome as medico_nome, e.status, "
                    + "(e.arquivo_blob IS NOT NULL) as has_file, e.nome_arquivo_original "
                    + "FROM tb_Exame e "
                    + "JOIN tb_Paciente p ON e.paciente_id = p.id "
                    + "JOIN tb_Medico m ON e.medico_id = m.id ORDER BY e.data_realizacao DESC";
        }

        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (!userTypeLogado.equals("Admin")) { // Se não for admin, precisa do ID
                stmt.setInt(1, this.userIdLogado);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo"),
                        rs.getDate("data_realizacao") != null ? dateFormat.format(rs.getDate("data_realizacao")) : "",
                        rs.getString("paciente_nome"),
                        rs.getString("medico_nome"),
                        rs.getString("status"),
                        rs.getBoolean("has_file"),
                        rs.getString("nome_arquivo_original")
                    });
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Exames", "Não foi possível carregar a lista de exames: " + e.getMessage());
            e.printStackTrace();
        }
    }

     // Método público para atualizar a lista de exames. Chamado por outras telas.
    
    public void refreshExamesList() {
        loadExamesData();
        // Opcional: Se a tela de exames não estiver visível, não precisa forçar a exibição
        // if (panelExames.isVisible()) {
        //     showPanel("exames");
        // }
    }

    private void loadPacientesData() {
        DefaultListModel<PacienteListItem> model = new DefaultListModel<>();
        String sql = "SELECT p.id, p.nome, p.cpf FROM tb_Paciente p "
                   + "JOIN tb_Medico_Paciente mp ON p.id = mp.paciente_id "
                   + "WHERE mp.medico_id = ? ORDER BY p.nome";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.userIdLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addElement(new PacienteListItem(rs.getInt("id"), rs.getString("nome"), rs.getString("cpf")));
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Pacientes", "Não foi possível carregar a lista de pacientes: " + e.getMessage());
            e.printStackTrace();
        }
        this.listPacientes.setModel(model);
    }

    private void loadConsultasData() {
        DefaultTableModel model = (DefaultTableModel) this.tblConsultas.getModel();
        model.setRowCount(0);
        String sql = "SELECT c.id, c.data_consulta, m.nome as medico_nome, m.especialidade, c.status "
                   + "FROM tb_Consulta c JOIN tb_Medico m ON c.medico_id = m.id "
                   + "WHERE c.paciente_id = ? ORDER BY c.data_consulta DESC";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.userIdLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getTimestamp("data_consulta") != null ? dateTimeFormat.format(rs.getTimestamp("data_consulta")) : "", 
                        rs.getString("medico_nome"),
                        rs.getString("especialidade"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Consultas", "Não foi possível carregar a lista de consultas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMedicosData() {
        DefaultListModel<MedicoListItem> model = new DefaultListModel<>();
        String sql = "SELECT m.id, m.nome, m.especialidade FROM tb_Medico m "
                   + "JOIN tb_Medico_Paciente mp ON m.id = mp.medico_id "
                   + "WHERE mp.paciente_id = ? ORDER BY m.nome";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.userIdLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addElement(new MedicoListItem(rs.getInt("id"), rs.getString("nome"), rs.getString("especialidade")));
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Médicos", "Não foi possível carregar a lista de médicos: " + e.getMessage());
            e.printStackTrace();
        }
        this.listMedicos.setModel(model);
    }

    private void loadMensagensData() {
        DefaultTableModel model = (DefaultTableModel) this.tblMensagens.getModel();
        model.setRowCount(0);
        String sql = "SELECT m.id, m.data_envio, COALESCE(med.nome, pac.nome) as remetente, m.assunto, m.lida "
                   + "FROM tb_Mensagem m "
                   + "LEFT JOIN tb_Medico med ON m.remetente_medico_id = med.id "
                   + "LEFT JOIN tb_Paciente pac ON m.remetente_paciente_id = pac.id "
                   + "WHERE m.destinatario_medico_id = ? OR m.destinatario_paciente_id = ? "
                   + "ORDER BY m.data_envio DESC";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (userTypeLogado.equals("Médico")) {
                stmt.setInt(1, this.userIdLogado);
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
                stmt.setInt(2, this.userIdLogado);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getTimestamp("data_envio") != null ? dateTimeFormat.format(rs.getTimestamp("data_hora")) : "",
                        rs.getString("remetente"),
                        rs.getString("assunto"),
                        rs.getBoolean("lida")
                    });
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Mensagens", "Não foi possível carregar a lista de mensagens: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Ações dos Botões --- //
    private void btnNovoExameActionPerformed(ActionEvent e) {
        ExameCadastro exameCadastro = new ExameCadastro(this);
        exameCadastro.setVisible(true);
    }

    private void btnVerExameActionPerformed(ActionEvent e) {
        int selectedRow = tblExames.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = tblExames.convertRowIndexToModel(selectedRow);
            int exameId = (int) tblExames.getModel().getValueAt(viewRow, COL_EXAME_ID);
            VerExame verExame = new VerExame(this, exameId);
            verExame.setVisible(true);
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para visualizar.");
        }
    }

    private void btnAbrirArquivoActionPerformed(ActionEvent e) {
        int selectedRow = tblExames.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = tblExames.convertRowIndexToModel(selectedRow);
            boolean hasFile = (boolean) tblExames.getModel().getValueAt(viewRow, COL_EXAME_HAS_FILE);
            if (hasFile) {
                int exameId = (int) tblExames.getModel().getValueAt(viewRow, COL_EXAME_ID);
                String originalFileName = (String) tblExames.getModel().getValueAt(viewRow, COL_EXAME_FILENAME);
                downloadAndOpenFile(exameId, originalFileName);
            } else {
                showInfoDialog("Sem Arquivo", "Este exame não possui um arquivo associado.");
            }
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para abrir o arquivo.");
        }
    }

    private void btnEditarExameActionPerformed(ActionEvent e) {
        int selectedRow = tblExames.getSelectedRow();
        if (selectedRow >= 0) {
            // Apenas médicos podem editar exames
            if (!userTypeLogado.equals("Médico")) {
                showWarningDialog("Acesso Negado", "Apenas médicos podem editar exames.");
                return;
            }
            int viewRow = tblExames.convertRowIndexToModel(selectedRow);
            int exameId = (int) tblExames.getModel().getValueAt(viewRow, COL_EXAME_ID);
            EditarExame editarExame = new EditarExame(this, exameId);
            editarExame.setVisible(true);
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para editar.");
        }
    }

    private void btnExcluirExameActionPerformed(ActionEvent e) {
        int selectedRow = tblExames.getSelectedRow();
        if (selectedRow >= 0) {
            // Apenas médicos podem excluir exames
            if (!userTypeLogado.equals("Médico")) {
                showWarningDialog("Acesso Negado", "Apenas médicos podem excluir exames.");
                return;
            }
            int viewRow = tblExames.convertRowIndexToModel(selectedRow);
            int exameId = (int) tblExames.getModel().getValueAt(viewRow, COL_EXAME_ID);
            String exameNome = (String) tblExames.getModel().getValueAt(viewRow, COL_EXAME_NOME);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o exame '" + exameNome + "' (ID: " + exameId + ")?\nEsta ação não pode ser desfeita.",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                excluirExame(exameId);
            }
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para excluir.");
        }
    }

    private void btnVerPacienteActionPerformed(ActionEvent e) {
        PacienteListItem selectedPaciente = listPacientes.getSelectedValue();
        if (selectedPaciente != null) {
            // Abre a nova tela de detalhes do paciente
            DetalhesPacienteMedico detalhesTela = new DetalhesPacienteMedico(this, selectedPaciente.getId());
            detalhesTela.setVisible(true);
        } else {
            showWarningDialog("Nenhum Paciente Selecionado", "Selecione um paciente na lista.");
        }
    }

    private void btnVerConsultaActionPerformed(ActionEvent e) {
        int selectedRow = tblConsultas.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = tblConsultas.convertRowIndexToModel(selectedRow);
            // Get data from the table model
            int consultaId = (int) tblConsultas.getModel().getValueAt(viewRow, 0);
            String dataHora = (String) tblConsultas.getModel().getValueAt(viewRow, 1);
            String medico = (String) tblConsultas.getModel().getValueAt(viewRow, 2);
            String especialidade = (String) tblConsultas.getModel().getValueAt(viewRow, 3);
            String status = (String) tblConsultas.getModel().getValueAt(viewRow, 4);

            // Format the details message with newline characters
            String detalhes = "Detalhes da Consulta (ID: " + consultaId + ")\n\n" +
                             "Data e Hora: " + dataHora + "\n" +
                             "Médico: " + medico + "\n" +
                             "Especialidade: " + especialidade + "\n" +
                             "Status: " + status;

            // Display the details using the existing helper method
            showInfoDialog("Detalhes da Consulta", detalhes);
        } else {
            showWarningDialog("Nenhuma Consulta Selecionada", "Selecione uma consulta na lista.");
        }
    }

    private void btnVerMedicoActionPerformed(ActionEvent e) {
        MedicoListItem selectedMedico = listMedicos.getSelectedValue();
        if (selectedMedico != null) {
            // Implementar lógica para ver detalhes do médico
            showInfoDialog("Detalhes do Médico", "Nome: " + selectedMedico.getNome() + "\nEspecialidade: " + selectedMedico.getEspecialidade());
        } else {
            showWarningDialog("Nenhum Médico Selecionado", "Selecione um médico na lista.");
        }
    }

    private void btnLerMensagemActionPerformed(ActionEvent e) {
        int selectedRow = tblMensagens.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = tblMensagens.convertRowIndexToModel(selectedRow);
            int mensagemId = (int) tblMensagens.getModel().getValueAt(viewRow, 0);
            // Implementar lógica para ler a mensagem
            showInfoDialog("Ler Mensagem", "ID da Mensagem: " + mensagemId);
            // Marcar como lida se necessário
        } else {
            showWarningDialog("Nenhuma Mensagem Selecionada", "Selecione uma mensagem na lista.");
        }
    }

    private void btnNovaMensagemActionPerformed(ActionEvent e) {
        // Implementar lógica para abrir tela de nova mensagem
        showInfoDialog("Nova Mensagem", "Funcionalidade de nova mensagem ainda não implementada.");
    }

    private void btnLogoutActionPerformed(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja sair?", "Confirmar Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Usuario.getInstance().clearSession(); // Limpa a sessão
            this.dispose(); // Fecha a janela principal
            new Login().setVisible(true); // Abre a tela de login
        }
    }

    // --- Métodos Auxiliares e de Banco de Dados --- //

    // Método para carregar dados do paciente no painel de configurações
    private void loadPacienteDataForConfig() {
        if (!userTypeLogado.equals("Paciente")) return; // Só carrega para pacientes

        String sql = "SELECT nome, email, data_nascimento, telefone, endereco FROM tb_Paciente WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.userIdLogado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    txtNome.setText(rs.getString("nome"));
                    txtEmail.setText(rs.getString("email"));
                    Date dataNasc = rs.getDate("data_nascimento");
                    if (dataNasc != null) {
                        txtDataNascimento.setValue(dateFormat.format(dataNasc));
                    } else {
                        txtDataNascimento.setValue(null); // Limpa se for nulo
                    }
                    txtTelefone.setText(rs.getString("telefone"));
                    txtEndereco.setText(rs.getString("endereco"));
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Dados", "Não foi possível carregar seus dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para salvar alterações nos dados do paciente
    private void btnSalvarDadosActionPerformed(ActionEvent e) {
        if (!userTypeLogado.equals("Paciente")) return; // Segurança extra

        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String dataNascStr = txtDataNascimento.getText().replace("/", "").trim();
        String telefone = txtTelefone.getText().trim();
        String endereco = txtEndereco.getText().trim();

        // Validação básica
        if (nome.isEmpty() || email.isEmpty()) {
            showWarningDialog("Campos Obrigatórios", "Nome e Email são obrigatórios.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showWarningDialog("Email Inválido", "Por favor, insira um email válido.");
            return;
        }

        Date dataNascimento = null;
        if (!dataNascStr.replace("_", "").isEmpty()) { // Verifica se não está vazio ou só com placeholders
            try {
                dataNascimento = dateFormat.parse(txtDataNascimento.getText());
            } catch (ParseException ex) {
                showWarningDialog("Data Inválida", "Formato da data de nascimento inválido. Use DD/MM/AAAA.");
                return;
            }
        }

        String sql = "UPDATE tb_Paciente SET nome = ?, email = ?, data_nascimento = ?, telefone = ?, endereco = ? WHERE id = ?";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            if (dataNascimento != null) {
                stmt.setDate(3, new java.sql.Date(dataNascimento.getTime()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }
            stmt.setString(4, telefone.isEmpty() ? null : telefone);
            stmt.setString(5, endereco.isEmpty() ? null : endereco);
            stmt.setInt(6, this.userIdLogado);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                showInfoDialog("Sucesso", "Seus dados foram atualizados com sucesso!");
                // Atualiza o nome na instância do usuário e na label da sidebar
                Usuario.getInstance().setUserName(nome);
                this.userNameLogado = nome;
                this.lblUserName.setText(nome);
            } else {
                showErrorDialog("Erro ao Salvar", "Não foi possível atualizar seus dados. Tente novamente.");
            }

        } catch (SQLException ex) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao salvar os dados: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void downloadAndOpenFile(int exameId, String originalFileName) {
        String sql = "SELECT arquivo_blob FROM tb_Exame WHERE id = ?";
        byte[] fileBytes = null;

        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exameId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("arquivo_blob");
                    if (blob != null) {
                        fileBytes = blob.getBytes(1, (int) blob.length());
                    }
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro de Download", "Erro ao buscar o arquivo no banco: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (fileBytes != null) {
            try {
                // Salva em um arquivo temporário
                Path tempDir = Files.createTempDirectory("virtualexams_");
                File tempFile = new File(tempDir.toFile(), originalFileName);
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(fileBytes);
                }

                // Tenta abrir o arquivo
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(tempFile);
                    // Opcional: marcar para deletar ao sair da JVM
                    tempFile.deleteOnExit();
                    tempDir.toFile().deleteOnExit();
                } else {
                    showWarningDialog("Não Suportado", "A abertura automática de arquivos não é suportada neste sistema.\nArquivo salvo em: " + tempFile.getAbsolutePath());
                }
            } catch (IOException e) {
                showErrorDialog("Erro ao Abrir Arquivo", "Não foi possível salvar ou abrir o arquivo temporário: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showErrorDialog("Erro de Download", "Não foi possível recuperar o conteúdo do arquivo.");
        }
    }

    private void excluirExame(int exameId) {
        String sql = "DELETE FROM tb_Exame WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exameId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                showInfoDialog("Exclusão Concluída", "Exame excluído com sucesso!");
                loadExamesData(); // Atualiza a lista
            } else {
                showErrorDialog("Erro na Exclusão", "Nenhum exame foi excluído (ID não encontrado?).");
            }
        } catch (SQLException e) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao excluir o exame: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Métodos de Acessibilidade --- //
    public int getCurrentFontSize() {
        return this.currentFontSize;
    }

    public void updateFonts(int fontSize) {
        this.currentFontSize = fontSize;
        if (this.sliderFontSize != null && this.sliderFontSize.getValue() != fontSize) {
            this.sliderFontSize.setValue(fontSize); // Atualiza o slider se a mudança veio de fora
        }
        if (this.lblFontSizeValue != null) {
            this.lblFontSizeValue.setText(String.valueOf(fontSize));
        }

        Font newFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newBoldFont = new Font("Segoe UI", Font.BOLD, fontSize);
        Font newTitleFont = new Font("Segoe UI", Font.BOLD, fontSize + 8);
        Font newSidebarUserFont = new Font("Segoe UI", Font.BOLD, fontSize + 2);
        Font newSidebarTypeFont = new Font("Segoe UI", Font.ITALIC, fontSize);
        Font newSidebarButtonFont = new Font("Segoe UI", Font.BOLD, fontSize);
        Font newTableFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newListFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newLabelFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newTextFieldFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newButtonFont = new Font("Segoe UI", Font.BOLD, fontSize);

        // Atualiza fontes dos componentes visíveis
        lblUserName.setFont(newSidebarUserFont);
        lblUserType.setFont(newSidebarTypeFont);
        btnDashboard.setFont(newSidebarButtonFont);
        btnExames.setFont(newSidebarButtonFont);
        btnPacientes.setFont(newSidebarButtonFont);
        btnConsultas.setFont(newSidebarButtonFont);
        btnMeusMedicos.setFont(newSidebarButtonFont);
        btnMensagens.setFont(newSidebarButtonFont);
        btnConfig.setFont(newSidebarButtonFont);
        btnLogout.setFont(newSidebarButtonFont);

        // Atualiza fontes dos painéis (exemplo)
        if (panelDashboard != null && panelDashboard.getComponentCount() > 1) {
            panelDashboard.getComponents()[0].setFont(newTitleFont); // Título
            panelDashboard.getComponents()[1].setFont(newFont); // Descrição
        }
        if (panelExames != null && panelExames.getComponentCount() > 6) {
            panelExames.getComponents()[0].setFont(newTitleFont); // Título
            ((JButton) panelExames.getComponents()[1]).setFont(newBoldFont); // Botão Novo
            tblExames.setFont(newTableFont);
            tblExames.setRowHeight(fontSize + 6);
            ((JButton) panelExames.getComponents()[3]).setFont(newFont); // Botão Ver
            ((JButton) panelExames.getComponents()[4]).setFont(newFont); // Botão Abrir
            ((JButton) panelExames.getComponents()[5]).setFont(newFont); // Botão Editar
            ((JButton) panelExames.getComponents()[6]).setFont(newBoldFont); // Botão Excluir
        }
        if (panelPacientes != null && panelPacientes.getComponentCount() > 2) {
            panelPacientes.getComponents()[0].setFont(newTitleFont);
            listPacientes.setFont(newListFont);
            ((JButton) panelPacientes.getComponents()[2]).setFont(newFont);
        }
        if (panelConsultas != null && panelConsultas.getComponentCount() > 2) {
            panelConsultas.getComponents()[0].setFont(newTitleFont);
            tblConsultas.setFont(newTableFont);
            tblConsultas.setRowHeight(fontSize + 6);
            ((JButton) panelConsultas.getComponents()[2]).setFont(newFont);
        }
        if (panelMeusMedicos != null && panelMeusMedicos.getComponentCount() > 2) {
            panelMeusMedicos.getComponents()[0].setFont(newTitleFont);
            listMedicos.setFont(newListFont);
            ((JButton) panelMeusMedicos.getComponents()[2]).setFont(newFont);
        }
        if (panelMensagens != null && panelMensagens.getComponentCount() > 3) {
            panelMensagens.getComponents()[0].setFont(newTitleFont);
            tblMensagens.setFont(newTableFont);
            tblMensagens.setRowHeight(fontSize + 6);
            ((JButton) panelMensagens.getComponents()[2]).setFont(newFont);
            ((JButton) panelMensagens.getComponents()[3]).setFont(newBoldFont);
        }
        if (panelConfig != null) {
            // Percorre os componentes para atualizar fontes
            for (java.awt.Component comp : panelConfig.getComponents()) {
                if (comp instanceof JLabel) {
                    if (((JLabel) comp).getText().equals("Configurações")) {
                        comp.setFont(newTitleFont);
                    } else {
                        comp.setFont(newLabelFont);
                    }
                } else if (comp instanceof JTextField || comp instanceof JFormattedTextField) {
                    comp.setFont(newTextFieldFont);
                } else if (comp instanceof JButton) {
                    comp.setFont(newButtonFont);
                } else if (comp instanceof JSlider) {
                    // Slider não tem setFont direto, mas pode ajustar labels associadas
                }
            }
            // Reajusta a fonte específica do valor do slider
            if (lblFontSizeValue != null) lblFontSizeValue.setFont(newBoldFont);
        }

        // Revalida e repinta a interface para aplicar as mudanças de fonte
        this.revalidate();
        this.repaint();
    }

    // --- Métodos de Diálogo --- //
    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // --- Classes Internas para Listas --- //
    private static class PacienteListItem {
        private int id;
        private String nome;
        private String cpf;

        public PacienteListItem(int id, String nome, String cpf) {
            this.id = id;
            this.nome = nome;
            this.cpf = cpf;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getCpf() { return cpf; }

        @Override
        public String toString() {
            return nome + " (CPF: " + cpf + ")";
        }
    }

    private static class MedicoListItem {
        private int id;
        private String nome;
        private String especialidade;

        public MedicoListItem(int id, String nome, String especialidade) {
            this.id = id;
            this.nome = nome;
            this.especialidade = especialidade;
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public String getEspecialidade() { return especialidade; }

        @Override
        public String toString() {
            return "Dr(a). " + nome + " (" + especialidade + ")";
        }
    }

    // --- Método Principal (main) --- //
    public static void main(String[] args) {
        // Define o Look and Feel para Nimbus para uma aparência mais moderna
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            // Simula um login para teste (REMOVER EM PRODUÇÃO)
            // Usuario.getInstance().setSession(1, "Ana Pereira", "Paciente");
            // new Principal().setVisible(true);

            // Inicia pela tela de Login
            new Login().setVisible(true);
        });
    }
}
