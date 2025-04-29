package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

/**
 * MessageServer listens for client connections and handles SQL queries sent by
 * clients.
 */
public class MessageServer {

	/**
	 * Database connection URL, login name, and password.
	 */
	public static final String DB_LOCATION = "jdbc:mysql://db.engr.ship.edu:3306/cmsc471_10?useTimezone=true&serverTimezone=UTC";
	public static final String LOGIN_NAME = "cmsc471_10";
	public static final String PASSWORD = "Password_10";

	/**
	* Main method to start the server and listen for client connections.
	*/
	public static void main(String[] args) {
		try {
			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		try (ServerSocket serverSocket = new ServerSocket(4446)) {
			System.out.println("[Server] Listening on port 4446...");

			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("[Server] Client connected.");

//        thread allows for multiple client connections
				new Thread(() -> handleClient(clientSocket)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	* Handles communication with a connected client.
	*/
	private static void handleClient(Socket clientSocket) {
		 try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		         Connection conn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD);
		         Statement stmt = conn.createStatement()) {

		      String sql = in.readLine();
		      System.out.println("[Server] Received query: " + sql);
		      String result = executeSQL(conn,sql);
		      out.write(result);
		      out.newLine();
		      out.flush();
		    } catch (Exception e) {
		      System.out.println("[Server] Error: " + e.getMessage());
		      e.printStackTrace();
		    }

	}

	/**
	* Executes an SQL command and returns the result as a String.
	*/
	private static String executeSQL(Connection conn, String message) {
//    get the first word of the message to determine the command type
//    tbh there might be a better/safer way to do this
		String commandWord = message.trim().split("\\s+")[0].toUpperCase();

		try {
			return switch (commandWord) {
			case "SELECT" -> handleSelect(conn, message);
			case "INSERT" -> handleInsert(conn, message);
			case "UPDATE" -> handleUpdate(conn, message);
			case "DELETE" -> handleDelete(conn, message);
			case "CALL" -> storedProcedure(conn, message);
			default -> "Invalid command type: " + commandWord;
			};
		} catch (SQLException e) {
			return "SQL Error: " + e.getMessage();
		} catch (Exception e) {
			return "Server Processing Error: " + e.getMessage();
		}
	}

	/**
	* Handles a stored procedure call and returns the result as a String.
	*/
	private static String storedProcedure(Connection conn, String message) {
		try (CallableStatement stmt = conn.prepareCall(message)) {
			boolean hasResults = stmt.execute();
			StringBuilder result = new StringBuilder();

			if (hasResults) {
				try (ResultSet rs = stmt.getResultSet()) {
					ResultSetMetaData meta = rs.getMetaData();
					int colCount = meta.getColumnCount();

					for (int i = 1; i <= colCount; i++) {
						result.append(meta.getColumnName(i)).append("\t");
					}
					result.append("\n");

					while (rs.next()) {
						for (int i = 1; i < colCount; i++) {
							result.append(rs.getString(i)).append("\t");
						}
						result.append("\n");
					}
				}
			}
			return result.toString();
		} catch (SQLException e) {
			return "SQL Error: " + e.getMessage();
		}
	}

	/**
	* Handles a SELECT query and returns the result as a String.
	* Doesn't use a Stored Procedure.
   * @param conn
   * @param query
   * @return
   * @throws SQLException
	*/
	private static String handleSelect(Connection conn, String query) throws SQLException {
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			ResultSetMetaData meta = rs.getMetaData();
			int colCount = meta.getColumnCount();
			StringBuilder result = new StringBuilder();

			for (int i = 1; i <= colCount; i++) {
				result.append(meta.getColumnName(i)).append("\t");
			}
			result.append("\n");

			while (rs.next()) {
				for (int i = 1; i < colCount; i++) {
					result.append(rs.getString(i)).append("\t");
				}
				result.append("\n");
			}
			return result.toString();
		}
	}

	/**
	* Handles an INSERT query and returns the number of rows inserted.
	*/
	private static String handleInsert(Connection conn, String query) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected + (" row(s) inserted.");
		}
	}

	/**
	* Handles an UPDATE query and returns the number of rows updated.
	*/
	private static String handleUpdate(Connection conn, String query) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected + (" row(s) updated.");
		}
	}

	/**
	* Handles a DELETE query and returns the number of rows deleted.
	*/
	private static String handleDelete(Connection conn, String query) throws SQLException {
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected + (" row(s) deleted.");
		}
	}
}
