package homelet.mccanny.minesweeper.service;

import homelet.mccanny.minesweeper.model.game.*;
import homelet.mccanny.minesweeper.repository.GameSerDer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;

@Component
public class MinesweeperManager {

    private static final Logger logger = LogManager.getLogger();

    /**
     * game: an instance of minesweeper game
     */
    private MinesweeperGame game;
    /**
     * gameEventListener: an listener for game events
     */
    private GameEventListener gameEventListener;
    /**
     * gameTouched: a flag indicate the game has received any user input after created
     */
    private boolean gameTouched;
    /**
     * currentGameSaveLocation: the save location of current game, if null means not saved
     */
    private File currentGameSaveLocation;

    @Autowired
    private GameSerDer serDer;

    // *****************************************************************************************************************
    // Game state listener
    // *****************************************************************************************************************

    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public GameEventListener getGameEventListener() {
        return Objects.requireNonNull(gameEventListener, "Event listener not set");
    }

    // *****************************************************************************************************************
    // Game state setter
    // *****************************************************************************************************************

    public void setTimeElapsed(long timeElapsed) {
        game.setTimeElapsed(timeElapsed);
        gameTouched = true;
    }

    public void setPausing(boolean pausing) {
        game.setPausing(pausing);
        gameTouched = true;
        getGameEventListener().onGameChanged(getGameStatus());
    }

    // *****************************************************************************************************************
    // Game state getter
    // *****************************************************************************************************************

    public boolean isGameNotInitialized() {
        return game == null;
    }

    public boolean isGamePausing(GameStatus status) {
        return status == GameStatus.PAUSING;
    }

    public boolean isGameWon(GameStatus status) {
        return status == GameStatus.WIN;
    }

    public boolean isGameLose(GameStatus status) {
        return status == GameStatus.LOSE;
    }

    public boolean isGameFinished(GameStatus status) {
        return isGameWon(status) || isGameLose(status);
    }

    public long getTimeElapsed() {
        return game.getTimeElapsed();
    }

    public int getRemainingMine() {
        return game.getAvailableFlags();
    }

    public boolean shouldSaveGame() {
        return game != null && gameTouched;
    }

    public boolean hasSaveLocation() {
        return currentGameSaveLocation != null;
    }

    public File getCurrentGameSaveLocation() {
        return currentGameSaveLocation;
    }

    public GameStatus getGameStatus() {
        return game.getGameStatus();
    }

    public HardnessLevel getGameHardnessLevel() {
        return game.getLevel();
    }

    public BlockStatus getBlockStatus(int rowIndex, int colIndex, boolean gameFinished) {
        if (game.isInvalidPosition(rowIndex, colIndex)) {
            logger.error("Cannot get status of block on position ({}, {}), out of bound", rowIndex, colIndex);
            return BlockStatus.HIDDEN;
        }
        return game.getBlock(rowIndex, colIndex).getBlockStatus(gameFinished);
    }

    public MinesweeperGame getGame() {
        return game;
    }

    // *****************************************************************************************************************
    // Game actions
    // *****************************************************************************************************************

    public boolean saveGame(File gameFile) {
        if (isGameNotInitialized()) {
            logger.error("Game was not initialised to save");
            return false;
        }
        try (FileWriter writer = new FileWriter(gameFile)) {
            JSONObject gameObject = serDer.saveGameToJson(game);
            writer.write(gameObject.toString(4));
            gameTouched = false;    // changes was saved
            currentGameSaveLocation = gameFile;
            logger.info("Game saved at {}", gameFile);
            return true;
        } catch (IOException e) {
            logger.error(e);
            logger.error("Could not save game file");
            return false;
        } catch (JSONException e) {
            logger.error(e);
            logger.error("Failed to save game");
            return false;
        }
    }

    public boolean loadGame(File gameFile) {
        try (InputStream file = new BufferedInputStream(new FileInputStream(gameFile))) {
            JSONTokener tokener = new JSONTokener(file);
            game = serDer.readGameFromJson(new JSONObject(tokener));
            gameTouched = false;
            currentGameSaveLocation = gameFile;
            // trigger listener
            getGameEventListener().onGameStart();
            getGameEventListener().onGameChanged(getGameStatus());
            logger.info("Game loaded from {} with hardness {}", gameFile, game.getLevel());
            return true;
        } catch (IOException e) {
            logger.error(e);
            logger.error("Could not load game file");
            return false;
        } catch (JSONException e) {
            logger.error(e);
            logger.error("Failed to parse game file");
            return false;
        }
    }

    public void newGame(HardnessLevel level) {
        game = new MinesweeperGame(level);
        gameTouched = false;
        currentGameSaveLocation = null;
        logger.info("Game created with hardness {}", game.getLevel());
        // trigger listener
        getGameEventListener().onGameStart();
        GameStatus statusAfterAction = getGameStatus();
        getGameEventListener().onGameChanged(statusAfterAction);
        if (isGameWon(statusAfterAction)) {
            getGameEventListener().onGameWon();
        } else if (isGameLose(statusAfterAction)) {
            getGameEventListener().onGameLose();
        }
    }

    public void surrenderGame() {
        game.setSurrender(true);
        gameTouched = true;
        // trigger listener
        GameStatus statusAfterAction = getGameStatus();
        getGameEventListener().onGameChanged(statusAfterAction);
        if (isGameWon(statusAfterAction)) {
            getGameEventListener().onGameWon();
        } else if (isGameLose(statusAfterAction)) {
            getGameEventListener().onGameLose();
        }
    }

    /**
     * left click to reveal a block
     */
    public void leftClickOnBlock(int rowIndex, int colIndex) {
        if (game.isInvalidPosition(rowIndex, colIndex)) {
            logger.error("Cannot left click on position ({}, {}), out of bound", rowIndex, colIndex);
            return;
        }
        game.revealBlock(rowIndex, colIndex);
        gameTouched = true;
        // trigger listener
        GameStatus statusAfterAction = getGameStatus();
        getGameEventListener().onGameChanged(statusAfterAction);
        if (isGameWon(statusAfterAction)) {
            getGameEventListener().onGameWon();
        } else if (isGameLose(statusAfterAction)) {
            getGameEventListener().onGameLose();
        }
    }

    /**
     * left double click to reveal the block, and all blocks around it
     */
    public void leftDoubleClickOnBlock(int rowIndex, int colIndex) {
        if (game.isInvalidPosition(rowIndex, colIndex)) {
            logger.error("Cannot double left click on position ({}, {}), out of bound", rowIndex, colIndex);
            return;
        }
        for (Block adjBlock : game.getBlocksAround(rowIndex, colIndex)) {
            game.revealBlock(adjBlock.getRowIndex(), adjBlock.getColIndex());
        }
        gameTouched = true;
        // trigger listener
        GameStatus statusAfterAction = getGameStatus();
        getGameEventListener().onGameChanged(statusAfterAction);
        if (isGameWon(statusAfterAction)) {
            getGameEventListener().onGameWon();
        } else if (isGameLose(statusAfterAction)) {
            getGameEventListener().onGameLose();
        }
    }

    /**
     * right click to flag a block
     */
    public void rightClickOnBlock(int rowIndex, int colIndex) {
        if (game.isInvalidPosition(rowIndex, colIndex)) {
            logger.error("Cannot right click on position ({}, {}), out of bound", rowIndex, colIndex);
            return;
        } else if (game.getAvailableFlags() <= 0) {
            logger.info("No more flags");
            return;
        }
        game.toggleFlagBlock(rowIndex, colIndex);
        gameTouched = true;
        // trigger listener
        GameStatus statusAfterAction = getGameStatus();
        getGameEventListener().onGameChanged(statusAfterAction);
        if (isGameWon(statusAfterAction)) {
            getGameEventListener().onGameWon();
        } else if (isGameLose(statusAfterAction)) {
            getGameEventListener().onGameLose();
        }
    }
}
