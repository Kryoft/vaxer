/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package cittadini;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
			System.out.println("\n- Scelta Criterio di Ricerca -");
			String ricerca_per = Utili.leggiString("Ricerca per:\n1) Nome Centro\n2) Comune e Tipologia\n\n> ");
			
			if (ricerca_per.equals("1")) {
				System.out.println("\n- Ricerca per Nome -");
				String centro = Utili.leggiString("Nome Centro > ").strip();
				return cercaCentroVaccinale(centro.toLowerCase());
			} else if (ricerca_per.equals("2")) {
				System.out.println("\n- Ricerca per Comune e Tipologia -");
				String comune = Utili.leggiString("Comune del Centro > ").strip();
				String tipologia = Utili.inserisciTipologiaCentro("Tipologia:\n\n1) Ospedaliero\n2) Aziendale\n3) Hub\n\n> ");
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
		
		while ((str = br.readLine()) != null) {
			if (str.toLowerCase().contains(nome_centro)) {
				centro = str.split(";")[0];
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
		
		// Scorre tutte le righe del file;
		while((str = br.readLine()) != null) {
			if(str.toLowerCase().contains(comune)) {
				
				// Splitta ogni riga in un array di stringhe;
				columns = str.toLowerCase().split(";");
				
				// Se comune e tipologia sono contenute nelle rispettive colonne del file CentriVaccinali.dati allora stampa str; 
				if(columns[2].contains(tipologia)) {
					address = columns[1].split(",");
					if(address[1].contains(comune)) {
						
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
		int scelta;	// Integer utilizzato per far selezionare all'utente un centro vaccinale nella lista centri_trovati;
		int counter = 1;
		
		System.out.println("\n- Centri Trovati -");
		for (String centro : centri_trovati) {
			System.out.println(counter++ + ") " + centro.substring(0, centro.indexOf(';')) + "\n");
		}
		
		// Se sono stati trovati dei centri l'utente può selezionarne uno;
		if(centri_trovati.size() > 0) {
			do {
				try {
				scelta = Integer.parseInt(Utili.leggiString("Seleziona uno dei centri sopra elencati per visualizzarne le informazioni: "));
				} catch (NumberFormatException nfe) {
					// se non inserisce un numero imposto scelta a -1 così richiede all'utente di inserire un numero valido
					scelta = -1;
				}
			} while(scelta <= 0 || scelta > centri_trovati.size());
			
			str = centri_trovati.get(--scelta);	// Recupera dalla lista centriTrovati il centro selezionato dall'utente;
			columns = str.split(";");
			System.out.println("\n- Informazioni -");
			// Costruisce e stampa la stringa di output per le informazioni sul centro vaccinale;
			System.out.println(String.format("Nome Centro: %s\nIndirizzo: %s\nTipologia: %s\n", columns[0], columns[1], columns[2]).replace("\"", ""));
			
			//TODO tramite il nome del centro cercare tutti i vaccinati nel centro di riferimento utilizzando il file Cittadini_Vaccinati.dati per mostrare la tabella riassuntiva degli eventi avversi segnalati;
			
		} else {
			System.out.println("Non ci sono centri con questo nome\n");
		}
	}
	
	public void registraCittadino() {
		/*
		 * Inserisce i campi dell'oggetto che invoca la funzione nel file Cittadini_Registrati.dati
		 * (sha-256 per crittazione)
		 */
	}
	
	public String sha256(String base) {
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
	
	public void inserisciEventiAvversi() {
		// Può essere invocato solo dopo aver effettuato il login;
	}
	
	public static void main(String[] args) throws IOException {
		
		String choice;
		boolean exit = false;
		
		do {
			System.out.println("- Menu Cittadini -");
			System.out.println("Quale operazione vuoi eseguire?\n");
			System.out.println("1) Registrati");
			System.out.println("2) Cerca un centro vaccinale");
			System.out.println("3) Visualizza informazioni centro vaccinale");
			System.out.println("4) Segnala eventi avversi post-vaccinazione");
			System.out.println("0) Menu Principale");
			
			choice = Utili.leggiString("\n> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					System.out.println("Registrazione...");
					break;
				case "2":
					ArrayList<String> centri_trovati = scegliCriterioRicerca();
					for (String centro : centri_trovati)
						System.out.println("- " + centro.substring(0, centro.indexOf(';')));
					break;
				case "3":
					System.out.println("\n- Visualizzazione Informazioni Centro -");
					visualizzaInfoCentroVaccinale();
					break;
				case "4":
					System.out.println("Segnalazione eventi avversi...");
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
		
	}
	
}
