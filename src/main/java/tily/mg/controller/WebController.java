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
import tily.mg.entity.PrixFafi;
import tily.mg.entity.Utilisateur;
import tily.mg.entity.TypeFiofanana;
import tily.mg.entity.TypeFiloha;
import tily.mg.entity.DetailsFiofanana;
import tily.mg.service.AuthService;
import tily.mg.service.DashboardService;
import tily.mg.service.ExcelImportService;
import tily.mg.service.FafiService;
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

    @Autowired
    private FafiService fafiService;

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
     * Vérifie si l'utilisateur connecté est admin (strictement ADMIN)
     */
    private boolean isAdmin() {
        return getCurrentUser().map(Utilisateur::isAdmin).orElse(false);
    }

    /**
     * Vérifie si l'utilisateur a un accès administratif (ADMIN ou DFAF)
     * Les deux peuvent voir toutes les données mais seul ADMIN peut créer des comptes
     */
    private boolean hasAdminAccess() {
        return getCurrentUser().map(Utilisateur::hasAdminAccess).orElse(false);
    }

    /**
     * Récupère l'ID du Fivondronana de l'utilisateur connecté (null pour admin/dfaf)
     */
    private Integer getCurrentUserFivondronanaId() {
        return getCurrentUser().map(Utilisateur::getFivondronanaId).orElse(null);
    }

    private String getCurrentUserName() {
        return getCurrentUser().map(Utilisateur::getNomComplet).orElse("Utilisateur");
    }

    /**
     * Vérifie si l'utilisateur connecté peut gérer les personnes de son Fivondronana
     * (ADMIN, DFAF ou USER rattaché à un Fivondronana — pas les Filoha)
     */
    private boolean canManagePersonnes() {
        if (hasAdminAccess()) {
            return true;
        }
        return getCurrentUser()
                .map(user -> user.getFivondronana() != null && !user.isFiloha())
                .orElse(false);
    }

    /**
     * Vérifie si l'utilisateur connecté peut modifier/supprimer une personne donnée
     */
    private boolean canManagePersonne(Integer personneId) {
        if (hasAdminAccess()) {
            return true;
        }
        Optional<Utilisateur> currentUser = getCurrentUser();
        if (currentUser.isEmpty() || currentUser.get().isFiloha()) {
            return false;
        }
        Integer userFivondronanaId = getCurrentUserFivondronanaId();
        return userFivondronanaId != null
                && personneService.personneAppartientAFivondronana(personneId, userFivondronanaId);
    }

    private void addCommonAttributes(Model model) {
        model.addAttribute("userName", getCurrentUserName());
        model.addAttribute("isAdmin", hasAdminAccess());
        model.addAttribute("isStrictAdmin", isAdmin());
        model.addAttribute("canManagePersonnes", canManagePersonnes());
        getCurrentUser().ifPresent(user -> {
            model.addAttribute("currentUser", user);
            model.addAttribute("isFiloha", user.isFiloha());
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
        boolean admin = hasAdminAccess();
        int anneeCourante = dashboardService.getAnneeCourante();

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
            responsablesWithFafi = dashboardService.getResponsablesWithFafiByFivondronana(fivondronanaId);
            responsablesWithoutFafi = totalResponsables - responsablesWithFafi;

            totalEleves = personneService.countElevesByFivondronana(fivondronanaId);
            elevesWithFafi = dashboardService.getElevesWithFafiByFivondronana(fivondronanaId);
            elevesWithoutFafi = totalEleves - elevesWithFafi;
        }

        model.addAttribute("totalResponsables", totalResponsables);
        model.addAttribute("responsablesWithFafi", responsablesWithFafi);
        model.addAttribute("responsablesWithoutFafi", responsablesWithoutFafi);

        model.addAttribute("totalEleves", totalEleves);
        model.addAttribute("elevesWithFafi", elevesWithFafi);
        model.addAttribute("elevesWithoutFafi", elevesWithoutFafi);

        // FAFI Total stats (filtré par fivondronana pour USER)
        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
        String totalFafi;
        Long paidFafi;
        Long unpaidFafi;

        if (admin) {
            // Admin voit le total global
            totalFafi = formatter.format(dashboardService.getTotalFafiMontant()) + " Ar";
            paidFafi = dashboardService.getTotalPaidFafi();
            unpaidFafi = dashboardService.getTotalUnpaidFafi();
        } else {
            // USER voit seulement son fivondronana
            totalFafi = formatter.format(dashboardService.getTotalFafiMontantByFivondronana(fivondronanaId)) + " Ar";
            paidFafi = dashboardService.getTotalPaidFafiByFivondronana(fivondronanaId);
            unpaidFafi = dashboardService.getTotalUnpaidFafiByFivondronana(fivondronanaId, totalResponsables, totalEleves);
        }

        model.addAttribute("totalFafi", totalFafi);
        model.addAttribute("paidFafi", paidFafi);
        model.addAttribute("unpaidFafi", unpaidFafi);
        
        // Ajouter l'année courante et les prix FAFI
        model.addAttribute("anneeCourante", anneeCourante);
        model.addAttribute("prixMpiandraikitra", fafiService.getPrixMpiandraikitraAnneeActuelle());
        model.addAttribute("prixBeazina", fafiService.getPrixBeazinaAnneeActuelle());

        return "dashboard";
    }

    @GetMapping("/responsables")
    public String responsables(
            Model model,
            @RequestParam(required = false) Integer fivondronanaId,
            @RequestParam(required = false) Integer secteurId,
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) Integer fizaranaId,
            @RequestParam(required = false) Integer dingamPiofananaId,
            @RequestParam(required = false) Boolean hasFafi
    ) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Responsables");

        boolean admin = hasAdminAccess();
        Optional<Utilisateur> currentUser = getCurrentUser();
        boolean isFiloha = currentUser.isPresent() && currentUser.get().isFiloha();
        Integer userFivondronanaId = getCurrentUserFivondronanaId();

        List<Personne> responsables;

        if (admin || isFiloha) {
            // Admin et Filoha peuvent voir tous les responsables
            if (fivondronanaId != null || secteurId != null || andraikitraId != null || fizaranaId != null || dingamPiofananaId != null || hasFafi != null) {
                responsables = personneService.filterResponsables(fivondronanaId, secteurId, andraikitraId, fizaranaId, dingamPiofananaId, hasFafi);
            } else {
                responsables = personneService.findAllResponsables();
            }
            // Admin peut voir la liste des Fivondronana pour filtrer (Filoha non)
            if (admin) {
                model.addAttribute("fivondronana", personneService.findAllFivondronana());
            }
        } else {
            // Utilisateur Fivondronana voit seulement son Fivondronana
            if (secteurId != null || andraikitraId != null || fizaranaId != null || dingamPiofananaId != null || hasFafi != null) {
                responsables = personneService.filterResponsablesByFivondronana(userFivondronanaId, secteurId, andraikitraId, fizaranaId, dingamPiofananaId, hasFafi);
            } else {
                responsables = personneService.findResponsablesByFivondronana(userFivondronanaId);
            }
        }

        model.addAttribute("responsables", responsables);
        model.addAttribute("totalCount", responsables.size());

        // Reference data for filters and form
        model.addAttribute("secteurs", personneService.findAllSecteurs());
        model.addAttribute("andraikitra", personneService.findAllAndraikitra());
        model.addAttribute("fizarana", personneService.findAllFizarana());
        model.addAttribute("dingamPiofanana", personneService.findAllDingamPiofanana());
        model.addAttribute("fafiStatuts", personneService.findAllFafiStatuts());
        
        // Données FAFI
        model.addAttribute("anneeCourante", fafiService.getAnneeCourante());
        model.addAttribute("prixFafi", fafiService.getPrixMpiandraikitraAnneeActuelle());

        return "responsables";
    }

    @GetMapping("/responsables/details")
    public String detailsResponsable(
            @RequestParam Integer id,
            Model model
    ) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Détails Mpiandraikitra");

        // Charger la personne avec toutes ses relations
        Optional<Personne> personneOpt = personneService.findByIdWithAllRelations(id);
        
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            
            // Vérifier les permissions : non-admin ne peut voir que son Fivondronana
            if (!hasAdminAccess()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (personne.getFivondronana() == null || !personne.getFivondronana().getId().equals(userFivondronanaId)) {
                    model.addAttribute("errorMessage", "Tsy manan-kery ny fijerena ity Mpiandraikitra ity.");
                    return "details-responsable";
                }
            }
            
            // Vérifier que c'est bien un responsable
            if (!personne.isMpiandraikitra()) {
                model.addAttribute("errorMessage", "Ity olona ity dia tsy Mpiandraikitra.");
                return "details-responsable";
            }
            
            model.addAttribute("personne", personne);
            model.addAttribute("anneeCourante", fafiService.getAnneeCourante());
            // Ajouter la liste des types de formation pour le dropdown
            model.addAttribute("typeFiofanana", personneService.findAllTypeFiofanana());
            // Ajouter la liste des Fivondronana pour les dropdowns de la section C
            model.addAttribute("fivondronanaList", personneService.findAllFivondronana());
        } else {
            model.addAttribute("errorMessage", "Tsy hita ny Mpiandraikitra.");
        }

        return "details-responsable";
    }

    @PostMapping("/responsables/details/update-type-fiofanana")
    public String updateTypeFiofanana(
            @RequestParam Integer id,
            @RequestParam(required = false) Integer typeFiofananaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Vérifier les permissions : seulement ceux qui ont accès aux mpiandraikitra
            Optional<Personne> personneOpt = personneService.findByIdWithAllRelations(id);
            
            if (!personneOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Mpiandraikitra.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            Personne personne = personneOpt.get();
            
            // Vérifier que c'est bien un responsable
            if (!personne.isMpiandraikitra()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ity olona ity dia tsy Mpiandraikitra.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            // Vérifier les permissions : non-admin ne peut modifier que son Fivondronana
            if (!hasAdminAccess()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (personne.getFivondronana() == null || !personne.getFivondronana().getId().equals(userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity Mpiandraikitra ity.");
                    return "redirect:/responsables/details?id=" + id;
                }
            }
            
            // Mettre à jour le typeFiofanana
            // Utiliser findById simple pour avoir une entité gérée par Hibernate
            Optional<Personne> personneToUpdateOpt = personneService.findById(id);
            if (!personneToUpdateOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Mpiandraikitra.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            Personne personneToUpdate = personneToUpdateOpt.get();
            
            // Initialiser les types fiofanana s'ils n'existent pas dans la base
            personneService.findAllTypeFiofanana(); // Cette méthode initialise automatiquement si nécessaire
            
            if (typeFiofananaId != null && typeFiofananaId > 0) {
                Optional<TypeFiofanana> typeFiofananaOpt = personneService.findTypeFiofananaById(typeFiofananaId);
                if (typeFiofananaOpt.isPresent()) {
                    personneToUpdate.setTypeFiofanana(typeFiofananaOpt.get());
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny type fiofanana voafidy (ID: " + typeFiofananaId + ").");
                    return "redirect:/responsables/details?id=" + id;
                }
            } else {
                personneToUpdate.setTypeFiofanana(null);
            }
            
            // Sauvegarder avec flush pour forcer la persistance immédiate
            Personne savedPersonne = personneService.saveAndFlush(personneToUpdate);
            
            // Vérifier que la sauvegarde a bien fonctionné
            if (savedPersonne == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety rehefa nanova ny type fiofanana.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            // Message de succès en malgache
            if (typeFiofananaId != null) {
                personneService.findTypeFiofananaById(typeFiofananaId).ifPresent(type -> {
                    redirectAttributes.addFlashAttribute("successMessage", "Type fiofanana novaina soa! : " + type.getNom());
                });
            } else {
                redirectAttributes.addFlashAttribute("successMessage", "Type fiofanana voafafa soa!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety rehefa nanova ny type fiofanana: " + e.getMessage());
        }
        
        return "redirect:/responsables/details?id=" + id;
    }

    @PostMapping("/responsables/details/save-details-fiofanana")
    public String saveDetailsFiofanana(
            @RequestParam Integer id,
            @RequestParam(required = false) String asan1,
            @RequestParam(required = false) String asan2,
            @RequestParam(required = false) String asan3,
            @RequestParam(required = false) String asan4,
            @RequestParam(required = false) String asan5,
            @RequestParam(required = false) String asan6,
            @RequestParam(required = false) String asan7,
            @RequestParam(required = false) String asanFilohaNanome,
            @RequestParam(required = false) String ezaka1,
            @RequestParam(required = false) String ezaka2,
            @RequestParam(required = false) String ezaka3,
            @RequestParam(required = false) String ezaka4,
            @RequestParam(required = false) String ezaka5,
            @RequestParam(required = false) String ezaka6,
            @RequestParam(required = false) String ezaka7,
            @RequestParam(required = false) String ezakaFilohaNanome,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate bitsikyDaty1,
            @RequestParam(required = false) Integer bitsikyFivondronana1,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate bitsikyDaty2,
            @RequestParam(required = false) Integer bitsikyFivondronana2,
            @RequestParam(required = false) String dinikyTheme1,
            @RequestParam(required = false) String dinikyFiloha1,
            @RequestParam(required = false) String dinikyTheme2,
            @RequestParam(required = false) String dinikyFiloha2,
            @RequestParam(required = false) String dinikyTheme3,
            @RequestParam(required = false) String dinikyFiloha3,
            @RequestParam(required = false) String dinikyTheme4,
            @RequestParam(required = false) String dinikyFiloha4,
            @RequestParam(required = false) String dinikyTheme5,
            @RequestParam(required = false) String dinikyFiloha5,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate filasianaDaty,
            @RequestParam(required = false) String filasianaFiloha,
            @RequestParam(required = false) String lasyRavinala,
            @RequestParam(required = false) String soutenance,
            @RequestParam(required = false) String lasyNanoloranaTp2,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Vérifier les permissions
            Optional<Personne> personneOpt = personneService.findByIdWithAllRelations(id);
            
            if (!personneOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Mpiandraikitra.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            Personne personne = personneOpt.get();
            
            // Vérifier que c'est bien un responsable
            if (!personne.isMpiandraikitra()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ity olona ity dia tsy Mpiandraikitra.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            // Vérifier les permissions : non-admin ne peut modifier que son Fivondronana
            if (!hasAdminAccess()) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (personne.getFivondronana() == null || !personne.getFivondronana().getId().equals(userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity Mpiandraikitra ity.");
                    return "redirect:/responsables/details?id=" + id;
                }
            }
            
            // Vérifier que la personne a un typeFiofanana
            if (personne.getTypeFiofanana() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manana type fiofanana ity Mpiandraikitra ity.");
                return "redirect:/responsables/details?id=" + id;
            }
            
            Integer typeFiofananaId = personne.getTypeFiofanana().getId();
            
            // Créer ou mettre à jour DetailsFiofanana
            DetailsFiofanana details = new DetailsFiofanana();
            
            // Section A
            details.setAsan1(asan1 != null && !asan1.trim().isEmpty() ? asan1.trim() : null);
            details.setAsan2(asan2 != null && !asan2.trim().isEmpty() ? asan2.trim() : null);
            details.setAsan3(asan3 != null && !asan3.trim().isEmpty() ? asan3.trim() : null);
            details.setAsan4(asan4 != null && !asan4.trim().isEmpty() ? asan4.trim() : null);
            details.setAsan5(asan5 != null && !asan5.trim().isEmpty() ? asan5.trim() : null);
            details.setAsan6(asan6 != null && !asan6.trim().isEmpty() ? asan6.trim() : null);
            details.setAsan7(asan7 != null && !asan7.trim().isEmpty() ? asan7.trim() : null);
            details.setAsanFilohaNanome(asanFilohaNanome != null && !asanFilohaNanome.trim().isEmpty() ? asanFilohaNanome.trim() : null);
            
            // Section B
            details.setEzaka1(ezaka1 != null && !ezaka1.trim().isEmpty() ? ezaka1.trim() : null);
            details.setEzaka2(ezaka2 != null && !ezaka2.trim().isEmpty() ? ezaka2.trim() : null);
            details.setEzaka3(ezaka3 != null && !ezaka3.trim().isEmpty() ? ezaka3.trim() : null);
            details.setEzaka4(ezaka4 != null && !ezaka4.trim().isEmpty() ? ezaka4.trim() : null);
            details.setEzaka5(ezaka5 != null && !ezaka5.trim().isEmpty() ? ezaka5.trim() : null);
            details.setEzaka6(ezaka6 != null && !ezaka6.trim().isEmpty() ? ezaka6.trim() : null);
            details.setEzaka7(ezaka7 != null && !ezaka7.trim().isEmpty() ? ezaka7.trim() : null);
            details.setEzakaFilohaNanome(ezakaFilohaNanome != null && !ezakaFilohaNanome.trim().isEmpty() ? ezakaFilohaNanome.trim() : null);
            
            // Section C
            details.setBitsikyDaty1(bitsikyDaty1);
            if (bitsikyFivondronana1 != null && bitsikyFivondronana1 > 0) {
                personneService.findFivondronanaById(bitsikyFivondronana1).ifPresent(details::setBitsikyFivondronana1);
            }
            details.setBitsikyDaty2(bitsikyDaty2);
            if (bitsikyFivondronana2 != null && bitsikyFivondronana2 > 0) {
                personneService.findFivondronanaById(bitsikyFivondronana2).ifPresent(details::setBitsikyFivondronana2);
            }
            
            // Section D: Diniky ny filoha
            details.setDinikyTheme1(dinikyTheme1 != null && !dinikyTheme1.trim().isEmpty() ? dinikyTheme1.trim() : null);
            details.setDinikyFiloha1(dinikyFiloha1 != null && !dinikyFiloha1.trim().isEmpty() ? dinikyFiloha1.trim() : null);
            details.setDinikyTheme2(dinikyTheme2 != null && !dinikyTheme2.trim().isEmpty() ? dinikyTheme2.trim() : null);
            details.setDinikyFiloha2(dinikyFiloha2 != null && !dinikyFiloha2.trim().isEmpty() ? dinikyFiloha2.trim() : null);
            details.setDinikyTheme3(dinikyTheme3 != null && !dinikyTheme3.trim().isEmpty() ? dinikyTheme3.trim() : null);
            details.setDinikyFiloha3(dinikyFiloha3 != null && !dinikyFiloha3.trim().isEmpty() ? dinikyFiloha3.trim() : null);
            details.setDinikyTheme4(dinikyTheme4 != null && !dinikyTheme4.trim().isEmpty() ? dinikyTheme4.trim() : null);
            details.setDinikyFiloha4(dinikyFiloha4 != null && !dinikyFiloha4.trim().isEmpty() ? dinikyFiloha4.trim() : null);
            details.setDinikyTheme5(dinikyTheme5 != null && !dinikyTheme5.trim().isEmpty() ? dinikyTheme5.trim() : null);
            details.setDinikyFiloha5(dinikyFiloha5 != null && !dinikyFiloha5.trim().isEmpty() ? dinikyFiloha5.trim() : null);
            
            // Section E: Filasiana
            details.setFilasianaDaty(filasianaDaty);
            details.setFilasianaFiloha(filasianaFiloha != null && !filasianaFiloha.trim().isEmpty() ? filasianaFiloha.trim() : null);
            
            // Section F: Ravinala
            details.setLasyRavinala(lasyRavinala != null && !lasyRavinala.trim().isEmpty() ? lasyRavinala.trim() : null);
            details.setSoutenance(soutenance != null && !soutenance.trim().isEmpty() ? soutenance.trim() : null);
            
            // Section G: TP2
            details.setLasyNanoloranaTp2(lasyNanoloranaTp2 != null && !lasyNanoloranaTp2.trim().isEmpty() ? lasyNanoloranaTp2.trim() : null);
            
            // Sauvegarder avec le typeFiofanana actuel
            personneService.saveOrUpdateDetailsFiofanana(id, typeFiofananaId, details);
            
            redirectAttributes.addFlashAttribute("successMessage", "Ny antsipiriany dia voatahiry soa!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety rehefa nanatahiry ny antsipiriany: " + e.getMessage());
        }
        
        return "redirect:/responsables/details?id=" + id;
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
            @RequestParam(required = false) Integer fizaranaId,
            @RequestParam(required = false) Integer fivondronanaId,
            @RequestParam(required = false) Integer dingamPiofananaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Vérifier les permissions : Filoha ne peut pas ajouter
            Optional<Utilisateur> currentUser = getCurrentUser();
            if (currentUser.isPresent() && currentUser.get().isFiloha()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanampiana Mpiandraikitra.");
                return "redirect:/responsables";
            }
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
            if (hasAdminAccess()) {
                effectiveFivondronanaId = fivondronanaId;
            } else {
                effectiveFivondronanaId = getCurrentUserFivondronanaId();
            }

            personneService.createResponsable(personne, secteurId, andraikitraId, fizaranaId, effectiveFivondronanaId, dingamPiofananaId);
            
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
            @RequestParam(required = false) Integer fizaranaId,
            @RequestParam(required = false) Integer dingamPiofananaId,
            @RequestParam(required = false) String numeroFafi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (!canManagePersonne(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity olona ity.");
                return "redirect:/responsables";
            }

            personneService.updateResponsable(
                    id, nom, prenom, totem, dateNaissance, numeroTelephone, numeroCin,
                    nomPere, nomMere, dateFanekena, secteurId, andraikitraId, fizaranaId,
                    dingamPiofananaId, numeroFafi
            );
            
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
            if (!canManagePersonne(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny famafana ity olona ity.");
                return "redirect:/responsables";
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

        boolean admin = hasAdminAccess();
        Optional<Utilisateur> currentUser = getCurrentUser();
        boolean isFiloha = currentUser.isPresent() && currentUser.get().isFiloha();
        Integer userFivondronanaId = getCurrentUserFivondronanaId();

        List<Personne> eleves;

        if (admin || isFiloha) {
            // Admin et Filoha peuvent voir tous les eleves
            if (fivondronanaId != null || secteurId != null || fizaranaId != null || ambaratonga != null || hasFafi != null) {
                eleves = personneService.filterEleves(fivondronanaId, secteurId, fizaranaId, ambaratonga, hasFafi);
            } else {
                eleves = personneService.findAllEleves();
            }
            // Admin peut voir la liste des Fivondronana pour filtrer (Filoha non)
            if (admin) {
                model.addAttribute("fivondronana", personneService.findAllFivondronana());
            }
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
        
        // Données FAFI
        model.addAttribute("anneeCourante", fafiService.getAnneeCourante());
        model.addAttribute("prixFafi", fafiService.getPrixBeazinaAnneeActuelle());

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
            // Vérifier les permissions : Filoha ne peut pas ajouter
            Optional<Utilisateur> currentUser = getCurrentUser();
            if (currentUser.isPresent() && currentUser.get().isFiloha()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanampiana Beazina.");
                return "redirect:/eleves";
            }
            
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
            if (hasAdminAccess()) {
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
            @RequestParam(required = false) String numeroFafi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (!canManagePersonne(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity olona ity.");
                return "redirect:/eleves";
            }

            personneService.updateEleve(
                    id, nom, prenom, totem, dateNaissance, ambaratonga,
                    nomPere, nomMere, dateFanekena, secteurId, fizaranaId, numeroFafi
            );
            
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
            if (!canManagePersonne(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny famafana ity olona ity.");
                return "redirect:/eleves";
            }

            personneService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Élève supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression : " + e.getMessage());
        }
        
        return "redirect:/eleves";
    }

    // Endpoint pour modifier le FAFI d'un élève (admin/dfaf ou utilisateur du même Fivondronana)
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
            boolean admin = hasAdminAccess();
            // Vérifier les permissions
            if (!admin) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(personneId, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier le FAFI de cette personne");
                    return "redirect:/eleves";
                }
                // USER ne peut pas modifier le statut - ignorer le paramètre statut
                statut = null;
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
            boolean admin = hasAdminAccess();
            // Vérifier les permissions
            if (!admin) {
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                if (!personneService.personneAppartientAFivondronana(personneId, userFivondronanaId)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier le FAFI de cette personne");
                    return "redirect:/responsables";
                }
                // USER ne peut pas modifier le statut - ignorer le paramètre statut
                statut = null;
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
            Integer fivondronanaId = hasAdminAccess() ? null : getCurrentUserFivondronanaId();
            
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
            Integer fivondronanaId = hasAdminAccess() ? null : getCurrentUserFivondronanaId();
            
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
        // Liste des Filoha sans compte pour créer des comptes
        List<Personne> filohaSansCompte = personneService.findAllFiloha().stream()
            .filter(f -> {
                // Vérifier si ce Filoha a déjà un compte
                return authService.findAllNonAdminUsers().stream()
                    .noneMatch(u -> u.getPersonne() != null && u.getPersonne().getId().equals(f.getId()));
            })
            .toList();
        model.addAttribute("filohaSansCompte", filohaSansCompte);

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

    @PostMapping("/admin/utilisateurs/ajouter-filoha")
    public String ajouterUtilisateurFiloha(
            @RequestParam String email,
            @RequestParam String motDePasse,
            @RequestParam Integer personneId,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdmin()) {
            return "redirect:/access-denied";
        }

        try {
            authService.creerCompteFiloha(email, motDePasse, personneId);
            redirectAttributes.addFlashAttribute("successMessage", "Compte Filoha créé avec succès !");
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

    // ========== ADMIN/DFAF: Configuration des prix FAFI ==========

    @GetMapping("/admin/configuration-fafi")
    public String configurationFafi(Model model) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Configuration FAFI");
        
        int anneeCourante = fafiService.getAnneeCourante();
        model.addAttribute("anneeCourante", anneeCourante);
        model.addAttribute("prixMpiandraikitra", fafiService.getPrixMpiandraikitraAnneeActuelle());
        model.addAttribute("prixBeazina", fafiService.getPrixBeazinaAnneeActuelle());
        
        // Historique des prix
        List<PrixFafi> historiquePrix = fafiService.getAllPrixFafi();
        model.addAttribute("historiquePrix", historiquePrix);
        
        // Liste des années distinctes
        java.util.Set<Integer> anneesSet = new java.util.TreeSet<>(java.util.Collections.reverseOrder());
        historiquePrix.forEach(p -> anneesSet.add(p.getAnnee()));
        model.addAttribute("anneesAvecPrix", anneesSet);

        return "admin/configuration-fafi";
    }

    @PostMapping("/admin/configuration-fafi/modifier")
    public String modifierPrixFafi(
            @RequestParam String typePersonne,
            @RequestParam BigDecimal prix,
            RedirectAttributes redirectAttributes
    ) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        try {
            int anneeCourante = fafiService.getAnneeCourante();
            fafiService.savePrixFafi(typePersonne, prix, anneeCourante);
            redirectAttributes.addFlashAttribute("successMessage", "Vidin'ny FAFI novaina soa amin'ny " + typePersonne + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/admin/configuration-fafi";
    }

    @PostMapping("/admin/configuration-fafi/ajouter-annee")
    public String ajouterPrixFafiAnnee(
            @RequestParam Integer annee,
            @RequestParam BigDecimal prixMpiandraikitra,
            @RequestParam BigDecimal prixBeazina,
            RedirectAttributes redirectAttributes
    ) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        try {
            fafiService.savePrixFafi("Mpiandraikitra", prixMpiandraikitra, annee);
            fafiService.savePrixFafi("Beazina", prixBeazina, annee);
            redirectAttributes.addFlashAttribute("successMessage", "Vidin'ny FAFI ho an'ny taona " + annee + " tafiditra soa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/admin/configuration-fafi";
    }

    // ========== Paiement FAFI multiple ==========

    @PostMapping("/responsables/payer-fafi-multiple")
    public String payerFafiMultipleResponsables(
            @RequestParam("personneIds") List<Integer> personneIds,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (!hasAdminAccess()) {
                // Vérifier que toutes les personnes appartiennent au même fivondronana
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                for (Integer personneId : personneIds) {
                    if (!personneService.personneAppartientAFivondronana(personneId, userFivondronanaId)) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier le FAFI de certaines personnes");
                        return "redirect:/responsables";
                    }
                }
            }

            int count = fafiService.marquerFafiPayePourAnnee(personneIds, "Mpiandraikitra");
            redirectAttributes.addFlashAttribute("successMessage", count + " Mpiandraikitra efa nandoa FAFI amin'ity taona ity!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/responsables";
    }

    @PostMapping("/eleves/payer-fafi-multiple")
    public String payerFafiMultipleEleves(
            @RequestParam("personneIds") List<Integer> personneIds,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (!hasAdminAccess()) {
                // Vérifier que toutes les personnes appartiennent au même fivondronana
                Integer userFivondronanaId = getCurrentUserFivondronanaId();
                for (Integer personneId : personneIds) {
                    if (!personneService.personneAppartientAFivondronana(personneId, userFivondronanaId)) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Vous n'avez pas la permission de modifier le FAFI de certaines personnes");
                        return "redirect:/eleves";
                    }
                }
            }

            int count = fafiService.marquerFafiPayePourAnnee(personneIds, "Beazina");
            redirectAttributes.addFlashAttribute("successMessage", count + " Beazina efa nandoa FAFI amin'ity taona ity!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/eleves";
    }

    // ========== FILOHA ==========

    @GetMapping("/filoha")
    public String filoha(
            Model model,
            @RequestParam(required = false) Integer typeFilohaId,
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) Integer dingamPiofananaId,
            @RequestParam(required = false) Integer typeFiofananaId,
            @RequestParam(required = false) Boolean hasFafi
    ) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Filoha");

        List<Personne> filoha;

        if (hasAdminAccess()) {
            // Admin peut filtrer ou voir tout
            if (typeFilohaId != null || andraikitraId != null || dingamPiofananaId != null || typeFiofananaId != null || hasFafi != null) {
                filoha = personneService.filterFiloha(typeFilohaId, andraikitraId, dingamPiofananaId, typeFiofananaId, hasFafi);
            } else {
                filoha = personneService.findAllFiloha();
            }
        } else {
            // Les Filoha connectés voient seulement leur propre profil
            Optional<Utilisateur> currentUser = getCurrentUser();
            if (currentUser.isPresent() && currentUser.get().isFiloha() && currentUser.get().getPersonne() != null) {
                filoha = java.util.Collections.singletonList(currentUser.get().getPersonne());
            } else {
                filoha = java.util.Collections.emptyList();
            }
        }

        model.addAttribute("filoha", filoha);
        model.addAttribute("totalCount", filoha.size());

        // Reference data for filters and form
        model.addAttribute("typeFiloha", personneService.findAllTypeFiloha());
        model.addAttribute("andraikitra", personneService.findAllAndraikitra());
        model.addAttribute("dingamPiofanana", personneService.findAllDingamPiofanana());
        model.addAttribute("typeFiofanana", personneService.findAllTypeFiofanana());
        model.addAttribute("fafiStatuts", personneService.findAllFafiStatuts());
        
        // Données FAFI
        model.addAttribute("anneeCourante", fafiService.getAnneeCourante());
        model.addAttribute("prixFafi", fafiService.getPrixMpiandraikitraAnneeActuelle());

        return "filoha";
    }

    @GetMapping("/filoha/details")
    public String detailsFiloha(
            @RequestParam Integer id,
            Model model
    ) {
        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Détails Filoha");

        // Charger la personne avec toutes ses relations
        Optional<Personne> personneOpt = personneService.findByIdWithAllRelations(id);
        
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            
            // Vérifier que c'est bien un Filoha
            if (!personne.isFiloha()) {
                model.addAttribute("errorMessage", "Ity olona ity dia tsy Filoha.");
                return "details-filoha";
            }
            
            // Vérifier les permissions : non-admin Filoha ne peut voir que son propre profil
            if (!hasAdminAccess()) {
                Optional<Utilisateur> currentUser = getCurrentUser();
                if (!currentUser.isPresent() || !currentUser.get().isFiloha() || 
                    currentUser.get().getPersonne() == null || 
                    !currentUser.get().getPersonne().getId().equals(id)) {
                    model.addAttribute("errorMessage", "Tsy manan-kery ny fijerena ity Filoha ity.");
                    return "details-filoha";
                }
            }
            
            model.addAttribute("personne", personne);
            model.addAttribute("anneeCourante", fafiService.getAnneeCourante());
            model.addAttribute("typeFiofanana", personneService.findAllTypeFiofanana());
            model.addAttribute("fivondronanaList", personneService.findAllFivondronana());
        } else {
            model.addAttribute("errorMessage", "Tsy hita ny Filoha.");
        }

        return "details-filoha";
    }

    @PostMapping("/filoha/details/update-type-fiofanana")
    public String updateTypeFiofananaFiloha(
            @RequestParam Integer id,
            @RequestParam(required = false) Integer typeFiofananaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<Personne> personneOpt = personneService.findByIdWithAllRelations(id);
            if (!personneOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Filoha.");
                return "redirect:/filoha/details?id=" + id;
            }
            Personne personne = personneOpt.get();

            if (!personne.isFiloha()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ity olona ity dia tsy Filoha.");
                return "redirect:/filoha/details?id=" + id;
            }

            // Vérifier les permissions
            if (!hasAdminAccess()) {
                Optional<Utilisateur> currentUser = getCurrentUser();
                if (!currentUser.isPresent() || !currentUser.get().isFiloha() || 
                    currentUser.get().getPersonne() == null || 
                    !currentUser.get().getPersonne().getId().equals(id)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity Filoha ity.");
                    return "redirect:/filoha/details?id=" + id;
                }
            }

            if (typeFiofananaId != null && typeFiofananaId > 0) {
                Optional<TypeFiofanana> typeFiofananaOpt = personneService.findTypeFiofananaById(typeFiofananaId);
                if (typeFiofananaOpt.isPresent()) {
                    personne.setTypeFiofanana(typeFiofananaOpt.get());
                    personneService.saveAndFlush(personne);
                    redirectAttributes.addFlashAttribute("successMessage", "Type fiofanana voatahiry soa!");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny type fiofanana.");
                }
            } else {
                personne.setTypeFiofanana(null);
                personneService.saveAndFlush(personne);
                redirectAttributes.addFlashAttribute("successMessage", "Type fiofanana voafafa soa!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }
        return "redirect:/filoha/details?id=" + id;
    }

    @PostMapping("/filoha/details/save-details-fiofanana")
    public String saveDetailsFiofananaFiloha(
            @RequestParam Integer id,
            @RequestParam(required = false) String asan1,
            @RequestParam(required = false) String asan2,
            @RequestParam(required = false) String asan3,
            @RequestParam(required = false) String asan4,
            @RequestParam(required = false) String asan5,
            @RequestParam(required = false) String asan6,
            @RequestParam(required = false) String asan7,
            @RequestParam(required = false) String asanFilohaNanome,
            @RequestParam(required = false) String ezaka1,
            @RequestParam(required = false) String ezaka2,
            @RequestParam(required = false) String ezaka3,
            @RequestParam(required = false) String ezaka4,
            @RequestParam(required = false) String ezaka5,
            @RequestParam(required = false) String ezaka6,
            @RequestParam(required = false) String ezaka7,
            @RequestParam(required = false) String ezakaFilohaNanome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bitsikyDaty1,
            @RequestParam(required = false) Integer bitsikyFivondronana1,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bitsikyDaty2,
            @RequestParam(required = false) Integer bitsikyFivondronana2,
            @RequestParam(required = false) String dinikyTheme1,
            @RequestParam(required = false) String dinikyFiloha1,
            @RequestParam(required = false) String dinikyTheme2,
            @RequestParam(required = false) String dinikyFiloha2,
            @RequestParam(required = false) String dinikyTheme3,
            @RequestParam(required = false) String dinikyFiloha3,
            @RequestParam(required = false) String dinikyTheme4,
            @RequestParam(required = false) String dinikyFiloha4,
            @RequestParam(required = false) String dinikyTheme5,
            @RequestParam(required = false) String dinikyFiloha5,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filasianaDaty,
            @RequestParam(required = false) String filasianaFiloha,
            @RequestParam(required = false) String lasyRavinala,
            @RequestParam(required = false) String soutenance,
            @RequestParam(required = false) String lasyNanoloranaTp2,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Optional<Personne> personneOpt = personneService.findByIdWithAllRelations(id);
            if (!personneOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Filoha.");
                return "redirect:/filoha/details?id=" + id;
            }
            Personne personne = personneOpt.get();

            if (!personne.isFiloha()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ity olona ity dia tsy Filoha.");
                return "redirect:/filoha/details?id=" + id;
            }

            // Vérifier les permissions
            if (!hasAdminAccess()) {
                Optional<Utilisateur> currentUser = getCurrentUser();
                if (!currentUser.isPresent() || !currentUser.get().isFiloha() || 
                    currentUser.get().getPersonne() == null || 
                    !currentUser.get().getPersonne().getId().equals(id)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity Filoha ity.");
                    return "redirect:/filoha/details?id=" + id;
                }
            }

            // Ensure the person has a typeFiofanana before saving details
            if (personne.getTypeFiofanana() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mifidy 'Type fiofanana' aloha vao manatahiry ny antsipiriany.");
                return "redirect:/filoha/details?id=" + id;
            }

            DetailsFiofanana details = new DetailsFiofanana();
            details.setAsan1(asan1 != null && !asan1.trim().isEmpty() ? asan1.trim() : null);
            details.setAsan2(asan2 != null && !asan2.trim().isEmpty() ? asan2.trim() : null);
            details.setAsan3(asan3 != null && !asan3.trim().isEmpty() ? asan3.trim() : null);
            details.setAsan4(asan4 != null && !asan4.trim().isEmpty() ? asan4.trim() : null);
            details.setAsan5(asan5 != null && !asan5.trim().isEmpty() ? asan5.trim() : null);
            details.setAsan6(asan6 != null && !asan6.trim().isEmpty() ? asan6.trim() : null);
            details.setAsan7(asan7 != null && !asan7.trim().isEmpty() ? asan7.trim() : null);
            details.setAsanFilohaNanome(asanFilohaNanome != null && !asanFilohaNanome.trim().isEmpty() ? asanFilohaNanome.trim() : null);
            details.setEzaka1(ezaka1 != null && !ezaka1.trim().isEmpty() ? ezaka1.trim() : null);
            details.setEzaka2(ezaka2 != null && !ezaka2.trim().isEmpty() ? ezaka2.trim() : null);
            details.setEzaka3(ezaka3 != null && !ezaka3.trim().isEmpty() ? ezaka3.trim() : null);
            details.setEzaka4(ezaka4 != null && !ezaka4.trim().isEmpty() ? ezaka4.trim() : null);
            details.setEzaka5(ezaka5 != null && !ezaka5.trim().isEmpty() ? ezaka5.trim() : null);
            details.setEzaka6(ezaka6 != null && !ezaka6.trim().isEmpty() ? ezaka6.trim() : null);
            details.setEzaka7(ezaka7 != null && !ezaka7.trim().isEmpty() ? ezaka7.trim() : null);
            details.setEzakaFilohaNanome(ezakaFilohaNanome != null && !ezakaFilohaNanome.trim().isEmpty() ? ezakaFilohaNanome.trim() : null);
            details.setBitsikyDaty1(bitsikyDaty1);
            if (bitsikyFivondronana1 != null && bitsikyFivondronana1 > 0) {
                personneService.findFivondronanaById(bitsikyFivondronana1).ifPresent(details::setBitsikyFivondronana1);
            }
            details.setBitsikyDaty2(bitsikyDaty2);
            if (bitsikyFivondronana2 != null && bitsikyFivondronana2 > 0) {
                personneService.findFivondronanaById(bitsikyFivondronana2).ifPresent(details::setBitsikyFivondronana2);
            }
            
            // Section D: Diniky ny filoha
            details.setDinikyTheme1(dinikyTheme1 != null && !dinikyTheme1.trim().isEmpty() ? dinikyTheme1.trim() : null);
            details.setDinikyFiloha1(dinikyFiloha1 != null && !dinikyFiloha1.trim().isEmpty() ? dinikyFiloha1.trim() : null);
            details.setDinikyTheme2(dinikyTheme2 != null && !dinikyTheme2.trim().isEmpty() ? dinikyTheme2.trim() : null);
            details.setDinikyFiloha2(dinikyFiloha2 != null && !dinikyFiloha2.trim().isEmpty() ? dinikyFiloha2.trim() : null);
            details.setDinikyTheme3(dinikyTheme3 != null && !dinikyTheme3.trim().isEmpty() ? dinikyTheme3.trim() : null);
            details.setDinikyFiloha3(dinikyFiloha3 != null && !dinikyFiloha3.trim().isEmpty() ? dinikyFiloha3.trim() : null);
            details.setDinikyTheme4(dinikyTheme4 != null && !dinikyTheme4.trim().isEmpty() ? dinikyTheme4.trim() : null);
            details.setDinikyFiloha4(dinikyFiloha4 != null && !dinikyFiloha4.trim().isEmpty() ? dinikyFiloha4.trim() : null);
            details.setDinikyTheme5(dinikyTheme5 != null && !dinikyTheme5.trim().isEmpty() ? dinikyTheme5.trim() : null);
            details.setDinikyFiloha5(dinikyFiloha5 != null && !dinikyFiloha5.trim().isEmpty() ? dinikyFiloha5.trim() : null);
            
            // Section E: Filasiana
            details.setFilasianaDaty(filasianaDaty);
            details.setFilasianaFiloha(filasianaFiloha != null && !filasianaFiloha.trim().isEmpty() ? filasianaFiloha.trim() : null);
            
            // Section F: Ravinala
            details.setLasyRavinala(lasyRavinala != null && !lasyRavinala.trim().isEmpty() ? lasyRavinala.trim() : null);
            details.setSoutenance(soutenance != null && !soutenance.trim().isEmpty() ? soutenance.trim() : null);
            
            // Section G: TP2
            details.setLasyNanoloranaTp2(lasyNanoloranaTp2 != null && !lasyNanoloranaTp2.trim().isEmpty() ? lasyNanoloranaTp2.trim() : null);
            
            // Set the current typeFiofanana to the details object
            details.setTypeFiofanana(personne.getTypeFiofanana());

            personneService.saveOrUpdateDetailsFiofanana(id, personne.getTypeFiofanana().getId(), details);

            redirectAttributes.addFlashAttribute("successMessage", "Ny antsipiriany dia voatahiry soa!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety rehefa nanatahiry ny antsipiriany: " + e.getMessage());
        }
        return "redirect:/filoha/details?id=" + id;
    }

    // ========== CONFIGURATION RÔLES FILOHA ==========

    @GetMapping("/admin/configuration-filoha")
    public String configurationFiloha(Model model) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        addCommonAttributes(model);
        model.addAttribute("pageTitle", "Configuration Rôles Filoha");
        
        List<TypeFiloha> typeFilohaList = personneService.findAllTypeFiloha();
        model.addAttribute("typeFilohaList", typeFilohaList);
        
        return "admin/configuration-filoha";
    }

    @PostMapping("/admin/configuration-filoha/ajouter")
    public String ajouterTypeFiloha(
            @RequestParam String nom,
            RedirectAttributes redirectAttributes
    ) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        try {
            // Vérifier si le nom existe déjà
            if (personneService.findTypeFilohaByNom(nom).isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ity anarana ity dia efa misy: " + nom);
                return "redirect:/admin/configuration-filoha";
            }

            TypeFiloha typeFiloha = new TypeFiloha(nom);
            personneService.saveTypeFiloha(typeFiloha);
            
            redirectAttributes.addFlashAttribute("successMessage", "Type Filoha voatahiry soa: " + nom);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/admin/configuration-filoha";
    }

    @PostMapping("/admin/configuration-filoha/modifier")
    public String modifierTypeFiloha(
            @RequestParam Integer id,
            @RequestParam String nom,
            RedirectAttributes redirectAttributes
    ) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        try {
            Optional<TypeFiloha> typeFilohaOpt = personneService.findTypeFilohaById(id);
            if (!typeFilohaOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Type Filoha.");
                return "redirect:/admin/configuration-filoha";
            }

            // Vérifier si le nom existe déjà pour un autre type
            Optional<TypeFiloha> existingOpt = personneService.findTypeFilohaByNom(nom);
            if (existingOpt.isPresent() && !existingOpt.get().getId().equals(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ity anarana ity dia efa misy: " + nom);
                return "redirect:/admin/configuration-filoha";
            }

            TypeFiloha typeFiloha = typeFilohaOpt.get();
            typeFiloha.setNom(nom);
            personneService.saveTypeFiloha(typeFiloha);
            
            redirectAttributes.addFlashAttribute("successMessage", "Type Filoha novaina soa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/admin/configuration-filoha";
    }

    @PostMapping("/admin/configuration-filoha/supprimer")
    public String supprimerTypeFiloha(
            @RequestParam Integer id,
            RedirectAttributes redirectAttributes
    ) {
        if (!hasAdminAccess()) {
            return "redirect:/access-denied";
        }

        try {
            Optional<TypeFiloha> typeFilohaOpt = personneService.findTypeFilohaById(id);
            if (!typeFilohaOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Type Filoha.");
                return "redirect:/admin/configuration-filoha";
            }

            // Vérifier si des personnes utilisent ce type
            List<Personne> personnesAvecType = personneService.findAllFiloha().stream()
                .filter(p -> p.getTypeFiloha() != null && p.getTypeFiloha().getId().equals(id))
                .toList();
            
            if (!personnesAvecType.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Tsy azo afafa ity Type Filoha ity satria misy Filoha " + personnesAvecType.size() + " mampiasa azy.");
                return "redirect:/admin/configuration-filoha";
            }

            personneService.deleteTypeFiloha(id);
            
            redirectAttributes.addFlashAttribute("successMessage", "Type Filoha voafafa soa!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety: " + e.getMessage());
        }

        return "redirect:/admin/configuration-filoha";
    }

    @PostMapping("/filoha/ajouter")
    public String ajouterFiloha(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam(required = false) String totem,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String numeroTelephone,
            @RequestParam(required = false) String numeroCin,
            @RequestParam(required = false) String nomPere,
            @RequestParam(required = false) String nomMere,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFanekena,
            @RequestParam(required = false) Integer typeFilohaId,
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) Integer dingamPiofananaId,
            @RequestParam(required = false) Integer typeFiofananaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Seuls les admin peuvent ajouter des Filoha
            if (!hasAdminAccess()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanampiana Filoha.");
                return "redirect:/filoha";
            }

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

            personneService.createFiloha(personne, typeFilohaId, andraikitraId, dingamPiofananaId, typeFiofananaId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Tafiditra Filoha !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety :" + e.getMessage());
        }
        
        return "redirect:/filoha";
    }

    @PostMapping("/filoha/modifier")
    public String modifierFiloha(
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
            @RequestParam(required = false) Integer typeFilohaId,
            @RequestParam(required = false) Integer andraikitraId,
            @RequestParam(required = false) Integer dingamPiofananaId,
            @RequestParam(required = false) Integer typeFiofananaId,
            @RequestParam(required = false) String numeroFafi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Vérifier les permissions
            if (!hasAdminAccess()) {
                Optional<Utilisateur> currentUser = getCurrentUser();
                if (!currentUser.isPresent() || !currentUser.get().isFiloha() || 
                    currentUser.get().getPersonne() == null || 
                    !currentUser.get().getPersonne().getId().equals(id)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny fanovana ity Filoha ity.");
                    return "redirect:/filoha";
                }
            }

            personneService.updateFiloha(
                    id, nom, prenom, totem, dateNaissance, numeroTelephone, numeroCin,
                    nomPere, nomMere, dateFanekena, typeFilohaId, andraikitraId,
                    dingamPiofananaId, typeFiofananaId, numeroFafi
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "Voatahiry soa ny Filoha !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety : " + e.getMessage());
        }
        
        return "redirect:/filoha";
    }

    @PostMapping("/filoha/supprimer")
    public String supprimerFiloha(
            @RequestParam Integer id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Seuls les admin peuvent supprimer des Filoha
            if (!hasAdminAccess()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy manan-kery ny famafana Filoha.");
                return "redirect:/filoha";
            }

            Optional<Personne> personneOpt = personneService.findById(id);
            if (personneOpt.isPresent() && personneOpt.get().isFiloha()) {
                personneService.delete(id);
                redirectAttributes.addFlashAttribute("successMessage", "Voafafa soa ny Filoha !");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Tsy hita ny Filoha.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nisy tsy nety : " + e.getMessage());
        }
        
        return "redirect:/filoha";
    }
}
