package org.android.group.escolarmobile.conn;

import java.util.ArrayList;
import java.util.List;

import org.android.group.escolarmobile.app.teacher.ProfessorVO;
import org.android.group.escolarmobile.turma.TurmaVO;

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
	public static final String TABLE_MATERIA = "Materia";
	public static final String TABLE_MATRICULA = "Matricula";
	public static final String TABLE_PROFESSOR = "Professor";
	public static final String TABLE_TURMA = "Turma";
	
	public static final String COLUMN_DATA_NASCIMENTO = "dt_nascimento";
	public static final String COLUMN_DESCRICAO = "descricao";
	public static final String COLUMN_HORAS = "horas";
	public static final String COLUMN_ID = "_id";
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
		"CREATE TABLE Turma (_id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, descricao TEXT);";
	private static final String CREATE_ALUNO = 
		"CREATE TABLE Aluno (_id INTEGER PRIMARY KEY AUTOINCREMENT, id_turma INTEGER NOT NULL, registro TEXT NOT NULL, nome TEXT NOT NULL, dt_nascimento DATE);";
	private static final String CREATE_PROFESSOR = 
		"CREATE TABLE Professor (_id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT NOT NULL, nome TEXT NOT NULL, senha TEXT NOT NULL);";		
	private static final String CREATE_MATERIA = 
		"CREATE TABLE Materia (_id INTEGER PRIMARY KEY AUTOINCREMENT, id_professor INTEGER NOT NULL, id_turma INTEGER NOT NULL, nome TEXT NOT NULL, horas INTEGER, descricao TEXT);";
	private static final String CREATE_MATRICULA = 
		"CREATE TABLE Matricula (_id INTEGER PRIMARY KEY AUTOINCREMENT, id_aluno_turma INTEGER NOT NULL, id_materia INTEGER NOT NULL);";

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
			db.execSQL(CREATE_PROFESSOR);
			db.execSQL(CREATE_MATERIA);
			db.execSQL(CREATE_MATRICULA);
			
			insertDummyData(db);
		}
		
		/**
		 * Este método deve apenas inserir alguns dados para fins de testes.
		 * Não está claro se deverá ficar na versão final como valores default.
		 * 
		 * @param db
		 */
		private void insertDummyData(SQLiteDatabase db) {
			String sqlTurma = "INSERT INTO Turma(Nome, Descricao) VALUES(?,?)";
			db.execSQL(sqlTurma, new String[]{"1a. A", "Primeiro Ano - Classe A"});
			db.execSQL(sqlTurma, new String[]{"1a. B", "Primeiro Ano - Classe B"});
			db.execSQL(sqlTurma, new String[]{"2a. A", "Segundo Ano - Classe A"});
			db.execSQL(sqlTurma, new String[]{"3a. A", "Terceiro Ano - Classe A"});
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATRICULA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIA);
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
     * Se não puder ser criada, lançaa uma exceção para sinalizar a falha.
     * 
     * @return this (auto-refer�ncia, permitindo encadear métodos na inicialização).
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
			//initialValues.put(COLUMN_ID, turmaVO.getId());
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
	 * @return null se não encontrar a turma especificada.
	 */
	public TurmaVO consultarTurma(long id) {
		TurmaVO turma = null;
		Cursor c = consultarTurma(COLUMN_ID, String.valueOf(id));
		
		if(c != null) {
			c.moveToFirst();
			turma = new TurmaVO();
			turma.setId(c.getInt(0));
			turma.setNome(c.getString(1));
			turma.setDescricao(c.getString(2));
		}
		
		return turma;
	}	
	
	/**
	 * Retorna os dados da turma indicada.
	 * 
	 * @param nome
	 * @return null se não encontrar a turma especificada.
	 */
	public TurmaVO consultarTurma(String nome) {
		TurmaVO turma = null;
		Cursor c = consultarTurma(COLUMN_NOME, nome);
		
		if(c != null) {
			c.moveToFirst();
			
			if(!c.isAfterLast()) {
				turma = new TurmaVO();
				turma.setId(c.getLong(0));
				turma.setNome(c.getString(1));
				turma.setDescricao(c.getString(2));
			}
		}
		
		return turma;
	}
	
	/**
	 * Método privado para realizar consultas de turmas.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarTurma(String key, String value) {
		return consultar(TABLE_TURMA, 
				new String[] {COLUMN_ID, COLUMN_NOME, COLUMN_DESCRICAO},
				key, value);		
	}
	
	/**
	 * Remove a turma com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerTurma(long id) {
		return remover(TABLE_TURMA, id);
	}
	
	/**
	 * <b>Aparentemente, este método não é mais utilizado e, portanto, pode ser removido da classe.</b>
	 * Remove a turma com o nome especificado.
	 * 
	 * @param nome
	 * @return
	 */
	@Deprecated
	public boolean removerTurma(String nome) {
		Cursor cursor = consultarTurma(COLUMN_NOME, nome);
		
		if(cursor != null) {
			return removerTurma(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
		} else {
			return false;
		}
	}
	
	/**
	 * Cria um novo registro de professor na tabela. Se o registro for incluído com
	 * sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param professorVO DAO com os dados do professor.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirProfessor(ProfessorVO professorVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes e logins dos professores devem ser únicos.
		if(consultarProfessor(professorVO.getNome(), false) == null &&
				consultarProfessor(professorVO.getLogin(), true) == null) {
			ContentValues initialValues = new ContentValues();
			//initialValues.put(COLUMN_ID, professorVO.getId());
			initialValues.put(COLUMN_LOGIN, professorVO.getLogin());
			initialValues.put(COLUMN_NOME, professorVO.getNome());
			initialValues.put(COLUMN_SENHA, professorVO.getSenha());
			
			return mDb.insert(TABLE_PROFESSOR, null, initialValues);
		} else {
			return -1;
		}
	}

	/**
	 * Atualiza o registro de professor com os dados fornecidos.
	 * 
	 * @param professorVO
	 * @return
	 */
	public boolean atualizarProfessor(ProfessorVO professorVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, professorVO.getId());
		updatedValues.put(COLUMN_LOGIN, professorVO.getLogin());
		updatedValues.put(COLUMN_NOME, professorVO.getNome());
		updatedValues.put(COLUMN_SENHA, professorVO.getSenha());
			
		return mDb.update(TABLE_PROFESSOR, updatedValues, COLUMN_ID + " = " + professorVO.getId(), null) > 0;
	}
	
	/**
	 * Retorna o registro da professor com o ID fornecido, se existir.
	 * 
	 * @param id
	 * @return null se não encontrar o professor especificado.
	 */
	public ProfessorVO consultarProfessor(long id) {
		ProfessorVO professor = null;
		Cursor c = consultarTurma(COLUMN_ID, String.valueOf(id));
		
		if(c != null) {
			c.moveToFirst();
			professor = new ProfessorVO();
			professor.setId(c.getInt(0));
			professor.setLogin(c.getString(1));
			professor.setNome(c.getString(2));
			professor.setSenha(c.getString(3));
		}
		
		return professor;
	}	
	
	/**
	 * Retorna os dados do professor indicado.
	 * 
	 * @param key Nome ou login para ser procurado.
	 * @param isLogin <b>True</b> indica que o valor passado como chave é um login. <b>False</b> indica que é um nome.
	 * @return null se não encontrar o professor especificado.
	 */
	public ProfessorVO consultarProfessor(String key, boolean isLogin) {
		ProfessorVO professor = null;
		Cursor c = consultarProfessor(isLogin ? COLUMN_LOGIN : COLUMN_NOME, key);
		
		if(c != null) {
			c.moveToFirst();
			
			if(!c.isAfterLast()) {
				professor = new ProfessorVO();
				professor.setId(c.getLong(0));
				professor.setLogin(c.getString(1));
				professor.setNome(c.getString(2));
				professor.setSenha(c.getString(3));
			}
		}
		
		return professor;
	}
	
	/**
	 * Método privado para realizar consultas de professores.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarProfessor(String key, String value) {
		return consultar(TABLE_PROFESSOR, 
				new String[] {COLUMN_ID, COLUMN_LOGIN, COLUMN_NOME, COLUMN_DESCRICAO},
				key, value);
	}
	
	/**
	 * Remove o professor com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerProfessor(long id) {
		return remover(TABLE_PROFESSOR, id);
	}
	
	/**
	 * Método genérico para remover entradas nas tabelas baseadas no id fornecido.
	 * 
	 * @param table Tabela onde será executada a deleção.
	 * @param id Chave da linha a ser deletada
	 * @return <b>True</b> se a operação foi bem-sucedida; <b>false</b> em caso de erro.
	 */
	private boolean remover(String table, long id) {
		return mDb.delete(table, COLUMN_ID + " = " + id, null) > 0;
	}
	
	/**
	 * Método genérico para efetuar consultas às tabelas, utilizando a chave fornecida.
	 * 
	 * @param table Tabela onde será executada a busca.
	 * @param colunas Colunas que devem ser consideradas no retorno da busca.
	 * @param key Coluna que deverá conter a palavra-chave definida como <b>value</b>.
	 * @param value Palavra-chave da busca.
	 * @return Cursor na primeira posição, caso algum dado tenha sido encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	private Cursor consultar(String table, String[] colunas, String key, String value) {
		Cursor mCursor = 
			mDb.query(false, table, colunas, key + " = '" + value + "'", null, null, null, null, null);
		
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;

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
	
	/**
	 * Retorna uma lista com todos os valores encontrados para a coluna da tabela definida.
	 * 
	 * @param tabela Tabela de onde serão consultados os registros.
	 * @param coluna Coluna a ser exibida.
	 * @return Lista com os valores encontrados. Retorna uma lista vazia se não encontrar nenhum valor válido.
	 */
	public List<String> consultarTodos(String tabela, String coluna) {
		List<String> resultado = new ArrayList<String>();
		Cursor cursor = consultarTodos(tabela, new String[]{coluna});
		
		while(!cursor.isAfterLast()) {
			resultado.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return resultado;
	}
}
