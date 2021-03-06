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
                System.out.println("Error inserting client");
                return false;
            }

        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
            System.out.println("Database state: " + ex.getSQLState());
            System.out.println("Error code: " + ex.getErrorCode());
        }

        System.out.println("Client was inserted correctly!");
        return true;
    }

    /**
     * Method to list phones of a client by his CIF
     *
     * @param cif Client's CIF
     * @param connection Connection to database
     * @return Array with phones of an specific client
     */
    @Override
    public ARRAY listPhones(String cif, Connection connection) {
        oracle.sql.ARRAY phones = null;

        String query = "SELECT c.phones FROM clients_table c WHERE c.cif=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, cif);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

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
            System.out.println("No phones found for client " + cif + ". Please add new phone below: ");

            try (PreparedStatement stmt = connection.prepareStatement("UPDATE clients_table c SET c.phones=phones_t(?) WHERE c.cif=?")) {

                String numberNewPhone = stdin.readLine();

                stmt.setString(1, numberNewPhone);
                stmt.setString(2, cif);

                if (stmt.executeUpdate() != 1) {
                    System.out.println("Error updateing new phone of client with CIF" + cif);
                } else {
                    System.out.println("ACTION COMPLETED SUCCESFULLY");
                }
            } catch (SQLException es) {
                System.out.println("Database error: " + es.getMessage());
                System.out.println("Database state: " + es.getSQLState());
                System.out.println("Error code: " + es.getErrorCode());

            } catch (IOException ex) {
                System.out.println("I/O error: " +ex.getMessage());
            }
        } else {

            try (PreparedStatement stmt = connection.prepareStatement("UPDATE TABLE(SELECT PHONES FROM CLIENTS_TABLE WHERE CIF=?) p SET VALUE(p) = ? WHERE VALUE(p) = ?")) {
                int i;
                System.out.println("Client " + cif + " phone numbers");
                Object[] phones = (Object[]) array.getArray();

                for (i = 0; i < array.length(); i++) {
                    System.out.println("Phone " + i + " number: " + phones[i]);
                }

                String input, inputOldNumber;
                boolean check = false;
                
                do {
                    System.out.println("What phone you want to modify?");
                    inputOldNumber = stdin.readLine();

                    for(int j = 0;j < array.length();j++){
                        if(phones[j].equals(inputOldNumber)){
                            check = true;
                            break;
                        }
                    }
                    
                } while (check = false);

                System.out.println("Insert new phone below: ");
                input = stdin.readLine();

                stmt.setString(1, cif);
                stmt.setObject(2, input);
                stmt.setString(3, inputOldNumber);

                if (stmt.executeUpdate() != 1) {
                    System.out.println("Error updating new phone of client with CIF " + cif);
                } else {
                    System.out.println("Phone " +inputOldNumber+ " updated to " +input+ " correctly!");
                }

            } catch (SQLException es) {
                System.out.println("Database error: " + es.getMessage());
                System.out.println("Database state: " + es.getSQLState());
                System.out.println("Error code: " + es.getErrorCode());
            } catch (IOException ex) {
                System.out.println("I/O error: " +ex.getMessage());
            }
        }

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
            } else {
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
