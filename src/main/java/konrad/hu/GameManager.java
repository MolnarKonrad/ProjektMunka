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

    public boolean addTestMemberToDatabase(Member member) {
        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("A név megadása kötelező.");
        }
        if (isMemberExists(member.getName())) {
            throw new IllegalArgumentException("A tag már létezik az adatbázisban.");
        }
        if (member.getName().matches(".*\\d.*")) {
            throw new IllegalArgumentException("A név nem lehet számokból álló karakterlánc.");
        }

        return true;
    }

    private boolean isMemberExists(String name) {
        return false;
    }

    public void addPerkToMember(String memberName, String perkName) {
        try {
            String memberQuery = "SELECT id, is_leader FROM members WHERE name = ?";
            PreparedStatement memberStmt = connection.prepareStatement(memberQuery);
            memberStmt.setString(1,memberName);
            ResultSet memberResult = memberStmt.executeQuery();

            if (memberResult.next()) {
                int memberId = memberResult.getInt("id");
                boolean isLeader = memberResult.getBoolean("is_leader");

                if (!isLeader || !memberName.equals(memberName)) {
                    System.out.println("Csak az adott tag vagy a vezető adhat hozzá perket!");
                    return;
                }

                String perkQuery = "SELECT id FROM perks WHERE name = ?";
                PreparedStatement perkStmt = connection.prepareStatement(perkQuery);
                perkStmt.setString(1,perkName);
                ResultSet perkResult = perkStmt.executeQuery();

                if (perkResult.next()) {
                    int perkId = perkResult.getInt("id");

                    String countQuery = "SELECT COUNT(*) AS count FROM members_perks WHERE member_id = ?";
                    PreparedStatement countStmt = connection.prepareStatement(countQuery);
                    countStmt.setInt(1,memberId);
                    ResultSet countResult = countStmt.executeQuery();
                    countResult.next();
                    int perkCount = countResult.getInt("count");

                    if (perkCount < 10) {
                        String checkPerkQuery = "SELECT * FROM members_perks WHERE member_id = ? AND perk_id = ?";
                        PreparedStatement checkPerkStmt = connection.prepareStatement(checkPerkQuery);
                        checkPerkStmt.setInt(1, memberId);
                        checkPerkStmt.setInt(2, perkId);
                        ResultSet checkPerkResult = checkPerkStmt.executeQuery();

                        if (!checkPerkResult.next()) {
                            String insertQuery = "INSERT INTO members_perks (member_id, perk_id) VALUES (?, ?)";
                            PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                            insertStmt.setInt(1, memberId);
                            insertStmt.setInt(2, perkId);
                            insertStmt.executeUpdate();
                            System.out.println("Perk hozzáadva a taghoz!");
                            saveMembersToFile();
                        } else {
                            System.out.println("A tag már rendelkezik ezzel a perk-kel!");
                        }
                    } else {
                        System.out.println("A tagnak már 10 perk-je van!");
                    }
                } else {
                    System.out.println("A megadott perk nem található az adatbázisban!");
                }
            } else {
                System.out.println("A megadott tag nem található az adatbázisban!");
            }
        } catch (SQLException e) {
            System.out.println("Hiba a perk hozzáadásakor: " + e.getMessage());
        }
    }

    public void listMemberPerks(String memberName) {
        try {
            String memberQuery = "SELECT id FROM members WHERE name = ?";
            PreparedStatement memberStmt = connection.prepareStatement(memberQuery);
            memberStmt.setString(1,memberName);
            ResultSet memberResult = memberStmt.executeQuery();

            if (memberResult.next()) {
                int memberId = memberResult.getInt("id");

                String perksQuery = "SELECT p.name, p.description FROM perks p JOIN members_perks mp ON p.id = mp.perk_id WHERE mp.member_id = ? ORDER BY p.name ASC";
                PreparedStatement perksStmt = connection.prepareStatement(perksQuery);
                perksStmt.setInt(1, memberId);
                ResultSet perksResult = perksStmt.executeQuery();

                System.out.println(memberName + " perk-jei: ");
                while (perksResult.next()) {
                    String perkName = perksResult.getString("name");
                    String perkDescription = perksResult.getString("description");
                    System.out.println("- " + perkName + ": " + perkDescription);
                }
            } else {
                System.out.println("A megadott tag nem található az adatbázisban!");
            }
        } catch (SQLException e) {
            System.out.println("Hiba a perk-ek listázása során: " + e.getMessage());
        }
    }

    public void replaceMemberPerk(String memberName, String oldPerkName, String newPerkName) {
        try {
            String memberQuery = "SELECT id FROM members WHERE name = ?";
            PreparedStatement memberStmt = connection.prepareStatement(memberQuery);
            memberStmt.setString(1, memberName);
            ResultSet memberResult = memberStmt.executeQuery();

            if (memberResult.next()) {
                int memberId = memberResult.getInt("id");

                String oldPerkQuery = "SELECT id FROM perks WHERE name = ?";
                PreparedStatement oldPerkStmt = connection.prepareStatement(oldPerkQuery);
                oldPerkStmt.setString(1, oldPerkName);
                ResultSet oldPerkResult = oldPerkStmt.executeQuery();

                if (oldPerkResult.next()) {
                    int oldPerkId = oldPerkResult.getInt("id");

                    String newPerkQuery = "SELECT id FROM perks WHERE name = ?";
                    PreparedStatement newPerkStmt = connection.prepareStatement(newPerkQuery);
                    newPerkStmt.setString(1, newPerkName);
                    ResultSet newPerkResult = newPerkStmt.executeQuery();

                    if (newPerkResult.next()) {
                        int newPerkId = newPerkResult.getInt("id");

                        String deleteQuery = "DELETE FROM members_perks WHERE member_id = ? AND perk_id = ?";
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                            deleteStmt.setInt(1, memberId);
                            deleteStmt.setInt(2, oldPerkId);
                            deleteStmt.executeUpdate();
                        }

                        String insertQuery = "INSERT INTO members_perks (member_id, perk_id) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setInt(1, memberId);
                            insertStmt.setInt(2, newPerkId);
                            insertStmt.executeUpdate();
                        }

                        System.out.println("Perk lecserélve: " + oldPerkName + " -> " + newPerkName);
                        saveMembersToFile();
                    } else {
                        System.out.println("Az új perk nem található az adatbázisban.");
                    }
                } else {
                    System.out.println("A régi perk nem található a tagnál.");
                }
            } else {
                System.out.println("A megadott tag nem található az adatbázisban.");
            }
        } catch (SQLException e) {
            System.out.println("Hiba a perk lecserélésekor: " + e.getMessage());
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
}
