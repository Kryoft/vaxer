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

import cittadini.Cittadini;
import menu.MainMenu;
import menu.Utili;

public class CentriVaccinali {
	
	String nome_centro;
	Indirizzo indirizzo;
	String tipologia;
	
	/*
	 * Costruttore rimosso perchè si andranno a modificare i campi una volta creato l'oggetto, come per le altre classi.
	 */
	
	public static CentriVaccinali registraCentroVaccinale() throws IOException {
		// Inizializza un oggetto "CentriVaccinali" e successivamente richiede le informazioni necessarie all'utente;
		CentriVaccinali centro = new CentriVaccinali();
		
		System.out.println("Inserisci le informazioni richieste:");
		centro.nome_centro = Utili.leggiString("- Nome del centro > ").strip().replace(";", "");
		
		// Controlla se il file CentriVaccinali.dati esiste già oppure no.
		// Nel caso non esistesse inserisce i nomi dei campi, ...
		if (!Files.exists(Paths.get(MainMenu.CENTRI_VACCINALI_PATH)))
			Utili.scriviSuFile(MainMenu.CENTRI_VACCINALI_PATH, true, "NOME;INDIRIZZO;TIPOLOGIA" + Utili.NEW_LINE);
		// ...nel caso non esistesse controlla se esiste già un centro con quel nome.
		else {
			ArrayList<String> centri = Cittadini.cercaCentroVaccinale(centro.nome_centro);
			for (String centro_vaccinale : centri)
				if (centro.nome_centro.equals(centro_vaccinale))
					return null;
		}
		
		System.out.println("- Indirizzo:");
		centro.indirizzo = new Indirizzo();
		centro.indirizzo.qualificatore = Utili.leggiString("    1. Qualificatore (via/v.le/pzza/strada/...) > ").strip().replace(";", "");
		centro.indirizzo.nome = Utili.leggiString("    2. Nome (Giuseppe Garibaldi, Roma, ...) > ").strip().replace(";", "");
		centro.indirizzo.numero_civico = Utili.leggiString("    3. Numero Civico > ").strip().replace(";", "");
		centro.indirizzo.comune = Utili.leggiString("    4. Comune > ").strip().replace(";", "");
		centro.indirizzo.sigla_provincia = Utili.leggiString("    5. Sigla della Provincia > ").strip().replace(";", "");
		centro.indirizzo.cap = Utili.leggiString("    6. CAP > ").strip().replace(";", "");
		centro.tipologia = Utili.inserisciTipologiaCentro(String.format("- Tipologia:%s1) Ospedaliero%s2) Aziendale%s3) Hub%s%s> ",
																			Utili.NEW_LINE,
																			Utili.NEW_LINE,
																			Utili.NEW_LINE,
																			Utili.NEW_LINE,
																			Utili.NEW_LINE));
		System.out.println();
		
		// Scrive sul file CentriVaccinali.dati il nuovo centro vaccinale;
		Utili.scriviSuFile(MainMenu.CENTRI_VACCINALI_PATH, true,
							String.format("%s;%s;%s%s",
									centro.nome_centro,
									centro.indirizzo.toString(),
									centro.tipologia,
									Utili.NEW_LINE));
		
		// Restituisce il nuovo centro vaccinale;
		return centro;
	}
	
	//Modificato il valore restituito da void a boolean per restituire false se il centro vaccinale in cui si vogliono inserire i dati non esiste;
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
		String nuovo_id_vaccinazione;
		String ultima_riga = Utili.leggiUltimaRiga(file_path);
		if (ultima_riga == null) {
			nuovo_id_vaccinazione = "AAAAAAAAAAAAAAAA";
		} else {
			nuovo_id_vaccinazione = ultima_riga.substring(0, ultima_riga.indexOf(';'));
			nuovo_id_vaccinazione = generaId(nuovo_id_vaccinazione);
		}
		
		if (!Files.exists(Paths.get(file_path)))
			Utili.scriviSuFile(file_path, true, "ID_VACCINAZIONE;NOME_CITTADINO;COGNOME_CITTADINO;CODICE_FISCALE;NOME_CENTRO_VACCINALE;DATA_SOMMINISTRAZIONE_VACCINO;VACCINO_SOMMINISTRATO;EVENTI_AVVERSI_E_SEVERITA;NOTE_OPZIONALI" + Utili.NEW_LINE);
		
		Utili.scriviSuFile(file_path, true, String.format("%s;%s;%s;%s;%s;%s;%s;;%s",
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
	
	private static String generaId(String ultimo_codice) {
		char[] nuovo_codice = ultimo_codice.toCharArray();
		// Siccome in Java le stringhe sono immutabili, ogni volta che viene eseguita un'operazione di
		// concatenazione o modifica di una stringa ne viene creata un'altra che prende il posto di quella
		// vecchia, che verrà presto eliminata dal Garbage Collector. La classe StringBuilder è stata
		// creata appositamente per costruire una stringa evitando questo
		StringBuilder nuovo_codice_str = new StringBuilder();
		
		nuovo_codice[nuovo_codice.length-1]++;
		for (int i = nuovo_codice.length-1; i >= 0; i--) {
			if (nuovo_codice[i] == 'Z'+1) {
				nuovo_codice[i] = 'A';
				nuovo_codice[i-1]++;
			}
			// Siccome il for va al contrario, appendo alla stringa il carattere in posizione i...
			nuovo_codice_str.append(nuovo_codice[i]);
		}
		
		// ... poi la inverto
		nuovo_codice_str.reverse();
		return nuovo_codice_str.toString();
	}
	
	@Override
	public String toString() {
		return String.format("Nome Centro: %s\nIndirizzo: %s\nTipologia: %s", nome_centro, indirizzo.toString(), tipologia);
	}

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
