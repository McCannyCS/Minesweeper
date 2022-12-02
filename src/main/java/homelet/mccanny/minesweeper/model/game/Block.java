package homelet.mccanny.minesweeper.model.game;

/**
 * Class Block
 *
 * Represent a single block in minesweeper game.
 *
 * This class contains Task 1.
 */
public class Block {
	
	/** isMine: true if block contains a mine */
	private final boolean isMine;
	/** rowIndex: the index in row */
	private int rowIndex;
	/** colIndex: the index in column */
	private int colIndex;
	/** flagged: true if user has flagged this block */
	private boolean flagged;
	/** revealed: true if this block is revealed */
	private boolean revealed;
	/** numMineAround: the number of mines that is around this block */
	private int numMineAround;
	
	/**
	 * Construct this block with specified parameters, other parameters should be left with default state.
	 *
	 * @param rowIndex the index to the row
	 * @param colIndex the index to the col
	 * @param isMine   if this block is a mine block
	 */
	public Block(int rowIndex, int colIndex, boolean isMine) {
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.isMine = isMine;
		this.flagged = false;
		this.revealed = false;
		this.numMineAround = 0;
	}
	
	/**
	 * Construct block from data.
	 */
	public Block(boolean isMine, int rowIndex, int colIndex, boolean flagged, boolean revealed, int numMineAround) {
		this.isMine = isMine;
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.flagged = flagged;
		this.revealed = revealed;
		this.numMineAround = numMineAround;
	}
	
	/**
	 * Copy Constructor, clone data from another Block
	 */
	public Block(Block other) {
		this.isMine = other.isMine;
		this.rowIndex = other.rowIndex;
		this.colIndex = other.colIndex;
		this.flagged = other.flagged;
		this.revealed = other.revealed;
		this.numMineAround = other.numMineAround;
	}
	
	// *****************************************************************************************************************
	// Tasks
	// *****************************************************************************************************************
	
	/**
	 * Task 1: GetBlockStatus
	 *
	 * Determine the status of the block.
	 *
	 * @param gameFinished true if game is finished, otherwise false
	 * @return a BlockStatus that describes the status of this block
	 */
	public BlockStatus getBlockStatus(boolean gameFinished) {
		// TODO Implement Task 1
		return BlockStatus.HIDDEN;
	}
	
	// *****************************************************************************************************************
	// helper functions that you may use
	// *****************************************************************************************************************
	
	/**
	 * Get the BlockStatus (M_0, ..., M_8) depending on the value of numMineAround variable.
	 *
	 * @return a block status (M_0, ..., M_8)
	 * @throws IllegalStateException if numMineAround > 8 or numMineAround < 0
	 */
	private BlockStatus getMineAroundStatus() {
		if (numMineAround == 0) {
			return BlockStatus.M_0;
		} else if (numMineAround == 1) {
			return BlockStatus.M_1;
		} else if (numMineAround == 2) {
			return BlockStatus.M_2;
		} else if (numMineAround == 3) {
			return BlockStatus.M_3;
		} else if (numMineAround == 4) {
			return BlockStatus.M_4;
		} else if (numMineAround == 5) {
			return BlockStatus.M_5;
		} else if (numMineAround == 6) {
			return BlockStatus.M_6;
		} else if (numMineAround == 7) {
			return BlockStatus.M_7;
		} else if (numMineAround == 8) {
			return BlockStatus.M_8;
		} else {
			throw new IllegalStateException("numMineAround = " + numMineAround);
		}
	}
	
	// *****************************************************************************************************************
	// DO NOT TOUCH CODE BELOW THIS LINE
	// *****************************************************************************************************************
	
	public int getRowIndex() {
		return rowIndex;
	}
	
	public int getColIndex() {
		return colIndex;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}
	
	public boolean isMine() {
		return isMine;
	}
	
	public boolean isFlagged() {
		return flagged;
	}
	
	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}
	
	public boolean isRevealed() {
		return revealed;
	}
	
	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}
	
	public int getNumMineAround() {
		return numMineAround;
	}
	
	public void setNumMineAround(int numMineAround) {
		if (numMineAround < 0 || numMineAround > 8) {
			throw new IllegalArgumentException("Numbers of mine around a block should be between 0 and 8");
		}
		this.numMineAround = numMineAround;
	}
	
	@Override
	public String toString() {
		String repr;
		if (isMine) {
			repr = "MineBlock";
		} else {
			repr = "RegularBlock";
		}
		repr += " at position (" + rowIndex + ", " + colIndex + ")";
		if (flagged) {
		    repr += " FLAGGED";
		}
		if (revealed) {
			repr += " REVEALED";
		}
		if (!flagged && !revealed){
			repr += " HIDDEN";
		}
		repr += " with " + numMineAround + " mine block around";
		return repr;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Block block = (Block) o;
		
		if (isMine != block.isMine) return false;
		if (rowIndex != block.rowIndex) return false;
		if (colIndex != block.colIndex) return false;
		if (flagged != block.flagged) return false;
		if (revealed != block.revealed) return false;
		return numMineAround == block.numMineAround;
	}
	
	@Override
	public int hashCode() {
		int result = (isMine ? 1 : 0);
		result = 31 * result + rowIndex;
		result = 31 * result + colIndex;
		result = 31 * result + (flagged ? 1 : 0);
		result = 31 * result + (revealed ? 1 : 0);
		result = 31 * result + numMineAround;
		return result;
	}
}
