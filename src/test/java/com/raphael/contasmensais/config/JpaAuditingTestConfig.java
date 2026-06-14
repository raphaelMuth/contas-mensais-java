package com.raphael.contasmensais.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita JPA Auditing nos testes @DataJpaTest.
 * A aplicação principal usa @EnableJpaAuditing em ContasMensaisApplication,
 * que não é carregada nesse contexto parcial.
 */
@TestConfiguration
@EnableJpaAuditing
public class JpaAuditingTestConfig {
}
