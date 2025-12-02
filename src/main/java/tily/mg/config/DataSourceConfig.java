package tily.mg.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class DataSourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Vérifier que DATABASE_URL est fournie
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("DATABASE_URL n'est pas définie. Veuillez configurer la variable d'environnement DATABASE_URL.");
        }
        
        String jdbcUrl;
        String username;
        String password;
        String host;
        int port;
        String dbName;
        
        try {
            // Parser l'URL PostgreSQL standard (postgresql://user:pass@host:port/db)
            URI dbUri = new URI(databaseUrl);
            
            // Extraire username et password depuis userInfo (peut contenir des caractères encodés)
            if (dbUri.getUserInfo() != null) {
                String[] userInfo = dbUri.getUserInfo().split(":", 2);
                username = userInfo.length > 0 ? URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8) : null;
                password = userInfo.length > 1 ? URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8) : null;
            } else {
                throw new IllegalStateException("DATABASE_URL ne contient pas d'informations d'authentification (user:pass@)");
            }
            
            // Extraire host, port et database
            host = dbUri.getHost();
            if (host == null) {
                throw new IllegalStateException("DATABASE_URL ne contient pas d'host valide");
            }
            
            port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
            
            String path = dbUri.getPath();
            dbName = path != null && path.length() > 1 ? path.substring(1) : null;
            if (dbName == null || dbName.isEmpty()) {
                throw new IllegalStateException("DATABASE_URL ne contient pas de nom de base de données");
            }
            
            // Construire l'URL JDBC avec paramètres de connexion
            jdbcUrl = buildJdbcUrl(host, port, dbName);
            
            logger.info("Configuration DB depuis DATABASE_URL");
            logger.info("Host: {}, Port: {}, Database: {}, Username: {}", host, port, dbName, username);
            
        } catch (URISyntaxException e) {
            throw new RuntimeException("Erreur lors du parsing de DATABASE_URL: " + databaseUrl, e);
        }
        
        logger.info("JDBC URL configurée (sans credentials): {}", jdbcUrl.replaceAll("password=[^&]*", "password=***"));
        
        // Configuration HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        // Configuration du pool de connexions
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(0); // Ne pas créer de connexions au démarrage
        config.setConnectionTimeout(30000); // 30 secondes pour établir la connexion
        config.setIdleTimeout(300000); // 5 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setLeakDetectionThreshold(60000); // 60 secondes
        
        // IMPORTANT: Désactiver complètement la validation au démarrage
        // Cela permet à l'application de démarrer même si la DB n'est pas disponible
        config.setInitializationFailTimeout(-1); // -1 = ne jamais échouer au démarrage
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000); // 3 secondes pour validation
        
        // Auto-commit
        config.setAutoCommit(true);
        
        // Créer la DataSource sans validation initiale
        // Les connexions seront créées à la demande quand nécessaire
        HikariDataSource dataSource = new HikariDataSource(config);
        
        logger.info("HikariCP DataSource configurée - l'application peut démarrer même si la DB n'est pas disponible");
        logger.info("Pool de connexions: min={}, max={}", config.getMinimumIdle(), config.getMaximumPoolSize());
        logger.warn("La validation de connexion au démarrage est désactivée - les connexions seront créées à la demande");
        
        return dataSource;
    }
    
    private String buildJdbcUrl(String host, int port, String dbName) {
        String baseUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        return appendConnectionParams(baseUrl, host);
    }
    
    private String appendConnectionParams(String jdbcUrl, String host) {
        StringBuilder url = new StringBuilder(jdbcUrl);
        
        // Déterminer si on doit ajouter ? ou &
        String separator = jdbcUrl.contains("?") ? "&" : "?";
        
        // Paramètres de connexion PostgreSQL optimisés pour Railway
        url.append(separator).append("connectTimeout=30"); // 30 secondes pour établir la connexion
        url.append("&socketTimeout=60"); // 60 secondes pour les opérations socket
        url.append("&loginTimeout=30"); // 30 secondes pour l'authentification
        url.append("&tcpKeepAlive=true"); // Garder la connexion alive
        url.append("&ApplicationName=tily"); // Nom de l'application
        
        // SSL - Pour Railway, les connexions internes (postgres.railway.internal) n'ont pas besoin de SSL
        String sslMode = System.getenv("DB_SSL");
        
        if (host != null && host.contains("railway.internal")) {
            // Connexion interne Railway - pas besoin de SSL
            url.append("&ssl=false");
            url.append("&sslmode=disable");
            logger.debug("Mode SSL désactivé pour connexion interne Railway: {}", host);
        } else if (sslMode != null && (sslMode.equalsIgnoreCase("true") || sslMode.equalsIgnoreCase("require"))) {
            // SSL forcé via variable d'environnement
            url.append("&ssl=true");
            url.append("&sslmode=require");
        } else {
            // Par défaut, essayer avec SSL mais accepter sans
            url.append("&ssl=true");
            url.append("&sslmode=prefer");
        }
        
        return url.toString();
    }
}

