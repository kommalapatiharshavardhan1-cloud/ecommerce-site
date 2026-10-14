package com.ecommerce.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataSourceConfig {

    @Value("${DB_URL:}")
    private String dbUrl;

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${DB_USERNAME:}")
    private String username;

    @Value("${DB_PASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        String resolvedUrl;
        String resolvedUser = (username == null) ? "" : username.trim();
        String resolvedPassword = (password == null) ? "" : password;

        String source = (dbUrl == null || dbUrl.isBlank()) ? databaseUrl : dbUrl;
        source = (source == null) ? "" : source.trim();

        if (source.isEmpty()) {
            resolvedUrl = "jdbc:postgresql://localhost:5432/ecommerce_db";
        } else if (source.startsWith("jdbc:")) {
            resolvedUrl = source;
        } else {
            // Neon provides DATABASE_URL in postgresql://user:pass@host:port/db?query form.
            try {
                URI uri = URI.create(source);
                String scheme = uri.getScheme();
                if (scheme == null || !scheme.startsWith("postgres")) {
                    resolvedUrl = source.startsWith("jdbc:") ? source : "jdbc:" + source;
                } else {
                    String host = uri.getHost();
                    int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                    String path = uri.getPath();
                    if (path == null || path.isEmpty()) {
                        path = "/neondb";
                    }
                    String query = uri.getQuery();
                    resolvedUrl = "jdbc:postgresql://" + host + ":" + port + path;
                    if (query != null && !query.isEmpty()) {
                        resolvedUrl += "?" + query;
                    }

                    String userInfo = uri.getUserInfo();
                    if (userInfo != null && !userInfo.isEmpty()) {
                        String[] parts = userInfo.split(":", 2);
                        if (resolvedUser.isEmpty()) {
                            resolvedUser = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                        }
                        if (resolvedPassword.isEmpty() && parts.length > 1) {
                            resolvedPassword = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                        }
                    }
                }
            } catch (Exception e) {
                resolvedUrl = source.startsWith("jdbc:") ? source : "jdbc:" + source;
            }
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(resolvedUrl);
        if (!resolvedUser.isEmpty()) {
            config.setUsername(resolvedUser);
        }
        if (!resolvedPassword.isEmpty()) {
            config.setPassword(resolvedPassword);
        }
        return new HikariDataSource(config);
    }
}
