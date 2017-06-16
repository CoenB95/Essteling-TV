package games;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class AnacondaGame extends GamePane {

	private long lastNanos;
	private List<Snake> snakes;

	public AnacondaGame() {
		super("Anaconda");

		snakes = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Snake snake = new Snake();
			snake.x = Math.random() * 900;
			snakes.add(snake);
		}

		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
	}

	@Override
	public void update(double elapsedTime) {
		snakes.forEach(snake -> {
			snake.update(elapsedTime);
			if (snake.x < -snake.length) {
				snake.x = getWidth();
				snake.y = snake.width + Math.random() * (getHeight() - snake.width);
			}
		});
	}

	@Override
	public void draw(GraphicsContext g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		snakes.forEach(snake -> snake.draw(g));
	}

	private static class Snake {
		double x;
		double y;
		double length;
		double width;
		private double speed;
		private static Image head = new Image("/snake_head.png", true);
		private static final Image body_0 = new Image("/snake_middle_straight.png", true);
		private static final Image body_1 = new Image("/snake_middle_left.png", true);
		private static final Image body_2 = new Image("/snake_middle_right.png", true);
		private static Image tail = new Image("/snake_tail.png", true);

		private List<Image> segments;

		public Snake() {
			speed = 50 + Math.random() * 200;
			segments = new ArrayList<>();
			segments.add(head);
			for (int i = (int) (Math.random() * 6); i > 0; i--) {
				switch (i % 3) {
					case 0:
						segments.add(body_0);
						break;
					case 1:
						segments.add(body_1);
						break;
					case 2:
						segments.add(body_2);
						break;
				}
			}
			segments.add(tail);
		}

		public void draw(GraphicsContext g) {
			g.save();
			g.translate(x, y);
			g.rotate(-90);
			g.scale(0.5, 0.5);
			for (int i = 0; i < segments.size(); i++) {
				g.drawImage(segments.get(i), 0, head.getHeight() * i);
			}
			g.restore();
			width = head.getWidth();
			length = head.getWidth() * segments.size();
		}

		public void update(double elapsedTime) {
			x -= speed * elapsedTime;
		}
	}
}
