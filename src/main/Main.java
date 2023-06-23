package main;

import xadrez.PartidaXadrez;

public class Main {

	public static void main(String[] args) {

		PartidaXadrez partidaXadrez = new PartidaXadrez();

		UI.printBoard(partidaXadrez.getPecas());

	}

}
