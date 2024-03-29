/*
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
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
 * <code>Utili</code> � una classe astratta contenente vari metodi utilizzati in tutta l'applicazione
 * per l'esecuzione di varie funzionalit�.
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
	 * <code>in</code> instanzia un oggetto statico di tipo <code>BufferedReader</code>. Sar� utilizzato
	 * per l'input in runtime.
	 */
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	/**
	 * <code>fw</code> � un oggetto statico di tipo <code>FileWriter</code>. Utile alla scrittura su file.
	 */
	static FileWriter fw;
	
	/**
	 * <code>bw</code> � un oggetto statico di tipo <code>BufferedWriter</code>. Utile alla scrittura su file.
	 */
	static BufferedWriter bw;
	
	/**
	 * <code>start_time</code> � un oggetto statico di tipo primitivo <code>long</code>.
	 */
	static long start_time;
	
	/**
	 * <code>NEW_LINE</code> � un oggetto statico e costante di tipo <code>String</code> che contiene il carattere
	 * new line della macchina su cui � eseguita l'applicazione.
	 * 
	 * @see java.lang.System#getProperty(String)
	 */
	public static final String NEW_LINE = System.getProperty("line.separator");  // https://stackoverflow.com/questions/36796136/difference-between-system-getpropertyline-separator-and-n
	
	/**
	 * <code>raf</code> � un oggetto di tipo RandomAccessFile contenente un'istanza dell'oggetto di tipo
	 * <code>RandomAccessFile</code>.
	 * Utile alla lettura e scrittura su file byte per byte.
	 * 
	 * @see java.io.RandomAccessFile
	 */
	static RandomAccessFile raf;
	
	/**
	 * Stampa un messaggio sulla stream di output "standard" e richiede l'inserimento di input via tastiera,
	 * il quale sar� restituito. In base al valore del parametro <code>can_be_blank</code> l'utente pu� inserire
	 * in input una stringa vuota.
	 * 
	 * @param message
	 * 		Un oggetto di tipo <code>String</code> che rappresenta il messaggio che si vuole mostrare
	 * 		prima di richiedere l'input all'utente.
	 * 
	 * @param can_be_blank
	 * 		Una variabile di tipo boolean che, se <code>true</code>, permette all'utente di inserire
	 * 		in input una stringa vuota oppure contenente solo spazi, altrimenti, se <code>false</code>,
	 * 		richiede esplicitamente di inserire uno o pi� caratteri.
	 * 
	 * @return
	 * 		La stringa inserita in input.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static String leggiString(String message, boolean can_be_blank) throws IOException {
		System.out.print(message);
		String input = in.readLine();
		while (!can_be_blank && input.isBlank()) {
			System.out.print("L'inserimento non pu� essere vuoto." + Utili.NEW_LINE + message);
			input = in.readLine();
		}
		return input;
	}
	
	/**
	 * Stampa un messaggio sul terminale seguito dalla stringa " (S� / No)". Richiede quindi
	 * all'utente di rispondere al messaggio mostrato solo con stringhe quali "s", "si", "s�", 
	 * "n", "no" (maiuscole incluse). 
	 * In base all'esito, true o false � ritornato.
	 * 
	 * @param message
	 * 		Il messaggio a cui l'utente dovr� rispondere con un "s�" o un "no".
	 * 
	 * @return <strong>boolean</strong>
	 * 		true o false in base alla risposta dell'utente (s� / no).
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static boolean leggiSiNo(String message) throws IOException {
		while(true) {
			String s = leggiString(message + " (S� / No)" + NEW_LINE + "> ", false);
			switch(s.toLowerCase()) {
			case "s", "si", "s�":
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
	 * Scrive o appende la stringa <code>testo</code> al file allocato nel percorso <code>path</code>.
	 * 
	 * @param path
	 * 		Il percorso del file su cui si vuole scrivere.
	 * 
	 * @param append
	 * 		true se si vuole appendere il <code>testo</code> al file. false in caso contrario.
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
	 * Memorizza l'output del metodo {@link #leggiString(String, boolean)}, il quale richiede l'input all'utente.
	 * In base all'input fornito, una tra le opzioni "Ospedaliero", "Aziendale" e "Hub" viene selezionata e restituita.
	 * 
	 * @see centrivaccinali.CentriVaccinali
	 * 
	 * @param message
	 * 		Una stringa che sar� stampata.
	 * 
	 * @return <strong>String</strong>
	 * 		La stringa relativa alla scelta eseguita dall'utente.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static String inserisciTipologiaCentro(String message) throws IOException {
		while (true) {
			String tipologia = leggiString(message, false);
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
	 * Legge il contenuto della riga all'indice <code>riga</code> nel file di testo <code>file_path</code>
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
		 * Leggo a partire da raf.length()-2 perch�:
		 * 1) se leggessi da raf.length(), raf.readByte() lancerebbe una EOFException (End Of File Exception)
		 * perch� tenterei di leggere dalla fine del file;
		 * 2) se leggessi da raf.length()-1 leggerei subito il carattere '\n' perch� � l'ultimo che
		 * viene scritto (N.B. '\n' occupa 2 bytes https://stackoverflow.com/questions/15290203/why-is-a-newline-2-bytes-in-windows).
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
	 * 		Percorso del file di testo dove si desidera scrivere.
	 *
	 * @param append
	 * 		true se si desidera appendere o meno caratteri in fondo al file di testo. false in caso contrario.
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
	 * Memorizza la differenza di tempo (in millisecondi) tra questo momento e la mezzanotte
	 * del 1 Gennaio 1970 UTC.
	 * <br>Utilizzata in fase di testing.
	 */
	public static void startTimer() {
		start_time = System.currentTimeMillis();
	}
	
	/**
	 * Ritorna il tempo (in millisecondi) trascorso dalla chiamata di {@link #startTimer()} in una stringa.
	 * 
	 * @return <strong>String</strong>
	 * 		Come stringa, il tempo (in millisecondi) trascorso dalla chiamata di {@link #startTimer()}.
	 * @see menu.Utili#startTimer()
	 */
	public static String stopTimer() {
		return "Execution Time: " + (System.currentTimeMillis() - start_time) + "ms";
	}
	
}
