package virtualexams;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image; // Importação para ícone
import java.awt.Toolkit; // Importação para ícone
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException; // Importação para ícone
import java.io.InputStream; // Importação para ícone
import java.net.URL; // Importação para ícone
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO; // Importação para ícone
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


// Tela de Visualização de Exame com ícone personalizado

public class VerExame extends JFrame {

    // Declaração de variáveis da UI
    private JButton btnDownload;
    private JButton btnFechar;
    private JLabel lblArquivo;
    private JLabel lblDataEnvio;
    private JLabel lblDataRealizacao;
    private JLabel lblMedico;
    private JLabel lblNome;
    private JLabel lblPaciente;
    private JLabel lblTipo;
    private JTextArea txtResultado;
    private JLabel titleLabel;
    private JLabel nomeLabel, tipoLabel, dataRealizacaoLabel, dataEnvioLabel, medicoLabel, pacienteLabel, resultadoLabel, arquivoLabel;
    private JPanel mainPanel;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Principal parentFrame; // Referência à tela principal
    private final int exameId;
    private byte[] arquivoBlobAtual; // Armazena o conteúdo binário do arquivo
    private String nomeOriginalArquivoAtual; // Armazena o nome original do arquivo
    private String tipoArquivoAtual; // Armazena o tipo/extensão do arquivo

    // Cores padronizadas
    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final Color COR_FUNDO_PAINEL_CLARO = new Color(245, 245, 245);
    private final Color COR_BOTAO_VERDE = new Color(70, 150, 70);
    private final Color COR_BOTAO_CINZA = new Color(150, 150, 150);
    private final String ICON_RESOURCE_PATH = "/app_icon.jpg"; // Ícone na raiz do classpath

    /**
     * Construtor da tela de visualização de exame
     *
     * @param parentFrame Referência à tela principal
     * @param exameId ID do exame a ser visualizado
     */
    public VerExame(Principal parentFrame, int exameId) {
        this.parentFrame = parentFrame;
        this.exameId = exameId;
        initComponents();
        this.setLocationRelativeTo(parentFrame); // Centraliza em relação à tela principal
        this.setTitle("Virtual Exams - Visualização de Exame");
        this.setIcon(); // Define o ícone da janela
        carregarDadosExame();
        // Aplica a fonte atual definida na tela Principal
        updateFonts(parentFrame.getCurrentFontSize());
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

    
    // Inicializa os componentes da interface gráfica.
    
    private void initComponents() {
        // Declaração dos componentes
        mainPanel = new JPanel();
        titleLabel = new JLabel();
        nomeLabel = new JLabel();
        lblNome = new JLabel();
        tipoLabel = new JLabel();
        lblTipo = new JLabel();
        dataRealizacaoLabel = new JLabel();
        lblDataRealizacao = new JLabel();
        dataEnvioLabel = new JLabel();
        lblDataEnvio = new JLabel();
        resultadoLabel = new JLabel();
        JScrollPane scrollResultado = new JScrollPane();
        txtResultado = new JTextArea();
        arquivoLabel = new JLabel();
        lblArquivo = new JLabel();
        medicoLabel = new JLabel();
        lblMedico = new JLabel();
        pacienteLabel = new JLabel();
        lblPaciente = new JLabel();
        btnDownload = new JButton();
        btnFechar = new JButton();

        // Configuração da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Configuração do painel principal
        mainPanel.setBackground(COR_FUNDO_PAINEL_CLARO);
        mainPanel.setLayout(null);

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COR_AZUL_LOGO);
        titleLabel.setText("Visualização de Exame");
        mainPanel.add(titleLabel);
        titleLabel.setBounds(20, 20, 460, 30);

        int currentY = 70;
        int labelWidth = 150;
        int valueWidth = 300;
        int fieldHeight = 20;
        int spacing = 10;

        // Nome do Exame
        nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nomeLabel.setText("Nome do Exame:");
        mainPanel.add(nomeLabel);
        nomeLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNome.setText("Carregando...");
        mainPanel.add(lblNome);
        lblNome.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Tipo de Exame
        tipoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tipoLabel.setText("Tipo de Exame:");
        mainPanel.add(tipoLabel);
        tipoLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTipo.setText("Carregando...");
        mainPanel.add(lblTipo);
        lblTipo.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Data de Realização
        dataRealizacaoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dataRealizacaoLabel.setText("Data de Realização:");
        mainPanel.add(dataRealizacaoLabel);
        dataRealizacaoLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblDataRealizacao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDataRealizacao.setText("Carregando...");
        mainPanel.add(lblDataRealizacao);
        lblDataRealizacao.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Data de Envio
        dataEnvioLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dataEnvioLabel.setText("Data de Envio:");
        mainPanel.add(dataEnvioLabel);
        dataEnvioLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblDataEnvio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDataEnvio.setText("Carregando...");
        mainPanel.add(lblDataEnvio);
        lblDataEnvio.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Médico
        medicoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        medicoLabel.setText("Médico:");
        mainPanel.add(medicoLabel);
        medicoLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblMedico.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMedico.setText("Carregando...");
        mainPanel.add(lblMedico);
        lblMedico.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Paciente
        pacienteLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pacienteLabel.setText("Paciente:");
        mainPanel.add(pacienteLabel);
        pacienteLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPaciente.setText("Carregando...");
        mainPanel.add(lblPaciente);
        lblPaciente.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing + 10; // Mais espaço antes do resultado

        // Resultado
        resultadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultadoLabel.setText("Resultado:");
        mainPanel.add(resultadoLabel);
        resultadoLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        currentY += fieldHeight + 5;
        txtResultado.setEditable(false);
        txtResultado.setColumns(20);
        txtResultado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtResultado.setRows(5);
        txtResultado.setText("Carregando...");
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        scrollResultado.setViewportView(txtResultado);
        mainPanel.add(scrollResultado);
        scrollResultado.setBounds(20, currentY, 460, 150);
        currentY += 150 + spacing + 10; // Mais espaço antes do arquivo

        // Arquivo
        arquivoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        arquivoLabel.setText("Arquivo:");
        mainPanel.add(arquivoLabel);
        arquivoLabel.setBounds(20, currentY, labelWidth, fieldHeight);
        lblArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblArquivo.setText("Carregando...");
        mainPanel.add(lblArquivo);
        lblArquivo.setBounds(labelWidth + 30, currentY, valueWidth, fieldHeight);
        currentY += fieldHeight + spacing + 20; // Mais espaço antes dos botões

        // Botões de ação
        btnDownload.setBackground(COR_BOTAO_VERDE);
        btnDownload.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDownload.setForeground(Color.WHITE);
        btnDownload.setText("Download");
        btnDownload.setEnabled(false); // Desabilitado por padrão
        btnDownload.addActionListener(this::btnDownloadActionPerformed);
        mainPanel.add(btnDownload);
        btnDownload.setBounds(20, currentY, 120, 40);

        btnFechar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnFechar.setText("Fechar");
        btnFechar.setBackground(COR_BOTAO_CINZA);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.addActionListener(this::btnFecharActionPerformed);
        mainPanel.add(btnFechar);
        btnFechar.setBounds(150, currentY, 100, 40);
        currentY += 40 + 20; // Espaço final

        // Configuração final da janela
        mainPanel.setPreferredSize(new java.awt.Dimension(500, currentY));
        getContentPane().add(mainPanel);
        pack(); // Ajusta o tamanho da janela ao conteúdo
    }

    
    // Carrega os dados do exame do banco de dados, incluindo o arquivo binário.
    
    private void carregarDadosExame() {
        String sql = "SELECT e.*, m.nome as medico_nome, p.nome as paciente_nome, "
                + "e.arquivo_blob, e.nome_arquivo_original, e.tipo_arquivo "
                + "FROM tb_Exame e "
                + "JOIN tb_Medico m ON e.medico_id = m.id "
                + "JOIN tb_Paciente p ON e.paciente_id = p.id "
                + "WHERE e.id = ?";

        try (Connection conn = ConexaoDB.getConnection()) {
            if (conn == null) {
                showErrorDialog("Erro de Conexão", "Não foi possível conectar ao banco de dados.");
                this.dispose();
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, exameId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Preencher os campos com os dados do exame
                        lblNome.setText(rs.getString("nome"));
                        lblTipo.setText(rs.getString("tipo"));

                        Date dataRealizacao = rs.getDate("data_realizacao");
                        lblDataRealizacao.setText(dataRealizacao != null ? dateFormat.format(dataRealizacao) : "Não informada");

                        Date dataEnvio = rs.getTimestamp("data_envio");
                        lblDataEnvio.setText(dataEnvio != null ? dateFormat.format(dataEnvio) : "Não informada");

                        lblMedico.setText(rs.getString("medico_nome"));
                        lblPaciente.setText(rs.getString("paciente_nome"));

                        String resultado = rs.getString("resultado");
                        txtResultado.setText(resultado != null && !resultado.isEmpty() ? resultado : "Nenhum resultado disponível.");
                        txtResultado.setCaretPosition(0); // Volta para o início do texto

                        // Carregar dados do arquivo BLOB
                        Blob blob = rs.getBlob("arquivo_blob");
                        if (blob != null) {
                            this.arquivoBlobAtual = blob.getBytes(1, (int) blob.length());
                            this.nomeOriginalArquivoAtual = rs.getString("nome_arquivo_original");
                            this.tipoArquivoAtual = rs.getString("tipo_arquivo");

                            lblArquivo.setText(this.nomeOriginalArquivoAtual != null ? this.nomeOriginalArquivoAtual : "Arquivo sem nome");
                            btnDownload.setEnabled(true); // Habilita o botão de download
                        } else {
                            lblArquivo.setText("Nenhum arquivo disponível.");
                            btnDownload.setEnabled(false);
                        }
                    } else {
                        showErrorDialog("Erro", "Exame não encontrado.");
                        this.dispose();
                    }
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao carregar dados do exame: " + e.getMessage());
            e.printStackTrace(); // Ajuda na depuração
            this.dispose();
        }
    }

    
    // Ação do botão Download: Permite ao usuário salvar o arquivo armazenado no banco
    
    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.arquivoBlobAtual == null || this.nomeOriginalArquivoAtual == null) {
            showWarningDialog("Download Indisponível", "Não há arquivo válido associado a este exame para download.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Exame Como...");
        fileChooser.setSelectedFile(new File(this.nomeOriginalArquivoAtual));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            Path destinationPath = fileToSave.toPath();

            try {
                Files.write(destinationPath, this.arquivoBlobAtual);
                showInfoDialog("Download Concluído", "Arquivo salvo com sucesso em:\n" + destinationPath.toString());

                // Opcional: Abrir o arquivo após salvar
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(fileToSave);
                    }
                } catch (IOException | SecurityException ex) {
                    System.err.println("Erro ao tentar abrir o arquivo baixado: " + ex.getMessage());
                }

            } catch (IOException e) {
                showErrorDialog("Erro de Download", "Erro ao salvar o arquivo: " + e.getMessage());
                e.printStackTrace();
            } catch (SecurityException e) {
                showErrorDialog("Erro de Segurança", "Não foi possível salvar o arquivo devido a restrições de segurança.");
                e.printStackTrace();
            }
        }
    }
    
    // Ação do botão Fechar: Fecha a janela.
    
    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // --- Métodos de Acessibilidade --- //
    public void updateFonts(int fontSize) {
        Font newFont = new Font("Segoe UI", Font.PLAIN, fontSize);
        Font newBoldFont = new Font("Segoe UI", Font.BOLD, fontSize);
        Font newTitleFont = new Font("Segoe UI", Font.BOLD, fontSize + 8);

        titleLabel.setFont(newTitleFont);
        nomeLabel.setFont(newBoldFont);
        lblNome.setFont(newFont);
        tipoLabel.setFont(newBoldFont);
        lblTipo.setFont(newFont);
        dataRealizacaoLabel.setFont(newBoldFont);
        lblDataRealizacao.setFont(newFont);
        dataEnvioLabel.setFont(newBoldFont);
        lblDataEnvio.setFont(newFont);
        medicoLabel.setFont(newBoldFont);
        lblMedico.setFont(newFont);
        pacienteLabel.setFont(newBoldFont);
        lblPaciente.setFont(newFont);
        resultadoLabel.setFont(newBoldFont);
        txtResultado.setFont(newFont);
        arquivoLabel.setFont(newBoldFont);
        lblArquivo.setFont(newFont);

        // Ajusta fonte dos botões
        btnDownload.setFont(new Font(btnDownload.getFont().getName(), Font.BOLD, fontSize));
        btnFechar.setFont(new Font(btnFechar.getFont().getName(), Font.PLAIN, fontSize));

        // Revalida e redesenha o painel
        mainPanel.revalidate();
        mainPanel.repaint();
        pack(); // Reajusta o tamanho da janela à nova fonte
    }

    // --- Métodos Auxiliares de UI --- //
    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    // Método main (opcional, para testar a tela isoladamente)
    // public static void main(String args[]) {
    //     java.awt.EventQueue.invokeLater(() -> {
    //         // Para testar, precisaríamos de uma instância mock de Principal ou null
    //         // e um ID de exame válido.
    //         // new VerExame(null, 1).setVisible(true);
    //     });
    // }
}

