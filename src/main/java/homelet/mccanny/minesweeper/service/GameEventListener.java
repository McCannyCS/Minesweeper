package homelet.mccanny.minesweeper.service;

import homelet.mccanny.minesweeper.model.game.GameStatus;

public interface GameEventListener {

    void onGameStart();

    void onGameWon();

    void onGameLose();

    void onGameChanged(GameStatus newStatus);
}
