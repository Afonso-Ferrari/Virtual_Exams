package virtualexams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SegurancaUtil {
    
    // Transforma a senha em texto plano num Hash ilegível de segurança
    public static String gerarHash(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
}