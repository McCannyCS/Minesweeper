package homelet.mccanny.minesweeper.model.game;

public class HardnessLevel {
	public static final HardnessLevel TINY = new HardnessLevel(3, 3, 3);
	public static final HardnessLevel EASY = new HardnessLevel(9, 9, 10);
	public static final HardnessLevel MEDIUM = new HardnessLevel(16, 16, 40);
	public static final HardnessLevel HARD = new HardnessLevel(30, 16, 99);
	
	/** boardWidth: the width of the board */
	private final int boardWidth;
	/** boardWidth: the height of the board */
	private final int boardHeight;
	/** numMines: number of mines on the board */
	private final int numMines;
	
	public HardnessLevel(int boardWidth, int boardHeight, int numMines) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.numMines = numMines;
	}
	
	@Override
	public String toString() {
		return boardWidth + "x" + boardHeight + " (numMines=" + numMines + ')';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		HardnessLevel level = (HardnessLevel) o;
		
		if (boardWidth != level.boardWidth) return false;
		if (boardHeight != level.boardHeight) return false;
		return numMines == level.numMines;
	}
	
	@Override
	public int hashCode() {
		int result = boardWidth;
		result = 31 * result + boardHeight;
		result = 31 * result + numMines;
		return result;
	}
	
	// *****************************************************************************************************************
	// getters
	// *****************************************************************************************************************
	
	public int getBoardWidth() {
		return boardWidth;
	}
	
	public int getBoardHeight() {
		return boardHeight;
	}
	
	public int getNumMines() {
		return numMines;
	}
}
