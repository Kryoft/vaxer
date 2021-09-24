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
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Utili {
	
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static FileWriter fw;
	static BufferedWriter bw;
	static long start_time;
	public static final String NEW_LINE = System.getProperty("line.separator");  // https://stackoverflow.com/questions/36796136/difference-between-system-getpropertyline-separator-and-n
	static RandomAccessFile raf;
	
	public static String leggiString(String message) throws IOException {
		System.out.print(message);
		return in.readLine();
	}
	
	public static boolean leggiSiNo(String message) throws IOException {
		while(true) {
			String s = leggiString(message + " (Sì / No)" + NEW_LINE + "> ");
			switch(s.toLowerCase()) {
			case "s", "si", "sì":
				return true;
			case "n", "no":
				return false;
			default:
				System.out.println("ERRORE: risposta non valida");
				break;
			}
		}
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
	
	// Questo metodo permette di leggere l'ultima riga di un file senza
	// scorrerne tutte le righe con il BufferedReader
	public static String leggiUltimaRiga(String file_path) throws IOException {
		if (!Files.exists(Paths.get(file_path)))
			return null;
		
		String line;
		raf = new RandomAccessFile(file_path, "r");  // "r" = read, "rw" = read and write
		
		/*
		 * Leggo a partire da raf.length()-2 perché:
		 * 1) se leggessi da raf.length(), raf.readByte() lancerebbe una EOFException (End Of File Exception)
		 * perché tenterei di leggere dalla fine del file;
		 * 2) se leggessi da raf.length()-1 leggerei subito il carattere '\n' perché è l'ultimo che
		 * viene scritto (o forse non leggerei niente perché '\n' occupa 2 bytes https://stackoverflow.com/questions/15290203/why-is-a-newline-2-bytes-in-windows).
		 * Se appunto chiamassi raf.readLine() otterrei null
		 */
		long index = raf.length()-2;
		do {
			raf.seek(index--);
		} while (raf.readByte() != '\n' && index > -1);
		
		line = raf.readLine();
		
		raf.close();
		return line;
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
