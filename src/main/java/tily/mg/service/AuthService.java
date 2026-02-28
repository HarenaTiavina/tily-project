package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tily.mg.entity.Fivondronana;
import tily.mg.entity.Personne;
import tily.mg.entity.Utilisateur;
import tily.mg.repository.FivondronanaRepository;
import tily.mg.repository.PersonneRepository;
import tily.mg.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private FivondronanaRepository fivondronanaRepository;

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Créer un compte pour un Fivondronana
     */
    public Utilisateur creerCompteFivondronana(String email, String motDePasse, Integer fivondronanaId) {
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Vérifier que le Fivondronana existe
        Fivondronana fivondronana = fivondronanaRepository.findById(fivondronanaId)
            .orElseThrow(() -> new RuntimeException("Fivondronana non trouvé"));

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setFivondronana(fivondronana);
        utilisateur.setRole("USER");
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Créer un compte admin
     */
    public Utilisateur creerCompteAdmin(String email, String motDePasse) {
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Créer l'utilisateur admin
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setFivondronana(null); // Admin n'a pas de Fivondronana
        utilisateur.setRole("ADMIN");
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Créer un compte pour un Filoha
     */
    public Utilisateur creerCompteFiloha(String email, String motDePasse, Integer personneId) {
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Vérifier que la Personne existe et est un Filoha
        Personne personne = personneRepository.findById(personneId)
            .orElseThrow(() -> new RuntimeException("Personne non trouvée"));
        
        if (!personne.isFiloha()) {
            throw new RuntimeException("Cette personne n'est pas un Filoha");
        }

        // Créer l'utilisateur Filoha
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setPersonne(personne);
        utilisateur.setFivondronana(null); // Filoha n'a pas de Fivondronana
        utilisateur.setRole("FILOHA");
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Authentification d'un utilisateur
     */
    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmailWithFivondronana(email);
        
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            
            // Vérifier si le compte est actif
            if (!utilisateur.getActif()) {
                return Optional.empty();
            }
            
            // Vérifier le mot de passe
            if (passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
                // Mettre à jour la dernière connexion
                utilisateur.setDerniereConnexion(LocalDateTime.now());
                utilisateurRepository.save(utilisateur);
                return Optional.of(utilisateur);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Trouver un utilisateur par email
     */
    public Optional<Utilisateur> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return utilisateurRepository.findByEmailWithFivondronana(email.trim());
    }

    /**
     * Trouver un utilisateur par ID avec détails
     */
    public Optional<Utilisateur> findById(Integer id) {
        return utilisateurRepository.findByIdWithDetails(id);
    }

    /**
     * Vérifier si un email existe
     */
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Mettre à jour le mot de passe
     */
    public void updatePassword(Integer userId, String newPassword) {
        utilisateurRepository.findById(userId).ifPresent(user -> {
            user.setMotDePasse(passwordEncoder.encode(newPassword));
            utilisateurRepository.save(user);
        });
    }

    /**
     * Récupérer tous les Fivondronana
     */
    public List<Fivondronana> findAllFivondronana() {
        return fivondronanaRepository.findAll();
    }

    /**
     * Récupérer tous les utilisateurs non-admin
     */
    public List<Utilisateur> findAllNonAdminUsers() {
        return utilisateurRepository.findAllNonAdmin();
    }

    /**
     * Récupérer tous les utilisateurs (non-admin) avec leurs relations chargées
     */
    public List<Utilisateur> findAllNonAdminUsersWithRelations() {
        List<Utilisateur> users = utilisateurRepository.findAllNonAdmin();
        // Les relations sont déjà chargées via les requêtes dans le repository
        return users;
    }

    /**
     * Activer/Désactiver un utilisateur
     */
    public void toggleUserActif(Integer userId) {
        utilisateurRepository.findById(userId).ifPresent(user -> {
            user.setActif(!user.getActif());
            utilisateurRepository.save(user);
        });
    }

    /**
     * Supprimer un utilisateur
     */
    public void deleteUser(Integer userId) {
        utilisateurRepository.deleteById(userId);
    }
}
