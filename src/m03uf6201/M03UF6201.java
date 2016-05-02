package m03uf6201;

import dao.ClientDAOFactory;
import dao.ClientDAOImplem;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import singleton.DatabaseConnection;

public class M03UF6201 {

    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        oracle.sql.STRUCT client;
        ArrayList<oracle.sql.STRUCT> clients;

        try {

            ClientDAOFactory daofactory = new ClientDAOFactory();

            ClientDAOImplem clientDaoImplem = daofactory.createClientDAO();

            Connection con = DatabaseConnection.getInstance();

            int opcion;

            do {
                //MENU
                System.out.println("1.- See all clients."
                        + "\n2.- Insert new clients."
                        + "\n3.- Update a client's phone."
                        + "\n4.- Update a client's discount."
                        + "\n5.- Exit.\n");
                String input = scan.next();
                opcion = Integer.parseInt(input);

                switch (opcion) {
                    case 1:
                        clients = clientDaoImplem.showAllClients(con);

                        if (clients.size() > 0) {
                            for (STRUCT clientToShow : clients) {
                                Object[] clientsValues = clientToShow.getAttributes();
                                String cif = (String) clientsValues[0];
                                String name = (String) clientsValues[1];
                                String surname = (String) clientsValues[2];
                                BigDecimal discount = (BigDecimal) clientsValues[7];

                                System.out.println("Client " + name + "\n"
                                        + "CIF: " + cif + "\n"
                                        + "Surname: " + surname + "\n"
                                        + "Discount: " + discount + "\n");
                            }
                        } else {
                            System.out.println("There is no clients on database!\n");
                        }

                        break;
                    case 2:
                        client = inputClient(scan, con);
                        clientDaoImplem.addClient(client, con);
                        break;
                    case 3:
                        System.out.println("Type a client's CIF: ");
                        String cif = scan.next();

                        clientDaoImplem.updateClientPhone(input, con);
                        break;
                    case 4:
                        System.out.println("Type a client's CIF: ");
                        String updateCif = scan.next();

                        System.out.println("Type a new discount to apply: ");
                        BigDecimal discount = scan.nextBigDecimal();

                        boolean update = clientDaoImplem.updateClientDiscount(updateCif, discount, con);

                        if (update) {
                            System.out.println("New discount applied to client " + updateCif + " correctly!\n");
                        } else {
                            System.out.println("Error updating discount of client with CIF " + updateCif);
                        }

                        break;
                    case 5:
                        System.out.println("Thank you. See you soon.");

                        scan.close();
                        break;
                }

            } while (opcion != 5);

        } catch (IOException ex) {
            System.out.println("Error" + ex);
        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
            System.out.println("Database state: " + ex.getSQLState());
            System.out.println("Error code: " + ex.getErrorCode());
        }

    }

    private static oracle.sql.STRUCT inputClient(Scanner scan, Connection con) throws IOException, SQLException {
        String cif, name, surname, street, town, postalcode, province;
        BigDecimal discount;
        String[] phones = new String[3];
        String objectName = "CLIENT_T";

        System.out.println("Type a new client's CIF: ");
        cif = scan.next();

        System.out.println("Type a new client's name: ");
        name = scan.next();

        System.out.println("Type a new client's surname: ");
        surname = scan.next();

        System.out.println("Type a new client's street: ");
        street = scan.next();

        System.out.println("Type a new client's town: ");
        town = scan.next();

        System.out.println("Type a new client's postal code: ");
        postalcode = scan.next();

        System.out.println("Type a new client's province: ");
        province = scan.next();

        System.out.println("Type a new client's discount: ");
        discount = scan.nextBigDecimal();

        System.out.println("How many phones you want to enter? 1, 2 or 3");
        int input = scan.nextInt();

        int auxNums = input;

        if (auxNums == 1 || auxNums == 2 || auxNums == 3) {

            for (int i = 0; i < auxNums; i++) {
                System.out.println("Type a new client's phone: ");
                String aux = scan.next();

                phones[i] = aux;
            }

        } else {

            phones[0] = null;

        }

        oracle.sql.ArrayDescriptor arrayDescriptor = oracle.sql.ArrayDescriptor.createDescriptor("PHONES_T", con);
        oracle.sql.ARRAY phonesArray = new oracle.sql.ARRAY(arrayDescriptor, con, phones);
        StructDescriptor structDescriptor = StructDescriptor.createDescriptor(objectName, con);
        
        Object[] attributes = new Object[]{cif, name, surname, street, town, postalcode, province, discount, phonesArray};
        
        oracle.sql.STRUCT object = new oracle.sql.STRUCT(structDescriptor, con, attributes);

        return object;
    }
    /*
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
        float flocompro = (float) - 0.000001;
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

                System.out.println(ex + "Error al leer los datos");
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

    }*/

}
