package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.STRUCT;

/**
 * Public class that implements methods from ClientDAO
 * @author kebuac
 */
public class ClientDAOImplem implements ClientDAO {
    
    /**
     * Method to get all clients from Oracle database
     * @param connection Database connection
     * @return List with all registered clients on database
     */
    @Override
    public ArrayList<STRUCT> showAllClients(Connection connection) {
        ArrayList<STRUCT> clients = new ArrayList<>();
        
        try (Statement statement = connection.createStatement()){
            String clientsQuery = "SELECT VALUE(c) FROM clients_table c";
            ResultSet resultSet = statement.executeQuery(clientsQuery);
            
            //Getting results
            while(resultSet.next()){
                clients.add((STRUCT) resultSet.getObject(1));
            }
            
            for(STRUCT client : clients){
                Object[] clientsValues = client.getAttributes();
                String cif = (String) clientsValues[0];
                String name = (String) clientsValues[1];
                String surname = (String) clientsValues[2];
                float discount = (float) clientsValues[7];
                
                System.out.println("Client: \n"
                        + "CIF: " +cif+ "\n"
                        + "Name:`" +name+ "\n"
                        + "Surname: " +surname+ "\n"
                        + "Discount: " +discount);
            }
            
        }catch(SQLException ex){
            System.out.println("Database error: " +ex.getMessage());
            System.out.println("Database state: " +ex.getSQLState());
            System.out.println("Error code: " +ex.getErrorCode());
        }
        
        return clients;
    }

    @Override
    public boolean addClient(STRUCT client, Connection connection) {
        String insertQuery = "INSERT INTO clients_table VALUES (?)";
        
        try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){
            preparedStatement.setObject(1, client);
            
            if(preparedStatement.executeUpdate() != 1){
                return false;
            }
            
        }catch (SQLException ex) {
            System.out.println("Database error: " +ex.getMessage());
            System.out.println("Database state: " +ex.getSQLState());
            System.out.println("Error code: " +ex.getErrorCode());
        }
        
        return true;
    }

    @Override
    public boolean updateClientPhone(STRUCT client, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean updateClientDiscount(STRUCT client, Connection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
