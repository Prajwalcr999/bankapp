import java.sql.*;
import java.util.*;

public class BankingServiceImpl implements BankingService {
    private Connection conn;

    public BankingServiceImpl() {
        try {
            this.conn = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customer (customerID, name, address, contact) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customer.getCustomerID());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getContact());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addAccount(Account account) {
        String sql = "INSERT INTO account (accountID, customerID, type, balance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getAccountID());
            ps.setInt(2, account.getCustomerID());
            ps.setString(3, account.getType());
            ps.setDouble(4, account.getBalance());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addTransaction(Transaction transaction) {
        String insertSql = "INSERT INTO transaction (transactionId,accountid, type, amount, timestamp) VALUES (?,?, ?, ?, ?)";
        String updateSql = "UPDATE account SET balance = balance + ? WHERE accountid = ?";

        try {
            conn.setAutoCommit(false); // Start transaction

            double delta = 0;
            if ("deposit".equalsIgnoreCase(transaction.getType())) {
                delta = transaction.getAmount();
            } else if ("withdrawal".equalsIgnoreCase(transaction.getType())) {
                delta = -transaction.getAmount();
            } else {
                System.out.println("Invalid transaction type.");
                return;
            }

            // Insert into transactions table
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1,transaction.getTransactionID());
                insertStmt.setInt(2, transaction.getAccountID());
                insertStmt.setString(3, transaction.getType());
                insertStmt.setDouble(4, transaction.getAmount());
                insertStmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
                insertStmt.executeUpdate();
            }

            // Update account balance directly
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, delta);
                updateStmt.setInt(2, transaction.getAccountID());
                updateStmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Transaction processed and balance updated.");

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
            e.printStackTrace();
        }
    }



    @Override
    public void addBeneficiary(Beneficiary b) {
        String sql = "INSERT INTO beneficiary (beneficiaryID, customerID, name, accountNumber, bankDetails) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, b.getBeneficiaryID());
            ps.setInt(2, b.getCustomerID());
            ps.setString(3, b.getName());
            ps.setString(4, b.getAccountNumber());
            ps.setString(5, b.getBankDetails());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer findCustomerById(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM customer WHERE customerID = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(id, rs.getString("name"), rs.getString("address"), rs.getString("contact"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Account findAccountById(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM account WHERE accountID = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(id, rs.getInt("customerID"), rs.getString("type"), rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Transaction findTransactionById(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM transaction WHERE transactionID = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Transaction t=  new Transaction( rs.getInt("accountID"), rs.getString("type"), rs.getDouble("amount"));
            t.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
            t.setTransactionID(id);
            return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Beneficiary findBeneficiaryById(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM beneficiary WHERE beneficiaryID = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Beneficiary(id, rs.getInt("customerID"), rs.getString("name"), rs.getString("accountNumber"), rs.getString("bankDetails"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM account WHERE customerID = ?")) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Account(rs.getInt("accountID"), customerId, rs.getString("type"), rs.getDouble("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM transaction WHERE accountID = ?")) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t=  new Transaction( rs.getInt("accountID"), rs.getString("type"), rs.getDouble("amount"));
                t.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                t.setTransactionID(rs.getInt("transactionID"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Beneficiary> getBeneficiariesByCustomerId(int customerId) {
        List<Beneficiary> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM beneficiary WHERE customerID = ?")) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Beneficiary(rs.getInt("beneficiaryID"), customerId, rs.getString("name"), rs.getString("accountNumber"), rs.getString("bankDetails")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Collection<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM account");
            while (rs.next()) {
                list.add(new Account(rs.getInt("accountID"), rs.getInt("customerID"), rs.getString("type"), rs.getDouble("balance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Collection<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM customer");
            while (rs.next()) {
                list.add(new Customer(rs.getInt("customerID"), rs.getString("name"), rs.getString("address"), rs.getString("contact")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Collection<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM transaction");
            while (rs.next()) {
                Transaction t=  new Transaction( rs.getInt("accountID"), rs.getString("type"), rs.getDouble("amount"));
                t.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                t.setTransactionID(rs.getInt("transactionID"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Collection<Beneficiary> getAllBeneficiaries() {
        List<Beneficiary> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM beneficiary");
            while (rs.next()) {
                list.add(new Beneficiary(rs.getInt("beneficiaryID"), rs.getInt("customerID"), rs.getString("name"), rs.getString("accountNumber"), rs.getString("bankDetails")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
