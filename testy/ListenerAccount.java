package music;



import javax.naming.AuthenticationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListenerAccount extends Account {
    public ListenerAccount(int id, String name) {
        super(id, name);
    }

    public Playlist createPlaylist(List<Integer> songIds) throws SQLException, NotEnoughCreditsException {
        Playlist playlist = new Playlist();
        for (var songId : songIds) { //var sam ustala typ zmiennej na podstawie przypisanej wartosci
            if (!Persistence.hasSong(this.id, songId)) {
                buySong(songId);
            }

            var optionalSong = Song.Persistence.read(songId);
            if (optionalSong.isPresent()) {
                playlist.add(optionalSong.get());
            } else {
                throw new SQLException("Song not found with id: " + songId);
            }
        }
        return playlist;
    }

    public void buySong(int songId) throws SQLException, NotEnoughCreditsException {
        if (Persistence.hasSong(this.id, songId)) {
            return;
        }

        int credits = Persistence.getCredits(this.id);
        if (credits < 1) {
            throw new NotEnoughCreditsException();
        }

        Persistence.addSong(this.id, songId);
        Persistence.addCredits(this.id, -1);
    }

    public static class Persistence {
        public static void init() throws SQLException {
            Account.Persistence.init();

            String sql1 = "CREATE TABLE IF NOT EXISTS listener_account( " +
                    "id_account INTEGER NOT NULL PRIMARY KEY," +
                    "credits INTEGER NOT NULL)";
            PreparedStatement statement1 = DatabaseConnection.getConnection().prepareStatement(sql1);
            statement1.executeUpdate();

            String sql2 = "CREATE TABLE IF NOT EXISTS owned_songs( " +
                    "id_account INTEGER NOT NULL," +
                    "id_song INTEGER NOT NULL," +
                    "PRIMARY KEY (id_account, id_song))";
            PreparedStatement statement2 = DatabaseConnection.getConnection().prepareStatement(sql2);
            statement2.executeUpdate();
        }

        public static int register(String username, String password) throws SQLException {
            try {
                int id = Account.Persistence.register(username, password);
                String sql = "INSERT INTO listener_account(id_account, credits) VALUES (?, 0)";
                PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
                statement.setInt(1, id);
                statement.executeUpdate();
                return id;
            } catch (SQLException | RuntimeException e) {
                throw new RuntimeException(e);
            }
        }

        public static int getCredits(int id) throws SQLException {
            String sql = "SELECT credits FROM listener_account WHERE id_account = ?";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("credits");
            } else {
                throw new SQLException("Credits not found for account: " + id);
            }
        }

        public static void addCredits(int id, int amount) throws SQLException {
            int currentCredits = getCredits(id);
            String sql = "UPDATE listener_account SET credits = ? WHERE id_account = ?";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, currentCredits + amount);
            statement.setInt(2, id);
            statement.executeUpdate();
        }

        public static void addSong(int accountId, int songId) throws SQLException {
            String sql = "INSERT INTO owned_songs VALUES(?, ?)";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, accountId);
            statement.setInt(2, songId);
            statement.executeUpdate();
        }

        public static boolean hasSong(int accountId, int songId) throws SQLException {
            String sql = "SELECT * FROM owned_songs WHERE id_account = ? AND id_song = ?";
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setInt(1, accountId);
            statement.setInt(2, songId);
            return statement.executeQuery().next();
        }

        public static ListenerAccount authenticate(String username, String password) throws AuthenticationException {
            Account account = Account.Persistence.authenticate(username, password);
            return new ListenerAccount(account.getId(), account.getUsername());
        }
    }
}
