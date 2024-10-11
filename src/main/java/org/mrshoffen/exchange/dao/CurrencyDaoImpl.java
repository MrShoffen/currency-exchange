package org.mrshoffen.exchange.dao;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.exception.DatabaseException;
import org.mrshoffen.exchange.utils.ConnectionManager;
import org.sqlite.SQLiteErrorCode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class CurrencyDaoImpl implements CurrencyDao {
    private static final CurrencyDao INSTANCE = new CurrencyDaoImpl();

    private static final String FIND_ALL_SQL = "SELECT id, code, full_name, sign FROM currencies";
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + " WHERE code = ?";
    private static final String SAVE_CURRENCY_SQL = "INSERT INTO currencies (code, full_name, sign)" +
                                                    " VALUES (?, ?, ?)" +
                                                    "RETURNING id, code, full_name, sign";

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Currency> findAll() {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Currency> allCurrencies = new ArrayList<>();
            while (resultSet.next()) {
                allCurrencies.add(buildCurrency(resultSet));
            }
            return allCurrencies;
        } catch (SQLException e) {
            throw new DatabaseException("Error! Failed to find currencies in database");
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DatabaseException("Error! Failed to find currency with code: " + code + " in database.");

        }
    }

    @Override
    public Optional<Currency> save(Currency entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CURRENCY_SQL)) {
            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getFullName());
            preparedStatement.setString(3, entity.getSign());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DatabaseException("Error! Failed to save currency with code: %s in database."
                        .formatted(entity.getCode()));
            }
            entity.setId(resultSet.getInt("id"));
            return Optional.of(entity);

        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                return Optional.empty();
            }
            throw new DatabaseException("Error! Failed to save currency with code: %s in database."
                    .formatted(entity.getCode()));
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getInt("id"))
                .code(resultSet.getString("code"))
                .fullName(resultSet.getString("full_name"))
                .sign(resultSet.getString("sign"))
                .build();
    }
}
