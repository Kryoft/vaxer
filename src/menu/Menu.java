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
			System.out.println("A quale sezione vuoi accedere?");
			System.out.println(Utili.new_line + "1) Centri Vaccinali");
			System.out.println("2) Cittadini");
			System.out.println("0) Esci");
			
			choice = Utili.leggiString(Utili.new_line + "> ").strip();
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					centrivaccinali.CentriVaccinali.main(null);
					break;
				case "2":
					cittadini.Cittadini.main(null);
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
	}

}
