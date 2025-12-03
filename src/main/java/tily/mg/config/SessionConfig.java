package tily.mg.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour Spring Session
 * Spring Session JDBC est temporairement désactivé pour éviter les conflits de transactions
 * Les sessions utilisent maintenant le stockage par défaut (mémoire)
 * 
 * TODO: Reconfigurer Spring Session JDBC avec un DataSourceTransactionManager séparé
 */
@Configuration
public class SessionConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionConfig.class);
    
    public SessionConfig() {
        logger.info("Spring Session configuration initialized (in-memory sessions)");
    }
}

