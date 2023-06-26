package xadrez;

import java.util.ArrayList;
import java.util.List;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogador;
	private Tabuleiro tabuleiro;
	
	List<Peca> pecasNoTabuleiro = new ArrayList<>();
	List<Peca> pecasCapturadas = new ArrayList<>();

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogador = Cor.BRANCO;
		initialSetup();
	}

	public int getTurno() {
		return turno;
	}

	public Cor getjogador() {
		return jogador;
	}

	public PecaXadrez[][] getPecas() {
		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for (int i = 0; i < tabuleiro.getLinhas(); i++) {
			for (int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}

	public boolean[][] movimentosPossiveis(XadrezPosicao origemPosicao) {
		Posicao posicao = origemPosicao.paraPosicao();
		validarOrigemPosicao(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}

	public PecaXadrez performeXadrezMove(XadrezPosicao origemPosicao, XadrezPosicao destinoPosicao) {
		Posicao origem = origemPosicao.paraPosicao();
		Posicao destino = destinoPosicao.paraPosicao();
		validarOrigemPosicao(origem);
		validarDestinoPosicao(origem, destino);
		Peca pecaCapturada = fazerMovimento(origem, destino);
		proxTurno();
		return (PecaXadrez) pecaCapturada;
	}

	private Peca fazerMovimento(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removerPeca(origem);
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.lugarPeca(p, destino);
		
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		return pecaCapturada;
	}

	private void validarOrigemPosicao(Posicao posicao) {
		if (!tabuleiro.temPeca(posicao)) {
			throw new XadrezExcecao("Não existe peça na posição de origem.");
		}
		if (jogador != ((PecaXadrez) tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezExcecao("A peça escolhida não é sua.");
		}
		if (!tabuleiro.peca(posicao).temAlgumMovimentoPossivel()) {
			throw new XadrezExcecao("Não existem movimentos possíveis para a peca escolhida.");
		}
	}

	private void validarDestinoPosicao(Posicao origem, Posicao destino) {
		if (!tabuleiro.peca(origem).movimentoPossivel(destino)) {
			throw new XadrezExcecao("A peça escolhida não pode se mover para a posição de destino.");
		}
	}

	private void proxTurno() {
		turno++;
		jogador = (jogador == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private void placeNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.lugarPeca(peca, new XadrezPosicao(coluna, linha).paraPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void initialSetup() {
		placeNovaPeca('c', 1, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('c', 2, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('d', 2, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('e', 2, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('e', 1, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('d', 1, new Rei(tabuleiro, Cor.BRANCO));

		placeNovaPeca('c', 7, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('c', 8, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('d', 7, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('e', 7, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('e', 8, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('d', 8, new Rei(tabuleiro, Cor.PRETO));
	}

}
