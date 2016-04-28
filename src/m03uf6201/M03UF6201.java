package m03uf6201;

import dao.ClientDAO;
import dao.ClientDAOFactory;
import dao.ClientDAOImplem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp.Factory;
import oracle.sql.ARRAY;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import singleton.ConnectionInformation;
import singleton.DatabaseConnection;

public class M03UF6201 {

 
    public static void main(String[] args){
        
        oracle.sql.STRUCT client;
        String cif,aux;
        Float discount;
      
        
        try {
            
       ClientDAOFactory daofactory = new ClientDAOFactory();
       
       ClientDAOImplem clientDaoImplem =  daofactory.createClientDAO();
       
      
       Connection con = DatabaseConnection.getInstance();

       
       
            int opcion;

            
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

            do {
               
            System.out.println("Pulse 1 para ver clientes." + "\n" + "Pulse 2 para dar de alta clientes." + "\n" + "Pulse 3 para actualizar numero de cliente." + "\n" + "Pulse 4 para ver actualizar descuento de cliente." + "\n" + "Pulse 5 para salir del programa" + "\n");
            String input = stdin.readLine();
            opcion = Integer.parseInt(input);
            
                switch (opcion) {

                    case 1:
                        
                            clientDaoImplem.showAllClients(con);

                        break;

                    case 2:
                   client = inputClient(stdin, con);
                   clientDaoImplem.addClient(client, con);
                        break;
                    case 3:
                        System.out.println("Introduce cif para el cliente");
                        cif = stdin.readLine();

                        clientDaoImplem.updateClientPhone(input, con);
                        break;
                    case 4:
                        System.out.println("Introduce cif para el cliente");
                        cif = stdin.readLine();
                        System.out.println("Introduce nuevo descuento para el cliente");
                        aux = stdin.readLine();
                        discount = Float.parseFloat(aux);
                        clientDaoImplem.updateClientDiscount(cif, discount, con);
                        
                        break;
                    case 5:

                        System.out.println("Gracias por utilizar el programa");

                         {
                            try {
                                
                                stdin.close();
                                
                            } catch (IOException ex) {
                                System.out.println("Error" + ex);
                            }
                        }

                        break;
                }

            } while (opcion != 5);
        
        } catch (IOException ex) {

            System.out.println("Error" + ex);

        } catch (SQLException ex) {
            Logger.getLogger(M03UF6201.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private static oracle.sql.STRUCT inputClient(BufferedReader stdin, Connection con) throws IOException, SQLException{
    String cif,name,surnames,street,town,postalcode,province,aux;
    Float discount;
    String[] phones= new String[0];
    String objectName = "CLIENTE_T";
    cif = stdin.readLine();
    name = stdin.readLine();
    surnames = stdin.readLine();
    street = stdin.readLine();
    town = stdin.readLine();
    postalcode = stdin.readLine();
    province = stdin.readLine();
    aux = stdin.readLine();
    discount = Float.parseFloat(aux);
    phones[0]=stdin.readLine();
    StructDescriptor structDescriptor = StructDescriptor.createDescriptor(objectName, con);
    Object[] attributes = new Object[]{cif,name,surnames,street,town,postalcode,province,discount,phones};
    oracle.sql.STRUCT object = new oracle.sql.STRUCT(structDescriptor, con, attributes);
    return object;
    }

}

    
    

