package org.android.brasil.projetos.dao;

import org.android.brasil.projetos.model.Categoria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class CategoriaDAO  extends BasicoDAO{
	
	public CategoriaDAO(Context ctx) {
		super(ctx);
	}

	public static final String COLUNA_ID = "_id";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String TABELA_CATEGORIA = "categoria";
	
	
	private static final String CRIAR_TABELA_CATEGORIA = "create table " + TABELA_CATEGORIA
	+ " ( " + COLUNA_ID + " integer primary key autoincrement, " + COLUNA_DESCRICAO
	+ " text not null);";
	
	public static final String TODOS = "Todos";
	public static final String OUTRA = "Outra";
	
	private static final String categoriaDefault = "insert into categoria (descricao) values ('"+OUTRA+"')";
	public static final String categoriaDefaultTodos = "insert into categoria (descricao) values ('"+TODOS+"')";
	public static final String categoriaDefaultCD = "insert into categoria (descricao) values ('CD')";
	public static final String categoriaDefaultDVD = "insert into categoria (descricao) values ('DVD')";
	public static final String categoriaDefaultLivro = "insert into categoria (descricao) values ('Livro')";
	
	
	
	
	public static String createTableCategoria() {
		return CRIAR_TABELA_CATEGORIA;
	}
	
	public static Categoria deCursorParaCategoria(Cursor c){
		Categoria cat = new Categoria();
		cat.setId(c.getLong(c.getColumnIndex(COLUNA_ID)));
		cat.setNomeCategoria(c.getString(c.getColumnIndex(COLUNA_DESCRICAO)));
		return cat;
	}
	
	public static ContentValues inserirCategoria(String descricao) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUNA_DESCRICAO, descricao);

		return initialValues;
	}
	
	public static String insertCategoriaDefault() {
		return categoriaDefault;
	}
	
	public static long inserir(Categoria cat){
		ContentValues values = deCategoriaParaContentValues(cat);
		return inserir(TABELA_CATEGORIA, values);
	}
	
	public static long atualizar(Categoria cat){
		ContentValues values = deCategoriaParaContentValues(cat);
		return atualizar(TABELA_CATEGORIA, values,new String[] {COLUNA_ID} ,new String[] {String.valueOf(cat.getId())});
	}

	public static boolean remover(long idCategoria){
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

	
	public static Cursor consultarTodasCategorias() throws SQLException {
		return consultarTodos(TABELA_CATEGORIA);
	}
	
	public static Cursor consultarCategoria(long idCategoria) throws SQLException {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_ID, String.valueOf(idCategoria));
		return mCursor;

	}
	
	public static Categoria consultar(long idCategoria) throws SQLException {
		Cursor mCursor = consultar(TABELA_CATEGORIA, COLUNA_ID, String.valueOf(idCategoria));
		Categoria cat = deCursorParaCategoria(mCursor);
		mCursor.close();
		return cat;

	}
	
	public static boolean deleteCategoria(long id) {

		return remover(id);
	}

	

}
