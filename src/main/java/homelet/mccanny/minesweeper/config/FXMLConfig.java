package homelet.mccanny.minesweeper.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

@Configuration
public class FXMLConfig {

    private static final Logger logger = LogManager.getLogger();

    @Value("${fxml.main}")
    private ClassPathResource mainView;

    @Value("${fxml.leaderboard}")
    private ClassPathResource leaderboardView;

    @Bean
    public URL mainView() {
        try {
            return mainView.getURL();
        } catch (IOException e) {
            logger.error(e);
            logger.info("Failed to load main view fxml");
            return null;
        }
    }

    @Bean
    @Primary
    public URL leaderboardView() {
        try {
            return leaderboardView.getURL();
        } catch (IOException e) {
            logger.error(e);
            logger.info("Failed to load leaderboard view fxml");
            return null;
        }
    }
}
