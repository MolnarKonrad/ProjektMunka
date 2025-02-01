package konrad.hu;

import java.io.*;
import java.sql.*;
import java.util.*;

public class GameManager {
    private Set<Member> members;
    private Map<String, Perk> perks;
    private Connection connection;

    public GameManager() {
        members = new HashSet<>();
        perks = new HashMap<>();
        connectToDatabase();
//        loadMembersFromFile("members.txt");
//        loadPerksFromFile("perks.ser");
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_db","root","");
            System.out.println("Az adatbázis kapcsolat létrejött!");

        } catch (SQLException e) {
            System.out.println("Hiba az adatbázis kapcsolat létrehozásakor: " + e.getMessage());;
        }
    }

    public boolean isDatabaseEmpty() {
        String query = "SELECT COUNT(*) FROM members";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println("Hiba az adatbázis lekérdezésekor: " + e.getMessage());
        }
        return false;
    }

    public Member findMemberByName(String memberName) {
        String query = "SELECT * FROM members WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, memberName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Member(rs.getString("name"), rs.getBoolean("is_leader"));
            }
        } catch (SQLException e) {
            System.out.println("Hiba a tag keresésekor: " + e.getMessage());
        }
        return null; // Ha nem található
    }

    public void addMemberToDatabase(Member member){

        String query = "INSERT INTO members (name, is_leader) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, member.getName());
            stmt.setBoolean(2, member.isLeader());
            stmt.executeUpdate();
            members.add(member);
            saveMembersToFile();
            System.out.println("Tag hozzáadva: " + member.getName());
        } catch (SQLException e) {
            System.out.println("Hiba a tag hozzáadásakor: " + e.getMessage());
        }
    }

    private void saveMembersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("members.txt"))) {
            oos.writeObject(members);
            System.out.println("Tagok sikeresen mentve fájlba!");
        } catch (IOException e) {
            System.out.println("Hiba a tagok mentésekor: " + e.getMessage());
        }
    }

    public boolean removeMemberFromDatabase(String memberNameToDelete) {

        String query = "DELETE FROM members WHERE name = ?";
        boolean isLeader = false;

        if (isLeader(memberNameToDelete)) {
            isLeader = true;
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, memberNameToDelete);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Hiba a tag eltávolításakor: " + e.getMessage());
        }

        return isLeader;
    }

    private boolean isLeader(String memberName) {
        String query = "SELECT is_leader FROM members WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, memberName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("is_leader");
            }
        } catch (SQLException e) {
            System.out.println("Hiba a tag státuszának ellenőrzésekor: " + e.getMessage());
        }
        return false;
    }

    public boolean hasActiveLeader() {
        String query = "SELECT COUNT(*) FROM members WHERE is_leader = true";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Hiba a vezető keresésekor: " + e.getMessage());
        }
        return false;
    }
}
