import javafx.scene.layout.Pane;

/**
 * @author Coen Boelhouwers
 */
public abstract class GamePane extends Pane {
	private String attractionName;

	public GamePane(String attractionName) {
		this.attractionName = attractionName;
	}

	public String getAttractionName() {
		return attractionName;
	}
}
