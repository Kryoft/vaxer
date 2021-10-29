/*
 * Davide Spinelli, 744151, CO
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

public class Cittadini {
	
	private static final String SEPARATORE_SEVERITA = ",";
	private static final String SEPARATORE_SEVERITA_MEDIE = ",";
	private static final String SEPARATORE_NUMERO_SEGNALAZIONI = ",";
	private static final String SEPARATORE_NOTE_OPZIONALI = "|";
	
	// memorizzo la coppia "ID_vaccinazione, riga" così da poter ottenere subito la riga cui un cittadino è scritto dato il suo ID di vaccinazione
	public static Map<String, Long> cittadini_vaccinati = new HashMap<>();
	public static long numero_righe_file_cittadini_vaccinati = 0;
	// memorizzo la coppia "user_ID, riga" perché memorizzare "user_ID, password" direttamente avrebbe un gran peso sulla memoria RAM (la password è sempre di 64 caratteri).
	public static Map<String, Long> cittadini_registrati = new HashMap<>();
	public static long numero_righe_file_cittadini_registrati = 0;
	private static String logged_userID;
	private static String logged_ID;
	private static final String[] eventi_avversi = {"Mal di testa",
													"Febbre",
													"Dolori muscolari e articolari",
													"Linfoadenopatia",
													"Tachicardia",
													"Crisi ipertensiva"};
	
	private String nome;
	private String cognome;
	private String codice_fiscale;
	private String indirizzo_email;
	private String user_id;
	private String password;
	private String id_vaccinazione;
	
	public static ArrayList<String> scegliCriterioRicerca() throws IOException {
		while (true) {
			System.out.println(Utili.NEW_LINE + "- Scelta Criterio di Ricerca -");
			String ricerca_per = Utili.leggiString(String.format("Ricerca per:%s1) Nome Centro%s2) Comune e Tipologia%s%s> ",
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE,
																	Utili.NEW_LINE));
			
			if (ricerca_per.strip().equals("1")) {
				System.out.println(Utili.NEW_LINE + "- Ricerca per Nome -");
				String centro = Utili.leggiString("Nome Centro > ").strip();
				return cercaCentroVaccinale(centro);
			} else if (ricerca_per.strip().equals("2")) {
				System.out.println(Utili.NEW_LINE + "- Ricerca per Comune e Tipologia -");
				String comune = Utili.leggiString("Comune del Centro > ").strip();
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
	
	public static void visualizzaInfoCentroVaccinale() throws IOException {
		ArrayList<String> centri_trovati = scegliCriterioRicerca(); // Lista utilizzata per tenere traccia di tutti i centri vaccinali trovati in modo da permettere all'utente di selezionarne uno
		
		// Se sono stati trovati dei centri l'utente può selezionarne uno
		if (centri_trovati.isEmpty())
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
					output.append(String.format("%s - %s: %s (Severità media), %s (Numero di segnalazioni)",
												Utili.NEW_LINE,
												eventi_avversi[i],
												severita_medie[i],
												numero_segnalazioni[i]));
				}
				
				System.out.println(output.toString() + Utili.NEW_LINE);
			}
		}
	}
	
	public static int selezionaCentro(ArrayList<String> centri_trovati, String messaggio_di_scelta, String messaggio_di_annullamento) throws IOException {
		int scelta;  // Integer utilizzato per far selezionare all'utente un centro vaccinale nella lista centri_trovati;
		int counter = 1;
		
		for (String centro : centri_trovati) {
			System.out.println(counter++ + ") " + centro.substring(0, centro.indexOf(';')));
		}
		System.out.println("0) " + messaggio_di_annullamento + Utili.NEW_LINE);
		
		do {
			try {
				scelta = Integer.parseInt(Utili.leggiString(messaggio_di_scelta));
			} catch (NumberFormatException nfe) {
				// se non inserisce un numero imposto scelta a -1 così richiede all'utente di inserire un numero valido
				scelta = -1;
			}
		} while (scelta < 0 || scelta > centri_trovati.size());
		
		return scelta;  // if (scelta == 0) then l'utente vuole annullare la selezione
	}
	
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
			// tale ID sia anche in 'cittadini_vaccinati', perché solo i cittadini vaccinati possono aver eseguito
			// la registrazione. Per quegli ID imposto la riga associata in 'cittadini_vaccinati' negativa, in modo
			// che quando trovo un ID con riga negativa so che è già registrato e non permetto una nuova registrazione
			// con quell'ID. Inoltre, l'informazione riga rimane comunque, è solo negativa, quindi posso sapere a quale
			// riga è scritto il cittadino con quell'ID nel file Cittadini_Vaccinati.dati
			cittadini_vaccinati.replace(ID, -cittadini_vaccinati.get(ID));
		}
		
		br.close();
		return true;
	}
	
	public static void registraCittadino() throws IOException {
		/*
		 * Inserisce i campi dell'oggetto che invoca la funzione nel file Cittadini_Registrati.dati
		 * (sha-256 per crittazione)
		 */
		
		// Non si possono inserire stringhe ad una specifica posizione di un file di testo, ma solo modificare
		// i bytes già scritti in una certa posizione (si istanzia un RandomAccessFile in "rw" mode e si fa seek(long pos)
		// per spostarsi di 'pos' bytes nel file; successivamente si possono utilizzare i metodi write, come per esempio
		// writeBytes(String s) per MODIFICARE i byte successivi a 'pos' con quelli specificati come argomento).
		RandomAccessFile raf = new RandomAccessFile(MainMenu.CITTADINI_REGISTRATI_PATH, "rw");
		
		if (raf.length() == 0)
			raf.writeBytes("USER_ID;NOME;COGNOME;CODICE_FISCALE;EMAIL;PASSWORD;ID_VACCINAZIONE" + Utili.NEW_LINE);
		
		Cittadini cittadino = new Cittadini();
		
		cittadino.nome = Utili.leggiString("- Nome > ").strip().replace(";", "");
		cittadino.cognome = Utili.leggiString("- Cognome > ").strip().replace(";", "");
		cittadino.codice_fiscale = Utili.leggiString("- Codice Fiscale > ").strip().replace(";", "");
		cittadino.indirizzo_email = Utili.leggiString("- Indirizzo e-mail > ").strip().replace(";", "");	// (opzionale) controllo se si tratta di una email
		cittadino.user_id = Utili.leggiString("- User ID > ").strip().replace(";", "");
		cittadino.password = sha256(Utili.leggiString("- Password > "));
		
		Long num_riga = null;
		while (num_riga == null || num_riga < 0) {
			while ((cittadino.id_vaccinazione = Utili.leggiString("- ID Vaccinazione > ").strip().replace(";", "").toUpperCase()).length() != 16) {
				if (!Utili.leggiSiNo("Errore: L'ID di vaccinazione deve essere composto da 16 caratteri alfanumerici. Riprovare?")) {
					raf.close();
					return;
				}
			}
			
			num_riga = cittadini_vaccinati.get(cittadino.id_vaccinazione);
			
			if (num_riga == null)  // se la riga che corrisponde all'ID inserito non è nulla, allora esiste un cittadino con quell'ID
				if (Utili.leggiSiNo("Errore: L'ID inserito non corrisponde a nessun cittadino vaccinato. Riprovare?"))
					continue;
				else {
					raf.close();
					return;
				}
			else if (num_riga < 0) {
				if (Utili.leggiSiNo("Errore: L'ID inserito corrisponde ad un utente già registrato. Riprovare?"))
					continue;
				else {
					raf.close();
					return;
				}
			}
		}
		
		String riga = Utili.leggiRiga(MainMenu.CITTADINI_VACCINATI_PATH, num_riga);
		
		riga = String.format("%s;%s;%s;%s;%s;%s;%s%s", 
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
	
	// Può essere invocato solo dopo aver effettuato il login;
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
					choice = Integer.parseInt(Utili.leggiString(Utili.NEW_LINE + "> ").strip());
				} catch(NumberFormatException e) {
					System.out.println(Utili.NEW_LINE + "ERRORE: risposta non valida");
				}
			} while(choice < 0 || choice > 6);
			
			if (choice != 0) {
				choice--;
				
				do {
					try {
						severita[choice] = Integer.parseInt(Utili.leggiString("Inserire severità (da 1 a 5)" + Utili.NEW_LINE + "> "));
					} catch(NumberFormatException e) {
						System.out.println(Utili.NEW_LINE + "ERRORE: risposta non valida");
					}
				} while (severita[choice] < 1 || severita[choice] > 5);
				
				if (Utili.leggiSiNo("Desideri aggiungere una nota?")) {
					do {
						note[choice] = Utili.leggiString("Inserire nota (max 256 caratteri)" + Utili.NEW_LINE + "> ")
																								.replace("|", "")
																								.replace(";", "")
																								.strip();
					} while (note[choice].length() > 256);
				}
				
				choice++;
			}
			
		} while(choice != 0);
		
		// Controlla se l'utente ha inserito severità oppure no. Se tutti i valori nell'array severita sono a 0,
		// il cittadino non avrà inserito niente, perciò termino.
		boolean ha_inserito_valori = false;
		for (int val : severita)
			if (val != 0) {
				ha_inserito_valori = true;
				break;
			}
		if (!ha_inserito_valori)
			return false;
		
		// TODO: MEMORIZZARE SU FILE I DATI NEGLI ARRAY severita[] E note[]
		
		long riga_cittadino_su_file_vaccinati = cittadini_vaccinati.get(logged_ID);
		if (riga_cittadino_su_file_vaccinati < 0)
			riga_cittadino_su_file_vaccinati = -riga_cittadino_su_file_vaccinati;
		memorizzaEventiAvversi(severita, note, riga_cittadino_su_file_vaccinati);
		
		// ...
		
		return true;
	}
	
	private static void memorizzaEventiAvversi(int[] severita, String[] note_opzionali, long riga) throws IOException {
		// Se sono arrivato a questo punto, so già che ho da scrivere dei dati, quindi posso iniziare a copiare
		// nel file temporaneo dei Cittadini_Vaccinati le righe che precedono la riga del cittadino loggato correntemente.
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CITTADINI_VACCINATI_PATH));
		String temp_file_path = MainMenu.CITTADINI_VACCINATI_PATH.substring(0, MainMenu.CITTADINI_VACCINATI_PATH.lastIndexOf('.')) + "_temp" + MainMenu.CITTADINI_VACCINATI_PATH.substring(MainMenu.CITTADINI_VACCINATI_PATH.lastIndexOf('.'));
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp_file_path));
		
		long contatore_riga = 1;
		// non ho bisogno di controllare che br.readLine sia null perché sto cercando una riga che c'è sicuramente
		while (contatore_riga++ < riga) {
			bw.write(br.readLine() + Utili.NEW_LINE);
		}
		
		String line = br.readLine();
		String[] dati = line.split(";");
		StringBuilder nuova_riga = new StringBuilder();
		
		for (int i = 0; i < 7; i++)  // ricopio nella nuova_riga i primi 7 campi (da 0 a 6)
			nuova_riga.append(dati[i] + ';');
		
		int[] severita_sul_file = Stream.of(dati[7].split(SEPARATORE_SEVERITA)).mapToInt(Integer::parseInt).toArray();  // converte l'array di String con le severità prese dal file in un array di int [https://stackoverflow.com/a/37093052]
		StringBuilder nuove_severita = new StringBuilder();
		String[] note_sul_file = dati[8].split("\\|");  // https://stackoverflow.com/a/16311662
		StringBuilder nuove_note_opzionali = new StringBuilder();
			
		// Loop per l'inserimento delle severità e delle note opzionali in nuova_riga
		// i va da 0 a 10 perché itero ogni carattere del campo EVENTI_AVVERSI_E_SEVERITA
		for (int i = 0; i < 11; i++) {
			// dati[7] corrisponde al campo delle severità che sarà nel formato seguente: "0,3,0,2,0,0".
			if (i % 2 != 0) {  // Se i è dispari appendo il separatore, che per le severità sarà ',' e per le note sarà '|'
				nuove_severita.append(SEPARATORE_SEVERITA);
				nuove_note_opzionali.append(SEPARATORE_NOTE_OPZIONALI);
			} else {
				if (severita[i/2] != 0) {  // se la severità corrente è != 0 significa che la voglio inserire...
					if (severita_sul_file[i/2] != 0)  // ...però se dati[7].charAt(i) != '0' allora è già presente una severità. Chiedo quindi all'utente se vuole sostituirla con quella appena inserita.
						nuove_severita.append(Utili.leggiSiNo("Per l'evento avverso '" +
																eventi_avversi[i/2] +
																"' è già presente la severità " +
																severita_sul_file[i/2] +
																". Desideri sostituirla con " +
																severita[i/2] +
																"?")? severita[i/2] : 0);
					else  // se invece dati[7].charAt(i) == '0' la sostituisco senza chiederlo all'utente.
						nuove_severita.append(severita[i/2]);
				} else {  // se invece la severità corrente è 0 significa che non la voglio inserire...
					nuove_severita.append(0);  // ...quindi appendo 0
				}
				
				if (note_opzionali[i/2] != null) {  // se la nota_opzionale corrente è != null significa che la voglio inserire...
					// ...però se !note_sul_file[i/2].equals(#) allora è già presente una nota. Chiedo quindi all'utente se vuole sostituirla con quella appena inserita.
					if (!note_sul_file[i/2].equals("#"))
						nuove_note_opzionali.append(Utili.leggiSiNo("Per la nota opzionale riguardante l'evento avverso '" +
																	eventi_avversi[i/2] +
																	"' è già presente la nota '" +
																	note_sul_file[i/2] +
																	"'. Desideri sostituirla con '" +
																	note_opzionali[i/2] +
																	"'?")? note_opzionali[i/2] : note_sul_file[i/2]);
					else  // se invece note_sul_file[i/2].equals("#") la sostituisco senza chiederlo all'utente.
						nuove_note_opzionali.append(note_opzionali[i/2]);
				} else {  // se invece la nota corrente è nulla significa che non la voglio inserire...
					nuove_note_opzionali.append(note_sul_file[i/2]);  // ...quindi appendo quella già presente
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
		
		aggiornaSeveritaMediaENumeroSegnalazioni(dati[4], severita_sul_file, Stream.of(nuove_severita.toString()
																							.split(","))
																							.mapToInt(Integer::parseInt)
																							.toArray());
	}
	
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
	
	private static boolean login() throws IOException {
		boolean logged_in = false;
		Long riga;
		String user_ID;
		String[] dati = null;
		
		System.out.println(Utili.NEW_LINE + "- LOGIN -");
		
		do {
			user_ID = Utili.leggiString("- UserID > ");
		} while ((riga = cittadini_registrati.get(user_ID)) == null && Utili.leggiSiNo(Utili.NEW_LINE +
																		"L'UserID non esiste. Riprovare?"));
		
		if (riga != null) {
			dati = Utili.leggiRiga(MainMenu.CITTADINI_REGISTRATI_PATH, riga).split(";");
		}
		
		if (dati[5] != null) {
			do {
				logged_in = (dati[5].equals(sha256(Utili.leggiString("- Password > ")))) ? true : false;
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
	
	public static void main(String[] args) throws IOException {
		
		String choice;
		boolean exit = false;
		
		do {
			System.out.println("- Menu Cittadini -");
			
			if (cittadini_vaccinati.isEmpty()) {
				System.out.println("Caricamento Cittadini Vaccinati...");
				caricaCittadiniVaccinati();
				System.out.println("Caricamento Completato." + Utili.NEW_LINE);
			}
			if (cittadini_registrati.isEmpty()) {
				System.out.println("Caricamento Cittadini Registrati...");
				caricaCittadiniRegistrati();
				System.out.println("Caricamento completato." + Utili.NEW_LINE);
			}
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
			
			choice = Utili.leggiString(Utili.NEW_LINE + "> ").strip();
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
					for (String centro : centri_trovati)
						System.out.println("- " + centro.substring(0, centro.indexOf(';')));
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
