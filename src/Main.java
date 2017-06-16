import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * @author Coen Boelhouwers
 */
public class Main extends Application implements LoginPane.OnAttractionSelectedListener,
		EventHandler<KeyEvent> {

	private Stage primaryStage;
	private MainScene mainScene;
	private EsstelingDatabase database;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Regular.ttf"), 100);
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Thin.ttf"), 100);
		Font.loadFont(getClass().getResourceAsStream("/Roboto-Medium.ttf"), 100);

		database = new EsstelingDatabase();

		mainScene = new MainScene();
		mainScene.changePane(new LoginPane(this));
		mainScene.setOnKeyPressed(this);
		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void onAttractionSelected(LoginPane.AttractionItem item) {
		mainScene.changePane(new AllScoresPane(item.getGamePane()));
		EsstelingDatabase.changeAttraction(item.getAttractionName());
		database.refreshNow();
	}

	@Override
	public void handle(KeyEvent event) {
		if (event.getCode() == KeyCode.F11) {
			primaryStage.setFullScreen(!primaryStage.isFullScreen());
			event.consume();
		} else if (event.getCode() == KeyCode.E) {
			database.refreshNow();
			mainScene.changePane(new EndOfDayPane());
			event.consume();
		} else if (event.getCode() == KeyCode.Q) {
			mainScene.changePane(new LoginPane(this));
			event.consume();
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		database.stop();
	}
}
