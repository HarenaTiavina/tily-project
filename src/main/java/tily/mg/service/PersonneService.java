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

    // CRUD Operations
    public List<Personne> findAll() {
        return personneRepository.findAll();
    }

    public Optional<Personne> findById(Integer id) {
        return personneRepository.findById(id);
    }

    public Personne save(Personne personne) {
        return personneRepository.save(personne);
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
    public List<Personne> filterResponsables(Integer fivondronanaId, Integer secteurId, Integer andraikitraId, Integer fizaranaId, Boolean hasFafi) {
        return personneRepository.filterResponsables(fivondronanaId, secteurId, andraikitraId, fizaranaId, hasFafi);
    }

    public List<Personne> filterEleves(Integer fivondronanaId, Integer secteurId, Integer fizaranaId, String ambaratonga, Boolean hasFafi) {
        return personneRepository.filterEleves(fivondronanaId, secteurId, fizaranaId, ambaratonga, hasFafi);
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
    public List<Personne> filterResponsablesByFivondronana(Integer fivondronanaId, Integer secteurId, Integer andraikitraId, Integer fizaranaId, Boolean hasFafi) {
        return personneRepository.filterResponsablesByFivondronana(fivondronanaId, secteurId, andraikitraId, fizaranaId, hasFafi);
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

    // Create new Responsable avec Fivondronana
    public Personne createResponsable(Personne personne, Integer secteurId, Integer andraikitraId, Integer fizaranaId, Integer fivondronanaId) {
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
}
