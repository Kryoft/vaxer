/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package centrivaccinali;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import cittadini.Cittadini;
import menu.MainMenu;
import menu.Utili;

/**
 * 
 * <code>CentriVaccinali</code> contiene tutte le operazioni necessarie alla gestione dei centri vaccinali,
 * utilizzando come appoggio vari metodi contenuti in <code>menu.Utili</code>.
 * 
 * @see menu.Utili
 * 
 * @author Cristian Corti
 * @author Manuel Marceca
 *
 */
public class CentriVaccinali {
	
	/**
	 * Attributo che memorizza il nome del centro in formato <code>String</code>.
	 */
	String nome_centro;
	
	/**
	 * Attributo di tipo <code>Indirizzo</code> che memorizza in un record i dati riguardanti l'indirizzo del 
	 * relativo centro vaccinale.
	 * 
	 * @see centrivaccinali.Indirizzo
	 */
	Indirizzo indirizzo;
	
	/**
	 * Attributo di tipo <code>String</code> in cui è memorizzata la tipologia di centro vaccinale 
	 * (Ospedaliero, Aziendale o Hub).
	 */
	String tipologia;
	
	// Lista principali centri vaccinali in Italia : https://www.governo.it/it/cscovid19/report-vaccini/
	/**
	 * Registra un centro vaccinale memorizzando i dati nell'oggetto <code>centro</code> e li scrive sul relativo 
	 * file di testo <code>CentriVaccinali.dati</code>.
	 * Nel caso in cui tale file non esista, verrà creato.
	 * Se invece il file esiste ma contiene già un centro con lo stesso nome, <code>null</code> verrà ritornato.
	 * 
	 * @see menu.MainMenu
	 * 
	 * @return <strong>centro</strong>
	 * 		un oggetto di tipo <code>CentriVaccinali</code> contenente i dati del centro registrato.<br>
	 * 		<strong>null</strong> se il centro con il nome specificato dall'utente era già presente.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 * 	
	 */
	public static CentriVaccinali registraCentroVaccinale() throws IOException {
		// Inizializza un oggetto "CentriVaccinali" e successivamente richiede le informazioni necessarie all'utente;
		CentriVaccinali centro = new CentriVaccinali();
		
		System.out.println("Inserisci le informazioni richieste:");
		centro.nome_centro = Utili.leggiString("- Nome del centro > ").strip().replace(";", "");
		
		// Controlla se il file CentriVaccinali.dati esiste già oppure no.
		// Nel caso non esistesse lo creerebbe ed inserirebbe i nomi dei campi, ...
		if (!Files.exists(Paths.get(MainMenu.CENTRI_VACCINALI_PATH)))
			Utili.scriviSuFile(MainMenu.CENTRI_VACCINALI_PATH, true, "NOME;INDIRIZZO;TIPOLOGIA;SEVERITA_MEDIA;NUMERO_SEGNALAZIONI" + Utili.NEW_LINE);
		// ...nel caso invece esistesse controllerebbe se fosse già presente un centro con quel nome.
		else {
			ArrayList<String> centri = Cittadini.cercaCentroVaccinale(centro.nome_centro);
			for (String centro_vaccinale : centri)
				if (centro.nome_centro.equals(centro_vaccinale))
					return null;
		}
		
		System.out.println("- Indirizzo:");
		centro.indirizzo = new Indirizzo();
		centro.indirizzo.qualificatore = Utili.leggiString("    1. Qualificatore (via/v.le/pzza/strada/...) > ").strip().replace(";", "").replace(",", "");
		centro.indirizzo.nome = Utili.leggiString("    2. Nome (Giuseppe Garibaldi, Roma, ...) > ").strip().replace(";", "").replace(",", "");
		centro.indirizzo.numero_civico = Utili.leggiString("    3. Numero Civico > ").strip().replace(";", "").replace(",", "");
		centro.indirizzo.comune = Utili.leggiString("    4. Comune > ").strip().replace(";", "").replace(",", "");
		centro.indirizzo.sigla_provincia = Utili.leggiString("    5. Sigla della Provincia > ").strip().replace(";", "").replace(",", "");
		centro.indirizzo.cap = Utili.leggiString("    6. CAP > ").strip().replace(";", "").replace(",", "");
		centro.tipologia = Utili.inserisciTipologiaCentro(String.format("- Tipologia:%s1) Ospedaliero%s2) Aziendale%s3) Hub%s%s> ",
																			Utili.NEW_LINE,
																			Utili.NEW_LINE,
																			Utili.NEW_LINE,
																			Utili.NEW_LINE,
																			Utili.NEW_LINE));
		System.out.println();
		
		// Scrive sul file CentriVaccinali.dati il nuovo centro vaccinale;
		Utili.scriviSuFile(MainMenu.CENTRI_VACCINALI_PATH, true,
							String.format("%s;%s;%s;0,0,0,0,0,0;0,0,0,0,0,0%s",
									centro.nome_centro,
									centro.indirizzo.toString(),
									centro.tipologia,
									Utili.NEW_LINE));
		
		// Restituisce il nuovo centro vaccinale;
		return centro;
	}
	
	//Modificato il valore restituito da void a boolean per restituire false se il centro vaccinale in cui si vogliono inserire i dati non esiste;
	/**
	 * Registra un vaccinato richiedendo all'utente di inserire i dati,
	 * che saranno poi scritti sul file <code>Cittadini_Vaccinati.dati</code>.
	 * Un nuovo ID viene generato per tale scopo. Nel caso in cui tale file non esista, verrà creato.
	 * 
	 * @return <strong>boolean</strong>
	 * 		In caso di fallimento dell'operazione (centro vaccinale non trovato) <code>false</code> è ritornato.
	 * 		In caso di successo, <code>true</code> è ritornato.
	 * 
	 * @throws IOException	
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */
	public static boolean registraVaccinato() throws IOException {
		/* 
		 * Campi per il file Cittadini_Vaccinati:
		 * ID_VACCINAZIONE;NOME_CITTADINO;COGNOME_CITTADINO;CODICE_FISCALE;NOME_CENTRO_VACCINALE;DATA_SOMMINISTRAZIONE_VACCINO;VACCINO_SOMMINISTRATO;EVENTI_AVVERSI_E_SEVERITA;NOTE_OPZIONALI
		 * Esempio di severità e note opzionali:
		 * [...];4, 0, 0, 0, 0, 0;mi fa male la testa, *, *, *, *, *
		 */
		
		String nome_centro;
		
		System.out.println("Inserisci le informazioni richieste:");
		String nome_cittadino = Utili.leggiString("    1. Nome del Cittadino > ").strip().replace(";", "");
		String cognome_cittadino = Utili.leggiString("    2. Cognome del Cittadino > ").strip().replace(";", "");
		String codice_fiscale = Utili.leggiString("    3. Codice Fiscale > ").strip().replace(";", "");
		
		if ((nome_centro = ottieniNomeCentro()) == null)  // prima esegue l'assegnamento a nome_centro (dopo aver chiamato il metodo ottieniNomeCentro), poi verifica la condizione nome_centro == null
			return false;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date data_vaccinazione;
		while (true) {
			try {
				data_vaccinazione = sdf.parse(Utili.leggiString("    5. Data della Vaccinazione ('giorno/mese/anno' es. 15/03/2021):"));
				break;
			} catch (ParseException e) {
				System.out.println("Data non valida.");
			}
		}
		String data_vacc = sdf.format(data_vaccinazione);
		
		String nome_vaccino = Utili.leggiString("    6. Nome del Vaccino > ").strip().replace(";", "");
		
		String file_path = MainMenu.CITTADINI_VACCINATI_PATH;
		
		String nuovo_id_vaccinazione = generaNuovoId();
		
		if (!Files.exists(Paths.get(file_path)))
			Utili.scriviSuFile(file_path, true, "ID_VACCINAZIONE;NOME_CITTADINO;COGNOME_CITTADINO;CODICE_FISCALE;NOME_CENTRO_VACCINALE;DATA_SOMMINISTRAZIONE_VACCINO;VACCINO_SOMMINISTRATO;EVENTI_AVVERSI_E_SEVERITA;NOTE_OPZIONALI" + Utili.NEW_LINE);
		
		Utili.scriviSuFile(file_path, true, String.format("%s;%s;%s;%s;%s;%s;%s;0,0,0,0,0,0;#|#|#|#|#|#%s",
				nuovo_id_vaccinazione,
				nome_cittadino,
				cognome_cittadino,
				codice_fiscale,
				nome_centro,
				data_vacc,
				nome_vaccino,
				Utili.NEW_LINE));
		
		Cittadini.cittadini_vaccinati.put(nuovo_id_vaccinazione, ++Cittadini.numero_righe_file_cittadini_vaccinati);
		
		return true;
	}
	
	/**
	 * Chiede all'utente di digitare il nome del centro e ne verifica la presenza nel file <code>CentriVaccinali.dati</code>.
	 * Nel caso nessuna corrispondenza è trovata, ritorna <code>null</code>.
	 * 
	 * @return <strong>nome_centro</strong>
	 * 		Il nome del centro a cui corrisponde la ricerca effettuata secondo l'input dell'utente.
	 * 		Nel caso in cui il processo fallisca, <code>null</code> viene ritornato.
	 * 
	 * @throws IOException
	 * 		Viene chiamata un'eccezione nel caso si verifichi un qualsiasi errore legato a input/output.
	 */		
	private static String ottieniNomeCentro() throws IOException {
		String nome_centro;
		ArrayList<String> centri_trovati;
		
		// loop che fa ricercare e selezionare all'operatore il centro vaccinale dove il cittadino ha eseguito la vaccinazione
		while (true) {
			nome_centro = Utili.leggiString("    4. Centro in cui il Cittadino ha eseguito la Vaccinazione (\"*esci\" per annullare) > ");
			centri_trovati = Cittadini.cercaCentroVaccinale(nome_centro);
			
			if (nome_centro.equals("*esci"))
				return null;
			else if (centri_trovati.isEmpty())
				System.out.println(Utili.NEW_LINE + "Non ho trovato centri con questo nome");
			else {
				System.out.println(Utili.NEW_LINE + "- Centri Trovati -");
				int scelta = Cittadini.selezionaCentro(centri_trovati,
														"Seleziona uno dei centri sopra elencati: ",
														"Esegui nuovamente la ricerca");
				
				if (scelta != 0) {
					nome_centro = centri_trovati.get(--scelta).split(";")[0];
					break;
				}
			}
		}
		
		return nome_centro;
	}
	
	/**
	 * Genera un ID di vaccinazione di 16 caratteri alfanumerici casuale e unico,
	 * verificandone la presenza nella HashMap <code>cittadini_vaccinati</code>.
	 * 
	 * @see {@link Cittadini#cittadini_vaccinati}
	 * 
	 * @return <strong>codice</strong>
	 * 		Una stringa contenente l'ID generato
	 */
	private static String generaNuovoId() {
		StringBuilder nuovo_codice = new StringBuilder();
		String codice;
		Random random = new Random();
		
		do {
			for (int i = 0; i < 16; i++) {
				if (random.nextBoolean())
					nuovo_codice.append((char) (random.nextInt(10)+48));  // I numeri da 0 a 9 in ASCII risiedono nelle posizioni da 48 a 57
				else
					nuovo_codice.append((char) (random.nextInt(26)+65));  // Le lettere maiuscole in ASCII risiedono nelle posizioni da 65 a 90
			}
			codice = nuovo_codice.toString();
			nuovo_codice.delete(0, nuovo_codice.length());
		} while (Cittadini.cittadini_vaccinati.containsKey(codice));  // se l'ID è già presente nella HashMap, generane un altro
		
		return codice;
	}
	
	
	/**
	 * Genera una stringa contenente i dati relativi agli attributi dell'oggetto di tipo <code>CentriVaccinali</code>.
	 * 
	 * @return <strong>String</strong>
	 * 		una stringa contenente i dati contenuti nell'oggetto di tipo <code>CentriVaccinali</code>
	 */
	@Override
	public String toString() {
		return String.format("Nome Centro: %s\nIndirizzo: %s\nTipologia: %s", nome_centro, indirizzo.toString(), tipologia);
	}

	
	/**
	 * Il main della classe {@link CentriVaccinali}.
	 * <p>
	 * Utilizzato per interagire con l'utente stampando messaggi sul terminale e richiedendo risposte in input.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// chiede all'utente di inserire ogni campo dell'Indirizzo
		
		String choice;
		boolean exit = false;
		
		if (Cittadini.cittadini_vaccinati.isEmpty()) {
			System.out.println("Caricamento Cittadini Vaccinati...");
			Cittadini.caricaCittadiniVaccinati();
			System.out.println("Caricamento Completato." + Utili.NEW_LINE);
		}
		
		do {
			System.out.println("- Menu Centri Vaccinali -");
			System.out.println("Quale operazione vuoi eseguire?" + Utili.NEW_LINE);
			System.out.println("1) Registra nuovo centro");
			System.out.println("2) Registra nuovo vaccinato");
			System.out.println("0) Menu Principale");
			
			choice = Utili.leggiString(Utili.NEW_LINE + "> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					CentriVaccinali nuovo = registraCentroVaccinale();
					System.out.println(nuovo == null ? Utili.NEW_LINE + "Operazione annullata: Un centro con quel nome esiste già." + Utili.NEW_LINE : nuovo.toString() + Utili.NEW_LINE);
					break;
				case "2":
					registraVaccinato();
					System.out.println();
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
		
	}

}
