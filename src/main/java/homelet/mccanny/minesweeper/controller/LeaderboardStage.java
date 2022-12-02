package homelet.mccanny.minesweeper.controller;

import homelet.mccanny.minesweeper.model.game.HardnessLevel;
import homelet.mccanny.minesweeper.model.leaderboard.LeaderboardRecord;
import homelet.mccanny.minesweeper.service.LeaderboardManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@Scope("prototype")
public class LeaderboardStage extends Stage {
	
	private static final Logger logger = LogManager.getLogger();
	
	@FXML
	public ToggleGroup hardnessFilter;
	@FXML
	public RadioButton hardnessAll;
	@FXML
	public RadioButton hardnessEasy;
	@FXML
	public RadioButton hardnessMedium;
	@FXML
	public RadioButton hardnessHard;
	@FXML
	public RadioButton hardnessCustom;
	
	@FXML
	public TableView<LeaderboardRecord> recordTable;
	@FXML
	public TableColumn<LeaderboardRecord, String> rankCol;
	@FXML
	public TableColumn<LeaderboardRecord, String> usernameCol;
	@FXML
	public TableColumn<LeaderboardRecord, String> hardnessCol;
	@FXML
	public TableColumn<LeaderboardRecord, String> recordTimeCol;
	@FXML
	public TableColumn<LeaderboardRecord, String> acquireDateCol;
	
	private ObservableList<LeaderboardRecord> records;
	
	@Autowired
	private LeaderboardManager leaderboardManager;
	
	public LeaderboardStage() {
		super();
		initModality(Modality.WINDOW_MODAL);
		setTitle("Leaderboard");
	}
	
	@Autowired
	private void setScene(URL leaderboardView, Image gameIcon) throws IOException {
		FXMLLoader loader = new FXMLLoader(leaderboardView);
		loader.setController(this);
		Scene scene = loader.load();
		setScene(scene);
		getIcons().add(gameIcon);
	}
	
	@FXML
	public void initialize() {
		initState();
		initLeaderboardTable();
		initHardnessFilter();
	}
	
	private void initState() {
		logger.debug("Initializing leaderboard states");
		records = FXCollections.observableArrayList();
	}
	
	private void initLeaderboardTable() {
		logger.debug("Initializing leaderboard table");
		recordTable.setItems(records);
		rankCol.setCellValueFactory(ef -> new SimpleStringProperty(Integer.toString(records.indexOf(ef.getValue()) + 1)));
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		hardnessCol.setCellValueFactory(ef -> new SimpleStringProperty(ef.getValue().getRecordHardness().toString()));
		recordTimeCol.setCellValueFactory(ef -> new SimpleStringProperty(leaderboardManager.getRecordTimeFormatter().format(ef.getValue().getRecordTime() / 1000.0)));
		acquireDateCol.setCellValueFactory(ef -> new SimpleStringProperty(leaderboardManager.getAcquiredDateTimeFormatter().format(ef.getValue().getAcquiredDate())));
	}
	
	private void initHardnessFilter() {
		logger.debug("Initializing leaderboard hardness filter");
		hardnessEasy.setUserData(HardnessLevel.EASY);
		hardnessMedium.setUserData(HardnessLevel.MEDIUM);
		hardnessHard.setUserData(HardnessLevel.HARD);
		hardnessAll.setUserData(null);
		hardnessCustom.setUserData(null);
		hardnessFilter.selectedToggleProperty().addListener(this::onHardnessFilterChange);
		hardnessFilter.selectToggle(hardnessAll);
	}
	
	private void onHardnessFilterChange(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
		if (newValue == hardnessAll) {
			records.setAll(leaderboardManager.getAllRecords());
		} else if (newValue == hardnessCustom) {
			records.setAll(leaderboardManager.getCustomHardnessRecords());
		} else {
			records.setAll(leaderboardManager.getRecordsOfHardness((HardnessLevel) newValue.getUserData()));
		}
	}
}
