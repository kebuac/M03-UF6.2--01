package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

/**
 * Public class that implements methods from ClientDAO
 *
 * @author kebuac
 */
public class ClientDAOImplem implements ClientDAO {

    /**
     * Method to get all clients from Oracle database
     *
     * @param connection Database connection
     * @return List with all registered clients on database
     */
    @Override
    public ArrayList<STRUCT> showAllClients(Connection connection) {
        ArrayList<STRUCT> clients = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {
            String clientsQuery = "SELECT VALUE(c) FROM clients_table c";
            ResultSet resultSet = statement.executeQuery(clientsQuery);

            //Getting results
            while (resultSet.next()) {
                clients.add((STRUCT) resultSet.getObject(1));
            }

        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
            System.out.println("Database state: " + ex.getSQLState());
            System.out.println("Error code: " + ex.getErrorCode());
        }

        return clients;
    }

    /**
     * Method to add new client into Oracle database
     *
     * @param client Client to insert
     * @param connection Database connection
     * @return Successful or not
     */
    @Override
    public boolean addClient(STRUCT client, Connection connection) {
        String insertQuery = "INSERT INTO clients_table VALUES (?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setObject(1, client);

            if (preparedStatement.executeUpdate() != 1) {
                System.out.println("Error inserting client " + client);
                return false;
            }

        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
            System.out.println("Database state: " + ex.getSQLState());
            System.out.println("Error code: " + ex.getErrorCode());
        }

        System.out.println("Client " + client + "was inserted correctly!");
        return true;
    }

    /**
     * Method to list phones of a client by his CIF
     * @param cif Client's CIF
     * @param connection Connection to database
     * @return Array with phones of an specific client
     */
    @Override
    public ARRAY listPhones(String cif, Connection connection) {
        oracle.sql.ARRAY phones = null;

        try (PreparedStatement stmt = connection.prepareStatement("SELECT c.phones" + "FROM clientes_table c WHERE c.cif=?")) {
            stmt.setString(1, cif);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                phones = (oracle.sql.ARRAY) rs.getArray(1);

            }

        } catch (SQLException es) {
            System.out.println("Database error: " + es.getMessage());
            System.out.println("Database state: " + es.getSQLState());
            System.out.println("Error code: " + es.getErrorCode());
        }

        return phones;
    }

    /**
     * Method to update a client's phone
     *
     * @param cif Client's cif to filter phones
     * @param connection Database connection
     */
    @Override
    public void updateClientPhone(String cif, Connection connection) {

        oracle.sql.ARRAY array = this.listPhones(cif, connection);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        if (array == null) {
            System.out.println("No phones found for client " + cif + " Please add new phone below: ");

            try (PreparedStatement stmt = connection.prepareStatement("UPDATE" + "clients_table c SET c.phones=phones_nt(?) where c.cif=?")) {
                String number = stdin.readLine();

                stmt.setObject(1, inputPhone(number, connection));
                stmt.setString(2, cif);

                if (stmt.executeUpdate() != 1) {
                    System.out.println("Error adding new phone to client " + cif);
                }
                else{System.out.println("ACTION COMPLETED SUCCESFULLY");}
            } catch (SQLException es) {
                System.out.println("Database error: " + es.getMessage());
                System.out.println("Database state: " + es.getSQLState());
                System.out.println("Error code: " + es.getErrorCode());

            } catch (IOException ex) {

                System.out.println("Error writing new phone");
            }
        } else {

            try (PreparedStatement stmt = connection.prepareStatement("UPDATE" + "TABLE(SELECT PHONES FROM CLIENTS_TABLE WHERE CIF=?) p SET VALUE(p) = ? WHERE p.phone_number= ?")) {
                int i;
                System.out.println("Client " + cif + " phone numbers");
                Object[] phones = (Object[]) array.getArray();
               
                for (i = 0; i < array.length(); i++) {
                    oracle.sql.STRUCT struct = (oracle.sql.STRUCT) phones[i];
                    Object[] structAttributes = struct.getAttributes();
                    if (structAttributes != null) {
                        System.out.println("Phone " + i + " number: " + structAttributes[0]);
                    }
                }
                
                int opcion;
                String input;
                
                do{
                System.out.println("What phone you want to modify?");
                input = stdin.readLine();
                opcion = Integer.parseInt(input);
                System.out.println("Insert new phone below: ");
                input = stdin.readLine();
                
                }
                while(opcion!=1||opcion!=2||opcion!=3);
                
                oracle.sql.STRUCT structnew = inputPhone(input,connection);
                oracle.sql.STRUCT structold = (oracle.sql.STRUCT) phones[opcion];
                Object[] structAttributes = structold.getAttributes();
                String phoneold = (String) structAttributes[0];
                    
               
                stmt.setString(1, cif);
    
                stmt.setObject(2, structnew);
                
                stmt.setString(3, phoneold);
              
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Error UPDATING new phone to client " + cif);
                }
                else{System.out.println("ACTION COMPLETED SUCCESFULLY");}

            } catch (SQLException es) {
                System.out.println("Database error: " + es.getMessage());
                System.out.println("Database state: " + es.getSQLState());
                System.out.println("Error code: " + es.getErrorCode());
            } catch (IOException ex) {
                System.out.println("Error writing new phone");
            }
        }

    }

    /**
     * Method to add new phone to client
     * @param number Phone's number
     * @param connection Database connection
     * @return New phone's number to insert
     * @throws SQLException 
     */
    private static oracle.sql.STRUCT inputPhone(String number, Connection connection) throws SQLException {

        StructDescriptor structDescriptor = StructDescriptor.createDescriptor("phone_t", connection);

        Object[] attributes = new Object[]{number};
        oracle.sql.STRUCT object = new oracle.sql.STRUCT(structDescriptor, connection, attributes);
        return object;
    }

    /**
     * Method to update a client's discount
     *
     * @param cif Client's cif to filter
     * @param discountPercentage Percentage of discount that's going to be
     * applied
     * @param connection Database connection
     * @return Update successful or not
     */
    @Override
    public boolean updateClientDiscount(String cif, BigDecimal discountPercentage, Connection connection) {
        try (PreparedStatement stmt = connection.prepareStatement("UPDATE clients_table c SET c.discount = ? where c.cif = ?")) {

            stmt.setBigDecimal(1, discountPercentage);

            stmt.setString(2, cif);

            if (stmt.executeUpdate() != 1) {
                return false;

            }else {
                return true;
            }

        } catch (SQLException es) {
            System.out.println("Database error: " + es.getMessage());
            System.out.println("Database state: " + es.getSQLState());
            System.out.println("Error code: " + es.getErrorCode());
            return false;
        }

    }
}
