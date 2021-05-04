/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package centrivaccinali;

import java.text.SimpleDateFormat;

public class CentriVaccinali {
	
	String nome_centro;
	Indirizzo indirizzo;
	byte tipologia;
	
	public CentriVaccinali(String nome_centro, Indirizzo indirizzo, byte tipologia) {
		this.nome_centro = nome_centro;
		this.indirizzo = indirizzo;
		this.tipologia = tipologia;
	}
	
	public void registraCentroVaccinale() {
		// crea il file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale viene sostituito dinamicamente
	}
	
	public void registraVaccinato(String nome_centro, String nome_cittadino, String cognome_cittadino, String codice_fiscale, SimpleDateFormat data_vaccinazione, String nome_vaccino, int id_vaccinazione) {
		/*
		 * Questa funzione verrà utilizzata dall'operatore sanitario, poco dopo aver vaccinato il cittadino. Per questo motivo,
		 * l'id_vaccinato dovrà essere generato in quel momento.
		 * Memorizza i dati del cittadino nel file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale
		 * viene sostituito dinamicamente eseguire algoritmo di ordinamento alla chiamata
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

	public static void main(String[] args) {
		// chiede all'utente di inserire ogni campo dell'Indirizzo
	}

}
