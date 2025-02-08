package konrad.hu;

import java.sql.SQLException;

public class Main {
/**
 * A program belépési metódusa.
 *
 * Ez a metódus létrehozza a GameManager példányt,
 * és meghívja a databaseCheck() metódust az adatbázis állapotának ellenőrzésére.
 * @throws SQLException ha hiba lép fel az adatbázis kapcsolat létrehozásakor
 */
    public static void main(String[] args) throws SQLException {

        GameManager gameManager = new GameManager();
        gameManager.databaseCheck();

    }
}