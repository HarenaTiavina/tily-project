package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tily.mg.entity.*;
import tily.mg.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonneService {

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private TypePersonneRepository typePersonneRepository;

    @Autowired
    private SecteurRepository secteurRepository;

    @Autowired
    private FizaranaRepository fizaranaRepository;

    @Autowired
    private AndraikitraRepository andraikitraRepository;

    @Autowired
    private FafiRepository fafiRepository;

    @Autowired
    private FivondronanaRepository fivondronanaRepository;

    @Autowired
    private DingamPiofananaRepository dingamPiofananaRepository;
    
    @Autowired
    private TypeFiofananaRepository typeFiofananaRepository;
    
    @Autowired
    private DetailsFiofananaRepository detailsFiofananaRepository;
    
    @Autowired
    private TypeFilohaRepository typeFilohaRepository;

    @Autowired
    private FafiService fafiService;
 
    // CRUD Operations
    public List<Personne> findAll() {
        return personneRepository.findAll();
    }

    public Optional<Personne> findById(Integer id) {
        return personneRepository.findById(id);
    }
    
    public Optional<Personne> findByIdWithAllRelations(Integer id) {
        Optional<Personne> personneOpt = personneRepository.findByIdWithAllRelations(id);
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            // Recharger les détails avec leurs relations pour éviter les problèmes de lazy loading
            if (personne.getId() != null) {
                List<DetailsFiofanana> detailsList = detailsFiofananaRepository.findAllByPersonneId(personne.getId());
                personne.setDetailsFiofananaList(detailsList);
            }
        }
        return personneOpt;
    }

    public Personne save(Personne personne) {
        return personneRepository.save(personne);
    }
    
    public Personne saveAndFlush(Personne personne) {
        return personneRepository.saveAndFlush(personne);
    }

    public void delete(Integer id) {
        personneRepository.deleteById(id);
    }

    // ========== ADMIN: Toutes les personnes ==========
    
    public List<Personne> findAllResponsables() {
        return personneRepository.findAllResponsables();
    }

    public List<Personne> findAllEleves() {
        return personneRepository.findAllEleves();
    }

    public List<Personne> findAllFiloha() {
        return personneRepository.findAllFiloha();
    }

    public Long countResponsables() {
        return personneRepository.countByTypePersonneNom("Responsable");
    }

    public Long countEleves() {
        return personneRepository.countByTypePersonneNom("Eleve");
    }

    public Long countResponsablesWithFafi() {
        return personneRepository.countResponsablesWithFafi();
    }

    public Long countElevesWithFafi() {
        return personneRepository.countElevesWithFafi();
    }

    // Filter pour admin (fivondronanaId optionnel)
    public List<Personne> filterResponsables(Integer fivondronanaId, Integer secteurId, Integer andraikitraId, Integer fizaranaId, Integer dingamPiofananaId, Boolean hasFafi) {
        return personneRepository.filterResponsables(fivondronanaId, secteurId, andraikitraId, fizaranaId, dingamPiofananaId, hasFafi);
    }

    public List<Personne> filterEleves(Integer fivondronanaId, Integer secteurId, Integer fizaranaId, String ambaratonga, Boolean hasFafi) {
        return personneRepository.filterEleves(fivondronanaId, secteurId, fizaranaId, ambaratonga, hasFafi);
    }

    public List<Personne> filterFiloha(Integer typeFilohaId, Integer andraikitraId, Integer dingamPiofananaId, Integer typeFiofananaId, Boolean hasFafi) {
        return personneRepository.filterFiloha(typeFilohaId, andraikitraId, dingamPiofananaId, typeFiofananaId, hasFafi);
    }

    // ========== FIVONDRONANA: Personnes par Fivondronana ==========
    
    public List<Personne> findResponsablesByFivondronana(Integer fivondronanaId) {
        return personneRepository.findResponsablesByFivondronana(fivondronanaId);
    }

    public List<Personne> findElevesByFivondronana(Integer fivondronanaId) {
        return personneRepository.findElevesByFivondronana(fivondronanaId);
    }

    public Long countResponsablesByFivondronana(Integer fivondronanaId) {
        return personneRepository.countByTypePersonneNomAndFivondronana("Responsable", fivondronanaId);
    }

    public Long countElevesByFivondronana(Integer fivondronanaId) {
        return personneRepository.countByTypePersonneNomAndFivondronana("Eleve", fivondronanaId);
    }

    public Long countResponsablesWithFafiByFivondronana(Integer fivondronanaId) {
        return personneRepository.countResponsablesWithFafiByFivondronana(fivondronanaId);
    }

    public Long countElevesWithFafiByFivondronana(Integer fivondronanaId) {
        return personneRepository.countElevesWithFafiByFivondronana(fivondronanaId);
    }

    // Filter par Fivondronana (pour non-admin)
    public List<Personne> filterResponsablesByFivondronana(Integer fivondronanaId, Integer secteurId, Integer andraikitraId, Integer fizaranaId, Integer dingamPiofananaId, Boolean hasFafi) {
        return personneRepository.filterResponsablesByFivondronana(fivondronanaId, secteurId, andraikitraId, fizaranaId, dingamPiofananaId, hasFafi);
    }

    public List<Personne> filterElevesByFivondronana(Integer fivondronanaId, Integer secteurId, Integer fizaranaId, String ambaratonga, Boolean hasFafi) {
        return personneRepository.filterElevesByFivondronana(fivondronanaId, secteurId, fizaranaId, ambaratonga, hasFafi);
    }

    // Reference data
    public List<TypePersonne> findAllTypePersonnes() {
        return typePersonneRepository.findAll();
    }

    public List<Secteur> findAllSecteurs() {
        return secteurRepository.findAll();
    }

    public List<Fizarana> findAllFizarana() {
        return fizaranaRepository.findAll();
    }

    public List<Andraikitra> findAllAndraikitra() {
        return andraikitraRepository.findAll();
    }

    public List<Fivondronana> findAllFivondronana() {
        return fivondronanaRepository.findAll();
    }

    public List<DingamPiofanana> findAllDingamPiofanana() {
        return dingamPiofananaRepository.findAll();
    }

    public List<TypeFiloha> findAllTypeFiloha() {
        return typeFilohaRepository.findAllByOrderByNomAsc();
    }

    public Optional<TypeFiloha> findTypeFilohaById(Integer id) {
        return typeFilohaRepository.findById(id);
    }

    public Optional<TypeFiloha> findTypeFilohaByNom(String nom) {
        return typeFilohaRepository.findByNom(nom);
    }

    public TypeFiloha saveTypeFiloha(TypeFiloha typeFiloha) {
        return typeFilohaRepository.save(typeFiloha);
    }

    public void deleteTypeFiloha(Integer id) {
        typeFilohaRepository.deleteById(id);
    }

    // Statistics
    public BigDecimal getTotalFafiMontant() {
        BigDecimal total = fafiRepository.getTotalMontantActive();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long countWithFafi() {
        Long count = fafiRepository.countActive();
        return count != null ? count : 0L;
    }

    // Search
    public List<Personne> search(String query) {
        return personneRepository.search(query);
    }

    // Find reference entities by ID
    public Optional<TypePersonne> findTypePersonneById(Integer id) {
        return typePersonneRepository.findById(id);
    }

    public Optional<TypePersonne> findTypePersonneByNom(String nom) {
        return typePersonneRepository.findByNom(nom);
    }

    public Optional<Secteur> findSecteurById(Integer id) {
        return secteurRepository.findById(id);
    }

    public Optional<Fizarana> findFizaranaById(Integer id) {
        return fizaranaRepository.findById(id);
    }

    public Optional<Andraikitra> findAndraikitraById(Integer id) {
        return andraikitraRepository.findById(id);
    }

    public Optional<Fivondronana> findFivondronanaById(Integer id) {
        return fivondronanaRepository.findById(id);
    }

    public Optional<DingamPiofanana> findDingamPiofananaById(Integer id) {
        return dingamPiofananaRepository.findById(id);
    }
    
    public List<TypeFiofanana> findAllTypeFiofanana() {
        // Initialiser les types fiofanana s'ils n'existent pas
        initializeTypeFiofananaIfNeeded();
        return typeFiofananaRepository.findAllByOrderByNomAsc();
    }
    
    public Optional<TypeFiofanana> findTypeFiofananaById(Integer id) {
        return typeFiofananaRepository.findById(id);
    }
    
    /**
     * Initialise les types fiofanana s'ils n'existent pas dans la base de données
     */
    private void initializeTypeFiofananaIfNeeded() {
        long count = typeFiofananaRepository.count();
        if (count == 0) {
            // Insérer les 4 types de formation
            TypeFiofanana fanomababa = new TypeFiofanana("fanomababa");
            TypeFiofanana fanaterana = new TypeFiofanana("fanaterana");
            TypeFiofanana ravinala = new TypeFiofanana("ravinala");
            TypeFiofanana tp2 = new TypeFiofanana("TP2");
            
            typeFiofananaRepository.save(fanomababa);
            typeFiofananaRepository.save(fanaterana);
            typeFiofananaRepository.save(ravinala);
            typeFiofananaRepository.save(tp2);
            
            typeFiofananaRepository.flush();
        }
    }

    // Create new Filoha (sans fivondronana ni secteur)
    public Personne createFiloha(Personne personne, Integer typeFilohaId, Integer andraikitraId, Integer dingamPiofananaId, Integer typeFiofananaId) {
        // Set type to Filoha - créer si n'existe pas
        TypePersonne typeFiloha = typePersonneRepository.findByNom("Filoha")
            .orElseGet(() -> {
                TypePersonne newType = new TypePersonne("Filoha");
                return typePersonneRepository.save(newType);
            });
        personne.setTypePersonne(typeFiloha);
        
        // Set typeFiloha if provided
        if (typeFilohaId != null) {
            typeFilohaRepository.findById(typeFilohaId).ifPresent(personne::setTypeFiloha);
        }
        
        // Set andraikitra if provided
        if (andraikitraId != null) {
            andraikitraRepository.findById(andraikitraId).ifPresent(personne::setAndraikitra);
        }
        
        // Set dingamPiofanana if provided
        if (dingamPiofananaId != null) {
            dingamPiofananaRepository.findById(dingamPiofananaId).ifPresent(personne::setDingamPiofanana);
        }
        
        // Set typeFiofanana if provided
        if (typeFiofananaId != null) {
            typeFiofananaRepository.findById(typeFiofananaId).ifPresent(personne::setTypeFiofanana);
        }
        
        // Filoha n'a pas de fivondronana ni secteur
        personne.setFivondronana(null);
        personne.setSecteur(null);
        personne.setFizarana(null);
        
        return personneRepository.save(personne);
    }

    // Create new Responsable avec Fivondronana
    public Personne createResponsable(Personne personne, Integer secteurId, Integer andraikitraId, Integer fizaranaId, Integer fivondronanaId, Integer dingamPiofananaId) {
        // Set type to Responsable
        typePersonneRepository.findByNom("Responsable").ifPresent(personne::setTypePersonne);
        
        // Set secteur if provided
        if (secteurId != null) {
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        }
        
        // Set andraikitra if provided
        if (andraikitraId != null) {
            andraikitraRepository.findById(andraikitraId).ifPresent(personne::setAndraikitra);
        }
        
        // Set fizarana (sampana) if provided
        if (fizaranaId != null) {
            fizaranaRepository.findById(fizaranaId).ifPresent(personne::setFizarana);
        }
        
        // Set fivondronana (obligatoire pour les non-admin)
        if (fivondronanaId != null) {
            fivondronanaRepository.findById(fivondronanaId).ifPresent(personne::setFivondronana);
        }
        
        // Set dingam-piofanana if provided
        if (dingamPiofananaId != null) {
            dingamPiofananaRepository.findById(dingamPiofananaId).ifPresent(personne::setDingamPiofanana);
        }
        
        // Ne pas définir ambaratonga pour les responsables
        personne.setAmbaratonga(null);
        
        return personneRepository.save(personne);
    }

    // Create new Eleve (Beazina) avec Fivondronana
    public Personne createEleve(Personne personne, Integer secteurId, Integer fizaranaId, Integer fivondronanaId) {
        // Set type to Eleve
        typePersonneRepository.findByNom("Eleve").ifPresent(personne::setTypePersonne);
        
        // Set secteur if provided
        if (secteurId != null) {
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        }
        
        // Set fizarana if provided
        if (fizaranaId != null) {
            fizaranaRepository.findById(fizaranaId).ifPresent(personne::setFizarana);
        }
        
        // Set fivondronana (obligatoire pour les non-admin)
        if (fivondronanaId != null) {
            fivondronanaRepository.findById(fivondronanaId).ifPresent(personne::setFivondronana);
        }
        
        return personneRepository.save(personne);
    }

    // Récupérer les statuts FAFI (liste fixe pour éviter les problèmes si certains statuts n'existent pas encore en base)
    public List<String> findAllFafiStatuts() {
        return java.util.Arrays.asList("Active", "Inactive");
    }

    public Personne updateResponsable(
            Integer id,
            String nom,
            String prenom,
            String totem,
            LocalDate dateNaissance,
            String numeroTelephone,
            String numeroCin,
            String nomPere,
            String nomMere,
            LocalDate dateFanekena,
            Integer secteurId,
            Integer andraikitraId,
            Integer fizaranaId,
            Integer dingamPiofananaId,
            String numeroFafi
    ) {
        Personne personne = personneRepository.findByIdWithFafi(id)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + id));

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
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        } else {
            personne.setSecteur(null);
        }

        if (andraikitraId != null) {
            andraikitraRepository.findById(andraikitraId).ifPresent(personne::setAndraikitra);
        } else {
            personne.setAndraikitra(null);
        }

        if (fizaranaId != null) {
            fizaranaRepository.findById(fizaranaId).ifPresent(personne::setFizarana);
        } else {
            personne.setFizarana(null);
        }

        if (dingamPiofananaId != null) {
            dingamPiofananaRepository.findById(dingamPiofananaId).ifPresent(personne::setDingamPiofanana);
        } else {
            personne.setDingamPiofanana(null);
        }

        updateNumeroFafiIfPresent(personne, numeroFafi);
        return personneRepository.save(personne);
    }

    public Personne updateEleve(
            Integer id,
            String nom,
            String prenom,
            String totem,
            LocalDate dateNaissance,
            String ambaratonga,
            String nomPere,
            String nomMere,
            LocalDate dateFanekena,
            Integer secteurId,
            Integer fizaranaId,
            String numeroFafi
    ) {
        Personne personne = personneRepository.findByIdWithFafi(id)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + id));

        personne.setNom(nom);
        personne.setPrenom(prenom);
        personne.setTotem(totem);
        personne.setDateNaissance(dateNaissance);
        personne.setAmbaratonga(ambaratonga);
        personne.setNomPere(nomPere);
        personne.setNomMere(nomMere);
        personne.setDateFanekena(dateFanekena);

        if (secteurId != null) {
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        } else {
            personne.setSecteur(null);
        }

        if (fizaranaId != null) {
            fizaranaRepository.findById(fizaranaId).ifPresent(personne::setFizarana);
        } else {
            personne.setFizarana(null);
        }

        updateNumeroFafiIfPresent(personne, numeroFafi);
        return personneRepository.save(personne);
    }

    public Personne updateFiloha(
            Integer id,
            String nom,
            String prenom,
            String totem,
            LocalDate dateNaissance,
            String numeroTelephone,
            String numeroCin,
            String nomPere,
            String nomMere,
            LocalDate dateFanekena,
            Integer typeFilohaId,
            Integer andraikitraId,
            Integer dingamPiofananaId,
            Integer typeFiofananaId,
            String numeroFafi
    ) {
        Personne personne = personneRepository.findByIdWithFafi(id)
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + id));

        if (!personne.isFiloha()) {
            throw new RuntimeException("Ity olona ity dia tsy Filoha.");
        }

        personne.setNom(nom);
        personne.setPrenom(prenom);
        personne.setTotem(totem);
        personne.setDateNaissance(dateNaissance);
        personne.setNumeroTelephone(numeroTelephone);
        personne.setNumeroCin(numeroCin);
        personne.setNomPere(nomPere);
        personne.setNomMere(nomMere);
        personne.setDateFanekena(dateFanekena);
        personne.setSecteur(null);
        personne.setFivondronana(null);
        personne.setFizarana(null);

        if (typeFilohaId != null) {
            typeFilohaRepository.findById(typeFilohaId).ifPresent(personne::setTypeFiloha);
        } else {
            personne.setTypeFiloha(null);
        }

        if (andraikitraId != null) {
            andraikitraRepository.findById(andraikitraId).ifPresent(personne::setAndraikitra);
        } else {
            personne.setAndraikitra(null);
        }

        if (dingamPiofananaId != null) {
            dingamPiofananaRepository.findById(dingamPiofananaId).ifPresent(personne::setDingamPiofanana);
        } else {
            personne.setDingamPiofanana(null);
        }

        if (typeFiofananaId != null) {
            typeFiofananaRepository.findById(typeFiofananaId).ifPresent(personne::setTypeFiofanana);
        } else {
            personne.setTypeFiofanana(null);
        }

        updateFilohaNumeroFafiIfPresent(personne, numeroFafi);
        return personneRepository.save(personne);
    }

    private void updateNumeroFafiIfPresent(Personne personne, String numeroFafi) {
        if (numeroFafi != null && !numeroFafi.trim().isEmpty()) {
            if (personne.getFafi() == null) {
                Fafi fafi = new Fafi();
                fafi.setNumeroFafi(numeroFafi.trim());
                personne.setFafi(fafi);
            } else {
                personne.getFafi().setNumeroFafi(numeroFafi.trim());
            }
        }
    }

    private void updateFilohaNumeroFafiIfPresent(Personne personne, String numeroFafi) {
        if (numeroFafi != null && !numeroFafi.trim().isEmpty()) {
            if (personne.getFafi() == null) {
                Fafi fafi = new Fafi();
                fafi.setPersonne(personne);
                fafi.setNumeroFafi(numeroFafi.trim());
                fafi.setStatut("Active");
                fafi.setAnnee(fafiService.getAnneeCourante());
                fafi.setMontant(fafiService.getPrixMpiandraikitraAnneeActuelle());
                personne.setFafi(fafi);
            } else {
                personne.getFafi().setNumeroFafi(numeroFafi.trim());
            }
        }
    }

    // Mettre à jour ou créer le FAFI d'une personne
    public void updateFafi(Integer personneId, LocalDate datePaiement, BigDecimal montant, String statut, String numeroFafi) {
        // Récupérer la personne avec son Fafi chargé explicitement
        Optional<Personne> personneOpt = personneRepository.findByIdWithFafi(personneId);
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            Fafi fafi = personne.getFafi();
            
            if (fafi == null) {
                // Créer un nouveau FAFI
                fafi = new Fafi();
                if (datePaiement != null) {
                    fafi.setDatePaiement(datePaiement);
                }
                if (montant != null) {
                    fafi.setMontant(montant);
                }
                fafi.setStatut(statut != null && !statut.isEmpty() ? statut : "Inactive");
                if (numeroFafi != null && !numeroFafi.isEmpty()) {
                    fafi.setNumeroFafi(numeroFafi);
                }
                // Sauvegarder le Fafi
                fafi = fafiRepository.saveAndFlush(fafi);
                // Associer le Fafi à la personne
                personne.setFafi(fafi);
                // Sauvegarder la personne
                personneRepository.saveAndFlush(personne);
            } else {
                // Mettre à jour le FAFI existant
                if (datePaiement != null) {
                    fafi.setDatePaiement(datePaiement);
                }
                if (montant != null) {
                    fafi.setMontant(montant);
                }
                if (statut != null && !statut.isEmpty()) {
                    fafi.setStatut(statut);
                }
                if (numeroFafi != null && !numeroFafi.isEmpty()) {
                    fafi.setNumeroFafi(numeroFafi);
                }
                // Sauvegarder le Fafi mis à jour avec flush pour forcer la persistance
                fafiRepository.saveAndFlush(fafi);
                // S'assurer que la personne référence le Fafi mis à jour
                personne.setFafi(fafi);
                personneRepository.saveAndFlush(personne);
            }
        }
    }

    // Vérifier si une personne appartient à un Fivondronana
    public boolean personneAppartientAFivondronana(Integer personneId, Integer fivondronanaId) {
        Optional<Personne> personneOpt = personneRepository.findById(personneId);
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            return personne.getFivondronana() != null && personne.getFivondronana().getId().equals(fivondronanaId);
        }
        return false;
    }

    // ========== DETAILS FIOFANANA ==========
    
    public Optional<DetailsFiofanana> findDetailsFiofananaByPersonneIdAndTypeFiofananaId(Integer personneId, Integer typeFiofananaId) {
        return detailsFiofananaRepository.findByPersonneIdAndTypeFiofananaId(personneId, typeFiofananaId);
    }
    
    public DetailsFiofanana saveOrUpdateDetailsFiofanana(Integer personneId, Integer typeFiofananaId, DetailsFiofanana details) {
        Optional<Personne> personneOpt = personneRepository.findById(personneId);
        if (!personneOpt.isPresent()) {
            throw new RuntimeException("Personne non trouvée avec l'ID: " + personneId);
        }
        
        Personne personne = personneOpt.get();
        
        // Vérifier que le typeFiofanana existe
        Optional<TypeFiofanana> typeFiofananaOpt = findTypeFiofananaById(typeFiofananaId);
        if (!typeFiofananaOpt.isPresent()) {
            throw new RuntimeException("TypeFiofanana non trouvé avec l'ID: " + typeFiofananaId);
        }
        
        TypeFiofanana typeFiofanana = typeFiofananaOpt.get();
        
        // Chercher les détails existants pour ce type
        Optional<DetailsFiofanana> existingOpt = detailsFiofananaRepository.findByPersonneIdAndTypeFiofananaId(personneId, typeFiofananaId);
        
        DetailsFiofanana detailsToSave;
        if (existingOpt.isPresent()) {
            // Mettre à jour les détails existants
            detailsToSave = existingOpt.get();
        } else {
            // Créer de nouveaux détails
            detailsToSave = new DetailsFiofanana();
            detailsToSave.setPersonne(personne);
            detailsToSave.setTypeFiofanana(typeFiofanana);
        }
        
        // Copier les valeurs depuis l'objet fourni
        if (details != null) {
            // Section A
            detailsToSave.setAsan1(details.getAsan1());
            detailsToSave.setAsan2(details.getAsan2());
            detailsToSave.setAsan3(details.getAsan3());
            detailsToSave.setAsan4(details.getAsan4());
            detailsToSave.setAsan5(details.getAsan5());
            detailsToSave.setAsan6(details.getAsan6());
            detailsToSave.setAsan7(details.getAsan7());
            detailsToSave.setAsanFilohaNanome(details.getAsanFilohaNanome());
            
            // Section B
            detailsToSave.setEzaka1(details.getEzaka1());
            detailsToSave.setEzaka2(details.getEzaka2());
            detailsToSave.setEzaka3(details.getEzaka3());
            detailsToSave.setEzaka4(details.getEzaka4());
            detailsToSave.setEzaka5(details.getEzaka5());
            detailsToSave.setEzaka6(details.getEzaka6());
            detailsToSave.setEzaka7(details.getEzaka7());
            detailsToSave.setEzakaFilohaNanome(details.getEzakaFilohaNanome());
            
            // Section C
            detailsToSave.setBitsikyDaty1(details.getBitsikyDaty1());
            detailsToSave.setBitsikyFivondronana1(details.getBitsikyFivondronana1());
            detailsToSave.setBitsikyDaty2(details.getBitsikyDaty2());
            detailsToSave.setBitsikyFivondronana2(details.getBitsikyFivondronana2());
            
            // Section D: Diniky ny filoha
            detailsToSave.setDinikyTheme1(details.getDinikyTheme1());
            detailsToSave.setDinikyFiloha1(details.getDinikyFiloha1());
            detailsToSave.setDinikyTheme2(details.getDinikyTheme2());
            detailsToSave.setDinikyFiloha2(details.getDinikyFiloha2());
            detailsToSave.setDinikyTheme3(details.getDinikyTheme3());
            detailsToSave.setDinikyFiloha3(details.getDinikyFiloha3());
            detailsToSave.setDinikyTheme4(details.getDinikyTheme4());
            detailsToSave.setDinikyFiloha4(details.getDinikyFiloha4());
            detailsToSave.setDinikyTheme5(details.getDinikyTheme5());
            detailsToSave.setDinikyFiloha5(details.getDinikyFiloha5());
            
            // Section E: Filasiana
            detailsToSave.setFilasianaDaty(details.getFilasianaDaty());
            detailsToSave.setFilasianaFiloha(details.getFilasianaFiloha());
            
            // Section F: Ravinala
            detailsToSave.setLasyRavinala(details.getLasyRavinala());
            detailsToSave.setSoutenance(details.getSoutenance());
            
            // Section G: TP2
            detailsToSave.setLasyNanoloranaTp2(details.getLasyNanoloranaTp2());
        }
        
        // Sauvegarder
        return detailsFiofananaRepository.saveAndFlush(detailsToSave);
    }
}
