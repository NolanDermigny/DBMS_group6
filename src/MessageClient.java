package src;

import java.io.*;
import java.net.Socket;

public class MessageClient {
  public static void main(String[] args) {
    try (Socket clientSocket = new Socket("127.0.0.1", 4446);
         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

      System.out.println("[Client] Connected to server.");
//      example query
      String message = "SELECT * FROM PERSON";
      out.println(message);
      System.out.println("[Client] Sent message: " + message);

      String response;
      System.out.println("[Client] Received response:");
      while ((response = in.readLine()) != null) {
        System.out.println(response);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
