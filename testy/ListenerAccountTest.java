package music;

import org.junit.jupiter.api.*;

import javax.naming.AuthenticationException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListenerAccountTest {
    static final String DB_PATH = "src/main/resources/songs.db";
    static DatabaseConnection connection;

    @BeforeAll
    static void setup() throws SQLException {
        connection = new DatabaseConnection();
        connection.connect(DB_PATH);
        Account.Persistence.init();
        ListenerAccount.Persistence.init();
        Song.Persistence.setConnection(connection);
    }

    @AfterAll
    static void teardown() throws SQLException {
        connection.disconnect();
    }

    // 3a: Test rejestracji konta
    @Test
    void testRegisterAccount() throws SQLException {
        String username = "testuser";
        String password = "password";
        int accountId = ListenerAccount.Persistence.register(username, password);

        int credits = ListenerAccount.Persistence.getCredits(accountId);
        assertEquals(0, credits, "Nowe konto powinno mieć 0 kredytów");
    }

    // 3b: Test logowania do konta
    @Test
    void testAuthenticateAccount() throws SQLException, AuthenticationException {
        String username = "loginuser";
        String password = "securepass";
        ListenerAccount.Persistence.register(username, password);

        ListenerAccount account = ListenerAccount.Persistence.authenticate(username, password);
        assertNotNull(account);
        assertEquals(username, account.getUsername());
    }

    // 3c: Test początkowego stanu i dodawania kredytów
    @Test
    void testInitialAndAddCredits() throws SQLException {
        String username = "credituser";
        String password = "123";
        int id = ListenerAccount.Persistence.register(username, password);

        assertEquals(0, ListenerAccount.Persistence.getCredits(id), "Na początku konto powinno mieć 0 kredytów");

        ListenerAccount.Persistence.addCredits(id, 5);
        assertEquals(5, ListenerAccount.Persistence.getCredits(id));
    }

    // 3d: Wyjątek i metoda buySong
    @Test
    void testBuySongSuccessfully() throws Exception {
        String username = "buyer1";
        String password = "pass";
        int id = ListenerAccount.Persistence.register(username, password);
        ListenerAccount account = ListenerAccount.Persistence.authenticate(username, password);

        ListenerAccount.Persistence.addCredits(id, 1);
        int songId = 1;

        assertFalse(ListenerAccount.Persistence.hasSong(id, songId));

        account.buySong(songId);

        assertTrue(ListenerAccount.Persistence.hasSong(id, songId));
        assertEquals(0, ListenerAccount.Persistence.getCredits(id));
    }

    @Test
    void testBuySongAlreadyOwned() throws Exception {
        String username = "buyer2";
        String password = "pass";
        int id = ListenerAccount.Persistence.register(username, password);
        ListenerAccount.Persistence.addCredits(id, 1);
        ListenerAccount.Persistence.addSong(id, 2);

        ListenerAccount account = ListenerAccount.Persistence.authenticate(username, password);

        account.buySong(2); // Powinno nic nie zmienić
        assertTrue(ListenerAccount.Persistence.hasSong(id, 2));
        assertEquals(1, ListenerAccount.Persistence.getCredits(id)); // kredyt nie został odjęty
    }

    @Test
    void testBuySongNoCredits() throws Exception {
        String username = "buyer3";
        String password = "pass";
        int id = ListenerAccount.Persistence.register(username, password);
        ListenerAccount account = ListenerAccount.Persistence.authenticate(username, password);

        assertThrows(NotEnoughCreditsException.class, () -> account.buySong(3)); //sprawdza czy metoda rzuci wyjatek bo powinna 
    }

    // 3e: Test tworzenia playlisty
    @Test
    void testCreatePlaylist() throws Exception {
        String username = "playlistuser";
        String password = "pass";
        int id = ListenerAccount.Persistence.register(username, password); //rejestracja nowego uzytkownika w bazie bo nie ma kredytow
        ListenerAccount account = ListenerAccount.Persistence.authenticate(username, password);

        // Dodaj kredyty na 2 piosenki
        ListenerAccount.Persistence.addCredits(id, 2);

        List<Integer> songIds = List.of(1, 2);
        Playlist playlist = account.createPlaylist(songIds);

        assertEquals(2, playlist.size());
        assertEquals("Hey Jude", playlist.get(0).title());
        assertEquals("(I Can't Get No) Satisfaction", playlist.get(1).title());
    }
}

