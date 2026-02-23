/** Clasa pentru gestionarea proceselor de autentificare si inregistrare a utilizatorilor.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.controller;

import com.interventii.management_interventii.model.Client;
import com.interventii.management_interventii.service.AuthService;
import com.interventii.management_interventii.repository.ClientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("clientPin") == null) {
            String clientPin = String.format("%04d", (int)(Math.random() * 10000));
            session.setAttribute("clientPin", clientPin);
        }

        model.addAttribute("displayPin", session.getAttribute("clientPin"));
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        List<String> errors = new ArrayList<>();

        // validare email format
        String emailError = validateEmailFormat(email);
        if (emailError != null) {
            errors.add(emailError);
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/login";
        }

        boolean isEmployee = email.toLowerCase().endsWith("@firma.ro");
        String correctPin = (String) session.getAttribute("clientPin");

        if (isEmployee) {
            if (password.equals(correctPin)) {
                errors.add("Employees cannot use the Client PIN");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/login";
            }
            // parola angajat harcodata
            if (!password.equals("Admin123!")) {
                errors.add("Incorrect employee password");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/login";
            }

            // authService pt a autentifica angajatul
            AuthService.AuthResult result = authService.authenticate(email, password);
            if (result.isSuccess()) {
                session.setAttribute("user", result.getUser());
                session.setAttribute("userName", result.getUser().getNumeComplet());
                session.setAttribute("userType", "employee");
                return "redirect:/dashboard";
            } else {
                errors.add("Authentication failed");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/login";
            }
        } else {
            if (!password.equals(correctPin)) {
                errors.add("Incorrect PIN");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/login";
            }
            var clientOpt = clientRepository.findByEmail(email);
            if (clientOpt.isPresent()) {
                session.setAttribute("client", clientOpt.get());
                session.setAttribute("userType", "client");
                return "redirect:/client/dashboard";
            } else {
                errors.add("Email not registered");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:/login";
            }
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String nume,
                           @RequestParam String prenume,
                           @RequestParam String email,
                           @RequestParam String telefon,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        List<String> errors = new ArrayList<>();

        // validare prenume
        String prenumeError = validateName(prenume, "First Name");
        if (prenumeError != null) {
            errors.add(prenumeError);
        }

        // vf nume
        String numeError = validateName(nume, "Last Name");
        if (numeError != null) {
            errors.add(numeError);
        }

        // vf email
        String emailError = validateEmailFormat(email);
        if (emailError != null) {
            errors.add(emailError);
        } else {
            // vf email companie
            if (email.toLowerCase().endsWith("@firma.ro")) {
                errors.add("Company emails are for employees only");
            }
            // vf duplicat
            if (clientRepository.findByEmail(email).isPresent()) {
                errors.add("Email already exists");
            }
        }

        // vf telefon
        String telefonError = validatePhoneNumber(telefon);
        if (telefonError != null) {
            errors.add(telefonError);
        }

        // daca sunt erori se returneaza toate
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/login";
        }

        // generare ID
        Integer nextId = clientRepository.findMaxId() + 1;

        // salvare client
        Client c = new Client();
        c.setId(nextId);
        c.setNume(nume);
        c.setPrenume(prenume);
        c.setEmail(email);
        c.setTelefon(telefon);
        clientRepository.save(c);

        // auto login
        session.setAttribute("client", c);
        session.setAttribute("userType", "client");

        List<String> success = new ArrayList<>();
        success.add("Welcome! Your account has been created successfully");
        redirectAttributes.addFlashAttribute("errors", success);

        return "redirect:/client/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /*
      nume, prenume: doar litere, minim 2 caractere
      return mesaj de eroare sau null dacă e valid
     */
    private String validateName(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            return fieldName + " is required";
        }

        String trimmed = text.trim();

        if (trimmed.length() < 2) {
            return fieldName + " must have at least 2 letters";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetter(c)) {
                return fieldName + " must contain only letters";
            }
        }

        return null;
    }

    /*
      nr de telefon:
      - trebuie sa inceapa cu 07
      - trebuie sa aiba exact 10 cifre
      - doar cifre, fara alte caractere
      return mesaj de eroare sau null dacă e valid
     */
    private String validatePhoneNumber(String tel) {
        if (tel == null || tel.trim().isEmpty()) {
            return "Phone number is required";
        }

        String trimmed = tel.trim();

        if (trimmed.length() != 10) {
            return "Phone must have exactly 10 digits";
        }

        if (!trimmed.startsWith("07")) {
            return "Phone must start with 07";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isDigit(c)) {
                return "Phone must contain only digits";
            }
        }

        return null;
    }

    /*
      email:
      - cel putin 1 litera inainte de @
      - contine @
      - domeniu intre @ si .
      - trb sa se termine cu extensie (ex: .ro, .com)
      return mesaj de eroare sau null daca e valid
     */
    private String validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }

        String trimmed = email.trim();

        if (!trimmed.contains("@")) {
            return "Email must contain @";
        }

        int atPos = trimmed.indexOf("@");

        if (atPos == 0) {
            return "Email must have at least one character before @";
        }

        // cel putin o litera inainte de @
        String beforeAt = trimmed.substring(0, atPos);
        boolean hasLetter = false;
        for (int i = 0; i < beforeAt.length(); i++) {
            char c = beforeAt.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
                break;
            }
        }

        if (!hasLetter) {
            return "Email must have at least one letter before @";
        }

        if (!trimmed.contains(".")) {
            return "Email must contain a domain extension (ex: .com, .ro)";
        }

        int lastDotPos = trimmed.lastIndexOf(".");

        if (lastDotPos <= atPos + 1) {
            return "Email must have a domain between @ and . (ex: @gmail.com)";
        }

        if (trimmed.length() - lastDotPos < 2) {
            return "Email must end with an extension (ex: .com, .ro)";
        }

        return null;
    }
}