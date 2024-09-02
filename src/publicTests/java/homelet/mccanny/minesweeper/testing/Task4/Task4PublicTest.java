package homelet.mccanny.minesweeper.testing.Task4;

import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static homelet.mccanny.minesweeper.testing.utility.MinesweeperConsolePrettyPrinter.prettyPrinter;
import static homelet.mccanny.minesweeper.testing.utility.TestUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Task 4 Public Tests")
public class Task4PublicTest {

    @DisplayName("countMinesAround")
    @ParameterizedTest(name = "TestCase {0}")
    @ValueSource(strings = {
            "task4_3x3_3_public",
            "task4_9x9_10_public",
            "task4_16x16_40_public",
            "task4_30x16_99_public",
            "task4_customHardness_public"
    })
    public void countMinesAround_tests(String objFile) {
        Task4TestObj testObj = loadTestObj(Task4TestObj.class, objFile);
        setMinesweeperRandomSeed(testObj.seed);
        MinesweeperGame game = new MinesweeperGame(testObj.game);
        System.out.print("Expected ");
        prettyPrinter(game).printMinesAround(true);
        System.out.print("Actual ");
        prettyPrinter(game).printMinesAround(false);
        // assertions
        for (int i = 0; i < game.getBoard().length; i++) {
            for (int j = 0; j < game.getBoard()[i].length; j++) {
                if (!game.getBoard()[i][j].isMine()) {
                    assertEquals(testObj.game.getBoard()[i][j].getNumMineAround(), game.countMinesAround(i, j),
                            "countNumMineAround result is different at position (" + i + ", " + j + ")");
                }
            }
        }
        resetMinesweeperRandom();
    }
}
