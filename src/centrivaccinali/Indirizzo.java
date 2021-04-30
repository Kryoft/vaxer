package centrivaccinali;

public class Indirizzo {
	
	String qualificatore;
	String nome;
	String numero_civico;
	String comune;
	String sigla_provincia;
	String cap; // se mettessi int gli 0 all'inizio verrebbero cancellati
	
	public Indirizzo(String ...dati) {
		this.qualificatore = dati[0];
		this.nome = dati[1];
		this.numero_civico = dati[2];
		this.comune = dati[3];
		this.sigla_provincia = dati[4];
		this.cap = dati[5];
	}

}
