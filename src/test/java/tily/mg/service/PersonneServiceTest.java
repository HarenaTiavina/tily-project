package tily.mg.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tily.mg.entity.*;
import tily.mg.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonneServiceTest {

    @Mock
    private PersonneRepository personneRepository;

    @Mock
    private TypePersonneRepository typePersonneRepository;

    @Mock
    private SecteurRepository secteurRepository;

    @Mock
    private FizaranaRepository fizaranaRepository;

    @Mock
    private AndraikitraRepository andraikitraRepository;

    @Mock
    private FafiRepository fafiRepository;

    @Mock
    private FivondronanaRepository fivondronanaRepository;

    @Mock
    private DingamPiofananaRepository dingamPiofananaRepository;

    @InjectMocks
    private PersonneService personneService;

    private Personne personne;
    private TypePersonne typeResponsable;
    private TypePersonne typeEleve;
    private Secteur secteur;
    private Fizarana fizarana;
    private Andraikitra andraikitra;
    private Fivondronana fivondronana;
    private DingamPiofanana dingamPiofanana;

    @BeforeEach
    void setUp() {
        // Setup TypePersonne
        typeResponsable = new TypePersonne();
        typeResponsable.setId(1);
        typeResponsable.setNom("Responsable");

        typeEleve = new TypePersonne();
        typeEleve.setId(2);
        typeEleve.setNom("Eleve");

        // Setup Secteur
        secteur = new Secteur();
        secteur.setId(1);
        secteur.setNom("Analamanga");

        // Setup Fizarana
        fizarana = new Fizarana();
        fizarana.setId(1);
        fizarana.setNom("Analamanga");

        // Setup Andraikitra
        andraikitra = new Andraikitra();
        andraikitra.setId(1);
        andraikitra.setNom("Mpitandrina lehibe");

        // Setup Fivondronana
        fivondronana = new Fivondronana();
        fivondronana.setId(1);
        fivondronana.setNom("Antananarivo Renivohitra");
        fivondronana.setCode("ANT-R");

        // Setup DingamPiofanana
        dingamPiofanana = new DingamPiofanana();
        dingamPiofanana.setId(1);
        dingamPiofanana.setNom("Fanomanana");

        // Setup Personne
        personne = new Personne();
        personne.setId(1);
        personne.setNom("RAKOTO");
        personne.setPrenom("Jean");
        personne.setTotem("Akanga");
        personne.setDateNaissance(LocalDate.of(1990, 3, 15));
    }

    @Test
    void testFindAll() {
        // Given
        List<Personne> personnes = Arrays.asList(personne);
        when(personneRepository.findAll()).thenReturn(personnes);

        // When
        List<Personne> result = personneService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(personne, result.get(0));
        verify(personneRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(personneRepository.findById(1)).thenReturn(Optional.of(personne));

        // When
        Optional<Personne> result = personneService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(personne, result.get());
        verify(personneRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        when(personneRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Personne> result = personneService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(personneRepository, times(1)).findById(999);
    }

    @Test
    void testSave() {
        // Given
        when(personneRepository.save(any(Personne.class))).thenReturn(personne);

        // When
        Personne result = personneService.save(personne);

        // Then
        assertNotNull(result);
        assertEquals(personne, result);
        verify(personneRepository, times(1)).save(personne);
    }

    @Test
    void testDelete() {
        // When
        personneService.delete(1);

        // Then
        verify(personneRepository, times(1)).deleteById(1);
    }

    @Test
    void testFindAllResponsables() {
        // Given
        personne.setTypePersonne(typeResponsable);
        List<Personne> responsables = Arrays.asList(personne);
        when(personneRepository.findAllResponsables()).thenReturn(responsables);

        // When
        List<Personne> result = personneService.findAllResponsables();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).findAllResponsables();
    }

    @Test
    void testCountResponsables() {
        // Given
        when(personneRepository.countByTypePersonneNom("Responsable")).thenReturn(5L);

        // When
        Long count = personneService.countResponsables();

        // Then
        assertEquals(5L, count);
        verify(personneRepository, times(1)).countByTypePersonneNom("Responsable");
    }

    @Test
    void testFindAllEleves() {
        // Given
        personne.setTypePersonne(typeEleve);
        List<Personne> eleves = Arrays.asList(personne);
        when(personneRepository.findAllEleves()).thenReturn(eleves);

        // When
        List<Personne> result = personneService.findAllEleves();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).findAllEleves();
    }

    @Test
    void testCountEleves() {
        // Given
        when(personneRepository.countByTypePersonneNom("Eleve")).thenReturn(10L);

        // When
        Long count = personneService.countEleves();

        // Then
        assertEquals(10L, count);
        verify(personneRepository, times(1)).countByTypePersonneNom("Eleve");
    }

    @Test
    void testCreateResponsable() {
        // Given
        when(typePersonneRepository.findByNom("Responsable")).thenReturn(Optional.of(typeResponsable));
        when(secteurRepository.findById(1)).thenReturn(Optional.of(secteur));
        when(andraikitraRepository.findById(1)).thenReturn(Optional.of(andraikitra));
        when(fizaranaRepository.findById(1)).thenReturn(Optional.of(fizarana));
        when(fivondronanaRepository.findById(1)).thenReturn(Optional.of(fivondronana));
        when(dingamPiofananaRepository.findById(1)).thenReturn(Optional.of(dingamPiofanana));
        when(personneRepository.save(any(Personne.class))).thenReturn(personne);

        // When
        Personne result = personneService.createResponsable(personne, 1, 1, 1, 1, 1);

        // Then
        assertNotNull(result);
        assertEquals(typeResponsable, personne.getTypePersonne());
        assertEquals(secteur, personne.getSecteur());
        assertEquals(andraikitra, personne.getAndraikitra());
        assertEquals(fizarana, personne.getFizarana());
        assertEquals(fivondronana, personne.getFivondronana());
        assertEquals(dingamPiofanana, personne.getDingamPiofanana());
        assertNull(personne.getAmbaratonga()); // Pas de niveau pour les responsables
        verify(typePersonneRepository, times(1)).findByNom("Responsable");
        verify(secteurRepository, times(1)).findById(1);
        verify(andraikitraRepository, times(1)).findById(1);
        verify(fizaranaRepository, times(1)).findById(1);
        verify(fivondronanaRepository, times(1)).findById(1);
        verify(dingamPiofananaRepository, times(1)).findById(1);
        verify(personneRepository, times(1)).save(personne);
    }

    @Test
    void testCreateResponsableWithoutOptionalFields() {
        // Given
        when(typePersonneRepository.findByNom("Responsable")).thenReturn(Optional.of(typeResponsable));
        when(personneRepository.save(any(Personne.class))).thenReturn(personne);

        // When
        Personne result = personneService.createResponsable(personne, null, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(typeResponsable, personne.getTypePersonne());
        assertNull(personne.getSecteur());
        assertNull(personne.getAndraikitra());
        assertNull(personne.getFizarana());
        assertNull(personne.getFivondronana());
        assertNull(personne.getDingamPiofanana());
        assertNull(personne.getAmbaratonga());
        verify(typePersonneRepository, times(1)).findByNom("Responsable");
        verify(secteurRepository, never()).findById(anyInt());
        verify(andraikitraRepository, never()).findById(anyInt());
        verify(fizaranaRepository, never()).findById(anyInt());
        verify(fivondronanaRepository, never()).findById(anyInt());
        verify(dingamPiofananaRepository, never()).findById(anyInt());
    }

    @Test
    void testCreateEleve() {
        // Given
        when(typePersonneRepository.findByNom("Eleve")).thenReturn(Optional.of(typeEleve));
        when(secteurRepository.findById(1)).thenReturn(Optional.of(secteur));
        when(fizaranaRepository.findById(1)).thenReturn(Optional.of(fizarana));
        when(fivondronanaRepository.findById(1)).thenReturn(Optional.of(fivondronana));
        when(personneRepository.save(any(Personne.class))).thenReturn(personne);

        // When
        Personne result = personneService.createEleve(personne, 1, 1, 1);

        // Then
        assertNotNull(result);
        assertEquals(typeEleve, personne.getTypePersonne());
        assertEquals(secteur, personne.getSecteur());
        assertEquals(fizarana, personne.getFizarana());
        assertEquals(fivondronana, personne.getFivondronana());
        verify(typePersonneRepository, times(1)).findByNom("Eleve");
        verify(secteurRepository, times(1)).findById(1);
        verify(fizaranaRepository, times(1)).findById(1);
        verify(fivondronanaRepository, times(1)).findById(1);
        verify(personneRepository, times(1)).save(personne);
    }

    @Test
    void testFilterResponsables() {
        // Given
        personne.setTypePersonne(typeResponsable);
        List<Personne> responsables = Arrays.asList(personne);
        when(personneRepository.filterResponsables(1, 1, 1, 1, 1, true)).thenReturn(responsables);

        // When
        List<Personne> result = personneService.filterResponsables(1, 1, 1, 1, 1, true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).filterResponsables(1, 1, 1, 1, 1, true);
    }

    @Test
    void testFilterEleves() {
        // Given
        personne.setTypePersonne(typeEleve);
        List<Personne> eleves = Arrays.asList(personne);
        when(personneRepository.filterEleves(1, 1, 1, "Louveteau", true)).thenReturn(eleves);

        // When
        List<Personne> result = personneService.filterEleves(1, 1, 1, "Louveteau", true);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).filterEleves(1, 1, 1, "Louveteau", true);
    }

    @Test
    void testFindResponsablesByFivondronana() {
        // Given
        personne.setTypePersonne(typeResponsable);
        personne.setFivondronana(fivondronana);
        List<Personne> responsables = Arrays.asList(personne);
        when(personneRepository.findResponsablesByFivondronana(1)).thenReturn(responsables);

        // When
        List<Personne> result = personneService.findResponsablesByFivondronana(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).findResponsablesByFivondronana(1);
    }

    @Test
    void testFindElevesByFivondronana() {
        // Given
        personne.setTypePersonne(typeEleve);
        personne.setFivondronana(fivondronana);
        List<Personne> eleves = Arrays.asList(personne);
        when(personneRepository.findElevesByFivondronana(1)).thenReturn(eleves);

        // When
        List<Personne> result = personneService.findElevesByFivondronana(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).findElevesByFivondronana(1);
    }

    @Test
    void testFindAllFizarana() {
        // Given
        List<Fizarana> fizaranaList = Arrays.asList(fizarana);
        when(fizaranaRepository.findAll()).thenReturn(fizaranaList);

        // When
        List<Fizarana> result = personneService.findAllFizarana();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fizaranaRepository, times(1)).findAll();
    }

    @Test
    void testFindAllAndraikitra() {
        // Given
        List<Andraikitra> andraikitraList = Arrays.asList(andraikitra);
        when(andraikitraRepository.findAll()).thenReturn(andraikitraList);

        // When
        List<Andraikitra> result = personneService.findAllAndraikitra();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(andraikitraRepository, times(1)).findAll();
    }

    @Test
    void testFindAllFivondronana() {
        // Given
        List<Fivondronana> fivondronanaList = Arrays.asList(fivondronana);
        when(fivondronanaRepository.findAll()).thenReturn(fivondronanaList);

        // When
        List<Fivondronana> result = personneService.findAllFivondronana();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fivondronanaRepository, times(1)).findAll();
    }

    @Test
    void testGetTotalFafiMontant() {
        // Given
        when(fafiRepository.getTotalMontantActive()).thenReturn(new BigDecimal("1000.00"));

        // When
        BigDecimal result = personneService.getTotalFafiMontant();

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result);
        verify(fafiRepository, times(1)).getTotalMontantActive();
    }

    @Test
    void testGetTotalFafiMontantNull() {
        // Given
        when(fafiRepository.getTotalMontantActive()).thenReturn(null);

        // When
        BigDecimal result = personneService.getTotalFafiMontant();

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testCountWithFafi() {
        // Given
        when(fafiRepository.countActive()).thenReturn(5L);

        // When
        Long count = personneService.countWithFafi();

        // Then
        assertEquals(5L, count);
        verify(fafiRepository, times(1)).countActive();
    }

    @Test
    void testCountWithFafiNull() {
        // Given
        when(fafiRepository.countActive()).thenReturn(null);

        // When
        Long count = personneService.countWithFafi();

        // Then
        assertEquals(0L, count);
    }

    @Test
    void testSearch() {
        // Given
        List<Personne> personnes = Arrays.asList(personne);
        when(personneRepository.search("Jean")).thenReturn(personnes);

        // When
        List<Personne> result = personneService.search("Jean");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personneRepository, times(1)).search("Jean");
    }

    @Test
    void testFindFizaranaById() {
        // Given
        when(fizaranaRepository.findById(1)).thenReturn(Optional.of(fizarana));

        // When
        Optional<Fizarana> result = personneService.findFizaranaById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(fizarana, result.get());
        verify(fizaranaRepository, times(1)).findById(1);
    }

    @Test
    void testFindAndraikitraById() {
        // Given
        when(andraikitraRepository.findById(1)).thenReturn(Optional.of(andraikitra));

        // When
        Optional<Andraikitra> result = personneService.findAndraikitraById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(andraikitra, result.get());
        verify(andraikitraRepository, times(1)).findById(1);
    }

    @Test
    void testFindFivondronanaById() {
        // Given
        when(fivondronanaRepository.findById(1)).thenReturn(Optional.of(fivondronana));

        // When
        Optional<Fivondronana> result = personneService.findFivondronanaById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(fivondronana, result.get());
        verify(fivondronanaRepository, times(1)).findById(1);
    }

    @Test
    void testPersonneAppartientAFivondronana() {
        // Given
        personne.setFivondronana(fivondronana);
        when(personneRepository.findById(1)).thenReturn(Optional.of(personne));

        // When
        boolean result = personneService.personneAppartientAFivondronana(1, 1);

        // Then
        assertTrue(result);
        verify(personneRepository, times(1)).findById(1);
    }

    @Test
    void testPersonneAppartientAFivondronanaDifferentFivondronana() {
        // Given
        personne.setFivondronana(fivondronana);
        when(personneRepository.findById(1)).thenReturn(Optional.of(personne));

        // When
        boolean result = personneService.personneAppartientAFivondronana(1, 2);

        // Then
        assertFalse(result);
        verify(personneRepository, times(1)).findById(1);
    }

    @Test
    void testPersonneAppartientAFivondronanaPersonneNotFound() {
        // Given
        when(personneRepository.findById(999)).thenReturn(Optional.empty());

        // When
        boolean result = personneService.personneAppartientAFivondronana(999, 1);

        // Then
        assertFalse(result);
        verify(personneRepository, times(1)).findById(999);
    }
}
