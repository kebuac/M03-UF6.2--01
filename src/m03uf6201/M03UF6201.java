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

    public static void main(String[] args) {

        oracle.sql.STRUCT client;
        String cif, aux;
        Float discount;

        try {

            ClientDAOFactory daofactory = new ClientDAOFactory();

            ClientDAOImplem clientDaoImplem = daofactory.createClientDAO();

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
                        cif = validarString(stdin);

                        clientDaoImplem.updateClientPhone(input, con);
                        break;
                    case 4:
                        System.out.println("Introduce cif para el cliente");
                        cif = validarString(stdin);
                        System.out.println("Introduce nuevo descuento para el cliente");
                        aux = stdin.readLine();
                        discount = validarFloat(stdin);
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

    private static oracle.sql.STRUCT inputClient(BufferedReader stdin, Connection con) throws IOException, SQLException {
        String cif, name, surnames, street, town, postalcode, province;
        Float discount;
        String[] phones = new String[0];
        String objectName = "CLIENTE_T";

        System.out.println("Introduce el cif del cliente");

        cif = validarString(stdin);
        System.out.println("Introduce el nombre del cliente");

        name = validarString(stdin);
        System.out.println("Introduce el name del cliente");

        surnames = validarString(stdin);
        System.out.println("Introduce el surnames del cliente");

        street = validarString(stdin);
        System.out.println("Introduce el street del cliente");

        town = validarString(stdin);
        System.out.println("Introduce el town del cliente");

        postalcode = validarString(stdin);
        System.out.println("Introduce el postalcode del cliente");

        province = validarString(stdin);
        //
        System.out.println("Introduce el descuento del cliente");
        discount = validarFloat(stdin);

        System.out.println("¿Cuántos números quieres introducir? 1, 2 o 3");
        String input = stdin.readLine();

        int auxNums = Integer.parseInt(input);

        if (auxNums == 1 || auxNums == 2 || auxNums == 3) {

            for (int i = 0; i < auxNums; i++) {
                System.out.println("Introduce número del cliente");

                String aux = "" + validarInt(stdin);

                phones[i] = aux;
            }

        } else {

            phones[0] = null;

        }
        
        StructDescriptor structDescriptor = StructDescriptor.createDescriptor(objectName, con);
        Object[] attributes = new Object[]{cif, name, surnames, street, town, postalcode, province, discount, phones};
        oracle.sql.STRUCT object = new oracle.sql.STRUCT(structDescriptor, con, attributes);
        return object;
    }

    public static String validarString(BufferedReader stdin) {

        boolean validacion = false;
        String input = null;
        do {

            try {

                input = stdin.readLine();

                if (input.matches("[a-z]*")) {

                    validacion = true;
                } else {

                    System.out.println("Argumento no valido, introduza el dato nuevamente");
                }

            } catch (IOException ex) {

                System.out.println("Error al leer los datos");
            }

        } while (validacion = false);

        return input;

    }

    public static float validarFloat(BufferedReader stdin) {

        boolean validacion = false;
        String input = null;
        float flo = 0;
        float flocompro = (float) -0.000001;
        do {

            try {

                input = stdin.readLine();

                if (input.matches("[0-9]*")) {

                    flo = Float.parseFloat(input);
                    validacion = true;
                } else if (input.matches("[0-9]+.[0-9]*")) {

                    flo = Float.parseFloat(input);
                    validacion = true;
                } else {

                    System.out.println("Argumento no valido, introduza el dato nuevamente");
                }

            } catch (IOException ex) {

                System.out.println("Error al leer los datos");
            }

        } while (validacion = false && flo > flocompro);

        return flo;

    }

    public static int validarInt(BufferedReader stdin) {

        boolean validacion = false;
        String input = null;
        int inte = 0;
        int flocompro = (int) -0.000001;
        do {

            try {

                input = stdin.readLine();

                if (input.matches("[0-9]*")) {

                    inte = Integer.parseInt(input);
                    validacion = true;
                } else {

                    System.out.println("Argumento no valido, introduza el dato nuevamente");
                }

            } catch (IOException ex) {

                System.out.println(ex + " Error al leer los datos");
            }

        } while (validacion = false && inte > flocompro);

        return inte;

    }

}
