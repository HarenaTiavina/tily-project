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

    /**
     * Récupère l'utilisateur connecté
     */
    private Optional<Utilisateur> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return authService.findByEmail(auth.getName());
        }
        return Optional.empty();
    }

    /**
     * Vérifie si l'utilisateur connecté est admin
     */
    private boolean isAdmin() {
        return getCurrentUser().map(Utilisateur::isAdmin).orElse(false);
    }

    /**
     * Récupère l'ID du Fivondronana de l'utilisateur connecté (null pour admin)
     */
    private Integer getCurrentUserFivondronanaId() {
        return getCurrentUser().map(Utilisateur::getFivondronanaId).orElse(null);
    }

    private String getCurrentUserName() {
        return getCurrentUser().map(Utilisateur::getNomComplet).orElse("Utilisateur");
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("isAdmin", isAdmin());
        getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
            if (user.getFivondronana() != null) {
                model.addAttribute("currentFivondronana", user.getFivondronana());
            }
        });
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Dashboard Overview");

        Integer fivondronanaId = getCurrentUserFivondronanaId();
        boolean admin = isAdmin();

        // Responsables stats
        Long totalResponsables;
        Long responsablesWithFafi;
        Long responsablesWithoutFafi;

        // Eleves stats
        Long totalEleves;
        Long elevesWithFafi;
        Long elevesWithoutFafi;

        if (admin) {
            // Admin voit tout
            totalResponsables = dashboardService.getTotalResponsables();
            responsablesWithFafi = dashboardService.getResponsablesWithFafi();
            responsablesWithoutFafi = dashboardService.getResponsablesWithoutFafi();

            totalEleves = dashboardService.getTotalEleves();
            elevesWithFafi = dashboardService.getElevesWithFafi();
            elevesWithoutFafi = dashboardService.getElevesWithoutFafi();
        } else {
            // Utilisateur Fivondronana voit seulement son Fivondronana
            totalResponsables = personneService.countResponsablesByFivondronana(fivondronanaId);
            responsablesWithFafi = personneService.countResponsablesWithFafiByFivondronana(fivondronanaId);
            responsablesWithoutFafi = totalResponsables - responsablesWithFafi;

            totalEleves = personneService.countElevesByFivondronana(fivondronanaId);
            elevesWithFafi = personneService.countElevesWithFafiByFivondronana(fivondronanaId);
            elevesWithoutFafi = totalEleves - elevesWithFafi;
        }

        model.addAttribute("totalResponsables", totalResponsables);
        model.addAttribute("responsablesWithFafi", responsablesWithFafi);
        model.addAttribute("responsablesWithoutFafi", responsablesWithoutFafi);

        model.addAttribute("totalEleves", totalEleves);
        model.addAttribute("elevesWithFafi", elevesWithFafi);
        model.addAttribute("elevesWithoutFafi", elevesWithoutFafi);

        // FAFI Total stats (admin seulement pour le total global)
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
            @RequestParam(required = false) Integer fivondronanaId,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) Boolean hasFafi
    ) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Responsables");

        boolean admin = isAdmin();
        Integer userFivondronanaId = getCurrentUserFivondronanaId();

        List<Personne> responsables;

        if (admin) {
            // Admin peut filtrer par Fivondronana ou voir tout
            if (fivondronanaId != null || secteurId != null || andraikitraId != null || hasFafi != null) {
                responsables = personneService.filterResponsables(fivondronanaId, secteurId, andraikitraId, hasFafi);
            } else {
                responsables = personneService.findAllResponsables();
            }
            // Admin peut voir la liste des Fivondronana pour filtrer
            model.addAttribute("fivondronana", personneService.findAllFivondronana());
        } else {
            // Utilisateur Fivondronana voit seulement son Fivondronana
            if (secteurId != null || andraikitraId != null || hasFafi != null) {
                responsables = personneService.filterResponsablesByFivondronana(userFivondronanaId, secteurId, andraikitraId, hasFafi);
            } else {
                responsables = personneService.findResponsablesByFivondronana(userFivondronanaId);
            }
        }

        model.addAttribute("responsables", responsables);
        model.addAttribute("totalCount", responsables.size());

        // Reference data for filters and form
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("andraikitra", personneService.findAllAndraikitra());
        model.addAttribute("fafiStatuts", personneService.findAllFafiStatuts());

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
            @RequestParam(required = false) Integer fivondronanaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Personne personne = new Personne();
            personne.setNom(nom);
            personne.setPrenom(prenom);
            personne.setTotem(totem);
            personne.setDateNaissance(dateNaissance);
            personne.setNumeroTelephone(numeroTelephone);
            personne.setNumeroCin(numeroCin);
            personne.setNomPere(nomPere);
            personne.setNomMere(nomMere);
            personne.setDateFanekena(dateFanekena);

            // Pour les non-admin, forcer le Fivondronana de l'utilisateur
            Integer effectiveFivondronanaId;
            if (isAdmin()) {
                effectiveFivondronanaId = fivondronanaId;
            } else {
                effectiveFivondronanaId = getCurrentUserFivondronanaId();
            }

            personneService.createResponsable(personne, secteurId, andraikitraId, effectiveFivondronanaId);
            
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
            // Vérifier les permissions
            if (!isAdmin()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(id, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier cette personne");
                    return "redirect:/responsables";
                }
            }

            personneService.findById(id).ifPresent(personne -> {
                personne.setNom(nom);
                personne.setPrenom(prenom);
                personne.setTotem(totem);
                personne.setDateNaissance(dateNaissance);
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
            // Vérifier les permissions
            if (!isAdmin()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(id, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de supprimer cette personne");
                    return "redirect:/responsables";
                }
            }

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
            @RequestParam(required = false) Integer fivondronanaId,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer fizaranaId,
            @RequestParam(required = false) String ambaratonga,
            @RequestParam(required = false) Boolean hasFafi
    ) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Élèves");

        boolean admin = isAdmin();
        Integer userFivondronanaId = getCurrentUserFivondronanaId();

        List<Personne> eleves;

        if (admin) {
            // Admin peut filtrer par Fivondronana ou voir tout
            if (fivondronanaId != null || secteurId != null || fizaranaId != null || ambaratonga != null || hasFafi != null) {
                eleves = personneService.filterEleves(fivondronanaId, secteurId, fizaranaId, ambaratonga, hasFafi);
            } else {
                eleves = personneService.findAllEleves();
            }
            // Admin peut voir la liste des Fivondronana pour filtrer
            model.addAttribute("fivondronana", personneService.findAllFivondronana());
        } else {
            // Utilisateur Fivondronana voit seulement son Fivondronana
            if (secteurId != null || fizaranaId != null || ambaratonga != null || hasFafi != null) {
                eleves = personneService.filterElevesByFivondronana(userFivondronanaId, secteurId, fizaranaId, ambaratonga, hasFafi);
            } else {
                eleves = personneService.findElevesByFivondronana(userFivondronanaId);
            }
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
            @RequestParam(required = false) Integer fivondronanaId,
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

            // Pour les non-admin, forcer le Fivondronana de l'utilisateur
            Integer effectiveFivondronanaId;
            if (isAdmin()) {
                effectiveFivondronanaId = fivondronanaId;
            } else {
                effectiveFivondronanaId = getCurrentUserFivondronanaId();
            }

            personneService.createEleve(personne, secteurId, fizaranaId, effectiveFivondronanaId);
            
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
            // Vérifier les permissions
            if (!isAdmin()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(id, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier cette personne");
                    return "redirect:/eleves";
                }
            }

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
            // Vérifier les permissions
            if (!isAdmin()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(id, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de supprimer cette personne");
                    return "redirect:/eleves";
                }
            }

            personneService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Élève supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        
        return "redirect:/eleves";
    }

    // Endpoint pour modifier le FAFI d'un élève (admin ou utilisateur du même Fivondronana)
    @PostMapping("/eleves/modifier-fafi")
    public String modifierFafiEleve(
            @RequestParam Integer personneId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String numeroFafi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Vérifier les permissions
            if (!isAdmin()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(personneId, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier le FAFI de cette personne");
                    return "redirect:/eleves";
                }
            }

            personneService.updateFafi(personneId, datePaiement, montant, statut, numeroFafi);
            redirectAttributes.addFlashAttribute("successMessage", "FAFI novaina soa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }
        return "redirect:/eleves";
    }

    // Endpoint pour modifier le FAFI d'un responsable
    @PostMapping("/responsables/modifier-fafi")
    public String modifierFafiResponsable(
            @RequestParam Integer personneId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement,
            @RequestParam(required = false) BigDecimal montant,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String numeroFafi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Vérifier les permissions
            if (!isAdmin()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(personneId, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier le FAFI de cette personne");
                    return "redirect:/responsables";
                }
            }

            personneService.updateFafi(personneId, datePaiement, montant, statut, numeroFafi);
            redirectAttributes.addFlashAttribute("successMessage", "FAFI novaina soa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }
        return "redirect:/responsables";
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
            // Récupérer le Fivondronana de l'utilisateur pour l'import
            Integer fivondronanaId = isAdmin() ? null : getCurrentUserFivondronanaId();
            
            ExcelImportService.ImportResult result = excelImportService.importBeazina(file, fivondronanaId);
            
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
            // Récupérer le Fivondronana de l'utilisateur pour l'import
            Integer fivondronanaId = isAdmin() ? null : getCurrentUserFivondronanaId();
            
            ExcelImportService.ImportResult result = excelImportService.importMpiandraikitra(file, fivondronanaId);
            
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

    // ========== ADMIN: Gestion des utilisateurs Fivondronana ==========

    @GetMapping("/admin/utilisateurs")
    public String gestionUtilisateurs(Model model) {
        if (!isAdmin()) {
            return "redirect:/access-denied";
        }

        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Gestion des Utilisateurs");
        model.addAttribute("utilisateurs", authService.findAllNonAdminUsers());
        model.addAttribute("fivondronana", personneService.findAllFivondronana());

        return "admin/utilisateurs";
    }

    @PostMapping("/admin/utilisateurs/ajouter")
    public String ajouterUtilisateurFivondronana(
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam Integer fivondronanaId,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdmin()) {
            return "redirect:/access-denied";
        }

        try {
            authService.creerCompteFivondronana(email, motDePasse, fivondronanaId);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/admin/utilisateurs/toggle")
    public String toggleUtilisateur(
            @RequestParam Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdmin()) {
            return "redirect:/access-denied";
        }

        try {
            authService.toggleUserActif(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Statut modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/admin/utilisateurs/supprimer")
    public String supprimerUtilisateur(
            @RequestParam Integer userId,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdmin()) {
            return "redirect:/access-denied";
        }

        try {
            authService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur : " + e.getMessage());
        }

        return "redirect:/admin/utilisateurs";
    }
}
