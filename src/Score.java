import java.time.LocalDateTime;

/**
 * @author CoenB95
 */
public class Score implements Comparable<Score> {
	private String name;
	private int score;
	LocalDateTime time;

	public Score(String name, int score) {
		this(name, score, LocalDateTime.now());
	}

	public Score(String name, int score, LocalDateTime time) {
		this.name = name;
		this.score = score;
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public LocalDateTime getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "Score{name=\"" + name + "\", score=" + score + ", time=" + time + "}";
	}

	@Override
	public int compareTo(Score o) {
		if (this.score < o.score) return 1;
		else if (this.score > o.score) return -1;
		else return this.name.compareTo(o.name);
	}
}
