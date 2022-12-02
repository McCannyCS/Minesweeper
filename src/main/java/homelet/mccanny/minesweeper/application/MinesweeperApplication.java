package homelet.mccanny.minesweeper.application;

import homelet.mccanny.minesweeper.controller.MinesweeperController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URL;

public class MinesweeperApplication extends Application {
	
	@Override
	public void start(Stage stage) throws IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
		URL mainView = (URL) context.getBean("mainView");
		Image gameIcon = (Image) context.getBean("gameIcon");
		FXMLLoader fxmlLoader = new FXMLLoader(mainView);
		fxmlLoader.setControllerFactory(context::getBean);
		Scene scene = new Scene(fxmlLoader.load());
		MinesweeperController controller = fxmlLoader.getController();
		stage.setOnCloseRequest(controller::onGameClose);
		stage.getIcons().add(gameIcon);
		stage.setResizable(false);
		stage.setTitle("Minesweeper");
		stage.setScene(scene);
		Platform.runLater(controller::initGame);
		stage.show();
	}
}
