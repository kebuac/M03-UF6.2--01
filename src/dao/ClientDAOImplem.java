package dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            for (STRUCT client : clients) {
                Object[] clientsValues = client.getAttributes();
                String cif = (String) clientsValues[0];
                String name = (String) clientsValues[1];
                String surname = (String) clientsValues[2];
                float discount = (float) clientsValues[7];

                System.out.println("Client: \n"
                        + "CIF: " + cif + "\n"
                        + "Name:`" + name + "\n"
                        + "Surname: " + surname + "\n"
                        + "Discount: " + discount);
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
        String insertQuery = "INSERT INTO clientes_table VALUES (?)";

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

    @Override
    public ARRAY list(String cif, Connection connection) {
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
     * @return Successful or not
     */
    @Override
    public void updateClientPhone(String cif, Connection connection) {

        oracle.sql.ARRAY array = this.list(cif, connection);
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        if (array == null) {
            System.out.println("No phones found for client " + cif + " Please add new phone below: ");

            try (PreparedStatement stmt = connection.prepareStatement("UPDATE" + "clientes_table c SET c.phones=phones_nt(?) where c.cif=?")) {
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

            try (PreparedStatement stmt = connection.prepareStatement("UPDATE" + "TABLE(SELECT PHONES FROM CLIENES_TABLE WHERE CIF=?) p SET VALUE(p) = ? WHERE p.phone_number= ?")) {
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
                System.out.println("Que telefono quieres modificar?");
                input = stdin.readLine();
                opcion = Integer.parseInt(input);
                System.out.println("Introducte el nuevo telefono: ");
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

    private static oracle.sql.STRUCT inputPhone(String number, Connection connection) throws SQLException {

        StructDescriptor structDescriptor = StructDescriptor.createDescriptor("phone_t", connection);

        Object[] attributes = new Object[]{number};
        oracle.sql.STRUCT object = new oracle.sql.STRUCT(structDescriptor, connection, attributes);
        return object;
    }

    /**
     * Method to update a client's discount
     *
     * @param cif Client's cif to filter phones
     * @param discountPercentage Percentage of discount that's going to be
     * applied
     * @param connection Database connection
     * @return Successful or not
     */
    @Override
    public boolean updateClientDiscount(String cif, float discountPercentage, Connection connection) {
        
try (PreparedStatement stmt = connection.prepareStatement("UPDATE" + "clientes_table c SET c.descuento = ? where c.cif = ?")) {
       

                stmt.setFloat(1, discountPercentage);
    
                stmt.setString(2, cif);
                
                if (stmt.executeUpdate() != 1) {
                    System.out.println("Error updating discount to client " + cif);
                    return false;
 
                }
                else{
                    
                System.out.println("ACTION COMPLETED SUCCESFULLY");
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
