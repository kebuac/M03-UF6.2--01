package dao;

/**
 * Public class to bring access on ClientDAOImplem
 * @author kebuac
 */
public class ClientDAOFactory {
    
    //Create a new ClientDAOImplem
    public ClientDAOImplem createClientDAO(){
        return new ClientDAOImplem();
    }
}
