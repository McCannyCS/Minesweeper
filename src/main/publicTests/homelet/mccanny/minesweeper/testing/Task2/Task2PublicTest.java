package homelet.mccanny.minesweeper.testing.Task2;

import homelet.mccanny.minesweeper.model.game.HardnessLevel;
import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static homelet.mccanny.minesweeper.testing.utility.MinesweeperConsolePrettyPrinter.prettyPrinter;
import static homelet.mccanny.minesweeper.testing.utility.TestUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Task 2 Public Tests")
public class Task2PublicTest {
	
	@DisplayName("fisherYatesShuffle")
	@ParameterizedTest(name = "TestCase {0}")
	@ValueSource(strings = {
			"task2_3x3_3_public",
			"task2_9x9_10_public",
			"task2_16x16_40_public",
			"task2_30x16_99_public",
			"task2_customHardness_public"
	})
	public void fisherYatesShuffle_goldenCompareTest(String objFile) {
		Task2TestObj testObj = loadTestObj(Task2TestObj.class, objFile);
		setMinesweeperRandomSeed(testObj.seed);
		System.out.print("Expected ");
		prettyPrinter(testObj.golden).printMineDistribution();
		MinesweeperGame actual = new MinesweeperGame(new HardnessLevel(testObj.boardWidth, testObj.boardHeight, testObj.numMine));
		System.out.print("Actual ");
		prettyPrinter(actual).printMineDistribution();
		// assertions
		assertEquals(testObj.golden.getBoard().length, actual.getBoard().length, "Wrong dimension (height is wrong)");
		for (int i = 0; i < testObj.golden.getBoard().length; i++) {
			assertEquals(testObj.golden.getBoard()[i].length, actual.getBoard()[i].length, "Wrong dimension (width is wrong)");
			for(int j = 0; j < testObj.golden.getBoard()[i].length; j++) {
				assertEquals(testObj.golden.getBoard()[i][j].isMine(), actual.getBoard()[i][j].isMine(),
						"Shuffle result is different at position (" + i + ", " + j + ")");
			}
		}
		resetMinesweeperRandom();
	}
}
