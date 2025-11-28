package tily.mg.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FizaranaTest {

    private Fizarana fizarana;

    @BeforeEach
    void setUp() {
        fizarana = new Fizarana();
        fizarana.setId(1);
        fizarana.setNom("Analamanga");
    }

    @Test
    void testGettersAndSetters() {
        // Test getters and setters
        assertEquals(1, fizarana.getId());
        assertEquals("Analamanga", fizarana.getNom());
    }

    @Test
    void testConstructorWithNom() {
        // When
        Fizarana newFizarana = new Fizarana("Antananarivo");

        // Then
        assertEquals("Antananarivo", newFizarana.getNom());
    }

    @Test
    void testDefaultConstructor() {
        // When
        Fizarana newFizarana = new Fizarana();

        // Then
        assertNotNull(newFizarana);
        assertNull(newFizarana.getId());
        assertNull(newFizarana.getNom());
    }

    @Test
    void testSetNom() {
        // When
        fizarana.setNom("Antananarivo");

        // Then
        assertEquals("Antananarivo", fizarana.getNom());
    }

    @Test
    void testSetId() {
        // When
        fizarana.setId(2);

        // Then
        assertEquals(2, fizarana.getId());
    }
}

