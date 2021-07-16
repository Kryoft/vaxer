/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package cittadini;

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
	
	public void cercaCentroVaccinale(String nome_centro) {
		// Cercare i centri il cui nome contiene la stringa passata come argomento nel file CentriVaccinali.dati;
	}
	
	public void cercaCentroVaccinale(String comune, byte tipologia) {
		// Cercare i centri il cui comune e tipologia corrispondono ai dati passati come argomento nel file CentriVaccinali.dati;
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
					System.out.println("Ricerca centro vaccinale...");
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
