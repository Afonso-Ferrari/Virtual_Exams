package virtualexams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean registrar(String nome, String email, String senhaPlana, String cpf, String tipoUsuario, String especialidade, String crm) throws SQLException {
        String tabela = "Médico".equals(tipoUsuario) ? "tb_Medico" : "tb_Paciente";
        String sql;
        
        if ("Médico".equals(tipoUsuario)) {
            sql = "INSERT INTO " + tabela + " (nome, email, senha, cpf, especialidade, crm) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO " + tabela + " (nome, email, senha, cpf) VALUES (?, ?, ?, ?)";
        }

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, SegurancaUtil.gerarHash(senhaPlana)); // Criptografa a senha
            stmt.setString(4, cpf);

            if ("Médico".equals(tipoUsuario)) {
                stmt.setString(5, especialidade);
                stmt.setString(6, crm);
            }

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean autenticar(String email, String senhaPlana, String tipoUsuario) throws SQLException {
        String tabela = "Médico".equals(tipoUsuario) ? "tb_Medico" : "tb_Paciente";
        String sql = "SELECT id, nome FROM " + tabela + " WHERE email = ? AND senha = ?";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, SegurancaUtil.gerarHash(senhaPlana)); // Compara o Hash

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario sessao = Usuario.getInstance();
                    sessao.setUserId(rs.getInt("id"));
                    sessao.setUserName(rs.getString("nome"));
                    sessao.setUserType(tipoUsuario);
                    return true;
                }
                return false;
            }
        }
    }
}