import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
		Label label1 = new Label("Het park is nu gesloten");
		label1.setFont(Font.font("Roboto", 60));
		label1.setTextFill(Color.WHITE);
		label1.setWrapText(true);
		label1.layoutXProperty().bind(widthProperty().divide(2).subtract(label1.widthProperty().divide(2)));
		label1.layoutYProperty().bind(heightProperty().divide(6).subtract(label1.heightProperty().divide(2)));
		label1.maxWidthProperty().bind(widthProperty().multiply(0.9));

		Label label2 = new Label("Wij wensen u nog een fijne dag en zien u graag terug in de Essteling");
		label2.setFont(Font.font("Roboto", 40));
		label2.setTextFill(Color.WHITE);
		label2.setWrapText(true);
		label2.layoutXProperty().bind(widthProperty().divide(4).subtract(label2.widthProperty().divide(2)));
		label2.layoutYProperty().bind(label1.layoutYProperty().add(label1.heightProperty()));
		label2.maxWidthProperty().bind(widthProperty().multiply(0.4));

		Label label3 = new Label("The park is now closed. We wish you a nice day and hope to see you again soon" +
				" in the Essteling");
		label3.setFont(Font.font("Roboto", 22));
		label3.setTextFill(Color.WHITE);
		label3.setWrapText(true);
		label3.layoutXProperty().bind(widthProperty().multiply(0.05));
		label3.layoutYProperty().bind(heightProperty().multiply(0.75));
		label3.maxWidthProperty().bind(widthProperty().multiply(0.3));

		Label label4 = new Label("Dag winnaar " + EsstelingDatabase.getCurrentAttractionName());
		label4.setFont(Font.font("Roboto Thin", 40));
		label4.setTextFill(Color.WHITE);
		label4.setWrapText(true);
		label4.layoutXProperty().bind(widthProperty().multiply(0.61));
		label4.layoutYProperty().bind(label1.layoutYProperty().add(label1.heightProperty()));
		label4.maxWidthProperty().bind(widthProperty().multiply(0.38));

		topScoreLabel = new Label("Laden... ");
		topScoreLabel.setFont(Font.font("Roboto", 22));
		topScoreLabel.setTextFill(Color.WHITE);
		topScoreLabel.setWrapText(true);
		topScoreLabel.layoutXProperty().bind(widthProperty().multiply(0.61));
		topScoreLabel.layoutYProperty().bind(label4.layoutYProperty().add(label4.heightProperty()));
		topScoreLabel.maxWidthProperty().bind(widthProperty().multiply(0.38));

//		topScoresToday = new ScorePane("Winnaars");
//		topScoresToday.layoutXProperty().bind(widthProperty().multiply(0.61));
//		topScoresToday.layoutYProperty().bind(label1.layoutYProperty().add(label1.heightProperty()));
//		topScoresToday.prefWidthProperty().bind(widthProperty().multiply(0.38));
//		topScoresToday.prefHeightProperty().bind(label3.layoutYProperty().subtract(label1.layoutYProperty()
//				.add(label1.heightProperty())));

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

		setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		getChildren().addAll(label1, label2, label3, label4, topScoreLabel);
	}

	@Override
	public void onDatabaseRefreshed(boolean success, List<Score> allScores) {
		if (success) {
			allScores.stream()
					.sorted()
					.filter(s -> s.getTime().isAfter(LocalDate.now().atStartOfDay()))
					.findFirst()
					.ifPresent(s -> topScoreLabel.setText(s.getName() + " met een score van " + s.getScore() +
							" om " + s.getTime().format(DateTimeFormatter.ofPattern("H:mm"))));
		}
	}
}
