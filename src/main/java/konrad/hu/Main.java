package konrad.hu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        GameManager gameManager = new GameManager();

        if (gameManager.isDatabaseEmpty()) {
            System.out.print("Az adatbázis üres. Kérlek add meg a tag nevét, aki vezető lesz: ");
            String newMemberName = scanner.nextLine();
            gameManager.addMemberToDatabase(new Member(newMemberName, true));
            System.out.println("A tag hozzáadva: " + newMemberName + " (Leader)");
            showLeaderMenu(gameManager, scanner);
        } else {
            if (!gameManager.hasActiveLeader()) {
                System.out.println("Jelenleg nincs aktív vezető az adatbázisban.");
                System.out.print("Kérlek add meg a tag nevét, aki vezető lesz: ");
                String newMemberName = scanner.nextLine();
                gameManager.addMemberToDatabase(new Member(newMemberName, true));
                System.out.println("A tag hozzáadva: " + newMemberName + " (Leader)");
                showLeaderMenu(gameManager, scanner);
            } else {
                System.out.print("Kérlek add meg a neved: ");
                String userName = scanner.nextLine();

                Member member = gameManager.findMemberByName(userName);
                if (member == null) {
                    System.out.println("Hiba: A megadott felhasználó nem található az adatbázisban.");
                    scanner.close();
                    return;
                }

                if (member.isLeader()) {
                    showLeaderMenu(gameManager, scanner);
                } else {
                    System.out.println("Hiba: Nem vagy vezető.");
                    scanner.close();
                    return;
                }
            }
        }

        scanner.close();
    }

    private static void showLeaderMenu(GameManager gameManager, Scanner scanner){

        while (true) {

            System.out.println("\n --- Menük ---");
            System.out.println("1. Tag hozzáadása");
            System.out.println("2. Tag eltávolítása");
            System.out.println("3. Perk hozzáadása taghoz");
            System.out.println("4. Tag perk-jeinek listázása");
            System.out.println("5. Tag perk-jének cseréje");
            System.out.println("6. Kilépés");

            System.out.println("Válassz egy lehetőséget: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Tag neve: ");
                    String name = scanner.nextLine();
                    System.out.println("Vezető (igen/nem): ");
                    boolean isLeader = scanner.nextBoolean();
                    gameManager.addMemberToDatabase(new Member(name, isLeader));
                    break;
                case 2:
                    System.out.print("Add meg a tag nevét, akit el szeretnél távolítani: ");
                    String memberToRemove = scanner.nextLine();
                    boolean wasLeaderRemoved = gameManager.removeMemberFromDatabase(memberToRemove);

                    if (wasLeaderRemoved) {
                        System.out.println("A kiválasztott tag, aki vezető volt, eltávolítva.");
                        System.out.println("Nincs aktuális vezető, a program leáll.");
                        scanner.close();
                        return;
                    } else {
                        System.out.println("Tag eltávolítva: " + memberToRemove);
                    }
                    break;
                case 3:
                    System.out.print("Tag neve, akinek perk hozzáadása: ");
                    String targetMemberName = scanner.nextLine();
                    System.out.print("Megadott perk neve: ");
                    String perkName = scanner.nextLine();
                    gameManager.addPerkToMember(targetMemberName, perkName);
                    break;
                case 4:
                    System.out.print("Tag neve a perkek listázásához: ");
                    String memberNameToList = scanner.nextLine();
                    gameManager.listMemberPerks(memberNameToList);
                    break;
                case 5:
                    System.out.print("Tag neve, akinek perkjét cserélni szeretnéd: ");
                    String memberNameForReplace = scanner.nextLine();
                    System.out.print("Régi perk neve: ");
                    String oldPerkName = scanner.nextLine();
                    System.out.print("Új perk neve: ");
                    String newPerkName = scanner.nextLine();
                    gameManager.replaceMemberPerk(memberNameForReplace, oldPerkName, newPerkName);
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
}