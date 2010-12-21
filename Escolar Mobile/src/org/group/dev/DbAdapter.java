package org.group.dev;

import org.group.dev.turma.dao.TurmaVO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Classe para comunicação com o banco de dados. Realiza todas as operações de criação, inserção, remoção, atualização e consulta.
 * 
 * @author Otavio
 *
 */
public class DbAdapter {
	public static final String DB_NAME = "Escolar";
	public static final int DB_VERSION = 1;
	
	public static final String TABLE_ALUNO = "Aluno";
	public static final String TABLE_ALUNO_TURMA = "Aluno_Turma";
	public static final String TABLE_MATERIA = "Materia";
	public static final String TABLE_MATRICULA = "Matricula";
	public static final String TABLE_PROFESSOR = "Professor";
	public static final String TABLE_TURMA = "Turma";
	
	public static final String COLUMN_DATA_NASCIMENTO = "dt_nascimento";
	public static final String COLUMN_DESCRICAO = "descricao";
	public static final String COLUMN_HORAS = "horas";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ID_ALUNO = "id_aluno";
	public static final String COLUMN_ID_ALUNO_TURMA = "id_aluno_turma";
	public static final String COLUMN_ID_MATERIA = "id_materia";
	public static final String COLUMN_ID_PROFESSOR = "id_professor";
	public static final String COLUMN_ID_TURMA = "id_turma";
	public static final String COLUMN_LOGIN = "login";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_REGISTRO = "registro";
	public static final String COLUMN_SENHA = "senha";
	
	private static final String TAG = "DbAdapter";
	
	private static final String CREATE_TURMA = 
		"CREATE TABLE Turma (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, descricao TEXT);";
	private static final String CREATE_ALUNO = 
		"CREATE TABLE Aluno (id INTEGER PRIMARY KEY AUTOINCREMENT, registro TEXT NOT NULL, nome TEXT NOT NULL, dt_nascimento DATE);";
	private static final String CREATE_PROFESSOR = 
		"CREATE TABLE Professor (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT NOT NULL, nome TEXT NOT NULL, senha TEXT NOT NULL);";		
	private static final String CREATE_ALUNO_TURMA = 
		"CREATE TABLE Aluno_Turma (id INTEGER PRIMARY KEY AUTOINCREMENT, id_aluno INTEGER NOT NULL, id_turma INTEGER NOT NULL);";
	private static final String CREATE_MATERIA = 
		"CREATE TABLE Materia (id INTEGER PRIMARY KEY AUTOINCREMENT, id_professor INTEGER NOT NULL, id_turma INTEGER NOT NULL, nome TEXT NOT NULL, horas INTEGER, descricao TEXT);";
	private static final String CREATE_MATRICULA = 
		"CREATE TABLE Matricula (id INTEGER PRIMARY KEY AUTOINCREMENT, id_aluno_turma INTEGER NOT NULL, id_materia INTEGER NOT NULL);";

	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Esta inner class é responsável pelos controles de criação, atualização e instanciação do gerenciador do banco de dados.
	 * @author Otavio
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_ALUNO);
			db.execSQL(CREATE_TURMA);
			db.execSQL(CREATE_ALUNO_TURMA);
			db.execSQL(CREATE_PROFESSOR);
			db.execSQL(CREATE_MATERIA);
			db.execSQL(CREATE_MATRICULA);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATRICULA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNO_TURMA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNO);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TURMA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFESSOR);
			onCreate(db);			
		}
	}
	
	/**
     * Construtor.
     * 
     * @param ctx o Contexto no qual ele deve funcionar.
     */
	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	/**
     * Abre o banco de dados. Se não puder ser aberto, tenta criar uma nova instância do banco.
     * Se não puder ser criada, lança uma exceção para sinalizar a falha.
     * 
     * @return this (auto-referência, permitindo encadear métodos na inicialização).
     * @throws SQLException se o banco não puder ser criado ou instanciado.
     */
	public DbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	/**
	 * Cria um novo registro de turma na tabela. Se o registro for incluído com
	 * sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param turmaVO DAO com os dados da turma.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirTurma(TurmaVO turmaVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes das turmas devem ser únicos.
		if(consultarTurma(turmaVO.getNome()) == null) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID, turmaVO.getId());
			initialValues.put(COLUMN_NOME, turmaVO.getNome());
			initialValues.put(COLUMN_DESCRICAO, turmaVO.getDescricao());
			
			return mDb.insert(TABLE_TURMA, null, initialValues);
		} else {
			return -1;
		}
	}

	/**
	 * Atualiza o registro de turma com os dados fornecidos.
	 * 
	 * @param turmaVO
	 * @return
	 */
	public boolean atualizarTurma(TurmaVO turmaVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, turmaVO.getId());
		updatedValues.put(COLUMN_NOME, turmaVO.getNome());
		updatedValues.put(COLUMN_DESCRICAO, turmaVO.getDescricao());
			
		return mDb.update(TABLE_TURMA, updatedValues, COLUMN_ID + " = " + turmaVO.getId(), null) > 0;
	}
	
	/**
	 * Retorna o registro da turma com o ID fornecido, se existir.
	 * 
	 * @param id
	 * @return
	 */
	public Cursor consultarTurma(long id) {
		return consultarTurma(COLUMN_ID, String.valueOf(id));
	}
	
	/**
	 * Retorna o registro da turma com o nome fornecido, se existir. Caso exista mais de uma turma com o mesmo nome, 
	 * o cursor apontará para o primeiro resultado encontrado.
	 * 
	 * @param nome
	 * @return
	 */
	public Cursor consultarTurma(String nome) {
		return consultarTurma(COLUMN_NOME, nome);
	}
	
	/**
	 * Método privado para realizar consultas de turmas.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarTurma(String key, String value) {
		String[] colunas = new String[] {COLUMN_ID, COLUMN_NOME, COLUMN_DESCRICAO};
		
		Cursor mCursor = 
			mDb.query(false, TABLE_TURMA, colunas, key + " = " + value, null, null, null, null, null);
		
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	/**
	 * Remove a turma com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerTurma(long id) {
		return mDb.delete(TABLE_TURMA, COLUMN_ID + " = " + id, null) > 0;
	}
	
	/**
	 * Remove a turma com o nome especificado.
	 * 
	 * @param nome
	 * @return
	 */
	public boolean removerTurma(String nome) {
		Cursor cursor = consultarTurma(nome);
		
		if(cursor != null) {
			return removerTurma(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
		} else {
			return false;
		}
	}
	
	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela definida.
	 * 
	 * @param tabela Tabela de onde serão consultados os registros.
	 * @param colunas Colunas a serem exibidas.
	 * @return Cursor posicionado no primeiro elemento encontrado.
	 */
	public Cursor consultarTodos(String tabela, String[] colunas) {
		Cursor mCursor =
			mDb.query(tabela, colunas, null, null, null, null, null);
		
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
}
