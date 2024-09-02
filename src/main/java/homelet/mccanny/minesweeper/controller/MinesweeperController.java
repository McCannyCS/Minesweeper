package homelet.mccanny.minesweeper.controller;

import homelet.mccanny.minesweeper.model.game.BlockStatus;
import homelet.mccanny.minesweeper.model.game.GameStatus;
import homelet.mccanny.minesweeper.model.game.HardnessLevel;
import homelet.mccanny.minesweeper.service.GameEventListener;
import homelet.mccanny.minesweeper.service.LeaderboardManager;
import homelet.mccanny.minesweeper.service.MinesweeperManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class MinesweeperController {

    private static final Logger logger = LogManager.getLogger();

    // *****************************************************************************************************************
    // javafx ui component
    // *****************************************************************************************************************

    @FXML
    public MenuItem menuNewGame;
    @FXML
    public MenuItem menuSaveGame;
    @FXML
    public MenuItem menuLoadGame;
    @FXML
    public MenuItem menuPause;

    @FXML
    public ToggleGroup hardnessSelector;
    @FXML
    public RadioMenuItem menuHardnessEasy;
    @FXML
    public RadioMenuItem menuHardnessMedium;
    @FXML
    public RadioMenuItem menuHardnessHard;
    @FXML
    public RadioMenuItem menuHardnessCustom;

    @FXML
    public MenuItem menuLeaderboard;

    @FXML
    public Label remainingMines;
    @FXML
    public Label timeElapsed;
    @FXML
    public Button gameButton;

    @FXML
    public GridPane gameBoard;
    @FXML
    public Label pausingLabel;

    @FXML
    public Label aboutLabel;

    private Timeline timeElapsedTimeLine;

    // *****************************************************************************************************************
    // game assets
    // *****************************************************************************************************************

    @Autowired(required = false)
    private Font ledFont;
    @Autowired
    private ObservableMap<GameStatus, Image> buttonRaisedMap;
    @Autowired
    private ObservableMap<GameStatus, Image> buttonPressedMap;
    @Autowired
    private ObservableMap<BlockStatus, Image> blockAssetMap;
    @Value("${blockWidth}")
    private int blockWidth;
    @Value("${blockHeight}")
    private int blockHeight;

    @Autowired
    private MinesweeperManager gameManager;
    @Autowired
    private LeaderboardManager leaderboardManager;

    @Value("${version}")
    private String version;
    @Value("${acknowledgement}")
    private String acknowledgement;
    @Value("${copyright}")
    private String copyright;

    @Value("${minUsernameLength}")
    private Integer minUsernameLength;
    @Value("${defaultUsername}")
    private String defaultUsername;

    @Value("${elapseTimeUpdateInterval}")
    private Integer elapseTimeUpdateInterval;

    @Autowired
    private ObjectFactory<LeaderboardStage> leaderboardStageFactory;

    // *****************************************************************************************************************
    // game states
    // *****************************************************************************************************************

    private Property<GameStatus> currentGameStatus;
    private Property<HardnessLevel> currentHardnessLevel;
    private IntegerProperty currentMineRemaining;
    private LongProperty currentTimeElapse;
    private BooleanProperty currentPausing;
    private BooleanProperty currentInDialogFlow;
    private BooleanProperty currentTimelinePlaying;
    private Property<BlockStatus>[][] boardBlockStatus;

    // *****************************************************************************************************************
    // initialization
    // *****************************************************************************************************************

    @FXML
    public void initialize() {
        logger.info("Copyright : {}", copyright);
        logger.info("Acknowledgement : {}", acknowledgement);
        logger.info("Version : {}", version);
        initStatus();
        initRemainingMine();
        initTimeElapsed();
        initGameButton();
        initGameMenu();
        initHardnessMenu();
        initLeaderboardMenu();
        initAcknowledgement();
        initPausingLabel();
        initGameBoard();
        initGameBinding();
    }

    private void initStatus() {
        currentGameStatus = new SimpleObjectProperty<>(null);
        currentHardnessLevel = new SimpleObjectProperty<>(null);
        currentMineRemaining = new SimpleIntegerProperty(999);
        currentTimeElapse = new SimpleLongProperty(0);
        currentPausing = new SimpleBooleanProperty(false);
        currentInDialogFlow = new SimpleBooleanProperty(false);
        currentTimelinePlaying = new SimpleBooleanProperty(false);
    }

    private void initRemainingMine() {
        logger.debug("Initializing remainingMines");
        if (ledFont != null) {
            remainingMines.setFont(ledFont);
        }
        remainingMines.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString(currentMineRemaining.getValue()), currentMineRemaining));
    }

    private void initTimeElapsed() {
        logger.debug("Initializing timeElapsed");
        if (ledFont != null) {
            timeElapsed.setFont(ledFont);
        }
        timeElapsedTimeLine = new Timeline(new KeyFrame(Duration.millis(elapseTimeUpdateInterval), (actionEvent) -> currentTimeElapse.setValue(currentTimeElapse.getValue() + timeElapsedTimeLine.getCurrentTime().toMillis())));
        timeElapsedTimeLine.setAutoReverse(false);
        timeElapsedTimeLine.setCycleCount(Animation.INDEFINITE);
        timeElapsed.textProperty().bind(Bindings.createStringBinding(() -> Long.toString(currentTimeElapse.getValue() / 1000), currentTimeElapse));
    }

    private void initGameButton() {
        logger.debug("Initializing game button");
        ImageView gameButtonImage = new ImageView();
        gameButtonImage.imageProperty().bind(Bindings
                .when(gameButton.pressedProperty())
                .then(Bindings.valueAt(buttonPressedMap, currentGameStatus))
                .otherwise(Bindings.valueAt(buttonRaisedMap, currentGameStatus)));
        gameButton.setGraphic(gameButtonImage);
        gameButton.setOnAction(this::gameButtonPressed);
    }

    private void initGameMenu() {
        logger.debug("Initializing game menu");
        menuNewGame.setOnAction(this::newGameMenuPressed);
        menuLoadGame.setOnAction(this::loadGameMenuPressed);
        menuSaveGame.setOnAction(this::saveGameMenuPressed);
        menuPause.setOnAction(this::pausingGameMenuPressed);
        menuPause.textProperty().bind(Bindings.when(currentPausing).then("Resume").otherwise("Pause"));
    }

    private void initHardnessMenu() {
        logger.debug("Initializing hardness menu");
        menuHardnessEasy.setUserData(HardnessLevel.EASY);
        menuHardnessMedium.setUserData(HardnessLevel.MEDIUM);
        menuHardnessHard.setUserData(HardnessLevel.HARD);
        menuHardnessCustom.setUserData(null);   // data will be set later
        // by default use EASY
        hardnessSelector.selectToggle(menuHardnessEasy);
        currentHardnessLevel.setValue(HardnessLevel.EASY);
        hardnessSelector.selectedToggleProperty().addListener(this::hardnessSelectedChanged);
    }

    private void initLeaderboardMenu() {
        logger.debug("Initializing leaderboard menu");
        menuLeaderboard.setOnAction(this::leaderboardMenuPressed);
    }

    private void initAcknowledgement() {
        logger.debug("Initializing acknowledgement");
        aboutLabel.setText(copyright + " - " + acknowledgement);
    }

    private void initGameBoard() {
        logger.debug("Initializing game board");
        gameBoard.visibleProperty().bind(Bindings.not(currentPausing));
    }

    private void initPausingLabel() {
        logger.debug("Initializing pausing label");
        if (ledFont != null) {
            pausingLabel.setFont(ledFont);
        }
        pausingLabel.visibleProperty().bind(currentPausing);
        pausingLabel.setOnMouseClicked(this::pausingLabelPressed);
    }

    private void initGameBinding() {
        logger.debug("Initializing game bindings");
        gameManager.setGameEventListener(new MinesweeperEventListener());
        currentTimeElapse.addListener(((observable, oldValue, newValue) -> {
            if (gameManager.isGameNotInitialized()) {
                return;
            }
            gameManager.setTimeElapsed(newValue.longValue());
        }));
        currentGameStatus.addListener(((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                logger.debug("Game state changed to " + newValue);
            }
        }));
        currentTimelinePlaying.addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                logger.debug("TimeElapse play");
                timeElapsedTimeLine.play();
            } else {
                logger.debug("TimeElapse pause");
                timeElapsedTimeLine.pause();
            }
        }));
        currentInDialogFlow.addListener(((observable, oldValue, newValue) -> {
            gameManager.setPausing(newValue);
        }));
        currentPausing.bind(Bindings.createBooleanBinding(() -> gameManager.isGamePausing(currentGameStatus.getValue()), currentGameStatus));
        currentTimelinePlaying.bind(Bindings.createBooleanBinding(() -> currentGameStatus.getValue() != null && !gameManager.isGameFinished(currentGameStatus.getValue()) && !gameManager.isGamePausing(currentGameStatus.getValue()) && !currentInDialogFlow.getValue(), currentGameStatus, currentInDialogFlow));
    }

    public void initGame() {
        logger.debug("Initializing game");
        gameManager.newGame(currentHardnessLevel.getValue());
    }

    private void createGameBoard() {
        logger.debug("Creating game board");
        // clear previous gameBoard
        gameBoard.getChildren().clear();
        if (boardBlockStatus != null) {
            for (int rowIndex = 0; rowIndex < boardBlockStatus.length; rowIndex++) {
                for (int colIndex = 0; colIndex < boardBlockStatus[rowIndex].length; colIndex++) {
                    boardBlockStatus[rowIndex][colIndex].unbind();
                    boardBlockStatus[rowIndex][colIndex] = null;
                }
            }
        }
        // construct a new game board
        HardnessLevel level = gameManager.getGameHardnessLevel();
        boardBlockStatus = new Property[level.getBoardHeight()][level.getBoardWidth()];
        for (int rowIndex = 0; rowIndex < level.getBoardHeight(); rowIndex++) {
            for (int colIndex = 0; colIndex < level.getBoardWidth(); colIndex++) {
                final int finalRowIndex = rowIndex;
                final int finalColIndex = colIndex;
                Property<BlockStatus> blockStatus = new SimpleObjectProperty<>();
                ImageView blockView = new ImageView();
                blockView.imageProperty().bind(Bindings.valueAt(blockAssetMap, blockStatus));
                blockView.setOnMouseClicked(event -> blockClicked(event, finalRowIndex, finalColIndex));
                boardBlockStatus[rowIndex][colIndex] = blockStatus;
                gameBoard.add(blockView, colIndex, rowIndex);
            }
        }
        int gameBoardWidth = blockWidth * level.getBoardWidth();
        int gameBoardHeight = blockHeight * level.getBoardHeight();
        gameBoard.setPrefSize(gameBoardWidth, gameBoardHeight);
        Stage stage = (Stage) gameBoard.getScene().getWindow();
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    // *****************************************************************************************************************
    // Game handler
    // *****************************************************************************************************************

    public void onGameClose(WindowEvent windowEvent) {
        logger.info("Game closing");
        leaderboardManager.saveLeaderboard();
        if (!preventOverwriteCurrentDialogFlow()) {
            windowEvent.consume();   // canceled save
        }
    }

    private void gameButtonPressed(ActionEvent actionEvent) {
        if (gameManager.isGameNotInitialized()) {
            logger.debug("Ignored game button press : game not initialized");
            return;
        }
        // if playing, then pressing button means surrender
        // if game finished, then pressing button means start new game
        if (gameManager.isGameFinished(currentGameStatus.getValue())) {
            if (preventOverwriteCurrentDialogFlow()) {
                gameManager.newGame(currentHardnessLevel.getValue());
            }
        } else {
            logger.debug("User surrendered");
            gameManager.surrenderGame();
        }
    }

    private void newGameMenuPressed(ActionEvent actionEvent) {
        if (preventOverwriteCurrentDialogFlow()) {
            gameManager.newGame(currentHardnessLevel.getValue());
        }
    }

    private void loadGameMenuPressed(ActionEvent actionEvent) {
        loadGameDialogFlow();
    }

    private void saveGameMenuPressed(ActionEvent actionEvent) {
        if (gameManager.isGameNotInitialized()) {
            logger.debug("Ignored save game menu button press : game not initialized");
            return;
        }
        saveGameDialogFlow();
    }

    private void pausingGameMenuPressed(ActionEvent actionEvent) {
        if (currentInDialogFlow.getValue()) {
            return;
        }
        gameManager.setPausing(!gameManager.isGamePausing(currentGameStatus.getValue()));
    }

    private void hardnessSelectedChanged(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (newValue == menuHardnessCustom) {
            HardnessLevel customHardness = customHardnessDialogFlow();
            if (customHardness != null) {
                logger.debug("Custom hardness is set to {}", customHardness);
                menuHardnessCustom.setUserData(customHardness);
            } else {
                menuHardnessCustom.setUserData(currentHardnessLevel.getValue());
                return;
            }
        }
        currentHardnessLevel.setValue((HardnessLevel) newValue.getUserData());
        if (preventOverwriteCurrentDialogFlow()) {
            logger.debug("Creating new game with current hardness");
            gameManager.newGame(currentHardnessLevel.getValue());
        }
    }

    private void leaderboardMenuPressed(ActionEvent actionEvent) {
        boolean alreadyInDialogFlow = currentInDialogFlow.getValue();
        try {
            currentInDialogFlow.setValue(true);
            logger.debug("Showing leaderboard");
            LeaderboardStage leaderboardStage = leaderboardStageFactory.getObject();
            leaderboardStage.initOwner(gameButton.getScene().getWindow());
            leaderboardStage.showAndWait();
            logger.debug("Leaderboard close");
        } finally {
            if (!alreadyInDialogFlow) {
                currentInDialogFlow.setValue(false);
            }
        }
    }

    private void pausingLabelPressed(MouseEvent mouseEvent) {
        if (gameManager.isGameNotInitialized()) {
            logger.debug("Ignored pause game menu button press : game not initialized");
            return;
        }
        if (currentInDialogFlow.getValue()) {
            return;
        }
        gameManager.setPausing(!gameManager.isGamePausing(currentGameStatus.getValue()));
    }

    private void blockClicked(MouseEvent mouseEvent, int rowIndex, int colIndex) {
        if (gameManager.isGameNotInitialized() || gameManager.isGameFinished(currentGameStatus.getValue())) {
            logger.debug("Ignored click event : game not initialized or finished");
            return;
        }
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            if (mouseEvent.getClickCount() >= 2) {
                logger.debug("Left double click on ({}, {})", rowIndex, colIndex);
                gameManager.leftDoubleClickOnBlock(rowIndex, colIndex);
            } else {
                logger.debug("Left click on ({}, {})", rowIndex, colIndex);
                gameManager.leftClickOnBlock(rowIndex, colIndex);
            }
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            logger.debug("Right click on ({}, {})", rowIndex, colIndex);
            gameManager.rightClickOnBlock(rowIndex, colIndex);
        }
    }

    private class MinesweeperEventListener implements GameEventListener {

        @Override
        public void onGameStart() {
            currentTimeElapse.setValue(gameManager.getTimeElapsed());
            currentHardnessLevel.setValue(gameManager.getGameHardnessLevel());
            timeElapsedTimeLine.jumpTo(Duration.millis(gameManager.getTimeElapsed() % elapseTimeUpdateInterval));
            createGameBoard();
        }

        @Override
        public void onGameWon() {
            logger.info("Game Won");
            LocalDateTime now = LocalDateTime.now();
            String username = askWinnerNameDialogFlow();
            leaderboardManager.createNewRecord(username, gameManager.getGame(), now);
            LeaderboardStage leaderboardStage = leaderboardStageFactory.getObject();
            leaderboardStage.initOwner(gameButton.getScene().getWindow());
            leaderboardStage.showAndWait();
        }

        @Override
        public void onGameLose() {
            logger.info("Game Lose");
        }

        @Override
        public void onGameChanged(GameStatus newStatus) {
            currentGameStatus.setValue(newStatus);
            currentMineRemaining.setValue(gameManager.getRemainingMine());
            boolean gameFinished = gameManager.isGameFinished(currentGameStatus.getValue());
            for (int rowIndex = 0; rowIndex < boardBlockStatus.length; rowIndex++) {
                for (int colIndex = 0; colIndex < boardBlockStatus[rowIndex].length; colIndex++) {
                    boardBlockStatus[rowIndex][colIndex].setValue(gameManager.getBlockStatus(rowIndex, colIndex, gameFinished));
                }
            }
        }
    }

    private boolean preventOverwriteCurrentDialogFlow() {
        boolean alreadyInDialogFlow = currentInDialogFlow.getValue();
        try {
            currentInDialogFlow.setValue(true);
            if (gameManager.shouldSaveGame()) {
                // confirm to save
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                ButtonType save = new ButtonType("Save");
                ButtonType notSave = new ButtonType("Don't save");
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(save, notSave, cancel);
                alert.setTitle("Save current game");
                alert.setHeaderText("Do you want to save current game progress?");
                alert.setContentText("Creating a new game will overwrite current game progress.");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty()) {
                    // user closed dialog, default to cancel action
                    logger.debug("Canceled prevent overwrite");
                    return false;
                } else if (result.get().equals(save)) {
                    logger.debug("Saving game session");
                    return saveGameDialogFlow();
                } else if (result.get().equals(notSave)) {
                    logger.debug("Skip saving game session");
                    return true;
                } else {
                    logger.debug("Canceled prevent overwrite");
                    return false;
                }
            } else {
                return true;
            }
        } finally {
            if (!alreadyInDialogFlow) {
                currentInDialogFlow.setValue(false);
            }
        }
    }

    private boolean saveGameDialogFlow() {
        boolean alreadyInDialogFlow = currentInDialogFlow.getValue();
        try {
            currentInDialogFlow.setValue(true);
            File gameFile;
            if (gameManager.hasSaveLocation()) {
                gameFile = gameManager.getCurrentGameSaveLocation();
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save game file");
                fileChooser.setInitialFileName("minesweeper.json");
                gameFile = fileChooser.showSaveDialog(gameButton.getScene().getWindow());
                if (gameFile == null) {
                    logger.debug("Save game canceled");
                    return false;
                }
            }
            if (gameManager.saveGame(gameFile)) {
                return true;
            } else {
                logger.error("Cannot save game to file {}", gameFile);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to save game");
                alert.setHeaderText("Failed to save game.");
                alert.setContentText("Cannot save game to file \"" + gameFile.getAbsolutePath() + "\"");
                alert.showAndWait();
                return false;
            }
        } finally {
            if (!alreadyInDialogFlow) {
                currentInDialogFlow.setValue(false);
            }
        }
    }

    private boolean loadGameDialogFlow() {
        boolean alreadyInDialogFlow = currentInDialogFlow.getValue();
        try {
            currentInDialogFlow.setValue(true);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load game file");
            FileChooser.ExtensionFilter jsonFileFilter = new FileChooser.ExtensionFilter("minesweeper save file", "*.json");
            fileChooser.setSelectedExtensionFilter(jsonFileFilter);
            File gameFile = fileChooser.showOpenDialog(gameButton.getScene().getWindow());
            if (gameFile == null) {
                logger.debug("Load game canceled");
                return false;
            }
            if (gameManager.loadGame(gameFile)) {
                return true;
            } else {
                logger.error("Cannot load game from file {}", gameFile);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed to load game");
                alert.setHeaderText("Failed to load game.");
                alert.setContentText("Cannot load game from file \"" + gameFile.getAbsolutePath() + "\"");
                alert.showAndWait();
                return false;
            }
        } finally {
            if (!alreadyInDialogFlow) {
                currentInDialogFlow.setValue(false);
            }
        }
    }

    private HardnessLevel customHardnessDialogFlow() {
        boolean alreadyInDialogFlow = currentInDialogFlow.getValue();
        try {
            currentInDialogFlow.setValue(true);
            Dialog<HardnessLevel> dialog = new Dialog<>();
            dialog.setTitle("Custom Minesweeper Hardness");
            dialog.setHeaderText("Create a custom hardness with the following parameters.");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            HardnessLevel currentLevel = currentHardnessLevel.getValue();
            Spinner<Integer> numRow = new Spinner<>(1, 50, currentLevel.getBoardHeight());
            numRow.setEditable(true);
            numRow.setPromptText("Board Height");
            Spinner<Integer> numCol = new Spinner<>(1, 50, currentLevel.getBoardWidth());
            numCol.setEditable(true);
            numCol.setPromptText("Board Width");
            Spinner<Integer> numMines = new Spinner<>(0, 2500, currentLevel.getNumMines());
            numMines.setEditable(true);
            numMines.setPromptText("Number of mines");
            Label numRowLabel = new Label("Game Board Height");
            Label numColLabel = new Label("Game Board Width");
            Label numMineLabel = new Label("Number of mines");
            VBox layoutBox = new VBox(10, numColLabel, numCol, numRowLabel, numRow, numMineLabel, numMines);
            dialogPane.setContent(layoutBox);
            dialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.OK) {
                    if (numRow.getValue() != null && numCol.getValue() != null && numMines.getValue() != null) {
                        return new HardnessLevel(numCol.getValue(), numRow.getValue(), numMines.getValue());
                    } else {
                        return null;
                    }
                }
                return null;
            });
            dialog.setOnCloseRequest(event -> {
                if (numRow.getValue() == null || numCol.getValue() == null || numMines.getValue() == null) {
                    logger.error("Failed to create custom hardness");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed to create custom hardness");
                    alert.setHeaderText("Failed to create custom hardness.");
                    alert.setContentText("Could not parse value entered.");
                    alert.showAndWait();
                    event.consume();
                }
            });
            Platform.runLater(numMines::requestFocus);
            Optional<HardnessLevel> customHardness = dialog.showAndWait();
            if (customHardness.isPresent()) {
                return customHardness.get();
            } else {
                logger.info("Custom hardness select canceled");
                return null;
            }
        } finally {
            if (!alreadyInDialogFlow) {
                currentInDialogFlow.setValue(false);
            }
        }
    }

    private String askWinnerNameDialogFlow() {
        boolean alreadyInDialogFlow = currentInDialogFlow.getValue();
        try {
            currentInDialogFlow.setValue(true);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Winner, may I ask your name?");
            dialog.setHeaderText("Congratulations! You have won the game Minesweeper!\nPlease provide a username to put on leaderboard!");
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.getButtonTypes().addAll(ButtonType.OK);
            TextField username = new TextField();
            username.setPromptText("Username");
            username.setText(defaultUsername);
            Label usernameLabel = new Label("Username");
            VBox layoutBox = new VBox(10, usernameLabel, username);
            dialogPane.setContent(layoutBox);
            dialog.setResultConverter((ButtonType button) -> {
                if (button == ButtonType.OK) {
                    if (username.getCharacters().length() >= minUsernameLength) {
                        return username.getCharacters().toString();
                    }
                }
                return null;
            });
            dialog.setOnCloseRequest(event -> {
                if (username.getCharacters().length() < minUsernameLength) {
                    logger.error("Username too short");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Username too short");
                    alert.setHeaderText("Username too short.");
                    alert.setContentText("Provide a username longer than " + minUsernameLength + " characters.");
                    alert.showAndWait();
                    event.consume();
                }
            });
            Platform.runLater(username::requestFocus);
            Optional<String> winnerUsername = dialog.showAndWait();
            if (winnerUsername.isPresent()) {
                return winnerUsername.get();
            } else {
                logger.info("Username prompt canceled, using default");
                return defaultUsername;
            }
        } finally {
            if (!alreadyInDialogFlow) {
                currentInDialogFlow.setValue(false);
            }
        }
    }
}