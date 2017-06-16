import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author Coen Boelhouwers
 */
public class AnacondaGame extends GamePane {

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

	public AnacondaGame() {
		super("Araconda");

		Image backgroundImage = new Image("/snake_background2.png", true);
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.setCache(true);

		FadeTransition ft = new FadeTransition(Duration.millis(400), backgroundImageView);
		ft.setFromValue(0);
		ft.setToValue(1);

		TranslateTransition tt = new TranslateTransition(Duration.seconds(10), backgroundImageView);
		tt.setAutoReverse(true);
		tt.setCycleCount(TranslateTransition.INDEFINITE);
		//Wait for the image to finish loading
		backgroundImage.widthProperty().addListener((v1, v2, v3) -> {
			System.out.println("Image loaded");
			tt.setFromX(0);
			tt.toXProperty().bind(widthProperty().subtract(backgroundImage.widthProperty()));
			ft.playFromStart();
			tt.playFromStart();
		});
		widthProperty().addListener((v1, v2, v3) -> tt.playFromStart());

		Label scoreLabel = new Label("Anaconda");
		scoreLabel.setFont(Font.font("Roboto Thin", 100));
		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.layoutXProperty().bind(widthProperty().divide(2).subtract(scoreLabel.widthProperty().divide(2)));
		scoreLabel.layoutYProperty().bind(heightProperty().divide(2).subtract(scoreLabel.heightProperty().divide(2)));

		ProgressIndicator progressBar = new ProgressIndicator();
		progressBar.layoutXProperty().bind(widthProperty().divide(2).subtract(progressBar.widthProperty().divide(2)));
		progressBar.layoutYProperty().bind(scoreLabel.layoutYProperty().add(scoreLabel.heightProperty()));
		progressBar.setOpacity(0);

		progressFade = new FadeTransition(Duration.millis(400), progressBar);

		scoreBoxDaily = createScorePane("Top dag:", 0, 0, 0.4, 0.4);
		scoreBoxWeekly = createScorePane("Top week:", 0, 0.6, 0.4, 0.4);
		scoreBoxMonthly = createScorePane("Top maand:", 0.6, 0, 0.4, 0.4);
		scoreBoxYearly = createScorePane("Top " + LocalDate.now().getYear(),
				0.6, 0.6, 0.4, 0.4);

		scorePanes = Arrays.asList(scoreBoxDaily, scoreBoxWeekly, scoreBoxMonthly, scoreBoxYearly);

		Image qrImage = new Image("/qr_dummy.jpg", true);
		ImageView qrImageView = new ImageView(qrImage);
		qrImageView.setFitWidth(QR_IMAGE_SIZE);
		qrImageView.setFitHeight(QR_IMAGE_SIZE);
		qrImageView.layoutXProperty().bind(widthProperty().divide(2).subtract(QR_IMAGE_SIZE/2));
		qrImageView.layoutYProperty().bind(heightProperty().subtract(QR_IMAGE_SIZE));

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				double elapsedTime = (now - lastUpdate) / 1_000_000.0 / 1_000.0;
				if (elapsedTime > UPDATE_INTERVAL) {
					lastUpdate = now;
					System.out.println("Update scores...");
					updateScores();
				}
				System.out.println(qrImageView.getFitWidth());
			}
		}.start();

		setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		getChildren().addAll(backgroundImageView, qrImageView, scoreLabel);
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

	private void updateScores() {
		progressFade.setFromValue(0);
		progressFade.setToValue(1);
		progressFade.playFromStart();
		CompletableFuture.runAsync(() -> {
			try {
				EsstelingDatabase database = new EsstelingDatabase(getAttractionName());
				List<Score> scores = database.fetchScoresAfter(LocalDate.now().minusYears(1));
				System.out.println(scores);

				setScoresInPane(scoreBoxDaily, scores.stream().sorted().filter(s -> s.getTime()
						.isAfter(LocalDate.now().atStartOfDay())).collect(Collectors.toList()));

				setScoresInPane(scoreBoxWeekly, scores.stream().sorted().filter(s -> s.getTime()
						.isAfter(LocalDate.now().minusWeeks(1).atStartOfDay())).collect(Collectors.toList()));

				setScoresInPane(scoreBoxMonthly, scores.stream().sorted().filter(s -> s.getTime()
						.isAfter(LocalDate.now().minusMonths(1).atStartOfDay())).collect(Collectors.toList()));

				setScoresInPane(scoreBoxYearly, scores.stream().sorted().filter(s -> s.getTime()
						.isAfter(LocalDate.now().minusYears(1).atStartOfDay())).collect(Collectors.toList()));

				progressFade.setFromValue(1);
				progressFade.setToValue(0);
				progressFade.playFromStart();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		});
	}

	private void setScoresInPane(ScorePane pane, List<Score> scores) {
		pane.clearScores();
		for (int i = 0; i < scores.size(); i++) {
			Score score = scores.get(i);
			pane.addScore(score);
		}
	}
}
