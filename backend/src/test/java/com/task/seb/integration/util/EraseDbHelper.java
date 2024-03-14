package com.task.seb.integration.util;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EraseDbHelper {
    private final EntityManager entityManager;
    private static final Set<String> IGNORED_TABLES = Set.of("DATABASECHANGELOG", "DATABASECHANGELOGLOCK");

    @Transactional
    @SuppressWarnings("unchecked")
    public void cleanupDatabase() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        List<String> tableNames = entityManager.createNativeQuery(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'", String.class).getResultList();

        for (String tableName : tableNames) {
            if (!IGNORED_TABLES.contains(tableName)) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            }
        }

        List<String> sequenceNames = entityManager.createNativeQuery(
            "SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'", String.class).getResultList();

        for (String sequenceName : sequenceNames) {
            entityManager.createNativeQuery("ALTER SEQUENCE " + sequenceName + " RESTART WITH 1").executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}