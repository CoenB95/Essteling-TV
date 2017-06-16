import games.AnacondaGame;
import games.GamePane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

/**
 * @author Coen Boelhouwers
 */
public class LoginPane extends Pane {

	private ObservableList<AttractionItem> items;

	public LoginPane(OnAttractionSelectedListener l) {
		items = FXCollections.observableArrayList(
				new AttractionItem("Anaconda", AnacondaGame.class)
		);

		ListView<LoginPane.AttractionItem> listView = new ListView<>();
		listView.setItems(items);
		listView.getSelectionModel().selectedItemProperty().addListener((v1, v2, v3) -> {
			if (l != null) l.onAttractionSelected(v3);
		});
		listView.setCellFactory(param -> {
			return new ListCell<AttractionItem>() {
				@Override
				protected void updateItem(AttractionItem item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) setText("");
					else if (item == null) setText("null");
					else setText(item.name);
				}
			};
		});
		listView.layoutXProperty().bind(widthProperty().divide(2).subtract(listView.widthProperty().divide(2)));
		listView.layoutYProperty().bind(heightProperty().divide(2).subtract(listView.heightProperty().divide(2)));

		Label label = new Label("Kies attractie:");
		label.layoutXProperty().bind(widthProperty().divide(2).subtract(label.widthProperty().divide(2)));
		label.layoutYProperty().bind(listView.layoutYProperty().subtract(label.heightProperty()));

		getChildren().addAll(label, listView);
	}

	public static Scene newScene(OnAttractionSelectedListener l) {
		return new Scene(new LoginPane(l), 900, 500);
	}

	public static class AttractionItem {
		private String name;
		private Class<? extends GamePane> game;

		public AttractionItem(String name, Class<? extends GamePane> game) {
			this.name = name;
			this.game = game;
		}

		public String getAttractionName() {
			return name;
		}

		public GamePane getGamePane() {
			try {
				return game.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public interface OnAttractionSelectedListener {
		void onAttractionSelected(AttractionItem item);
	}
}
