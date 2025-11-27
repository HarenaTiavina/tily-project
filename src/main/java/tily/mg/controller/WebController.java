package tily.mg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tily.mg.entity.Personne;
import tily.mg.entity.Utilisateur;
import tily.mg.service.AuthService;
import tily.mg.service.DashboardService;
import tily.mg.service.PersonneService;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Controller
public class WebController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private PersonneService personneService;

    @Autowired
    private AuthService authService;

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            return authService.findByEmail(email)
                    .map(Utilisateur::getNomComplet)
                    .orElse("Utilisateur");
        }
        return "Utilisateur";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("pageTitle", "Dashboard Overview");

        // Responsables stats
        Long totalResponsables = dashboardService.getTotalResponsables();
        Long responsablesWithFafi = dashboardService.getResponsablesWithFafi();
        Long responsablesWithoutFafi = dashboardService.getResponsablesWithoutFafi();

        model.addAttribute("totalResponsables", totalResponsables);
        model.addAttribute("responsablesWithFafi", responsablesWithFafi);
        model.addAttribute("responsablesWithoutFafi", responsablesWithoutFafi);

        // Eleves stats
        Long totalEleves = dashboardService.getTotalEleves();
        Long elevesWithFafi = dashboardService.getElevesWithFafi();
        Long elevesWithoutFafi = dashboardService.getElevesWithoutFafi();

        model.addAttribute("totalEleves", totalEleves);
        model.addAttribute("elevesWithFafi", elevesWithFafi);
        model.addAttribute("elevesWithoutFafi", elevesWithoutFafi);

        // FAFI Total stats
        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
        String totalFafi = formatter.format(dashboardService.getTotalFafiMontant()) + " Ar";
        Long paidFafi = dashboardService.getTotalPaidFafi();
        Long unpaidFafi = dashboardService.getTotalUnpaidFafi();

        model.addAttribute("totalFafi", totalFafi);
        model.addAttribute("paidFafi", paidFafi);
        model.addAttribute("unpaidFafi", unpaidFafi);

        return "dashboard";
    }

    @GetMapping("/responsables")
    public String responsables(
            Model model,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer sectionId,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) Boolean hasAssurance
    ) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("pageTitle", "Responsables");

        // Get filtered or all responsables
        List<Personne> responsables;
        if (secteurId != null || sectionId != null || niveau != null || hasAssurance != null) {
            responsables = personneService.filterResponsables(secteurId, sectionId, niveau, hasAssurance);
        } else {
            responsables = personneService.findAllResponsables();
        }

        model.addAttribute("responsables", responsables);
        model.addAttribute("totalCount", responsables.size());

        // Reference data for filters and form
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("sections", personneService.findAllSections());

        return "responsables";
    }

    @PostMapping("/responsables/ajouter")
    public String ajouterResponsable(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String numeroTelephone,
            @RequestParam(required = false) String numeroCin,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer sectionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Personne personne = new Personne();
            personne.setNom(nom);
            personne.setPrenom(prenom);
            personne.setTotem(totem);
            personne.setDateNaissance(dateNaissance);
            personne.setNiveau(niveau);
            personne.setNumeroTelephone(numeroTelephone);
            personne.setNumeroCin(numeroCin);
            personne.setNomPere(nomPere);
            personne.setNomMere(nomMere);
            personne.setDateFanekena(dateFanekena);

            personneService.createResponsable(personne, secteurId, sectionId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Responsable ajouté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout : " + e.getMessage());
        }
        
        return "redirect:/responsables";
    }

    @PostMapping("/responsables/supprimer")
    public String supprimerResponsable(
            @RequestParam Integer id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            personneService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Responsable supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        
        return "redirect:/responsables";
    }

    @GetMapping("/eleves")
    public String eleves(
            Model model,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer sectionId,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) Boolean hasAssurance
    ) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("pageTitle", "Élèves");

        // Get filtered or all eleves
        List<Personne> eleves;
        if (secteurId != null || sectionId != null || niveau != null || hasAssurance != null) {
            eleves = personneService.filterEleves(secteurId, sectionId, niveau, hasAssurance);
        } else {
            eleves = personneService.findAllEleves();
        }

        model.addAttribute("eleves", eleves);
        model.addAttribute("totalCount", eleves.size());

        // Reference data for filters and form
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("sections", personneService.findAllSections());

        return "eleves";
    }

    @PostMapping("/eleves/ajouter")
    public String ajouterEleve(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer sectionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Personne personne = new Personne();
            personne.setNom(nom);
            personne.setPrenom(prenom);
            personne.setTotem(totem);
            personne.setDateNaissance(dateNaissance);
            personne.setNiveau(niveau);
            personne.setNomPere(nomPere);
            personne.setNomMere(nomMere);
            personne.setDateFanekena(dateFanekena);

            personneService.createEleve(personne, secteurId, sectionId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Élève ajouté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'ajout : " + e.getMessage());
        }
        
        return "redirect:/eleves";
    }

    @PostMapping("/eleves/supprimer")
    public String supprimerEleve(
            @RequestParam Integer id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            personneService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Élève supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        
        return "redirect:/eleves";
    }

    @GetMapping("/profil")
    public String profil(Model model) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("pageTitle", "Mon Profil");

        // Récupérer l'utilisateur connecté avec ses détails
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            authService.findByEmail(email).ifPresent(utilisateur -> {
                model.addAttribute("utilisateur", utilisateur);
                model.addAttribute("personne", utilisateur.getPersonne());
            });
        }

        // Reference data pour les selects
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("sections", personneService.findAllSections());

        return "profil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) String numeroTelephone,
            @RequestParam(required = false) String numeroCin,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer sectionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String email = auth.getName();
                authService.findByEmail(email).ifPresent(utilisateur -> {
                    Personne personne = utilisateur.getPersonne();
                    if (personne != null) {
                        personne.setNom(nom);
                        personne.setPrenom(prenom);
                        personne.setTotem(totem);
                        personne.setDateNaissance(dateNaissance);
                        personne.setNiveau(niveau);
                        personne.setNumeroTelephone(numeroTelephone);
                        personne.setNumeroCin(numeroCin);
                        personne.setNomPere(nomPere);
                        personne.setNomMere(nomMere);
                        personne.setDateFanekena(dateFanekena);

                        // Update secteur and section
                        if (secteurId != null) {
                            personneService.findSecteurById(secteurId).ifPresent(personne::setSecteur);
                        } else {
                            personne.setSecteur(null);
                        }

                        if (sectionId != null) {
                            personneService.findSectionById(sectionId).ifPresent(personne::setSection);
                        } else {
                            personne.setSection(null);
                        }

                        personneService.save(personne);
                    }
                });
            }
            redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour : " + e.getMessage());
        }

        return "redirect:/profil";
    }
}
