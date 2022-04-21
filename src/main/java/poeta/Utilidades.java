package poeta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Utilidades {

	private static final int SILABAS_POR_VERSO = 11; // TODO: No debe ir acá.
	private static final int MAX_SILABAS_EN_UNA_PALABRA = 20; // TODO: Esta constante no debería ser definida acá. Tal
																// vez ni es necesaria.
	private static final Set<Character> vocales = new HashSet<>(
			Arrays.asList('a', 'e', 'i', 'o', 'u', 'á', 'é', 'í', 'ó', 'ú', 'ü'));

	private static final Set<Character> vocalesNYS = new HashSet<>(
			Arrays.asList('a', 'e', 'i', 'o', 'u', 'á', 'é', 'í', 'ó', 'ú', 'ü', 'n', 's'));

	private static final Set<Character> vocalesAcentuadas = new HashSet<>(Arrays.asList('á', 'é', 'í', 'ó', 'ú'));

	// Probablemente la ü no es necesaria acá:
	private static final Set<Character> vocalesNoAcentuadas = new HashSet<>(
			Arrays.asList('a', 'e', 'i', 'o', 'u', 'ü'));
	private static final Set<Character> vocalesNoAcentuadasAbiertas = new HashSet<>(Arrays.asList('a', 'e', 'o'));

	// Probablemente la ü no es necesaria acá:
	private static final Set<Character> vocalesNoAcentuadasCerradas = new HashSet<>(Arrays.asList('i', 'u', 'ü'));

	private static ArrayList<ArrayList<String>> palabrasPorNúmeroDeSílabas = new ArrayList<ArrayList<String>>();
	private static HashMap<String, ArrayList<String>> palabrasPorTerminación = new HashMap<String, ArrayList<String>>();

	/**
	 * 
	 * Este método crea dos cosas: - Un ArrayList<ArrayList<String>> con todas las
	 * palabras que tienen la misma cantidad de sílabas. - Un HashMap de ArrayList
	 * con todas las palabras que tienen la misma terminación.
	 * 
	 * Estos dos valores son globales, luego este método no retorna nada.
	 * 
	 * @param diccionarioSilábico
	 * @return
	 */

	public static void hacerArregloYPalabrasQueRiman(String diccionarioSilábico) {

		for (int i = 0; i < MAX_SILABAS_EN_UNA_PALABRA; i++) {
			palabrasPorNúmeroDeSílabas.add(new ArrayList<String>());
		}

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(diccionarioSilábico));
			String palabra = reader.readLine();
			while (palabra != null) {
				// System.out.println(palabra);

				// Procesar palabra
				int cuántasSílabas = contarSílabas(palabra);
				palabrasPorNúmeroDeSílabas.get(cuántasSílabas - 1).add(palabra);

				// Procesar terminación.
				colocarPalabraPorTerminación(palabra);


				//System.out.println(palabra);
				
				// read next line
				palabra = reader.readLine();
				

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void colocarPalabraPorTerminación(String palabra) {

		String terminación = averiguarTerminación(palabra); // TODO: CORREGIR: Los monosílabos terminados en vocal, n o
															// s deben rimar con las palabras acentuadas.
		// System.out.println(palabra + " " + terminación);

		if (!palabrasPorTerminación.containsKey(terminación)) {
			palabrasPorTerminación.put(terminación, (new ArrayList<String>()));
		}
		palabrasPorTerminación.get(terminación).add(palabra);
	}

	private static String averiguarTerminación(String palabra) {

		// ¡A averiguar la acentuación!
		// Si hay tilde, no tenemos que pensar.
		for (int i = 0; i < palabra.length(); i++) {
			char caracter = palabra.charAt(i);
			if (vocalesAcentuadas.contains(caracter)) {
				// ¡Estamos de buenas!
				// Sin embargo...
				// Hay palabras que llevan acento por razón de hiato.
				// En esos casos, deben rimar con las otras que no tienen tilde.
				// Por ejempli: vil, infantil y monorraíl deben rimar.
				// País, bis y sentís deben rimar.
				// Debe ser suficiente con quitar la tilde inicial,
				// porque se sabe que la primera vocal es la que lleva el acento.

				// Cambie la vocal acentuada a no acentuada.
				char vocalNoAcentuada = desacentuar(caracter);
				return vocalNoAcentuada + palabra.substring(i + 1);
			}
		}

		// No hay tildes. Toca pensar.
		// Diría que por lo menos sabemos que no es esdrújula...
		// a menos que sea uno de esos adverbios terminados en mente. Uf.
		// TODO: Los adverbios terminados en mente tienen acento en la penúltima "e", no
		// en donde
		// fuere la tilde (por ejemplo, rápidamente debe rimar con mente). <- Hacer esto
		// después. Debe ser muy fácil.
		//

		// Entonces.

		// Separar palabra en sílabas.
		String[] sílabas = palabra.split("-");

		// Si tiene sólo una sílaba, fácil. De la última vocal pa' alante?
		// No: Para FUI es I
		// Para DEIS es EIS.
		// Para FIAR es AR
		// Parece ser:
		// De la vocal abierta para adelante (a lo más hay una, creo).
		// Si no, de la última cerrada para adelante.

		if (sílabas.length == 1) {
			String sílaba = sílabas[0];

			int índiceEnSílaba = primeraAbiertaOÚltimaCerrada(sílaba);

			return sílaba.substring(índiceEnSílaba);
		}

		// OK, ahora para el caso en que la palabra tenga dos o más sílabas.
		// El acento estará en la primera o en la segunda.
		// Miremos si termina en vocal, n o s.
		// Ejemplo: Parto.

		if (vocalesNYS.contains(palabra.charAt(palabra.length() - 1))) {
			// El acento es la primera abierta o la última cerrada de la segunda sílaba.
			String sílaba = sílabas[sílabas.length - 2];

			int índiceEnSílaba = primeraAbiertaOÚltimaCerrada(sílaba);
			return sílaba.substring(índiceEnSílaba) + "-" + sílabas[sílabas.length - 1];
		}

		// Bueno, pues nada. Termina en otra cosa y es aguda.
		// Ejemplo: Mordaz.
		// La misma cosa que cuando tiene sólo una sílaba.
		// TODO: Pegar estos dos casos.

		String sílaba = sílabas[sílabas.length - 1];

		int índiceEnSílaba = primeraAbiertaOÚltimaCerrada(sílaba);
		return sílaba.substring(índiceEnSílaba);
	}

	private static char desacentuar(char caracter) {
		String sera = Normalizer.normalize(caracter + "", Normalizer.Form.NFD);
		return sera.charAt(0);

	}

	private static int primeraAbiertaOÚltimaCerrada(String sílaba) {

		int result = 0;
		for (int i = 0; i < sílaba.length(); i++) {
			char caracter = sílaba.charAt(i);
			if (vocalesNoAcentuadasAbiertas.contains(caracter)) {
				return i;
			} else if (vocalesNoAcentuadasCerradas.contains(caracter)) {
				result = i;
			}
		}
		return result;
	}

	public static ArrayList<ArrayList<String>> hacerArreglo(String diccionarioSilábico) {

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < MAX_SILABAS_EN_UNA_PALABRA; i++) {
			result.add(new ArrayList<String>());
		}

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(diccionarioSilábico));
			String palabra = reader.readLine();
			while (palabra != null) {
				// System.out.println(palabra);

				// Procesar línea
				int cuántasSílabas = contarSílabas(palabra);
				result.get(cuántasSílabas - 1).add(palabra);

				// read next line
				palabra = reader.readLine();

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static int contarSílabas(String palabra) {
		// TODO Auto-generated method stub
		int result = 1;
		for (int i = 0; i < palabra.length(); i++) {
			if (palabra.charAt(i) == '-') {
				result++;
			}
		}
		return result;
	}

	public static String hacerVersoSinSinalefa(ArrayList<ArrayList<String>> palabras) {
		int sílabasRestantes = SILABAS_POR_VERSO;

		String result = "";

		Random rand = new Random(); // instance of random class

		while (sílabasRestantes > 0) {
			int límiteSuperior = contarPalabrasConMenosDeTantasSílabas(palabras, sílabasRestantes);

			int índiceDePalabra = rand.nextInt(límiteSuperior); // TODO: Aunque no es de verdad un índice porque, por
																// ahora, la palabra será sacada al azar del subconjunto
																// respectivo. ¿Cambiar de HashSet a ArrayList?

			String palabra = agarrarPalabra(palabras, índiceDePalabra);

			result = result + " " + palabra;

			sílabasRestantes -= contarSílabas(palabra);
		}
		return result;
	}

	private static String agarrarPalabra(ArrayList<ArrayList<String>> palabras, int índiceDePalabra) {

		int quedan = índiceDePalabra;
		int i = 0;
		int númeroDePalabrasConTalCantidadDeSílabas = 0;
		while (quedan >= 0) {
			númeroDePalabrasConTalCantidadDeSílabas = palabras.get(i).size();
			quedan -= númeroDePalabrasConTalCantidadDeSílabas;
			i++;
		}

		// Ahora, agarrar la palabra.
		return palabras.get(i - 1).get(quedan + númeroDePalabrasConTalCantidadDeSílabas);
	}

	private static int contarPalabrasConMenosDeTantasSílabas(ArrayList<ArrayList<String>> palabras,
			int sílabasRestantes) {

		int result = 0;
		for (int i = 0; i < sílabasRestantes; i++) {
			result += palabras.get(i).size();
		}
		return result;
	}

	public static String hacerVerso(int sílabasRestantes) {
		return hacerVerso(sílabasRestantes, false);
	}

	private static String hacerVerso(int sílabasRestantes, boolean posibleSinalefaFinal) {

		ArrayList<ArrayList<String>> palabras = palabrasPorNúmeroDeSílabas;
		String result = "";

		Random rand = new Random(); // instance of random class

		String palabra = "";
		String palabraAnterior = "x";

		while (sílabasRestantes > 0) {
			int límiteSuperior = contarPalabrasConMenosDeTantasSílabas(palabras, sílabasRestantes);

			int índiceDePalabra = rand.nextInt(límiteSuperior);

			palabra = agarrarPalabra(palabras, índiceDePalabra);

			// ¡Siempre vamos a hacer sinalefa!
			if (terminaEnVocalOY(palabraAnterior) && empiezaEnVocalOH(palabra)) {
				sílabasRestantes++;
			}

			char caracterFinal = palabra.charAt(palabra.length() - 1);
			
			
//			// TODO: Necesito corregir el problema de la sinalefa con la palabra final.
//			// Lastimosamente, no alcancé a solucionarlo para el Hackathon.
//			
//			if (!(posibleSinalefaFinal && sílabasRestantes - contarSílabas(palabra) == 0
//					&& (vocales.contains(caracterFinal) || caracterFinal == 'y'))) {
//				// Acá terminamos con una vocal cuando la palabra final del verso comienza por
//				// vocal o h. Eso no está bien.
//				// Por ahora, sólo voy a sacar otra palabra que termine en consonante.
//				// Es decir, mi programa, POR AHORA, no va a tener sinalefa con la última
//				// palabra.
//				// <- TODO: Mejorar esto.

				result = result + " " + palabra;
				sílabasRestantes -= contarSílabas(palabra);

				palabraAnterior = palabra;
//			}
//			else {
//				System.out.println ("Wat");
//			}
		}

		return result;
	}

	private static boolean empiezaEnVocalOH(String palabra) {
		return esVocal(palabra.charAt(0)) || palabra.charAt(0) == 'h';
	}

	/**
	 * @param palabra
	 * @return
	 */
	private static boolean terminaEnVocalOY(String palabra) {
		return esVocal(palabra.charAt(palabra.length() - 1)) || palabra.charAt(palabra.length() - 1) == 'y';
	}

	private static boolean esVocal(char caracter) {
		return vocales.contains(caracter);
	}

	public static ArrayList<String> hacerOctavaReal() {
		// OK. Primero que todo, busquemos tres terminaciones que sean diferentes
		// y que tengan por lo menos tres palabras las dos primeras, y dos la tercera.

		ArrayList<String> result = new ArrayList<String>();

		String[] terminaciones = sacarTerminacionesParaOctavaReal();

		// Hallemos las palabras finales para la octava real.

		String[] arregloA = hallarPalabrasQueRimenCon(3, terminaciones[0]);
		String[] arregloB = hallarPalabrasQueRimenCon(3, terminaciones[1]);
		String[] arregloC = hallarPalabrasQueRimenCon(2, terminaciones[2]);

		String[] palabrasFinales = { arregloA[0], arregloB[0], arregloA[1], arregloB[1], arregloA[2], arregloB[2],
				arregloC[0], arregloC[1] };

		// Esto es para saber si hay que cambiar el número de sílabas de cada verso
		// de acuerdo al hecho de que la terminación sea aguda (-1), grave, esdrújula
		// (+1) o sobreesdrújula (+2)
		int sílabasA = SILABAS_POR_VERSO + (contarSílabas(terminaciones[0]) - 2);
		int sílabasB = SILABAS_POR_VERSO + (contarSílabas(terminaciones[1]) - 2);
		int sílabasC = SILABAS_POR_VERSO + (contarSílabas(terminaciones[2]) - 2);

		// Ahora, hagamos el "poema":
		for (int i = 0; i < palabrasFinales.length; i++) {

			// Hagamos el primer verso.
			String palabraFinal = palabrasFinales[i];

			int numSílabas = sílabasC;
			if (i == 0 || i == 2 || i == 4) {
				numSílabas = sílabasA;
			} else if (i == 1 || i == 3 || i == 5) {
				numSílabas = sílabasB;
			}

			String verso = hacerVersoConPalabraFinal(palabraFinal, numSílabas);

//			
//			// Si la palabra final comienza en vocal o en h, démosle una oportunidad de que
//			// termine en vocal y haga sinalefa.
//
//			char caracterInicial = palabraFinal.charAt(0);
//			
//			// Pero si el caracter inicial es vocal y el último no lo es,
//			// toca solucionar el problema de la sinalefa:
//			
//			if ( (caracterInicial == 'h' || 
//					vocalesNoAcentuadasAbiertas.contains(desacentuar(caracterInicial)))){
//				// Quite la última palabra para hallar la vocal de la palabra anterior.
//				char últimaLetraDeLaPenúltimaPalabra =  verso.charAt(verso.lastIndexOf(" ") - 1);
//				
//				// TODO: Procese qué hacer si es vocal. ¿Simplemente buscar otra palabra que termine en consonante?
//				
//			}
//					
//					
//					

			String versoLimpio = limpiar(verso);
			result.add(versoLimpio);
		}

		return result;
	}

	private static String limpiar(String verso) {
		String result = "";
		for (int i = 0; i < verso.length(); i++) {
			if (verso.charAt(i) != '-')
				result = result + verso.charAt(i);
		}
		return result;
	}

	private static String hacerVersoConPalabraFinal(String palabraFinal, int totalSílabas) {

		int numSílabas = contarSílabas(palabraFinal);
		// ¿Comienza en vocal? Porque puede ser que hagamos sinalefa. <- TODO.

		char letraInicial = palabraFinal.charAt(0);
		String restoDelVerso = "";

		if (!vocales.contains(letraInicial) && letraInicial != 'h')
			restoDelVerso = hacerVerso(totalSílabas - numSílabas);
		else
			restoDelVerso = hacerVersoconPosibleSinalefaFinal(totalSílabas - numSílabas);

		return restoDelVerso + " " + palabraFinal;

	}

	private static String hacerVersoconPosibleSinalefaFinal(int sílabasRestantes) {
		return hacerVerso(sílabasRestantes, true);
	}

	private static String[] hallarPalabrasQueRimenCon(int cuántas, String terminación) {

		ArrayList<String> palabras = new ArrayList<String>(palabrasPorTerminación.get(terminación));
		Collections.shuffle(palabras);
		String[] result = new String[cuántas];
		for (int i = 0; i < cuántas; i++) {
			result[i] = palabras.get(i);
		}

		return result;
	}

	public static String[] sacarTerminacionesParaOctavaReal() {

		ArrayList<String> terminaciones = new ArrayList<String>(palabrasPorTerminación.keySet());
		Random r = new Random();

		int índiceA, índiceB, índiceC;
		String A, B, C;

		// Hallemos A.
		while (true) {
			índiceA = r.nextInt(terminaciones.size());
			A = terminaciones.get(índiceA);
			if (palabrasPorTerminación.get(A).size() >= 3)
				break;
		}

		// Hallemos B.
		while (true) {
			índiceB = r.nextInt(terminaciones.size());
			if (índiceA != índiceB) {
				B = terminaciones.get(índiceB);
				if (palabrasPorTerminación.get(B).size() >= 3)
					break;
			}
		}

		// Hallemos C.
		while (true) {
			índiceC = r.nextInt(terminaciones.size());
			if (índiceA != índiceC && índiceB != índiceC) {
				C = terminaciones.get(índiceC);
				if (palabrasPorTerminación.get(C).size() >= 2)
					break;
			}
		}

		String[] result = { A, B, C };
		return result;
	}

}
