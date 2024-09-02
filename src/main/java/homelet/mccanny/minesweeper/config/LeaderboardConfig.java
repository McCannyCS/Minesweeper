package homelet.mccanny.minesweeper.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

@Configuration
public class LeaderboardConfig {

    private static final Logger logger = LogManager.getLogger();

    @Value("${leaderboard.saveLocation}")
    private String leaderboardSaveLocation;

    @Bean
    public File leaderboardSaveFile() {
        try {
            String homeDir = System.getProperty("user.home");
            return Paths.get(homeDir, leaderboardSaveLocation).toFile();
        } catch (SecurityException e) {
            logger.error(e);
            logger.info("Could not locate leaderboard file");
            return null;
        }
    }

    @Bean
    @Qualifier("leaderboard")
    public DateTimeFormatter acquiredDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss a");
    }

    @Bean
    @Qualifier("leaderboard")
    public NumberFormat recordTimeFormatter() {
        NumberFormat df = DecimalFormat.getInstance();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.DOWN);
        return df;
    }
}
