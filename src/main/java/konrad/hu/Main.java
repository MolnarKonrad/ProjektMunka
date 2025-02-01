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
            }
        }
    }
}