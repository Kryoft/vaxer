package menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cittadini.Cittadini;

public class Menu {

	public static void main(String[] args) {
		//Menu principale usato per accedere ai main di Cittadini e CentriVaccinali;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("A quale sezione vuoi accedere?");
		System.out.println("\n1) Centri Vaccinali");
		System.out.println("2) Cittadini");
		System.out.println("\n> ");
	}

}
