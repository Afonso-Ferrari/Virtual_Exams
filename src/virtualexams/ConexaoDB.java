package virtualexams;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Classe para conexão com o banco de dados MySQL
 */
public class ConexaoDB {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bd_virtualexams";
    private static final String USER = "root";
    private static final String PASSWORD = "sua senha";

    // Retorna uma conexão com o banco de dados MySQL
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            return conn;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver MySQL não encontrado: " + e.getMessage(), "Erro de Driver", 0);
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage(), "Erro de Conexão", 0);
            return null;
        }
    }

    // Testa a conexão com o banco de dados
     
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = ConexaoDB.getConnection();
            boolean bl = conn != null;
            return bl;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sQLException) {
                }
            }
        }
    }
}