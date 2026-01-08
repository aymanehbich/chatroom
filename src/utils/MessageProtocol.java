package src.utils;

public class MessageProtocol {
    public static final String PROMPT = "PROMPT:";
    public static final String ERROR = "ERROR:";
    public static final String SYSTEM = "SYSTEM:";
    public static final String MESSAGE = "MESSAGE:";
    
    public static String createPrompt(String text) {
        return PROMPT + text;
    }
    
    public static String createError(String text) {
        return ERROR + text;
    }
    
    public static String createSystem(String text) {
        return SYSTEM + text;
    }
    
    public static String createMessage(String username, String text) {
        return MESSAGE + username + ": " + text;
    }
}
