package homelet.mccanny.minesweeper.config;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class IconConfig {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Value("${icon.game}")
	private Resource gameIcon;
	
	@Bean
	public Image gameIcon() {
		try {
			return new Image(gameIcon.getInputStream());
		} catch (IOException e) {
			logger.error(e);
			logger.info("Failed to load game icon");
			return null;
		}
	}
}
