package tily.mg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("userName", "User");
        model.addAttribute("pageTitle", "Dashboard Overview");
        return "dashboard";
    }

    @GetMapping("/responsables")
    public String responsables(Model model) {
        model.addAttribute("userName", "User");
        model.addAttribute("pageTitle", "Responsables");
        return "responsables";
    }

    @GetMapping("/eleves")
    public String eleves(Model model) {
        model.addAttribute("userName", "User");
        model.addAttribute("pageTitle", "Élèves");
        return "eleves";
    }

    @GetMapping("/profil")
    public String profil(Model model) {
        model.addAttribute("userName", "User");
        model.addAttribute("pageTitle", "Mon Profil");
        return "profil";
    }
}
