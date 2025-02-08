package konrad.hu;

/**
 * A Database interfész definiálja az adatbázis műveletekhez szükséges
 * metódusokat, amelyek a tagok és perkek kezelésére szolgálnak.
 */

public interface Database {

    /**
     * Ellenőrzi, hogy az adatbázis üres-e.
     *
     * @return true, ha az adatbázis üres; false, ha van legalább egy tag.
     */
    boolean isDatabaseEmpty();

    /**
     * Megkeresi a tagot a neve alapján.
     *
     * @param memberName a keresett tag neve
     * @return a megtalált Member objektum, vagy null, ha a tag nem található
     */
    Member findMemberByName(String memberName);

    /**
     * Hozzáad egy új tagot az adatbázishoz.
     *
     * @param member a hozzáadandó Member objektum
     */
    void addMemberToDatabase(Member member);

    /**
     * Eltávolít egy tagot az adatbázisból a neve alapján.
     *
     * @param memberNameToDelete a törlendő tag neve
     * @return true, ha a tag sikeresen törölve lett; false, ha a tag nem található
     */
    boolean removeMemberFromDatabase(String memberNameToDelete);

    /**
     * Ellenőrzi, hogy a megadott tag vezető-e.
     *
     * @param memberName a tag neve
     * @return true, ha a tag vezető; false, ha nem
     */
    boolean isLeader(String memberName);

    /**
     * Ellenőrzi, hogy van-e aktív vezető az adatbázisban.
     *
     * @return true, ha van aktív vezető; false, ha nincs
     */
    boolean hasActiveLeader();

    /**
     * Hozzáad egy perkét a megadott taghoz.
     *
     * @param member a tag neve
     * @param perkName a hozzáadandó perk neve
     */
    void addPerkToMember(String member, String perkName);

    /**
     * Kicserél egy meglévő perkét a megadott tagnál.
     *
     * @param memberName a tag neve
     * @param oldPerkName a régi perk neve
     * @param newPerkName az új perk neve
     */
    void replaceMemberPerk(String memberName, String oldPerkName, String newPerkName);

    /**
     * Kimenti az összes tagot és azok perkjeit az Info.txt fájlba.
     */
    void saveMembersAndPerksToFile();

    /**
     * Betölti az Info.txt fájl tartalmát, majd megjeleniti azt a képernyőn.
     */
    void loadFromFile(String fileName);

    /**
     * Előlépteti a megadott tagot vezetővé.
     *
     * @param memberName a tag neve
     * @return true, ha a tag sikeresen vezetővé lett előléptetve; false, ha a tag nem található
     */
    boolean promoteMemberToLeader(String memberName);
}
