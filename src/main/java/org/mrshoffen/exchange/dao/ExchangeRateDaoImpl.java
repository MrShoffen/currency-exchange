package org.mrshoffen.exchange.dao;

import org.mrshoffen.exchange.entity.Currency;
import org.mrshoffen.exchange.entity.ExchangeRate;
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


public class ExchangeRateDaoImpl implements ExchangeRateDao {

    private static final String FIND_ALL_SQL = """
                                               SELECT ex_rate.id         id,
                                                      ex_rate.rate       rate,
                                                      base_c.id          bc_id,
                                                      base_c.code        bc_code,
                                                      base_c.full_name   bc_full_name,
                                                      base_c.sign        bc_sign,
                                                      target_c.id        tc_id,
                                                      target_c.code      tc_code,
                                                      target_c.full_name tc_full_name,
                                                      target_c.sign      tc_sign
                                               FROM exchange_rates ex_rate
                                                        JOIN currencies base_c ON ex_rate.base_currency_id = base_c.id
                                                        JOIN currencies target_c ON ex_rate.target_currency_id = target_c.id
                                               """;

    private static final String FIND_BY_CODES_SQL = FIND_ALL_SQL + " WHERE base_c.code = ? AND target_c.code = ?";

    private static final String SAVE_EXCHANGE_RATE_SQL = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)" +
                                                         " VALUES (?, ?, ?)" +
                                                         "RETURNING id";

    private static final String UPDATE_EXCHANGE_RATE_SQL = "UPDATE exchange_rates SET rate = ?" +
                                                           " WHERE base_currency_id = ? AND target_currency_id = ?" +
                                                           "RETURNING id";

    @Override
    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> allExchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                allExchangeRates.add(buildExchangeRate(resultSet));
            }
            return allExchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException("Error! Failed to get exchange rates from database");
        }
    }

    @Override
    public Optional<ExchangeRate> findByCodes(String baseCurrency, String targetCurrency) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODES_SQL)) {
            preparedStatement.setString(1, baseCurrency);
            preparedStatement.setString(2, targetCurrency);
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseException("Error! Failed to find exchange rate from: " + baseCurrency + " to " + targetCurrency);
        }
    }

    @Override
    public Optional<ExchangeRate> save(ExchangeRate entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_EXCHANGE_RATE_SQL)) {
            preparedStatement.setInt(1, entity.getBaseCurrency().getId());
            preparedStatement.setInt(2, entity.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, entity.getRate());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DatabaseException("Error! Failed to save exchange rate from: %s to %s"
                        .formatted(entity.getBaseCurrency().getCode(), entity.getTargetCurrency().getCode()));
            }
            entity.setId(resultSet.getInt("id"));
            return Optional.of(entity);

        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                return Optional.empty();
            }
            throw new DatabaseException("Error! Failed to save exchange rate from %s to %s"
                    .formatted(entity.getBaseCurrency().getCode(), entity.getTargetCurrency().getCode()));
        }
    }

    @Override
    public Optional<ExchangeRate> update(ExchangeRate entity) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_EXCHANGE_RATE_SQL)) {
            preparedStatement.setBigDecimal(1, entity.getRate());
            preparedStatement.setInt(2, entity.getBaseCurrency().getId());
            preparedStatement.setInt(3, entity.getTargetCurrency().getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                entity.setId(resultSet.getInt("id"));
                return Optional.of(entity);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Error! Failed to update currency with id: " + entity.getId() + " in database.");
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt("id"))
                .rate(resultSet.getBigDecimal("rate"))
                .baseCurrency(
                        Currency.builder()
                                .id(resultSet.getInt("bc_id"))
                                .code(resultSet.getString("bc_code"))
                                .fullName(resultSet.getString("bc_full_name"))
                                .sign(resultSet.getString("bc_sign"))
                                .build()
                )
                .targetCurrency(
                        Currency.builder()
                                .id(resultSet.getInt("tc_id"))
                                .code(resultSet.getString("tc_code"))
                                .fullName(resultSet.getString("tc_full_name"))
                                .sign(resultSet.getString("tc_sign"))
                                .build()
                )
                .build();
    }
}
