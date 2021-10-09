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

import menu.MainMenu;
import menu.Utili;

public class Cittadini {
	
	// memorizzo la coppia "ID_vaccinazione, riga" cos� da poter ottenere subito la riga cui un cittadino � scritto dato il suo ID di vaccinazione
	public static Map<String, Long> cittadini_vaccinati = new HashMap<>();
	public static long numero_righe_file_cittadini_vaccinati = 0;
	// memorizzo la coppia "user_ID, riga" perch� memorizzare "user_ID, password" direttamente avrebbe un gran peso sulla memoria RAM (la password � sempre di 64 caratteri).
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
			
			if (ricerca_per.equals("1")) {
				System.out.println(Utili.NEW_LINE + "- Ricerca per Nome -");
				String centro = Utili.leggiString("Nome Centro > ").strip();
				return cercaCentroVaccinale(centro);
			} else if (ricerca_per.equals("2")) {
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
		String centro;
		nome_centro = nome_centro.toLowerCase();
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		while ((str = br.readLine()) != null) {
			if (str.toLowerCase().contains(nome_centro)) {
				centro = str.split(";")[0];						//sostituibile con un for o un substring?
				if (centro.toLowerCase().contains(nome_centro))
					centri_trovati.add(str);
//					System.out.println("- " + str.substring(1, str.length()-1) + "\n");
			}
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
					address = columns[1].split(",");
					if (address[1].contains(comune)) {
						
						centri_trovati.add(str);
						// Viene stampata l'intera stringa per aiutare l'utente a visualizzare eventuali errori di ricerca da lui commessi;
//						System.out.println("- " + str.replace(";", "; "));
					}
				}
			}
		}
		
		br.close();
		return centri_trovati;
	}
	
	public static void visualizzaInfoCentroVaccinale() throws IOException {
		ArrayList<String> centri_trovati = scegliCriterioRicerca(); // Lista utilizzata per tenere traccia di tutti i centri vaccinali trovati in modo da permettere all'utente di selezionarne uno;
		String[] columns = null;
		String str;
		
		// Se sono stati trovati dei centri l'utente pu� selezionarne uno;
		if (centri_trovati.isEmpty())
			System.out.println("Non ho trovato centri con questo nome" + Utili.NEW_LINE);
		else {
			System.out.println(Utili.NEW_LINE + "- Centri Trovati -");
			int scelta = selezionaCentro(centri_trovati, "Seleziona uno dei centri sopra elencati per visualizzarne le informazioni: ", "Annulla");
			
			if (scelta == 0)
				return;
			
			str = centri_trovati.get(--scelta);	// Recupera dalla lista centriTrovati il centro selezionato dall'utente;
			columns = str.split(";");
			System.out.println(Utili.NEW_LINE + "- Informazioni -");
			// Costruisce e stampa la stringa di output per le informazioni sul centro vaccinale;
			System.out.println(String.format("Nome Centro: %s%sIndirizzo: %s%sTipologia: %s%s",
												columns[0],
												Utili.NEW_LINE,
												columns[1],
												Utili.NEW_LINE,
												columns[2],
												Utili.NEW_LINE));
			
			//TODO tramite il nome del centro cercare tutti i vaccinati nel centro di riferimento utilizzando il file Cittadini_Vaccinati.dati per mostrare la tabella riassuntiva degli eventi avversi segnalati;
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
				// se non inserisce un numero imposto scelta a -1 cos� richiede all'utente di inserire un numero valido
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
			// tale ID sia anche in 'cittadini_vaccinati', perch� solo i cittadini vaccinati possono aver eseguito
			// la registrazione. Per quegli ID imposto la riga associata in 'cittadini_vaccinati' negativa, in modo
			// che quando trovo un ID con riga negativa so che � gi� registrato e non permetto una nuova registrazione
			// con quell'ID. Inoltre, l'informazione riga rimane comunque, � solo negativa, quindi posso sapere a quale
			// riga � scritto il cittadino con quell'ID nel file Cittadini_Vaccinati.dati
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
		// i bytes gi� scritti in una certa posizione (si istanzia un RandomAccessFile in "rw" mode e si fa seek(long pos)
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
	
	// Pu� essere invocato solo dopo aver effettuato il login;
	public static boolean inserisciEventiAvversi() throws IOException {
		if (logged_userID == null && !login())
			return false;
		
		String choice = "";
		int check = -1;	// Variabile utilizzata per gestire la scelta dell'utente. Inizializzata a -1 per evitare errori dati dal compilatore;
		int severita[] = new int[6];
		String note[] = new String[6];
		
		int sintomo_selezionato;
		
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
					choice = Utili.leggiString(Utili.NEW_LINE + "> ").strip();
					check = Integer.parseInt(choice);
				} catch(NumberFormatException e) {
					System.out.println(Utili.NEW_LINE + "ERRORE: risposta non valida");
				}
			} while(check < 0 || check > 6);
			
			if (!choice.equals("0")) {
				sintomo_selezionato = Integer.parseInt(choice) - 1;	//Variabile utilizzata per identificare la posizione del sintomo nell'array severita;
				
				do {
					try {
						severita[sintomo_selezionato] = Integer.parseInt(Utili.leggiString("Inserire severit� (da 1 a 5)" + Utili.NEW_LINE + "> "));
					} catch(NumberFormatException e) {
						System.out.println(Utili.NEW_LINE + "ERRORE: risposta non valida");
					}
				} while (severita[sintomo_selezionato] < 1 || severita[sintomo_selezionato] > 5);
				
				if (Utili.leggiSiNo("Desideri aggiungere una nota?")) {
					do {
						note[sintomo_selezionato] = Utili.leggiString("Inserire nota (max 256 caratteri)" + Utili.NEW_LINE + "> ");
						note[sintomo_selezionato].replace("|", "");
					} while (note[sintomo_selezionato].length() > 256);
				}
			}
			
		} while(!choice.equals("0"));
		
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
		
		// TODO: MEMORIZZARE SU FILE I DATI NEGLI ARRAY severita[] E note[]
		
		long riga_cittadino_su_file_vaccinati = cittadini_vaccinati.get(logged_ID);
		if (riga_cittadino_su_file_vaccinati < 0)
			riga_cittadino_su_file_vaccinati = -riga_cittadino_su_file_vaccinati;
		memorizzaEventiAvversi(severita, note, riga_cittadino_su_file_vaccinati);
		
		// ...
		
		return true;
	}
	
	private static void memorizzaEventiAvversi(int[] severita, String[] note_opzionali, long riga) throws IOException {
		// Se sono arrivato a questo punto, so gi� che ho da scrivere dei dati, quindi posso iniziare a copiare
		// nel file temporaneo dei Cittadini_Vaccinati le righe che precedono la riga del cittadino loggato correntemente.
		BufferedReader br = new BufferedReader(new FileReader(MainMenu.CITTADINI_VACCINATI_PATH));
		String temp_file_path = "data/Cittadini_Vaccinati_temp.dati";
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp_file_path));
		
		long contatore_riga = 1;
		// non ho bisogno di controllare che br.readLine sia null perch� sto cercando una riga che c'� sicuramente
		while (contatore_riga++ < riga) {
			bw.write(br.readLine() + Utili.NEW_LINE);
		}
		
		String line = br.readLine();
		String[] dati = line.split(";");
		StringBuilder nuova_riga = new StringBuilder();
		
		for (int i = 0; i < 7; i++)  // ricopio nella nuova_riga i primi 7 campi (da 0 a 6)
			nuova_riga.append(dati[i] + ';');
		
		StringBuilder nuove_severita = new StringBuilder();
		StringBuilder nuove_note_opzionali = new StringBuilder();
		String[] note_sul_file = dati[8].split("\\|");  // https://stackoverflow.com/a/16311662
			
		// Loop per l'inserimento delle severit� e delle note opzionali in nuova_riga
		for (int i = 0; i < 11; i++) {
			// dati[7] corrisponde al campo delle severit� che sar� nel formato seguente: "0,3,0,2,0,0".
			if (i % 2 != 0) {  // Se i � dispari appendo il divisore
				nuove_severita.append(',');
				nuove_note_opzionali.append('|');
			} else {
				if (severita[i/2] != 0) {  // se la severit� corrente � != 0 significa che la voglio inserire...
					if (dati[7].charAt(i) != '0')  // ...per� se dati[7].charAt(i) != '0' allora � gi� presente una severit�. Chiedo quindi all'utente se vuole sostituirla con quella appena inserita.
						nuove_severita.append(Utili.leggiSiNo("Per l'evento avverso '" +
																eventi_avversi[i/2] +
																"' � gi� presente la severit� " +
																dati[7].charAt(i) +
																". Desideri sostituirla con " +
																severita[i/2] +
																"?")? severita[i/2] : Character.toString(dati[7].charAt(i)));  // NB: � necessario convertire dati[7].charAt(i) in String perch� altrimenti il metodo append converte il char nel numero relativo alla posizione di quel carattere nella tabella ASCII
					else  // se invece dati[7].charAt(i) == '0' la sostituisco senza chiederlo all'utente.
						nuove_severita.append(severita[i/2]);
				} else {  // se invece la severit� corrente � 0 significa che non la voglio inserire...
					nuove_severita.append(Character.toString(dati[7].charAt(i)));  // ...quindi appendo quella gi� presente
				}
				
				if (note_opzionali[i/2] != null) {  // se la nota_opzionale corrente � != null significa che la voglio inserire...
					// ...per� se !note_sul_file[i/2].equals(#) allora � gi� presente una nota. Chiedo quindi all'utente se vuole sostituirla con quella appena inserita.
					if (!note_sul_file[i/2].equals("#"))
						nuove_note_opzionali.append(Utili.leggiSiNo("Per la nota opzionale riguardante l'evento avverso '" +
																	eventi_avversi[i/2] +
																	"' � gi� presente la nota " +
																	note_sul_file[i/2] +
																	". Desideri sostituirla con " +
																	note_opzionali[i/2] +
																	"?")? note_opzionali[i/2] : note_sul_file[i/2]);
					else  // se invece note_sul_file[i/2].equals("#") la sostituisco senza chiederlo all'utente.
						nuove_note_opzionali.append(note_opzionali[i/2]);
				} else {  // se invece la nota corrente � nulla significa che non la voglio inserire...
					nuove_note_opzionali.append(note_sul_file[i/2]);  // ...quindi appendo quella gi� presente
				}
			}
		}
		nuova_riga.append(nuove_severita + ";" + nuove_note_opzionali);
		
		bw.write(nuova_riga.toString() + Utili.NEW_LINE);
		
		while ((line = br.readLine()) != null) {
			bw.write(line + Utili.NEW_LINE);
		}
		
		br.close();
		bw.close();
		
		File to_delete = new File(MainMenu.CITTADINI_VACCINATI_PATH);
		to_delete.delete();
		File to_rename = new File("data/Cittadini_Vaccinati_temp.dati");
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
