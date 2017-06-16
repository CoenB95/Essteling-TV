package games;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

/**
 * @author Coen Boelhouwers
 */
public abstract class GamePane extends Pane {

	private String attractionName;
	private long lastNanos;

	public GamePane(String attractionName) {
		this.attractionName = attractionName;

		Canvas canvas = new Canvas(100, 100);
		GraphicsContext g = canvas.getGraphicsContext2D();

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (lastNanos < 0) lastNanos = now;
				double elapsedTime = (now - lastNanos) / 1_000_000.0 / 1_000.0;
				lastNanos = now;
				canvas.setWidth(getWidth());
				canvas.setHeight(getHeight());
				update(elapsedTime);
				draw(g);
			}
		}.start();

		getChildren().add(canvas);
	}

	public String getAttractionName() {
		return attractionName;
	}

	public abstract void update(double elapsedTime);
	public abstract void draw(GraphicsContext g);
}
