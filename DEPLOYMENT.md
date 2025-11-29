# Guide de Déploiement - Tily

## Variables d'environnement requises

Pour déployer l'application en production, vous devez configurer les variables d'environnement suivantes sur votre serveur :

### Base de données PostgreSQL

```bash
DATABASE_URL=jdbc:postgresql://host:port/database
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=votre_mot_de_passe
```

**Exemple pour Railway :**
```bash
DATABASE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=votre_mot_de_passe_railway
```

**Exemple pour Heroku :**
```bash
DATABASE_URL=jdbc:postgresql://ec2-xxx-xxx-xxx-xxx.compute-1.amazonaws.com:5432/xxxxx
DATABASE_USERNAME=xxxxx
DATABASE_PASSWORD=xxxxx
```

### Port du serveur

```bash
PORT=8080
```

> **Note :** La plupart des plateformes cloud (Railway, Heroku, etc.) définissent automatiquement la variable `PORT`. Vous n'avez généralement pas besoin de la configurer manuellement.

## Configuration sur différentes plateformes

### Railway

1. Allez dans votre projet Railway
2. Cliquez sur votre service
3. Allez dans l'onglet "Variables"
4. Ajoutez les variables :
   - `DATABASE_URL`
   - `DATABASE_USERNAME`
   - `DATABASE_PASSWORD`

### Heroku

```bash
heroku config:set DATABASE_URL="jdbc:postgresql://..."
heroku config:set DATABASE_USERNAME="postgres"
heroku config:set DATABASE_PASSWORD="votre_mot_de_passe"
```

### Docker

Dans votre `docker-compose.yml` ou lors du lancement :

```bash
docker run -e DATABASE_URL="jdbc:postgresql://..." \
           -e DATABASE_USERNAME="postgres" \
           -e DATABASE_PASSWORD="votre_mot_de_passe" \
           -e PORT=8080 \
           votre-image
```

### Serveur Linux (systemd)

Créez un fichier `/etc/systemd/system/tily.service` :

```ini
[Unit]
Description=Tily Application
After=network.target

[Service]
Type=simple
User=your-user
WorkingDirectory=/path/to/tily
ExecStart=/usr/bin/java -jar /path/to/tily/target/tily-0.0.1-SNAPSHOT.jar
Environment="DATABASE_URL=jdbc:postgresql://host:port/database"
Environment="DATABASE_USERNAME=postgres"
Environment="DATABASE_PASSWORD=votre_mot_de_passe"
Environment="PORT=8080"
Restart=always

[Install]
WantedBy=multi-user.target
```

## Vérification

Pour vérifier que les variables d'environnement sont bien configurées, vous pouvez :

1. **Sur Railway/Heroku** : Vérifiez dans l'interface web que les variables sont présentes
2. **En local** : Vérifiez avec `echo $DATABASE_URL` (Linux/Mac) ou `echo %DATABASE_URL%` (Windows)
3. **Dans l'application** : Les logs au démarrage afficheront les paramètres utilisés (sans le mot de passe)

## Développement local

Pour le développement local, vous pouvez :

1. **Créer un fichier `.env`** (non versionné) avec vos paramètres locaux
2. **Modifier directement `application.properties`** pour vos valeurs locales
3. **Utiliser les valeurs par défaut** définies dans `application.properties`

Les valeurs par défaut dans `application.properties` sont :
- `DATABASE_URL`: `jdbc:postgresql://localhost:5432/tily`
- `DATABASE_USERNAME`: `postgres`
- `DATABASE_PASSWORD`: `postgres`
- `PORT`: `8080`

## Sécurité

⚠️ **IMPORTANT :**
- Ne commitez JAMAIS les mots de passe dans le code
- Utilisez toujours des variables d'environnement pour les informations sensibles
- Le fichier `.env` doit être dans `.gitignore`
- Les valeurs par défaut dans `application.properties` sont uniquement pour le développement local

