package virtualexams;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DetalhesPacienteMedico extends JDialog {

    private final int pacienteId;
    private final Principal parentFrame; // Referência ao frame principal para refresh
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Color COR_AZUL_LOGO = new Color(60, 80, 89);
    private final Color COR_FUNDO_PAINEL_CLARO = new Color(245, 245, 245);
    private final Color COR_BOTAO_VERMELHO = new Color(200, 80, 80);

    // Componentes da UI
    private JLabel lblNomePaciente;
    private JLabel lblDataNascimento;
    private JLabel lblCpf;
    private JLabel lblTelefone;
    private JLabel lblEmail;
    private JTable tblExamesPaciente;
    private DefaultTableModel tblExamesModel;
    private JButton btnVerDetalhesExame;
    private JButton btnAbrirArquivoExame;
    private JButton btnEditarExame;
    private JButton btnExcluirExame;
    private JButton btnFechar;

    // Constantes para colunas da tabela de exames (ajustadas para esta tela)
    private static final int COL_EXAME_ID = 0;
    private static final int COL_EXAME_NOME = 1;
    private static final int COL_EXAME_TIPO = 2;
    private static final int COL_EXAME_DATA = 3;
    private static final int COL_EXAME_MEDICO = 4;
    private static final int COL_EXAME_STATUS = 5;
    private static final int COL_EXAME_HAS_FILE = 6; // Nova coluna (oculta)
    private static final int COL_EXAME_FILENAME = 7; // Nova coluna (oculta)

    // Construtor modificado para receber Principal
    public DetalhesPacienteMedico(Principal parent, int pacienteId) {
        super(parent, "Detalhes do Paciente", true); 
        this.parentFrame = parent;
        this.pacienteId = pacienteId;
        initComponents();
        loadPacienteData();
        loadPacienteExames();
        setLocationRelativeTo(parent);
        pack();
        // Ajusta visibilidade dos botões de edição/exclusão baseado no tipo de usuário
        adjustButtonVisibility();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(COR_FUNDO_PAINEL_CLARO);

        // --- Painel de Dados do Paciente ---
        JPanel panelDados = new JPanel();
        panelDados.setLayout(new BoxLayout(panelDados, BoxLayout.Y_AXIS));
        panelDados.setBackground(COR_FUNDO_PAINEL_CLARO);
        panelDados.setBorder(BorderFactory.createTitledBorder("Dados Pessoais"));

        lblNomePaciente = createInfoLabel("Nome Completo: ");
        lblDataNascimento = createInfoLabel("Data de Nascimento: ");
        lblCpf = createInfoLabel("CPF: ");
        lblTelefone = createInfoLabel("Telefone: ");
        lblEmail = createInfoLabel("Email: ");

        panelDados.add(lblNomePaciente);
        panelDados.add(Box.createRigidArea(new Dimension(0, 5)));
        panelDados.add(lblDataNascimento);
        panelDados.add(Box.createRigidArea(new Dimension(0, 5)));
        panelDados.add(lblCpf);
        panelDados.add(Box.createRigidArea(new Dimension(0, 5)));
        panelDados.add(lblTelefone);
        panelDados.add(Box.createRigidArea(new Dimension(0, 5)));
        panelDados.add(lblEmail);

        mainPanel.add(panelDados, BorderLayout.NORTH);

        // --- Painel de Exames ---
        JPanel panelExames = new JPanel(new BorderLayout(0, 10)); // Adicionado espaço vertical
        panelExames.setBackground(COR_FUNDO_PAINEL_CLARO);
        panelExames.setBorder(BorderFactory.createTitledBorder("Histórico de Exames"));

        // Modelo da tabela atualizado para incluir colunas ocultas
        tblExamesModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Exame", "Nome Exame", "Tipo", "Data", "Médico Solicitante", "Status", "TemArquivo", "NomeArquivo"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                 if (columnIndex == COL_EXAME_HAS_FILE) return Boolean.class;
                 return super.getColumnClass(columnIndex);
            }
        };
        tblExamesPaciente = new JTable(tblExamesModel);
        tblExamesPaciente.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblExamesPaciente.setAutoCreateRowSorter(true);
        tblExamesPaciente.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblExamesPaciente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblExamesPaciente.setRowHeight(20);

        // Ocultar colunas ID, TemArquivo e NomeArquivo
        hideTableColumn(tblExamesPaciente, COL_EXAME_ID);
        hideTableColumn(tblExamesPaciente, COL_EXAME_HAS_FILE);
        hideTableColumn(tblExamesPaciente, COL_EXAME_FILENAME);

        JScrollPane scrollExames = new JScrollPane(tblExamesPaciente);
        scrollExames.setPreferredSize(new Dimension(650, 250));
        panelExames.add(scrollExames, BorderLayout.CENTER);

        // --- Painel de Botões de Ação de Exames ---
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionButtonPanel.setBackground(COR_FUNDO_PAINEL_CLARO);

        btnVerDetalhesExame = new JButton("Visualizar Detalhes");
        styleSecondaryButton(btnVerDetalhesExame);
        btnVerDetalhesExame.addActionListener(this::btnVerExameActionPerformed);
        actionButtonPanel.add(btnVerDetalhesExame);

        btnAbrirArquivoExame = new JButton("Abrir/Baixar Arquivo");
        styleSecondaryButton(btnAbrirArquivoExame);
        btnAbrirArquivoExame.addActionListener(this::btnAbrirArquivoActionPerformed);
        actionButtonPanel.add(btnAbrirArquivoExame);

        btnEditarExame = new JButton("Editar");
        styleSecondaryButton(btnEditarExame);
        btnEditarExame.addActionListener(this::btnEditarExameActionPerformed);
        actionButtonPanel.add(btnEditarExame);

        btnExcluirExame = new JButton("Excluir");
        btnExcluirExame.setBackground(COR_BOTAO_VERMELHO);
        btnExcluirExame.setForeground(Color.WHITE);
        btnExcluirExame.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExcluirExame.addActionListener(this::btnExcluirExameActionPerformed);
        actionButtonPanel.add(btnExcluirExame);

        panelExames.add(actionButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(panelExames, BorderLayout.CENTER);

        // --- Botão Fechar (Painel Sul Principal) ---
        btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(COR_FUNDO_PAINEL_CLARO);
        southPanel.add(btnFechar);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(700, 550)); // Ajuste tamanho mínimo
    }

    private void hideTableColumn(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        table.getColumnModel().getColumn(columnIndex).setWidth(0);
        table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.BLACK);
        // Define um tamanho preferencial para consistência
        button.setPreferredSize(new Dimension(150, 30));
    }

    private JLabel createInfoLabel(String prefix) {
        JLabel label = new JLabel(prefix);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return label;
    }

    private void loadPacienteData() {
        String sql = "SELECT nome, data_nascimento, cpf, telefone, email FROM tb_Paciente WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    lblNomePaciente.setText("Nome Completo: " + rs.getString("nome"));
                    java.sql.Date dataNasc = rs.getDate("data_nascimento");
                    lblDataNascimento.setText("Data de Nascimento: " + (dataNasc != null ? dateFormat.format(dataNasc) : "Não informado"));
                    lblCpf.setText("CPF: " + formatCpf(rs.getString("cpf")));
                    lblTelefone.setText("Telefone: " + (rs.getString("telefone") != null ? rs.getString("telefone") : "Não informado"));
                    lblEmail.setText("Email: " + rs.getString("email"));
                } else {
                    showErrorDialog("Erro", "Paciente não encontrado.");
                    dispose();
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Dados do Paciente", "Não foi possível carregar os dados do paciente: " + e.getMessage());
            e.printStackTrace();
            dispose();
        }
    }

    private void loadPacienteExames() {
        tblExamesModel.setRowCount(0); // Limpa tabela
        // SQL atualizado para incluir has_file e nome_arquivo_original
        String sql = "SELECT e.id, e.nome, e.tipo, e.data_realizacao, m.nome as medico_nome, e.status, "
                   + "(e.arquivo_blob IS NOT NULL) as has_file, e.nome_arquivo_original "
                   + "FROM tb_Exame e JOIN tb_Medico m ON e.medico_id = m.id "
                   + "WHERE e.paciente_id = ? ORDER BY e.data_realizacao DESC";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.pacienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tblExamesModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo"),
                        rs.getDate("data_realizacao") != null ? dateFormat.format(rs.getDate("data_realizacao")) : "",
                        rs.getString("medico_nome"),
                        rs.getString("status"),
                        rs.getBoolean("has_file"), // Adicionado
                        rs.getString("nome_arquivo_original") // Adicionado
                    });
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Erro ao Carregar Exames", "Não foi possível carregar os exames do paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        try {
            MaskFormatter mf = new MaskFormatter("###.###.###-##");
            mf.setValueContainsLiteralCharacters(false);
            return mf.valueToString(cpf);
        } catch (ParseException e) {
            System.err.println("Erro ao formatar CPF: " + e.getMessage());
            return cpf;
        }
    }

    // --- Ações dos Botões de Exame (Adaptadas de Principal.java) ---

    private void btnVerExameActionPerformed(ActionEvent e) {
        int selectedRow = tblExamesPaciente.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = tblExamesPaciente.convertRowIndexToModel(selectedRow);
            int exameId = (int) tblExamesModel.getValueAt(viewRow, COL_EXAME_ID);
            // Usa 'this' como parent para o JDialog VerExame
            VerExame verExame = new VerExame(parentFrame, exameId);
            verExame.setVisible(true);
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para visualizar.");
        }
    }

    private void btnAbrirArquivoActionPerformed(ActionEvent e) {
        int selectedRow = tblExamesPaciente.getSelectedRow();
        if (selectedRow >= 0) {
            int viewRow = tblExamesPaciente.convertRowIndexToModel(selectedRow);
            boolean hasFile = (boolean) tblExamesModel.getValueAt(viewRow, COL_EXAME_HAS_FILE);
            if (hasFile) {
                int exameId = (int) tblExamesModel.getValueAt(viewRow, COL_EXAME_ID);
                String originalFileName = (String) tblExamesModel.getValueAt(viewRow, COL_EXAME_FILENAME);
                downloadAndOpenFile(exameId, originalFileName);
            } else {
                showInfoDialog("Sem Arquivo", "Este exame não possui um arquivo associado.");
            }
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para abrir o arquivo.");
        }
    }

    private void btnEditarExameActionPerformed(ActionEvent e) {
        int selectedRow = tblExamesPaciente.getSelectedRow();
        if (selectedRow >= 0) {
            // Verifica se o usuário logado é Médico
            if (!Usuario.getInstance().getUserType().equals("Médico")) {
                showWarningDialog("Acesso Negado", "Apenas médicos podem editar exames.");
                return;
            }
            int viewRow = tblExamesPaciente.convertRowIndexToModel(selectedRow);
            int exameId = (int) tblExamesModel.getValueAt(viewRow, COL_EXAME_ID);
            // Usa 'this' como parent para o JDialog EditarExame
            EditarExame editarExame = new EditarExame(parentFrame, exameId);
            editarExame.setVisible(true);
            // Atualiza a lista DESTA janela após fechar a edição
            loadPacienteExames();
            // Atualiza a lista na janela Principal
            if (parentFrame != null) {
                parentFrame.refreshExamesList();
            }
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para editar.");
        }
    }

    private void btnExcluirExameActionPerformed(ActionEvent e) {
        int selectedRow = tblExamesPaciente.getSelectedRow();
        if (selectedRow >= 0) {
            // Verifica se o usuário logado é Médico
            if (!Usuario.getInstance().getUserType().equals("Médico")) {
                showWarningDialog("Acesso Negado", "Apenas médicos podem excluir exames.");
                return;
            }
            int viewRow = tblExamesPaciente.convertRowIndexToModel(selectedRow);
            int exameId = (int) tblExamesModel.getValueAt(viewRow, COL_EXAME_ID);
            String exameNome = (String) tblExamesModel.getValueAt(viewRow, COL_EXAME_NOME);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir o exame '" + exameNome + "' (ID: " + exameId + ")?\nEsta ação não pode ser desfeita.",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (excluirExame(exameId)) {
                    // Atualiza a lista DESTA janela
                    loadPacienteExames();
                    // Atualiza a lista na janela Principal
                    if (parentFrame != null) {
                        parentFrame.refreshExamesList();
                    }
                }
            }
        } else {
            showWarningDialog("Nenhum Exame Selecionado", "Por favor, selecione um exame na lista para excluir.");
        }
    }

    // --- Métodos Auxiliares (Copiados/Adaptados de Principal.java) ---

    private void adjustButtonVisibility() {
        boolean isMedico = Usuario.getInstance().getUserType().equals("Médico");
        btnEditarExame.setVisible(isMedico);
        btnExcluirExame.setVisible(isMedico);
    }

    private boolean excluirExame(int exameId) {
        String sql = "DELETE FROM tb_Exame WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, exameId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                showInfoDialog("Exclusão Bem-sucedida", "O exame foi excluído com sucesso.");
                return true;
            } else {
                showErrorDialog("Erro na Exclusão", "Nenhum exame encontrado com o ID fornecido ou erro ao excluir.");
                return false;
            }
        } catch (SQLException ex) {
            showErrorDialog("Erro de Banco de Dados", "Erro ao excluir o exame: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private void downloadAndOpenFile(int exameId, String originalFileName) {
        String sql = "SELECT arquivo_blob FROM tb_Exame WHERE id = ?";
        File tempFile = null;

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, exameId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("arquivo_blob");
                    if (blob != null) {
                        InputStream inputStream = blob.getBinaryStream();
                        // Cria um arquivo temporário com o nome original
                        Path tempDir = Files.createTempDirectory("virtualexams_");
                        tempFile = new File(tempDir.toFile(), originalFileName);

                        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                        inputStream.close();

                        // Tenta abrir o arquivo com o programa padrão do sistema
                        if (Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().open(tempFile);
                                // Opcional: Agendar exclusão do arquivo temporário na saída
                                tempFile.deleteOnExit();
                                tempDir.toFile().deleteOnExit(); // Tenta excluir o diretório também
                            } catch (IOException ex) {
                                showErrorDialog("Erro ao Abrir Arquivo", "Não foi possível abrir o arquivo automaticamente: " + ex.getMessage() +
                                        "\nO arquivo foi salvo em: " + tempFile.getAbsolutePath());
                                ex.printStackTrace();
                            }
                        } else {
                            showInfoDialog("Arquivo Salvo", "O arquivo foi salvo em: " + tempFile.getAbsolutePath() +
                                    "\nNão foi possível abrir automaticamente.");
                        }
                    } else {
                        showErrorDialog("Erro", "Arquivo não encontrado no banco de dados para este exame.");
                    }
                } else {
                    showErrorDialog("Erro", "Exame não encontrado.");
                }
            }
        } catch (SQLException | IOException e) {
            showErrorDialog("Erro ao Baixar/Abrir Arquivo", "Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
            // Tenta limpar o arquivo temporário em caso de erro
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    // Métodos de diálogo (copiados de Principal para autossuficiência)
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

