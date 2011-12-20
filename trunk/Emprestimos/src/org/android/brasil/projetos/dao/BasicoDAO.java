package org.android.brasil.projetos.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class BasicoDAO {
	protected static SQLiteDatabase mDb;
	protected static Context mCtx;
	private static String TAG = "DB";
	private static int contador;

	private synchronized static int numeroConexoes(int i) {
		contador = contador + i;
		return contador;
	}

	/**
	 * Utiliza ctx para instânciar uma base de dados.
	 * 
	 * @param ctx
	 *            Necessário ser chamado antes de chamar qualquer método
	 *            estático!
	 */
	public synchronized static void open(Context ctx) {
		if (mDb == null || (mDb != null && !mDb.isOpen())) {

			mDb = DbAdapter.open(ctx);
		}
		mCtx = ctx;

		if (numeroConexoes(0) >= 0) {
			numeroConexoes(+1);
		}
	}

	/**
	 * Fecha o acesso a uma base de dados. Deve ser chamado sempre que tiver
	 * utilizado algum método estático!
	 */
	public synchronized static void close() {
		if (mDb != null && mDb.isOpen() && (numeroConexoes(0) == 1)) {
			DbAdapter.close();
			mDb.close();
		}
		if (numeroConexoes(0) > 0) {
			numeroConexoes(-1);
		}

	}

	/**
	 * Construtor básico
	 * 
	 * @param ctx
	 *            é uma instância de Context.
	 */
	public BasicoDAO(Context ctx) {
		mCtx = ctx;
	}

	/**
	 * @return
	 */
	public static String createTableString() {
		return null;
	}

	/**
	 * @return
	 */
	public static String upgradeTableString() {
		return null;
	}

	/**
	 * @param db
	 */
	public static void createDummyData(SQLiteDatabase db) {

	}

	/**
	 * Monta uma String com os parâmetros passados.
	 * 
	 * @param keys
	 *            Vetor com as colunas a serem utilizadas no "AND"
	 * @param values
	 *            Vetor com os valores a serem utilizadas no "AND"
	 * @return String contendo a condição.
	 */
	private static String condicaoANDBuilder(String[] keys, String[] values) {
		String condicao = new String();
		int parada = values.length < keys.length ? values.length : keys.length;

		for (int i = 0; i < parada; i++) {
			condicao += "UPPER(" + keys[i] + ") = UPPER('" + values[i]
					+ "') AND ";
		}
		if (condicao.length() > 5) {
			condicao = condicao.substring(0, condicao.length() - 5);
		}
		return condicao;
	}

	/**
	 * Monta uma String com os parâmetros passados.
	 * 
	 * @param keys
	 *            Vetor com as colunas a serem utilizadas no "OR"
	 * @param values
	 *            Vetor com os valores a serem utilizadas no "OR"
	 * @return String contendo a condição.
	 */
	private static String condicaoORBuilder(String[] keys, String[] values) {
		String condicao = new String();
		int parada = values.length < keys.length ? values.length : keys.length;

		for (int i = 0; i < parada; i++) {
			condicao += "UPPER(" + keys[i] + ") = UPPER('" + values[i]
					+ "') OR ";
		}

		if (condicao.length() > 4) {
			condicao = condicao.substring(0, condicao.length() - 4);
		}
		return condicao;
	}

	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave
	 * fornecida.
	 * 
	 * @param table
	 *            Tabela onde será executada a busca.
	 * @param colunas
	 *            Colunas que devem ser consideradas no retorno da busca.
	 * @param condicao
	 *            Condição que será utilizada para executar a query.
	 * @return Cursor na primeira posição, caso algum dado tenha sido
	 *         encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	private static Cursor consultaBasica(String table, String[] colunas,
			String condicao) {

		return consultaBasica(table, colunas, condicao, null);
	}

	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave
	 * fornecida.
	 * 
	 * @param table
	 *            Tabela onde será executada a busca.
	 * @param colunas
	 *            Colunas que devem ser consideradas no retorno da busca.
	 * @param condicao
	 *            Condição que será utilizada para executar a query.
	 * @return Cursor na primeira posição, caso algum dado tenha sido
	 *         encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	private static Cursor consultaBasica(String table, String[] colunas,
			String condicao, String orderBy) {
		Cursor mCursor = mDb.query(false, table, colunas, condicao, null, null,
				null, orderBy, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave
	 * fornecida. Exemplo: UPPER(keys[0]) = UPPER('values[0]') AND
	 * UPPER(keys[1]) = UPPER('values[1]')...
	 * 
	 * @param table
	 *            Tabela onde será executada a busca.
	 * @param colunas
	 *            Colunas que devem ser consideradas no retorno da busca.
	 * @param keys
	 *            Colunas que deverão conter a palavra-chave definida como
	 *            <b>value</b>.
	 * @param values
	 *            Palavra-chave da busca.
	 * @return Cursor na primeira posição, caso algum dado tenha sido
	 *         encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	protected static Cursor consultarAND(String table, String[] colunas,
			String[] keys, String[] values) {

		Cursor mCursor = consultaBasica(table, colunas,
				condicaoANDBuilder(keys, values));
		return mCursor;

	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida. É utilizado ignore case na comparação do OR. Exemplo:
	 * UPPER(keys[0]) = UPPER('values[0]') OR UPPER(keys[1]) =
	 * UPPER('values[1]')...
	 * 
	 * @param table
	 *            Tabela de onde serão consultados os registros.
	 * @param colunas
	 *            Colunas a serem exibidas.
	 * @param keys
	 *            Vetor com as colunas a serem utilizadas no "OR"
	 * @param values
	 *            Vetor com os valores a serem utilizadas no "OR"
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	protected static Cursor consultarOR(String table, String[] colunas,
			String[] keys, String[] values) {
		return consultaBasica(table, colunas, condicaoORBuilder(keys, values));

	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida. É utilizado ignore case na comparação do AND. Exemplo:
	 * likeKey[0] LIKE '%likeValue[0]%' AND UPPER(andKey[0]) =
	 * UPPER('andValue[0]')...
	 * 
	 * @param table
	 *            Tabela de onde serão consultados os registros.
	 * @param likeKeys
	 *            Vetor com as colunas a serem utilizadas no "LIKE"
	 * @param likeValues
	 *            Vetor com os valores a serem utilizados no "LIKE"
	 * @param colunas
	 *            Colunas a serem exibidas.
	 * @param andKeys
	 *            Vetor com as colunas a serem utilizadas no "AND"
	 * @param andValues
	 *            Vetor com os valores a serem utilizadas no "AND"
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	protected static Cursor consultarLikeAnd(String table, String[] colunas,
			String[] likeKeys, String[] likeValues, String[] andKeys,
			String[] andValues) {
		String condicao = "";

		int paradaLike = likeKeys.length < likeValues.length ? likeKeys.length
				: likeValues.length;
		if (likeKeys != null && likeValues != null) {
			for (int i = 0; i < paradaLike; i++) {
				condicao += likeKeys[i] + " LIKE '%" + likeValues[i]
						+ "%' AND ";
			}
			condicao = condicao.substring(0, condicao.length() - 5);

		}
		if ((andKeys != null) && (andValues != null) && andKeys.length > 0) {
			condicao = condicao + " AND "
					+ condicaoANDBuilder(andKeys, andValues);
		}

		return consultaBasica(table, colunas, condicao);

	}

	/**
	 * Método genérico para atualizar entradas nas tabelas baseado na igualdade
	 * entre as colunas (key) passadas e seus valores (values) fornecidos.
	 * Exemplo: UPPER(keys[0]) = UPPER('values[0]') AND UPPER(keys[1]) =
	 * UPPER('values[1]')...
	 * 
	 * @param table
	 *            Tabela onde será executada a deleção.
	 * @param cvalues
	 *            ContentValues contendo o conteúdo de uma linha a ser
	 *            atualizada.
	 * @return retorna o número de linhas atualizadas.
	 */
	protected static long atualizar(String table, ContentValues cvalues,
			String[] keys, String[] values) {
		long id = -1;
		mDb.beginTransaction();
		try {
			id = mDb.update(table, cvalues, condicaoANDBuilder(keys, values),
					null);
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			mDb.endTransaction();
		}

		return id;
	}

	protected static long atualizar(String table, ContentValues cvalues,
			String key, String value) {
		return atualizar(table, cvalues, new String[] { key },
				new String[] { value });
	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida.
	 * 
	 * @param tabela
	 *            Tabela de onde serão consultados os registros.
	 * @param colunas
	 *            Colunas a serem exibidas.
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	protected static Cursor consultarTodos(String tabela, String[] colunas) {
		return consultaBasica(tabela, colunas, null);
	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida.
	 * 
	 * @param tabela
	 *            Tabela de onde serão consultados os registros.
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	public static Cursor consultarTodos(String tabela) {
		return consultarTodos(tabela, null);
	}

	/**
	 * Método genérico para remover entradas nas tabelas baseadas no id
	 * fornecido. Exemplo: coluna = id
	 * 
	 * @param table
	 *            Tabela onde será executada a deleção.
	 * @param id
	 *            Chave da linha a ser deletada.
	 * @return <b>True</b> se a operação foi bem-sucedida; <b>false</b> em caso
	 *         de erro.
	 */
	protected static boolean remover(String table, String coluna, long id) {
		if (coluna != null) {
			coluna = coluna + " = " + id;
		}
		return mDb.delete(table, coluna, null) > 0;
	}

	/**
	 * Método genérico para remover todas as entradas da tabela fornecida.
	 * 
	 * @param table
	 *            Tabela onde será executada a deleção.
	 * @return <b>True</b> se a operação foi bem-sucedida; <b>false</b> em caso
	 *         de erro.
	 */
	protected static boolean removerTodos(String table) {
		return remover(table, null, 0);
	}

	/**
	 * Método genérico para inserir entradas na tabela fornecida.
	 * 
	 * @param table
	 *            Tabela onde será executada a deleção.
	 * @param values
	 *            ContentValues contendo o conteúdo de uma linha a ser inserida
	 * @return o id se a operação foi bem-sucedida; -1 em caso de erro.
	 */
	protected static long inserir(String table, ContentValues values) {
		long id = -1;
		if (!mDb.inTransaction()) {
			mDb.beginTransaction();
		}

		try {
			id = mDb.insert(table, null, values);
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (mDb.inTransaction()) {
				mDb.endTransaction();
			}
		}
		return id;
	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida. É utilizado ignore case na comparação do AND.
	 * 
	 * @param table
	 *            Tabela de onde serão consultados os registros.
	 * @param keys
	 *            Vetor com as colunas a serem utilizadas no "LIKE"
	 * @param values
	 *            Vetor com os valores a serem utilizados no "LIKE"
	 * @param colunas
	 *            Colunas a serem exibidas.
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	protected static Cursor consultarLike(String table, String[] colunas,
			String[] keys, String[] values) {

		return consultarLikeAnd(table, keys, values, colunas, null, null);

	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida. É utilizado ignore case na comparação do AND.
	 * 
	 * @param table
	 *            Tabela de onde serão consultados os registros.
	 * @param key
	 *            Coluna a ser utilizada no "LIKE"
	 * @param value
	 *            Valor a ser utilizado no "LIKE"
	 * @param colunas
	 *            Colunas a serem exibidas.
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	protected static Cursor consultarLike(String table, String[] colunas,
			String key, String value) {
		return consultarLike(table, colunas, new String[] { key },
				new String[] { value });
	}

	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave
	 * fornecida.
	 * 
	 * @param table
	 *            Tabela onde será executada a busca.
	 * @param colunas
	 *            Colunas que devem ser consideradas no retorno da busca.
	 * @param key
	 *            Coluna que deverá conter a palavra-chave definida como
	 *            <b>value</b>.
	 * @param value
	 *            Palavra-chave da busca.
	 * @return Cursor na primeira posição, caso algum dado tenha sido
	 *         encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	protected static Cursor consultar(String table, String[] colunas,
			String key, String value) {
		return consultarAND(table, colunas, new String[] { key },
				new String[] { value });

	}

	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave
	 * fornecida. Retorna todas as colunas da tabela.
	 * 
	 * @param table
	 *            Tabela onde será executada a busca.
	 * 
	 * @param key
	 *            Coluna que deverá conter a palavra-chave definida como
	 *            <b>value</b>.
	 * @param value
	 *            Palavra-chave da busca.
	 * @return Cursor na primeira posição, caso algum dado tenha sido
	 *         encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	protected static Cursor consultar(String table, String key, String value) {
		return consultarAND(table, null, new String[] { key },
				new String[] { value });

	}

	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave
	 * fornecida. Retorna todas as colunas da tabela.
	 * 
	 * @param table
	 *            Tabela onde será executada a busca.
	 * @param colunas
	 *            Colunas que devem ser consideradas no retorno da busca.
	 * 
	 * @param keys
	 *            Colunas que deverão conter as palavras-chave definidas como
	 *            <b>values</b>.
	 * @param values
	 *            Palavras-chave da busca.
	 * @param orderBy
	 *            Coluna que deve ser utilizada para ordenação dos itens
	 *            retornados
	 * 
	 * @return Cursor na primeira posição, caso algum dado tenha sido
	 *         encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	protected static Cursor consultar(String table, String[] colunas,
			String[] keys, String[] values, String orderBy) {

		return consultaBasica(table, colunas, condicaoANDBuilder(keys, values),
				orderBy);

	}

	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela
	 * definida. É utilizado ignore case na comparação do AND. Exemplo:
	 * likeKey[0] LIKE '%likeValue[0]%' AND UPPER(andKey[0]) =
	 * UPPER('andValue[0]')...
	 * 
	 * @param table
	 *            Tabela de onde serão consultados os registros.
	 * @param likeKey
	 *            Coluna a ser utilizada no "LIKE"
	 * @param likeValue
	 *            Valor a ser utilizado no "LIKE"
	 * @param colunas
	 *            Colunas a serem exibidas.
	 * @param andKeys
	 *            Vetor com as colunas a serem utilizadas no "AND"
	 * @param andValues
	 *            Vetor com os valores a serem utilizadas no "AND"
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	protected static Cursor consultarLikeAnd(String table, String[] colunas,
			String likeKey, String likeValue, String[] andKeys,
			String[] andValues) {
		return consultarLikeAnd(table, colunas, new String[] { likeKey },
				new String[] { likeValue }, andKeys, andValues);
	}
}
