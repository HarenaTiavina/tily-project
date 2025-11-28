package tily.mg.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tily.mg.entity.Personne;
import tily.mg.entity.TypePersonne;
import tily.mg.entity.Utilisateur;
import tily.mg.repository.TypePersonneRepository;
import tily.mg.repository.UtilisateurRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private TypePersonneRepository typePersonneRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Utilisateur utilisateur;
    private Personne personne;
    private TypePersonne typePersonne;

    @BeforeEach
    void setUp() {
        // Setup TypePersonne
        typePersonne = new TypePersonne();
        typePersonne.setId(1);
        typePersonne.setNom("Eleve");

        // Setup Personne
        personne = new Personne();
        personne.setId(1);
        personne.setNom("RAKOTO");
        personne.setPrenom("Jean");
        personne.setDateNaissance(LocalDate.of(1990, 3, 15));
        personne.setTypePersonne(typePersonne);

        // Setup Utilisateur
        utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setEmail("jean@example.com");
        utilisateur.setMotDePasse("$2a$10$encodedPassword");
        utilisateur.setRole("USER");
        utilisateur.setActif(true);
        utilisateur.setPersonne(personne);
        utilisateur.setDateCreation(LocalDateTime.now());
    }

    @Test
    void testInscrireSuccess() {
        // Given
        when(utilisateurRepository.existsByEmail("jean@example.com")).thenReturn(false);
        when(typePersonneRepository.findByNom("Eleve")).thenReturn(Optional.of(typePersonne));
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        Utilisateur result = authService.inscrire(
            "jean@example.com",
            "password123",
            "RAKOTO",
            "Jean",
            "Eleve",
            LocalDate.of(1990, 3, 15)
        );

        // Then
        assertNotNull(result);
        assertEquals("jean@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
        assertTrue(result.getActif());
        assertNotNull(result.getPersonne());
        assertEquals("RAKOTO", result.getPersonne().getNom());
        assertEquals("Jean", result.getPersonne().getPrenom());
        verify(utilisateurRepository, times(1)).existsByEmail("jean@example.com");
        verify(typePersonneRepository, times(1)).findByNom("Eleve");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    void testInscrireEmailAlreadyExists() {
        // Given
        when(utilisateurRepository.existsByEmail("jean@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.inscrire(
                "jean@example.com",
                "password123",
                "RAKOTO",
                "Jean",
                "Eleve",
                LocalDate.of(1990, 3, 15)
            );
        });

        assertEquals("Cet email est déjà utilisé", exception.getMessage());
        verify(utilisateurRepository, times(1)).existsByEmail("jean@example.com");
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    void testAuthentifierSuccess() {
        // Given
        when(utilisateurRepository.findByEmailWithPersonne("jean@example.com"))
            .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", utilisateur.getMotDePasse()))
            .thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        Optional<Utilisateur> result = authService.authentifier("jean@example.com", "password123");

        // Then
        assertTrue(result.isPresent());
        assertEquals(utilisateur, result.get());
        assertNotNull(utilisateur.getDerniereConnexion());
        verify(utilisateurRepository, times(1)).findByEmailWithPersonne("jean@example.com");
        verify(passwordEncoder, times(1)).matches("password123", utilisateur.getMotDePasse());
        verify(utilisateurRepository, times(1)).save(utilisateur);
    }

    @Test
    void testAuthentifierWrongPassword() {
        // Given
        when(utilisateurRepository.findByEmailWithPersonne("jean@example.com"))
            .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongPassword", utilisateur.getMotDePasse()))
            .thenReturn(false);

        // When
        Optional<Utilisateur> result = authService.authentifier("jean@example.com", "wrongPassword");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, times(1)).findByEmailWithPersonne("jean@example.com");
        verify(passwordEncoder, times(1)).matches("wrongPassword", utilisateur.getMotDePasse());
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    void testAuthentifierUserNotFound() {
        // Given
        when(utilisateurRepository.findByEmailWithPersonne("unknown@example.com"))
            .thenReturn(Optional.empty());

        // When
        Optional<Utilisateur> result = authService.authentifier("unknown@example.com", "password123");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, times(1)).findByEmailWithPersonne("unknown@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthentifierInactiveUser() {
        // Given
        utilisateur.setActif(false);
        when(utilisateurRepository.findByEmailWithPersonne("jean@example.com"))
            .thenReturn(Optional.of(utilisateur));

        // When
        Optional<Utilisateur> result = authService.authentifier("jean@example.com", "password123");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, times(1)).findByEmailWithPersonne("jean@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testFindByEmail() {
        // Given
        when(utilisateurRepository.findByEmailWithPersonne("jean@example.com"))
            .thenReturn(Optional.of(utilisateur));

        // When
        Optional<Utilisateur> result = authService.findByEmail("jean@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(utilisateur, result.get());
        verify(utilisateurRepository, times(1)).findByEmailWithPersonne("jean@example.com");
    }

    @Test
    void testFindById() {
        // Given
        when(utilisateurRepository.findByIdWithDetails(1))
            .thenReturn(Optional.of(utilisateur));

        // When
        Optional<Utilisateur> result = authService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(utilisateur, result.get());
        verify(utilisateurRepository, times(1)).findByIdWithDetails(1);
    }

    @Test
    void testEmailExists() {
        // Given
        when(utilisateurRepository.existsByEmail("jean@example.com")).thenReturn(true);

        // When
        boolean exists = authService.emailExists("jean@example.com");

        // Then
        assertTrue(exists);
        verify(utilisateurRepository, times(1)).existsByEmail("jean@example.com");
    }

    @Test
    void testEmailNotExists() {
        // Given
        when(utilisateurRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        // When
        boolean exists = authService.emailExists("unknown@example.com");

        // Then
        assertFalse(exists);
        verify(utilisateurRepository, times(1)).existsByEmail("unknown@example.com");
    }

    @Test
    void testUpdatePassword() {
        // Given
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.encode("newPassword")).thenReturn("$2a$10$newEncodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        authService.updatePassword(1, "newPassword");

        // Then
        verify(utilisateurRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(utilisateurRepository, times(1)).save(utilisateur);
        assertEquals("$2a$10$newEncodedPassword", utilisateur.getMotDePasse());
    }

    @Test
    void testUpdatePasswordUserNotFound() {
        // Given
        when(utilisateurRepository.findById(999)).thenReturn(Optional.empty());

        // When
        authService.updatePassword(999, "newPassword");

        // Then
        verify(utilisateurRepository, times(1)).findById(999);
        verify(passwordEncoder, never()).encode(anyString());
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }
}

