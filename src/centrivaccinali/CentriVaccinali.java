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

import menu.Utili;

public class CentriVaccinali {
	
	String nome_centro;
	Indirizzo indirizzo;
	String tipologia;
	
	/*
	 * Ho tolto questo costruttore perché potremmo semplicemente modificare i campi
	 * una volta creato l'oggetto, come facciamo con le altri classi (Cittadini ed Indirizzo)
	 */
//	public CentriVaccinali(String nome_centro, Indirizzo indirizzo, byte tipologia) {
//		this.nome_centro = nome_centro;
//		this.indirizzo = indirizzo;
//		this.tipologia = tipologia;
//	}
	
	public static CentriVaccinali registraCentroVaccinale() throws IOException {
		// crea il file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale viene sostituito dinamicamente
		CentriVaccinali centro = new CentriVaccinali();
		System.out.println("Inserire le informazioni richieste:");
		centro.nome_centro = Utili.leggiString("- Nome del centro > ");
		
		// Restituisce null se un file con quel nome esiste già
		String path = String.format("data/Vaccinati_%s.dati", centro.nome_centro);
		if (Files.exists(Paths.get(path)))
			return null;
		
		System.out.println("- Indirizzo:");
		centro.indirizzo = new Indirizzo();
		centro.indirizzo.qualificatore = Utili.leggiString("    1. Qualificatore (via/v.le/pzza/strada/...) > ");
		centro.indirizzo.nome = Utili.leggiString("    2. Nome (Giuseppe Garibaldi, Roma, ...) > ");
		centro.indirizzo.numero_civico = Utili.leggiString("    3. Numero Civico > ");
		centro.indirizzo.comune = Utili.leggiString("    4. Comune > ");
		centro.indirizzo.sigla_provincia = Utili.leggiString("    5. Sigla della Provincia > ");
		centro.indirizzo.cap = Utili.leggiString("    6. CAP > ");
		do {
			centro.tipologia = Utili.leggiString("- Tipologia:\n\n1) Ospedaliero\n2) Aziendale\n3) Hub\n\n> ");
			switch (centro.tipologia) {
			case "1":
				centro.tipologia = "Ospedaliero";
				break;
			case "2":
				centro.tipologia = "Aziendale";
				break;
			case "3":
				centro.tipologia = "Hub";
				break;
			default:
				System.out.println("Scelta non valida, riprova");
				centro.tipologia = "";
				break;
			}
		} while (centro.tipologia == "");
		
		Utili.scriviSuFile("data/CentriVaccinali.dati", true,
							String.format("\"%s\";\"%s\";\"%s\"\n",
									centro.nome_centro,
									centro.indirizzo.toString(),
									centro.tipologia));
		
		Utili.creaFile(path);
		Utili.scriviSuFile(path, true, "NOME_CENTRO;NOME_CITTADINO;COGNOME_CITTADINO;CODICE_FISCALE;DATA_SOMMINISTRAZIONE_VACCINO;VACCINO_SOMMINISTRATO;ID_VACCINAZIONE;EVENTO_AVVERSO;SEVERITA;NOTE_OPZIONALI\n");
		
		return centro;
	}
	
	//Modificato il valore restituito da void a boolean per restituire false se il centro vaccinale in cui si vogliono inserire i dati non esiste;
	public boolean registraVaccinato(String nome_centro, String nome_cittadino, String cognome_cittadino, String codice_fiscale, SimpleDateFormat data_vaccinazione, String nome_vaccino, char[] ultimo_id_vaccinazione) throws IOException{
		// Restituisce false se il file (centro vaccinale) in cui si vogliono inserire i dati non esiste;
		String path = String.format("data/Vaccinati_%s.dati", nome_centro);
		if(!Files.exists(Paths.get(path))) {
			return false;
		}
		
		//Genera una stringa con il nuovo id_vaccinazione partendo dall'ultimo ID che deve essere fornito come argomento;
		String id_vaccinazione = new String(this.generaId(ultimo_id_vaccinazione));	//TODO capire come viene recuperato l'ultimo id di vaccinazione (file?);
		
		//Scrive sul file i nuovi dati
		Utili.scriviSuFile(path, true,
				String.format("%s;%s;%s;%s;%s;%s;%s",
						id_vaccinazione,
						nome_centro,
						nome_cittadino,
						cognome_cittadino,
						codice_fiscale,
						data_vaccinazione.toString(),
						nome_vaccino));
		
		//TODO algoritmo di ordinamento dei dati su file Vaccinati_NomeCentroVaccinale.dati
		
		return true;
	}
	
	public static void caricaDati() {
		// inserisce in una struttura dati un oggetto di tipo CentriVaccinali per ogni centro contenuto nel file CentriVaccinali.dati
	}
	
	private char[] generaId(char[] ultimo_codice) {
		// il codice deve essere formato da 16 char tutti maiuscoli
		
		ultimo_codice[15]++;
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
			System.out.println("\n1) Registra nuovo centro");
			System.out.println("2) Registra nuovo vaccinato");
			System.out.println("0) Menu Principale");
			
			choice = Utili.leggiString("\n> ");
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					CentriVaccinali nuovo = registraCentroVaccinale();
					System.out.println(nuovo == null ? "Operazione annullata: Un file con quel nome esiste già." : nuovo.toString());
					break;
				case "2":
					System.out.println("Registrazione vaccinato...");
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
		
	}

}
