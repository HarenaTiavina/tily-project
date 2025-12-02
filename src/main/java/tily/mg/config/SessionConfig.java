package tily.mg.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

/**
 * Configuration pour Spring Session JDBC
 * Permet de mieux gérer les erreurs lors du nettoyage automatique des sessions
 */
@Configuration
@EnableJdbcHttpSession
public class SessionConfig implements SchedulingConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionConfig.class);
    
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // La configuration du nettoyage des sessions est gérée automatiquement par Spring Session
        // Cette classe permet de personnaliser si nécessaire
        logger.info("Spring Session JDBC configuration initialized");
    }
}

