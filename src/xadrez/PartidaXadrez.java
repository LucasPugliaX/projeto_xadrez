package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogador;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;

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

	public boolean getXeque() {
		return xeque;
	}

	public boolean getXequeMate() {
		return xequeMate;
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

		if (testeXeque(jogador)) {
			desfazerMovimento(origem, destino, pecaCapturada);
			throw new XadrezExcecao("Você não pode se colocar em xeque");
		}

		xeque = (testeXeque(oponente(jogador))) ? true : false;

		if (testeXequeMate(oponente(jogador))) {
			xequeMate = true;
		} else {
			proxTurno();
		}

		return (PecaXadrez) pecaCapturada;
	}

	private Peca fazerMovimento(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removerPeca(origem);
		p.acrescentaContMovimento();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.lugarPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}

		return pecaCapturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removerPeca(destino);
		p.descontaContMovimento();
		tabuleiro.lugarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.lugarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
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

	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}

	private PecaXadrez rei(Cor cor) {
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : lista) {
			if (p instanceof Rei) {
				return (PecaXadrez) p;
			}
		}
		throw new IllegalStateException("Não existe o rei da cor " + cor + "no tabuleiro");
	}

	private boolean testeXeque(Cor cor) {
		Posicao posicaoRei = rei(cor).getXadrezPosicao().paraPosicao();
		List<Peca> pecaOponente = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == oponente(cor))
				.collect(Collectors.toList());
		for (Peca p : pecaOponente) {
			boolean[][] mat = p.movimentosPossiveis();
			if (mat[posicaoRei.getLinha()][posicaoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testeXequeMate(Cor cor) {
		if (!testeXeque(cor)) {
			return false;
		}
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez) x).getCor() == cor)
				.collect(Collectors.toList());
		for (Peca p : lista) {
			boolean[][] mat = p.movimentosPossiveis();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao origem = ((PecaXadrez) p).getXadrezPosicao().paraPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = fazerMovimento(origem, destino);
						boolean testeXeque = testeXeque(cor);
						desfazerMovimento(origem, destino, p);
						if (!testeXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void placeNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.lugarPeca(peca, new XadrezPosicao(coluna, linha).paraPosicao());
		pecasNoTabuleiro.add(peca);
	}

	private void initialSetup() {
		placeNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		placeNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		placeNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		placeNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		placeNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		placeNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO));
		placeNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO));
		placeNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO));

		placeNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		placeNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		placeNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		placeNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		placeNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO));
		placeNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO));
		placeNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO));
		
	}

}
