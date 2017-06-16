package games;

import games.GamePane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * @author Coen Boelhouwers
 */
public class AnacondaGame extends GamePane {

	private long lastNanos;
	private double i;

	public AnacondaGame() {
		super("Anaconda");
		i = 400;
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
	}

	@Override
	public void update(double elapsedTime) {
		i -= 50 * elapsedTime;
		if (i < -50) i = getHeight();
	}

	@Override
	public void draw(GraphicsContext g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setFill(Color.GREEN);
		g.setFont(Font.font("Roboto Medium", 200));
		g.setTextAlign(TextAlignment.CENTER);
		g.fillText("TEST!", getWidth()/2, i);
	}
}
