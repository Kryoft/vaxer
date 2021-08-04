/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package centrivaccinali;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cittadini.Cittadini;
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
		System.out.println("- Indirizzo:");
		
		// Inizializza un oggetto "Indirizzo";
		centro.indirizzo = new Indirizzo();
		centro.indirizzo.qualificatore = Utili.leggiString("    1. Qualificatore (via/v.le/pzza/strada/...) > ").strip().replace(";", "");
		centro.indirizzo.nome = Utili.leggiString("    2. Nome (Giuseppe Garibaldi, Roma, ...) > ").strip().replace(";", "");
		centro.indirizzo.numero_civico = Utili.leggiString("    3. Numero Civico > ").strip().replace(";", "");
		centro.indirizzo.comune = Utili.leggiString("    4. Comune > ").strip().replace(";", "");
		centro.indirizzo.sigla_provincia = Utili.leggiString("    5. Sigla della Provincia > ").strip().replace(";", "");
		centro.indirizzo.cap = Utili.leggiString("    6. CAP > ").strip().replace(";", "");
		centro.tipologia = Utili.inserisciTipologiaCentro(String.format("- Tipologia:%s1) Ospedaliero%s2) Aziendale%s3) Hub%s%s> ",
																			Utili.new_line,
																			Utili.new_line,
																			Utili.new_line,
																			Utili.new_line,
																			Utili.new_line));
		System.out.println();
		
		// Scrive sul file CentriVaccinali.dati il nuovo centro vaccinale. Crea il file se non esiste già;
		Utili.scriviSuFile("data/CentriVaccinali.dati", true,
							String.format("\"%s\";\"%s\";\"%s\"",
									centro.nome_centro,
									centro.indirizzo.toString(),
									centro.tipologia));
		
		// Restituisce il nuovo centro vaccinale;
		return centro;
	}
	
	//Modificato il valore restituito da void a boolean per restituire false se il centro vaccinale in cui si vogliono inserire i dati non esiste;
	public static boolean registraVaccinato() throws IOException {
		/* 
		 * Campi per il file Cittadini_Vaccinati:
		 * NOME_CITTADINO;COGNOME_CITTADINO;CODICE_FISCALE;DATA_SOMMINISTRAZIONE_VACCINO;VACCINO_SOMMINISTRATO;ID_VACCINAZIONE;EVENTI_AVVERSI_E_SEVERITA;NOTE_OPZIONALI
		 * Esempio di severità e note opzionali:
		 * ;"4, 0, 0, 0, 0, 0";"mi fa male la testa, *, *, *, *, *"
		 */
		
		String nome_centro;
		ArrayList<String> centri_trovati;
		
		System.out.println("Inserisci le informazioni richieste:");
		while (true) {
			nome_centro = Utili.leggiString("    1. Centro in cui il Cittadino ha eseguito la Vaccinazione (\"*esci\" per annullare) > ");
			centri_trovati = Cittadini.cercaCentroVaccinale(nome_centro);
			
			if (nome_centro.equals("*esci"))
				return false;
			else if (centri_trovati.isEmpty())
				System.out.println(Utili.new_line + "Non ho trovato centri con questo nome");
			else
				break;
		}
		
		System.out.println(Utili.new_line + "- Centri Trovati -");
		int scelta = Cittadini.selezionaCentro(centri_trovati, "Seleziona uno dei centri sopra elencati: ");
		
		if (scelta == 0)
			return false;
		
		nome_centro = centri_trovati.get(--scelta).split(";")[0].replace("\"", "");
		
		String nome_cittadino = Utili.leggiString("    2. Nome del Cittadino > ").strip().replace(";", "");
		String cognome_cittadino = Utili.leggiString("    2. Cognome del Cittadino > ").strip().replace(";", "");
		String codice_fiscale = Utili.leggiString("    2. Codice Fiscale > ").strip().replace(";", "");
		SimpleDateFormat data_vaccinazione;
		String nome_vaccino = Utili.leggiString("    2. Nome del Vaccino > ").strip().replace(";", "");
		char[] ultimo_id_vaccinazione;
		
		// Restituisce false se il file (centro vaccinale) in cui si vogliono inserire i dati non esiste;
//		String path = String.format("data/Vaccinati_%s.dati", nome_centro);
//		if (!Files.exists(Paths.get(path))) {
//			return false;
//		}
//		
//		//TODO recupero ID di vaccinazione e creazione primo ID se nel file Vaccinati_NomeCentroVaccinale non è presente nessun vaccinato;
//		//Genera una stringa con il nuovo id_vaccinazione partendo dall'ultimo ID che deve essere fornito come argomento;
//		String id_vaccinazione = new String(this.generaId(ultimo_id_vaccinazione));
//		
//		//TODO modificare l'inserimento per aggiungere il nuovo vaccinato in testa al file;
//		//Scrive sul file i nuovi dati;
//		Utili.scriviSuFile(path, true,
//				String.format("%s;%s;%s;%s;%s;%s;%s",
//						id_vaccinazione,
//						nome_centro,
//						nome_cittadino,
//						cognome_cittadino,
//						codice_fiscale,
//						data_vaccinazione.toString(),
//						nome_vaccino));
		
		return true;
	}
	
	private char[] generaId(char[] ultimo_codice) {
		
		//ultimo_codice è lungo 5 se identifica il centro vaccinale o 11 se identifica il cittadino;
		ultimo_codice[ultimo_codice.length-1]++;
		for (int i = ultimo_codice.length-1; i >= 0; i--) {
			if (ultimo_codice[i] == 'Z'+1) {
				ultimo_codice[i] = 'A';
				ultimo_codice[i-1]++;
			} else break; // else break aggiunto da me (Cristian) per non fare cicli inutili
		}
		
		return ultimo_codice;
	}
	
	@Override
	public String toString() {
		return String.format("Nome Centro: %s\nIndirizzo: %s\nTipologia: %s", nome_centro, indirizzo.toString(), tipologia);
	}

	public static void main(String[] args) throws IOException {
		// chiede all'utente di inserire ogni campo dell'Indirizzo
		
		String choice;
		boolean exit = false;
		
		do {
			System.out.println("Quale operazione vuoi eseguire?");
			System.out.println(Utili.new_line + "1) Registra nuovo centro");
			System.out.println("2) Registra nuovo vaccinato");
			System.out.println("0) Menu Principale");
			
			choice = Utili.leggiString(Utili.new_line + "> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					CentriVaccinali nuovo = registraCentroVaccinale();
					System.out.println(nuovo == null ? "Operazione annullata: Un file con quel nome esiste già." : nuovo.toString() + Utili.new_line);
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
