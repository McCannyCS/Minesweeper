package homelet.mccanny.minesweeper.testing.Task6;

import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static homelet.mccanny.minesweeper.testing.utility.MinesweeperConsolePrettyPrinter.prettyPrinter;
import static homelet.mccanny.minesweeper.testing.utility.TestUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Task 6 Public Tests")
public class Task6PublicTest {
	
	@DisplayName("hasRevealedMine")
	@ParameterizedTest(name = "TestCase {0}")
	@ValueSource(strings = {
			"task6_3x3_3_public",
			"task6_9x9_10_public",
			"task6_16x16_40_public",
			"task6_30x16_99_public",
			"task6_customHardness_public"
	})
	public void hasRevealedMine_tests(String objFile) {
		Task6TestObj testObj = loadTestObj(Task6TestObj.class, objFile);
		setMinesweeperRandomSeed(testObj.seed);
		MinesweeperGame game = new MinesweeperGame(testObj.game);
		prettyPrinter(game).printRevealedCorrectAndIncorrect();
		// assertion
		assertEquals(testObj.hasRevealedMine, game.hasRevealedMine(), "hasRevealMine wrong");
		resetMinesweeperRandom();
	}
}
