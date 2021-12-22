/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package menu;

import java.io.IOException;

/**
 * La classe {@code MainMenu} è il punto di partenza dell'esecuzione dell'applicazione, da cui è possibile,
 * attraverso l'uso del terminale, avviare la sezione di programma dedicata alla funzione richiesta.
 * <p>
 * L'applicazione permette di accedere alla sezione dedicata alla gestione dei centri vaccinali e alla 
 * sezione dedicata ai cittadini vaccinati, i quali possono accedere con le proprie credenziali e l'ID
 * ricevuto a vaccinazione eseguita per visualizzare eventuali informazioni sui centri vaccinali e
 * dichiarare eventuali eventi avversi riscontrati in seguito alla somministrazione del vaccino.
 * 
 * @see centrivaccinali.CentriVaccinali
 * @see cittadini.Cittadini
 * 
 * @author Cristian Corti
 * @author Manuel Marceca
 */

public class MainMenu {
	
	/**
	 * Il percorso di <code>CentriVaccinali.dati</code>.
	 * In <code>CentriVaccinali.dati</code> sono memorizzati i centri vaccinali registrati, con rispettivi: 
	 *  <br>
	 *  - Nome
	 *  <br>
	 *  - Indirizzo
	 *  <br>
	 *  - Tipologia (Struttura ospedaliera, Aziendale o Hub)
	 *  <br>
	 *  - Severità media
	 *  <br>
	 *  - Numero di segnalazioni per sintomo
	 */
	public static final String CENTRI_VACCINALI_PATH = "data/CentriVaccinali.dati";
	
	/**
	 * Il percorso di <code>Cittadini_Vaccinati.dati</code>.
	 * In <code>Cittadini_Vaccinati.dati</code> sono memorizzati i dati dei cittadini vaccinati, con rispettivi:
	 *  <br>
	 *  - ID di vaccinazione
	 *  <br>
	 *  - Nome
	 *  <br>
	 *  - Cognome
	 *  <br>
	 *  - Codice fiscale
	 *  <br>
	 *  - Nome del centro vaccinale in cui è stata somministrata la vaccinazione
	 *  <br>
	 *  - Data della somministrazione del vaccino
	 *  <br>
	 *  - Nome del vaccino somministrato
	 *  <br>
	 *  - Sintomi dichiarati in scala da 1 a 5
	 *  <br>
	 *  - Note opzionali relative ai sintomi
	 */
	public static final String CITTADINI_VACCINATI_PATH = "data/Cittadini_Vaccinati.dati";
	
	/**
	 * Il percorso di <code>Cittadini_Registrati.dati</code>.
	 * In <code>Cittadini_Registrati.dati</code> sono memorizzati i cittadini registrati, con rispettivi: 
	 *  <br>
	 * 	- User ID
	 * 	<br>
	 *	- Nome
	 *	<br>
	 *	- Cognome
	 *	<br>
	 *	- Codice fiscale
	 *	<br>
	 *	- E-mail
	 *	<br>
	 *	- Password criptata
	 *	<br>
	 *	- ID di vaccinazione
	 */
	public static final String CITTADINI_REGISTRATI_PATH = "data/Cittadini_Registrati.dati";
	
	/**
	 * Il main dell'applicazione.
	 * <br> Stampa sul terminale le varie opzioni tra cui scegliere in base alle 
	 * necessità dell'utente, ed in base alla selezione effettuata metodi di classi appartenenti al 
	 * package <code>centrivaccinali</code> o <code>cittadini</code> saranno eseguiti.
	 * 
	 * @param args
	 * 		Array di stringhe di utilizzo facoltativo
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static void main(String[] args) throws IOException {
		
		/**
		 * <code>choice</code> è un attributo di tipo String a cui sarà assegnata la stringa di input
		 * per la selezione delle opzioni nel terminale
		 */
		String choice;
		
		/** 
		 * <code>exit</code> è un attibuto di tipo boolean inizializzato a false, che cambierà in true nel
		 * caso in cui l'utente scelga l'opzione "0) Esci".
		 * In tal caso, il programma sarà terminato.
		 */
		boolean exit = false;
		
		do {
			//Menu principale usato per accedere ai main di Cittadini e CentriVaccinali;
			System.out.println("- Menu Principale -");
			System.out.println("A quale sezione vuoi accedere?" + Utili.NEW_LINE);
			System.out.println("1) Centri Vaccinali");
			System.out.println("2) Cittadini");
			System.out.println("0) Esci");
			
			choice = Utili.leggiString(Utili.NEW_LINE + "> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					System.out.println();
					centrivaccinali.CentriVaccinali.main(null);
					break;
				case "2":
					System.out.println();
					cittadini.Cittadini.main(null);
					break;
				default:
					System.out.println(Utili.NEW_LINE + "Scelta non valida, riprova." + Utili.NEW_LINE);
					break;
			}
		} while (!exit);
	}

}
