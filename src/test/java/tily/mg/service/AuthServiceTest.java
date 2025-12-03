package tily.mg.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tily.mg.entity.Fivondronana;
import tily.mg.entity.Utilisateur;
import tily.mg.repository.FivondronanaRepository;
import tily.mg.repository.UtilisateurRepository;

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
    private FivondronanaRepository fivondronanaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Utilisateur utilisateur;
    private Fivondronana fivondronana;

    @BeforeEach
    void setUp() {
        // Setup Fivondronana
        fivondronana = new Fivondronana();
        fivondronana.setId(1);
        fivondronana.setNom("Antananarivo Renivohitra");
        fivondronana.setCode("ANT-R");

        // Setup Utilisateur
        utilisateur = new Utilisateur();
        utilisateur.setId(1);
        utilisateur.setEmail("antananarivo@example.com");
        utilisateur.setMotDePasse("$2a$10$encodedPassword");
        utilisateur.setRole("USER");
        utilisateur.setActif(true);
        utilisateur.setFivondronana(fivondronana);
        utilisateur.setDateCreation(LocalDateTime.now());
    }

    @Test
    void testCreerCompteFivondronanaSuccess() {
        // Given
        when(utilisateurRepository.existsByEmail("antananarivo@example.com")).thenReturn(false);
        when(fivondronanaRepository.findById(1)).thenReturn(Optional.of(fivondronana));
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        Utilisateur result = authService.creerCompteFivondronana(
            "antananarivo@example.com",
            "password123",
            1
        );

        // Then
        assertNotNull(result);
        assertEquals("antananarivo@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
        assertTrue(result.getActif());
        assertNotNull(result.getFivondronana());
        assertEquals("Antananarivo Renivohitra", result.getFivondronana().getNom());
        verify(utilisateurRepository, times(1)).existsByEmail("antananarivo@example.com");
        verify(fivondronanaRepository, times(1)).findById(1);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    void testCreerCompteFivondronanaEmailAlreadyExists() {
        // Given
        when(utilisateurRepository.existsByEmail("antananarivo@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.creerCompteFivondronana(
                "antananarivo@example.com",
                "password123",
                1
            );
        });

        assertEquals("Cet email est déjà utilisé", exception.getMessage());
        verify(utilisateurRepository, times(1)).existsByEmail("antananarivo@example.com");
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    void testCreerCompteFivondronanaFivondronanaNotFound() {
        // Given
        when(utilisateurRepository.existsByEmail("antananarivo@example.com")).thenReturn(false);
        when(fivondronanaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.creerCompteFivondronana(
                "antananarivo@example.com",
                "password123",
                999
            );
        });

        assertEquals("Fivondronana non trouvé", exception.getMessage());
        verify(utilisateurRepository, times(1)).existsByEmail("antananarivo@example.com");
        verify(fivondronanaRepository, times(1)).findById(999);
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    void testCreerCompteAdminSuccess() {
        // Given
        Utilisateur adminUser = new Utilisateur();
        adminUser.setId(2);
        adminUser.setEmail("admin@example.com");
        adminUser.setMotDePasse("$2a$10$encodedPassword");
        adminUser.setRole("ADMIN");
        adminUser.setActif(true);
        adminUser.setFivondronana(null);

        when(utilisateurRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("adminPass")).thenReturn("$2a$10$encodedPassword");
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(adminUser);

        // When
        Utilisateur result = authService.creerCompteAdmin("admin@example.com", "adminPass");

        // Then
        assertNotNull(result);
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());
        assertTrue(result.getActif());
        assertNull(result.getFivondronana());
        verify(utilisateurRepository, times(1)).existsByEmail("admin@example.com");
        verify(passwordEncoder, times(1)).encode("adminPass");
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }

    @Test
    void testAuthentifierSuccess() {
        // Given
        when(utilisateurRepository.findByEmailWithFivondronana("antananarivo@example.com"))
            .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password123", utilisateur.getMotDePasse()))
            .thenReturn(true);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        Optional<Utilisateur> result = authService.authentifier("antananarivo@example.com", "password123");

        // Then
        assertTrue(result.isPresent());
        assertEquals(utilisateur, result.get());
        assertNotNull(utilisateur.getDerniereConnexion());
        verify(utilisateurRepository, times(1)).findByEmailWithFivondronana("antananarivo@example.com");
        verify(passwordEncoder, times(1)).matches("password123", utilisateur.getMotDePasse());
        verify(utilisateurRepository, times(1)).save(utilisateur);
    }

    @Test
    void testAuthentifierWrongPassword() {
        // Given
        when(utilisateurRepository.findByEmailWithFivondronana("antananarivo@example.com"))
            .thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongPassword", utilisateur.getMotDePasse()))
            .thenReturn(false);

        // When
        Optional<Utilisateur> result = authService.authentifier("antananarivo@example.com", "wrongPassword");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, times(1)).findByEmailWithFivondronana("antananarivo@example.com");
        verify(passwordEncoder, times(1)).matches("wrongPassword", utilisateur.getMotDePasse());
        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }

    @Test
    void testAuthentifierUserNotFound() {
        // Given
        when(utilisateurRepository.findByEmailWithFivondronana("unknown@example.com"))
            .thenReturn(Optional.empty());

        // When
        Optional<Utilisateur> result = authService.authentifier("unknown@example.com", "password123");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, times(1)).findByEmailWithFivondronana("unknown@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testAuthentifierInactiveUser() {
        // Given
        utilisateur.setActif(false);
        when(utilisateurRepository.findByEmailWithFivondronana("antananarivo@example.com"))
            .thenReturn(Optional.of(utilisateur));

        // When
        Optional<Utilisateur> result = authService.authentifier("antananarivo@example.com", "password123");

        // Then
        assertFalse(result.isPresent());
        verify(utilisateurRepository, times(1)).findByEmailWithFivondronana("antananarivo@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testFindByEmail() {
        // Given
        when(utilisateurRepository.findByEmailWithFivondronana("antananarivo@example.com"))
            .thenReturn(Optional.of(utilisateur));

        // When
        Optional<Utilisateur> result = authService.findByEmail("antananarivo@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals(utilisateur, result.get());
        verify(utilisateurRepository, times(1)).findByEmailWithFivondronana("antananarivo@example.com");
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
        when(utilisateurRepository.existsByEmail("antananarivo@example.com")).thenReturn(true);

        // When
        boolean exists = authService.emailExists("antananarivo@example.com");

        // Then
        assertTrue(exists);
        verify(utilisateurRepository, times(1)).existsByEmail("antananarivo@example.com");
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

    @Test
    void testToggleUserActif() {
        // Given
        when(utilisateurRepository.findById(1)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(any(Utilisateur.class))).thenReturn(utilisateur);

        // When
        authService.toggleUserActif(1);

        // Then
        verify(utilisateurRepository, times(1)).findById(1);
        verify(utilisateurRepository, times(1)).save(utilisateur);
        assertFalse(utilisateur.getActif()); // Was true, now false
    }

    @Test
    void testDeleteUser() {
        // When
        authService.deleteUser(1);

        // Then
        verify(utilisateurRepository, times(1)).deleteById(1);
    }
}
