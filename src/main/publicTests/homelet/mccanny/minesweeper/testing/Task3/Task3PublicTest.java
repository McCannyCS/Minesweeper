package homelet.mccanny.minesweeper.testing.Task3;

import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static homelet.mccanny.minesweeper.testing.utility.MinesweeperConsolePrettyPrinter.prettyPrinter;
import static homelet.mccanny.minesweeper.testing.utility.TestUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Task 3 Public Tests")
public class Task3PublicTest {
	
	@DisplayName("getAvailableFlags")
	@ParameterizedTest(name = "TestCase {0}")
	@ValueSource(strings = {
			"task3_allFlagged_1",
			"task3_halfFlagged_1",
			"task3_someFlagged_1",
			"task3_noneFlagged_1",
	})
	public void getAvailableFlags_tests(String objFile) {
		Task3TestObj testObj = loadTestObj(Task3TestObj.class, objFile);
		setMinesweeperRandomSeed(testObj.seed);
		MinesweeperGame game = new MinesweeperGame(testObj.game);
		prettyPrinter(game).printFlagged();
		// assertion
		assertEquals(testObj.expectedRemainingMine, game.getAvailableFlags(), "Available flags calculated wrong");
		resetMinesweeperRandom();
	}
}
