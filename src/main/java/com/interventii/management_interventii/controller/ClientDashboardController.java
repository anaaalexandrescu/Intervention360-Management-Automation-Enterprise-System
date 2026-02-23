/** Clasa pentru gestionarea interfetei si actiunilor disponibile clientilor autentificati.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.controller;

import com.interventii.management_interventii.model.Client;
import com.interventii.management_interventii.model.Interventie;
import com.interventii.management_interventii.model.Serviciu;
import com.interventii.management_interventii.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client")
public class ClientDashboardController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InterventieRepository interventieRepository;

    @Autowired
    private ServiciuRepository serviciuRepository;

    @Autowired
    private InterventieMaterialeRepository interventieMaterialeRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private boolean checkClientAuth(HttpSession session) {
        return session.getAttribute("client") != null;
    }

    @GetMapping("/dashboard")
    public String clientDashboard(HttpSession session, Model model) {
        if (!checkClientAuth(session)) return "redirect:/login";

        Client client = (Client) session.getAttribute("client");
        List<Interventie> interventii = interventieRepository.findByClientId(client.getId());

        model.addAttribute("client", client);
        model.addAttribute("interventii", interventii);
        model.addAttribute("servicii", serviciuRepository.findAll());

        return "client_dashboard";
    }

    @PostMapping("/update-profile")
    @Transactional
    public String updateProfile(@RequestParam Integer clientId,
                                @RequestParam String nume, @RequestParam String prenume,
                                @RequestParam String email, @RequestParam String telefon,
                                @RequestParam(required = false) String strada,
                                @RequestParam(required = false) String numar,
                                @RequestParam(required = false) String oras,
                                @RequestParam(required = false) String judet,
                                @RequestParam(required = false) String sex,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkClientAuth(session)) return "redirect:/login";

        List<String> errors = new java.util.ArrayList<>();

        // vf nume
        String numeError = validateName(nume, "Last Name");
        if (numeError != null) {
            errors.add(numeError);
        }

        // vf prenume
        String prenumeError = validateName(prenume, "First Name");
        if (prenumeError != null) {
            errors.add(prenumeError);
        }

        // vf telefon
        String telefonError = validatePhoneNumber(telefon);
        if (telefonError != null) {
            errors.add(telefonError);
        }

        // vf oras (daca e completat)
        if (oras != null && !oras.trim().isEmpty()) {
            String orasError = validateCity(oras);
            if (orasError != null) {
                errors.add(orasError);
            }
        }

        // vf judet (daca e completat)
        if (judet != null && !judet.trim().isEmpty()) {
            String judetError = validateCounty(judet);
            if (judetError != null) {
                errors.add(judetError);
            }
        }

        // vf strada (daca e completata)
        if (strada != null && !strada.trim().isEmpty()) {
            String stradaError = validateStreet(strada);
            if (stradaError != null) {
                errors.add(stradaError);
            }
        }

        // vf numar (daca e completat)
        if (numar != null && !numar.trim().isEmpty()) {
            String numarError = validateNumber(numar);
            if (numarError != null) {
                errors.add(numarError);
            }
        }

        // daca sunt erori, le returnam toate
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/client/dashboard";
        }

        try {
            Client client = clientRepository.findById(clientId).orElseThrow();
            client.setNume(nume); client.setPrenume(prenume);
            client.setEmail(email); client.setTelefon(telefon);
            client.setStrada(strada); client.setNumar(numar);
            client.setOras(oras); client.setJudet(judet);
            client.setSex(sex);

            clientRepository.save(client);
            session.setAttribute("client", client);

            List<String> success = new java.util.ArrayList<>();
            success.add("Profile updated successfully");
            redirectAttributes.addFlashAttribute("errors", success);
        } catch (Exception e) {
            errors.add("Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errors", errors);
        }
        return "redirect:/client/dashboard";
    }

    @GetMapping("/edit-materials/{id}")
    public String editMaterials(@PathVariable Integer id, HttpSession session, Model model) {
        if (!checkClientAuth(session)) return "redirect:/login";

        Interventie interventie = interventieRepository.findById(id).orElse(null);
        if (interventie == null || !interventie.getStatus().equals("Programata")) {
            return "redirect:/client/dashboard";
        }

        model.addAttribute("interventie", interventie);
        model.addAttribute("materialeCurente", interventieMaterialeRepository.findMaterialeByInterventieId(id));
        model.addAttribute("materialeDisponibile", materialRepository.findAll());

        return "edit_materials";
    }

    @PostMapping("/save-materials")
    @Transactional
    public String saveMaterials(@RequestParam Integer interventieId,
                                @RequestParam(required = false) List<String> materialIds,
                                @RequestParam(required = false) List<String> cantitati,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkClientAuth(session)) return "redirect:/login";

        try {
            interventieMaterialeRepository.deleteByInterventieId(interventieId);

            if (materialIds != null && cantitati != null && materialIds.size() == cantitati.size()) {
                for (int i = 0; i < materialIds.size(); i++) {
                    try {
                        String materialIdStr = materialIds.get(i);
                        String cantitateStr = cantitati.get(i);

                        if (materialIdStr != null && !materialIdStr.trim().isEmpty() &&
                                cantitateStr != null && !cantitateStr.trim().isEmpty()) {

                            Integer materialId = parseToInteger(materialIdStr);
                            Integer cantitate = parseToInteger(cantitateStr);

                            if (cantitate > 0) {
                                interventieMaterialeRepository.insertMaterial(interventieId, materialId, cantitate);
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Format invalid pentru materialul " + i + ": " + e.getMessage());
                    }
                }
            }
            redirectAttributes.addFlashAttribute("success", "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare la salvare: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/client/dashboard";
    }

    @GetMapping("/invoice/{id}")
    public String viewInvoice(@PathVariable Integer id, HttpSession session, Model model) {
        if (!checkClientAuth(session)) return "redirect:/login";

        try {
            Interventie interventie = interventieRepository.findById(id).orElseThrow();
            Client client = clientRepository.findById(interventie.getClientId()).orElseThrow();
            Serviciu serviciu = serviciuRepository.findById(interventie.getServiciuId()).orElseThrow();
            List<Map<String, Object>> materiale = interventieMaterialeRepository.findMaterialeByInterventieId(id);

            double costMateriale = 0.0;
            for (Map<String, Object> m : materiale) {
                costMateriale += ((Number) m.get("subtotal")).doubleValue();
            }

            double totalGeneral = serviciu.getPretStandard() + costMateriale;

            model.addAttribute("interventie", interventie);
            model.addAttribute("client", client);
            model.addAttribute("serviciu", serviciu);
            model.addAttribute("materiale", materiale);
            model.addAttribute("costMateriale", costMateriale);
            model.addAttribute("totalGeneral", totalGeneral);

            return "invoice";
        } catch (Exception e) {
            return "redirect:/client/dashboard";
        }
    }

    @GetMapping("/request-intervention")
    public String requestInterventionForm(HttpSession session, Model model) {
        if (!checkClientAuth(session)) return "redirect:/login";

        Client client = (Client) session.getAttribute("client");
        model.addAttribute("client", client);
        model.addAttribute("servicii", serviciuRepository.findAll());
        model.addAttribute("materiale", materialRepository.findAll());

        return "client_request_intervention";
    }

    @PostMapping("/request-intervention")
    @Transactional
    public String requestIntervention(@RequestParam Integer serviciuId,
                                      @RequestParam String data,
                                      @RequestParam(required = false) String adresa,
                                      @RequestParam(required = false) String descriere,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (!checkClientAuth(session)) return "redirect:/login";

        try {
            Client client = (Client) session.getAttribute("client");
            Integer nextId = interventieRepository.findMaxId() + 1;
            LocalDateTime dataInterventie = LocalDateTime.parse(data);

            String interventionAddress = (adresa != null && !adresa.trim().isEmpty())
                    ? adresa
                    : (client.getStrada() + " " + client.getNumar() + ", " + client.getOras());

            interventieRepository.insertWithFullDetails(
                    nextId, client.getId(), serviciuId, dataInterventie,
                    interventionAddress, "Programata", null, descriere
            );

            redirectAttributes.addFlashAttribute("success", "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/client/dashboard";
    }

    @PostMapping("/schedule-intervention")
    @Transactional
    public String scheduleIntervention(@RequestParam Integer serviciuId,
                                       @RequestParam String data,
                                       @RequestParam String adresa,
                                       @RequestParam(required = false) String descriere,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!checkClientAuth(session)) return "redirect:/login";

        try {
            Client client = (Client) session.getAttribute("client");
            Integer nextId = interventieRepository.findMaxId() + 1;
            LocalDateTime dataInterventie = LocalDateTime.parse(data);

            interventieRepository.insertWithFullDetails(
                    nextId, client.getId(), serviciuId, dataInterventie,
                    adresa, "Programata", null, descriere
            );

            redirectAttributes.addFlashAttribute("success", "Intervenție programata cu succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/client/dashboard";
    }

    private String validateName(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            return fieldName + " is required";
        }

        String trimmed = text.trim();

        if (trimmed.length() < 2) {
            return fieldName + " must have at least 2 characters";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '-') {
                return fieldName + " must contain only letters, spaces, or hyphens";
            }
        }

        return null;
    }

    /*
      validare telefon:
      - trebuie sa inceapa cu 07
      - trebuie sa aiba exact 10 cifre
      - doar cifre
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
      validare oras:
      - minim 2 caractere
      - doar litere, spatii si cratima
      - nu poate incepe sau termina cu spatiu/cratima
      - nu poate contine doar spatii
     */
    private String validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return null;
        }

        String trimmed = city.trim();

        if (trimmed.length() < 2) {
            return "City must have at least 2 characters";
        }

        boolean hasLetters = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isLetter(c)) {
                hasLetters = true;
                break;
            }
        }

        if (!hasLetters) {
            return "City must contain at least one letter";
        }

        if (trimmed.startsWith("-") || trimmed.endsWith("-")) {
            return "City cannot start or end with a hyphen";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '-') {
                return "City must contain only letters, spaces, or hyphens";
            }
        }

        if (trimmed.contains("  ")) {
            return "City cannot contain double spaces";
        }

        if (trimmed.contains("--")) {
            return "City cannot contain double hyphens";
        }

        return null;
    }

    /*
      validare judet:
      - minim 2 caractere
      - doar litere, spatii si cratima
      - nu poate contine doar spatii
     */
    private String validateCounty(String county) {
        if (county == null || county.trim().isEmpty()) {
            return null;
        }

        String trimmed = county.trim();

        if (trimmed.length() < 2) {
            return "County must have at least 2 characters";
        }

        boolean hasLetters = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isLetter(c)) {
                hasLetters = true;
                break;
            }
        }

        if (!hasLetters) {
            return "County must contain at least one letter";
        }

        if (trimmed.startsWith("-") || trimmed.endsWith("-")) {
            return "County cannot start or end with a hyphen";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetter(c) && c != ' ' && c != '-') {
                return "County must contain only letters, spaces, or hyphens";
            }
        }

        if (trimmed.contains("  ")) {
            return "County cannot contain double spaces";
        }

        if (trimmed.contains("--")) {
            return "County cannot contain double hyphens";
        }

        return null;
    }

    /*
      validare strada:
      - minim 3 caractere
      - trebuie sa contina cel putin o litera
      - poate contine litere, cifre, spatii, cratima si punct
      - nu poate fi doar numere
     */
    private String validateStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            return null;
        }

        String trimmed = street.trim();

        if (trimmed.length() < 3) {
            return "Street must have at least 3 characters";
        }

        boolean hasLetters = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isLetter(c)) {
                hasLetters = true;
                break;
            }
        }

        if (!hasLetters) {
            return "Street must contain at least one letter";
        }

        if (trimmed.startsWith("-") || trimmed.endsWith("-") ||
                trimmed.startsWith(".") || trimmed.endsWith(".")) {
            return "Street cannot start or end with a hyphen or period";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetter(c) && !Character.isDigit(c) &&
                    c != ' ' && c != '-' && c != '.') {
                return "Street must contain only letters, numbers, spaces, hyphens, or periods";
            }
        }

        if (trimmed.contains("  ")) {
            return "Street cannot contain double spaces";
        }

        return null;
    }

    /*
      validare numar:
      - minim 1 caracter
      - trebuie sa contina cel putin o cifra
      - poate contine cifre, litere (pentru bloc/scara), spatii, cratima si slash
      - exemple valide: "10", "12A", "5-7", "10 Bl.3", "15/2"
     */
    private String validateNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            return null;
        }

        String trimmed = number.trim();

        if (trimmed.length() < 1) {
            return "Number must have at least 1 character";
        }

        boolean hasDigits = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isDigit(c)) {
                hasDigits = true;
                break;
            }
        }

        if (!hasDigits) {
            return "Number must contain at least one digit";
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != ' ' && c != '-' && c != '/' && c != '.') {
                return "Number can only contain letters, digits, spaces, hyphens, slashes, or periods";
            }
        }

        boolean hasAlphanumeric = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                hasAlphanumeric = true;
                break;
            }
        }

        if (!hasAlphanumeric) {
            return "Number must contain letters or digits";
        }

        if (trimmed.contains("  ")) {
            return "Number cannot contain double spaces";
        }

        return null;
    }

    private Integer parseToInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return (int) Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}