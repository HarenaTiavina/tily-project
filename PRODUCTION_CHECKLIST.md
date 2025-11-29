# Checklist de Production - Tily

## ✅ Vérifications effectuées

### 1. Configuration Application
- ✅ Thymeleaf cache activé (`spring.thymeleaf.cache=true`)
- ✅ SQL logging désactivé (`spring.jpa.show-sql=false`)
- ✅ SQL formatting désactivé (`spring.jpa.properties.hibernate.format_sql=false`)
- ✅ Configuration serveur avec support PORT variable d'environnement
- ✅ Configuration HikariCP pour le pool de connexions
- ✅ Spring Session JDBC configuré

### 2. Sécurité
- ✅ CSRF activé (sauf pour `/auth/**`)
- ✅ Headers de sécurité HTTP configurés (HSTS, X-Frame-Options, Content-Type-Options)
- ✅ Authentification par rôle (ADMIN, USER)
- ✅ Protection des endpoints sensibles
- ✅ Mots de passe hashés avec BCrypt
- ✅ Sessions gérées en base de données

### 3. Base de données
- ✅ DDL auto désactivé (`spring.jpa.hibernate.ddl-auto=none`)
- ✅ SQL init désactivé (`spring.sql.init.mode=never`)
- ✅ Scripts SQL manuels disponibles (`database/schema.sql`, `database/data.sql`)
- ✅ Tables Spring Session créées (`database/spring_session.sql`)

### 4. Build et Déploiement
- ✅ DevTools exclu du build de production
- ✅ Spring Boot Maven Plugin configuré
- ✅ Pas de dépendances inutilisées critiques

### 5. Code
- ✅ Pas de TODO/FIXME critiques
- ✅ Pas de console.log ou System.out.println
- ✅ Gestion d'erreurs appropriée
- ✅ Transactions configurées

## ⚠️ Points d'attention

### 1. Dépendances OAuth2
Les dépendances OAuth2 sont présentes dans `pom.xml` mais ne semblent pas être utilisées :
- `spring-boot-starter-security-oauth2-authorization-server`
- `spring-boot-starter-security-oauth2-client`

**Recommandation** : Si non utilisées, les retirer pour réduire la taille du JAR.

### 2. Credentials de base de données
Les credentials sont actuellement en dur dans `application.properties`. 

**Recommandation pour production** : Utiliser des variables d'environnement :
```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

### 3. Mots de passe par défaut
Le fichier `database/data.sql` contient des mots de passe de test (123456).

**Action requise** : Changer tous les mots de passe par défaut en production !

## 📋 Actions avant déploiement

1. **Base de données** :
   - [ ] Exécuter `database/schema.sql` sur la base de production
   - [ ] Exécuter `database/spring_session.sql` sur la base de production
   - [ ] Exécuter `database/data.sql` (ou adapter avec des données réelles)
   - [ ] Vérifier que toutes les tables sont créées

2. **Configuration** :
   - [ ] Configurer les variables d'environnement pour la base de données
   - [ ] Vérifier que le PORT est configuré (Railway le fait automatiquement)
   - [ ] Vérifier les URLs de redirection si nécessaire

3. **Sécurité** :
   - [ ] Changer tous les mots de passe par défaut
   - [ ] Vérifier que le compte admin a un mot de passe fort
   - [ ] Tester l'authentification

4. **Tests** :
   - [ ] Tester la connexion à la base de données
   - [ ] Tester l'authentification (login/logout)
   - [ ] Tester les fonctionnalités principales (CRUD)
   - [ ] Tester les permissions (ADMIN vs USER)

5. **Monitoring** :
   - [ ] Configurer les logs (si nécessaire)
   - [ ] Vérifier les métriques de performance
   - [ ] Configurer les alertes (si nécessaire)

## 🚀 Commandes de déploiement

### Build
```bash
mvn clean package -DskipTests
```

### Vérification du JAR
```bash
java -jar target/tily-0.0.1-SNAPSHOT.jar
```

### Variables d'environnement recommandées (Railway/Heroku)
```
PORT=8080
DATABASE_URL=jdbc:postgresql://...
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=...
SPRING_PROFILES_ACTIVE=prod
```

## 📝 Notes

- Le projet utilise Spring Boot 4.0.0
- Java 17 requis
- PostgreSQL requis
- Les sessions sont stockées en base de données (JDBC)
- Le cache Thymeleaf est activé pour de meilleures performances

