package virtualexams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class ExameDAO {

    public boolean salvar(String nome, String tipo, Date dataRealizacao, String resultado, byte[] arquivoBytes, String nomeOriginal, String tipoArquivo, int pacienteId, int medicoId) throws SQLException {
        String sql = "INSERT INTO tb_Exame (nome, tipo, data_realizacao, resultado, arquivo_blob, nome_arquivo_original, tipo_arquivo, paciente_id, medico_id, data_envio, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";

        try (Connection conn = ConexaoDB.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            stmt.setString(2, tipo);
            stmt.setDate(3, new java.sql.Date(dataRealizacao.getTime()));
            stmt.setString(4, resultado);
            
            if (arquivoBytes != null) {
                stmt.setBytes(5, arquivoBytes);
                stmt.setString(6, nomeOriginal);
                stmt.setString(7, tipoArquivo);
            } else {
                stmt.setNull(5, java.sql.Types.BLOB);
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.VARCHAR);
            }
            
            stmt.setInt(8, pacienteId);
            stmt.setInt(9, medicoId);
            stmt.setString(10, "Pendente");

            return stmt.executeUpdate() > 0;
        }
    }
}