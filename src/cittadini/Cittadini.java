/*
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package cittadini;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import menu.MainMenu;
import menu.Utili;

/**
 * <code>Cittadini</code> contiene tutte le operazioni necessarie ai cittadini per cercare e visualizzare
 * informazioni di un centro vaccinale, registrarsi al sistema e inserire eventi avversi.
 * <p> Come appoggio vengono utilizzati vari metodi contenuti in <code>menu.Utili</code>.
 * 
 * @see menu.Utili
 * 
 * @author Cristian Corti
 * @author Manuel Marceca
 */
public class Cittadini {
	
	/**
	 * Carattere utilizzato per separare le severit� nel file <code>Cittadini_Vaccinati.dati</code>.
	 */
	private static final String SEPARATORE_SEVERITA = ",";
	
	/**
	 * Carattere utilizzato per separare le severit� medie nel file <code>CentriVaccinali.dati</code>.
	 */
	private static final String SEPARATORE_SEVERITA_MEDIE = ",";
	
	/**
	 * Carattere utilizzato per separare il numero di segnalazioni nel file <code>CentriVaccinali.dati</code>.
	 */
	private static final String SEPARATORE_NUMERO_SEGNALAZIONI = ",";
	
	/**
	 * Carattere utilizzato per separare le note opzionali nel file <code>Cittadini_Vaccinati.dati</code>.
	 */
	private static final String SEPARATORE_NOTE_OPZIONALI = "|";
	
	// memorizzo la coppia "ID_vaccinazione, riga" cos� da poter ottenere subito la riga in cui un cittadino � scritto dato il suo ID di vaccinazione
	/**
	 * HashMap contenente delle coppie 'chiave':'valore', in cui la chiave � data dall'ID di vaccinazione del cittadino,
	 * mentre il valore � la riga in cui quell'ID � scritto nel file <code>Cittadini_Vaccinati.dati</code>.
	 * 
	 * @see HashMap
	 */
	public static Map<String, Long> cittadini_vaccinati;
	
	/**
	 * Memorizza il numero di righe presenti nel file <code>Cittadini_Vaccinati.dati</code>.
	 */
	public static long numero_righe_file_cittadini_vaccinati = 1;  // Va inizializzata ad 1 perch� la prima riga contiene i campi
	
	// memorizzo la coppia "user_ID, riga" perch� memorizzare "user_ID, password" direttamente avrebbe un peso maggiore sulla memoria RAM (la password � sempre di 64 caratteri).
	/**
	 * HashMap contenente delle coppie 'chiave':'valore', in cui la chiave � data dall'user ID del cittadino registrato,
	 * mentre il valore � la riga in cui quell'user ID � scritto nel file <code>Cittadini_Registrati.dati</code>.
	 * 
	 * @see HashMap
	 */
	public static Map<String, Long> cittadini_registrati;
	
	/**
	 * Memorizza il numero di righe presenti nel file <code>Cittadini_Registrati.dati</code>.
	 */
	public static long numero_righe_file_cittadini_registrati = 1;  // Va inizializzata ad 1 perch� la prima riga contiene i campi
	
	/**
	 * Memorizza l'user ID dell'utente correntemente loggato.
	 */
	private static String logged_userID;
	
	/**
	 * Memorizza l'ID di vaccinazione dell'utente loggato.
	 */
	private static String logged_ID;
	
	/**
	 * Array di stringhe contenente i vari sintomi (eventi avversi), quali:
	 * <br>
	 * - Mal di testa
	 * <br>
	 * - Febbre
	 * <br>
	 * - Dolori muscolari e articolari
	 * <br>
	 * - Linfoadenopatia
	 * <br>
	 * - Tachicardia
	 * <br>
	 * - Crisi ipertensiva
	 */
	private static final String[] eventi_avversi = {"Mal di testa",
													"Febbre",
													"Dolori muscolari e articolari",
													"Linfoadenopatia",
													"Tachicardia",
													"Crisi ipertensiva"};
	
	/**
	 * Stringa contenente il nome del cittadino.
	 */
	private String nome;
	
	/**
	 * Stringa contenente il cognome del cittadino.
	 */
	private String cognome;
	
	/**
	 * Stringa contenente il codice fiscale del cittadino.
	 */
	private String codice_fiscale;
	
	/**
	 * Stringa contenente l'indirizzo e-mail del cittadino.
	 */
	private String indirizzo_email;
	
	/**
	 * Stringa contenente l'user ID del cittadino.
	 */
	private String user_id;
	
	/**
	 * Stringa contenente la password del cittadino.
	 */
	private String password;
	
	/**
	 * Stringa contenente l'ID vaccinazione del cittadino.
	 */
	private String id_vaccinazione;
	
	/**
	 * Permette all'utente di selezionare un criterio di ricerca per trovare un centro vaccinale.
	 * 
	 * @see #cercaCentroVaccinale(String nome_centro)
	 * @see #cercaCentroVaccinale(String comune, String tipologia)
	 * 
	 * @return {@link #cercaCentroVaccinale(String nome_centro)} oppure {@link #cercaCentroVaccinale(String comune, String tipologia)}
	 * 		
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static ArrayList<String> scegliCriterioRicerca() throws IOException {
		if (Files.notExists(Paths.get(MainMenu.CENTRI_VACCINALI_PATH)))
			return null;
		
		while (true) {
			System.out.println(Utili.NEW_LINE + "- Scelta Criterio di Ricerca -");
			String ricerca_per = Utili.leggiString(String.format("Ricerca per:%s1) Nome Centro%s2) Comune e Tipologia%s%s> ",
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE), false);
			
			if (ricerca_per.strip().equals("1")) {
				System.out.println(Utili.NEW_LINE + "- Ricerca per Nome -");
				String centro = Utili.leggiString("Nome Centro > ", true).strip();
				return cercaCentroVaccinale(centro);
			} else if (ricerca_per.strip().equals("2")) {
				System.out.println(Utili.NEW_LINE + "- Ricerca per Comune e Tipologia -");
				String comune = Utili.leggiString("Comune del Centro > ", false).strip();
				String tipologia = Utili.inserisciTipologiaCentro(String.format("- Tipologia:%s1) Ospedaliero%s2) Aziendale%s3) Hub%s%s> ",
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE));
				return cercaCentroVaccinale(comune, tipologia);
			} else {
				System.out.println("Scelta non valida, riprova");
			}
		}
	}
	
	/**
	 * Cerca un centro vaccinale dato il nome del centro. 
	 * <br> Restituisce la lista dei possibili centri corrispondenti,
	 * tramite la quale l'utente potr� selezionare quello ricercato.
	 * 
	 * @param nome_centro
	 * 		Il nome del centro vaccinale ricercato.
	 * 
	 * @return <strong>centri_trovati</strong>
	 * 		Un'ArrayList di stringhe contenente i centri per cui vale la corrispondenza con il parametro <code>nome_centro</code>.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static ArrayList<String> cercaCentroVaccinale(String nome_centro) throws IOException {
		// Cercare i centri il cui nome contiene la stringa passata come argomento nel file CentriVaccinali.dati;
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CENTRI_VACCINALI_PATH));
		ArrayList<String> centri_trovati = new ArrayList<>();
		String str;
		nome_centro = nome_centro.toLowerCase();
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		while ((str = br.readLine()) != null) {
			if (str.substring(0, str.indexOf(';')).toLowerCase().contains(nome_centro))
				centri_trovati.add(str);
		}
		
		br.close();
		return centri_trovati;
	}
	
	/**
	 * Cerca un centro vaccinale dato il nome del comune e la tipologia del centro. 
	 * <br> Restituisce la lista dei possibili centri corrispondenti,
	 * tramite la quale l'utente potr� selezionare quello ricercato.
	 * 
	 * @param comune
	 * 		Il nome del comune in cui � situato il centro.
	 * 
	 * @param tipologia
	 * 		La tipologia del centro ricercato (Ospedaliero, Aziendale o Hub).
	 * 
	 * @return <strong>centri_trovati</strong>
	 * 		Un'ArrayList di stringhe contenente i centri per cui vale la corrispondenza con i parametri <code>comune</code> e <code>tipologia</code>.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static ArrayList<String> cercaCentroVaccinale(String comune, String tipologia) throws IOException {
		// Cercare i centri il cui comune e tipologia corrispondono ai dati passati come argomento nel file CentriVaccinali.dati;
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CENTRI_VACCINALI_PATH));
		ArrayList<String> centri_trovati = new ArrayList<>();
		String str;
		String[] address = null;
		String[] columns = null;
		
		comune = comune.toLowerCase();
		tipologia = tipologia.toLowerCase();
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		// Scorre tutte le righe del file;
		while ((str = br.readLine()) != null) {
			if (str.toLowerCase().contains(comune)) {
				// Splitta ogni riga in un array di stringhe;
				columns = str.toLowerCase().split(";");
				// Se comune e tipologia sono contenute nelle rispettive colonne del file CentriVaccinali.dati allora stampa str; 
				if (columns[2].contains(tipologia)) {
					address = columns[1].split(",");  // Siccome splittiamo per ',' impediamo all'utente di inserire virgole nell'indirizzo, altrimenti potrebbe accadere che il comune non si trovi in address[1]
					if (address[1].contains(comune))
						centri_trovati.add(str);
				}
			}
		}
		
		br.close();
		return centri_trovati;
	}
	
	/**
	 * Stampa sul terminale le informazioni di uno specifico centro vaccinale, selezionato dall'utente dopo una ricerca.
	 * 
	 * @see #scegliCriterioRicerca()
	 * @see #selezionaCentro(ArrayList, String, String)
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static void visualizzaInfoCentroVaccinale() throws IOException {
		ArrayList<String> centri_trovati = scegliCriterioRicerca(); // Lista utilizzata per tenere traccia di tutti i centri vaccinali trovati in modo da permettere all'utente di selezionarne uno
		
		if (centri_trovati == null)
			System.out.println("Funzione non disponibile: Nessun centro vaccinale registrato." + Utili.NEW_LINE);
		// Se sono stati trovati dei centri l'utente pu� selezionarne uno
		else if (centri_trovati.isEmpty())
			System.out.println("Nessun centro trovato." + Utili.NEW_LINE);
		else {
			System.out.println(Utili.NEW_LINE + "- Centri Trovati -");
			int scelta = selezionaCentro(centri_trovati, "Seleziona uno dei centri elencati per visualizzarne le informazioni: ", "Annulla");
			
			if (scelta != 0) {
				StringBuilder output = new StringBuilder();
				String[] columns = centri_trovati.get(--scelta).split(";");  // Recupera dalla lista centri_trovati il centro selezionato dall'utente, poi splitta la stringa con i vari campi
				
				output.append(String.format(Utili.NEW_LINE + "- Informazioni -" + Utili.NEW_LINE +
						"Nome Centro: %s%sIndirizzo: %s%sTipologia: %s%sEventi Avversi: ",
						columns[0],
						Utili.NEW_LINE,
						columns[1],
						Utili.NEW_LINE,
						columns[2],
						Utili.NEW_LINE));
				
				double[] severita_medie = Stream.of(columns[3].split(SEPARATORE_SEVERITA_MEDIE)).mapToDouble(Double::parseDouble).toArray();
				int[] numero_segnalazioni = Stream.of(columns[4].split(SEPARATORE_NUMERO_SEGNALAZIONI)).mapToInt(Integer::parseInt).toArray();
				
				for (int i = 0; i < 6; i++) {
					output.append(String.format("%s - %s: %s (Severit� media), %s (Numero di segnalazioni)",
												Utili.NEW_LINE,
												eventi_avversi[i],
												severita_medie[i],
												numero_segnalazioni[i]));
				}
				
				System.out.println(output.toString() + Utili.NEW_LINE);
			}
		}
	}
	
	/**
	 * Data una lista di centri, permette all'utente di selezionare il centro da lui ricercato.
	 * 
	 * @param centri_trovati
	 * 		ArrayList contenente i centri trovati
	 * 
	 * @param messaggio_di_scelta
	 * 		Messaggio stampato su terminale per indicare all'utente la possibilit� di scelta
	 * 
	 * @param messaggio_di_annullamento
	 * 		Messaggio stampato su terminale per indicare all'utente il comando di annullamento dell'operazione
	 * 
	 * @return <strong>scelta</strong>
	 * 		La scelta dell'utente (un intero).
	 * 		
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static int selezionaCentro(ArrayList<String> centri_trovati, String messaggio_di_scelta, String messaggio_di_annullamento) throws IOException {
		int scelta;  // Integer utilizzato per far selezionare all'utente un centro vaccinale nella lista centri_trovati;
		int counter = 1;
		
		for (String centro : centri_trovati) {
			System.out.println(counter++ + ") " + centro.substring(0, centro.indexOf(';')));
		}
		System.out.println("0) " + messaggio_di_annullamento + Utili.NEW_LINE);
		
		do {
			try {
				scelta = Integer.parseInt(Utili.leggiString(messaggio_di_scelta, false));
			} catch (NumberFormatException nfe) {
				// se l'utente non inserisce un numero imposto scelta a -1 cos� da richiedergli di inserire un numero valido
				scelta = -1;
			}
		} while (scelta < 0 || scelta > centri_trovati.size());
		
		return scelta;  // if (scelta == 0) then l'utente vuole annullare la selezione
	}
	
	/**
	 * Legge il contenuto di <code>Cittadini_Vaccinati.dati</code> e per ogni riga inserisce nella HashMap
	 * {@link #cittadini_vaccinati} l'ID di vaccinazione del cittadino come chiave, e la riga in cui quel'ID
	 * � scritto come valore.
	 * 
	 * @see HashMap
	 * 
	 * @return
	 * 		<code>true</code> nel caso in cui l'operazione termini con successo.
	 * 		<br>
	 * 		<code>false</code> in caso contrario (file <code>Cittadini_Vaccinati.dati</code> non esistente).
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static boolean caricaCittadiniVaccinati() throws IOException {
		if (!Files.exists(Paths.get(MainMenu.CITTADINI_VACCINATI_PATH)))
			return false;
		
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CITTADINI_VACCINATI_PATH));
		String s;
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		numero_righe_file_cittadini_vaccinati = 1;
		while ((s = br.readLine()) != null) {
			cittadini_vaccinati.put(s.substring(0, s.indexOf(';')), ++numero_righe_file_cittadini_vaccinati);
		}
		
		br.close();
		return true;
	}
	
	/**
	 * Legge il contenuto di <code>Cittadini_Registrati.dati</code> e per ogni riga inserisce nella HashMap
	 * {@link #cittadini_registrati} l'user ID del cittadino come chiave, e la riga in cui quello user ID
	 * � scritto come valore.
	 * 
	 * @return
	 * 		<code>true</code> nel caso in cui l'operazione termini con successo.
	 * 		<br>
	 * 		<code>false</code> in caso contrario (file <code>Cittadini_Registrati.dati</code> non esistente).
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static boolean caricaCittadiniRegistrati() throws IOException {
		if (!Files.exists(Paths.get(MainMenu.CITTADINI_REGISTRATI_PATH)))
			return false;
		
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CITTADINI_REGISTRATI_PATH));
		String s;
		String ID;
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		numero_righe_file_cittadini_registrati = 1;
		while ((s = br.readLine()) != null) {
			cittadini_registrati.put(s.substring(0, s.indexOf(';')), ++numero_righe_file_cittadini_registrati);
			ID = s.substring(s.lastIndexOf(';')+1);
			// Una volta ottenuto l'ID da una riga generica del file Cittadini_Registrati.dati, sono sicuro che
			// tale ID sia anche in 'cittadini_vaccinati', perch� solo i cittadini vaccinati possono aver eseguito
			// la registrazione. Perci� per quegli ID imposto la riga associata in 'cittadini_vaccinati' negativa, in modo
			// che quando trovo un ID con riga negativa so che � gi� registrato e non permetto una nuova registrazione
			// con quell'ID. Inoltre, l'informazione riga rimane comunque, � solo negativa, quindi posso sapere a quale
			// riga � scritto il cittadino con quell'ID nel file Cittadini_Vaccinati.dati semplicemente rendendo tale riga positiva.
			cittadini_vaccinati.replace(ID, -cittadini_vaccinati.get(ID));
		}
		
		br.close();
		return true;
	}
	
	/**
	 * Permette ad un utente di registrarsi al sistema inserendo i propri dati, i quali saranno poi memorizzati in 
	 * <code>Cittadini_Registrati.dati</code>.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static void registraCittadino() throws IOException {
		
		// Non si possono inserire stringhe ad una specifica posizione di un file di testo, ma solo modificare
		// i bytes gi� scritti in una certa posizione. (Questo � possibile instanziando un RandomAccessFile in
		// "rw" mode e, opzionalmente, facendo seek(long pos) per spostarsi di 'pos' bytes nel file;
		// successivamente si possono utilizzare i metodi di scrittura, come per esempio writeBytes(String s)
		// per MODIFICARE i bytes successivi a 'pos' con quelli specificati come argomento).
		RandomAccessFile raf = new RandomAccessFile(MainMenu.CITTADINI_REGISTRATI_PATH, "rw");
		
		if (raf.length() == 0)
			raf.writeBytes("USER_ID;NOME;COGNOME;CODICE_FISCALE;EMAIL;PASSWORD;ID_VACCINAZIONE" + Utili.NEW_LINE);
		
		Cittadini cittadino = new Cittadini();
		
		cittadino.nome = Utili.leggiString("- Nome > ", false).strip().replace(";", "");
		cittadino.cognome = Utili.leggiString("- Cognome > ", false).strip().replace(";", "");
		cittadino.codice_fiscale = Utili.leggiString("- Codice Fiscale > ", false).strip().replace(";", "");
		cittadino.indirizzo_email = Utili.leggiString("- Indirizzo e-mail > ", false).strip().replace(";", "");	// (opzionale) controllo se si tratta di una email
		cittadino.user_id = Utili.leggiString("- User ID > ", false).strip().replace(";", "").toLowerCase();
		cittadino.password = sha256(Utili.leggiString("- Password > ", false));
		
		Long num_riga = null;
		while (num_riga == null || num_riga < 0) {
			while ((cittadino.id_vaccinazione = Utili.leggiString("- ID Vaccinazione > ", false).strip().replace(";", "").toUpperCase()).length() != 16) {
				if (!Utili.leggiSiNo("Errore: L'ID di vaccinazione deve essere composto da 16 caratteri alfanumerici. Riprovare?")) {
					raf.close();
					return;
				}
			}
			
			num_riga = cittadini_vaccinati.get(cittadino.id_vaccinazione);
			
			if (num_riga == null)  // se la riga che corrisponde all'ID inserito non � nulla, allora esiste un cittadino con quell'ID
				if (Utili.leggiSiNo("Errore: L'ID inserito non corrisponde a nessun cittadino vaccinato. Riprovare?"))
					continue;
				else {
					raf.close();
					return;
				}
			else if (num_riga < 0) {
				if (Utili.leggiSiNo("Errore: L'ID inserito corrisponde ad un utente gi� registrato. Riprovare?"))
					continue;
				else {
					raf.close();
					return;
				}
			}
		}
		
//		String riga = Utili.leggiRiga(MainMenu.CITTADINI_VACCINATI_PATH, num_riga);
		
		String riga = String.format("%s;%s;%s;%s;%s;%s;%s%s", 
				cittadino.user_id,
				cittadino.nome,
				cittadino.cognome,
				cittadino.codice_fiscale,
				cittadino.indirizzo_email,
				cittadino.password,
				cittadino.id_vaccinazione,
				Utili.NEW_LINE);
		
		raf.seek(raf.length());  // imposta la posizione del puntatore del RAF alla fine del file
		raf.writeBytes(riga);
		
		cittadini_registrati.put(cittadino.user_id, ++numero_righe_file_cittadini_registrati);
		cittadini_vaccinati.replace(cittadino.id_vaccinazione, -cittadini_vaccinati.get(cittadino.id_vaccinazione));
		
		raf.close();
	}
	
	/**
	 * Data una password come parametro, ne restituisce la codifica in SHA-256 come stringa.
	 * 
	 * @param base
	 * 		La password in plain-text.
	 * 
	 * @return
	 *  	La password criptata come stringa.
	 */
	public static String sha256(String base) {
		/*
		 * https://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java/25243174#25243174 -- Spiegazione funzionamento codice
		 */
	    try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}
	
	// Pu� essere invocato solo dopo aver effettuato il login;
	/**
	 * Permette all'utente di segnalare eventuali sintomi/eventi avversi verificati dopo la vaccinazione.
	 * <p>
	 * L'insistenza di un sintomo pu� essere definita con un valore tra 1 a 5, ed eventualmente affiancata ad una
	 * nota personale.
	 * <br> Tali dati sono utilizzati da ogni centro vaccinale per determinare una media dei valori riscontrati per ogni
	 * sintomo.
	 * 
	 * @see #memorizzaEventiAvversi(int[], String[], long)
	 * 
	 * @return
	 * 		<code>true</code> nel caso in cui l'operazione termini con successo.
	 *		<br>
	 *		<code>false</code> in caso contrario.
	 *
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static boolean inserisciEventiAvversi() throws IOException {
		if (logged_userID == null && !login())
			return false;
		
		int choice = 0;  // Variabile utilizzata per gestire la scelta dell'utente.
		int severita[] = new int[6];
		String note[] = new String[6];
		
		do {
			do {
				System.out.println(Utili.NEW_LINE + "- Segnalazione Eventi Avversi - " + Utili.NEW_LINE);
				System.out.println("Selezionare il sintomo riscontrato" + Utili.NEW_LINE);
				System.out.println("1) " + eventi_avversi[0]);
				System.out.println("2) " + eventi_avversi[1]);
				System.out.println("3) " + eventi_avversi[2]);
				System.out.println("4) " + eventi_avversi[3]);
				System.out.println("5) " + eventi_avversi[4]);
				System.out.println("6) " + eventi_avversi[5]);
				System.out.println("0) Termina");
				
				try {
					choice = Integer.parseInt(Utili.leggiString(Utili.NEW_LINE + "> ", false).strip());
				} catch(NumberFormatException e) {
					System.out.println(Utili.NEW_LINE + "ERRORE: risposta non valida");
				}
			} while(choice < 0 || choice > 6);
			
			if (choice != 0) {
				choice--;
				
				do {
					try {
						severita[choice] = Integer.parseInt(Utili.leggiString("Inserire severit� (da 1 a 5)" + Utili.NEW_LINE + "> ", false));
					} catch(NumberFormatException e) {
						System.out.println(Utili.NEW_LINE + "ERRORE: risposta non valida");
					}
				} while (severita[choice] < 1 || severita[choice] > 5);
				
				if (Utili.leggiSiNo("Desideri aggiungere una nota?")) {
					do {
						note[choice] = Utili.leggiString("Inserire nota (max 256 caratteri)" + Utili.NEW_LINE + "> ", false)
																								.replace("|", "")
																								.replace(";", "")
																								.strip();
					} while (note[choice].length() > 256);
				}
				
				choice++;
			}
			
		} while(choice != 0);
		
		// Controlla se l'utente ha inserito severit� oppure no. Se tutti i valori nell'array severita sono a 0,
		// il cittadino non avr� inserito niente, perci� termino.
		boolean ha_inserito_valori = false;
		for (int val : severita)
			if (val != 0) {
				ha_inserito_valori = true;
				break;
			}
		if (!ha_inserito_valori)
			return false;
		
		long riga_cittadino_su_file_vaccinati = cittadini_vaccinati.get(logged_ID);
		if (riga_cittadino_su_file_vaccinati < 0)
			riga_cittadino_su_file_vaccinati = -riga_cittadino_su_file_vaccinati;
		memorizzaEventiAvversi(severita, note, riga_cittadino_su_file_vaccinati);
		
		return true;
	}
	
	/**
	 * Funzione chiamata dopo l'inserimento delle severit� e delle rispettive note opzionali per gli
	 * eventi avversi desiderati con la funzione {@link #inserisciEventiAvversi()}.
	 * Questi dati vengono memorizzati sul file di testo <code>Cittadini_Vaccinati.dati</code>.
	 * 
	 * @see #inserisciEventiAvversi()
	 * 
	 * @param severita
	 * 		Array contenente i valori in scala da 1 a 5 per i sintomi riscontrati dall'utente.
	 * 
	 * @param note_opzionali
	 * 		Array contenente le note opzionali per gli eventuali sintomi riscontrati dall'utente.
	 * 
	 * @param riga
	 * 		La riga relativa all'utente nel file <code>Cittadini_Vaccinati.dati</code>.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	private static void memorizzaEventiAvversi(int[] severita, String[] note_opzionali, long riga) throws IOException {
		// Se sono arrivato a questo punto, so gi� che ho da scrivere dei dati, quindi posso iniziare a copiare
		// nel file temporaneo dei Cittadini_Vaccinati le righe che precedono la riga del cittadino loggato correntemente.
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CITTADINI_VACCINATI_PATH));
		String temp_file_path = MainMenu.CITTADINI_VACCINATI_PATH.substring(0, MainMenu.CITTADINI_VACCINATI_PATH.lastIndexOf('.')) + "_temp" + MainMenu.CITTADINI_VACCINATI_PATH.substring(MainMenu.CITTADINI_VACCINATI_PATH.lastIndexOf('.'));
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp_file_path));
		
		long contatore_riga = 1;
		// Non ho bisogno di controllare che br.readLine sia null perch� sto cercando una riga sicuramente presente
		while (contatore_riga++ < riga) {
			bw.write(br.readLine() + Utili.NEW_LINE);
		}
		
		String line = br.readLine();
		String[] dati = line.split(";");
		StringBuilder nuova_riga = new StringBuilder();
		
		for (int i = 0; i < 7; i++)  // ricopio nella nuova_riga i primi 7 campi (da 0 a 6)
			nuova_riga.append(dati[i] + ';');
		
		int[] severita_sul_file = Stream.of(dati[7].split(SEPARATORE_SEVERITA)).mapToInt(Integer::parseInt).toArray();  // converte l'array di String con le severit� prese dal file in un array di int [https://stackoverflow.com/a/37093052]
		StringBuilder nuove_severita = new StringBuilder();
		String[] note_sul_file = dati[8].split("\\|");  // https://stackoverflow.com/a/16311662
		StringBuilder nuove_note_opzionali = new StringBuilder();
			
		// Loop per l'inserimento delle severit� e delle note opzionali in nuova_riga
		// i va da 0 a 10 perch� itero ogni carattere del campo EVENTI_AVVERSI_E_SEVERITA
		for (int i = 0; i < 11; i++) {
			// dati[7] corrisponde al campo delle severit� che sar� nel formato seguente: "0,3,0,2,0,0".
			if (i % 2 != 0) {  // Se i � dispari appendo il separatore, che per le severit� sar� ',' e per le note sar� '|'
				nuove_severita.append(SEPARATORE_SEVERITA);
				nuove_note_opzionali.append(SEPARATORE_NOTE_OPZIONALI);
			} else {
				if (severita[i/2] != 0) {  // se la severit� corrente � != 0 significa che la voglio inserire...
					if (severita_sul_file[i/2] != 0)  // ...per� se dati[7].charAt(i) != '0' allora � gi� presente una severit�. Chiedo quindi all'utente se vuole sostituirla con quella appena inserita.
						nuove_severita.append(Utili.leggiSiNo("Per l'evento avverso '" +
																eventi_avversi[i/2] +
																"' � gi� presente la severit� " +
																severita_sul_file[i/2] +
																". Desideri sostituirla con " +
																severita[i/2] +
																"?")? severita[i/2] : 0);
					else  // se invece dati[7].charAt(i) == '0' la sostituisco senza chiederlo all'utente.
						nuove_severita.append(severita[i/2]);
				} else {  // se invece la severit� corrente � 0 significa che non la voglio inserire...
					nuove_severita.append(0);  // ...quindi appendo 0
				}
				
				if (note_opzionali[i/2] != null) {  // se la nota_opzionale corrente � != null significa che la voglio inserire...
					// ...per� se !note_sul_file[i/2].equals(#) allora � gi� presente una nota. Chiedo quindi all'utente se vuole sostituirla con quella appena inserita.
					if (!note_sul_file[i/2].equals("#"))
						nuove_note_opzionali.append(Utili.leggiSiNo("Per la nota opzionale riguardante l'evento avverso '" +
																	eventi_avversi[i/2] +
																	"' � gi� presente la nota '" +
																	note_sul_file[i/2] +
																	"'. Desideri sostituirla con '" +
																	note_opzionali[i/2] +
																	"'?")? note_opzionali[i/2] : note_sul_file[i/2]);
					else  // se invece note_sul_file[i/2].equals("#") la sostituisco senza chiederlo all'utente.
						nuove_note_opzionali.append(note_opzionali[i/2]);
				} else {  // se invece la nota corrente � nulla significa che non la voglio inserire...
					nuove_note_opzionali.append(note_sul_file[i/2]);  // ...quindi appendo quella gi� presente
				}
			}
		}
		
		int[] nuove_severita_arr = Stream.of(nuove_severita.toString().split(SEPARATORE_SEVERITA)).mapToInt(Integer::parseInt).toArray();
		for (int i = 0; i < severita_sul_file.length; i++)
			nuova_riga.append(((nuove_severita_arr[i] != 0) ? nuove_severita_arr[i] : severita_sul_file[i]) + SEPARATORE_SEVERITA);
		nuova_riga.deleteCharAt(nuova_riga.length()-1);
		
		nuova_riga.append(";" + nuove_note_opzionali);
		
		bw.write(nuova_riga.toString() + Utili.NEW_LINE);
		
		while ((line = br.readLine()) != null) {
			bw.write(line + Utili.NEW_LINE);
		}
		
		br.close();
		bw.close();
		
		File to_delete = new File(MainMenu.CITTADINI_VACCINATI_PATH);
		to_delete.delete();
		File to_rename = new File(temp_file_path);
		to_rename.renameTo(to_delete);
		
		aggiornaSeveritaMediaENumeroSegnalazioni(dati[4], severita_sul_file, nuove_severita_arr);
	}
	
	/**
	 * Funzione chiamata dopo la memorizzazione delle severit� e delle note opzionali tramite la
	 * funzione {@link #memorizzaEventiAvversi(int[], String[], long)}.
	 * <br> Utilizza le vecchie severit� (quelle memorizzate nel file <code>Cittadini_Vaccinati.dati</code>
	 * alla tupla relativa all'utente correntemente loggato) e quelle nuove (inserite dall'utente durante
	 * questa esecuzione) per calcolare la nuova severit� media ed il nuovo numero di segnalazioni
	 * per ogni evento avverso del centro vaccinale indicato come parametro. I nuovi valori verranno poi
	 * sostituiti a quelli precedenti nel file <code>CentriVaccinali.dati</code>
	 * 
	 * @see #memorizzaEventiAvversi(int[], String[], long)
	 * @see java.lang.StringBuilder
	 * 
	 * @param nome_centro_vaccinale
	 * 		Il nome del centro vaccinale in cui l'utente loggato correntemente ha eseguito la vaccinazione.
	 * 
	 * @param vecchie_severita
	 * 		L'array contenente le severit� che erano presenti sul file <code>Cittadini_Vaccinati.dati</code>.
	 * 
	 * @param nuove_severita
	 * 		L'array contenente le severit� aggiornate dall'utente.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	private static void aggiornaSeveritaMediaENumeroSegnalazioni(String nome_centro_vaccinale, int[] vecchie_severita, int[] nuove_severita) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CENTRI_VACCINALI_PATH));
		String temp_file_path = MainMenu.CENTRI_VACCINALI_PATH.substring(0, MainMenu.CENTRI_VACCINALI_PATH.lastIndexOf('.')) + "_temp" + MainMenu.CENTRI_VACCINALI_PATH.substring(MainMenu.CENTRI_VACCINALI_PATH.lastIndexOf('.'));
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp_file_path));
		
		bw.write(br.readLine() + Utili.NEW_LINE);  // Leggo la prima riga e la scrivo nel nuovo file in quanto contiene i campi
		
		String s;
		while ((s = br.readLine()) != null) {
			if (s.substring(0, s.indexOf(';')).equals(nome_centro_vaccinale))
				break;
			else
				bw.write(s + Utili.NEW_LINE);
		}
		
		String[] dati = s.split(";");
		double[] severita_medie = Stream.of(dati[3].split(",")).mapToDouble(Double::parseDouble).toArray();
		int[] numero_segnalazioni = Stream.of(dati[4].split(",")).mapToInt(Integer::parseInt).toArray();
		
		for (int i = 0; i < vecchie_severita.length; i++) {
			if (nuove_severita[i] == 0)
				continue;
			else {
				if (numero_segnalazioni[i] == 0) {
					numero_segnalazioni[i]++;
					severita_medie[i] = nuove_severita[i];
				} else {
					int vecchio_numero_segnalazioni = numero_segnalazioni[i];
					if (vecchie_severita[i] == 0)
						numero_segnalazioni[i]++;
					int difference = nuove_severita[i] - vecchie_severita[i];
					
					double primo_calcolo = (double) vecchio_numero_segnalazioni / (double) numero_segnalazioni[i];
					double secondo_calcolo = severita_medie[i] * primo_calcolo;
					double terzo_calcolo = (double) difference / (double) numero_segnalazioni[i];
					severita_medie[i] = secondo_calcolo + terzo_calcolo;
//					severita_medie[i] = severita_medie[i] * (
//													(double) vecchio_numero_segnalazioni / (double) numero_segnalazioni[i]
//															) + (
//													(double) difference / (double) numero_segnalazioni[i]
//																);
				}
			}
		}
		
		StringBuilder severita_medie_string = new StringBuilder();
		for (double severita : severita_medie)
			severita_medie_string.append(severita + SEPARATORE_SEVERITA_MEDIE);
		severita_medie_string.deleteCharAt(severita_medie_string.length()-1);
		
		StringBuilder numero_segnalazioni_string = new StringBuilder();
		for (int segnalazione : numero_segnalazioni)
			numero_segnalazioni_string.append(segnalazione + SEPARATORE_NUMERO_SEGNALAZIONI);
		numero_segnalazioni_string.deleteCharAt(numero_segnalazioni_string.length()-1);
		
		String nuova = dati[0] + ';' + dati[1] + ';' + dati[2] + ';' + severita_medie_string.toString() + ';' + numero_segnalazioni_string.toString();
		bw.write(nuova + Utili.NEW_LINE);
		while ((s = br.readLine()) != null)
			bw.write(s + Utili.NEW_LINE);
		
		br.close();
		bw.close();
		
		File to_delete = new File(MainMenu.CENTRI_VACCINALI_PATH);
		to_delete.delete();
		File to_rename = new File(temp_file_path);
		to_rename.renameTo(to_delete);
	}
	
	
	/**
	 * Permette all'utente di eseguire il login controllando che lo user ID e la password da lui forniti siano corretti.
	 * 
	 * @return
	 * 		<code>true</code> se il login � avvenuto con successo,
	 * 		<br><code>false</code> altrimenti.
	 * 		
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	private static boolean login() throws IOException {
		boolean logged_in = false;
		Long riga;
		String user_ID;
		String[] dati = null;
		
		System.out.println(Utili.NEW_LINE + "- LOGIN -");
		
		do {
			user_ID = Utili.leggiString("- UserID > ", false).toLowerCase();
		} while ((riga = cittadini_registrati.get(user_ID)) == null && Utili.leggiSiNo(Utili.NEW_LINE +
																		"L'UserID non esiste. Riprovare?"));
		
		if (riga != null) {
			dati = Utili.leggiRiga(MainMenu.CITTADINI_REGISTRATI_PATH, riga).split(";");
		}
		
		if (dati != null && dati[5] != null) {
			do {
				logged_in = (dati[5].equals(sha256(Utili.leggiString("- Password > ", false)))) ? true : false;
			} while (!logged_in && Utili.leggiSiNo(Utili.NEW_LINE +
														"Password errata. Riprovare?"));
		}
		
		if (logged_in) {
			logged_userID = user_ID;
			logged_ID = dati[6];
			System.out.println("Login eseguito.");
		}
		return logged_in;
	}
	
	
	/**
	 * Il main della classe {@link #Cittadini}.
	 * <p>
	 * Utilizzato per interagire con l'utente stampando messaggi sul terminale e richiedendo risposte in input.
	 * 
	 * @param args
	 * 		Array di stringhe di utilizzo facoltativo
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static void main(String[] args) throws IOException {
		
		String choice;
		boolean exit = false;
		
		if (cittadini_vaccinati == null) {
			System.out.println("Caricamento Cittadini Vaccinati...");
			cittadini_vaccinati = new HashMap<>();
			caricaCittadiniVaccinati();
			System.out.println("Caricamento Completato." + Utili.NEW_LINE);
		}
		if (cittadini_registrati == null) {
			System.out.println("Caricamento Cittadini Registrati...");
			cittadini_registrati = new HashMap<>();
			caricaCittadiniRegistrati();
			System.out.println("Caricamento completato." + Utili.NEW_LINE);
		}
		
		do {
			System.out.println("- Menu Cittadini -");
			
			if (logged_userID != null)
				System.out.println("[Utente corrente: " + logged_userID + "]");
			
			System.out.println("Quale operazione vuoi eseguire?" + Utili.NEW_LINE);
			System.out.println("1) Registrati");
			System.out.println("2) Cerca un centro vaccinale");
			System.out.println("3) Visualizza informazioni centro vaccinale");
			System.out.println("4) Segnala eventi avversi post-vaccinazione");
			if (logged_userID != null)
				System.out.println("5) Logout");
			System.out.println("0) Menu Principale");
			
			choice = Utili.leggiString(Utili.NEW_LINE + "> ", false).strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					registraCittadino();
					System.out.println();
					break;
				case "2":
					ArrayList<String> centri_trovati = scegliCriterioRicerca();
					if (centri_trovati == null)
						System.out.println("Funzione non disponibile: Nessun centro vaccinale registrato.");
					else {
						for (String centro : centri_trovati)
							System.out.println("- " + centro.substring(0, centro.indexOf(';')));
					}
					System.out.println();
					break;
				case "3":
					visualizzaInfoCentroVaccinale();
					break;
				case "4":
					inserisciEventiAvversi();
					System.out.println();
					break;
				case "5":
					if (logged_userID != null) {
						logged_userID = null;
						System.out.println("[Utente disconnesso]");
					}
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
		
	}
	
}
