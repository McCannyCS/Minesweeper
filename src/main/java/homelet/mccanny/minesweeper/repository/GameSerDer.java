package homelet.mccanny.minesweeper.repository;

import homelet.mccanny.minesweeper.model.game.Block;
import homelet.mccanny.minesweeper.model.game.HardnessLevel;
import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

@Repository
public class GameSerDer {

    private static final String BOARD_KEY = "board";
    private static final String BLOCK_IS_MINE_KEY = "isMine";
    private static final String BLOCK_FLAGGED_KEY = "flagged";
    private static final String BLOCK_REVEALED_KEY = "revealed";
    private static final String BLOCK_MINE_AROUND_KEY = "minesAround";

    private static final String LEVEL_KEY = "level";
    private static final String BOARD_WIDTH_KEY = "boardWidth";
    private static final String BOARD_HEIGHT_KEY = "boardHeight";
    private static final String NUM_MINE_KEY = "numMine";

    private static final String TIME_ELAPSED_KEY = "timeElapsed";
    private static final String PAUSING_KEY = "pausing";
    private static final String SURRENDER_KEY = "surrender";

    // *****************************************************************************************************************
    // Serializer
    // *****************************************************************************************************************

    public JSONObject saveGameToJson(MinesweeperGame game) {
        JSONObject gameObj = new JSONObject();
        saveGameToJson(gameObj, game);
        return gameObj;
    }

    public void saveGameToJson(JSONObject gameObj, MinesweeperGame game) {
        gameObj.put(BOARD_KEY, saveBoard(game.getBoard()));
        gameObj.put(LEVEL_KEY, saveHardnessLevel(game.getLevel()));
        gameObj.put(TIME_ELAPSED_KEY, game.getTimeElapsed());
        gameObj.put(PAUSING_KEY, game.isPausing());
        gameObj.put(SURRENDER_KEY, game.isSurrender());
    }

    private JSONArray saveBoard(Block[][] board) {
        JSONArray boardArr = new JSONArray();
        for (Block[] row : board) {
            JSONArray colArr = new JSONArray();
            for (Block block : row) {
                colArr.put(saveBlock(block));
            }
            boardArr.put(colArr);
        }
        return boardArr;
    }

    private JSONObject saveBlock(Block block) {
        JSONObject blockObj = new JSONObject();
        blockObj.put(BLOCK_IS_MINE_KEY, block.isMine());
        blockObj.put(BLOCK_FLAGGED_KEY, block.isFlagged());
        blockObj.put(BLOCK_REVEALED_KEY, block.isRevealed());
        blockObj.put(BLOCK_MINE_AROUND_KEY, block.getNumMineAround());
        return blockObj;
    }

    private JSONObject saveHardnessLevel(HardnessLevel level) {
        JSONObject levelObj = new JSONObject();
        levelObj.put(BOARD_WIDTH_KEY, level.getBoardWidth());
        levelObj.put(BOARD_HEIGHT_KEY, level.getBoardHeight());
        levelObj.put(NUM_MINE_KEY, level.getNumMines());
        return levelObj;
    }

    // *****************************************************************************************************************
    // Deserializer
    // *****************************************************************************************************************

    public MinesweeperGame readGameFromJson(JSONObject gameObj) {
        Block[][] board = readBoard(gameObj.getJSONArray(BOARD_KEY));
        HardnessLevel level = readHardnessLevel(gameObj.getJSONObject(LEVEL_KEY));
        long timeElapsed = gameObj.getLong(TIME_ELAPSED_KEY);
        boolean pausing = gameObj.getBoolean(PAUSING_KEY);
        boolean surrender = gameObj.getBoolean(SURRENDER_KEY);
        return new MinesweeperGame(board, level, timeElapsed, pausing, surrender);
    }

    private HardnessLevel readHardnessLevel(JSONObject level) {
        int boardWidth = level.getInt(BOARD_WIDTH_KEY);
        int boardHeight = level.getInt(BOARD_HEIGHT_KEY);
        int numMines = level.getInt(NUM_MINE_KEY);
        return new HardnessLevel(boardWidth, boardHeight, numMines);
    }

    private Block[][] readBoard(JSONArray boardArr) {
        Block[][] board = new Block[boardArr.length()][];
        for (int rowIndex = 0; rowIndex < boardArr.length(); rowIndex++) {
            JSONArray rowArr = boardArr.getJSONArray(rowIndex);
            Block[] row = new Block[rowArr.length()];
            for (int colIndex = 0; colIndex < rowArr.length(); colIndex++) {
                row[colIndex] = readBlock(rowArr.getJSONObject(colIndex), rowIndex, colIndex);
            }
            board[rowIndex] = row;
        }
        return board;
    }

    private Block readBlock(JSONObject block, int rowIndex, int colIndex) {
        boolean isMine = block.getBoolean(BLOCK_IS_MINE_KEY);
        boolean flagged = block.getBoolean(BLOCK_FLAGGED_KEY);
        boolean revealed = block.getBoolean(BLOCK_REVEALED_KEY);
        int mineAround = block.getInt(BLOCK_MINE_AROUND_KEY);
        return new Block(isMine, rowIndex, colIndex, flagged, revealed, mineAround);
    }

}
