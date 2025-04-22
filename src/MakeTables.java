package src;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class MakeTables {

  public static void TableCreation(Connection conn) throws SQLException {
      Statement stmt = conn.createStatement();

      createLocationTable(stmt);
      createPersonTable(stmt);
      createGameCharacterTable(stmt);
      createCreatureTable(stmt);
      createGenericItemTable(stmt);
      createContainerTable(stmt);
      createArmorTable(stmt);
      createWeapon(stmt);
      createPreferences(stmt);
      createAbility(stmt);

  }

  public static void createLocationTable(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS PREFERENCES");
    stmt.executeUpdate("DROP TABLE IF EXISTS CREATURE");
    stmt.executeUpdate("DROP TABLE IF EXISTS ARMOR");
    stmt.executeUpdate("DROP TABLE IF EXISTS GAME_CHARACTER");
    stmt.executeUpdate("DROP TABLE IF EXISTS LOCATION");
    String createLocation = """
            CREATE TABLE LOCATION (
                          Location_ID INT,
                          Location_Type VARCHAR(15) NOT NULL,
                          Size INT NOT NULL,
                          Exit_ID INT NOT NULL,
                          PRIMARY KEY (Location_ID)
                      )""";
    stmt.executeUpdate(createLocation);
    System.out.println("Created LOCATION table.");
  }

  public static void createPersonTable(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS PERSON");

    String createPerson =
            """
             CREATE TABLE PERSON (
                        Login VARCHAR(10) NOT NULL,
            Creation_Date DATE NOT NULL,
                        Password
            VARCHAR(10) NOT NULL,
                        Email VARCHAR(20) NOT NULL,
                        Type_of_Account ENUM(
            'Player', 'Moderator', 'Manager') NOT
            NULL,
            Above_Account_ID INT NOT NULL,
                        Creature_ID INT NOT NULL,
            Character_Name VARCHAR(10) NOT NULL,
                        PRIMARY KEY(Login)
                    )""";
    stmt.executeUpdate(createPerson);
    System.out.println("Created PERSON table.");
  }

  public static void createGameCharacterTable(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS ARMOR");
    stmt.executeUpdate("DROP TABLE IF EXISTS GAME_CHARACTER");
    String createGameCharacter = """
            CREATE TABLE GAME_CHARACTER (
                Name VARCHAR(15) NOT NULL,
                Strength INT NOT NULL,
                Stamina INT NOT NULL,
                Photo BLOB,
                Max_HP INT,
                Curr_HP INT,
                Location_ID INT,
                PRIMARY KEY (Name),
                FOREIGN KEY (Location_ID) REFERENCES LOCATION(Location_ID)
                            ON DELETE CASCADE ON UPDATE CASCADE
                            )
            """;
    stmt.executeUpdate(createGameCharacter);
    System.out.println("Created GAME_CHARACTER table.");

  }

  public static void createCreatureTable(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS PREFERENCES");
    stmt.executeUpdate("DROP TABLE IF EXISTS CREATURE");
    String createCreature = """
            CREATE TABLE CREATURE (
            	Name CHAR(10)	NOT NULL,
            	Strength INT,
            	Stamina INT,
            	Photo BLOB,
              Max_HP INT,
              Curr_HP	INT,
              Location_ID INT,
              Damage_Reduction INT,
              Creature_Location INT,
              Ability_Name VARCHAR(15),
              PRIMARY KEY (Name),
              FOREIGN KEY (Location_ID) REFERENCES LOCATION(LOCATION_ID) ON DELETE CASCADE
           )
           """;
    stmt.executeUpdate(createCreature);
    System.out.println("Created CREATURE table.");

  }

  public static void createGenericItemTable(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS GENERIC_ITEM");
    String createGenericItem = """
            CREATE TABLE GENERIC_ITEM (
              Gen_Item_ID	VARCHAR(10)	NOT NULL,
              Description	CHAR(30) NOT NULL,
              Weight	DECIMAL(4,2)	CHECK (Weight > 0),
              Volume	DECIMAL(4,2)	CHECK (Volume > 0),
              PRIMARY KEY (Gen_Item_ID)
          )
          """;
    stmt.executeUpdate(createGenericItem);
    System.out.println("Created GENERIC_ITEM table.");

  }

  public static void createContainerTable(Statement stmt) throws
            SQLException {
    stmt.executeUpdate(" DROP TABLE IF EXISTS CONTAINER"
            );
    String createContainer =
            """
            CREATE TABLE CONTAINER (
          	Container_ID	VARCHAR(10)	NOT NULL,
          	Volume_Limit DECIMAL(4,2),
          	Weight_Limit	DECIMAL(4,2),
          	PRIMARY KEY (Container_ID)
          );
          """;
    stmt.executeUpdate(createContainer);
    System.out.println("Created CONTAINER table.");
  }

  public
            static void createArmorTable(
            Statement stmt) throws
            SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS ARMOR");
            String createArmor =
            """
            CREATE TABLE ARMOR (
          	Armor_ID INT	NOT NULL,
                      Name
                      VARCHAR(15)	NOT NULL,
          	Character_Name	VARCHAR(15)	NOT NULL,
          Damage_Reduction 	INT	NOT NULL,
          PRIMARY KEY(Armor_ID),
          FOREIGN KEY(Character_Name) REFERENCES GAME_CHARACTER(Name) ON DELETE CASCADE
          )
          
          """;
    stmt.executeUpdate(createArmor);
    System.out.println("Created ARMOR table.");
  }

  public static void createWeapon(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS WEAPON");
    String createWeapon = """
            CREATE TABLE WEAPON (
          	Weapon_ID INT	NOT NULL,
          	Ability_Name	VARCHAR(15)	NOT NULL,
          	Hands_Needed	ENUM('one', 'two'),
                      PRIMARY KEY(Weapon_ID)
          )
          """;
    stmt.executeUpdate(createWeapon);
    System.out.println("Created WEAPON table.");
  }

  public static void createPreferences(Statement stmt) throws SQLException {
    stmt.executeUpdate("DROP TABLE IF EXISTS PREFERENCES");
    String createPreferences = """
            CREATE TABLE PREFERENCES (
          	Creature_Name	CHAR(10) NOT NULL,
          	Player_Hate	BOOLEAN	NOT NULL,
          	Player_Like	BOOLEAN	NOT NULL,
          	Creature_Hate	BOOLEAN	NOT NULL,
          	Creature_Like	BOOLEAN	NOT NULL,
          	PRIMARY KEY (Creature_Name),
            FOREIGN KEY (Creature_Name) REFERENCES CREATURE(Name) ON DELETE CASCADE
                      )
          
          """;
    stmt.executeUpdate(createPreferences);
    System.out.println("Created PREFERENCES table.");
  }

  public static void createAbility(Statement stmt)
            throws SQLException {
            stmt.executeUpdate("DROP TABLE IF EXISTS ABILITY");
            String createAbility = """
            CREATE TABLE ABILITY (
              	Name VARCHAR(15)	NOT NULL,
              	Sound_Effect 	VARCHAR(15)	NOT NULL,
              	Rate_Of_Occurrence INT,
              	Cast_Time	TIME	NOT NULL,
              	Stat_Affected	ENUM('HP','Strength','Stamina')	NOT NULL,
              	Effect_Amount		INT,
              	PRIMARY KEY (Name)
              )
          """;
    stmt.executeUpdate(createAbility);
    System.out.println("Created ABILITY table.");
 }
}
