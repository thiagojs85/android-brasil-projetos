package org.android.brasil.projetos.model;

public enum TipoCategoria {

	OUTRA(1),
	TODOS(2);

	private int id = 0;

	private TipoCategoria(int i_id) {
		this.id = i_id;
	}

	public int getId() {
		return id;
	}
}
