package org.android.brasil.projetos.dao;

import org.android.brasil.projetos.dao.util.TableBuilder;
import org.android.brasil.projetos.model.Categoria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CategoriaDAO extends BasicoDAO {

	public CategoriaDAO(Context ctx) {
		super(ctx);
	}

	public static final String COLUNA_ID = "_id";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String TABELA_CATEGORIA = "categoria";

	private static String defineTable() {
		TableBuilder tb = new TableBuilder(TABELA_CATEGORIA);
		try {
			tb.setPrimaryKey(COLUNA_ID, "INTEGER",true);
			tb.addColuna(COLUNA_DESCRICAO, "TEXT", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tb.toString();
	}

	private static final String CRIAR_TABELA_CATEGORIA = defineTable();

	public static final String TODOS = "Todos";
	public static final String OUTRA = "Outra";

	private static final String categoriaDefault = "insert into "
			+ TABELA_CATEGORIA + " (descricao) values ('" + OUTRA + "')";
	public static final String categoriaDefaultTodos = "insert into "
			+ TABELA_CATEGORIA + " (descricao) values ('" + TODOS + "')";
	public static final String categoriaDefaultCD = "insert into "
			+ TABELA_CATEGORIA + " (descricao) values ('CD')";
	public static final String categoriaDefaultDVD = "insert into "
			+ TABELA_CATEGORIA + " (descricao) values ('DVD')";
	public static final String categoriaDefaultLivro = "insert into "
			+ TABELA_CATEGORIA + " (descricao) values ('Livro')";

	public static String createTableCategoria() {
		return CRIAR_TABELA_CATEGORIA;
	}

	public static Categoria deCursorParaCategoria(Cursor c) {
		Categoria cat = new Categoria();
		cat.setId(c.getLong(c.getColumnIndex(COLUNA_ID)));
		cat.setNomeCategoria(c.getString(c.getColumnIndex(COLUNA_DESCRICAO)));
		return cat;
	}

	public static String insertCategoriaDefault() {
		return categoriaDefault;
	}

	public static long inserir(Categoria cat) {
		ContentValues values = deCategoriaParaContentValues(cat);
		return inserir(TABELA_CATEGORIA, values);
	}

	public static long atualizar(Categoria cat) {
		ContentValues values = deCategoriaParaContentValues(cat);
		return atualizar(TABELA_CATEGORIA, values, COLUNA_ID,
				String.valueOf(cat.getId()));
	}

	public static boolean remover(long idCategoria) {
		return remover(TABELA_CATEGORIA, COLUNA_ID, idCategoria);
	}

	private static ContentValues deCategoriaParaContentValues(Categoria cat) {
		ContentValues values = new ContentValues();
		values.put(COLUNA_DESCRICAO, cat.getNomeCategoria());
		return values;
	}

	public static Cursor consultarTodos(String[] colunas) {
		return consultarTodos(TABELA_CATEGORIA, colunas);
	}

	public static Cursor consultarTodos() {
		return consultarTodos(TABELA_CATEGORIA);
	}

	public static Cursor consultarTodasCategorias() {
		return consultarTodos(TABELA_CATEGORIA);
	}

	public static Cursor consultarCategoria(long idCategoria) {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_ID,
				String.valueOf(idCategoria));
		return mCursor;

	}

	public static Categoria consultar(long idCategoria) {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_ID,
				String.valueOf(idCategoria));
		Categoria cat = deCursorParaCategoria(mCursor);
		mCursor.close();
		return cat;

	}

	public static boolean deleteCategoria(long id) {

		return remover(id);
	}

	public static boolean isDescricaoCategoriaJaExiste(String descricao) {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_DESCRICAO, descricao);
		
		boolean jaExiste = false;
		
		if (mCursor.getCount() > 0) {
			jaExiste =true;
		}
		
		return jaExiste;
	}

}
