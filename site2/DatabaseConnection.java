package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private java.sql.Connection connection;

    public Connection getConnection() {
        return this.connection;
    }
    public void connect(String databasePath) throws SQLException{
                 //jdbc:zCzymLaczymy://localhost:/nazwaBazyDanych(albo url)
            String url = "jdbc:sqlite:" + databasePath;
            this.connection = DriverManager.getConnection(url); //tworzymy polaczenie z baza
            System.out.println("Database connection established.");


    }
    public void disconnect() throws SQLException{
        try{
            if(this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("Database connection is closed.");}
        }catch(SQLException e){
            System.out.println("An error occurred while disconnecting from the DB " + e.getMessage());
        }
    }
}
