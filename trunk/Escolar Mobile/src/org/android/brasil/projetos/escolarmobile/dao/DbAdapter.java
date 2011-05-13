package org.android.brasil.projetos.escolarmobile.dao;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe para comunicação com o banco de dados. Realiza todas as operações de
 * criação, inserção, remoção, atualização e consulta.
 * 
 * @author Otavio
 * 
 */
public class DbAdapter {
	public static final String DB_NAME = "Escolar";
	public static final int DB_VERSION = 1;

	private static final String TAG = "DbAdapter";
	private static DatabaseHelper mDbHelper;

	private DbAdapter(){
		
	}
	/**
	 * Esta inner class é responsável pelos controles de criação, atualização e
	 * instanciação do gerenciador do banco de dados.
	 * 
	 * @author Otavio
	 * 
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(AlunoVO.createTableString());
			db.execSQL(TurmaVO.createTableString());
			db.execSQL(ProfessorVO.createTableString());
			db.execSQL(MateriaVO.createTableString());
			db.execSQL(TurmaMateriaVO.createTableString());
			db.execSQL(PresencaVO.createTableString());
			db.execSQL(NotaVO.createTableString());

			AlunoVO.createDummyData(db);
			TurmaVO.createDummyData(db);
			ProfessorVO.createDummyData(db);
			MateriaVO.createDummyData(db);
			TurmaMateriaVO.createDummyData(db);
			PresencaVO.createDummyData(db);
			NotaVO.createDummyData(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL(PresencaVO.upgradeTableString());
			db.execSQL(NotaVO.upgradeTableString());
			db.execSQL(TurmaMateriaVO.upgradeTableString());
			db.execSQL(AlunoVO.upgradeTableString());
			db.execSQL(MateriaVO.upgradeTableString());
			db.execSQL(TurmaVO.upgradeTableString());
			db.execSQL(ProfessorVO.upgradeTableString());
			onCreate(db);
		}
	}

	/**
	 * Abre o banco de dados. Se não puder ser aberto, tenta criar uma nova
	 * instância do banco. Se não puder ser criada, lança uma exceção para
	 * sinalizar a falha.
	 * 
	 * @return this (auto-referência, permitindo encadear métodos na
	 *         inicialização).
	 * @throws SQLException
	 *             se o banco não puder ser criado ou instanciado.
	 */
	public static SQLiteDatabase open(Context ctx) throws SQLException {
		mDbHelper = new DatabaseHelper(ctx);
		return mDbHelper.getWritableDatabase();
	}

	public static void close() {
		mDbHelper.close();
	}

}
