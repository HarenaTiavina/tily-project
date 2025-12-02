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

    @Value("${PGUSER:postgres}")
    private String pgUser;

    @Value("${PGPASSWORD:postgres}")
    private String pgPassword;

    @Value("${PGDATABASE:tily}")
    private String pgDatabase;

    @Value("${PGHOST:}")
    private String pgHost;

    @Value("${PGPORT:5432}")
    private String pgPort;

    @Bean
    @Primary
    public DataSource dataSource() {
        String jdbcUrl;
        String username;
        String password;
        
        // Si DATABASE_URL est fourni et au format postgresql://, le parser
        if (databaseUrl != null && !databaseUrl.isEmpty() && !databaseUrl.startsWith("jdbc:")) {
            try {
                // Parser l'URL PostgreSQL standard (postgresql://user:pass@host:port/db)
                URI dbUri = new URI(databaseUrl);
                
                // Extraire username et password depuis userInfo (peut contenir des caractères encodés)
                if (dbUri.getUserInfo() != null) {
                    String[] userInfo = dbUri.getUserInfo().split(":", 2);
                    username = userInfo.length > 0 ? URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8) : pgUser;
                    password = userInfo.length > 1 ? URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8) : pgPassword;
                } else {
                    username = pgUser;
                    password = pgPassword;
                }
                String host = dbUri.getHost();
                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String path = dbUri.getPath();
                String dbName = path != null && path.length() > 1 ? path.substring(1) : pgDatabase;
                
                // Construire l'URL JDBC avec paramètres de connexion
                jdbcUrl = buildJdbcUrl(host, port, dbName);
                logger.info("Configuration DB depuis DATABASE_URL - Host: {}, Port: {}, Database: {}", host, port, dbName);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Erreur lors du parsing de DATABASE_URL: " + databaseUrl, e);
            }
        } else if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("jdbc:")) {
            // Si DATABASE_URL est déjà au format JDBC, l'utiliser directement
            jdbcUrl = databaseUrl;
            // Ajouter les paramètres de connexion s'ils ne sont pas déjà présents
            if (!jdbcUrl.contains("connectTimeout")) {
                jdbcUrl = appendConnectionParams(jdbcUrl);
            }
            username = pgUser;
            password = pgPassword;
        } else {
            // Sinon, utiliser les variables PGUSER, PGPASSWORD, PGDATABASE, PGHOST, PGPORT
            String host = pgHost != null && !pgHost.isEmpty() ? pgHost : "postgres.railway.internal";
            int port = pgPort != null && !pgPort.isEmpty() ? Integer.parseInt(pgPort) : 5432;
            jdbcUrl = buildJdbcUrl(host, port, pgDatabase);
            username = pgUser;
            password = pgPassword;
            logger.info("Configuration DB depuis variables PG* - Host: {}, Port: {}, Database: {}", host, port, pgDatabase);
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
        config.setMinimumIdle(2);
        config.setConnectionTimeout(60000); // 60 secondes
        config.setIdleTimeout(300000); // 5 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setLeakDetectionThreshold(60000); // 60 secondes
        
        // Validation de connexion
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000); // 5 secondes
        
        // Auto-commit
        config.setAutoCommit(true);
        
        return new HikariDataSource(config);
    }
    
    private String buildJdbcUrl(String host, int port, String dbName) {
        String baseUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        return appendConnectionParams(baseUrl);
    }
    
    private String appendConnectionParams(String jdbcUrl) {
        StringBuilder url = new StringBuilder(jdbcUrl);
        
        // Déterminer si on doit ajouter ? ou &
        String separator = jdbcUrl.contains("?") ? "&" : "?";
        
        // Paramètres de connexion PostgreSQL
        url.append(separator).append("connectTimeout=30"); // 30 secondes pour établir la connexion
        url.append("&socketTimeout=60"); // 60 secondes pour les opérations socket
        url.append("&loginTimeout=30"); // 30 secondes pour l'authentification
        url.append("&tcpKeepAlive=true"); // Garder la connexion alive
        url.append("&ApplicationName=tily"); // Nom de l'application
        
        // SSL - configuré pour permettre les connexions SSL si nécessaire (Railway, etc.)
        // Par défaut, on essaie d'abord sans SSL, puis avec SSL si échec
        // Pour forcer SSL, définir DB_SSL=true dans les variables d'environnement
        String sslMode = System.getenv("DB_SSL");
        if (sslMode != null && (sslMode.equalsIgnoreCase("true") || sslMode.equalsIgnoreCase("require"))) {
            url.append("&ssl=true");
            url.append("&sslmode=require");
        } else {
            // Mode préféré : SSL si disponible, sinon sans SSL
            url.append("&ssl=true");
            url.append("&sslmode=prefer");
        }
        
        return url.toString();
    }
}

