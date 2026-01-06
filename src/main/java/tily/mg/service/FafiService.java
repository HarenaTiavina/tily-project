package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tily.mg.entity.Fafi;
import tily.mg.entity.Personne;
import tily.mg.entity.PrixFafi;
import tily.mg.repository.FafiRepository;
import tily.mg.repository.PersonneRepository;
import tily.mg.repository.PrixFafiRepository;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FafiService {

    @Autowired
    private FafiRepository fafiRepository;

    @Autowired
    private PrixFafiRepository prixFafiRepository;

    @Autowired
    private PersonneRepository personneRepository;

    /**
     * Initialise les prix FAFI par défaut pour l'année courante s'ils n'existent pas
     */
    @PostConstruct
    public void initPrixFafiDefaut() {
        int anneeCourante = LocalDate.now().getYear();
        
        // Créer le prix Mpiandraikitra s'il n'existe pas
        if (!prixFafiRepository.existsByTypePersonneAndAnnee("Mpiandraikitra", anneeCourante)) {
            PrixFafi prixMpiandraikitra = new PrixFafi("Mpiandraikitra", BigDecimal.valueOf(8000), anneeCourante);
            prixFafiRepository.save(prixMpiandraikitra);
        }
        
        // Créer le prix Beazina s'il n'existe pas
        if (!prixFafiRepository.existsByTypePersonneAndAnnee("Beazina", anneeCourante)) {
            PrixFafi prixBeazina = new PrixFafi("Beazina", BigDecimal.valueOf(5000), anneeCourante);
            prixFafiRepository.save(prixBeazina);
        }
    }

    // ========== Gestion des Prix FAFI ==========

    /**
     * Récupère le prix du FAFI pour les Mpiandraikitra pour l'année courante
     * Le prix doit exister dans la base de données
     */
    public BigDecimal getPrixMpiandraikitraAnneeActuelle() {
        int anneeCourante = LocalDate.now().getYear();
        return prixFafiRepository.findPrixMpiandraikitraForYear(anneeCourante)
                .map(PrixFafi::getPrix)
                .orElseGet(() -> {
                    // Créer le prix s'il n'existe pas (ne devrait pas arriver après @PostConstruct)
                    PrixFafi nouveauPrix = new PrixFafi("Mpiandraikitra", BigDecimal.valueOf(8000), anneeCourante);
                    prixFafiRepository.save(nouveauPrix);
                    return nouveauPrix.getPrix();
                });
    }

    /**
     * Récupère le prix du FAFI pour les Beazina pour l'année courante
     * Le prix doit exister dans la base de données
     */
    public BigDecimal getPrixBeazinaAnneeActuelle() {
        int anneeCourante = LocalDate.now().getYear();
        return prixFafiRepository.findPrixBeazinaForYear(anneeCourante)
                .map(PrixFafi::getPrix)
                .orElseGet(() -> {
                    // Créer le prix s'il n'existe pas (ne devrait pas arriver après @PostConstruct)
                    PrixFafi nouveauPrix = new PrixFafi("Beazina", BigDecimal.valueOf(5000), anneeCourante);
                    prixFafiRepository.save(nouveauPrix);
                    return nouveauPrix.getPrix();
                });
    }

    /**
     * Récupère tous les prix FAFI
     */
    public List<PrixFafi> getAllPrixFafi() {
        return prixFafiRepository.findAllOrderByAnneeDesc();
    }

    /**
     * Récupère les prix pour une année donnée
     */
    public List<PrixFafi> getPrixFafiByAnnee(Integer annee) {
        return prixFafiRepository.findByAnneeAndActifTrue(annee);
    }

    /**
     * Crée ou met à jour le prix FAFI pour un type de personne et une année
     */
    public PrixFafi savePrixFafi(String typePersonne, BigDecimal prix, Integer annee) {
        Optional<PrixFafi> existingPrix = prixFafiRepository.findByTypePersonneAndAnneeAndActifTrue(typePersonne, annee);
        
        PrixFafi prixFafi;
        if (existingPrix.isPresent()) {
            prixFafi = existingPrix.get();
            prixFafi.setPrix(prix);
        } else {
            prixFafi = new PrixFafi(typePersonne, prix, annee);
        }
        
        return prixFafiRepository.save(prixFafi);
    }

    /**
     * Récupère un prix FAFI par ID
     */
    public Optional<PrixFafi> getPrixFafiById(Integer id) {
        return prixFafiRepository.findById(id);
    }

    // ========== Gestion des Paiements FAFI ==========

    /**
     * Marque plusieurs personnes comme ayant payé le FAFI pour l'année courante
     */
    public int marquerFafiPayePourAnnee(List<Integer> personneIds, String typePersonne) {
        int anneeCourante = LocalDate.now().getYear();
        BigDecimal prix;
        
        if ("Mpiandraikitra".equals(typePersonne) || "Responsable".equals(typePersonne)) {
            prix = getPrixMpiandraikitraAnneeActuelle();
        } else {
            prix = getPrixBeazinaAnneeActuelle();
        }
        
        int count = 0;
        for (Integer personneId : personneIds) {
            Optional<Personne> personneOpt = personneRepository.findByIdWithFafi(personneId);
            if (personneOpt.isPresent()) {
                Personne personne = personneOpt.get();
                Fafi fafi = personne.getFafi();
                
                if (fafi == null) {
                    fafi = new Fafi();
                    fafi = fafiRepository.save(fafi);
                    personne.setFafi(fafi);
                }
                
                fafi.setDatePaiement(LocalDate.now());
                fafi.setMontant(prix);
                fafi.setStatut("Active");
                fafi.setAnnee(anneeCourante);
                
                fafiRepository.save(fafi);
                personneRepository.save(personne);
                count++;
            }
        }
        
        return count;
    }

    /**
     * Vérifie si le FAFI d'une personne est valide pour l'année courante
     */
    public boolean isFafiValideAnneeActuelle(Personne personne) {
        if (personne.getFafi() == null) {
            return false;
        }
        return personne.getFafi().isValidePourAnneeCourante();
    }

    /**
     * Récupère l'année courante
     */
    public int getAnneeCourante() {
        return LocalDate.now().getYear();
    }

    /**
     * Met à jour ou crée le FAFI d'une personne avec validation annuelle
     */
    public void updateFafiAnnuel(Integer personneId, LocalDate datePaiement, BigDecimal montant, String statut, String numeroFafi) {
        Optional<Personne> personneOpt = personneRepository.findByIdWithFafi(personneId);
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            Fafi fafi = personne.getFafi();
            
            if (fafi == null) {
                fafi = new Fafi();
                fafi = fafiRepository.save(fafi);
                personne.setFafi(fafi);
            }
            
            if (datePaiement != null) {
                fafi.setDatePaiement(datePaiement);
                // L'année est basée sur la date de paiement
                fafi.setAnnee(datePaiement.getYear());
            } else if (fafi.getAnnee() == null) {
                // Par défaut, l'année courante
                fafi.setAnnee(LocalDate.now().getYear());
            }
            
            if (montant != null) {
                fafi.setMontant(montant);
            }
            
            if (statut != null && !statut.isEmpty()) {
                fafi.setStatut(statut);
            } else if (fafi.getStatut() == null) {
                fafi.setStatut("Inactive");
            }
            
            if (numeroFafi != null && !numeroFafi.isEmpty()) {
                fafi.setNumeroFafi(numeroFafi);
            }
            
            fafiRepository.save(fafi);
            personneRepository.save(personne);
        }
    }
}

