/** Clasa pentru gestionarea logicii de autentificare a angajaților.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.service;

import com.interventii.management_interventii.model.User;
import com.interventii.management_interventii.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private static final String UNIVERSAL_PASSWORD = "Admin123!";

    public AuthResult authenticate(String email, String password) {
        // vf dacă parola este cea universala
        if (!password.equals(UNIVERSAL_PASSWORD)) {
            return new AuthResult(false, "Incorrect employee password!", null);
        }

        // cauta angajatul email
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return new AuthResult(false, "Email not registered in the system. Only employees can access.", null);
        }

        return new AuthResult(true, "Authentication successful", user);
    }

    public static class AuthResult {
        private boolean success;
        private String message;
        private User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}