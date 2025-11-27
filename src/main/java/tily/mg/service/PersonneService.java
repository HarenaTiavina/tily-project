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
    private SectionRepository sectionRepository;

    @Autowired
    private AssuranceRepository assuranceRepository;

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

    public Long countResponsablesWithAssurance() {
        return personneRepository.countResponsablesWithAssurance();
    }

    public List<Personne> filterResponsables(Integer secteurId, Integer sectionId, String niveau, Boolean hasAssurance) {
        return personneRepository.filterResponsables(secteurId, sectionId, niveau, hasAssurance);
    }

    // Eleves
    public List<Personne> findAllEleves() {
        return personneRepository.findAllEleves();
    }

    public Long countEleves() {
        return personneRepository.countByTypePersonneNom("Eleve");
    }

    public Long countElevesWithAssurance() {
        return personneRepository.countElevesWithAssurance();
    }

    public List<Personne> filterEleves(Integer secteurId, Integer sectionId, String niveau, Boolean hasAssurance) {
        return personneRepository.filterEleves(secteurId, sectionId, niveau, hasAssurance);
    }

    // Reference data
    public List<TypePersonne> findAllTypePersonnes() {
        return typePersonneRepository.findAll();
    }

    public List<Secteur> findAllSecteurs() {
        return secteurRepository.findAll();
    }

    public List<Section> findAllSections() {
        return sectionRepository.findAll();
    }

    // Statistics
    public BigDecimal getTotalAssuranceMontant() {
        BigDecimal total = assuranceRepository.getTotalMontantActive();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long countWithAssurance() {
        Long count = assuranceRepository.countActive();
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

    public Optional<Section> findSectionById(Integer id) {
        return sectionRepository.findById(id);
    }

    // Create new Responsable
    public Personne createResponsable(Personne personne, Integer secteurId, Integer sectionId) {
        // Set type to Responsable
        typePersonneRepository.findByNom("Responsable").ifPresent(personne::setTypePersonne);
        
        // Set secteur if provided
        if (secteurId != null) {
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        }
        
        // Set section if provided
        if (sectionId != null) {
            sectionRepository.findById(sectionId).ifPresent(personne::setSection);
        }
        
        return personneRepository.save(personne);
    }

    // Create new Eleve
    public Personne createEleve(Personne personne, Integer secteurId, Integer sectionId) {
        // Set type to Eleve
        typePersonneRepository.findByNom("Eleve").ifPresent(personne::setTypePersonne);
        
        // Set secteur if provided
        if (secteurId != null) {
            secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
        }
        
        // Set section if provided
        if (sectionId != null) {
            sectionRepository.findById(sectionId).ifPresent(personne::setSection);
        }
        
        return personneRepository.save(personne);
    }

    // Update existing person
    public Personne updatePersonne(Integer id, Personne personneDetails, Integer secteurId, Integer sectionId) {
        return personneRepository.findById(id).map(personne -> {
            personne.setNom(personneDetails.getNom());
            personne.setPrenom(personneDetails.getPrenom());
            personne.setTotem(personneDetails.getTotem());
            personne.setDateNaissance(personneDetails.getDateNaissance());
            personne.setNiveau(personneDetails.getNiveau());
            personne.setNumeroTelephone(personneDetails.getNumeroTelephone());
            personne.setNumeroCin(personneDetails.getNumeroCin());
            personne.setNomPere(personneDetails.getNomPere());
            personne.setNomMere(personneDetails.getNomMere());
            personne.setDateFanekena(personneDetails.getDateFanekena());
            
            if (secteurId != null) {
                secteurRepository.findById(secteurId).ifPresent(personne::setSecteur);
            }
            
            if (sectionId != null) {
                sectionRepository.findById(sectionId).ifPresent(personne::setSection);
            }
            
            return personneRepository.save(personne);
        }).orElse(null);
    }
}

