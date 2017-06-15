import com.l15v.MySQLConnector.DatabaseConnector;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Coen Boelhouwers
 */
public class EsstelingDatabase {

	private String attractieNaam;
	private String host;
	private String dataBase;
	private String gebNaam;
	private String password;

	public EsstelingDatabase(String attractieNaam) {
		this.attractieNaam = attractieNaam;
		this.host = "84.105.109.27";
		this.dataBase = "EsstelingApp";
		this.gebNaam = "PGB3";
		this.password = "B3Essteling";
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
}
