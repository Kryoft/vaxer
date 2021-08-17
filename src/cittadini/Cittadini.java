/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package cittadini;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;

import menu.Utili;

public class Cittadini {
	
	String nome;
	String cognome;
	String codice_fiscale;
	String indirizzo_email;
	String user_id;
	String password;
	String id_vaccinazione;
	
	public static ArrayList<String> scegliCriterioRicerca() throws IOException {
		while (true) {
			System.out.println(Utili.new_line + "- Scelta Criterio di Ricerca -");
			String ricerca_per = Utili.leggiString(String.format("Ricerca per:%s1) Nome Centro%s2) Comune e Tipologia%s%s> ",
																	Utili.new_line,
																	Utili.new_line,
																	Utili.new_line,
																	Utili.new_line));
			
			if (ricerca_per.equals("1")) {
				System.out.println(Utili.new_line + "- Ricerca per Nome -");
				String centro = Utili.leggiString("Nome Centro > ").strip();
				return cercaCentroVaccinale(centro.toLowerCase());
			} else if (ricerca_per.equals("2")) {
				System.out.println(Utili.new_line + "- Ricerca per Comune e Tipologia -");
				String comune = Utili.leggiString("Comune del Centro > ").strip();
				String tipologia = Utili.inserisciTipologiaCentro(String.format("- Tipologia:%s1) Ospedaliero%s2) Aziendale%s3) Hub%s%s> ",
																	Utili.new_line,
																	Utili.new_line,
																	Utili.new_line,
																	Utili.new_line,
																	Utili.new_line));
				return cercaCentroVaccinale(comune, tipologia);
			} else {
				System.out.println("Scelta non valida, riprova");
			}
		}
	}
	
	public static ArrayList<String> cercaCentroVaccinale(String nome_centro) throws IOException {
		// Cercare i centri il cui nome contiene la stringa passata come argomento nel file CentriVaccinali.dati;
		BufferedReader br = new BufferedReader(new FileReader("data/CentriVaccinali.dati"));
		ArrayList<String> centri_trovati = new ArrayList<>();
		String str;
		String centro;
		nome_centro = nome_centro.toLowerCase();
		
		br.readLine();  // Leggo la prima riga e la scarto in quanto contiene i campi
		
		while ((str = br.readLine()) != null) {
			if (str.toLowerCase().contains(nome_centro)) {
				centro = str.split(";")[0];						//sostituibile con un for o un substring?
				if (centro.toLowerCase().contains(nome_centro))
					centri_trovati.add(str.replace("\"", ""));
//					System.out.println("- " + str.substring(1, str.length()-1) + "\n");
			}
		}
		
		br.close();
		return centri_trovati;
	}
	
	public static ArrayList<String> cercaCentroVaccinale(String comune, String tipologia) throws IOException {
		// Cercare i centri il cui comune e tipologia corrispondono ai dati passati come argomento nel file CentriVaccinali.dati;
		BufferedReader br = new BufferedReader(new FileReader("data/CentriVaccinali.dati"));
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
						
						centri_trovati.add(str.replace("\"", ""));
						// Viene stampata l'intera stringa per aiutare l'utente a visualizzare eventuali errori di ricerca da lui commessi;
//						System.out.println("- " + str.replace("\"", "").replace(";", "; "));
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
		
		// Se sono stati trovati dei centri l'utente può selezionarne uno;
		if (centri_trovati.isEmpty())
			System.out.println("Non ho trovato centri con questo nome" + Utili.new_line);
		else {
			System.out.println(Utili.new_line + "- Centri Trovati -");
			int scelta = selezionaCentro(centri_trovati, "Seleziona uno dei centri sopra elencati per visualizzarne le informazioni: ", "Annulla");
			
			if (scelta == 0)
				return;
			
			str = centri_trovati.get(--scelta);	// Recupera dalla lista centriTrovati il centro selezionato dall'utente;
			columns = str.split(";");
			System.out.println(Utili.new_line + "- Informazioni -");
			// Costruisce e stampa la stringa di output per le informazioni sul centro vaccinale;
			System.out.println(String.format("Nome Centro: %s%sIndirizzo: %s%sTipologia: %s%s",
												columns[0],
												Utili.new_line,
												columns[1],
												Utili.new_line,
												columns[2],
												Utili.new_line).replace("\"", ""));
			
			//TODO tramite il nome del centro cercare tutti i vaccinati nel centro di riferimento utilizzando il file Cittadini_Vaccinati.dati per mostrare la tabella riassuntiva degli eventi avversi segnalati;
		}
	}
	
	public static int selezionaCentro(ArrayList<String> centri_trovati, String messaggio_di_scelta, String messaggio_di_annullamento) throws IOException {
		int scelta;  // Integer utilizzato per far selezionare all'utente un centro vaccinale nella lista centri_trovati;
		int counter = 1;
		
		for (String centro : centri_trovati) {
			System.out.println(counter++ + ") " + centro.substring(0, centro.indexOf(';')));
		}
		System.out.println("0) " + messaggio_di_annullamento + Utili.new_line);
		
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
	
	public static void registraCittadino() throws IOException {
		/*
		 * Inserisce i campi dell'oggetto che invoca la funzione nel file Cittadini_Registrati.dati
		 * (sha-256 per crittazione)
		 */
		
		// Non si possono inserire stringhe ad una specifica posizione di un file di testo, ma solo modificare
		// i bytes già scritti in una certa posizione (si istanzia un RandomAccessFile in "rw" mode e si fa seek(long pos)
		// per spostarsi di 'pos' bytes nel file; successivamente si possono utilizzare i metodi write, come per esempio
		// writeBytes(String s) per MODIFICARE i byte successivi a 'pos' con quelli specificati come argomento).
		RandomAccessFile raf = new RandomAccessFile("data/Cittadini_Registrati.dati", "rw");
		
		if (raf.length() == 0)
			raf.writeBytes("NOME;COGNOME;CODICE_FISCALE;EMAIL;USER_ID;PASSWORD;ID_VACCINAZIONE" + Utili.new_line);
		
		Cittadini cittadino = new Cittadini();
		
		cittadino.nome = Utili.leggiString("- Nome > ").strip().replace(";", "");
		cittadino.cognome = Utili.leggiString("- Cognome > ").strip().replace(";", "");
		cittadino.codice_fiscale = Utili.leggiString("- Codice Fiscale > ").strip().replace(";", "");
		cittadino.indirizzo_email = Utili.leggiString("- Indirizzo e-mail > ").strip().replace(";", "");	//(opzionale) controllo se si tratta di una email
		cittadino.user_id = Utili.leggiString("- User ID > ").strip().replace(";", "");
		cittadino.password = sha256(Utili.leggiString("- Password > "));
		cittadino.id_vaccinazione = Utili.leggiString("- ID Vaccinazione > ").strip().replace(";", "");
		
		String riga = String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"%s", 
				cittadino.nome,
				cittadino.cognome,
				cittadino.codice_fiscale,
				cittadino.indirizzo_email,
				cittadino.user_id,
				cittadino.password,
				cittadino.id_vaccinazione,
				Utili.new_line);
		
		raf.seek(raf.length());  // imposta la posizione del puntatore del RAF alla fine del file
		raf.writeBytes(riga);
		
		raf.close();
	}
	
	public static String sha256(String base) {
		/*
		 * https://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java/25243174#25243174 -- Spiegazione funzionamento codice
		 */
	    try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}
	
	// Metodo LOGIN da fare quando si avrà un'idea più precisa;
	
	public static void inserisciEventiAvversi() throws IOException {
		// Può essere invocato solo dopo aver effettuato il login;
		
		String choice = "";
		int check = -1;	//Variabile utilizzata per gestire la scelta dell'utente. Inizializzata a -1 per evitare errori dati dal complatore;
		int severita[] = new int[6];
		String note[] = new String[6];
		
		int sintomo_selezionato;
		
		do {	
			
			do {
				
				System.out.println(Utili.new_line + "SEGNALAZIONE EVENTI AVVERSI");
				Utili.aCapo(2);
				System.out.println("Selezionare il sintomo incontrato");
				System.out.println(Utili.new_line + "1) Mal di testa");
				System.out.println("2) Febbre");
				System.out.println("3) Dolori muscolari e articolari");
				System.out.println("4) Linfoadenopatia");
				System.out.println("5) Tachicardia");
				System.out.println("6) Crisi ipertensiva");
				System.out.println("0) Termina");
				
				try {
					choice = Utili.leggiString(Utili.new_line + "> ").strip();
					check = Integer.parseInt(choice);
				} catch(NumberFormatException e) {
					System.out.println(Utili.new_line + "ERRORE: risposta non valida");
				}
				
			} while(check < 0 || check >= 6);
			
			if(!choice.equals("0")) {
				sintomo_selezionato = Integer.parseInt(choice) - 1;	//Variabile utilizzata per identificare la posizione del sintomo nell'array severita;
						
				do {
					try{
						severita[sintomo_selezionato] = Integer.parseInt(Utili.leggiString("Inserire severità (da 1 a 5)" + Utili.new_line + ">"));
					} catch(NumberFormatException e) {
						System.out.println(Utili.new_line + "ERRORE: risposta non valida");
					}
				} while(severita[sintomo_selezionato] < 1 || severita[sintomo_selezionato] > 5);
						
				if(Utili.leggiSiNo("Desideri aggiungere una nota?")) {
					
					do {
						note[sintomo_selezionato] = Utili.leggiString("Inserire nota (max 256 caratteri)" + Utili.new_line + ">");
					} while(note[sintomo_selezionato].length() > 256);
					
				}	
			}
			
		} while(!choice.equals("0"));
		
		//MEMORIZZARE SU FILE I DATI NEGLI ARRAI severita[] E note[]
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		
		String choice;
		boolean exit = false;
		
		do {
			System.out.println("- Menu Cittadini -");
			System.out.println("Quale operazione vuoi eseguire?" + Utili.new_line);
			System.out.println("1) Registrati");
			System.out.println("2) Cerca un centro vaccinale");
			System.out.println("3) Visualizza informazioni centro vaccinale");
			System.out.println("4) Segnala eventi avversi post-vaccinazione");
			System.out.println("0) Menu Principale");
			
			choice = Utili.leggiString(Utili.new_line + "> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					registraCittadino();
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
					//System.out.println("Segnalazione eventi avversi...");
					inserisciEventiAvversi();
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
		
	}
	
}
