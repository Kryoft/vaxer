/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package centrivaccinali;

import java.text.SimpleDateFormat;

public class CentriVaccinali {
	
	String nome;
	Indirizzo indirizzo;
	byte tipologia;
	
	public CentriVaccinali() {
		
	}
	
	public void registraCentroVaccinale(String nome, String indirizzo, byte tipologia) {
		// crea il file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale viene sostituito dinamicamente
	}
	
	public void registraVaccinato(String nome_centro, String nome_cittadino, String cognome_cittadino, String codice_fiscale, SimpleDateFormat data_vaccinazione, String nome_vaccino, int id_vaccinazione) {
		/* Questa funzione verrà utilizzata dall'operatore sanitario, poco dopo aver vaccinato il cittadino. Per questo motivo,
		 * l'id_vaccinato dovrà essere generato in quel momento.
		 * Memorizza i dati del cittadino nel file Vaccinati_NomeCentroVaccinale.dati dove NomeCentroVaccinale
		 * viene sostituito dinamicamente eseguire algoritmo di ordinamento alla chiamata
		 */
	}
	
	public static void caricaDati() {
		// inserisce in una struttura dati un oggetto di tipo CentriVaccinali per ogni centro contenuto nel file CentriVaccinali.dati
	}

	public static void main(String[] args) {
		
	}

}
