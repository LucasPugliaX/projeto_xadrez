package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private Tabuleiro tabuleiro;

	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		initialSetup();
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

	public PecaXadrez performeXadrezMove(XadrezPosicao origemPosicao, XadrezPosicao destinoPosicao) {
		Posicao origem = origemPosicao.paraPosicao();
		Posicao destino = destinoPosicao.paraPosicao();
		validarOrigemPosicao(origem);
		Peca pecaCapturada = fazerMovimento(origem, destino);
		return (PecaXadrez) pecaCapturada;
	}

	private Peca fazerMovimento(Posicao origem, Posicao destino) {
		Peca p = tabuleiro.removerPeca(origem);
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.lugarPeca(p, destino);
		return pecaCapturada;
	}

	private void validarOrigemPosicao(Posicao posicao) {
		if (!tabuleiro.temPeca(posicao)) {
			throw new XadrezExcecao("Não existe peça na posição de origem.");
		}
	}

	private void placeNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.lugarPeca(peca, new XadrezPosicao(coluna, linha).paraPosicao());
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
