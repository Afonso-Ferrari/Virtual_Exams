package virtualexams;

public class ValidacoesUtil {
    
    // Verifica se algum dos campos passados está vazio
    public static boolean isCampoVazio(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Validação simples de email
    public static boolean isEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}