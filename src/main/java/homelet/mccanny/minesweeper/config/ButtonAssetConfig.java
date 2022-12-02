package homelet.mccanny.minesweeper.config;

import homelet.mccanny.minesweeper.model.game.GameStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ButtonAssetConfig {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Value("${button.pressed.clear}")
	private ClassPathResource pressedWin;
	@Value("${button.pressed.dead}")
	private ClassPathResource pressedLose;
	@Value("${button.pressed.normal}")
	private ClassPathResource pressedNormal;
	@Value("${button.raised.clear}")
	private ClassPathResource raisedWin;
	@Value("${button.raised.dead}")
	private ClassPathResource raisedLose;
	@Value("${button.raised.normal}")
	private ClassPathResource raisedNormal;
	
	@Bean
	public ObservableMap<GameStatus, Image> buttonPressedMap() {
		try {
			Map<GameStatus, Image> buttonPressedMap = new HashMap<>(GameStatus.values().length);
			buttonPressedMap.put(GameStatus.WIN, new Image(pressedWin.getInputStream()));
			buttonPressedMap.put(GameStatus.LOSE, new Image(pressedLose.getInputStream()));
			Image normal = new Image(pressedNormal.getInputStream());
			buttonPressedMap.put(GameStatus.PAUSING, normal);
			buttonPressedMap.put(GameStatus.PLAYING, normal);
			return FXCollections.observableMap(buttonPressedMap);
		} catch (IOException e) {
			logger.error(e);
			logger.info("Failed to load button pressed asset map");
			return null;
		}
	}
	
	@Bean
	public ObservableMap<GameStatus, Image> buttonRaisedMap() {
		try {
			Map<GameStatus, Image> buttonRaisedMap = new HashMap<>(GameStatus.values().length);
			buttonRaisedMap.put(GameStatus.WIN, new Image(raisedWin.getInputStream()));
			buttonRaisedMap.put(GameStatus.LOSE, new Image(raisedLose.getInputStream()));
			Image normal = new Image(raisedNormal.getInputStream());
			buttonRaisedMap.put(GameStatus.PAUSING, normal);
			buttonRaisedMap.put(GameStatus.PLAYING, normal);
			return FXCollections.observableMap(buttonRaisedMap);
		} catch (IOException e) {
			logger.error(e);
			logger.info("Failed to load button raise asset map");
			return null;
		}
	}
}
