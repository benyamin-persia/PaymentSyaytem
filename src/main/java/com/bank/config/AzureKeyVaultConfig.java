package com.bank.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AzureKeyVaultConfig {

    @Value("${azure.keyvault.uri}")
    private String keyVaultUri;                                                                               // Concept: Field | Why: stores persistent state required by this type.

    @Value("${azure.keyvault.database-secret-name:}")
    private String databaseSecretName;                                                                        // Concept: Field | Why: stores persistent state required by this type.

    @Bean
    public DataSource dataSource(Environment environment) {                                                   // Concept: Method | Why: encapsulates dataSource behavior for this component boundary.
        String url = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String driverClassName = environment.getProperty("spring.datasource.driver-class-name");
        String password = environment.getProperty("spring.datasource.password", "");

        if (isValidVaultUri(keyVaultUri) && databaseSecretName != null && !databaseSecretName.isBlank() && !databaseSecretName.contains("<")) { // Concept: Validation | Why: blocks invalid input before business logic executes.
            SecretClient secretClient = new SecretClientBuilder()
                    .vaultUrl(keyVaultUri)
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();
            KeyVaultSecret secret = secretClient.getSecret(databaseSecretName);
            password = secret.getValue();                                                                     // Concept: State Change | Why: updates runtime or domain state after checks pass.
        }

        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    private boolean isValidVaultUri(String uri) {                                                             // Concept: Method | Why: encapsulates isValidVaultUri behavior for this component boundary.
        if (uri == null || uri.isBlank() || uri.contains("<") || uri.contains(">") || uri.contains(" ")) {    // Concept: Validation | Why: blocks invalid input before business logic executes.
            return false;
        }
        try {
            new java.net.URL(uri);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
