import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * @author Coen Boelhouwers
 */
public class DynamicBackgroundPane extends Pane {

	public DynamicBackgroundPane() {
		Image backgroundImage = new Image("/snake_background2.png", true);
		ImageView backgroundImageView = new ImageView(backgroundImage);
		backgroundImageView.setCache(true);

		FadeTransition ft = new FadeTransition(Duration.millis(400), backgroundImageView);
		ft.setFromValue(0);
		ft.setToValue(1);

		TranslateTransition tt = new TranslateTransition(Duration.seconds(20), backgroundImageView);
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

		getChildren().add(backgroundImageView);
	}
}
