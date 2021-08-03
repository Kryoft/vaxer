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

import centrivaccinali.CentriVaccinali;
import menu.Utili;

public class Cittadini {
	
	String nome;
	String cognome;
	String codice_fiscale;
	String indirizzo_email;
	String user_id;
	String password;
	String id_vaccinazione;
	
	public static void scegliCriterioRicerca() throws IOException {
		while (true) {
			String ricerca_per = Utili.leggiString("Ricerca per:\n1) Nome Centro\n2) Comune e Tipologia\n\n> ");
			
			if (ricerca_per.equals("1")) {
				String centro = Utili.leggiString("Nome Centro > ").strip();
				cercaCentroVaccinale(centro);
				break;
			} else if (ricerca_per.equals("2")) {
				String comune = Utili.leggiString("Comune del Centro > ").strip();
				String tipologia = Utili.inserisciTipologiaCentro("- Tipologia:\n\n1) Ospedaliero\n2) Aziendale\n3) Hub\n\n> ");
				cercaCentroVaccinale(comune, tipologia);
				break;
			} else {
				System.out.println("Scelta non valida, riprova");
			}
		}
	}
	
	public static void cercaCentroVaccinale(String nome_centro) throws IOException {
		// Cercare i centri il cui nome contiene la stringa passata come argomento nel file CentriVaccinali.dati;
		BufferedReader br = new BufferedReader(new FileReader("data/CentriVaccinali.dati"));
		String str;
		String[] columns = null;
		nome_centro = nome_centro.toLowerCase();
		
		while ((str = br.readLine()) != null) {
			str = str.toLowerCase();
			if (str.contains(nome_centro)) {
				// splitto la riga solo successivamente aver controllato se contiene 'nome_centro'
				// e non prima perché fare lo split di tutte le righe è più costoso in termini di performance.
				columns = str.split(";");
				if (columns[0].contains(nome_centro))
					System.out.println("- " + columns[0]);
			}
		}
		
		br.close();
	}
	
	public static void cercaCentroVaccinale(String comune, String tipologia) throws IOException {
		// Cercare i centri il cui comune e tipologia corrispondono ai dati passati come argomento nel file CentriVaccinali.dati;
		BufferedReader br = new BufferedReader(new FileReader("data/CentriVaccinali.dati"));
		String str;
		String[] address = null;
		String[] columns = null;
		
		comune = comune.toLowerCase();
		tipologia = tipologia.toLowerCase();
		
		// Scorre tutte le righe del file;
		while((str = br.readLine()) != null) {
			str = str.toLowerCase();
			if(str.contains(comune)) {
				
				// Splitta ogni riga in un array di stringhe;
				columns = str.split(";");
				
				// Se comune e tipologia sono contenute nelle rispettive colonne del file CentriVaccinali.dati allora stampa str; 
				if(columns[2].contains(tipologia)) {
					address = columns[1].split(",");
					if(address[1].contains(comune)) {
						
						//Viene stampata l'intera stringa per aiutare l'utente a visualizzare eventuali errori di ricerca da lui commessi;
						System.out.println("- " + str.replace("\"", "").replace(";", "; "));
					}
				}
			}
		}
		
		br.close();
	}
	
	public void visualizzaInfoCentroVaccinale(CentriVaccinali nome_centro) {
		/*
		 * Restituisce tutte le informazione del Centro selezionato (accedendo ai campi di 'nome_centro',
		 * ad es. nome_centro.nome, nome_centro.indirizzo) e, una volta ottenute, usa il nome per trovare
		 * il file Vaccinati_%nome%.dati, da cui calcolare il numero di segnalazioni e severità media per ogni evento avverso
		 * Es. mal di testa: severità media = 3.5, numero di segnalazioni = 10
		 */
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
			System.out.println("Quale operazione vuoi eseguire?");
			System.out.println("\n1) Registrati");
			System.out.println("2) Cerca un centro vaccinale");
			System.out.println("3) Segnala eventi avversi post-vaccinazione");
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
					scegliCriterioRicerca();
					break;
				case "3":
					System.out.println("Segnalazione eventi avversi...");
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
		
	}
	
}
