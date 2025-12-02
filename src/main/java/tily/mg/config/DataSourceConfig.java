package tily.mg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${PGUSER:postgres}")
    private String pgUser;

    @Value("${PGPASSWORD:postgres}")
    private String pgPassword;

    @Value("${PGDATABASE:tily}")
    private String pgDatabase;

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
                
                username = dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":")[0] : pgUser;
                password = dbUri.getUserInfo() != null && dbUri.getUserInfo().split(":").length > 1 
                    ? dbUri.getUserInfo().split(":")[1] 
                    : pgPassword;
                String host = dbUri.getHost();
                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String path = dbUri.getPath();
                String dbName = path != null && path.length() > 1 ? path.substring(1) : pgDatabase;
                
                // Construire l'URL JDBC
                jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Erreur lors du parsing de DATABASE_URL: " + databaseUrl, e);
            }
        } else if (databaseUrl != null && !databaseUrl.isEmpty() && databaseUrl.startsWith("jdbc:")) {
            // Si DATABASE_URL est déjà au format JDBC, l'utiliser directement
            jdbcUrl = databaseUrl;
            username = pgUser;
            password = pgPassword;
        } else {
            // Sinon, utiliser les variables PGUSER, PGPASSWORD, PGDATABASE
            // Note: Pour Railway, on peut utiliser postgres.railway.internal comme host par défaut
            jdbcUrl = String.format("jdbc:postgresql://postgres.railway.internal:5432/%s", pgDatabase);
            username = pgUser;
            password = pgPassword;
        }
        
        return DataSourceBuilder.create()
            .url(jdbcUrl)
            .username(username)
            .password(password)
            .driverClassName("org.postgresql.Driver")
            .build();
    }
}

