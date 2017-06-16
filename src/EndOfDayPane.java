import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Coen Boelhouwers
 */
public class EndOfDayPane extends Pane implements EsstelingDatabase.DatabaseListener {

	private static final double UPDATE_INTERVAL = 30;//seconds
	private static final double QR_IMAGE_SIZE = 200;//pixels

	private long lastUpdate = -1;
	private Label topScoreLabel;
	//private ScorePane topScoresToday;

	public EndOfDayPane() {
		DynamicBackgroundPane backgroundImageView = new DynamicBackgroundPane();
		backgroundImageView.prefWidthProperty().bind(widthProperty());
		backgroundImageView.prefHeightProperty().bind(heightProperty());

		Label label2 = new Label("Wij wensen u nog een fijne dag en zien u graag terug in de Essteling");
		label2.setFont(Font.font("Roboto", 26));
		label2.setTextFill(Color.WHITE);
		label2.setWrapText(true);
		label2.layoutXProperty().bind(widthProperty().multiply(0.01));
		label2.layoutYProperty().bind(heightProperty().multiply(0.95).subtract(label2.heightProperty()));
		label2.maxWidthProperty().bind(widthProperty().multiply(0.48));

		Label label3 = new Label("The park is now closed. We wish you a nice day and hope to see you again soon" +
				" in the Essteling");
		label3.setFont(Font.font("Roboto", 26));
		label3.setTextFill(Color.WHITE);
		label3.setWrapText(true);
		label3.layoutXProperty().bind(widthProperty().multiply(0.51));
		label3.layoutYProperty().bind(heightProperty().multiply(0.95).subtract(label3.heightProperty()));
		label3.maxWidthProperty().bind(widthProperty().multiply(0.48));

		Label label1 = new Label("Het park is nu gesloten");
		label1.setFont(Font.font("Roboto Thin", 36));
		label1.setTextFill(Color.WHITE);
		label1.setWrapText(true);
		label1.layoutXProperty().bind(widthProperty().multiply(0.01));
		label1.layoutYProperty().bind(Bindings.min(label2.layoutYProperty(),
				label3.layoutYProperty()).subtract(label1.heightProperty()));
		label1.maxWidthProperty().bind(widthProperty().multiply(0.48));

		Label label4 = new Label("Dagwinnaar " + EsstelingDatabase.getCurrentAttractionName());
		label4.setFont(Font.font("Roboto Thin", 60));
		label4.setTextFill(Color.WHITE);
		label4.setWrapText(true);
		label4.layoutXProperty().bind(widthProperty().divide(2).subtract(label4.widthProperty().divide(2)));
		label4.layoutYProperty().bind(heightProperty().multiply(0.1));
		label4.maxWidthProperty().bind(widthProperty().multiply(0.9));


		topScoreLabel = new Label("Laden... ");
		topScoreLabel.setFont(Font.font("Roboto Medium", 80));
		topScoreLabel.setTextFill(Color.WHITE);
		topScoreLabel.setWrapText(true);
		topScoreLabel.layoutXProperty().bind(widthProperty().divide(2)
				.subtract(topScoreLabel.widthProperty().divide(2)));
		topScoreLabel.layoutYProperty().bind(label4.layoutYProperty().add(label4.heightProperty()));
		topScoreLabel.maxWidthProperty().bind(widthProperty().multiply(0.9));

		EsstelingDatabase.addListener(this);

		FadeTransition ft = new FadeTransition(Duration.millis(400), this);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.playFromStart();

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				double elapsedTime = (now - lastUpdate) / 1_000_000.0 / 1_000.0;
				if (elapsedTime > UPDATE_INTERVAL) {
					lastUpdate = now;
				}
			}
		}.start();

		setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, null, null)));
		getChildren().addAll(backgroundImageView, label1, label2, label3, label4, topScoreLabel);
	}

	@Override
	public void onDatabaseRefreshed(boolean success, List<Score> allScores) {
		if (success) {
			final AtomicInteger topScore = new AtomicInteger(-1);
			StringBuilder builder = new StringBuilder();
			allScores.stream()
					.filter(s -> s.getTime().isAfter(LocalDate.now().atStartOfDay()))
					.sorted()
					.forEach(s -> {
						if (topScore.get() < 0) {
							builder.append(s.getName());
							topScore.set(s.getScore());
						}
						else if (s.getScore() == topScore.get()) builder.append(", ").append(s.getName());
					});
			builder.append("\nScore: ").append(topScore.get());
			topScoreLabel.setText(builder.toString());
		}
	}
}
