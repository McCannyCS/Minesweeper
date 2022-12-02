package homelet.mccanny.minesweeper.model.leaderboard;

import homelet.mccanny.minesweeper.model.game.HardnessLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LeaderboardRecord implements Comparable<LeaderboardRecord> {
	
	private final String username;
	private final HardnessLevel recordHardness;
	private final long recordTime;
	private final LocalDateTime acquiredDate;
	
	public LeaderboardRecord(String username, HardnessLevel recordHardness, long recordTime, LocalDateTime acquiredDate) {
		this.username = username;
		this.recordHardness = recordHardness;
		this.recordTime = recordTime;
		this.acquiredDate = acquiredDate;
	}
	
	@Override
	public String toString() {
		return "\"" + username + "\"" + " beat the game Minesweeper with hardness " +
				recordHardness + " in " + (recordTime / 1000.0) + " second at " +
				acquiredDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
	
	@Override
	public int compareTo(LeaderboardRecord o) {
		int compRes = Long.compare(recordTime, o.recordTime);
		if (compRes == 0) {
			compRes = acquiredDate.compareTo(o.acquiredDate);
		}
		if (compRes == 0) {
			compRes = username.compareTo(o.username);
		}
		return compRes;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		LeaderboardRecord record = (LeaderboardRecord) o;
		
		if (recordTime != record.recordTime) return false;
		if (!username.equals(record.username)) return false;
		if (!recordHardness.equals(record.recordHardness)) return false;
		return acquiredDate.equals(record.acquiredDate);
	}
	
	@Override
	public int hashCode() {
		int result = username.hashCode();
		result = 31 * result + recordHardness.hashCode();
		result = 31 * result + (int) (recordTime ^ (recordTime >>> 32));
		result = 31 * result + acquiredDate.hashCode();
		return result;
	}
	
	// *****************************************************************************************************************
	// getters
	// *****************************************************************************************************************
	
	public String getUsername() {
		return username;
	}
	
	public HardnessLevel getRecordHardness() {
		return recordHardness;
	}
	
	public long getRecordTime() {
		return recordTime;
	}
	
	public LocalDateTime getAcquiredDate() {
		return acquiredDate;
	}
}
