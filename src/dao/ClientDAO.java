package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Interface with all methods which will need User
 * @author kebuac
 */
public interface ClientDAO {
    public ArrayList<oracle.sql.STRUCT> showAllClients(Connection connection);
    public boolean addClient(oracle.sql.STRUCT client, Connection connection);
    public void updateClientPhone(String cif, Connection connection);
    public boolean updateClientDiscount(String cif, BigDecimal discountPercentage, Connection connection);
    public oracle.sql.ARRAY listPhones (String cif, Connection connection);
}
