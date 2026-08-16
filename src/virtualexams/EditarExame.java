package virtualexams;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image; // Importação para ícone
import java.awt.Toolkit; // Importação para ícone
import java.io.File;
import java.io.IOException; // Importação para ícone
import java.io.InputStream; // Importação para ícone
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
import java.util.Date;
import javax.imageio.ImageIO; // Importação para ícone
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

// Tela de Edição de Exame com ícone personalizado

public class EditarExame extends JFrame {

    // --- Declarações de Variáveis --- //
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final int exameId;
    private final Principal parentScreen;

    // Dados do arquivo atual (carregado do BD)
    private byte[] arquivoBlobAtual;
    private String nomeOriginalArquivoAtual;
    private String tipoArquivoAtual;

    // Novo arquivo selecionado pelo usuário
    private File selectedFile;
    private boolean removerArquivo = false;

    // Componentes da UI
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel nomeLabel, tipoLabel, dataLabel, resultadoLabel, arquivoLabel;
    private JTextField txtNome;
    private JComboBox<String> cmbTipo;
    private JTextField txtData;
    private JTextArea txtResultado;
    private JTextField txtArquivo;
    private JButton btnSelecionarArquivo;
    private JButton btnRemoverArquivo;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private JButton btnVisualizarArquivo;
    private JScrollPane scrollResultado;

    // Cores padronizadas
    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final Color COR_FUNDO_PAINEL_CLARO = new Color(245, 245, 245);
    private final Color COR_BOTAO_VERDE = new Color(70, 150, 70);
    private final Color COR_BOTAO_VERMELHO = new Color(200, 80, 80);
    private final Color COR_BOTAO_CINZA = new Color(150, 150, 150);
    private final Color COR_TEXTO_CAMPO_DESABILITADO = new Color(100, 100, 100);
    private final Color COR_FUNDO_CAMPO_DESABILITADO = new Color(230, 230, 230);
    private final String ICON_RESOURCE_PATH = "/app_icon.jpg"; // Ícone na raiz do classpath

    /**
     * Construtor da tela de edição de exame
     *
     * @param parent Referência à tela principal que chamou esta tela
     * @param exameId ID do exame a ser editado
     */
    public EditarExame(Principal parent, int exameId) {
        this.exameId = exameId;
        this.parentScreen = parent;
        initComponents();
        this.setLocationRelativeTo(parent); // Centraliza em relação à tela principal
        this.setTitle("Virtual Exams - Edição de Exame [ID: " + exameId + "]");
        this.setIcon(); // Define o ícone da janela
        carregarDadosExame();
        // Aplica a fonte atual definida na tela Principal
        updateFonts(parentScreen.getCurrentFontSize());
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

     // Método que inicializa os componentes da interface gráfica

    private void initComponents() {
        // Declaração dos componentes
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
        btnRemoverArquivo = new JButton();
        btnVisualizarArquivo = new JButton();
        btnSalvar = new JButton();
        btnCancelar = new JButton();

        // Configuração da janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Configuração do painel principal
        mainPanel.setBackground(COR_FUNDO_PAINEL_CLARO);
        mainPanel.setLayout(null); // Layout absoluto

        // Título
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COR_AZUL_LOGO);
        titleLabel.setText("Edição de Exame");
        mainPanel.add(titleLabel);
        titleLabel.setBounds(20, 20, 400, 30);

        int currentY = 70;
        int labelWidth = 250; // Largura maior para data
        int fieldWidth = 490;
        int fieldHeight = 30;
        int labelHeight = 20;
        int spacing = 10;
        int smallSpacing = 5;

        // Campo Nome
        nomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nomeLabel.setText("Nome do Exame:");
        mainPanel.add(nomeLabel);
        nomeLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtNome);
        txtNome.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Campo Tipo
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

        // Campo Data
        dataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataLabel.setText("Data de Realização (dd/mm/aaaa):");
        mainPanel.add(dataLabel);
        dataLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;
        txtData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtData);
        txtData.setBounds(20, currentY, fieldWidth, fieldHeight);
        currentY += fieldHeight + spacing;

        // Campo Resultado/Observações
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
        scrollResultado.setBounds(20, currentY, fieldWidth, 150);
        currentY += 150 + spacing;

        // Campo Arquivo
        arquivoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        arquivoLabel.setText("Arquivo Associado (PDF/XML):");
        mainPanel.add(arquivoLabel);
        arquivoLabel.setBounds(20, currentY, labelWidth, labelHeight);
        currentY += labelHeight + smallSpacing;

        txtArquivo.setFont(new Font("Segoe UI", Font.ITALIC, 12)); // Fonte menor e itálico
        txtArquivo.setEditable(false);
        txtArquivo.setBackground(COR_FUNDO_CAMPO_DESABILITADO);
        txtArquivo.setForeground(COR_TEXTO_CAMPO_DESABILITADO);
        mainPanel.add(txtArquivo);
        txtArquivo.setBounds(20, currentY, 200, fieldHeight);

        btnVisualizarArquivo.setText("Ver");
        btnVisualizarArquivo.setToolTipText("Visualizar/Baixar o arquivo atual");
        btnVisualizarArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVisualizarArquivo.setEnabled(false); // Desabilitado inicialmente
        btnVisualizarArquivo.addActionListener(this::btnVisualizarArquivoActionPerformed);
        mainPanel.add(btnVisualizarArquivo);
        btnVisualizarArquivo.setBounds(230, currentY, 60, fieldHeight);

        btnSelecionarArquivo.setText("Alterar");
        btnSelecionarArquivo.setToolTipText("Selecionar um novo arquivo para substituir o atual");
        btnSelecionarArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSelecionarArquivo.addActionListener(this::btnSelecionarArquivoActionPerformed);
        mainPanel.add(btnSelecionarArquivo);
        btnSelecionarArquivo.setBounds(300, currentY, 85, fieldHeight);

        btnRemoverArquivo.setText("Remover");
        btnRemoverArquivo.setToolTipText("Desvincular o arquivo atual deste exame");
        btnRemoverArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRemoverArquivo.setBackground(COR_BOTAO_VERMELHO.brighter()); // Vermelho mais claro
        btnRemoverArquivo.setForeground(Color.WHITE);
        btnRemoverArquivo.setEnabled(false); // Desabilitado inicialmente
        btnRemoverArquivo.addActionListener(this::btnRemoverArquivoActionPerformed);
        mainPanel.add(btnRemoverArquivo);
        btnRemoverArquivo.setBounds(395, currentY, 85, fieldHeight);
        currentY += fieldHeight + spacing + 20; // Mais espaço antes dos botões principais

        // Botões de ação
        btnSalvar.setBackground(COR_BOTAO_VERDE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setText("Salvar Alterações");
        btnSalvar.addActionListener(this::btnSalvarActionPerformed);
        mainPanel.add(btnSalvar);
        btnSalvar.setBounds(20, currentY, 160, 40);

        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBackground(COR_BOTAO_CINZA);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);
        mainPanel.add(btnCancelar);
        btnCancelar.setBounds(190, currentY, 100, 40);
        currentY += 40 + 20; // Espaço final

        // Configuração final da janela
        mainPanel.setPreferredSize(new java.awt.Dimension(550, currentY));
        getContentPane().add(mainPanel);
        pack(); // Ajusta o tamanho da janela ao conteúdo
    }

    /**
     * Carrega os dados do exame do banco de dados para os campos do formulário,
     * incluindo o arquivo binário.
     */
    private void carregarDadosExame() {
        String sql = "SELECT nome, tipo, data_realizacao, resultado, "
                + "arquivo_blob, nome_arquivo_original, tipo_arquivo "
                + "FROM tb_Exame WHERE id = ?";

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
                        // Preencher campos de texto e combo
                        txtNome.setText(rs.getString("nome"));
                        cmbTipo.setSelectedItem(rs.getString("tipo"));
                        Date dataRealizacao = rs.getDate("data_realizacao");
                        if (dataRealizacao != null) {
                            txtData.setText(dateFormat.format(dataRealizacao));
                        }
                        txtResultado.setText(rs.getString("resultado"));
                        txtResultado.setCaretPosition(0);

                        // Carregar dados do arquivo BLOB atual
                        Blob blob = rs.getBlob("arquivo_blob");
                        if (blob != null) {
                            this.arquivoBlobAtual = blob.getBytes(1, (int) blob.length());
                            this.nomeOriginalArquivoAtual = rs.getString("nome_arquivo_original");
                            this.tipoArquivoAtual = rs.getString("tipo_arquivo");

                            txtArquivo.setText(this.nomeOriginalArquivoAtual != null ? this.nomeOriginalArquivoAtual : "Arquivo sem nome");
                            txtArquivo.setToolTipText("Arquivo atual: " + this.nomeOriginalArquivoAtual);
                            btnRemoverArquivo.setEnabled(true);
                            btnVisualizarArquivo.setEnabled(true);
                        } else {
                            txtArquivo.setText("(Nenhum arquivo associado)");
                            txtArquivo.setToolTipText(null);
                            btnRemoverArquivo.setEnabled(false);
                            btnVisualizarArquivo.setEnabled(false);
                        }
                        this.removerArquivo = false; // Reseta flag de remoção
                        this.selectedFile = null; // Reseta seleção de novo arquivo

                    } else {
                        showErrorDialog("Erro", "Exame com ID " + exameId + " não encontrado.");
                        this.dispose();
                    }
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao carregar dados do exame: " + e.getMessage());
            e.printStackTrace();
            this.dispose();
        }
    }

    /**
     * Ação do botão Visualizar/Baixar Arquivo: Permite ao usuário ver ou salvar
     * o arquivo atual.
     */
    private void btnVisualizarArquivoActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.arquivoBlobAtual == null || this.nomeOriginalArquivoAtual == null) {
            showWarningDialog("Ação Indisponível", "Não há arquivo válido para visualizar/baixar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Arquivo Atual Como...");
        fileChooser.setSelectedFile(new File(this.nomeOriginalArquivoAtual));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                Files.write(fileToSave.toPath(), this.arquivoBlobAtual);
                showInfoDialog("Download Concluído", "Arquivo salvo com sucesso!");
                // Tenta abrir o arquivo
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(fileToSave);
                    } catch (IOException ex) {
                        System.err.println("Erro ao tentar abrir o arquivo baixado: " + ex.getMessage());
                    }
                }
            } catch (IOException | SecurityException e) {
                showErrorDialog("Erro de Download", "Erro ao salvar o arquivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Ação do botão Selecionar/Alterar Arquivo: Abre JFileChooser.
     */
    private void btnSelecionarArquivoActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecionar Novo Arquivo de Exame (PDF ou XML)");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de Exame (PDF, XML)", "pdf", "xml");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            txtArquivo.setText("Novo: " + selectedFile.getName());
            txtArquivo.setToolTipText("Novo arquivo selecionado: " + selectedFile.getAbsolutePath());
            txtArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Fonte normal
            btnRemoverArquivo.setEnabled(false); // Não pode remover o atual se um novo foi selecionado
            this.removerArquivo = false;
        } else {
            // Se cancelou, volta ao estado anterior (mostrando o arquivo atual ou nenhum)
            if (this.arquivoBlobAtual != null) {
                txtArquivo.setText(this.nomeOriginalArquivoAtual != null ? this.nomeOriginalArquivoAtual : "Arquivo sem nome");
                txtArquivo.setToolTipText("Arquivo atual: " + this.nomeOriginalArquivoAtual);
                btnRemoverArquivo.setEnabled(true);
            } else {
                txtArquivo.setText("(Nenhum arquivo associado)");
                txtArquivo.setToolTipText(null);
                btnRemoverArquivo.setEnabled(false);
            }
            txtArquivo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            this.selectedFile = null;
            this.removerArquivo = false;
        }
    }

    /**
     * Ação do botão Remover Arquivo: Marca o arquivo para remoção.
     */
    private void btnRemoverArquivoActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja desvincular o arquivo atual deste exame?",
                "Confirmar Remoção de Arquivo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.removerArquivo = true;
            this.selectedFile = null; // Cancela seleção de novo arquivo se houver
            txtArquivo.setText("(Arquivo será removido)");
            txtArquivo.setToolTipText("O arquivo atual será desvinculado ao salvar.");
            txtArquivo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            btnRemoverArquivo.setEnabled(false);
            btnVisualizarArquivo.setEnabled(false);
        }
    }

    /**
     * Ação do botão Salvar: Valida os dados, lê o novo arquivo (se houver) e
     * atualiza no BD.
     */
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
        // Validar campos
        String nomeExame = txtNome.getText().trim();
        String dataStr = txtData.getText().trim();

        if (nomeExame.isEmpty()) {
            showWarningDialog("Campo Obrigatório", "O nome do exame é obrigatório.");
            txtNome.requestFocus();
            return;
        }
        if (dataStr.isEmpty()) {
            showWarningDialog("Campo Obrigatório", "A data de realização é obrigatória.");
            txtData.requestFocus();
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

        // Obter dados do formulário
        String tipoExameSelecionado = cmbTipo.getSelectedItem().toString();
        String resultadoObs = txtResultado.getText().trim();

        byte[] arquivoBytesParaSalvar = null;
        String nomeOriginalParaSalvar = null;
        String tipoArquivoParaSalvar = null;
        boolean atualizarArquivo = false;

        // Processar arquivo
        if (this.removerArquivo) {
            // Marcou para remover, então os campos de arquivo serão NULL
            atualizarArquivo = true; // Indica que a coluna do arquivo deve ser atualizada (para NULL)
        } else if (selectedFile != null) {
            // Selecionou um novo arquivo
            try {
                arquivoBytesParaSalvar = Files.readAllBytes(selectedFile.toPath());
                nomeOriginalParaSalvar = selectedFile.getName();
                tipoArquivoParaSalvar = getFileExtension(nomeOriginalParaSalvar);
                atualizarArquivo = true;
            } catch (IOException e) {
                showErrorDialog("Erro de Leitura", "Erro ao ler o novo arquivo selecionado: " + e.getMessage());
                return; // Interrompe o salvamento
            }
        } else {
            // Não selecionou novo arquivo e não marcou para remover, mantém o atual
            // Não precisa fazer nada com as variáveis de arquivo, pois não serão usadas no UPDATE
            atualizarArquivo = false;
        }

        // Atualizar exame no banco de dados
        if (atualizarExame(nomeExame, tipoExameSelecionado, dataRealizacao, resultadoObs,
                arquivoBytesParaSalvar, nomeOriginalParaSalvar, tipoArquivoParaSalvar, atualizarArquivo)) {
            showInfoDialog("Atualização Concluída", "Exame atualizado com sucesso!");
            this.dispose(); // Fecha a tela de edição
            if (parentScreen != null) {
                parentScreen.refreshExamesList(); // Atualiza a tela principal
            }
        }
    }

    /**
     * Atualiza o exame no banco de dados.
     *
     * @return true se a atualização foi bem-sucedida, false caso contrário
     */
    private boolean atualizarExame(String nome, String tipo, Date dataRealizacao, String resultado,
            byte[] arquivoBytes, String nomeOriginal, String tipoArquivo, boolean atualizarArquivo) {

        StringBuilder sqlBuilder = new StringBuilder("UPDATE tb_Exame SET nome = ?, tipo = ?, data_realizacao = ?, resultado = ?");
        if (atualizarArquivo) {
            sqlBuilder.append(", arquivo_blob = ?, nome_arquivo_original = ?, tipo_arquivo = ?");
        }
        sqlBuilder.append(" WHERE id = ?");
        String sql = sqlBuilder.toString();

        try (Connection conn = ConexaoDB.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setString(paramIndex++, nome);
            stmt.setString(paramIndex++, tipo);
            stmt.setDate(paramIndex++, new java.sql.Date(dataRealizacao.getTime()));
            stmt.setString(paramIndex++, resultado);

            if (atualizarArquivo) {
                if (arquivoBytes != null) {
                    stmt.setBytes(paramIndex++, arquivoBytes);
                    stmt.setString(paramIndex++, nomeOriginal);
                    stmt.setString(paramIndex++, tipoArquivo);
                } else { // Remover arquivo
                    stmt.setNull(paramIndex++, java.sql.Types.BLOB);
                    stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                    stmt.setNull(paramIndex++, java.sql.Types.VARCHAR);
                }
            }

            stmt.setInt(paramIndex, exameId); // WHERE id = ?

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao atualizar exame: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ação do botão Cancelar
     */
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // --- Métodos de Acessibilidade --- //
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

        // Ajusta fonte do campo de texto do arquivo e botões relacionados
        if (this.removerArquivo) {
            txtArquivo.setFont(newItalicFont);
        } else if (this.selectedFile != null) {
            txtArquivo.setFont(newButtonFont);
        } else if (this.arquivoBlobAtual != null) {
            txtArquivo.setFont(newButtonFont);
        } else {
            txtArquivo.setFont(newItalicFont);
        }
        btnVisualizarArquivo.setFont(newButtonFont);
        btnSelecionarArquivo.setFont(newButtonFont);
        btnRemoverArquivo.setFont(newButtonFont);

        // Ajusta fonte dos botões principais
        btnSalvar.setFont(new Font(btnSalvar.getFont().getName(), Font.BOLD, fontSize));
        btnCancelar.setFont(new Font(btnCancelar.getFont().getName(), Font.PLAIN, fontSize));

        // Revalida e redesenha o painel
        mainPanel.revalidate();
        mainPanel.repaint();
        pack(); // Reajusta o tamanho da janela à nova fonte
    }

    // --- Métodos Auxiliares --- //
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
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

    // Método main (opcional, para testar a tela isoladamente)
    // public static void main(String args[]) {
    //     java.awt.EventQueue.invokeLater(() -> {
    //         // Para testar, precisaríamos de uma instância mock de Principal ou null
    //         // e um ID de exame válido.
    //         // new EditarExame(null, 1).setVisible(true);
    //     });
    // }
}
