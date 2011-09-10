package org.android.brasil.projetos.model;

public class Categoria {
	private long id;
	private String nome;

	public long getIdCategoria() {
		return id;
	}

	public void setIdCategoria(long id) {
		this.id = id;
	}

	public void setNomeCategoria(String cat) {
		nome = cat;
	}

	public String getNomeDescricao() {
		return nome;
	}
}
