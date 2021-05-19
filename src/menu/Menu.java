/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package menu;

import java.io.IOException;

import cittadini.Cittadini;
import centrivaccinali.CentriVaccinali;

public class Menu {

	public static void main(String[] args) throws IOException {
		
		String choice;
		boolean exit = false;
		
		do {
			//Menu principale usato per accedere ai main di Cittadini e CentriVaccinali;
			System.out.println("A quale sezione vuoi accedere?");
			System.out.println("\n1) Centri Vaccinali");
			System.out.println("2) Cittadini");
			System.out.println("0) Esci");
			
			choice = Utili.leggiString("\n> ");
			switch (choice) {
				case "0":
					exit = true;
					break;
				case "1":
					CentriVaccinali.main(null);
					break;
				case "2":
					Cittadini.main(null);
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					break;
			}
		} while (!exit);
	}

}
