import com.l15v.MySQLConnector.DatabaseConnector;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Coen Boelhouwers
 */
public class EsstelingDatabase {

	private static String attractieNaam;
	private String host;
	private String dataBase;
	private String gebNaam;
	private String password;
	private static final List<DatabaseListener> listeners = new ArrayList<>();
	private static List<Score> cachedScores;
	private ScheduledExecutorService service;

	public EsstelingDatabase() {
		this.host = "84.105.109.27";
		this.dataBase = "EsstelingApp";
		this.gebNaam = "PGB3";
		this.password = "B3Essteling";

		cachedScores = new ArrayList<>();
		service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(this::updateScores, 0, 30, TimeUnit.SECONDS);
	}

	public static void addListener(DatabaseListener l) {
		listeners.add(l);
	}

	public static void changeAttraction(String attractieNaam) {
		EsstelingDatabase.attractieNaam = attractieNaam;
	}

	private List<Score> convertToScores(List<String> source) {
		List<Score> scores = new ArrayList<>();
		source.forEach(s -> {
			String[] sp = s.split("~");
			scores.add(new Score(sp[1], Integer.valueOf(sp[2]), LocalDate.parse(sp[3]).atTime(LocalTime.parse(sp[4]))));
		});
		return scores;
	}

	public List<Score> fetchScoresOf(LocalDate date) throws SQLException, ClassNotFoundException {
		DatabaseConnector con = new DatabaseConnector(host, dataBase, gebNaam, password);
		List<String> scoreStrings = con.voerSelectedOrderedQueryUit("*", attractieNaam,
				"Datum='" + date + "'", "Score DESC", 5);
		con.closeConnection();
		return convertToScores(scoreStrings);
	}

	public List<Score> fetchScoresOfToday() throws SQLException, ClassNotFoundException {
		return fetchScoresOf(LocalDate.now());
	}

	public List<Score> fetchScoresAfter(LocalDate date) throws SQLException, ClassNotFoundException {
		DatabaseConnector con = new DatabaseConnector(host, dataBase, gebNaam, password);
		List<String> scores = con.voerSelectedOrderedQueryUit("*", attractieNaam, "Datum >='" +
				date + "'", "score DESC", 5);
		con.closeConnection();
		return convertToScores(scores);
	}

	public List<Score> fetchScoresOfLastWeek() throws SQLException, ClassNotFoundException {
		return fetchScoresAfter(LocalDate.now().minusWeeks(1));
	}

	public List<Score> fetchScoresOfPastMonth() throws SQLException, ClassNotFoundException {
		return fetchScoresAfter(LocalDate.now().minusMonths(1));
	}

	public List<Score> fetchAllScores() throws SQLException, ClassNotFoundException {
		DatabaseConnector con = new DatabaseConnector(host, dataBase, gebNaam, password);
		List<String> scores = con.voerOrderedQueryUit("*", attractieNaam, "score DESC", 5);
		con.closeConnection();
		return convertToScores(scores);
	}

	public static List<Score> getCachedScores() {
		return cachedScores;
	}

	public static String getCurrentAttractionName() {
		return attractieNaam;
	}

	public void refreshNow() {
		service.submit(this::updateScores);
	}

	public void stop() {
		service.shutdown();
	}

	private void updateScores() {
		Platform.runLater(() -> listeners.forEach(DatabaseListener::onDatabaseRefreshStart));
		try {
			List<Score> newScores = fetchScoresAfter(LocalDate.now().minusYears(1));
			cachedScores.clear();
			cachedScores.addAll(newScores);
			Platform.runLater(() -> listeners.forEach(l -> l.onDatabaseRefreshed(true, cachedScores)));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			Platform.runLater(() -> listeners.forEach(l -> l.onDatabaseRefreshed(false, null)));
		}
	}

	public interface DatabaseListener {
		default void onDatabaseRefreshStart() {

		}
		default void onDatabaseRefreshed(boolean success, List<Score> allScores) {

		}
	}
}
