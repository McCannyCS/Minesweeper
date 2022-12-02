package homelet.mccanny.minesweeper.service;

import homelet.mccanny.minesweeper.model.game.HardnessLevel;
import homelet.mccanny.minesweeper.model.game.MinesweeperGame;
import homelet.mccanny.minesweeper.model.leaderboard.Leaderboard;
import homelet.mccanny.minesweeper.model.leaderboard.LeaderboardRecord;
import homelet.mccanny.minesweeper.repository.LeaderboardSerDer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class LeaderboardManager {
	
	private static final Logger logger = LogManager.getLogger();
	
	private Leaderboard leaderboard;
	
	@Autowired
	private LeaderboardSerDer leaderboardSerDer;
	
	@Autowired(required = false)
	private File leaderboardSaveFile;
	
	@Autowired
	@Qualifier("leaderboard")
	private DateTimeFormatter acquiredDateTimeFormatter;
	
	@Autowired
	@Qualifier("leaderboard")
	private NumberFormat recordTimeFormatter;
	
	@Autowired(required = false)
	private void loadLeaderboard(File leaderboardSaveFile) {
		if (leaderboardSaveFile == null) {
			logger.warn("Using virtual leaderboard");
		} else if (leaderboardSaveFile.exists()) {
			// load leaderboard file
			try (InputStream file = new BufferedInputStream(new FileInputStream(leaderboardSaveFile))) {
				JSONTokener tokener = new JSONTokener(file);
				leaderboard = leaderboardSerDer.readLeaderboardFromJson(new JSONObject(tokener));
				logger.info("Leaderboard loaded from {}", leaderboardSaveFile);
			} catch (IOException | JSONException e) {
				logger.error(e);
				logger.warn("Could not load leaderboard from {}, creating new leaderboard", leaderboardSaveFile);
			}
		} else {
			boolean mkdirResult = leaderboardSaveFile.getParentFile().mkdirs();
			logger.info("Creating new leaderboard at {}, (mkdirs={})", leaderboardSaveFile, mkdirResult);
		}
		if (leaderboard == null) {
			leaderboard = new Leaderboard(new ArrayList<>());
		}
	}
	
	public void saveLeaderboard() {
		if (leaderboardSaveFile != null) {
			try (FileWriter writer = new FileWriter(leaderboardSaveFile)) {
				JSONObject gameObject = leaderboardSerDer.saveLeaderboardToJson(leaderboard);
				writer.write(gameObject.toString(4));
				logger.info("Leaderboard saved at {}", leaderboardSaveFile);
			} catch (IOException e) {
				logger.error(e);
				logger.error("Could not save leaderboard file");
			} catch (JSONException e) {
				logger.error(e);
				logger.error("Failed to save leaderboard");
			}
		} else {
			logger.warn("Could not access leaderboard file, leaderboard could not be saved");
		}
	}
	
	public DateTimeFormatter getAcquiredDateTimeFormatter() {
		return acquiredDateTimeFormatter;
	}
	
	public NumberFormat getRecordTimeFormatter() {
		return recordTimeFormatter;
	}
	
	public void createNewRecord(String username, MinesweeperGame game, LocalDateTime dateTime) {
		LeaderboardRecord record = new LeaderboardRecord(username, game.getLevel(), game.getTimeElapsed(), dateTime);
		logger.info(record);
		if (leaderboard.addRecord(record)) {
			logger.info("Record added to leaderboard");
			saveLeaderboard();
		} else {
			logger.error("Could not add record to leaderboard");
		}
	}
	
	public List<LeaderboardRecord> getRecordsOfHardness(HardnessLevel level) {
		return leaderboard.getRecordsOfHardness(level);
	}
	
	public List<LeaderboardRecord> getCustomHardnessRecords() {
		ArrayList<LeaderboardRecord> records = new ArrayList<>(leaderboard.getAllRecords());
		records.removeAll(leaderboard.getRecordsOfHardness(HardnessLevel.EASY));
		records.removeAll(leaderboard.getRecordsOfHardness(HardnessLevel.MEDIUM));
		records.removeAll(leaderboard.getRecordsOfHardness(HardnessLevel.HARD));
		return records;
	}
	
	public List<LeaderboardRecord> getAllRecords() {
		return leaderboard.getAllRecords();
	}
}
