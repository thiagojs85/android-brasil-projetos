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

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {


	private static final String TAG = "EmprestimosDbAdapter";
	private static DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;
	

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 5;

	private static Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(EmprestimoDAO.CREATE_TABELA_EMPRESTIMO);
			db.execSQL(CategoriaDAO.CRIAR_TABELA_CATEGORIA);
			db.execSQL(CategoriaDAO.categoriaTodas);
			db.execSQL(CategoriaDAO.categoriaDefaultCD);
			db.execSQL(CategoriaDAO.categoriaDefaultDVD);
			db.execSQL(CategoriaDAO.categoriaDefaultLivro);
			db.execSQL(CategoriaDAO.categoriaDefaultOutra);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + EmprestimoDAO.TABELA_EMPRESTIMOS);
			db.execSQL("DROP TABLE IF EXISTS " + CategoriaDAO.TABELA_CATEGORIA);
			onCreate(db);
		}
	}

	public DbAdapter(Context ctx) {
		DbAdapter.mCtx = ctx;
	}

	public static  SQLiteDatabase open(Context ctx) throws SQLException {
		mCtx = ctx;
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return mDb;
	}

	public static void close() {
		mDbHelper.close();
	}
	
}
