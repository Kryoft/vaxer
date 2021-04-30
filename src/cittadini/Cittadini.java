/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package cittadini;

import centrivaccinali.CentriVaccinali;

public class Cittadini {
	
	String nome;
	String cognome;
	String codice_fiscale;
	String indirizzo_email;
	String user_id;
	String password;
	String id_vaccinazione; // da capire in che cazzo di formato viene fornito
	
	public void cercaCentroVaccinale(String nome_centro) {
		// scegliere algoritmo di ricerca (pattern matching, bruteforce, boyer-moore)
		// scegliere struttura dati da restituire
	}
	
	public void cercaCentroVaccinale(String comune, byte tipologia) {
		// scegliere algoritmo di ricerca (pattern matching, bruteforce, boyer-moore)
		// scegliere struttura dati da restituire
	}
	
	public void visualizzaInfoCentroVaccinale(CentriVaccinali nome_centro) {
		/*
		 * Restituisce tutte le informazione del Centro selezionato (accedendo ai campi di 'nome_centro',
		 * ad es. nome_centro.nome, nome_centro.indirizzo) e, una volta ottenute, usa il nome per trovare
		 * il file Vaccinati_%nome%.dati, da cui calcolare il numero di segnalazioni e severità media per ogni evento avverso
		 * Es. mal di testa: severità media = 3.5, numero di segnalazioni = 10
		 */
	}
	
	public static void main(String[] args) {
		
	}

}
