/*
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package centrivaccinali;

/**
 * Questa classe instanzia dei record che saranno utilizzati per la memorizzazione temporanea di dati che
 * saranno scritti sui file di testo.
 * 
 * @see centrivaccinali.CentriVaccinali
 * 
 * @author Cristian Corti
 * @author Manuel Marceca
 */
public class Indirizzo {
	
	/**
	 *  Il qualificatore dell'Indirizzo (es. via/v.le/pzza/strada/...)
	 */
	String qualificatore;
	
	/**
	 * Il nome della via/piazza/...
	 */
	String nome;
	
	/**
	 * Il numero civico 
	 */
	String numero_civico;
	
	/**
	 * Il comune
	 */
	String comune;
	
	/**
	 * La sigla della provincia
	 */
	String sigla_provincia;
	
	/**
	 * Il codice postale
	 */
	String cap; // se mettessi int gli 0 all'inizio verrebbero cancellati
	
	
	/**
	 * Costruisce la stringa contenente i dati dell'indirizzo.
	 * 
	 * @return <strong>String</strong>
	 * 		La stringa contenente secondo uno specifico ordine i contenuti del record
	 */
	@Override
	public String toString() {
		return String.format("%s %s, %s %s (%s) %s", qualificatore, nome, numero_civico, comune, sigla_provincia, cap);
	}
	
}
