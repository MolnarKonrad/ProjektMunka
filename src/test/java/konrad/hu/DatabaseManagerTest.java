package konrad.hu;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {
    private Connection connection;
    private DatabaseManager databaseManager;
    private Set<Member> members;

    @BeforeEach
    public void setUp() throws SQLException {

        connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        members = new HashSet<>();
        databaseManager = new DatabaseManager(connection, members);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE members (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), is_leader BOOLEAN)");
            stmt.execute("CREATE TABLE perks (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), description TEXT)");
            stmt.execute("CREATE TABLE members_perks (member_id INT, perk_id INT, FOREIGN KEY (member_id) REFERENCES members(id), FOREIGN KEY (perk_id) REFERENCES perks(id))");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE members_perks");
            stmt.execute("DROP TABLE perks");
            stmt.execute("DROP TABLE members");
        }
        connection.close();
    }

    @Test
    public void testIsDatabaseEmpty() throws SQLException {
        assertTrue(databaseManager.isDatabaseEmpty());


        databaseManager.addMemberToDatabase(new Member("John Doe", false));
        assertFalse(databaseManager.isDatabaseEmpty());
    }

    @Test
    public void testFindMemberByName() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Jane Doe", false));
        Member foundMember = databaseManager.findMemberByName("Jane Doe");
        assertNotNull(foundMember);
        assertEquals("Jane Doe", foundMember.getName());
    }

    @Test
    public void testAddMemberToDatabase() throws SQLException {
        Member member = new Member("Alice Smith", false);
        databaseManager.addMemberToDatabase(member);
        assertTrue(members.contains(member));
    }

    @Test
    public void testRemoveMemberFromDatabase() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Bob Brown", false));
        assertTrue(databaseManager.removeMemberFromDatabase("Bob Brown"));
        assertNull(databaseManager.findMemberByName("Bob Brown"));
    }

    @Test
    public void testIsLeader() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Leader Member", true));
        assertTrue(databaseManager.isLeader("Leader Member"));
        assertFalse(databaseManager.isLeader("Nonexistent Member"));
    }

    @Test
    public void testHasActiveLeader() throws SQLException {
        assertFalse(databaseManager.hasActiveLeader());
        databaseManager.addMemberToDatabase(new Member("Another Leader", true));
        assertTrue(databaseManager.hasActiveLeader());
    }

    @Test
    public void testAddPerkToMember() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Charlie", false));
        databaseManager.addPerkToMember("Charlie", "Super Strength");
        String query = "SELECT COUNT(*) FROM members_perks";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                assertEquals(1, rs.getInt(1));
            }
        }
    }

    @Test
    public void testReplaceMemberPerk() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Daisy", false));
        databaseManager.addPerkToMember("Daisy", "Speed");
        databaseManager.replaceMemberPerk("Daisy", "Speed", "Flying");
        String query = "SELECT COUNT(*) FROM members_perks WHERE member_id = (SELECT id FROM members WHERE name = 'Daisy')";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                assertEquals(1, rs.getInt(1));
            }
        }
    }

    @Test
    public void testSaveMembersAndPerksToFile() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Eve", false));
        databaseManager.addPerkToMember("Eve", "Invisibility");
        assertDoesNotThrow(() -> databaseManager.saveMembersAndPerksToFile());
    }

    @Test
    public void testPromoteMemberToLeader() throws SQLException {
        databaseManager.addMemberToDatabase(new Member("Frank", false));
        assertTrue(databaseManager.promoteMemberToLeader("Frank"));
        assertTrue(databaseManager.isLeader("Frank"));
    }
}
