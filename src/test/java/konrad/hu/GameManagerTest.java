package konrad.hu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {
    private GameManager gameManager;

    @BeforeEach
    public void setUp() {
        gameManager = new GameManager();
    }

    @Test
    public void testAddValidMemberToEmptyDatabase() {
        assert (gameManager.addTestMemberToDatabase(new Member("John Doe", true)));

    }

    @Test
    public void testAddValidMemberToExistingDatabase() {
        gameManager.addTestMemberToDatabase(new Member("Jane Smith", false));
        assertTrue(gameManager.addTestMemberToDatabase(new Member("Alice Johnson", false)));

    }
}
