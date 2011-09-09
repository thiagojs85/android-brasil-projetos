/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.android.brasil.projetos.dao;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EmprestimoDbAdapter {

	public static final String COLUNA_ID = "_id";
	public static final String COLUNA_ITEM = "item";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String COLUNA_STATUS = "status";
	public static final String COLUNA_ATIVAR_ALARME = "status_alarme";
	public static final String COLUNA_DATA_DEVOLUCAO = "devolucao";
	public static final String COLUNA_ID_CONTATO = "id_contato";
	public static final String COLUNA_ID_CATEGORIA = "id_categoria";

	public static final String TABELA_EMPRESTIMOS = "emprestimos";

	public static final int STAUTS_EMPRESTAR = 0;
	public static final int STAUTS_PEGAR_EMPRESTADO = 1;
	
	public static final int ATIVAR_ALARME = 0;
	public static final int DESATIVAR_ALARME = 1;

	private static final String TAG = "EmprestimosDbAdapter";
	private DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;
	
	private static final String CRIAR_TABELA_EMPRESTIMOS = "create table " + TABELA_EMPRESTIMOS
			+ " ( " + COLUNA_ID + " integer primary key autoincrement, " + COLUNA_ITEM
			+ " text not null, " + COLUNA_DESCRICAO + " text not null," + COLUNA_STATUS
			+ " Integer not null, "  + COLUNA_ATIVAR_ALARME
			+ " Integer not null, " + COLUNA_DATA_DEVOLUCAO + " Integer, " + COLUNA_ID_CONTATO
			+ " Integer not null, " + COLUNA_ID_CATEGORIA  + " Integer not null);";

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 3;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(CRIAR_TABELA_EMPRESTIMOS);
			db.execSQL(CategoriaVO.createTableCategoria());
			db.execSQL(CategoriaVO.insertCategoriaDefault());
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABELA_EMPRESTIMOS);
			onCreate(db);
		}
	}

	public EmprestimoDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public EmprestimoDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long inserirEmprestimo(String item, String descricao, Date data, int status,int ativarAlarme,
			long idContato) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUNA_ITEM, item);
		initialValues.put(COLUNA_DESCRICAO, descricao);
		initialValues.put(COLUNA_DATA_DEVOLUCAO, data.getTime());
		initialValues.put(COLUNA_STATUS, status);
		initialValues.put(COLUNA_ATIVAR_ALARME, ativarAlarme);
		initialValues.put(COLUNA_ID_CONTATO, idContato);

		return mDb.insert(TABELA_EMPRESTIMOS, null, initialValues);
	}

	public boolean deleteEmprestimo(long id) {

		return mDb.delete(TABELA_EMPRESTIMOS, COLUNA_ID + "=" + id, null) > 0;
	}

	public Cursor consultarTodos() {

		return mDb.query(TABELA_EMPRESTIMOS, null, null, null, null, null, null);
	}

	public Cursor consultarEmprestimo(long id) throws SQLException {

		Cursor mCursor =

		mDb.query(true, TABELA_EMPRESTIMOS, null, COLUNA_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean atualizarEmprestimo(long id, String item, String descricao, Date data,
			int status,int ativarAlarme, long idContato) {
		ContentValues values = new ContentValues();
		values.put(COLUNA_ITEM, item);
		values.put(COLUNA_DESCRICAO, descricao);
		values.put(COLUNA_DATA_DEVOLUCAO, data.getTime());
		values.put(COLUNA_STATUS, status);
		values.put(COLUNA_ATIVAR_ALARME, ativarAlarme);
		values.put(COLUNA_ID_CONTATO, idContato);

		return mDb.update(TABELA_EMPRESTIMOS, values, COLUNA_ID + "=" + id, null) > 0;
	}
}
