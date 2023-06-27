package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {

	private int turno;
	private Cor jogador;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	private PecaXadrez enPassantV;
	private PecaXadrez promovido;

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

	public PecaXadrez getEnPassantV() {
		return enPassantV;
	}
	
	public PecaXadrez getPromovido() {
		return promovido;
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

		PecaXadrez pecaMovida = (PecaXadrez)tabuleiro.peca(destino);
		
		// jogada especial promocão
		promovido = null;
		if(pecaMovida instanceof Peao) {
			if(pecaMovida.getCor() == Cor.BRANCO && destino.getLinha() == 0 || (pecaMovida.getCor() == Cor.PRETO && destino.getLinha() == 7)) {
				promovido = (PecaXadrez)tabuleiro.peca(destino);
				promovido = realocarPecaPromovida("Q");
			}
		}

		xeque = (testeXeque(oponente(jogador))) ? true : false;

		if (testeXequeMate(oponente(jogador))) {
			xequeMate = true;
		} else {
			proxTurno();
		}

		// jogada especial en passant
		if (pecaMovida instanceof Peao && (destino.getLinha() == origem.getLinha() - 2 || destino.getLinha() == origem.getLinha() + 2)) {
			enPassantV = pecaMovida;
		} 
		else {
			enPassantV = null;
		}

		return (PecaXadrez) pecaCapturada;
	}
	
	public PecaXadrez realocarPecaPromovida(String tipo) {
		if(promovido == null) {
			throw new IllegalStateException("Não há peça para realizar a troca.");
		}
		if(!tipo.equals("B") && !tipo.equals("C") && !tipo.equals("T") && !tipo.equals("Q")) {
			throw new InvalidParameterException("Peça inválida para promoção");
		}
		
		Posicao pos = promovido.getXadrezPosicao().paraPosicao();
		Peca p = tabuleiro.removerPeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaXadrez novaPeca = novaPeca(tipo, promovido.getCor());
		tabuleiro.lugarPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}

	private PecaXadrez novaPeca(String tipo, Cor cor) {
		if(tipo.equals("B")) return new Bispo(tabuleiro, cor);
		if(tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if(tipo.equals("T")) return new Torre(tabuleiro, cor);
		return new Rainha(tabuleiro, cor);
	}
	
	private Peca fazerMovimento(Posicao origem, Posicao destino) {
		PecaXadrez p = (PecaXadrez) tabuleiro.removerPeca(origem);
		p.acrescentaContMovimento();
		Peca pecaCapturada = tabuleiro.removerPeca(destino);
		tabuleiro.lugarPeca(p, destino);

		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}

		// jogada especial Roque pequeno
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.lugarPeca(torre, destinoT);
			torre.acrescentaContMovimento();
		}

		// jogada especial Roque grande
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(origemT);
			tabuleiro.lugarPeca(torre, destinoT);
			torre.acrescentaContMovimento();
		}

		// jogada especial en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posPeao;
				if (p.getCor() == Cor.BRANCO) {
					posPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} 
				else {
					posPeao = new Posicao(destino.getLinha() - 1, destino.getColuna());
				}
				pecaCapturada = tabuleiro.removerPeca(posPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}

		return pecaCapturada;
	}

	private void desfazerMovimento(Posicao origem, Posicao destino, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez) tabuleiro.removerPeca(destino);
		p.descontaContMovimento();
		tabuleiro.lugarPeca(p, origem);

		if (pecaCapturada != null) {
			tabuleiro.lugarPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}

		// jogada especial Roque pequeno
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() + 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() + 3);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(destinoT);
			tabuleiro.lugarPeca(torre, origemT);
			torre.descontaContMovimento();
		}

		// jogada especial Roque grande
		if (p instanceof Rei && destino.getColuna() == origem.getColuna() - 2) {
			Posicao origemT = new Posicao(origem.getLinha(), origem.getColuna() - 4);
			Posicao destinoT = new Posicao(origem.getLinha(), origem.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez) tabuleiro.removerPeca(destinoT);
			tabuleiro.lugarPeca(torre, origemT);
			torre.descontaContMovimento();
		}
		
		// jogada especial en passant
		if (p instanceof Peao) {
			if (origem.getColuna() != destino.getColuna() && pecaCapturada == enPassantV) {
				PecaXadrez peao = (PecaXadrez)tabuleiro.removerPeca(destino);
				Posicao posPeao;
				if (p.getCor() == Cor.BRANCO) {
					posPeao = new Posicao(3, destino.getColuna());
				} else {
					posPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.lugarPeca(peao, posPeao);
			}
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
		placeNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));
		placeNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		placeNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));

		placeNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		placeNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		placeNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		placeNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		placeNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		placeNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));
		placeNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		placeNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));

	}

}
