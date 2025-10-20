package utils;

import java.util.UUID;

public class TokenUtils {
    private TokenUtils() {
    }
    
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
