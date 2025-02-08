package konrad.hu;

import java.sql.*;
import java.util.*;
import static java.sql.DriverManager.getConnection;

/**
 * A GameManager osztály kezeli a játék logikáját, beleértve az adatbázis kapcsolódást,
 * a tagok és perkek kezelését, valamint a felhasználói interakciókat.
 */

@SuppressWarnings("ALL")
public class GameManager {
    static Scanner scanner = new Scanner(System.in);
    static String userName;
    private Connection connection;
    static Database database;
    private Set<Member> members;
    private Map<String, Perk> perks;

    /**
     * Konstruktor, amely inicializálja a GameManager példányt,
     * létrehozza a kapcsolatot az adatbázissal, és inicializálja
     * a tagok és perkek halmazát.
     *
     * @throws SQLException ha hiba lép fel az adatbázis kapcsolat létrehozásakor
     */
    public GameManager() throws SQLException {
        members = new HashSet<>();
        perks = new HashMap<>();
        connectToDatabase();
        database = new DatabaseManager(connection, members);
    }

    /**
     * Kapcsolódik az adatbázishoz.
     *
     * Ha a kapcsolat sikeres, kiírja az adatbázis kapcsolat létrejöttét.
     * Ha hiba lép fel, kiírja a hibaüzenetet.
     */
    public void connectToDatabase() {
        try {
            connection = getConnection("jdbc:mysql://localhost:3306/game_db","root","");
            System.out.println("Az adatbázis kapcsolat létrejött!");

        } catch (SQLException e) {
            System.out.println("Hiba az adatbázis kapcsolat létrehozásakor: " + e.getMessage());;
        }
    }

    /**
     * Ellenőrzi, hogy a megadott tagot hozzá lehet-e adni az adatbázishoz.
     *
     * @param member a hozzáadandó Member objektum
     * @return true, ha a tag hozzáadható; false, ha nem
     * @throws IllegalArgumentException ha a név megadása kötelező, vagy ha a tag már létezik
     */
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

    /**
     * Ellenőrzi, hogy a megadott tag létezik-e az adatbázisban.
     *
     * @param name a tag neve
     * @return true, ha a tag létezik; false, ha nem
     */
    private boolean isMemberExists(String name) {
        return false;
    }

    /**
     * Ellenőrzi az adatbázis állapotát, és kezeli a felhasználói interakciókat.
     *
     * Ha az adatbázis üres, új vezetőt kér be a felhasználótól.
     * Ha van aktív vezető, a felhasználótól kéri a nevét, és
     * a megfelelő menüt jeleníti meg.
     *
     * @return true, ha a felhasználó kilépett; false, ha nem
     */
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

    /**
     * Megjeleníti a vezetői menüt, ahol a vezető különböző műveleteket végezhet.
     *
     * @param database az adatbázis, amelyet a menü használ
     * @param scanner a felhasználói bemenet kezelésére
     */
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

    /**
     * Megjeleníti a tagi menüt, ahol a tag különböző műveleteket végezhet.
     *
     * @param database az adatbázis, amelyet a menü használ
     * @param scanner a felhasználói bemenet kezelésére
     */
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