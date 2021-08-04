/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package menu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Utili {
	
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static FileWriter fw;
	static BufferedWriter bw;
	static long start_time;
	public static String new_line = System.getProperty("line.separator");  // https://stackoverflow.com/questions/36796136/difference-between-system-getpropertyline-separator-and-n
	
	public static String leggiString(String message) throws IOException {
		System.out.print(message);
		return in.readLine();
	}
	
	public static void scriviSuFile(String path, boolean append, String testo) throws IOException {
		inizializzaWriter(path, append);
		bw.append(testo);
		chiudiWriter();
	}
	
	public static void creaFile(String path) throws IOException {
		inizializzaWriter(path, false);
		chiudiWriter();
	}
	
	public static String inserisciTipologiaCentro(String message) throws IOException {
		while (true) {
			String tipologia = leggiString(message);
			switch (tipologia) {
			case "1":
				return "Ospedaliero";
			case "2":
				return "Aziendale";
			case "3":
				return "Hub";
			default:
				System.out.println("Scelta non valida, riprova");
				break;
			}
		}
	}
	
	private static void inizializzaWriter(String path, boolean append) throws IOException {
		fw = new FileWriter(path, append);
		bw = new BufferedWriter(fw);
	}
	
	private static void chiudiWriter() throws IOException {
		bw.close();
		fw.close();
	}
	
	public static void startTimer() {
		start_time = System.currentTimeMillis();
	}
	
	public static String stopTimer() {
		return "Execution Time: " + (System.currentTimeMillis() - start_time) + "ms";
	}
	
}
