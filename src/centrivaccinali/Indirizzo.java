package centrivaccinali;

public class Indirizzo {
	
	String qualificatore;
	String nome;
	String numero_civico;
	String comune;
	String sigla_provincia;
	String cap; // se mettessi int gli 0 all'inizio verrebbero cancellati
	
	@Override
	public String toString() {
		return String.format("%s %s, %s %s (%s) %s", qualificatore, nome, numero_civico, comune, sigla_provincia, cap);
	}
	
}
