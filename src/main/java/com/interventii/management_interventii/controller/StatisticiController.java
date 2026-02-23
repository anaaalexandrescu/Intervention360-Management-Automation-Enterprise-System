/** Clasa pentru generarea si afisarea indicatorilor de performanta si a statisticilor.
 * @author Alexandrescu Anamaria
 * @version 27 decembrie 2025
 */
package com.interventii.management_interventii.controller;

import com.interventii.management_interventii.model.Interventie;
import com.interventii.management_interventii.repository.ClientRepository;
import com.interventii.management_interventii.repository.InterventieRepository;
import com.interventii.management_interventii.repository.ServiciuRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StatisticiController {

    @Autowired
    private InterventieRepository interventieRepository;

    @Autowired
    private ServiciuRepository serviciuRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/statistici")
    public String viewStatistici(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        List<Map<String, Object>> clientiTop = clientRepository.findClientiCuStatistici();
        model.addAttribute("clientiTop", clientiTop);

        List<Map<String, Object>> serviciiPopulare = serviciuRepository.getServiciiPopulare(1);
        model.addAttribute("serviciiPopulare", serviciiPopulare);

        List<Map<String, Object>> costuriInterventii = interventieRepository.getCosturiInterventii();
        model.addAttribute("costuriInterventii", costuriInterventii);

        // date pt grafice
        List<Interventie> toate = interventieRepository.findAll();

        long finalizate = toate.stream().filter(i -> "Finalizata".equalsIgnoreCase(i.getStatus())).count();
        long inDesfasurare = toate.stream().filter(i -> "In desfasurare".equalsIgnoreCase(i.getStatus())).count();
        long programate = toate.stream().filter(i -> "Programata".equalsIgnoreCase(i.getStatus())).count();

        model.addAttribute("statFinalizate", finalizate);
        model.addAttribute("statInLucru", inDesfasurare);
        model.addAttribute("statProgramate", programate);

        return "statistici";
    }
}