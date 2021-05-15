/*
 * Davide Spinelli, 744151, CO
 * Cristian Corti, 744359, CO
 * Manuel Marceca, 746494, CO
 */

package menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import cittadini.Cittadini;
import centrivaccinali.CentriVaccinali;

public class Menu {
	
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public static String leggiString(String message) throws IOException {
		System.out.print(message);
		return in.readLine();
	}
	
	public static int leggiInt(String message) throws IOException {
		System.out.print(message);
		return Integer.parseInt(in.readLine());
	}

	public static void main(String[] args) throws IOException {
		
		//Menu principale usato per accedere ai main di Cittadini e CentriVaccinali;
		System.out.println("A quale sezione vuoi accedere?");
		System.out.println("\n1) Centri Vaccinali");
		System.out.println("2) Cittadini");
		System.out.println("0) Esci");
		System.out.print("\n> ");
		
		String choice = "";
		do {
			choice = in.readLine();
			switch (choice) {
				case "0":
					break;
				case "1":
					CentriVaccinali.main(null);
					break;
				case "2":
					Cittadini.main(null);
					break;
				default:
					System.out.println("Scelta non valida, riprova.");
					choice = "";
					break;
			}
		} while (choice == "");

		in.close();
		
	}

}
