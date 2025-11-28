# Tests Unitaires - Tily

Ce répertoire contient les tests unitaires pour l'application Tily.

## Structure des Tests

### Services
- **PersonneServiceTest** : Tests pour le service de gestion des personnes (CRUD, filtres, création de responsables et élèves)
- **AuthServiceTest** : Tests pour le service d'authentification (inscription, connexion, gestion des mots de passe)

### Entités
- **PersonneTest** : Tests pour l'entité Personne (méthodes helper, logique métier)
- **FizaranaTest** : Tests pour l'entité Fizarana (Section)
- **AndraikitraTest** : Tests pour l'entité Andraikitra (Poste)

## Exécution des Tests

### Avec Maven
```bash
# Exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=PersonneServiceTest

# Exécuter avec rapport de couverture
mvn test jacoco:report
```

### Avec IDE
- **IntelliJ IDEA** : Clic droit sur le fichier de test → "Run 'TestName'"
- **Eclipse** : Clic droit sur le fichier de test → "Run As" → "JUnit Test"
- **VS Code** : Utiliser l'extension Java Test Runner

## Couverture des Tests

Les tests couvrent :
- ✅ Toutes les opérations CRUD
- ✅ Les méthodes de filtrage (responsables, élèves)
- ✅ La création de responsables et d'élèves avec leurs spécificités
- ✅ L'authentification et l'inscription
- ✅ Les méthodes helper des entités
- ✅ Les cas limites (null, valeurs vides, etc.)

## Bonnes Pratiques

1. **Nommage** : Les méthodes de test suivent le pattern `testMethodName_Scenario_ExpectedResult`
2. **Arrange-Act-Assert** : Chaque test suit le pattern AAA
3. **Mocking** : Utilisation de Mockito pour isoler les dépendances
4. **Assertions** : Utilisation de JUnit 5 assertions pour une meilleure lisibilité

## Ajout de Nouveaux Tests

Lors de l'ajout de nouvelles fonctionnalités :
1. Créer les tests correspondants
2. S'assurer que tous les tests passent
3. Maintenir une couverture de code élevée (>80%)

