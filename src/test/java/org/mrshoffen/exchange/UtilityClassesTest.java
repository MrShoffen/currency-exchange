package org.mrshoffen.exchange;

import org.junit.jupiter.api.Test;
import org.mrshoffen.exchange.testutils.DatabaseManipulation;
import org.mrshoffen.exchange.utils.ConnectionManager;
import org.mrshoffen.exchange.utils.PropertiesUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilityClassesTest {
    private static final String TEST_SQL = "SELECT * FROM currencies;";

    @Test
    void checkPropertyRead() {
        String property = PropertiesUtil.get("db.url");
        assertEquals("jdbc:sqlite::resource:database/database_test.db", property);

        String property2 = PropertiesUtil.get("not_present");
        assertNull(property2);
    }

    @Test
    void shouldConnectProperlyAndReadTransactionIsolation() {
        try (Connection connection = ConnectionManager.getConnection()) {
            assertEquals(Connection.TRANSACTION_SERIALIZABLE, connection.getTransactionIsolation());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateTableWith5Rows() {
        DatabaseManipulation.dropTestTables();
        DatabaseManipulation.createAndFillTestTables();
        try (Connection connection = ConnectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(TEST_SQL);
            int rowNumber = 0;
            while (resultSet.next()) {
                rowNumber++;
            }
            assertEquals(5, rowNumber);
            DatabaseManipulation.dropTestTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}