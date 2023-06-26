package main;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.XadrezExcecao;
import xadrez.XadrezPosicao;

public class Main {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		List<PecaXadrez> capturada = new ArrayList<>();

		while (true) {
			try {
				UI.limparTela();
				UI.printPartida(partidaXadrez, capturada);
				System.out.println();
				System.out.print("Origem: ");
				XadrezPosicao origem = UI.lerXadrezPosicao(sc);

				boolean[][] movimentosPossiveis = partidaXadrez.movimentosPossiveis(origem);
				UI.limparTela();
				UI.printBoard(partidaXadrez.getPecas(), movimentosPossiveis);
				System.out.println();
				System.out.print("Destino: ");
				XadrezPosicao desino = UI.lerXadrezPosicao(sc);

				PecaXadrez pecaCapturada = partidaXadrez.performeXadrezMove(origem, desino);
				
				if(pecaCapturada != null) {
					capturada.add(pecaCapturada);
				}
			} 
			catch (XadrezExcecao e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} 
			catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
	}
}