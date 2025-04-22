package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FillTables {
  public static void insertIntoTables(Connection conn) throws SQLException {
    insertLocations(conn);
  }

  public static void insertLocations(Connection conn) throws SQLException {
    String Loc1 = "INSERT INTO LOCATION (Location_ID,Location_Type, Size, Exit_ID) VALUES (1, 'Forest', 50, 11)";
    String Loc2 = "INSERT INTO LOCATION (Location_ID,Location_Type, Size, Exit_ID) VALUES (2, 'Desert', 75, 12)";
    String Loc3 = "INSERT INTO LOCATION (Location_ID,Location_Type, Size, Exit_ID) VALUES (3, 'China', 76, 13)";
    String Loc4 = "INSERT INTO LOCATION (Location_ID,Location_Type, Size, Exit_ID) VALUES (4, 'Hell', 100, 14)";
    String Loc5 = "INSERT INTO LOCATION (Location_ID,Location_Type, Size, Exit_ID) VALUES (5, 'Shippensburg', 10, 15)";
    PreparedStatement l1 = conn.prepareStatement(Loc1);
    PreparedStatement l2 = conn.prepareStatement(Loc2);
    PreparedStatement l3 = conn.prepareStatement(Loc3);
    PreparedStatement l4 = conn.prepareStatement(Loc4);
    PreparedStatement l5 = conn.prepareStatement(Loc5);
    l1.executeUpdate();
    l2.executeUpdate();
    l3.executeUpdate();
    l4.executeUpdate();
    l5.executeUpdate();
  }
}
