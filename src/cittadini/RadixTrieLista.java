package cittadini;

import java.util.LinkedList;

public class RadixTrieLista {

	private class Nodo {
		String elemento;
		LinkedList<Nodo> figli;
		boolean is_parola;

		public Nodo(String elemento, boolean is_parola) {
			this.elemento = elemento;
			figli = new LinkedList<>();
			this.is_parola = is_parola;
		}

		public boolean nonHaFigli() {
			return figli.isEmpty();
		}
	}
	private Nodo radice;
	private int offset_elemento_nodo;

	public RadixTrieLista() {
		radice = new Nodo("", false);
		radice.figli = new LinkedList<>();
	}

	public boolean isEmpty() {
		return radice.nonHaFigli();
	}

	public boolean inserisci(String parola) {
		return inserisci(parola, radice);
	}

	public boolean inserisci(String parola, Nodo corrente) {
		if (parola.equals(""))
			return false;

		if (corrente.nonHaFigli())
			corrente.figli.add(new Nodo(parola, true));
		else {
			Nodo successivo = corrente;
			for (Nodo nodo : corrente.figli)
				// There could only be one child which starts with a certain letter:
				// The root can't have "ciao" and "cane" as children, but it will have "c" as children;
				// Then, "c" will have "iao" and "ane" as children.
				if (nodo.elemento.startsWith(parola.substring(0, 1))) {
					successivo = nodo;
					break;
				}
			if (successivo == corrente) {
				corrente.figli.add(new Nodo(parola, true));
			} else {
				boolean va_splittato = false;
				boolean entrato_nel_loop = false;
				boolean is_una_parola = false;
				int indice_substring = 0;

				if (parola.length() > successivo.elemento.length()) {
					for (; indice_substring < successivo.elemento.length(); indice_substring++) {
						if (successivo.elemento.charAt(indice_substring) != parola.charAt(indice_substring)) {
							va_splittato = true;
							break;
						}
					}
				} else if (parola.length() < successivo.elemento.length()) {
					for (; indice_substring < parola.length(); indice_substring++) {
						if (successivo.elemento.charAt(indice_substring) != parola.charAt(indice_substring)) {
							va_splittato = true;
							entrato_nel_loop = true;
							break;
						}
					}
					if (!entrato_nel_loop) {
						va_splittato = true;
						is_una_parola = true;
					}
				} else {
					for (; indice_substring < parola.length(); indice_substring++) {
						if (successivo.elemento.charAt(indice_substring) != parola.charAt(indice_substring)) {
							entrato_nel_loop = true;
							va_splittato = true;
							break;
						}
					}
					
					// I have to enter inside here only if the current words are equal
					if (!entrato_nel_loop) {
						// if this is true it means that the word I tried to insert
						// was already there (because the node already had is_word = true)
						if (successivo.is_parola)
							return false;
						else {
							successivo.is_parola = true;
							return true;
						}
					}
				}

				if (va_splittato) {
					String split_sinistra = successivo.elemento.substring(0, indice_substring);
					String split_destra = successivo.elemento.substring(indice_substring);
					successivo.elemento = split_sinistra;
					Nodo nodo_split_destra = new Nodo(split_destra, successivo.is_parola);
					successivo.is_parola = (is_una_parola) ? true : false;
					nodo_split_destra.figli = successivo.figli;
					successivo.figli = new LinkedList<>();
					successivo.figli.add(nodo_split_destra);
					if (!is_una_parola)
						successivo.figli.add(new Nodo(parola.substring(indice_substring), true));
				} else {
					return inserisci(parola.substring(indice_substring), successivo);
				}
			}
		}

		return true;
	}

	public LinkedList<String> visita() {
		LinkedList<String> parole = new LinkedList<>();

		visita(radice, parole, "");

		return parole;
	}

	public LinkedList<String> visita(String parola) {
		if (parola.equals(""))
			return visita();
		
		LinkedList<String> parole = new LinkedList<>();
		Nodo nodo_iniziale = ottieniNodo(parola);
		
		if (nodo_iniziale != null) {
			String inizio_parola = parola.substring(0, parola.length()-offset_elemento_nodo);
			visita(nodo_iniziale, parole, inizio_parola);
		}

		return parole;
	}

	private void visita(Nodo corrente, LinkedList<String> parole, String parola) {
		parola += corrente.elemento;
		if (corrente.is_parola)
			parole.add(parola);
		for (Nodo nodo : corrente.figli) {
			visita(nodo, parole, parola);
		}
	}

	private Nodo ottieniNodo(String parola) {
		Nodo corrente = radice;
		Nodo successivo = radice;
		boolean parole_sono_uguali = true;

		while (parola.length() != 0) {
			offset_elemento_nodo = 0;
			if (corrente.nonHaFigli())
				return null;
			else {
				successivo = corrente;
				for (Nodo nodo : corrente.figli)
					if (nodo.elemento.charAt(0) == parola.charAt(0)) {
						successivo = nodo;
						break;
					}
				if (successivo == corrente) {
					return null;
				} else {
					boolean elemento_succ_piu_piccolo = true;
					parole_sono_uguali = true;

					if (parola.length() > successivo.elemento.length()) {
						for (int i = 0; i < successivo.elemento.length(); i++) {
							if (successivo.elemento.charAt(i) == parola.charAt(0)) {
								parola = parola.substring(1);
								offset_elemento_nodo++;
							} else return null;
						}
					} else { // if (text.length() <= next.element.length())
						elemento_succ_piu_piccolo = false;
						// I save the text's length because I shrink it inside the for loop
						int length_parola = parola.length();
						for (int i = 0; i < length_parola; i++) {
							if (successivo.elemento.charAt(i) == parola.charAt(0)) {
								parola = parola.substring(1);
								offset_elemento_nodo++;
							} else parole_sono_uguali = false;
						}
						if (!parole_sono_uguali) {
							return null;
						}
					}
					if (elemento_succ_piu_piccolo)
						corrente = successivo;
				}
			}
		}
		if (parole_sono_uguali)
			return successivo;
		return null;
	}

	@Override
	public String toString() {
		LinkedList<String> parole = visita();
		String risultato = "";

		while (!parole.isEmpty()) {
			risultato += parole.pop() + "\n";
		}

		return risultato;
	}

}
