package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tily.mg.repository.FafiRepository;
import tily.mg.repository.PersonneRepository;

import java.math.BigDecimal;

@Service
public class DashboardService {

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private FafiRepository fafiRepository;

    // Responsables stats
    public Long getTotalResponsables() {
        Long count = personneRepository.countByTypePersonneNom("Responsable");
        return count != null ? count : 0L;
    }

    public Long getResponsablesWithFafi() {
        return personneRepository.countResponsablesWithFafi();
    }

    public Long getResponsablesWithoutFafi() {
        Long total = getTotalResponsables();
        Long withFafi = getResponsablesWithFafi();
        return total - (withFafi != null ? withFafi : 0L);
    }

    // Eleves stats
    public Long getTotalEleves() {
        Long count = personneRepository.countByTypePersonneNom("Eleve");
        return count != null ? count : 0L;
    }

    public Long getElevesWithFafi() {
        return personneRepository.countElevesWithFafi();
    }

    public Long getElevesWithoutFafi() {
        Long total = getTotalEleves();
        Long withFafi = getElevesWithFafi();
        return total - (withFafi != null ? withFafi : 0L);
    }

    // FAFI Total stats
    public BigDecimal getTotalFafiMontant() {
        BigDecimal total = fafiRepository.getTotalMontantActive();
        return total != null ? total : BigDecimal.ZERO;
    }

    public Long getTotalPaidFafi() {
        Long count = fafiRepository.countActive();
        return count != null ? count : 0L;
    }

    public Long getTotalUnpaidFafi() {
        Long totalPersonnes = getTotalResponsables() + getTotalEleves();
        Long paid = getTotalPaidFafi();
        return totalPersonnes - paid;
    }
}

