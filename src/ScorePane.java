import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Coen Boelhouwers
 */
public class ScorePane extends Pane {

	private static final int MAX_NAME_LENGTH = 15;

	private long lastNanos = -1;
	private Font monoFont = Font.font("monospaced", 26);
	private VBox scoreBox;

	public ScorePane(String title) {
		Label scoreLabel = new Label(title);
		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.setLayoutY(0);
		scoreLabel.setFont(Font.font("Roboto", 30));
		scoreLabel.setBackground(new Background(new BackgroundFill(Color.web("#EEBA47"), null, null)));
		scoreLabel.setAlignment(Pos.TOP_CENTER);
		scoreLabel.prefWidthProperty().bind(widthProperty());

		Rectangle scoreBoxClip = new Rectangle(100, 100);
		scoreBoxClip.setLayoutY(1);// Glitch fix
		scoreBoxClip.heightProperty().bind(heightProperty());
		scoreBoxClip.widthProperty().bind(widthProperty());

		scoreBox = new VBox();
		scoreBox.setSpacing(10);

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (lastNanos < 0) lastNanos = now;
				double elapsedTime = (now - lastNanos) / 1_000_000.0 / 1_000.0;
				lastNanos = now;

				if (scoreBox.getChildren().isEmpty()) {
					scoreBox.setLayoutY(getHeight());
				} else {
					scoreBox.setLayoutY(scoreBox.getLayoutY() - 60 * elapsedTime);
					if (scoreBox.getLayoutY() < -scoreBox.getHeight() + scoreLabel.getHeight())
						scoreBox.setLayoutY(getHeight());
				}
			}
		}.start();

		prefWidthProperty().bind(Bindings.max(scoreLabel.widthProperty(), scoreBox.widthProperty()));
		prefHeightProperty().bind(scoreLabel.heightProperty().add(scoreBox.heightProperty()));
		setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0.4),
				null, null)));
		setClip(scoreBoxClip);
		getChildren().addAll(scoreBox, scoreLabel);
	}

	public void addScore(Score score) {
		Platform.runLater(() -> {
			int i = scoreBox.getChildren().size() + 1;
			Text textView = new Text(String.format("%2d %-" + MAX_NAME_LENGTH + "s: %3d", i,
					score.getName().length() < MAX_NAME_LENGTH ? score.getName() :
							score.getName().substring(0, MAX_NAME_LENGTH), score.getScore()));
			textView.setFont(monoFont);
			scoreBox.getChildren().add(textView);
		});
	}

	public void clearScores() {
		Platform.runLater(() -> scoreBox.getChildren().clear());
	}
}
