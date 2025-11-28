package tily.mg.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AndraikitraTest {

    private Andraikitra andraikitra;

    @BeforeEach
    void setUp() {
        andraikitra = new Andraikitra();
        andraikitra.setId(1);
        andraikitra.setNom("Mpitandrina lehibe");
    }

    @Test
    void testGettersAndSetters() {
        // Test getters and setters
        assertEquals(1, andraikitra.getId());
        assertEquals("Mpitandrina lehibe", andraikitra.getNom());
    }

    @Test
    void testConstructorWithNom() {
        // When
        Andraikitra newAndraikitra = new Andraikitra("Mpitandrina faharoa");

        // Then
        assertEquals("Mpitandrina faharoa", newAndraikitra.getNom());
    }

    @Test
    void testDefaultConstructor() {
        // When
        Andraikitra newAndraikitra = new Andraikitra();

        // Then
        assertNotNull(newAndraikitra);
        assertNull(newAndraikitra.getId());
        assertNull(newAndraikitra.getNom());
    }

    @Test
    void testSetNom() {
        // When
        andraikitra.setNom("Mpitandrina faharoa");

        // Then
        assertEquals("Mpitandrina faharoa", andraikitra.getNom());
    }

    @Test
    void testSetId() {
        // When
        andraikitra.setId(2);

        // Then
        assertEquals(2, andraikitra.getId());
    }
}

