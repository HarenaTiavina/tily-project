package tily.mg.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitaire pour générer des hash BCrypt pour les mots de passe
 */
public class BCryptGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Admin@Tily2024!";
        String hash = encoder.encode(password);
        System.out.println("Mot de passe: " + password);
        System.out.println("Hash BCrypt: " + hash);
    }
}

