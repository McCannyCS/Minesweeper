package homelet.mccanny.minesweeper.config;

import homelet.mccanny.minesweeper.model.game.BlockStatus;
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
public class BlockAssetConfig {

    private static final Logger logger = LogManager.getLogger();

    @Value("${block.m.0}")
    private ClassPathResource M_0;
    @Value("${block.m.1}")
    private ClassPathResource M_1;
    @Value("${block.m.2}")
    private ClassPathResource M_2;
    @Value("${block.m.3}")
    private ClassPathResource M_3;
    @Value("${block.m.4}")
    private ClassPathResource M_4;
    @Value("${block.m.5}")
    private ClassPathResource M_5;
    @Value("${block.m.6}")
    private ClassPathResource M_6;
    @Value("${block.m.7}")
    private ClassPathResource M_7;
    @Value("${block.m.8}")
    private ClassPathResource M_8;
    @Value("${block.flag}")
    private ClassPathResource flaggedBlock;
    @Value("${block.hidden}")
    private ClassPathResource hiddenBlock;
    @Value("${block.mine.correct}")
    private ClassPathResource mineBlockCorrect;
    @Value("${block.mine.wrong}")
    private ClassPathResource mineBlockWrong;

    @Bean
    public ObservableMap<BlockStatus, Image> blockAssetMap() {
        try {
            Map<BlockStatus, Image> statusImageMap = new HashMap<>(BlockStatus.values().length);
            statusImageMap.put(BlockStatus.M_0, new Image(M_0.getInputStream()));
            statusImageMap.put(BlockStatus.M_1, new Image(M_1.getInputStream()));
            statusImageMap.put(BlockStatus.M_2, new Image(M_2.getInputStream()));
            statusImageMap.put(BlockStatus.M_3, new Image(M_3.getInputStream()));
            statusImageMap.put(BlockStatus.M_4, new Image(M_4.getInputStream()));
            statusImageMap.put(BlockStatus.M_5, new Image(M_5.getInputStream()));
            statusImageMap.put(BlockStatus.M_6, new Image(M_6.getInputStream()));
            statusImageMap.put(BlockStatus.M_7, new Image(M_7.getInputStream()));
            statusImageMap.put(BlockStatus.M_8, new Image(M_8.getInputStream()));
            statusImageMap.put(BlockStatus.FLAGGED, new Image(flaggedBlock.getInputStream()));
            statusImageMap.put(BlockStatus.HIDDEN, new Image(hiddenBlock.getInputStream()));
            statusImageMap.put(BlockStatus.MINE_CORRECT, new Image(mineBlockCorrect.getInputStream()));
            statusImageMap.put(BlockStatus.MINE_WRONG, new Image(mineBlockWrong.getInputStream()));
            return FXCollections.observableMap(statusImageMap);
        } catch (IOException e) {
            logger.error(e);
            logger.info("Failed to load mine asset map");
            return null;
        }
    }

}
