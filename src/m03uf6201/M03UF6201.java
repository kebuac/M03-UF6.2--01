package m03uf6201;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class M03UF6201 {

 
    public static void main(String[] args) {
        
        try {
            
            int opcion;

            
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

            do {
               
            System.out.println("Pulse 1 para ver clientes." + "\n" + "Pulse 2 para dar de alta clientes." + "\n" + "Pulse 3 para actualizar numero de cliente." + "\n" + "Pulse 4 para ver actualizar descuento de cliente." + "\n" + "Pulse 5 para salir del programa" + "\n");
            String input = stdin.readLine();
            opcion = Integer.parseInt(input);
            
                switch (opcion) {

                    case 1:
                     
                        break;

                    case 2:
                        
                   
                        break;
                    case 3:
                        
                        break;
                    case 4:
                        
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

        }

    }
    

}
    
    
    

