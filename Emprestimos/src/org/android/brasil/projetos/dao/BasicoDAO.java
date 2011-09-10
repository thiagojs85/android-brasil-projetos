package org.android.brasil.projetos.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class BasicoDAO {
	protected static SQLiteDatabase mDb;
	protected static Context mCtx;

	/**
	 * Construtor básico
	 * @param ctx é uma instância de Context.
	 */
	public BasicoDAO(Context ctx) {
		mCtx = ctx;
	}

	/**
	 * Utiliza ctx para instânciar uma base de dados.
	 * @param ctx
	 * Necessário ser chamado antes de chamar qualquer método estático!
	 */
	public static void  open(Context ctx) {
		if (mDb == null || (mDb != null && !mDb.isOpen())) {
			mDb = EmprestimoDbAdapter.open(ctx);
		}
		mCtx = ctx;
	}

	/**
	 *  Fecha o acesso a uma base de dados.
	 *  Deve ser chamado sempre que tiver utilizado algum método estático!
	 */
	public static void close() {
		if (mDb != null && mDb.isOpen()) {
			EmprestimoDbAdapter.close();
			mDb.close();
		}

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
		return consultar(table, colunas, new String[] { key },
				new String[] { value });

	}
	protected static Cursor consultar(String table,
			String key, String value) {
		return consultar(table, null, new String[] { key },
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
			String[] key, String[] value) {
		String condicao = new String();
		int parada = key.length < value.length ? key.length : value.length;

		for (int i = 0; i < parada; i++) {
			condicao += "UPPER("+key[i] + ") = UPPER('" + value[i] + "') AND ";
		}
		
		// O loop-for acima deixará um " AND " sobrando, então deve-se
		// remove-lo.
		condicao = condicao.substring(0, condicao.length() - 5);

		Cursor mCursor = mDb.query(false, table, colunas, condicao, null, null,
				null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;

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
		Cursor mCursor = mDb.query(tabela, colunas, null, null, null, null,
				null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
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
		Cursor mCursor = mDb.query(tabela, null, null, null, null, null,
				null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/**
	 * Retorna uma lista com todos os valores encontrados para a coluna da
	 * tabela definida.
	 * 
	 * @param tabela
	 *            Tabela de onde serão consultados os registros.
	 * @param coluna
	 *            Coluna a ser exibida.
	 * @return Lista com os valores encontrados. Retorna uma lista vazia se não
	 *         encontrar nenhum valor válido.
	 */
	protected static List<String> consultarTodos(String tabela, String coluna) {
		List<String> resultado = new ArrayList<String>();
		Cursor cursor = consultarTodos(tabela, new String[] { coluna });

		while (!cursor.isAfterLast()) {
			resultado.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();

		return resultado;
	}

	/**
	 * Método genérico para remover entradas nas tabelas baseadas no id
	 * fornecido.
	 * 
	 * @param table
	 *            Tabela onde será executada a deleção.
	 * @param id
	 *            Chave da linha a ser deletada
	 * @return <b>True</b> se a operação foi bem-sucedida; <b>false</b> em caso
	 *         de erro.
	 */
	protected static boolean remover(String table, String coluna, long id) {
		return mDb.delete(table, coluna + " = " + id, null) > 0;
	}
	
	protected static boolean removerTodos(String table) {
		return mDb.delete(table, null, null) > 0;
	}

	protected static long inserir(String table, ContentValues values){
		return mDb.insert(table, null, values);
	}
	protected static long atualizar(String table, ContentValues cvalues, String[] key, String[] value){
		String condicao = new String();
		int parada = key.length < value.length ? key.length : value.length;

		for (int i = 0; i < parada; i++) {
			condicao += "UPPER("+key[i] + ") = UPPER('" + value[i] + "') AND ";
		}
		condicao = condicao.substring(0, condicao.length() - 5);
		
		return mDb.update(table, cvalues, condicao, null);
	}
	
}
