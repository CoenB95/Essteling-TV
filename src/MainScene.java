import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * @author Coen Boelhouwers
 */
public class MainScene extends Scene {

	private Pane pane;
	private Pane child;

	public MainScene() {
		super(new Pane());
		pane = (Pane) getRoot();
	}

	public void changePane(Pane newPane) {
		Pane oldPane = child;
		child = newPane;
		newPane.prefWidthProperty().bind(widthProperty());
		newPane.prefHeightProperty().bind(heightProperty());
		newPane.setOpacity(0);

		if (oldPane != null) {
			FadeTransition ft1 = new FadeTransition(Duration.millis(400), oldPane);
			ft1.setFromValue(1);
			ft1.setToValue(0);
			ft1.playFromStart();
		}

		FadeTransition ft2 = new FadeTransition(Duration.millis(400), newPane);
		ft2.setFromValue(0);
		ft2.setToValue(1);
		ft2.playFromStart();
		ft2.setOnFinished(event -> {
			if (oldPane != null) pane.getChildren().remove(oldPane);
		});

		pane.getChildren().add(newPane);
		if (oldPane != null) oldPane.toFront();
	}
}
