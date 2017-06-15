import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * @author Coen Boelhouwers
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Regular.ttf"), 100);
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Thin.ttf"), 100);

		primaryStage.setScene(LoginPane.newScene(item -> {
			primaryStage.setScene(item.getGameScene());
			primaryStage.setFullScreen(true);
		}));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
