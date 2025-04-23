package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FillTables {

  public static void insertIntoTables(Connection conn) throws SQLException {
    insertLocations(conn);
    insertPerson(conn);
    insertGameCharacters(conn);
    insertCreatures(conn);
    insertArmor(conn);
    insertGenericItems(conn);
    insertPreferences(conn);
  }

  public static void insertLocations(Connection conn) throws SQLException {
    PreparedStatement l1 = conn.prepareStatement("INSERT INTO LOCATION (Location_ID, Location_Type, Size, Exit_ID) VALUES (1, 'Forest', 50, 11)");
    PreparedStatement l2 = conn.prepareStatement("INSERT INTO LOCATION (Location_ID, Location_Type, Size, Exit_ID) VALUES (2, 'Desert', 75, 12)");
    PreparedStatement l3 = conn.prepareStatement("INSERT INTO LOCATION (Location_ID, Location_Type, Size, Exit_ID) VALUES (3, 'Cave', 30, 13)");
    PreparedStatement l4 = conn.prepareStatement("INSERT INTO LOCATION (Location_ID, Location_Type, Size, Exit_ID) VALUES (4, 'Ocean', 100, 14)");
    PreparedStatement l5 = conn.prepareStatement("INSERT INTO LOCATION (Location_ID, Location_Type, Size, Exit_ID) VALUES (5, 'City', 60, 15)");

    l1.executeUpdate();
    l2.executeUpdate();
    l3.executeUpdate();
    l4.executeUpdate();
    l5.executeUpdate();
  }

  public static void insertPerson(Connection conn) throws SQLException {
    PreparedStatement p1 = conn.prepareStatement("INSERT INTO PERSON (Login, Creation_Date, Password, Email, Type_Of_Account, Above_Account_ID, Creature_ID, Character_Name) VALUES ('Amanda', '2000-01-01', 'password1', 'a@gmail.com', 'Player', 1, 11, 'Joe')");
    PreparedStatement p2 = conn.prepareStatement("INSERT INTO PERSON (Login, Creation_Date, Password, Email, Type_Of_Account, Above_Account_ID, Creature_ID, Character_Name) VALUES ('Nolan', '2001-02-02', 'password2', 'n@gmail.com', 'Player', 2, 12, 'Sam')");
    PreparedStatement p3 = conn.prepareStatement("INSERT INTO PERSON (Login, Creation_Date, Password, Email, Type_Of_Account, Above_Account_ID, Creature_ID, Character_Name) VALUES ('Carlie', '2002-03-03', 'password3', 'c@gmail.com', 'Moderator', 3, 13, 'Girard')");
    PreparedStatement p4 = conn.prepareStatement("INSERT INTO PERSON (Login, Creation_Date, Password, Email, Type_Of_Account, Above_Account_ID, Creature_ID, Character_Name) VALUES ('Kevin', '2003-04-04', 'password4', 'k@gmail.com', 'Manager', 4, 14, 'Lee')");
    PreparedStatement p5 = conn.prepareStatement("INSERT INTO PERSON (Login, Creation_Date, Password, Email, Type_Of_Account, Above_Account_ID, Creature_ID, Character_Name) VALUES ('Charles', '2004-05-05', 'password5', 'ch@gmail.com', 'Player', 5, 15, 'Mia')");

    p1.executeUpdate();
    p2.executeUpdate();
    p3.executeUpdate();
    p4.executeUpdate();
    p5.executeUpdate();
  }

  public static void insertGameCharacters(Connection conn) throws SQLException {
    PreparedStatement g1 = conn.prepareStatement("INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('Joe', 10, 12, 100, 100, 1)");
    PreparedStatement g2 = conn.prepareStatement("INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('Sam', 8, 9, 80, 75, 2)");
    PreparedStatement g3 = conn.prepareStatement("INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('Girard', 14, 7, 120, 110, 3)");
    PreparedStatement g4 = conn.prepareStatement("INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('Lee', 6, 13, 95, 90, 4)");
    PreparedStatement g5 = conn.prepareStatement("INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('Mia', 11, 10, 100, 98, 5)");

    g1.executeUpdate();
    g2.executeUpdate();
    g3.executeUpdate();
    g4.executeUpdate();
    g5.executeUpdate();
  }

  public static void insertCreatures(Connection conn) throws SQLException {
    PreparedStatement c1 = conn.prepareStatement("INSERT INTO CREATURE (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID, Damage_Reduction, Creature_Location, Ability_Name) VALUES ('Wolf', 7, 8, 60, 60, 1, 2, 1, 'BITE')");
    PreparedStatement c2 = conn.prepareStatement("INSERT INTO CREATURE (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID, Damage_Reduction, Creature_Location, Ability_Name) VALUES ('Dragon', 4, 6, 30, 28, 2, 1, 2, 'FIRE BREATH')");
    PreparedStatement c3 = conn.prepareStatement("INSERT INTO CREATURE (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID, Damage_Reduction, Creature_Location, Ability_Name) VALUES ('Ogre', 15, 5, 200, 180, 3, 5, 3, 'SMASH')");
    PreparedStatement c4 = conn.prepareStatement("INSERT INTO CREATURE (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID, Damage_Reduction, Creature_Location, Ability_Name) VALUES ('Fairy', 3, 10, 40, 40, 4, 1, 4, 'HEAL')");
    PreparedStatement c5 = conn.prepareStatement("INSERT INTO CREATURE (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID, Damage_Reduction, Creature_Location, Ability_Name) VALUES ('Goblin', 6, 7, 70, 68, 5, 2, 5, 'STAB')");

    c1.executeUpdate();
    c2.executeUpdate();
    c3.executeUpdate();
    c4.executeUpdate();
    c5.executeUpdate();
  }

  public static void insertArmor(Connection conn) throws SQLException {
    PreparedStatement a1 = conn.prepareStatement("INSERT INTO ARMOR (Armor_ID, Name, Character_Name, Damage_Reduction) VALUES (1, 'Leather', 'Joe', 10)");
    PreparedStatement a2 = conn.prepareStatement("INSERT INTO ARMOR (Armor_ID, Name, Character_Name, Damage_Reduction) VALUES (2, 'Iron', 'Sam', 20)");
    PreparedStatement a3 = conn.prepareStatement("INSERT INTO ARMOR (Armor_ID, Name, Character_Name, Damage_Reduction) VALUES (3, 'Steel', 'Girard', 30)");
    PreparedStatement a4 = conn.prepareStatement("INSERT INTO ARMOR (Armor_ID, Name, Character_Name, Damage_Reduction) VALUES (4, 'Cloth', 'Lee', 5)");
    PreparedStatement a5 = conn.prepareStatement("INSERT INTO ARMOR (Armor_ID, Name, Character_Name, Damage_Reduction) VALUES (5, 'Chainmail', 'Mia', 25)");

    a1.executeUpdate();
    a2.executeUpdate();
    a3.executeUpdate();
    a4.executeUpdate();
    a5.executeUpdate();
  }

  public static void insertGenericItems(Connection conn) throws SQLException {
    PreparedStatement i1 = conn.prepareStatement("INSERT INTO GENERIC_ITEM (Gen_Item_ID, Description, Weight, Volume) VALUES ('i1', 'Health Potion', 0.5, 0.3)");
    PreparedStatement i2 = conn.prepareStatement("INSERT INTO GENERIC_ITEM (Gen_Item_ID, Description, Weight, Volume) VALUES ('i2', 'Underwater Potion', 0.4, 0.2)");
    PreparedStatement i3 = conn.prepareStatement("INSERT INTO GENERIC_ITEM (Gen_Item_ID, Description, Weight, Volume) VALUES ('i3', 'Torch', 1.0, 0.7)");
    PreparedStatement i4 = conn.prepareStatement("INSERT INTO GENERIC_ITEM (Gen_Item_ID, Description, Weight, Volume) VALUES ('i4', 'Map', 0.2, 0.1)");
    PreparedStatement i5 = conn.prepareStatement("INSERT INTO GENERIC_ITEM (Gen_Item_ID, Description, Weight, Volume) VALUES ('i5', 'Rope', 1.5, 1.0)");

    i1.executeUpdate();
    i2.executeUpdate();
    i3.executeUpdate();
    i4.executeUpdate();
    i5.executeUpdate();
  }

  public static void insertPreferences(Connection conn) throws SQLException {
    PreparedStatement pr1 = conn.prepareStatement("INSERT INTO PREFERENCES (Creature_Name, Player_Hate, Player_Like, Creature_Hate, Creature_Like) VALUES ('Wolf', true, false, false, true)");
    PreparedStatement pr2 = conn.prepareStatement("INSERT INTO PREFERENCES (Creature_Name, Player_Hate, Player_Like, Creature_Hate, Creature_Like) VALUES ('Dragon', false, true, true, false)");
    PreparedStatement pr3 = conn.prepareStatement("INSERT INTO PREFERENCES (Creature_Name, Player_Hate, Player_Like, Creature_Hate, Creature_Like) VALUES ('Ogre', true, false, true, false)");
    PreparedStatement pr4 = conn.prepareStatement("INSERT INTO PREFERENCES (Creature_Name, Player_Hate, Player_Like, Creature_Hate, Creature_Like) VALUES ('Fairy', false, true, false, true)");
    PreparedStatement pr5 = conn.prepareStatement("INSERT INTO PREFERENCES (Creature_Name, Player_Hate, Player_Like, Creature_Hate, Creature_Like) VALUES ('Goblin', true, false, true, false)");

    pr1.executeUpdate();
    pr2.executeUpdate();
    pr3.executeUpdate();
    pr4.executeUpdate();
    pr5.executeUpdate();
  }
}

