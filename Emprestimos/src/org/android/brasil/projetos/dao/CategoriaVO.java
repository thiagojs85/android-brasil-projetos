package org.android.brasil.projetos.dao;

import android.content.ContentValues;

public class CategoriaVO {
	
	public static final String COLUNA_ID = "_id";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String TABELA_CATEGORIA = "categoria";
	
	
	private static final String CRIAR_TABELA_CATEGORIA = "create table " + TABELA_CATEGORIA
	+ " ( " + COLUNA_ID + " integer primary key autoincrement, " + COLUNA_DESCRICAO
	+ " text not null);";
	
	private static final String categoriaDefault = "insert into categoria (descricao) values ('OUTRA')";
	
	
	public static String createTableCategoria() {
		return CRIAR_TABELA_CATEGORIA;
	}
	
	public static ContentValues inserirCategoria(String descricao) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUNA_DESCRICAO, descricao);

		return initialValues;
	}
	
	public static String insertCategoriaDefault() {
		return categoriaDefault;
	}
	
	

}
