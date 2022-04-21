package poeta;

import java.util.ArrayList;
import java.util.HashMap;

public class Poeta {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(System.getProperty("user.dir"));

		String diccionarioSilábico = "src/main/resources/silabas.txt";
		// TODO: Verificar que exista. Esta verificación debe ir en su propio método en
		// otra parte.
		// TODO: Esta variable tiene que ir en un archivo de configuración, no aquí.

		/*
		 * Aproximación inicial: Simplemente frases que tengan once sílabas.
		 * 
		 * Preparación: - Separar las palabras en HashSets de acuerdo al número de
		 * sílabas. - Hacer arreglo A con cantidad de palabras con 1, 1y2, 1y2y3...
		 * numMaxDeSilabasEnUnaPalabra sílabas. Pasos: contador = 11. mientras no tenga
		 * 8 versos mientras (true): Saque un número del 1 al
		 * A[numMaxDeSilabasEnUnaPalabra] Tomar una palabra al azar del número de
		 * sílabas correspondiente. Restar número de sílabas de la palabra tomada, del
		 * contador. Si contador es igual a cero, termine. Quite de A los grupos que
		 * tienen más de 11-numMaxDeSilabasEnUnaPalabra sílabas.
		 * 
		 */

//		ArrayList<ArrayList<String>> palabras = Utilidades.hacerArreglo(diccionarioSilábico);

		Utilidades.hacerArregloYPalabrasQueRiman(diccionarioSilábico);
		
		ArrayList<String> estrofa = Utilidades.hacerOctavaReal();
		//ArrayList<ArrayList<String>> palabras = Utilidades.hacerArregloyPalabrasQueRiman(diccionarioSilábico);
		
		
		// HashMap <String, ArrayList <String>> palabrasPorTerminación = Utilidades.hacerPalabrasPorTerminación (diccionarioSilábico);
		
//		for (int i = 0; i < 8; i++) {
//			// Hagamos un "verso".
//			String verso = Utilidades.hacerVerso(palabras);
//			System.out.println(verso);
//		}
		//
		
		for ( int i = 0; i < estrofa.size();i++) {
			System.out.println (estrofa.get(i));
		}
	}

}
