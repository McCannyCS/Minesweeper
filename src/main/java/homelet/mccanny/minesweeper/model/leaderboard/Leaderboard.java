package homelet.mccanny.minesweeper.model.leaderboard;

import homelet.mccanny.minesweeper.model.game.HardnessLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Leaderboard {

    private final TreeSet<LeaderboardRecord> leaderboardRecords;

    public Leaderboard(List<LeaderboardRecord> records) {
        this.leaderboardRecords = new TreeSet<>(records);
    }

    public List<LeaderboardRecord> getRecordsOfHardness(HardnessLevel hardness) {
        ArrayList<LeaderboardRecord> records = new ArrayList<>();
        for (LeaderboardRecord record : leaderboardRecords) {
            if (record.getRecordHardness().equals(hardness)) {
                records.add(record);
            }
        }
        return records;
    }

    public boolean addRecord(LeaderboardRecord record) {
        return leaderboardRecords.add(record);
    }

    public ArrayList<LeaderboardRecord> getAllRecords() {
        return new ArrayList<>(leaderboardRecords);
    }
}
