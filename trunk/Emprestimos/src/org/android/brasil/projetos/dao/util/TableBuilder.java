package org.android.brasil.projetos.dao.util;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

	private static final String[] ACTIONS = { "NO ACTION", "RESTRICT",
			"SET NULL", "SET DEFAULT", "CASCADE" };

	private static final String VIRGULA = " , ";

	public static final int NO_ACTION = 0;
	public static final int RESTRICT = 1;
	public static final int SET_NULL = 2;
	public static final int SET_DEFAULT = 3;
	public static final int CASCADE = 4;

	private String tabela;
	private List<String> pks;
	private List<String> colunas;
	private List<String> fks;


	public TableBuilder(String tabela) {
		this.tabela = tabela;
		colunas = new ArrayList<String>();
		pks = new ArrayList<String>();
		fks = new ArrayList<String>();
	}

	public void setPrimaryKey(String coluna, String tipo) {
		setPrimaryKey(new String[] { coluna }, new String[] { tipo });
	}

	public void setPrimaryKey(String[] colunas, String[] tipos) {
		int parada = colunas.length < tipos.length ? colunas.length
				: tipos.length;
		for (int i = 0; i < parada; ++i) {

			this.addColuna(colunas[i], tipos[i], false);

			pks.add(colunas[i]);
		}
	}

	public void addColuna(String nome, String tipo, boolean notNull) {
		colunas.add(nome + " " + tipo + (notNull ? " NOT NULL , " : VIRGULA));
	}

	public void addFK(String nome, String tipo, String tabelaRef,
			String colunaRef, int actionDelete, int actionUpdate) {

		this.addColuna(nome, tipo, true);

		fks.add("FOREIGN KEY ( " + nome + " )  REFERENCES " + tabelaRef + " ("
				+ colunaRef + " ) " + "ON DELETE " + ACTIONS[actionDelete]
				+ " ON UPDATE " + ACTIONS[actionUpdate] + VIRGULA);

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(tabela);
		sb.append(" ( ");

		for (int i = 0; i < colunas.size(); ++i) {
			sb.append(colunas.get(i));
		}

		if (pks != null && pks.size() > 0) {
			sb.append(" PRIMARY KEY ( ");

			for (int i = 0; i < pks.size(); ++i) {
				if (i != pks.size() -1) {
					sb.append(pks.get(i) + VIRGULA);
				} else {
					sb.append(pks.get(i));
				}
			}

			sb.append(" ) ");
		}

		for (int i = 0; i < fks.size(); ++i) {
			sb.append(fks.get(i));
		}

		String teste = sb.toString();

		if (teste.endsWith(VIRGULA)) {
			return teste.substring(0, teste.length() - VIRGULA.length())
					+ " ); ";

		} else {

			sb.append(" ); ");
			return sb.toString();
		}

	}
}
