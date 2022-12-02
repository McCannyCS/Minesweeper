package homelet.mccanny.minesweeper.repository;

import homelet.mccanny.minesweeper.model.game.HardnessLevel;
import homelet.mccanny.minesweeper.model.leaderboard.Leaderboard;
import homelet.mccanny.minesweeper.model.leaderboard.LeaderboardRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LeaderboardSerDer {
	
	private static final String LEADERBOARD_KEY = "leaderboard";
	
	private static final String USERNAME_KEY = "username";
	private static final String RECORD_HARDNESS_KEY = "hardness";
	private static final String RECORD_TIME_KEY = "recordTime";
	private static final String ACQUIRED_DATE_KEY = "date";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	
	private static final String BOARD_WIDTH_KEY = "boardWidth";
	private static final String BOARD_HEIGHT_KEY = "boardHeight";
	private static final String NUM_MINE_KEY = "numMine";
	
	// *****************************************************************************************************************
	// Serializer
	// *****************************************************************************************************************
	
	public JSONObject saveLeaderboardToJson(Leaderboard leaderboard) {
		JSONObject gameObj = new JSONObject();
		gameObj.put(LEADERBOARD_KEY, saveLeaderboard(leaderboard.getAllRecords()));
		return gameObj;
	}
	
	private JSONArray saveLeaderboard(List<LeaderboardRecord> records) {
		JSONArray recordArr = new JSONArray();
		for (LeaderboardRecord record : records) {
			recordArr.put(saveLeaderboardRecord(record));
		}
		return recordArr;
	}
	
	private JSONObject saveLeaderboardRecord(LeaderboardRecord record) {
		JSONObject recordObj = new JSONObject();
		recordObj.put(USERNAME_KEY, record.getUsername());
		recordObj.put(RECORD_HARDNESS_KEY, saveHardnessLevel(record.getRecordHardness()));
		recordObj.put(RECORD_TIME_KEY, record.getRecordTime());
		recordObj.put(ACQUIRED_DATE_KEY, DATE_TIME_FORMATTER.format(record.getAcquiredDate()));
		return recordObj;
	}
	
	private JSONObject saveHardnessLevel(HardnessLevel level) {
		// TO DO remove duplicated code from GameSerDer
		JSONObject levelObj = new JSONObject();
		levelObj.put(BOARD_WIDTH_KEY, level.getBoardWidth());
		levelObj.put(BOARD_HEIGHT_KEY, level.getBoardHeight());
		levelObj.put(NUM_MINE_KEY, level.getNumMines());
		return levelObj;
	}
	
	// *****************************************************************************************************************
	// Deserializer
	// *****************************************************************************************************************
	
	public Leaderboard readLeaderboardFromJson(JSONObject gameObj) {
		ArrayList<LeaderboardRecord> records = readLeaderboard(gameObj.getJSONArray(LEADERBOARD_KEY));
		return new Leaderboard(records);
	}
	
	private ArrayList<LeaderboardRecord> readLeaderboard(JSONArray recordArr) {
		ArrayList<LeaderboardRecord> records = new ArrayList<>();
		for (int i = 0; i < recordArr.length(); i++) {
			records.add(readLeaderboardRecord(recordArr.getJSONObject(i)));
		}
		return records;
	}
	
	private LeaderboardRecord readLeaderboardRecord(JSONObject recordObj) {
		String username = recordObj.getString(USERNAME_KEY);
		HardnessLevel recordHardness = readHardnessLevel(recordObj.getJSONObject(RECORD_HARDNESS_KEY));
		long recordTime = recordObj.getLong(RECORD_TIME_KEY);
		LocalDateTime acquiredDate = LocalDateTime.parse(recordObj.getString(ACQUIRED_DATE_KEY), DATE_TIME_FORMATTER);
		return new LeaderboardRecord(username, recordHardness, recordTime, acquiredDate);
	}
	
	private HardnessLevel readHardnessLevel(JSONObject level) {
		// to do remove duplicated code from GameSerDer
		int boardWidth = level.getInt(BOARD_WIDTH_KEY);
		int boardHeight = level.getInt(BOARD_HEIGHT_KEY);
		int numMines = level.getInt(NUM_MINE_KEY);
		return new HardnessLevel(boardWidth, boardHeight, numMines);
	}
}
