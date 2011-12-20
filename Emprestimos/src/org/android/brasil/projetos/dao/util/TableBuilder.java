package org.android.brasil.projetos.dao.util;

import java.util.HashMap;

public class TableBuilder {

	private static boolean PRINT_CREATION = true;

	private static final String[] ACTIONS = { "NO ACTION", "RESTRICT",
			"SET NULL", "SET DEFAULT", "CASCADE" };

	private static final String VIRGULA = ",";

	public static final int NO_ACTION = 0;
	public static final int RESTRICT = 1;
	public static final int SET_NULL = 2;
	public static final int SET_DEFAULT = 3;
	public final int CASCADE = 4;

	public static final String INTEGER = "INTEGER";
	public static final String REAL = "REAL";
	public static final String TEXT = "TEXT";

	private String tabela;
	private HashMap<String, String> pks;
	private HashMap<String, String> colunas;
	private HashMap<String, String> fks;

	public TableBuilder(String tabela) {
		this.tabela = tabela;
		colunas = new HashMap<String, String>();
		pks = new HashMap<String, String>();
		fks = new HashMap<String, String>();
	}

	public void setPrimaryKey(String coluna, String tipo, boolean autoInc)
			throws Exception {
		if (autoInc) {
			coluna = coluna + " AUTOINCREMENT ";
		}
		setPrimaryKey(new String[] { coluna }, new String[] { tipo });
	}

	public void setPrimaryKey(String[] colunas, String[] tipos)
			throws Exception {
		int parada = colunas.length < tipos.length ? colunas.length
				: tipos.length;
		for (int i = 0; i < parada; ++i) {

			if (!pks.containsKey(colunas[i])) {
				pks.put(colunas[i], tipos[i]);
			} else {
				throw new Exception("Colunas repetidas!: " + colunas[i]);
			}

			// UMA PK pode ser FK
			this.addColuna(colunas[i].replace(" AUTOINCREMENT ", ""), tipos[i],
					false, false);

		}
	}

	private void addColuna(String nome, String tipo, boolean notNull,
			boolean checkColunas) throws Exception {
		if (!colunas.containsKey(nome)) {
			colunas.put(nome, nome + " " + tipo
					+ (notNull ? " NOT NULL " + VIRGULA : VIRGULA));
		} else if (checkColunas) {
			throw new Exception("Colunas repetidas!: " + nome);
		}
	}

	public void addColuna(String nome, String tipo, boolean notNull)
			throws Exception {
		this.addColuna(nome, tipo, notNull, true);
	}

	public void addFK(String nome, String tipo, String tabelaRef,
			String colunaRef, int actionDelete, int actionUpdate)
			throws Exception {

		// Uma FK pode ser PK
		this.addColuna(nome, tipo, false, false);

		fks.put(nome, "CONSTRAINT FK_" + nome + " FOREIGN KEY (" + nome
				+ ")  REFERENCES " + tabelaRef + " (" + colunaRef + ") "
				+ "ON DELETE " + ACTIONS[actionDelete] + " ON UPDATE "
				+ ACTIONS[actionUpdate] + VIRGULA);

	}

	public void addFK_PKMultiple(String[] nomes, String[] tipos,
			String tabelaRef, int actionDelete, int actionUpdate)
			throws Exception {
		int parada = nomes.length < tipos.length ? nomes.length : tipos.length;
		String constraintNome = "";
		String nomesColunas = "";

		for (int i = parada - 1; i > -1; --i) {
			if (i == parada - 1) {
				constraintNome = nomes[i] + "__";
				nomesColunas = nomes[i];
			} else {
				constraintNome = nomes[i] + "_" + constraintNome;
				nomesColunas = nomes[i] + VIRGULA + nomesColunas;

			}
			this.addColuna(nomes[i], tipos[i], false, false);
		}
		fks.put(nomesColunas, "CONSTRAINT FK_" + constraintNome
				+ " FOREIGN KEY (" + nomesColunas + ")  REFERENCES "
				+ tabelaRef + " ON DELETE " + ACTIONS[actionDelete]
				+ " ON UPDATE " + ACTIONS[actionUpdate] + VIRGULA);

	}

	public void addFK(String[] nomes, String[] tipos, String tabelaRef,
			String[] colunasRef, int actionDelete, int actionUpdate)
			throws Exception {
		int parada = nomes.length < tipos.length ? nomes.length : tipos.length;
		String constraintNome = "";
		String nomesColunas = "";
		String nomesColunasRef = "";

		for (int i = 0; i < parada; ++i) {
			if (i == 0) {
				constraintNome = nomes[i] + "_";
				nomesColunas = nomes[i];
				nomesColunasRef = colunasRef[i];
			} else {
				constraintNome = nomes[i] + "_" + constraintNome;
				nomesColunas = nomes[i] + VIRGULA + nomesColunas;
				nomesColunasRef = colunasRef[i] + VIRGULA + nomesColunasRef;

			}
			this.addColuna(nomes[i], tipos[i], false, false);
		}
		fks.put(nomesColunas, "CONSTRAINT FK_" + constraintNome
				+ " FOREIGN KEY (" + nomesColunas + ")  REFERENCES "
				+ tabelaRef + " (" + nomesColunasRef + ") " + "ON DELETE "
				+ ACTIONS[actionDelete] + " ON UPDATE " + ACTIONS[actionUpdate]
				+ VIRGULA);

	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(tabela);
		sb.append(" (");

		for (String col : colunas.values()) {
			sb.append(col);
		}

		if (pks != null && pks.size() > 0) {
			sb.append(" PRIMARY KEY (");

			int sizePk = pks.size();
			int i = 0;
			for (String pk : pks.keySet()) {
				if (i < sizePk - 1) {
					sb.append(pk + VIRGULA);
				} else {
					sb.append(pk);
				}
				i = i + 1;
			}

			sb.append(") ");
		}

		for (String fk : fks.values()) {
			sb.append(fk);
		}

		String teste = sb.toString();

		if (teste.endsWith(VIRGULA)) {
			String t = teste.substring(0, teste.length() - VIRGULA.length())
					+ "); ";
			if (PRINT_CREATION) {
				System.out.println(t);
			}
			return t;

		} else {

			sb.append("); ");
			if (PRINT_CREATION) {
				System.out.println(sb.toString());
			}
			return sb.toString();
		}

	}

}
