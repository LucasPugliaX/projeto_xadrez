package main;

import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.XadrezPosicao;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadrez partidaXadrez = new PartidaXadrez();

		while (true) {
			UI.printBoard(partidaXadrez.getPecas());
			System.out.println("");
			System.out.print("Origem: ");
			XadrezPosicao origem = UI.lerXadrezPosicao(sc);

			System.out.println("");
			System.out.print("Destino: ");
			XadrezPosicao desino = UI.lerXadrezPosicao(sc);

			PecaXadrez pecaCapturada = partidaXadrez.performeXadrezMove(origem, desino);
		}
	}

}
