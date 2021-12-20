/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

/**
 * Parte del package <code>menu</code>, contenente la classe <code>MainMenu</code>
 */
package menu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;



/**
 * 
 * <code>Utili</code> è una classe astratta contenente vari metodi utilizzati in tutta l'applicazione
 * per rendere più scorrevole e semplice l'utilizzo e la lettura delle funzionalità relative.
 * 
 * @see menu.MainMenu
 * @see centrivaccinali.CentriVaccinali
 * @see cittadini.Cittadini
 * 
 * @author Cristian Corti
 * @author Manuel Marceca
 */
public abstract class Utili {
	
	/**
	 * <code>in</code> instanzia un oggetto statico di tipo <code>BufferedReader</code>. Sarà utilizzato
	 * per l'input in runtime.
	 */
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	/**
	 * <code>fw</code> è un oggetto statico di tipo <code>FileWriter</code>. Utile alla scrittura su file.
	 */
	static FileWriter fw;
	
	/**
	 * <code>bw</code> è un oggetto statico di tipo <code>BufferedWriter</code>. Utile alla scrittura su file.
	 */
	static BufferedWriter bw;
	
	/**
	 * <code>start_time</code> è un oggetto statico di tipo primitivo <code>long</code>.
	 */
	static long start_time;
	
	/**
	 * <code>NEW_LINE</code> è un oggetto statico e costante di tipo <code>String<code> che contiene il carattere
	 * new line della macchina su cui è eseguita l'applicazione.
	 * 
	 * @see java.lang.System.getProperty(String key)
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");  // https://stackoverflow.com/questions/36796136/difference-between-system-getpropertyline-separator-and-n
	
	
	/**
	 * <code>raf</code> è un oggetto di tipo RandomAccessFile contenente un'istanza dell'oggetto di tipo
	 * <code>RandomAccessFile</code>.
	 * Utile alla lettura e scrittura sui file di testo.
	 * 
	 * @see java.io.RandomAccessFile
	 */
	static RandomAccessFile raf;
	
	
	/**
	 * Stampa un messaggio su terminale e richiede l'inserimento di input via tastiera, il quale sarà ritornato
	 * 
	 * @param message
	 * 		Una stringa di tipo <code>String</code>.
	 * 
	 * @return {@link #in.readline()}
	 * 		La stringa inserita in input.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static String leggiString(String message) throws IOException {
		System.out.print(message);
		return in.readLine();
	}
	
	
	/**
	 * Stampa un messaggio sul terminale seguito dalla stringa " (Sì / No)". Richiede quindi
	 * all'utente di rispondere al messaggio mostrato solo con stringhe quali "s", "si", "sì", 
	 * "n", "no" (maiuscole incluse). 
	 * In base all'esito, true o false è ritornato.
	 * 
	 * @param message
	 * 		Il messaggio a cui l'utente dovrà rispondere con un "sì" o un "no".
	 * 
	 * @return <strong>boolean</strong>
	 * 		true o false in base alla risposta dell'utente (sì / no).
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
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
	
	
	/**
	 * Appende la stringa <code>testo</code> al file allocato nel percorso <code>path</code>.
	 * 
	 * @param path
	 * 		Il percorso del file su cui si vuole scrivere.
	 * 
	 * @param append
	 * 		true se si vuole appendere il <code>testo</code> al file. False in caso contrario.
	 * 
	 * @param testo
	 * 		Una stringa di testo contenente i dati che si vogliono appendere al file di testo.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static void scriviSuFile(String path, boolean append, String testo) throws IOException {
		inizializzaWriter(path, append);
		bw.append(testo);
		chiudiWriter();
	}
	
	
	/**
	 * Crea un file di testo nel percorso <code>path</code>.
	 * 
	 * @param path
	 * 		Il percorso nel quale si vuole creare il file di testo.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static void creaFile(String path) throws IOException {
		inizializzaWriter(path, false);
		chiudiWriter();
	}
	
	
	/**
	 * Dato il parametro <code>message</code>, questo viene stampato. In base all'input dell'utente,
	 * la scelta tra le opzioni "Ospedaliero", "Aziendale" e "Hub" viene selezionata.
	 * <p>
	 * Utilizzato in <code>CentriVaccinali</code>.
	 * 
	 * @see centrivaccinali.CentriVaccinali
	 * 
	 * @param message
	 * 		Una stringa che sarà stampata.
	 * 
	 * @return <strong>String</strong>
	 * 		La stringa relativa alla scelta eseguita dall'utente.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
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
	
	
	/**
	 * Legge il contenuto della <code>riga</code> trovata nel file di testo nel percorso <code>file_path</code>
	 * 
	 * @param file_path
	 * 		Il percorso del file di testo.
	 * 
	 * @param riga
	 * 		L'indice della riga ricercata.
	 * 
	 * @return <strong>s</strong>
	 * 		Il contenuto della riga selezionata (String).
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static String leggiRiga(String file_path, long riga) throws IOException {
		if (!Files.exists(Paths.get(file_path)))
			return null;
		
		BufferedReader br = new BufferedReader(new FileReader(file_path));
		String s = null;
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		int riga_da_leggere = 2;
		while (riga_da_leggere++ < riga && br.readLine() != null) {}
		
		s = br.readLine();
		
		br.close();
		return s;
	}
	
	// Questo metodo permette di leggere l'ultima riga di un file senza
	// scorrerne tutte le righe con il BufferedReader
	/**
	 * Legge l'ultima riga del file nel percorso <code>file_path</code> senza scorrerne tutte le 
	 * righe con il BufferedReader.
	 * 
	 * @param file_path
	 * 		Il percorso del file di testo.
	 * 
	 * @return <strong>line</strong>
	 * 		Il contenuto dell'ultima riga trovata (String).
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
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
	
	
	/**
	 * Inizializza FileWriter <code>fw</code> e BufferedWriter <code>bw</code>.
	 * 
	 * @param path
	 * 		Percorso del file di testo.
	 *
	 * @param append
	 * 		true se si desidera appendere o meno stringhe al file di testo. False in caso contrario.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	private static void inizializzaWriter(String path, boolean append) throws IOException {
		fw = new FileWriter(path, append);
		bw = new BufferedWriter(fw);
	}
	
	
	/**
	 * Termina gli stream di <code>FileWriter</code> e <code>BufferedWriter</code>.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	private static void chiudiWriter() throws IOException {
		bw.close();
		fw.close();
	}
	
	
	/**
	 * Utilizzata in fase di testing.
	 */
	public static void startTimer() {
		start_time = System.currentTimeMillis();
	}
	
	/**
	 * Ritorna il tempo trascorso dalla chiamata di {@link #startTimer()} in una stringa.
	 * 
	 * @return <strong>String</strong>
	 * 		Come stringa, il tempo trascorso dalla chiamata di {@link #startTimer()}.
	 * @see menu.Utili.startTimer()
	 */
	public static String stopTimer() {
		return "Execution Time: " + (System.currentTimeMillis() - start_time) + "ms";
	}
	
}
