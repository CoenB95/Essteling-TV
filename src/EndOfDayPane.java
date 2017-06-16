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
public class EndOfDayPane extends GamePane {

	private static final double UPDATE_INTERVAL = 30;//seconds
	private static final double QR_IMAGE_SIZE = 200;//pixels

	private long lastUpdate = -1;

	public EndOfDayPane() {
		super("Anaconda");

		Label scoreLabel = new Label("Het park is nu gesloten");
		scoreLabel.setFont(Font.font("Roboto", 50));
		scoreLabel.setTextFill(Color.WHITE);
		scoreLabel.layoutXProperty().bind(widthProperty().divide(2).subtract(scoreLabel.widthProperty().divide(2)));
		scoreLabel.layoutYProperty().bind(heightProperty().divide(4).subtract(scoreLabel.heightProperty().divide(2)));

		FadeTransition ft = new FadeTransition(Duration.millis(400), scoreLabel);
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
		getChildren().addAll(scoreLabel);
	}
}
