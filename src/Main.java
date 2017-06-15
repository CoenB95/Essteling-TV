import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * @author Coen Boelhouwers
 */
public class Main extends Application implements LoginPane.OnAttractionSelectedListener {

	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Regular.ttf"), 100);
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Thin.ttf"), 100);

		primaryStage.setScene(LoginPane.newScene(this));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void onAttractionSelected(LoginPane.AttractionItem item) {
		Scene scene = item.getGameScene();
		scene.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.F11)) {
				primaryStage.setFullScreen(!primaryStage.isFullScreen());
				event.consume();
			} else if (event.getCode().equals(KeyCode.Q)) {
				scene.setOnKeyPressed(null);
				primaryStage.setScene(LoginPane.newScene(this));
				event.consume();
			}
		});
		primaryStage.setScene(scene);
	}
}
