package konrad.hu;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {

        GameManager gameManager = new GameManager();
        gameManager.databaseCheck();

    }
}