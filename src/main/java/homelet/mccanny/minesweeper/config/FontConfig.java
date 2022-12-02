package homelet.mccanny.minesweeper.config;

import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FontConfig {
	
	private static final Logger logger = LogManager.getLogger();
	
	@Value("${font.led}")
	private ClassPathResource ledFontFile;
	
	@Bean
	public Font ledFont() {
		try {
			return Font.loadFont(ledFontFile.getInputStream(), 40);
		} catch (IOException e) {
			logger.error(e);
			logger.info("Failed to load led font, using default ...");
			return null;
		}
	}
}
