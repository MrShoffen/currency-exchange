package org.mrshoffen.exchange.utils.exchange.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;

@UtilityClass
public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String DRIVER_KEY = "db.driver";
    private static final String POOL_SIZE_KEY = "db.pool.size";

    private static final HikariDataSource CONNECTION_POOL;

    static {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(PropertiesUtil.get(URL_KEY));
        hikariConfig.setDriverClassName(PropertiesUtil.get(DRIVER_KEY));
        hikariConfig.setMaximumPoolSize(parseInt(PropertiesUtil.get(POOL_SIZE_KEY)));

        CONNECTION_POOL = new HikariDataSource(hikariConfig);
    }

    public static Connection getConnection() throws SQLException {
        return CONNECTION_POOL.getConnection();
    }
}
