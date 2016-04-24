package dao;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Interface with all methods which will need User
 * @author kebuac
 */
public interface ClientDAO {
    public ArrayList<oracle.sql.STRUCT> showAllClients(Connection connection);
    public boolean addClient(oracle.sql.STRUCT client, Connection connection);
    public boolean updateClientPhone(oracle.sql.STRUCT client, Connection connection);
    public boolean updateClientDiscount(oracle.sql.STRUCT client, Connection connection);
}
