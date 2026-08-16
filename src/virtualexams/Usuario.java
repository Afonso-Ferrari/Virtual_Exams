package virtualexams;


// Classe para gerenciar a sessão do usuario logado no sistema Implementa

public class Usuario {

 
    private static Usuario instance;

    // Informações do usuario logado
    private int userId;
    private String userName;
    private String userType;

    
    // Construtor privado para impedir instanciação externa
     
    private Usuario() {
        // Inicializa com valores padrão 
        userId = 0;
        userName = "";
        userType = "";
    }

     // @return Instancia de UserSession
    
    public static synchronized Usuario getInstance() {
        if (instance == null) {
            instance = new Usuario();
        }
        return instance;
    }

    /**
     * Limpa os dados da sessão (logout)
     */
    public void clearSession() {
        userId = 0;
        userName = "";
        userType = "";
    }

    // Getters e Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

   
    // Verifica se o usuario 
   
    public boolean isMedico() {
        return "Medico".equals(userType);
    }

    /**
     * Verifica se o usuario são paciente
     *
     * @return true se for paciente, false caso contrario
     */
    public boolean isPaciente() {
        return "Paciente".equals(userType);
    }
}
