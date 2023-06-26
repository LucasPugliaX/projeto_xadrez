package xadrez;

import tabuleiro.Peca;
import tabuleiro.Posicao;
import tabuleiro.Tabuleiro;

public abstract class PecaXadrez extends Peca {

	private Cor cor;
	private int contMovimento;

	public PecaXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContMovimento() {
		return contMovimento;
	}
	
	public void acrescentaContMovimento() {
		contMovimento++;
	}
	
	public void descontaContMovimento() {
		contMovimento--;
	}
	
	public XadrezPosicao getXadrezPosicao() {
		return XadrezPosicao.dePosicao(posicao);
	}

	protected boolean temPecaOponente(Posicao posicao) {
		PecaXadrez p = (PecaXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}
}
