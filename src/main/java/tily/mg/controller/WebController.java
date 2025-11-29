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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tily.mg.entity.Personne;
import tily.mg.entity.Utilisateur;
import tily.mg.service.AuthService;
import tily.mg.service.DashboardService;
import tily.mg.service.ExcelImportService;
import tily.mg.service.PersonneService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private PersonneService personneService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ExcelImportService excelImportService;

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
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) Boolean hasFafi
    ) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("pageTitle", "Responsables");

        // Get filtered or all responsables
        List<Personne> responsables;
        if (secteurId != null || andraikitraId != null || hasFafi != null) {
            responsables = personneService.filterResponsables(secteurId, andraikitraId, hasFafi);
        } else {
            responsables = personneService.findAllResponsables();
        }

        model.addAttribute("responsables", responsables);
        model.addAttribute("totalCount", responsables.size());

        // Reference data for filters and form
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("andraikitra", personneService.findAllAndraikitra());

        return "responsables";
    }

    @PostMapping("/responsables/ajouter")
    public String ajouterResponsable(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String numeroTelephone,
            @RequestParam(required = false) String numeroCin,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer andraikitraId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Personne personne = new Personne();
            personne.setNom(nom);
            personne.setPrenom(prenom);
            personne.setTotem(totem);
            personne.setDateNaissance(dateNaissance);
            // Pas de niveau pour les responsables
            personne.setNumeroTelephone(numeroTelephone);
            personne.setNumeroCin(numeroCin);
            personne.setNomPere(nomPere);
            personne.setNomMere(nomMere);
            personne.setDateFanekena(dateFanekena);

            personneService.createResponsable(personne, secteurId, andraikitraId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Tafiditra mpiandraikitra !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety :" + e.getMessage());
        }
        
        return "redirect:/responsables";
    }

    @PostMapping("/responsables/modifier")
    public String modifierResponsable(
            @RequestParam Integer id,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String numeroTelephone,
            @RequestParam(required = false) String numeroCin,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer andraikitraId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            personneService.findById(id).ifPresent(personne -> {
                personne.setNom(nom);
                personne.setPrenom(prenom);
                personne.setTotem(totem);
                personne.setDateNaissance(dateNaissance);
                // Pas d'ambaratonga pour les responsables
                personne.setAmbaratonga(null);
                personne.setNumeroTelephone(numeroTelephone);
                personne.setNumeroCin(numeroCin);
                personne.setNomPere(nomPere);
                personne.setNomMere(nomMere);
                personne.setDateFanekena(dateFanekena);

                if (secteurId != null) {
                    personneService.findSecteurById(secteurId).ifPresent(personne::setSecteur);
                } else {
                    personne.setSecteur(null);
                }

                if (andraikitraId != null) {
                    personneService.findAndraikitraById(andraikitraId).ifPresent(personne::setAndraikitra);
                } else {
                    personne.setAndraikitra(null);
                }

                personneService.save(personne);
            });
            
            redirectAttributes.addFlashAttribute("successMessage", "Responsable modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la modification : " + e.getMessage());
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
            @RequestParam(required = false) Integer fizaranaId,
            @RequestParam(required = false) String ambaratonga,
            @RequestParam(required = false) Boolean hasFafi
    ) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("pageTitle", "Élèves");

        // Get filtered or all eleves
        List<Personne> eleves;
        if (secteurId != null || fizaranaId != null || ambaratonga != null || hasFafi != null) {
            eleves = personneService.filterEleves(secteurId, fizaranaId, ambaratonga, hasFafi);
        } else {
            eleves = personneService.findAllEleves();
        }

        model.addAttribute("eleves", eleves);
        model.addAttribute("totalCount", eleves.size());

        // Reference data for filters and form
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("fizarana", personneService.findAllFizarana());
        model.addAttribute("fafiStatuts", personneService.findAllFafiStatuts());

        return "eleves";
    }

    @PostMapping("/eleves/ajouter")
    public String ajouterEleve(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String ambaratonga,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer fizaranaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Personne personne = new Personne();
            personne.setNom(nom);
            personne.setPrenom(prenom);
            personne.setTotem(totem);
            personne.setDateNaissance(dateNaissance);
            personne.setAmbaratonga(ambaratonga);
            personne.setNomPere(nomPere);
            personne.setNomMere(nomMere);
            personne.setDateFanekena(dateFanekena);

            personneService.createEleve(personne, secteurId, fizaranaId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Tafiditra soa ny Beazina!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety : " + e.getMessage());
        }
        
        return "redirect:/eleves";
    }

    @PostMapping("/eleves/modifier")
    public String modifierEleve(
            @RequestParam Integer id,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String ambaratonga,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer fizaranaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            personneService.findById(id).ifPresent(personne -> {
                personne.setNom(nom);
                personne.setPrenom(prenom);
                personne.setTotem(totem);
                personne.setDateNaissance(dateNaissance);
                personne.setAmbaratonga(ambaratonga);
                personne.setNomPere(nomPere);
                personne.setNomMere(nomMere);
                personne.setDateFanekena(dateFanekena);

                if (secteurId != null) {
                    personneService.findSecteurById(secteurId).ifPresent(personne::setSecteur);
                } else {
                    personne.setSecteur(null);
                }

                if (fizaranaId != null) {
                    personneService.findFizaranaById(fizaranaId).ifPresent(personne::setFizarana);
                } else {
                    personne.setFizarana(null);
                }

                personneService.save(personne);
            });
            
            redirectAttributes.addFlashAttribute("successMessage", "Élève modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la modification : " + e.getMessage());
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
        model.addAttribute("fizarana", personneService.findAllFizarana());
        model.addAttribute("andraikitra", personneService.findAllAndraikitra());
        model.addAttribute("fafiStatuts", personneService.findAllFafiStatuts());

        return "profil";
    }

    @PostMapping("/profil/modifier")
    public String modifierProfil(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String ambaratonga,
            @RequestParam(required = false) String numeroTelephone,
            @RequestParam(required = false) String numeroCin,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer fizaranaId,
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fafiDatePaiement,
            @RequestParam(required = false) BigDecimal fafiMontant,
            @RequestParam(required = false) String fafiStatut,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String email = auth.getName();
                Optional<Utilisateur> utilisateurOpt = authService.findByEmail(email);
                if (utilisateurOpt.isPresent()) {
                    Utilisateur utilisateur = utilisateurOpt.get();
                    Personne personne = utilisateur.getPersonne();
                    if (personne != null) {
                        personne.setNom(nom);
                        personne.setPrenom(prenom);
                        personne.setTotem(totem);
                        personne.setDateNaissance(dateNaissance);
                        personne.setNumeroTelephone(numeroTelephone);
                        personne.setNumeroCin(numeroCin);
                        personne.setNomPere(nomPere);
                        personne.setNomMere(nomMere);
                        personne.setDateFanekena(dateFanekena);

                        // Si c'est un Beazina, utiliser ambaratonga et fizarana
                        // Si c'est un Mpiandraikitra, utiliser andraikitra (pas d'ambaratonga)
                        if (personne.isBeazina()) {
                            personne.setAmbaratonga(ambaratonga);
                        } else if (personne.isMpiandraikitra()) {
                            personne.setAmbaratonga(null);
                        }

                        // Update secteur
                        if (secteurId != null) {
                            personneService.findSecteurById(secteurId).ifPresent(personne::setSecteur);
                        } else {
                            personne.setSecteur(null);
                        }

                        // Update fizarana pour Beazina
                        if (personne.isBeazina()) {
                            if (fizaranaId != null) {
                                personneService.findFizaranaById(fizaranaId).ifPresent(personne::setFizarana);
                            } else {
                                personne.setFizarana(null);
                            }
                            personne.setAndraikitra(null);
                        }

                        // Update andraikitra pour Mpiandraikitra
                        if (personne.isMpiandraikitra()) {
                            if (andraikitraId != null) {
                                personneService.findAndraikitraById(andraikitraId).ifPresent(personne::setAndraikitra);
                            } else {
                                personne.setAndraikitra(null);
                            }
                            personne.setFizarana(null);
                        }

                        // Sauvegarder les modifications de la personne
                        personneService.save(personne);
                        
                        // Mettre à jour le FAFI si les paramètres sont fournis
                        personneService.updateFafi(personne.getId(), fafiDatePaiement, fafiMontant, fafiStatut);
                        
                        redirectAttributes.addFlashAttribute("successMessage", "Voaray ny fihovana");
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny mombamomba ny olona");
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny mpampiasa");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy voaantoka ny mpampiasa");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy olana : " + e.getMessage());
        }

        return "redirect:/profil";
    }

    // Endpoint pour modifier le FAFI d'un élève (admin)
    @PostMapping("/eleves/modifier-fafi")
    public String modifierFafiEleve(
            @RequestParam Integer personneId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String statut,
            RedirectAttributes redirectAttributes
    ) {
        try {
            personneService.updateFafi(personneId, datePaiement, montant, statut);
            redirectAttributes.addFlashAttribute("successMessage", "FAFI novaina soa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }
        return "redirect:/eleves";
    }

    // Endpoint pour modifier le FAFI depuis le profil (utilisateur)
    @PostMapping("/profil/modifier-fafi")
    public String modifierFafiProfil(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String statut,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                String email = auth.getName();
                Optional<Utilisateur> utilisateurOpt = authService.findByEmail(email);
                if (utilisateurOpt.isPresent()) {
                    Utilisateur utilisateur = utilisateurOpt.get();
                    Personne personne = utilisateur.getPersonne();
                    if (personne != null) {
                        personneService.updateFafi(personne.getId(), datePaiement, montant, statut);
                        redirectAttributes.addFlashAttribute("successMessage", "FAFI novaina soa!");
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny mombamomba ny olona");
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny mpampiasa amin'ny email: " + email);
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy voaantoka ny mpampiasa");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }
        return "redirect:/profil";
    }

    // Endpoint pour importer des Beazina depuis Excel
    @PostMapping("/eleves/import-excel")
    public String importBeazinaExcel(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucun fichier sélectionné");
            return "redirect:/eleves";
        }

        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls") && !fileName.endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le fichier doit être un fichier Excel (.xlsx, .xls) ou CSV (.csv)");
            return "redirect:/eleves";
        }

        try {
            ExcelImportService.ImportResult result = excelImportService.importBeazina(file);
            
            if (result.getErrorCount() == 0) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Import réussi ! " + result.getSuccessCount() + " Beazina importé(s)");
            } else {
                redirectAttributes.addFlashAttribute("importResult", result);
                redirectAttributes.addFlashAttribute("warningMessage", 
                    "Import partiel : " + result.getSuccessCount() + " réussi(s), " + 
                    result.getErrorCount() + " erreur(s)");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de l'import : " + e.getMessage());
        }

        return "redirect:/eleves";
    }

    // Endpoint pour importer des Mpiandraikitra depuis Excel
    @PostMapping("/responsables/import-excel")
    public String importMpiandraikitraExcel(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aucun fichier sélectionné");
            return "redirect:/responsables";
        }

        String fileName = file.getOriginalFilename().toLowerCase();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls") && !fileName.endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Le fichier doit être un fichier Excel (.xlsx, .xls) ou CSV (.csv)");
            return "redirect:/responsables";
        }

        try {
            ExcelImportService.ImportResult result = excelImportService.importMpiandraikitra(file);
            
            if (result.getErrorCount() == 0) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Import réussi ! " + result.getSuccessCount() + " Mpiandraikitra importé(s)");
            } else {
                redirectAttributes.addFlashAttribute("importResult", result);
                redirectAttributes.addFlashAttribute("warningMessage", 
                    "Import partiel : " + result.getSuccessCount() + " réussi(s), " + 
                    result.getErrorCount() + " erreur(s)");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur lors de l'import : " + e.getMessage());
        }

        return "redirect:/responsables";
    }
}
