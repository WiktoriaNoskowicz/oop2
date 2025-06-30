package auth;

import database.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class AccountManager {
    private final Connection connection;


    public AccountManager(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    private void initializeTable() {
        try (PreparedStatement stmt = this.connection.prepareStatement("""
                        CREATE TABLE IF NOT EXISTS accounts (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password TEXT NOT NULL
                        );
                """)) {
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("accounts table creation error: " + e.getMessage());
        }
    }

    public boolean authenticate(String username, String password) {
        try (PreparedStatement stmt = this.connection.prepareStatement(
                "SELECT password FROM accounts WHERE username = ?;")) {
            stmt.setString(1, username); //podstawia username w miejscu ?
            ResultSet rs = stmt.executeQuery(); // wykonuje zapytanie i zwraca wynik w postaci obiektu ResultSet
            if (rs.next()) { //jesli uzytkownik istnieje to ma chociaz jeden wiersz
                String hashed = rs.getString("password"); //pobiera wrtosc kolumny password czyli haslo
                return BCrypt.checkpw(password, hashed); //bierze haslo uzytkownika i je zahaszowuje i jesli sie zgadza to zwraca true -> sukces logowania
            }
        } catch (SQLException e) {
            System.err.println("Auth error: " + e.getMessage());
        }
        return false;
    }

    public Account getAccount(String usernameOrId) {
        try {
            PreparedStatement stmt; //przechowuje obiekt zapytania sql potem
            boolean isNumeric = usernameOrId.matches("\\d+"); //sprawdz czy id to liczba (czy uzytkownik podal id)

            if (isNumeric) {
                stmt = this.connection.prepareStatement("SELECT id, username FROM accounts WHERE id = ?;");
                stmt.setInt(1, Integer.parseInt(usernameOrId)); //Zamieniamy tekst na liczbę całkowitą i wstawiamy jako parametr ?.
            } else {//jesli iztykownik podal login (string)
                stmt = this.connection.prepareStatement("SELECT id, username FROM accounts WHERE username = ?;");
                stmt.setString(1, usernameOrId); //Ustawiamy wartość parametru ? jako string z usernameOrId
            }

            ResultSet rs = stmt.executeQuery(); //Wykonujemy przygotowane zapytanie. Jeśli znajdzie pasujące konto, wynik trafi do rs
            if (rs.next()) { //sprawdzamy czy rs ma jakis wiersz -> sa dane
                return new Account(rs.getInt("id"), rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Accont retrival error: " + e.getMessage());
        }
        return null;
    }
    public boolean register(String username, String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt()); //tworzenie hasla, 2 arg to ziarno generatora
        try (PreparedStatement stmt = this.connection.prepareStatement(
                "INSERT INTO accounts (username, password) VALUES (?, ?);")) { //to tzw. placeholdery – wypełnione potem przez stmt.setString(...).
            stmt.setString(1, username);
            stmt.setString(2, hashed);
            stmt.executeUpdate(); //Wykonuje zapytanie INSERT — dodaje nowy wiersz do tabeli accounts.
            return true;
        } catch (SQLException e) {
            System.err.println("Register error: " + e.getMessage());
            return false;
        }
    }


}