/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package centrivaccinali;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import menu.Menu;

public class CentriVaccinali {
	
	String nome_centro;
	Indirizzo indirizzo;
	int tipologia;
	
//	public CentriVaccinali(String nome_centro, Indirizzo indirizzo, byte tipologia) {
//		this.nome_centro = nome_centro;
//		this.indirizzo = indirizzo;
//		this.tipologia = tipologia;
//	}
	
	public static CentriVaccinali registraCentroVaccinale() throws IOException {
		// crea il file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale viene sostituito dinamicamente
		
		CentriVaccinali centro = new CentriVaccinali();
		System.out.println("Inserire le informazioni richieste:");
		centro.nome_centro = Menu.leggiString("- Nome del centro > ");
		
		System.out.println("- Indirizzo:");
		centro.indirizzo = new Indirizzo();
		centro.indirizzo.qualificatore = Menu.leggiString("    1. Qualificatore (via/v.le/pzza/strada/...) > ");
		centro.indirizzo.nome = Menu.leggiString("    2. Nome (Giuseppe Garibaldi, Roma, ...) > ");
		centro.indirizzo.numero_civico = Menu.leggiString("    3. Numero Civico > ");
		centro.indirizzo.comune = Menu.leggiString("    4. Comune > ");
		centro.indirizzo.sigla_provincia = Menu.leggiString("    5. Sigla della Provincia > ");
		centro.indirizzo.cap = Menu.leggiString("    6. CAP > ");
		
		centro.tipologia = Menu.leggiInt("- Tipologia:\n\n1) Ospedaliero\n2) Aziendale\n3) Hub\n\n> ");
		
		return centro;
	}
	
	public void registraVaccinato(String nome_centro, String nome_cittadino, String cognome_cittadino, String codice_fiscale, SimpleDateFormat data_vaccinazione, String nome_vaccino, int id_vaccinazione) {
		/*
		 * Questa funzione verrà utilizzata dall'operatore sanitario, poco dopo aver vaccinato il cittadino. Per questo motivo,
		 * l'id_vaccinato dovrà essere generato in quel momento.
		 * Memorizza i dati del cittadino nel file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale
		 * viene sostituito dinamicamente (eseguire algoritmo di ordinamento alla chiamata)
		 */
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
			}
		}
		
		return ultimo_codice;
	}
	
	@Override
	public String toString() {
		String str_tipologia = "";
		switch (tipologia) {
		case 1:
			str_tipologia = "Ospedaliero";
			break;
		case 2:
			str_tipologia = "Aziendale";
			break;
		case 3:
			str_tipologia = "Hub";
			break;
		}
		return String.format("Nome Centro: %s\nIndirizzo: %s\nTipologia: %s", nome_centro, indirizzo.toString(), str_tipologia);
	}

	public static void main(String[] args) throws IOException {
		// chiede all'utente di inserire ogni campo dell'Indirizzo
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Quale operazione vuoi eseguire?");
		System.out.println("\n1) Registra nuovo centro");
		System.out.println("2) Registra nuovo vaccinato");
		System.out.println("0) Menu Principale");
		System.out.print("\n> ");
		
		String choice = "";
		do {
			choice = in.readLine();
			switch (choice) {
				case "0":
					Menu.main(null); // lick my memory
					break;
				case "1":
					CentriVaccinali nuovo = registraCentroVaccinale();
					System.out.println(nuovo.toString());
					break;
				case "2":
					System.out.println("Registrazione vaccinato...");
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					choice = "";
					break;
			}
		} while (choice == "");
		
		in.close();
		
	}

}
