package src;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Scanner;



public class ClientServer
{
  public static final String DB_LOCATION = "jdbc:mysql://db.engr.ship.edu:3306/cmsc471_10?useTimezone=true&serverTimezone=UTC";
  public static final String LOGIN_NAME = "cmsc471_10";
  public static final String PASSWORD = "Password_10";

  public static void main(String[] args) {
    try {
      // Register JDBC driver
      DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
      // Always set up the test table on startup
      try (Connection conn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD)) {
        System.out.println("Connected");
        MakeTables.TableCreation(conn);
        FillTables.insertIntoTables(conn);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      //Run as server or client
      ClientServer app = new ClientServer();
      if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
        app.setupServer();
      } else {
        app.setupClient();
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // SERVER: Receives query, runs it, sends results
  public void setupServer() {
    try (ServerSocket serverSocket = new ServerSocket(4446)) {
      System.out.println("[Server] Listening on port 4446...");

      //Accept client connection
      Socket incomingClient = serverSocket.accept();
      System.out.println("[Server] Client connected.");

      //Get input and output stream
      Scanner in = new Scanner(incomingClient.getInputStream());
      DataOutputStream out = new DataOutputStream(incomingClient.getOutputStream());

      // Read SQL query from client
      String query = in.nextLine();
      System.out.println("[Server] Received query: " + query);

      StringBuilder result = new StringBuilder();

      //Execute the query
      try (Connection conn = DriverManager.getConnection(DB_LOCATION, LOGIN_NAME, PASSWORD);
           Statement stmt = conn.createStatement();
           ResultSet rs = stmt.executeQuery(query)) {

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        //Append
        while (rs.next()) {
          for (int i = 1; i <= colCount; i++) {
            result.append(rs.getString(i));
            if (i < colCount) result.append(", ");
          }
          result.append("\n");
        }
      } catch (SQLException e) {
        result.append("SQL Error: ").append(e.getMessage()).append("\n");
      }

      //Send results to the client
      out.writeBytes(result.toString());
      System.out.println("[Server] Sent results to client.");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // CLIENT: Sends query, receives and prints results
  public void setupClient() {
    try (Socket clientSocket = new Socket("127.0.0.1", 4446)) {
      System.out.println("[Client] Connected to server.");

      //Set up output and input stream
      DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
      Scanner in = new Scanner(clientSocket.getInputStream());

      String query = "SELECT * FROM GENERIC_ITEM WHERE Gen_Item_ID = 'i1'";



      out.writeBytes(query + "\n");

      System.out.println("[Client] Sent query to server.\n[Client] Received result:\n");

      //Print results
      while (in.hasNextLine()) {
        System.out.println(in.nextLine());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}