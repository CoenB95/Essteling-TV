import games.GamePane;
import javafx.animation.FadeTransition;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Coen Boelhouwers
 */
public class AllScoresPane extends Pane implements EsstelingDatabase.DatabaseListener {

	private static final double UPDATE_INTERVAL = 30;//seconds
	private static final double QR_IMAGE_SIZE = 200;//pixels

	private long lastUpdate = -1;
	private Font monoFont = Font.font("monospaced", 40);
	private FadeTransition progressFade;
	private List<ScorePane> scorePanes;
	private ScorePane scoreBoxDaily;
	private ScorePane scoreBoxWeekly;
	private ScorePane scoreBoxMonthly;
	private ScorePane scoreBoxYearly;

	public AllScoresPane(GamePane game) {
		game.prefWidthProperty().bind(widthProperty());
		game.prefHeightProperty().bind(heightProperty());

		DynamicBackgroundPane backgroundImageView = new DynamicBackgroundPane();
		backgroundImageView.prefWidthProperty().bind(widthProperty());
		backgroundImageView.prefHeightProperty().bind(heightProperty());

		Label scoreLabel = new Label(game.getAttractionName());
		scoreLabel.setFont(Font.font("Roboto Thin", 100));
		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.layoutXProperty().bind(widthProperty().divide(2).subtract(scoreLabel.widthProperty().divide(2)));
		scoreLabel.layoutYProperty().bind(heightProperty().divide(2).subtract(scoreLabel.heightProperty().divide(2)));

		ProgressIndicator progressBar = new ProgressIndicator();
		progressBar.layoutXProperty().bind(widthProperty().divide(2).subtract(progressBar.widthProperty().divide(2)));
		progressBar.layoutYProperty().bind(scoreLabel.layoutYProperty().add(scoreLabel.heightProperty()));
		progressBar.setOpacity(0);

		progressFade = new FadeTransition(Duration.millis(400), progressBar);

		scoreBoxDaily = createScorePane("Dag Top 10", 0.01, 0.01, 0.38, 0.38);
		scoreBoxWeekly = createScorePane("Week Top 10", 0.01, 0.61, 0.38, 0.38);
		scoreBoxMonthly = createScorePane("Maand Top 10", 0.61, 0.01, 0.38, 0.38);
		scoreBoxYearly = createScorePane("Jaar Top 10 (" + LocalDate.now().getYear() + ")",
				0.61, 0.61, 0.38, 0.38);

		scorePanes = Arrays.asList(scoreBoxDaily, scoreBoxWeekly, scoreBoxMonthly, scoreBoxYearly);

		EsstelingDatabase.addListener(this);

		Image qrImage = new Image("/qr_code.jpg", true);
		ImageView qrImageView = new ImageView(qrImage);
		qrImageView.fitWidthProperty().bind(Bindings.min(scoreBoxYearly.layoutXProperty().subtract(scoreBoxWeekly
				.layoutXProperty().add(scoreBoxWeekly.widthProperty())).subtract(10),
				scoreBoxWeekly.heightProperty().subtract(20)));
		qrImageView.fitHeightProperty().bind(qrImageView.fitWidthProperty());
		qrImageView.layoutXProperty().bind(widthProperty().divide(2).subtract(qrImageView.fitWidthProperty()
				.divide(2)));
		qrImageView.layoutYProperty().bind(heightProperty().subtract(qrImageView.fitHeightProperty()).subtract(10));

		setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		getChildren().addAll(backgroundImageView, game, qrImageView, scoreLabel);
		getChildren().addAll(scorePanes);
		getChildren().add(progressBar);
	}

	private ScorePane createScorePane(String title, double xOffset, double yOffset, double width, double height) {
		ScorePane pane = new ScorePane(title);
		pane.layoutXProperty().bind(widthProperty().multiply(xOffset));
		pane.prefWidthProperty().bind(widthProperty().multiply(width));
		pane.maxWidthProperty().bind(widthProperty().multiply(width));
		pane.layoutYProperty().bind(heightProperty().multiply(yOffset));
		pane.prefHeightProperty().bind(heightProperty().multiply(height));
		pane.maxHeightProperty().bind(heightProperty().multiply(height));
		return pane;
	}

	/*private void updateScores() {

		CompletableFuture.runAsync(() -> {
			try {
				EsstelingDatabase database = new EsstelingDatabase(getAttractionName());
				List<Score> scores = database.fetchScoresAfter(LocalDate.now().minusYears(1));
				System.out.println(scores);




			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		});
	}*/

	private void setScoresInPane(ScorePane pane, List<Score> scores) {
		pane.clearScores();
		for (int i = 0; i < scores.size(); i++) {
			Score score = scores.get(i);
			pane.addScore(score);
		}
	}

	@Override
	public void onDatabaseRefreshStart() {
		progressFade.setFromValue(0);
		progressFade.setToValue(1);
		progressFade.playFromStart();
	}

	@Override
	public void onDatabaseRefreshed(boolean success, List<Score> scores) {
		setScoresInPane(scoreBoxDaily, scores.stream().sorted().filter(s -> s.getTime()
				.isAfter(LocalDate.now().atStartOfDay())).limit(10).collect(Collectors.toList()));

		setScoresInPane(scoreBoxWeekly, scores.stream().sorted().filter(s -> s.getTime()
				.isAfter(LocalDate.now().minusWeeks(1).atStartOfDay())).limit(10).collect(Collectors.toList()));

		setScoresInPane(scoreBoxMonthly, scores.stream().sorted().filter(s -> s.getTime()
				.isAfter(LocalDate.now().minusMonths(1).atStartOfDay())).limit(10).collect(Collectors.toList()));

		setScoresInPane(scoreBoxYearly, scores.stream().sorted().filter(s -> s.getTime()
				.isAfter(LocalDate.now().minusYears(1).atStartOfDay())).limit(10).collect(Collectors.toList()));

		progressFade.setFromValue(1);
		progressFade.setToValue(0);
		progressFade.playFromStart();
	}
}
