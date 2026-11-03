package ar.com.GestionTurnos.config;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ajusta la tabla turnos al nuevo id String legible (antes UUID).
 */
@Component
public class TurnosSchemaMigrator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TurnosSchemaMigrator.class);

    private final JdbcTemplate jdbcTemplate;
    private final EntityManagerFactory entityManagerFactory;

    public TurnosSchemaMigrator(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'turnos'",
                    Integer.class);
            if (count == null || count == 0) {
                return;
            }

            String dataType = jdbcTemplate.queryForObject(
                    "SELECT data_type FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'turnos' AND column_name = 'id'",
                    String.class);

            if (dataType != null && !"varchar".equalsIgnoreCase(dataType) && !"char".equalsIgnoreCase(dataType)) {
                log.warn("Columna turnos.id es '{}' (se esperaba VARCHAR). Recreando tabla turnos...", dataType);
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
                jdbcTemplate.execute("DROP TABLE IF EXISTS turnos");
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

                SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
                sessionFactory.getSchemaManager().exportMappedObjects(false);
                log.info("Tabla turnos recreada con id VARCHAR legible.");
            }
        } catch (Exception e) {
            log.warn("No se pudo verificar/migrar esquema de turnos: {}", e.getMessage());
        }
    }
}
