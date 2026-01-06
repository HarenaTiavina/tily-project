package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tily.mg.repository.FafiRepository;
import tily.mg.repository.PersonneRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardService {

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private FafiRepository fafiRepository;

    /**
     * Récupère l'année courante
     */
    public int getAnneeCourante() {
        return LocalDate.now().getYear();
    }

    // Responsables stats
    public Long getTotalResponsables() {
        Long count = personneRepository.countByTypePersonneNom("Responsable");
        return count != null ? count : 0L;
    }

    /**
     * Compte les responsables avec FAFI payé pour l'année courante
     */
    public Long getResponsablesWithFafi() {
        Long count = personneRepository.countResponsablesWithFafiForYear(getAnneeCourante());
        return count != null ? count : 0L;
    }

    public Long getResponsablesWithoutFafi() {
        Long total = getTotalResponsables();
        Long withFafi = getResponsablesWithFafi();
        return total - withFafi;
    }

    // Eleves stats
    public Long getTotalEleves() {
        Long count = personneRepository.countByTypePersonneNom("Eleve");
        return count != null ? count : 0L;
    }

    /**
     * Compte les élèves avec FAFI payé pour l'année courante
     */
    public Long getElevesWithFafi() {
        Long count = personneRepository.countElevesWithFafiForYear(getAnneeCourante());
        return count != null ? count : 0L;
    }

    public Long getElevesWithoutFafi() {
        Long total = getTotalEleves();
        Long withFafi = getElevesWithFafi();
        return total - withFafi;
    }

    // FAFI Total stats
    public BigDecimal getTotalFafiMontant() {
        BigDecimal total = fafiRepository.getTotalMontantActive();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long getTotalPaidFafi() {
        return getResponsablesWithFafi() + getElevesWithFafi();
    }

    public Long getTotalUnpaidFafi() {
        Long totalPersonnes = getTotalResponsables() + getTotalEleves();
        Long paid = getTotalPaidFafi();
        return totalPersonnes - paid;
    }

    // FAFI Total stats filtrées par Fivondronana
    public BigDecimal getTotalFafiMontantByFivondronana(Integer fivondronanaId) {
        BigDecimal total = fafiRepository.getTotalMontantActiveByFivondronana(fivondronanaId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Compte les responsables avec FAFI payé pour l'année courante par fivondronana
     */
    public Long getResponsablesWithFafiByFivondronana(Integer fivondronanaId) {
        Long count = personneRepository.countResponsablesWithFafiByFivondronanaForYear(fivondronanaId, getAnneeCourante());
        return count != null ? count : 0L;
    }

    /**
     * Compte les élèves avec FAFI payé pour l'année courante par fivondronana
     */
    public Long getElevesWithFafiByFivondronana(Integer fivondronanaId) {
        Long count = personneRepository.countElevesWithFafiByFivondronanaForYear(fivondronanaId, getAnneeCourante());
        return count != null ? count : 0L;
    }

    public Long getTotalPaidFafiByFivondronana(Integer fivondronanaId) {
        return getResponsablesWithFafiByFivondronana(fivondronanaId) + getElevesWithFafiByFivondronana(fivondronanaId);
    }

    public Long getTotalUnpaidFafiByFivondronana(Integer fivondronanaId, Long totalResponsables, Long totalEleves) {
        Long totalPersonnes = totalResponsables + totalEleves;
        Long paid = getTotalPaidFafiByFivondronana(fivondronanaId);
        return totalPersonnes - paid;
    }
}

