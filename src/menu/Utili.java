package menu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utili {
	
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static FileWriter fw;
	static BufferedWriter bw;
	static long start_time;
	
	public static String leggiString(String message) throws IOException {
		System.out.print(message);
		return in.readLine();
	}
	
	public static int leggiInt(String message) throws IOException {
		System.out.print(message);
		return Integer.parseInt(in.readLine());
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
