package org.mrshoffen.exchange.testutils;

import lombok.experimental.UtilityClass;
import org.mrshoffen.exchange.utils.ConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@UtilityClass
public class DatabaseManipulation {
    private static final String[] CREATE_TEST_TABLES_SQLs = parseSQLs("create_test_tables.sql");
    private static final String[] FILL_TEST_TABLES_SQLs = parseSQLs("fill_test_tables.sql");
    private static final String[] DROP_TEST_TABLES_SQLs = parseSQLs("drop_tables.sql");

    public static void createAndFillTestTables() {
        executeSQLs(CREATE_TEST_TABLES_SQLs);
        executeSQLs(FILL_TEST_TABLES_SQLs);
    }

    public static void dropTestTables() {
        executeSQLs(DROP_TEST_TABLES_SQLs);
    }

    private static String[] parseSQLs(String scriptName) {
        try (InputStream inputStream = DatabaseManipulation.class.getClassLoader().getResourceAsStream("database/scripts/" + scriptName)) {
            return new String(inputStream.readAllBytes()).split(";");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeSQLs(String[] SQLSs) {
        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            for (String SQL : SQLSs) {
                statement.executeUpdate(SQL);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
