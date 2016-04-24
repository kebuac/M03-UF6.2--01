package singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Public class used as Singleton
 * @author kebuac
 */
public class DatabaseConnection {
    //Instance
    private static Connection instance;
    
    //Private empty constructor to restrict access
    private DatabaseConnection(){}
    
    /**
     * Static method to open database
     * @return Database
     * @throws SQLException Exception when exists a problem with database connection 
     */
    public static Connection getInstance() throws SQLException {
        if(instance == null){
            instance = DriverManager.getConnection(ConnectionInformation.DATABASE_URL, ConnectionInformation.USERNAME, 
                    ConnectionInformation.PASSWORD);
            System.out.println("Successful connection to database!");
        }
        
        return instance;
    }
    
    /**
     * Static method to close database
     * @throws SQLException Eception when exists a problem with database close
     */
    public static void closeConnection() throws SQLException {
        if(instance != null){
            instance.close();
            instance = null;
            System.out.println("Databas was closed!");
        }
    }
}
