package org.android.brasil.projetos.model;

public enum TipoStatus {

	EMPRESTADO(0),
	DEVOLVIDO(1);

	private int id = 0;

	private TipoStatus(int i_id) {
		this.id = i_id;
	}

	public int getId() {
		return id;
	}
}
