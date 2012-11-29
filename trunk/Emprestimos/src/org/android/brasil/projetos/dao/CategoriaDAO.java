package org.android.brasil.projetos.dao;

import org.android.brasil.projetos.dao.util.TableBuilder;
import org.android.brasil.projetos.model.Categoria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;

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
			tb.setPrimaryKey(COLUNA_ID, "INTEGER", true);
			tb.addColuna(COLUNA_DESCRICAO, "TEXT", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tb.toString();
	}

	public static final String CRIAR_TABELA_CATEGORIA = defineTable();

	private static final String OUTRA = "Outra";
	private static final String TODOS = "Todas";
	public static final int TODAS_ID = 1;
	public static final int OUTRA_ID = 5;

	public static final String categoriaTodas = "insert into "
			+ TABELA_CATEGORIA + " (_id, descricao) values ("+TODAS_ID+", '" + TODOS + "')";
	public static final String categoriaDefaultCD = "insert into "
			+ TABELA_CATEGORIA + " (_id, descricao) values (2, 'CD')";
	public static final String categoriaDefaultDVD = "insert into "
			+ TABELA_CATEGORIA + " (_id, descricao) values (3, 'DVD')";
	public static final String categoriaDefaultLivro = "insert into "
			+ TABELA_CATEGORIA + " (_id, descricao) values (4, 'Livro')";
	public static final String categoriaDefaultOutra = "insert into "
			+ TABELA_CATEGORIA + " (_id, descricao) values ("+OUTRA_ID+", '" + OUTRA + "')";
	public static Categoria deCursorParaCategoria(Cursor c) {
		Categoria cat = new Categoria();
		cat.setId(c.getLong(c.getColumnIndex(COLUNA_ID)));
		cat.setNomeCategoria(c.getString(c.getColumnIndex(COLUNA_DESCRICAO)));
		return cat;
	}

	public long inserir(Categoria cat) {
		ContentValues values = deCategoriaParaContentValues(cat);
		return inserir(TABELA_CATEGORIA, values);
	}

	public long atualizar(Categoria cat) {
		ContentValues values = deCategoriaParaContentValues(cat);
		return atualizar(TABELA_CATEGORIA, values, COLUNA_ID,
				String.valueOf(cat.getId()));
	}

	public boolean remover(long idCategoria) {
		return remover(TABELA_CATEGORIA, COLUNA_ID, idCategoria);
	}

	private ContentValues deCategoriaParaContentValues(Categoria cat) {
		ContentValues values = new ContentValues();
		values.put(COLUNA_DESCRICAO, cat.getNomeCategoria());
		return values;
	}

	public Cursor consultarTodos(String[] colunas) {
		return consultarTodos(TABELA_CATEGORIA, colunas);
	}

	public Cursor consultarTodos() {
		return consultarTodos(TABELA_CATEGORIA);
	}

	public Cursor consultarTodasCategorias() {
		return consultarTodos(TABELA_CATEGORIA);
	}

	public Cursor consultarCategoria(long idCategoria) {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_ID,
				String.valueOf(idCategoria));
		return mCursor;

	}

	public Categoria consultar(long idCategoria) {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_ID,
				String.valueOf(idCategoria));
		Categoria cat = deCursorParaCategoria(mCursor);
		mCursor.close();
		return cat;

	}

	public boolean deleteCategoria(long id) {

		return remover(id);
	}

	public boolean isDescricaoCategoriaJaExiste(String descricao) {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_DESCRICAO,
				descricao);

		boolean jaExiste = false;

		if (mCursor.getCount() > 0) {
			jaExiste = true;
		}

		return jaExiste;
	}
	public Loader<Cursor> getLoaderAllContents() {
		return getLoader(TABELA_CATEGORIA);
	}

	public Loader<Cursor> getLoaderContents(long idCat) {
		return getLoader(TABELA_CATEGORIA,COLUNA_ID,String.valueOf(idCat));
	}
}
