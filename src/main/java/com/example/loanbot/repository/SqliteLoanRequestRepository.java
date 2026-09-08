package com.example.loanbot.repository;

import com.example.loanbot.model.LoanRequest;
import com.example.loanbot.model.PaymentType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация хранилища заявок поверх SQLite (через JDBC).
 * В отличие от InMemoryLoanRequestRepository, данные переживают перезапуск процесса —
 * файл базы данных лежит на диске.
 * Подключение открывается один раз на всё время работы бота; методы синхронизированы,
 * так как SQLite не рассчитан на параллельную запись из нескольких потоков.
 */
public class SqliteLoanRequestRepository implements LoanRequestRepository {

    private final Connection connection;

    public SqliteLoanRequestRepository(String databaseFilePath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
            createSchema();
        } catch (SQLException exception) {
            throw new IllegalStateException("Не удалось подключиться к SQLite: " + databaseFilePath, exception);
        }
    }

    private void createSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS loan_requests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        amount TEXT NOT NULL,
                        months INTEGER NOT NULL,
                        annual_rate TEXT NOT NULL,
                        payment_type TEXT NOT NULL
                    )
                    """);
        }
    }

    @Override
    public synchronized void save(LoanRequest request) {
        String sql = "INSERT INTO loan_requests (user_id, amount, months, annual_rate, payment_type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, request.getUserId());
            statement.setString(2, request.getAmount().toPlainString());
            statement.setInt(3, request.getMonths());
            statement.setString(4, request.getAnnualRate().toPlainString());
            statement.setString(5, request.getPaymentType().name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Не удалось сохранить заявку в SQLite", exception);
        }
    }

    @Override
    public synchronized List<LoanRequest> findByUserId(long userId) {
        String sql = "SELECT user_id, amount, months, annual_rate, payment_type "
                + "FROM loan_requests WHERE user_id = ? ORDER BY id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Не удалось прочитать историю из SQLite", exception);
        }
    }

    @Override
    public synchronized List<LoanRequest> findAll() {
        String sql = "SELECT user_id, amount, months, annual_rate, payment_type FROM loan_requests ORDER BY id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return mapResultSet(resultSet);
        } catch (SQLException exception) {
            throw new IllegalStateException("Не удалось прочитать заявки из SQLite", exception);
        }
    }

    private List<LoanRequest> mapResultSet(ResultSet resultSet) throws SQLException {
        List<LoanRequest> requests = new ArrayList<>();

        while (resultSet.next()) {
            requests.add(new LoanRequest(
                    resultSet.getLong("user_id"),
                    new BigDecimal(resultSet.getString("amount")),
                    resultSet.getInt("months"),
                    new BigDecimal(resultSet.getString("annual_rate")),
                    PaymentType.valueOf(resultSet.getString("payment_type"))
            ));
        }

        return requests;
    }
}
