package konrad.hu;

import java.sql.*;
import java.util.*;
import static java.sql.DriverManager.getConnection;

@SuppressWarnings("ALL")
public class GameManager {
    static Scanner scanner = new Scanner(System.in);
    static String userName;
    private Connection connection;
    static Database database;
    private Set<Member> members;
    private Map<String, Perk> perks;

    public GameManager() throws SQLException {
        members = new HashSet<>();
        perks = new HashMap<>();
        connectToDatabase();
        database = new DatabaseManager(connection, members);
    }

    public void connectToDatabase() {
        try {
            connection = getConnection("jdbc:mysql://localhost:3306/game_db","root","");
            System.out.println("Az adatbázis kapcsolat létrejött!");

        } catch (SQLException e) {
            System.out.println("Hiba az adatbázis kapcsolat létrehozásakor: " + e.getMessage());;
        }
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

public static boolean databaseCheck() {
    if (database.isDatabaseEmpty()) {
        System.out.print("Az adatbázis üres. Kérlek add meg a tag nevét, aki vezető lesz: ");
        String newMemberName = scanner.nextLine();
        database.addMemberToDatabase(new Member(newMemberName, true));
        System.out.println("A tag hozzáadva: " + newMemberName + " (Leader)");
        showLeaderMenu(database, scanner);
    } else {
        Member member;
        if (!database.hasActiveLeader()) {
            System.out.println("Jelenleg nincs aktív vezető az adatbázisban.");
            System.out.print("Kérlek add meg a tag nevét, aki vezető lesz: ");
            String newMemberName = scanner.nextLine();
            database.addMemberToDatabase(new Member(newMemberName, true));
            System.out.println("A tag hozzáadva: " + newMemberName + " (Leader)");
            showLeaderMenu(database, scanner);
        } else {
            System.out.print("Kérlek add meg a neved: ");
            userName = scanner.nextLine();
            member = database.findMemberByName(userName);

            if (member == null) {
                System.out.println("Hiba: A megadott felhasználó nem található az adatbázisban.");
                scanner.close();
                return true;
            }

            if (member.isLeader()) {
                showLeaderMenu(database, scanner);
            } else {
                showMemberMenu(database, scanner);
            }
        }
    }
    return false;
    }

    public static void showLeaderMenu(Database database, Scanner scanner){

        while (true) {

            System.out.println("\n --- Menük ---");
            System.out.println("1. Tag hozzáadása");
            System.out.println("2. Tag eltávolítása");
            System.out.println("3. Perk hozzáadása taghoz");
            System.out.println("4. Tag perk-jének cseréje");
            System.out.println("5. Információ az aktuális tagokról és azok perkjeiről");
            System.out.println("6. Kilépés");

            System.out.println("Válassz egy lehetőséget: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Tag neve: ");
                    String name = scanner.nextLine();
                    database.addMemberToDatabase(new Member(name, false));
                    break;
                case 2:
                    System.out.print("Add meg a tag nevét, akit el szeretnél távolítani: ");
                    String memberToRemove = scanner.nextLine();
                    boolean wasLeaderRemoved = database.isLeader(memberToRemove);
                    database.removeMemberFromDatabase(memberToRemove);

                    if (wasLeaderRemoved) {
                        System.out.println("A kiválasztott tag, aki vezető volt, eltávolítva.");
                        System.out.println("Nincs aktuális vezető, add meg a vezető nevét!");
                        name = scanner.nextLine();
                        Member member = database.findMemberByName(name);

                        if (member == null) {
                            database.addMemberToDatabase(new Member(name, true));
                            showLeaderMenu(database, scanner);
                        } else {
                            database.promoteMemberToLeader(name);
                            showLeaderMenu(database, scanner);
                        }
                        return;
                    } else {
                        System.out.println("Tag eltávolítva: " + memberToRemove);
                    }
                    break;
                case 3:
                    System.out.print("Tag neve, akihez perk-et szeretnél hozzáadni: ");
                    String targetMemberName = scanner.nextLine();
                    System.out.print("Megadott perk neve: ");
                    String perkName = scanner.nextLine();
                    database.addPerkToMember(targetMemberName, perkName);
                    break;
                case 4:
                    System.out.print("Tag neve, akinek perkjét cserélni szeretnéd: ");
                    String memberNameForReplace = scanner.nextLine();
                    System.out.print("Régi perk neve: ");
                    String oldPerkName = scanner.nextLine();
                    System.out.print("Új perk neve: ");
                    String newPerkName = scanner.nextLine();
                    database.replaceMemberPerk(memberNameForReplace, oldPerkName, newPerkName);
                    break;
                case 5:
                    database.listMembersAndPerks();
                    break;
                case 6:
                    System.out.println("Kilépés...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Érvénytelen választás, próbáld újra.");
                    break;
            }
        }
    }

    private static void showMemberMenu(Database database, Scanner scanner){

        while (true) {

            System.out.println("\n --- Menük ---");
            System.out.println("1. Perk hozzáadása");
            System.out.println("2. Perk cseréje");
            System.out.println("3. Információ az aktuális tagokról és azok perkjeiről");
            System.out.println("4. Kilépés");

            System.out.println("Válassz egy lehetőséget: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("A perk neve: ");
                    String perkName = scanner.nextLine();
                    database.addPerkToMember(userName, perkName);
                    break;
                case 2:
                    System.out.print("Régi perk neve: ");
                    String oldPerkName = scanner.nextLine();
                    System.out.print("Új perk neve: ");
                    String newPerkName = scanner.nextLine();
                    database.replaceMemberPerk(userName, oldPerkName, newPerkName);
                    break;
                case 3:
                    database.listMembersAndPerks();
                    break;
                case 4:
                    System.out.println("Kilépés...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Érvénytelen választás, próbáld újra.");
                    break;
            }
        }
    }
}