package homelet.mccanny.minesweeper.testing.Task7;

import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static homelet.mccanny.minesweeper.testing.utility.MinesweeperConsolePrettyPrinter.prettyPrinter;
import static homelet.mccanny.minesweeper.testing.utility.TestUtility.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Task 7 Public Tests")
public class Task7PublicTest {
	
	@DisplayName("revealBlock")
	@ParameterizedTest(name = "TestCase {0}")
	@ValueSource(strings = {
			"task7_3x3_3_public_mineReveal",
			"task7_3x3_3_public_noneReveal",
			"task7_3x3_3_public_oneReveal",
			"task7_9x9_10_public_oneWildOneShallow",
			"task7_9x9_10_public_oneWildReveal",
			"task7_16x16_40_public_noShallow",
			"task7_30x16_99_public_allShallow",
			"task7_30x16_99_public_mixed",
			"task7_customHardness_public_mixed",
	})
	public void revealBlock_tests(String objFile) throws IllegalAccessException, NoSuchFieldException {
		Task7TestObj testObj = loadTestObj(Task7TestObj.class, objFile);
		setMinesweeperRandomSeed(testObj.seed);
		MinesweeperGame actual = new MinesweeperGame(testObj.gameBeforeReveal);
		System.out.print("Before ");
		prettyPrinter(actual).printRevealed();
		// performs reveal sequence
		System.out.println("Reveal sequence: ");
		for (int i = 0; i < testObj.getMaxStep(); i++) {
			int[] step = testObj.getStep(i);
			System.out.println("Step " + (i + 1) + " : (" + step[0] + ", " + step[1] + ")");
			actual.revealBlock(step[0], step[1]);
		}
		System.out.println();
		System.out.print("After ");
		prettyPrinter(actual).printRevealed();
		System.out.print("Expected After ");
		prettyPrinter(testObj.gameAfterReveal).printRevealed();
		// assertion
		assertEquals(testObj.gameAfterReveal.getBoard().length, actual.getBoard().length, "Wrong dimension (height is wrong)");
		for (int i = 0; i < testObj.gameAfterReveal.getBoard().length; i++) {
			assertEquals(testObj.gameAfterReveal.getBoard()[i].length, actual.getBoard()[i].length, "Wrong dimension (width is wrong)");
			for(int j = 0; j < testObj.gameAfterReveal.getBoard()[i].length; j++) {
				assertEquals(testObj.gameAfterReveal.getBoard()[i][j].isRevealed(), actual.getBoard()[i][j].isRevealed(),
						"Reveal status is different at position (" + i + ", " + j + ")");
			}
		}
		resetMinesweeperRandom();
	}
}
