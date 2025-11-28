package tily.mg.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PersonneTest {

    private Personne personne;
    private TypePersonne typeResponsable;
    private TypePersonne typeEleve;
    private Assurance assurance;
    private Secteur secteur;
    private Fizarana fizarana;
    private Andraikitra andraikitra;

    @BeforeEach
    void setUp() {
        personne = new Personne();
        personne.setId(1);
        personne.setNom("RAKOTO");
        personne.setPrenom("Jean");
        personne.setTotem("Akanga");
        personne.setDateNaissance(LocalDate.of(1990, 3, 15));
        personne.setAmbaratonga("Louveteau");
        personne.setNumeroTelephone("034 12 345 67");
        personne.setNumeroCin("101 251 456 789");
        personne.setNomPere("Pierre Rakoto");
        personne.setNomMere("Marie Rasoa");
        personne.setDateFanekena(LocalDate.of(2020, 1, 12));

        typeResponsable = new TypePersonne();
        typeResponsable.setId(1);
        typeResponsable.setNom("Responsable");

        typeEleve = new TypePersonne();
        typeEleve.setId(2);
        typeEleve.setNom("Eleve");

        assurance = new Assurance();
        assurance.setId(1);
        assurance.setStatut("Active");

        secteur = new Secteur();
        secteur.setId(1);
        secteur.setNom("Analamanga");

        fizarana = new Fizarana();
        fizarana.setId(1);
        fizarana.setNom("Analamanga");

        andraikitra = new Andraikitra();
        andraikitra.setId(1);
        andraikitra.setNom("Mpitandrina lehibe");
    }

    @Test
    void testGetNomComplet() {
        // When
        String nomComplet = personne.getNomComplet();

        // Then
        assertEquals("Jean RAKOTO", nomComplet);
    }

    @Test
    void testIsMpiandraikitra() {
        // Given
        personne.setTypePersonne(typeResponsable);

        // When
        boolean isMpiandraikitra = personne.isMpiandraikitra();

        // Then
        assertTrue(isMpiandraikitra);
    }

    @Test
    void testIsNotMpiandraikitra() {
        // Given
        personne.setTypePersonne(typeEleve);

        // When
        boolean isMpiandraikitra = personne.isMpiandraikitra();

        // Then
        assertFalse(isMpiandraikitra);
    }

    @Test
    void testIsMpiandraikitraWithNullType() {
        // Given
        personne.setTypePersonne(null);

        // When
        boolean isMpiandraikitra = personne.isMpiandraikitra();

        // Then
        assertFalse(isMpiandraikitra);
    }

    @Test
    void testIsBeazina() {
        // Given
        personne.setTypePersonne(typeEleve);

        // When
        boolean isBeazina = personne.isBeazina();

        // Then
        assertTrue(isBeazina);
    }

    @Test
    void testIsNotBeazina() {
        // Given
        personne.setTypePersonne(typeResponsable);

        // When
        boolean isBeazina = personne.isBeazina();

        // Then
        assertFalse(isBeazina);
    }

    @Test
    void testIsBeazinaWithNullType() {
        // Given
        personne.setTypePersonne(null);

        // When
        boolean isBeazina = personne.isBeazina();

        // Then
        assertFalse(isBeazina);
    }

    @Test
    void testHasAssurance() {
        // Given
        personne.setAssurance(assurance);

        // When
        boolean hasAssurance = personne.hasAssurance();

        // Then
        assertTrue(hasAssurance);
    }

    @Test
    void testHasNoAssurance() {
        // Given
        personne.setAssurance(null);

        // When
        boolean hasAssurance = personne.hasAssurance();

        // Then
        assertFalse(hasAssurance);
    }

    @Test
    void testHasAssuranceInactive() {
        // Given
        assurance.setStatut("Inactive");
        personne.setAssurance(assurance);

        // When
        boolean hasAssurance = personne.hasAssurance();

        // Then
        assertFalse(hasAssurance);
    }

    @Test
    void testGettersAndSetters() {
        // Test all getters and setters
        assertEquals(1, personne.getId());
        assertEquals("RAKOTO", personne.getNom());
        assertEquals("Jean", personne.getPrenom());
        assertEquals("Akanga", personne.getTotem());
        assertEquals(LocalDate.of(1990, 3, 15), personne.getDateNaissance());
        assertEquals("Louveteau", personne.getAmbaratonga());
        assertEquals("034 12 345 67", personne.getNumeroTelephone());
        assertEquals("101 251 456 789", personne.getNumeroCin());
        assertEquals("Pierre Rakoto", personne.getNomPere());
        assertEquals("Marie Rasoa", personne.getNomMere());
        assertEquals(LocalDate.of(2020, 1, 12), personne.getDateFanekena());
    }

    @Test
    void testSetFizarana() {
        // When
        personne.setFizarana(fizarana);

        // Then
        assertEquals(fizarana, personne.getFizarana());
    }

    @Test
    void testSetAndraikitra() {
        // When
        personne.setAndraikitra(andraikitra);

        // Then
        assertEquals(andraikitra, personne.getAndraikitra());
    }

    @Test
    void testSetSecteur() {
        // When
        personne.setSecteur(secteur);

        // Then
        assertEquals(secteur, personne.getSecteur());
    }

    @Test
    void testSetTypePersonne() {
        // When
        personne.setTypePersonne(typeEleve);

        // Then
        assertEquals(typeEleve, personne.getTypePersonne());
    }

    @Test
    void testSetAssurance() {
        // When
        personne.setAssurance(assurance);

        // Then
        assertEquals(assurance, personne.getAssurance());
    }

    @Test
    void testDeprecatedMethods() {
        // Test deprecated methods for backward compatibility
        personne.setTypePersonne(typeResponsable);
        assertTrue(personne.isMpiandraikitra());
        // Test deprecated method still works
        @SuppressWarnings("deprecation")
        boolean isResponsable = personne.isResponsable();
        assertTrue(isResponsable);

        personne.setTypePersonne(typeEleve);
        assertTrue(personne.isBeazina());
        // Test deprecated method still works
        @SuppressWarnings("deprecation")
        boolean isEleve = personne.isEleve();
        assertTrue(isEleve);
    }
}

