/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package menu;

import java.io.IOException;

public class Menu {

	public static void main(String[] args) throws IOException {
		
		String choice;
		boolean exit = false;
		
		do {
			//Menu principale usato per accedere ai main di Cittadini e CentriVaccinali;
			System.out.println("- Menu Principale -");
			System.out.println("A quale sezione vuoi accedere?" + Utili.NEW_LINE);
			System.out.println("1) Centri Vaccinali");
			System.out.println("2) Cittadini");
			System.out.println("0) Esci");
			
			choice = Utili.leggiString(Utili.NEW_LINE + "> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					System.out.println();
					centrivaccinali.CentriVaccinali.main(null);
					break;
				case "2":
					System.out.println();
					cittadini.Cittadini.main(null);
					break;
				default:
					System.out.println(Utili.NEW_LINE + "Scelta non valida, riprova." + Utili.NEW_LINE);
					break;
			}
		} while (!exit);
	}

}
