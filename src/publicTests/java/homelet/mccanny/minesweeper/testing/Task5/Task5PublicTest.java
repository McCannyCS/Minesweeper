package homelet.mccanny.minesweeper.testing.Task5;

import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static homelet.mccanny.minesweeper.testing.utility.MinesweeperConsolePrettyPrinter.prettyPrinter;
import static homelet.mccanny.minesweeper.testing.utility.TestUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Task 5 Public Tests")
public class Task5PublicTest {

    @DisplayName("hasUnFlaggedMines")
    @ParameterizedTest(name = "TestCase {0}")
    @ValueSource(strings = {
            "task5_3x3_3_public",
            "task5_9x9_10_public",
            "task5_16x16_40_public",
            "task5_30x16_99_public",
            "task5_customHardness_public"
    })
    public void hasUnFlaggedMines_tests(String objFile) {
        Task5TestObj testObj = loadTestObj(Task5TestObj.class, objFile);
        setMinesweeperRandomSeed(testObj.seed);
        MinesweeperGame game = new MinesweeperGame(testObj.game);
        prettyPrinter(game).printCorrectAndIncorrectFlagged();
        // assertion
        assertEquals(testObj.hasUnFlaggedMine, game.hasUnFlaggedMines(), "hasUnFlagMines wrong");
        resetMinesweeperRandom();
    }
}
