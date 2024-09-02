package homelet.mccanny.minesweeper.testing.Task1;

import homelet.mccanny.minesweeper.model.game.Block;
import homelet.mccanny.minesweeper.model.game.BlockStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("task1")
@DisplayName("Task 1 Public Tests")
class Task1PublicTest {

    @Test
    @DisplayName("getBlockStatus game finished")
    public void getBlockStatus_gameFinished_test() {
        Block hiddenMine = new Block(true, 0, 0, false, false, 0);
        assertEquals(BlockStatus.MINE_WRONG, hiddenMine.getBlockStatus(true), "Case HiddenMine failed");

        Block flaggedMine = new Block(true, 0, 0, true, false, 0);
        assertEquals(BlockStatus.MINE_CORRECT, flaggedMine.getBlockStatus(true), "Case FlaggedMine failed");

        Block hiddenRegular = new Block(false, 0, 0, false, false, 0);
        assertEquals(BlockStatus.HIDDEN, hiddenRegular.getBlockStatus(true), "Case HiddenRegular failed");

        Block revealedRegular = new Block(false, 0, 0, false, true, 5);
        assertEquals(BlockStatus.M_5, revealedRegular.getBlockStatus(true), "Case RevealedRegular failed");
    }

    @Test
    @DisplayName("getBlockStatus game not finished")
    public void getBlockStatus_gameNotFinished_test() {
        Block hiddenMine = new Block(true, 0, 0, false, false, 0);
        assertEquals(BlockStatus.HIDDEN, hiddenMine.getBlockStatus(false), "Case HiddenMine failed");

        Block flaggedMine = new Block(true, 0, 0, true, false, 0);
        assertEquals(BlockStatus.FLAGGED, flaggedMine.getBlockStatus(false), "Case FlaggedMine failed");

        Block revealedRegular = new Block(false, 0, 0, false, true, 3);
        assertEquals(BlockStatus.M_3, revealedRegular.getBlockStatus(false), "Case RevealedRegular failed");
    }
}