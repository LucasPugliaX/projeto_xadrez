package tabuleiro;

public class Tabuleiro {

	private int linhas;
	private int colunas;
	private Peca[][] pecas;

	public Tabuleiro(int linhas, int colunas) {
		this.linhas = linhas;
		this.colunas = colunas;
		pecas = new Peca[linhas][colunas];
		if (linhas < 1 || colunas < 1) {
			throw new TabuleiroExecao("Erro criando o tabueiro: é necessário qu haja ao menos uma linha e uma coluna");
		}
	}

	public int getLinhas() {
		return linhas;
	}

	public int getColunas() {
		return colunas;
	}

	public Peca peca(int linha, int coluna) {

		if (!posicaoExiste(linha, coluna)) {
			throw new TabuleiroExecao("Não há esta posição no tabuleiro.");
		}
		return pecas[linha][coluna];
	}

	public Peca peca(Posicao posicao) {
		if (!posicaoExiste(posicao)) {
			throw new TabuleiroExecao("Não há esta posição no tabuleiro.");
		}
		return pecas[posicao.getLinha()][posicao.getColuna()];
	}

	public void lugarPeca(Peca peca, Posicao posicao) {
		if (temPeca(posicao)) {
			throw new TabuleiroExecao("Já existe uma peça nesta posição." + posicao);
		}
		pecas[posicao.getLinha()][posicao.getColuna()] = peca;
		peca.posicao = posicao;
	}

	public Peca removerPeca(Posicao posicao) {
		if (!posicaoExiste(posicao)) {
			throw new TabuleiroExecao("Não há esta posição no tabuleiro.");
		}
		if (peca(posicao) == null) {
			return null;
		}
		Peca aux = peca(posicao);
		aux.posicao = null;
		pecas[posicao.getLinha()][posicao.getColuna()] = null;
		return aux;
	}

	public boolean posicaoExiste(int linha, int coluna) {
		return linha >= 0 && linha < linhas && coluna >= 0 && coluna < colunas;
	}

	public boolean posicaoExiste(Posicao posicao) {
		return posicaoExiste(posicao.getLinha(), posicao.getColuna());
	}

	public boolean temPeca(Posicao posicao) {
		if (!posicaoExiste(posicao)) {
			throw new TabuleiroExecao("Não há esta posição no tabuleiro.");
		}
		return peca(posicao) != null;
	}
}
