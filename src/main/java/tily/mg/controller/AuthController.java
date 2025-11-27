package tily.mg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tily.mg.service.AuthService;

import java.time.LocalDate;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email ou mot de passe incorrect");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Vous avez été déconnecté avec succès");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Inscription réussie ! Vous pouvez maintenant vous connecter");
        }
        return "login";
    }

    @GetMapping("/inscription")
    public String inscription() {
        return "inscription";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    @PostMapping("/auth/inscription")
    public String inscrire(
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam String confirmMotDePasse,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String typePersonne,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Validation des mots de passe
            if (!motDePasse.equals(confirmMotDePasse)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Les mots de passe ne correspondent pas");
                return "redirect:/inscription";
            }

            // Validation de la longueur du mot de passe
            if (motDePasse.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "Le mot de passe doit contenir au moins 6 caractères");
                return "redirect:/inscription";
            }

            // Vérifier si l'email existe déjà
            if (authService.emailExists(email)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cet email est déjà utilisé");
                return "redirect:/inscription";
            }

            // Créer le compte
            authService.inscrire(email, motDePasse, nom, prenom, typePersonne, dateNaissance);

            return "redirect:/login?registered=true";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'inscription : " + e.getMessage());
            return "redirect:/inscription";
        }
    }
}

