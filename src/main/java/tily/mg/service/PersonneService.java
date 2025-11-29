package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tily.mg.entity.*;
import tily.mg.repository.*;

import java.math.BigDecimal;
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

    // Responsables
    public List<Personne> findAllResponsables() {
        return personneRepository.findAllResponsables();
    }

    public Long countResponsables() {
        return personneRepository.countByTypePersonneNom("Responsable");
    }

    public Long countResponsablesWithFafi() {
        return personneRepository.countResponsablesWithFafi();
    }

    public List<Personne> filterResponsables(Integer secteurId, Integer andraikitraId, Boolean hasFafi) {
        return personneRepository.filterResponsables(secteurId, andraikitraId, hasFafi);
    }

    // Eleves
    public List<Personne> findAllEleves() {
        return personneRepository.findAllEleves();
    }

    public Long countEleves() {
        return personneRepository.countByTypePersonneNom("Eleve");
    }

    public Long countElevesWithFafi() {
        return personneRepository.countElevesWithFafi();
    }

    public List<Personne> filterEleves(Integer secteurId, Integer fizaranaId, String ambaratonga, Boolean hasFafi) {
        return personneRepository.filterEleves(secteurId, fizaranaId, ambaratonga, hasFafi);
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

    // Create new Responsable (pas de niveau, utilise andraikitra au lieu de section)
    public Personne createResponsable(Personne personne, Integer secteurId, Integer andraikitraId) {
        // Set type to Responsable
        typePersonneRepository.findByNom("Responsable").ifPresent(personne::setTypePersonne);
        
        // Set secteur if provided
        if (secteurId != null) {
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        }
        
        // Set andraikitra if provided (pas de niveau pour les responsables)
        if (andraikitraId != null) {
            andraikitraRepository.findById(andraikitraId).ifPresent(personne::setAndraikitra);
        }
        
        // Ne pas définir ambaratonga pour les responsables
        personne.setAmbaratonga(null);
        
        return personneRepository.save(personne);
    }

    // Create new Eleve (Beazina)
    public Personne createEleve(Personne personne, Integer secteurId, Integer fizaranaId) {
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
        
        return personneRepository.save(personne);
    }

    // Récupérer les statuts FAFI distincts depuis la base
    public List<String> findAllFafiStatuts() {
        return fafiRepository.findDistinctStatuts();
    }
}

