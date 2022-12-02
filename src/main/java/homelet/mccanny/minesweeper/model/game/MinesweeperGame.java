package homelet.mccanny.minesweeper.model.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MinesweeperGame {
	
	/** board: representation of the game board */
	private final Block[][] board;
	/** hardnessLevel: the hardnessLevel the game was initialized with */
	private final HardnessLevel level;
	/** timeElapsed: the time elapsed on the game */
	private long timeElapsed;
	/** pausing: true if game is paused */
	private boolean pausing;
	/** surrendered: true if user has surrendered */
	private boolean surrender;
	
	/**
	 * Initialize a game of the level.
	 */
	public MinesweeperGame(HardnessLevel level) {
		this.level = level;
		this.board = new Block[level.getBoardHeight()][level.getBoardWidth()];
		this.timeElapsed = 0;
		this.pausing = false;
		this.surrender = false;
		generateRandomBoard();
	}
	
	/**
	 * Initialize game with existing data, simply assign the values.
	 */
	public MinesweeperGame(Block[][] board, HardnessLevel level, long timeElapsed, boolean pausing, boolean surrender) {
		this.board = board;
		this.level = level;
		this.timeElapsed = timeElapsed;
		this.pausing = pausing;
		this.surrender = surrender;
	}
	
	/**
	 * Copy construct the game with data from the other game.
	 */
	public MinesweeperGame(MinesweeperGame other) {
		this.board = Arrays.stream(other.board)
				.map(rows -> Arrays.stream(rows).map(Block::new).toArray(Block[]::new))
				.toArray(Block[][]::new);
		this.level = other.level;
		this.timeElapsed = other.timeElapsed;
		this.pausing = other.pausing;
		this.surrender = other.surrender;
	}
	
	// *****************************************************************************************************************
	// Tasks
	// *****************************************************************************************************************
	
	/**
	 * Task 2: Fisher-Yates Shuffle
	 *
	 * Shuffle the board according to the Fisher-Yates Shuffling algorithm.
	 */
	private void fisherYatesShuffle() {
		// TODO implement Task 2
		int n = level.getBoardWidth() * level.getBoardHeight();
		for (int i = 0; i < n - 1; i++) {
			int j = (int) (random());   // change this line to generate j, such that i <= j < n
			int iRowIndex = i / level.getBoardWidth();
			int iColIndex = i % level.getBoardWidth();
			int jRowIndex = j / level.getBoardWidth();
			int jColIndex = j % level.getBoardWidth();
			swap(iRowIndex, iColIndex, jRowIndex, jColIndex);
		}
	}
	
	/**
	 * Task 3: Get available flags
	 *
	 * Get the number of flag available to user.
	 *
	 * @return numMine - numFlaggedBlock
	 */
	public int getAvailableFlags() {
		int numFlaggedBlock = 0;
		// TODO Implement Task 3
		return level.getNumMines() - numFlaggedBlock;
	}
	
	/**
	 * Task 4: Count mines around block.
	 *
	 * Precondition: (rowIndex, colIndex) is a valid position.
	 *
	 * @param rowIndex row index
	 * @param colIndex col index
	 * @return the number of mine blocks around (rowIndex, colIndex).
	 */
	public int countMinesAround(int rowIndex, int colIndex) {
		int numMinesAround = 0;
		// TODO Implement Task 4
		return numMinesAround;
	}
	
	/**
	 * Task 5: has un-flagged mines
	 *
	 * @return true if there are some un-flagged mine blocks, otherwise false
	 */
	public boolean hasUnFlaggedMines() {
		// TODO Implement Task 5
		return true;
	}
	
	/**
	 * Task 6: Has revealed mines
	 *
	 * @return true if there are some revealed mine blocks, otherwise false
	 */
	public boolean hasRevealedMine() {
		// TODO Implement Task 6
		return false;
	}
	
	/**
	 * Task 7: Reveal Block (left-click action)
	 *
	 * Reveal the block at (rowIndex, colIndex), and reveal the adjacent blocks.
	 *
	 * Precondition: (rowIndex, colIndex) is a valid position
	 *
	 * @param rowIndex row index
	 * @param colIndex col index
	 */
	public void revealBlock(int rowIndex, int colIndex) {
		// TODO Implement Task 7
	}
	
	// *****************************************************************************************************************
	// helper functions that you may use
	// *****************************************************************************************************************
	
	/**
	 * Returns a random number, the behaviour is consistent with Math.random().
	 *
	 * @return a random number in range [0, 1),
	 *         Note: 0 is inclusive, 1 is exclusive
	 */
	private double random() {
		return randomNumberGenerator.nextDouble();
	}
	
	/**
	 * Check if a position (rowIndex, colIndex) is not on the board.
	 *
	 * @param rowIndex row index
	 * @param colIndex col index
	 * @return true if (rowIndex, colIndex) does not represent a valid position on board
	 */
	public boolean isInvalidPosition(int rowIndex, int colIndex) {
		return rowIndex < 0 || colIndex < 0 || rowIndex >= level.getBoardHeight() || colIndex >= level.getBoardWidth();
	}
	
	/**
	 * Get all adjacent and diagonal blocks around the block (rowIndex, colIndex).
	 * <p>
	 * Note only blocks on valid index are returned. The returned list should have a maximum of 8 blocks.
	 * <p>
	 * Precondition: (rowIndex, colIndex) is a valid position.
	 *
	 * @param rowIndex row index
	 * @param colIndex col index
	 * @return an arraylist of blocks
	 */
	public ArrayList<Block> getBlocksAround(int rowIndex, int colIndex) {
		ArrayList<Block> blocksAround = new ArrayList<>();
		for (int[] step : MinesweeperGame.STEPS) {
			int nextRowIndex = rowIndex + step[0];
			int nextColIndex = colIndex + step[1];
			if (!isInvalidPosition(nextRowIndex, nextColIndex)) {
				blocksAround.add(board[nextRowIndex][nextColIndex]);
			}
		}
		return blocksAround;
	}
	
	/**
	 * Swap the block on (row1, col1) and on (row2, col2)
	 * <p>
	 * Precondition: (row1, col1) and (row2, col2) are valid positions
	 *
	 * @param row1 row index for first
	 * @param col1 col index for first
	 * @param row2 row index for second
	 * @param col2 col index for second
	 */
	private void swap(int row1, int col1, int row2, int col2) {
		Block temp = board[row2][col2];
		board[row2][col2] = board[row1][col1];
		board[row1][col1] = temp;
		// also update row/col index
		board[row1][col1].setRowIndex(row1);
		board[row1][col1].setColIndex(col1);
		board[row2][col2].setRowIndex(row2);
		board[row2][col2].setColIndex(col2);
	}
	
	// *****************************************************************************************************************
	// DO NOT TOUCH CODE BELOW THIS LINE
	// *****************************************************************************************************************
	
	/** all possible steps */
	private static final int[][] STEPS = {
			{-1, -1}, {-1, 0}, {-1, 1},
			{0, -1}, {0, 1},
			{1, -1}, {1, 0}, {1, 1}
	};
	
	/** randomNumberGenerator */
	private static Random randomNumberGenerator = new Random();
	
	/** Generate a random board. */
	private void generateRandomBoard() {
		// first initialize all blocks, the first numMine number of blocks are mines;
		int mineCount = 0;
		for (int rowIndex = 0; rowIndex < level.getBoardHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < level.getBoardWidth(); colIndex++) {
				board[rowIndex][colIndex] = new Block(rowIndex, colIndex, mineCount++ < level.getNumMines());
			}
		}
		// shuffle-board
		fisherYatesShuffle();
		// set mine around
		for (int rowIndex = 0; rowIndex < level.getBoardHeight(); rowIndex++) {
			for (int colIndex = 0; colIndex < level.getBoardWidth(); colIndex++) {
				board[rowIndex][colIndex].setNumMineAround(countMinesAround(rowIndex, colIndex));
			}
		}
	}
	
	/**
	 * Get the status of the game.
	 * - LOSE if user surrendered
	 * - PAUSING if pausing flag was set.
	 * - WIN if all mine blocks are flagged.
	 * - LOSE if some mine blocks is revealed.
	 * - otherwise PLAYING
	 */
	public GameStatus getGameStatus() {
		if (surrender) {
			return GameStatus.LOSE;
		} else if (pausing) {
			return GameStatus.PAUSING;
		} else if (hasRevealedMine()) {
			return GameStatus.LOSE;
		} else if (hasUnFlaggedMines()) {
			return GameStatus.PLAYING;
		} else {
			return GameStatus.WIN;
		}
	}
	
	/**
	 * Toggle the flagged status of the block at (rowIndex, colIndex). Update numFlagged accordingly.
	 * <p>
	 * Called when user right-click on a block.
	 * <p>
	 * Precondition: (rowIndex, colIndex) is a valid position
	 *
	 * @param rowIndex row index
	 * @param colIndex col index
	 */
	public void toggleFlagBlock(int rowIndex, int colIndex) {
		Block block = board[rowIndex][colIndex];
		// revealed block cannot be flagged
		if (block.isRevealed()) {
			return;
		}
		block.setFlagged(!block.isFlagged());
	}
	
	/** Precondition: (rowIndex, colIndex) is a valid position. */
	public Block getBlock(int rowIndex, int colIndex) {
		return board[rowIndex][colIndex];
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MinesweeperGame game = (MinesweeperGame) o;
		
		if (timeElapsed != game.timeElapsed) return false;
		if (pausing != game.pausing) return false;
		if (surrender != game.surrender) return false;
		if (!Arrays.deepEquals(board, game.board)) return false;
		return level.equals(game.level);
	}
	
	@Override
	public int hashCode() {
		int result = Arrays.deepHashCode(board);
		result = 31 * result + level.hashCode();
		result = 31 * result + (int) (timeElapsed ^ (timeElapsed >>> 32));
		result = 31 * result + (pausing ? 1 : 0);
		result = 31 * result + (surrender ? 1 : 0);
		return result;
	}
	
	public Block[][] getBoard() {
		return board;
	}
	
	public HardnessLevel getLevel() {
		return level;
	}
	
	public long getTimeElapsed() {
		return timeElapsed;
	}
	
	public synchronized void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	
	public boolean isPausing() {
		return pausing;
	}
	
	public void setPausing(boolean pausing) {
		this.pausing = pausing;
	}
	
	public boolean isSurrender() {
		return surrender;
	}
	
	public void setSurrender(boolean surrender) {
		this.surrender = surrender;
	}
}
