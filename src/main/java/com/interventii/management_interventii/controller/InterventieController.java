/** Clasa pentru gestionarea fluxului de lucru al interventiilor de catre angajati.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.controller;

import com.interventii.management_interventii.model.Interventie;
import com.interventii.management_interventii.model.User;
import com.interventii.management_interventii.model.Client;
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
public class InterventieController {

    @Autowired
    private InterventieRepository interventieRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ServiciuRepository serviciuRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private InterventieMaterialeRepository interventieMaterialeRepository;

    private boolean checkAuth(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!checkAuth(session)) {
            return "redirect:/login";
        }

        User user = (User) session.getAttribute("user");
        model.addAttribute("userName", user.getNumeComplet());
        model.addAttribute("userCalificare", user.getCalificare());

        model.addAttribute("programate", interventieRepository.findByStatus("Programata"));
        model.addAttribute("inDesfasurare", interventieRepository.findByStatus("In desfasurare"));
        model.addAttribute("finalizate", interventieRepository.findByStatus("Finalizata"));

        return "dashboard";
    }

    // search
    @PostMapping("/search-intervention")
    public String searchIntervention(@RequestParam String interventionId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!checkAuth(session)) return "redirect:/login";

        List<String> errors = new java.util.ArrayList<>();

        // vf id gol
        if (interventionId == null || interventionId.trim().isEmpty()) {
            errors.add("Please enter an Intervention ID");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/dashboard";
        }

        String trimmedId = interventionId.trim();

        // vf id contine doar cifre
        boolean isNumeric = true;
        for (int i = 0; i < trimmedId.length(); i++) {
            char c = trimmedId.charAt(i);
            if (!Character.isDigit(c)) {
                isNumeric = false;
                break;
            }
        }

        if (!isNumeric) {
            errors.add("Intervention ID must contain only numbers");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/dashboard";
        }

        // String -> Integer
        Integer id;
        try {
            id = Integer.parseInt(trimmedId);
        } catch (NumberFormatException e) {
            errors.add("Invalid Intervention ID format");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/dashboard";
        }

        Interventie interventie = null;
        List<Interventie> toateInterventiile = interventieRepository.findAll();

        for (Interventie i : toateInterventiile) {
            if (i.getId().equals(id)) {
                interventie = i;
                break;
            }
        }

        if (interventie == null) {
            errors.add("Intervention with ID " + id + " not found");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/dashboard";
        }

        return "redirect:/interventie/" + id;
    }

    @GetMapping("/clienti")
    public String vizualizareClienti(HttpSession session, Model model) {
        if (!checkAuth(session)) {
            return "redirect:/login";
        }

        List<Map<String, Object>> clientiCuStatistici = clientRepository.findClientiCuStatistici();
        model.addAttribute("clienti", clientiCuStatistici);
        model.addAttribute("user", ((User) session.getAttribute("user")).getNumeComplet());

        return "vizualizare_clienti";
    }

    @GetMapping("/adaugare_client")
    public String formularAdaugareClient(HttpSession session, Model model) {
        if (!checkAuth(session)) {
            return "redirect(/login";
        }

        model.addAttribute("client", new Client());
        return "adauga_client";
    }

    @PostMapping("/adaugare_client")
    @Transactional
    public String adaugareClient(@ModelAttribute Client client,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!checkAuth(session)) {
            return "redirect:/login";
        }

        try {
            Integer nextId = clientRepository.findMaxId() + 1;
            client.setId(nextId);
            clientRepository.save(client);
            redirectAttributes.addFlashAttribute("success", "Client adaugat cu succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare la adaugare: " + e.getMessage());
        }

        return "redirect:/clienti";
    }

    @GetMapping("/client/sterge/{id}")
    @Transactional
    public String stergeClient(@PathVariable Integer id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!checkAuth(session)) {
            return "redirect:/login";
        }

        try {
            List<Interventie> interventii = interventieRepository.findByClientId(id);
            for (Interventie interventie : interventii) {
                interventieRepository.deleteAlocariAngajati(interventie.getId());
                interventieMaterialeRepository.deleteByInterventieId(interventie.getId());
            }
            interventieRepository.deleteByClientId(id);
            clientRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("success", "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare: " + e.getMessage());
        }

        return "redirect:/clienti";
    }

    @GetMapping("/adauga_interventie")
    public String formularAdaugaInterventie(HttpSession session, Model model) {
        if (!checkAuth(session)) {
            return "redirect:/login";
        }

        model.addAttribute("interventie", new Interventie());
        model.addAttribute("clienti", clientRepository.findAll());
        model.addAttribute("servicii", serviciuRepository.findAll());
        model.addAttribute("materiale", materialRepository.findAll());
        model.addAttribute("user", ((User) session.getAttribute("user")).getNumeComplet());

        return "adauga_interventie";
    }

    @PostMapping("/adauga_interventie")
    @Transactional
    public String adaugaInterventie(@RequestParam Integer clientId,
                                    @RequestParam Integer serviciuId,
                                    @RequestParam String data,
                                    @RequestParam(required = false) Integer materialId,
                                    @RequestParam(required = false) Integer cantitate,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!checkAuth(session)) {
            return "redirect:/login";
        }

        try {
            Integer nextId = interventieRepository.findMaxId() + 1;
            LocalDateTime dataInterventie = LocalDateTime.parse(data);

            interventieRepository.insertWithFullDetails(
                    nextId, clientId, serviciuId, dataInterventie,
                    null, "Programata", null, null
            );

            if (materialId != null && cantitate != null && cantitate > 0) {
                interventieMaterialeRepository.insertMaterial(nextId, materialId, cantitate);
            }

            redirectAttributes.addFlashAttribute("success", "");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Eroare: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/interventie/{id}")
    public String detaliiInterventie(@PathVariable Integer id, HttpSession session, Model model) {
        if (!checkAuth(session)) return "redirect:/login";

        try {
            model.addAttribute("interventie", interventieRepository.findInterventieDetails(id));
            model.addAttribute("materiale", interventieMaterialeRepository.findMaterialeByInterventieId(id));
            return "detalii";
        } catch (Exception e) {
            return "redirect:/dashboard?error=not_found";
        }
    }

    @PostMapping("/update_status")
    @Transactional
    public String updateStatus(@RequestParam Integer idInterventie, @RequestParam String status, HttpSession session) {
        if (!checkAuth(session)) return "redirect:/login";
        interventieRepository.updateStatus(idInterventie, status);
        return "redirect:/interventie/" + idInterventie;
    }

    @GetMapping("/sterge_interventie/{id}")
    @Transactional
    public String stergeInterventie(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        if (!checkAuth(session)) return "redirect:/login";

        try {
            interventieRepository.deleteAlocariAngajati(id);
            interventieMaterialeRepository.deleteByInterventieId(id);
            interventieRepository.deleteById(id);
            ra.addFlashAttribute("success", "");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Eroare.");
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/editare/{id}")
    public String formularEditare(@PathVariable Integer id, HttpSession session, Model model) {
        if (!checkAuth(session)) return "redirect:/login";
        Interventie interventie = interventieRepository.findById(id).orElse(null);
        if (interventie == null) return "redirect:/dashboard";
        model.addAttribute("interventie", interventie);
        return "editare";
    }

    @PostMapping("/salveaza")
    @Transactional
    public String salveaza(@ModelAttribute Interventie interventie, HttpSession session) {
        if (!checkAuth(session)) return "redirect:/login";

        if (interventie.getId() == null) {
            Integer nextId = interventieRepository.findMaxId() + 1;
            if (interventie.getData() == null) interventie.setData(LocalDateTime.now());
            interventieRepository.insertWithId(
                    nextId, interventie.getClientId(), interventie.getServiciuId(),
                    interventie.getData(), interventie.getAdresa(), interventie.getStatus(),
                    interventie.getCategorieId(), interventie.getDescriere()
            );
        } else {
            interventieRepository.save(interventie);
        }
        return "redirect:/dashboard";
    }
}