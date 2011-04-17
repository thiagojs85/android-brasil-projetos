package org.android.group.escolarmobile.conn;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.android.group.escolarmobile.app.student.AlunoVO;
import org.android.group.escolarmobile.app.student.PresencaVO;
import org.android.group.escolarmobile.app.subject.MateriaVO;
import org.android.group.escolarmobile.app.subject.NotaVO;
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
 * Classe para comunica��o com o banco de dados. Realiza todas as opera��es de cria��o, inser��o, remo��o, 
 * atualiza��o e consulta.
 * 
 * @author Otavio
 *
 */
public class DbAdapter {
	public static final String DB_NAME = "Escolar";
	public static final int DB_VERSION = 1;
	
	public static final String TABLE_ALUNO = "Aluno";
	public static final String TABLE_PRESENCA = "Presenca";
	public static final String TABLE_MATERIA = "Materia";
	public static final String TABLE_MATRICULA = "Matricula";
	public static final String TABLE_NOTA = "Nota";
	public static final String TABLE_PROFESSOR = "Professor";
	public static final String TABLE_TURMA = "Turma";
	
	public static final String COLUMN_DATA = "data";
	public static final String COLUMN_DATA_NASCIMENTO = "dt_nascimento";
	public static final String COLUMN_DESCRICAO = "descricao";
	public static final String COLUMN_HORAS = "horas";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ID_ALUNO = "id_aluno";
	public static final String COLUMN_ID_MATERIA = "id_materia";
	public static final String COLUMN_ID_MATRICULA = "id_matricula";
	public static final String COLUMN_ID_PROFESSOR = "id_professor";
	public static final String COLUMN_ID_TURMA = "id_turma";
	public static final String COLUMN_LOGIN = "login";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_NOTA = "nota";
	public static final String COLUMN_PADRAO = "padrao";
	public static final String COLUMN_PERIODO = "periodo";
	public static final String COLUMN_FALTA = "falta";
	public static final String COLUMN_REGISTRO = "registro";
	public static final String COLUMN_SENHA = "senha";
	
	
	private static final String TAG = "DbAdapter";
	
	private static final String CREATE_ALUNO = 
		"CREATE TABLE " + TABLE_ALUNO + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_REGISTRO + " TEXT NOT NULL, " + COLUMN_NOME + " TEXT NOT NULL, " + COLUMN_ID_TURMA +
		" INTEGER NOT NULL, " + COLUMN_DATA_NASCIMENTO + " DATE);";
	private static final String CREATE_PRESENCA = 
		"CREATE TABLE " + TABLE_PRESENCA + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
		COLUMN_DATA + " DATE NOT NULL, " + COLUMN_ID_ALUNO + " INTEGER NOT NULL, " + COLUMN_FALTA +
		" INTEGER NOT NULL);";
	private static final String CREATE_PROFESSOR = 
		"CREATE TABLE Professor (_id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT NOT NULL, nome TEXT NOT NULL, senha TEXT NOT NULL);";		
	private static final String CREATE_MATERIA = 
		"CREATE TABLE " + TABLE_MATERIA + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_ID_PROFESSOR + " INTEGER NOT NULL, " + COLUMN_NOME + " TEXT NOT NULL, " + COLUMN_HORAS +
		" INTEGER, " + COLUMN_DESCRICAO + " TEXT, " + COLUMN_PADRAO + " TEXT);";
	private static final String CREATE_NOTA = 
		"CREATE TABLE Nota (_id INTEGER PRIMARY KEY AUTOINCREMENT, id_matricula INTEGER NOT NULL, periodo INTEGER NOT NULL, nota FLOAT NOT NULL);";
	private static final String CREATE_TURMA = 
		"CREATE TABLE Turma (_id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, descricao TEXT);";
	private static final String CREATE_MATRICULA= 
		"CREATE TABLE " + TABLE_MATRICULA + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
		COLUMN_ID_MATERIA + " INTEGER NOT NULL, " + COLUMN_ID_TURMA + " INTEGER NOT NULL);";
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Esta inner class � respons�vel pelos controles de cria��o, atualiza��o e instancia��o do gerenciador do banco de dados.
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
			db.execSQL(CREATE_PRESENCA);
			db.execSQL(CREATE_NOTA);
			
			insertDummyData(db);
		}
		
		/**
		 * Este m�todo deve apenas inserir alguns dados para fins de testes.
		 * N�o est� claro se dever� ficar na vers�o final como valores default.
		 * 
		 * @param db
		 */
		private void insertDummyData(SQLiteDatabase db) {
			Log.v(TAG, "Creating dummy data in tables...");
			String sql = "INSERT INTO " + TABLE_PROFESSOR + "(" + COLUMN_LOGIN + ", " +
					COLUMN_NOME + ", " + COLUMN_SENHA + ") VALUES(?,?,?)";
			db.execSQL(sql, new String[]{"otavio", "Otavio K Rofatto", "123"});
			db.execSQL(sql, new String[]{"julio", "Julio Cotta", "123"});
			db.execSQL(sql, new String[]{"neto", "Neto", "123"});
			
			sql = "INSERT INTO " + TABLE_TURMA + "(" + COLUMN_NOME + ", " +
					COLUMN_DESCRICAO + ") VALUES(?,?)";
			db.execSQL(sql, new String[]{"1a. A", "Primeiro Ano - Classe A"});
			db.execSQL(sql, new String[]{"1a. B", "Primeiro Ano - Classe B"});
			db.execSQL(sql, new String[]{"2a. A", "Segundo Ano - Classe A"});
			db.execSQL(sql, new String[]{"3a. A", "Terceiro Ano - Classe A"});
			
			sql = "INSERT INTO " + TABLE_MATERIA + "(" + COLUMN_ID_PROFESSOR + ", " +
					COLUMN_NOME + ", " + COLUMN_HORAS + ", " + COLUMN_DESCRICAO + ", " +
					COLUMN_PADRAO + ") VALUES((SELECT _id FROM Professor WHERE login = ?),?,?,?,?)";
			db.execSQL(sql, new String[]{"otavio", "Português (Mat�ria Teste)", "40", "Este � um teste", "N"});
			db.execSQL(sql, new String[]{"otavio", "Matem�tica (Mat�ria Teste)", "44", "Este � um teste", "N"});
			db.execSQL(sql, new String[]{"julio", "Hist�ria (Mat�ria Teste)", "100", "Este � um teste", "N"});
			db.execSQL(sql, new String[]{"neto", "Física (Mat�ria Teste)", "85", "Este � um teste", "N"});
			db.execSQL(sql, new String[]{"neto", "Química (Mat�ria Teste)", "10", "Este � um teste", "N"});
			db.execSQL(sql, new String[]{"neto", "Biologia (Mat�ria Teste)", "5", "Este � um teste", "N"});
			db.execSQL(sql, new String[]{"otavio", "Mat�ria TESTE (Mat�ria Teste)", "20", "Este � um padr�o", "S"});
			
			sql = "INSERT INTO " + TABLE_MATRICULA + "(" + COLUMN_ID_MATERIA + ", " + 
					COLUMN_ID_TURMA + ") VALUES((SELECT _id FROM Materia WHERE horas = ?), " +
					"(SELECT _id FROM Turma WHERE nome = ?))";
			db.execSQL(sql, new String[]{"40", "1a. A"});
			db.execSQL(sql, new String[]{"44", "1a. A"});
			db.execSQL(sql, new String[]{"100", "1a. A"});
			db.execSQL(sql, new String[]{"85", "1a. A"});
			db.execSQL(sql, new String[]{"10", "1a. A"});
			db.execSQL(sql, new String[]{"5", "1a. A"});
			db.execSQL(sql, new String[]{"44", "2a. A"});
			db.execSQL(sql, new String[]{"85", "2a. A"});
				
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESENCA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATRICULA);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALUNO);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIA);
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
     * Abre o banco de dados. Se n�o puder ser aberto, tenta criar uma nova instância do banco.
     * Se n�o puder ser criada, lan�aa uma exce��o para sinalizar a falha.
     * 
     * @return this (auto-refer�ncia, permitindo encadear m�todos na inicializa��o).
     * @throws SQLException se o banco n�o puder ser criado ou instanciado.
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
	 * Recupera os nomes de todos os alunos matriculados na mat�ria definida.
	 * 
	 * @param id C�digo de identifica��o da mat�ria na tabela.
	 * @return Cursor apontando para o primeiro elemento encontrado. NULL se n�o houver nenhuma entrada.
	 */
	public Cursor acessarAlunosPorMaterias(long id) {
		String sql = "SELECT a.* FROM " + TABLE_ALUNO + " a, " + TABLE_MATRICULA + " m WHERE " +
				"m." + COLUMN_ID_MATERIA + " = ? AND a." + COLUMN_ID_TURMA + " = m." + COLUMN_ID_TURMA;  
		Cursor c = mDb.rawQuery(sql, new String[]{String.valueOf(id)});
		
		if(c != null) {
			c.moveToFirst();
			
			if(c.isAfterLast()) {
				return null;
			} else {
				return c;
			}
		} else {
			return null;
		}
		/*
		Cursor c = consultar(TABLE_MATRICULA, new String[]{COLUMN_ID, COLUMN_ID_ALUNO}, 
			    COLUMN_ID_MATERIA, String.valueOf(id));
		if(c!= null) {
			c.moveToFirst();
			String values = new String();
			
			if(!c.isAfterLast()) {
				while(!c.isAfterLast()) {
					values += ", " + c.getLong(1);
					c.moveToNext();
				}
				
				values = values.substring(2);
				
				c = mDb.query(TABLE_ALUNO, 
						new String[]{}, COLUMN_ID + " IN (" + values + ")", 
						null, null, null, COLUMN_NOME + " asc");
				return c;
			} else {
				return null;
			}
		} else {
			return null;
		}
		*/
	}

	/**
	 * Atualiza o registro de aluno com os dados fornecidos.
	 * 
	 * @param alunoVO
	 * @return
	 */
	public boolean atualizarAluno(AlunoVO alunoVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, alunoVO.getId());
		updatedValues.put(COLUMN_REGISTRO, alunoVO.getRegistro());
		updatedValues.put(COLUMN_NOME, alunoVO.getNome());
		updatedValues.put(COLUMN_ID_TURMA, alunoVO.getIdTurma());
		updatedValues.put(COLUMN_DATA_NASCIMENTO, alunoVO.getDataNascimento());
			
		return mDb.update(TABLE_ALUNO, updatedValues, COLUMN_ID + " = " + alunoVO.getId(), null) > 0;
	}
	
	/**
	 * Retorna o registro do aluno com o ID fornecido, se existir.
	 * 
	 * @param id
	 * @return null se n�o encontrar o aluno especificado.
	 */
	public AlunoVO consultarAluno(long id) {
		AlunoVO aluno = null;
		Cursor c = consultarAluno(COLUMN_ID, String.valueOf(id));
		
		if(c != null) {
			c.moveToFirst();
			aluno = new AlunoVO();
			aluno.setId(c.getInt(0));
			aluno.setRegistro(c.getString(1));
			aluno.setNome(c.getString(2));
			aluno.setIdTurma(c.getInt(3));
			aluno.setDataNascimento(c.getString(4));
		}
		
		return aluno;
	}	
	
	/**
	 * Retorna os dados do aluno indicado.
	 * 
	 * @param key Nome para ser procurado.
	 * @return null se n�o encontrar o aluno especificado.
	 */
	public AlunoVO consultarAluno(String key) {
		AlunoVO aluno = null;
		Cursor c = consultarProfessor(COLUMN_NOME, key);
		
		if(c != null) {
			c.moveToFirst();
			
			if(!c.isAfterLast()) {
				aluno = new AlunoVO();
				aluno.setId(c.getInt(0));
				aluno.setRegistro(c.getString(1));
				aluno.setNome(c.getString(2));
				aluno.setIdTurma(c.getInt(3));
				aluno.setDataNascimento(c.getString(4));
			}
		}
		
		return aluno;
	}
	
	/**
	 * M�todo privado para realizar consultas de alunos.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarAluno(String key, String value) {
		return consultar(TABLE_ALUNO, 
				new String[] {COLUMN_ID, COLUMN_REGISTRO, COLUMN_NOME, COLUMN_ID_TURMA, COLUMN_DATA_NASCIMENTO},
				key, value);
	}
	
	/**
	 * Cria um novo registro de aluno na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param alunoVO DAO com os dados do aluno.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirAluno(AlunoVO alunoVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes dos alunos devem ser �nicos.
		if(consultarAluno(alunoVO.getNome()) == null) {
			ContentValues initialValues = new ContentValues();
			//initialValues.put(COLUMN_ID, alunoVO.getId());
			initialValues.put(COLUMN_REGISTRO, alunoVO.getRegistro());
			initialValues.put(COLUMN_NOME, alunoVO.getNome());
			initialValues.put(COLUMN_ID_TURMA, alunoVO.getIdTurma());
			initialValues.put(COLUMN_DATA_NASCIMENTO, alunoVO.getDataNascimento());
			
			return mDb.insert(TABLE_ALUNO, null, initialValues);
		} else {
			return -1;
		}
	}
	
	/**
	 * Remove o aluno com o id especificado. Remove as entradas dependentes tamb�m (entradas na tabela de Matricula).
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerAluno(long id) {
		boolean resultado = false;
		mDb.beginTransaction();
		
		try {
			//removerMatricula(COLUMN_ID_ALUNO, id);
			remover(TABLE_ALUNO, id);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}
		
		return resultado;
	}
	
	/**
	 * Remove os alunos que possuam o valor <b>id</b> na coluna <b>column</b>.
	 * 
	 * @param column
	 * @param id
	 * @return
	 */
	public boolean removerAluno(String column, long id) {
		Cursor cursor = consultar(TABLE_ALUNO, new String[]{COLUMN_ID}, column, String.valueOf(id));
		
		if(cursor != null) {
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()) {
				removerAluno(cursor.getLong(0));
				cursor.moveToNext();
			}
			
			cursor.close();
		}
		
		return true;
	}
	
	/**
	 * Atualiza o registro de mat�ria com os dados fornecidos.
	 * 
	 * @param materiaVO
	 * @return
	 */
	public boolean atualizarMateria(MateriaVO materiaVO) {
		boolean success = false;
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, materiaVO.getId());
		updatedValues.put(COLUMN_ID_PROFESSOR, materiaVO.getIdProfessor());
		updatedValues.put(COLUMN_NOME, materiaVO.getNome());
		updatedValues.put(COLUMN_HORAS, materiaVO.getHoras());
		updatedValues.put(COLUMN_DESCRICAO, materiaVO.getDescricao());
		updatedValues.put(COLUMN_PADRAO, materiaVO.getPadrao());
		
		mDb.beginTransaction();
		try {
			success = mDb.update(TABLE_MATERIA, updatedValues, COLUMN_ID + " = " + materiaVO.getId(), null) > 0;
			success &= limparMatricula(materiaVO.getId());
			success &= inserirMatricula(materiaVO);
			
			if(success) {
				mDb.setTransactionSuccessful();
			}
	   } finally {
		     mDb.endTransaction();
		   }

		return success;
	}
	
	/**
	 * Retorna o registro da mat�ria com o ID fornecido, se existir.
	 * 
	 * @param id
	 * @return null se n�o encontrar o aluno especificado.
	 */
	public MateriaVO consultarMateria(long id) {
		MateriaVO materia = null;
		Cursor c = consultarMateria(COLUMN_ID, String.valueOf(id));
		
		if(c != null) {
			c.moveToFirst();
			materia = new MateriaVO();
			materia.setId(c.getInt(0));
			materia.setIdProfessor(c.getLong(1));
			//materia.setIdTurma(c.getLong(2));
			materia.setNome(c.getString(2));
			materia.setHoras(c.getInt(3));
			materia.setDescricao(c.getString(4));
			materia.setPadrao(c.getString(5));
			
			c.close();
		}
		
		return materia;
	}	
	
	/**
	 * Retorna os dados da mat�ria indicada.
	 * 
	 * @param key Nome para ser procurado.
	 * @return null se n�o encontrar o aluno especificado.
	 */
	public MateriaVO consultarMateria(String key) {
		MateriaVO materia = null;
		Cursor c = consultarMateria(COLUMN_NOME, key);
		
		if(c != null) {
			c.moveToFirst();
			
			if(!c.isAfterLast()) {
				materia = new MateriaVO();
				materia.setId(c.getInt(0));
				materia.setIdProfessor(c.getLong(1));
				//materia.setIdTurma(c.getLong(2));
				materia.setNome(c.getString(2));
				materia.setHoras(c.getInt(3));
				materia.setDescricao(c.getString(4));
				materia.setPadrao(c.getString(5));
			}
			
			c.close();
		}
		
		return materia;
	}
	
	/**
	 * M�todo privado para realizar consultas de alunos.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarMateria(String key, String value) {
		return consultar(TABLE_MATERIA,
				//new String[] {COLUMN_ID, COLUMN_ID_PROFESSOR, COLUMN_ID_TURMA, COLUMN_NOME, COLUMN_HORAS, COLUMN_DESCRICAO},
				new String[] {COLUMN_ID, COLUMN_ID_PROFESSOR, COLUMN_NOME, COLUMN_HORAS, COLUMN_DESCRICAO, COLUMN_PADRAO},
				key, value);
	}
	
	/**
	 * Consulta todas as mat�rias da turma dada.
	 * 
	 * @param idTurma C�digo de ID da turma.
	 * @return vetor de ids das mat�rias encontradas.
	 */
	private long[] consultarMateriasPorTurma(long idTurma) {
		ArrayList<String> resultados = new ArrayList<String>();
		
		Cursor c = consultar(TABLE_MATRICULA, new String[]{COLUMN_ID_MATERIA}, 
				COLUMN_ID_TURMA, String.valueOf(idTurma));
		
		if(c != null) {
			c.moveToFirst();
			
			while(!c.isAfterLast()) {
				resultados.add(String.valueOf(c.getLong(0)));
				c.moveToNext();
			}
			
			c.close();
			
			if(!resultados.isEmpty()) {
				long[] ids = new long[resultados.size()];
				for(int i = 0; i < ids.length; i++) {
					ids[i] = Long.parseLong(resultados.get(i));
				}
				return ids;
			}
		}
		
		// Se o programa chegar a este ponto, � porque n�o foi encontrada nenhuma mat�ria.
		return null;
	}
	
	/**
	 * Devolve um Cursor com todas as  mat�rias de uma determinada turma.
	 * 
	 * @param idTurma C�digo de identifica��o da turma.
	 * @return Cursor apontando para o primeiro elemento. Se n�o houver nenhuma mat�ria, retorna NULL.
	 */
	public Cursor acessarMateriasPorTurma(long idTurma) {

		String sql = "SELECT mt.* FROM " + TABLE_MATERIA + " mt, " + TABLE_MATRICULA + " mc WHERE " +
				"mc." + COLUMN_ID_TURMA + " = ? AND mt." + COLUMN_ID + " = mc." + COLUMN_ID_MATERIA;  
		Cursor c = mDb.rawQuery(sql, new String[]{String.valueOf(idTurma)});
		
		if(c != null) {
			c.moveToFirst();
			
			if(c.isAfterLast()) {
				return null;
			} else {
				return c;
			}
		} else {
			return null;
		}
		
		/*
		Cursor c = consultar(TABLE_MATRICULA, new String[]{COLUMN_ID, COLUMN_ID_MATERIA}, 
			    COLUMN_ID_TURMA, String.valueOf(idTurma));
		if(c!= null) {
			c.moveToFirst();
			String values = new String();
			
			if(!c.isAfterLast()) {
				while(!c.isAfterLast()) {
					values += ", " + c.getLong(1);
					c.moveToNext();
				}
				
				values = values.substring(2);
				
				c = mDb.query(TABLE_MATERIA, 
						null, COLUMN_ID + " IN (" + values + ")", 
						null, null, null, null);
				return c;
			} else {
				c.close();
				return null;
			}
		} else {
			return null;
		}*/
	}
	
	/**
	 * Cria um novo registro de mat�ria na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param materiaVO DAO com os dados da mat�ria.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirMateria(MateriaVO materiaVO) {
		boolean success = false;
		
		// Apesar de ID ser a verdadeira chave do registro, os nomes das mat�rias devem ser �nicos.
		if(consultarMateria(materiaVO.getNome()) == null) {
			ContentValues initialValues = new ContentValues();
			//initialValues.put(COLUMN_ID, alunoVO.getId());
			initialValues.put(COLUMN_ID_PROFESSOR, materiaVO.getIdProfessor());
			//initialValues.put(COLUMN_ID_TURMA, materiaVO.getIdTurma());
			initialValues.put(COLUMN_NOME, materiaVO.getNome());
			initialValues.put(COLUMN_HORAS, materiaVO.getHoras());
			initialValues.put(COLUMN_DESCRICAO, materiaVO.getDescricao());
			
			mDb.beginTransaction();
			try {
				materiaVO.setId(mDb.insert(TABLE_MATERIA, null, initialValues));
				success = materiaVO.getId() > 0;
				success &= inserirMatricula(materiaVO);
				
				if(success) {
					mDb.setTransactionSuccessful();
				}
		   } finally {
			     mDb.endTransaction();
			   }

			return materiaVO.getId();
		} else {
			return -1;
		}
	}
	
	/**
	 * Remove a mat�ria com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerMateria(long id) {
		boolean resultado = false;
		
		mDb.beginTransaction();
		
		try {
			removerMatricula(COLUMN_ID_MATERIA, id);
			remover(TABLE_MATERIA, id);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}
		
		return resultado;		
	}
	
	/**
	 * Remove as materias da tabela que possuam o valor <b>value</b> na coluna <b>column</b>.
	 * 
	 * @param column Coluna considerada para dele��o.
	 * @param value Valor considerado para dele��o.
	 * @return
	 */
	public boolean removerMateria(String column, long value) {
		boolean resultado = false;
		boolean transacaoExterna = true;
		
		if(!mDb.inTransaction()) {
			mDb.beginTransaction();
			transacaoExterna = false;
		}			
			
		try {
			Cursor cursor = consultar(TABLE_MATERIA, new String[]{COLUMN_ID}, column, String.valueOf(value));
			
			if(cursor != null) {
				cursor.moveToFirst();
				
				while(!cursor.isAfterLast()) {
					removerMateria(cursor.getLong(0));
					cursor.moveToNext();
				}
				
				cursor.close();
			}
			if(!transacaoExterna) {
				mDb.setTransactionSuccessful();
			}
			resultado = true;
		} finally {
			if(!transacaoExterna) {
				mDb.endTransaction();
			}
		}
		
		return resultado;
	}
	
	/**
	 * Cria rela�ões entre as mat�rias e as turmas �s quais ela pertence.
	 * 
	 * @param materiaVO Informa�ões sobre a mat�ria (id da mat�ria e id(s) da(s) turma(s)).
	 * @return TRUE se bem sucedido; FALSE caso contr�rio.
	 */
	private boolean inserirMatricula(MateriaVO materiaVO) {
		boolean success = true;
		
		for(int i = 0; i < materiaVO.getIdTurmas().length; i++) {
			long idTurma = materiaVO.getIdTurmas()[i];
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID_MATERIA, materiaVO.getId());
			initialValues.put(COLUMN_ID_TURMA, idTurma);
			
			success &= mDb.insert(TABLE_MATRICULA, null, initialValues) > 0;
		}
		
		return success;
	}
	
	/**
	 * Remove todas as incidências de uma determinada mat�ria na tabela de rela�ões Mat�rias X Turmas.
	 * 
	 * @param id Id da mat�ria.
	 * @return TRUE.
	 */
	private boolean limparMatricula(long id) {
		mDb.execSQL("DELETE FROM " + TABLE_MATRICULA + " WHERE " + COLUMN_ID_MATERIA + " = ?", 
				new String[]{String.valueOf(id)});
		return true;
	}
	
	/**
	 * Retorna o registro das matrículas com o ID de aluno fornecido, se existir.
	 * 
	 * @param id
	 * @return uma lista vazia se o aluno n�o estiver matriculado em nehuma mat�ria.
	 *
	public List<MatriculaVO> consultarMatricula(long id) {
		ArrayList<MatriculaVO> listaMatriculas = new ArrayList<MatriculaVO>();
		MatriculaVO matricula = null;
		Cursor c = consultarMatricula(COLUMN_ID_ALUNO, String.valueOf(id));
		
		if(c != null) {
			c.moveToFirst();
			
			while(!c.isAfterLast()) {
				matricula = new MatriculaVO();
				matricula.setId(c.getInt(0));
				matricula.setIdAluno(c.getLong(1));
				matricula.setIdTurma(c.getLong(2));
				matricula.setIdMateria(c.getLong(3));
				
				matricula.setTurma(consultarTurma(matricula.getIdTurma()).getNome());
				matricula.setMateria(consultarMateria(matricula.getIdMateria()).getNome());
				
				listaMatriculas.add(matricula);
			}
			
			c.close();
		}
		
		return listaMatriculas;
		
	}*/
	
	/**
	 * M�todo privado para realizar consultas de matrículas.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 *
	private Cursor consultarMatricula(String key, String value) {
		
		return consultar(TABLE_MATERIA,
				new String[] {COLUMN_ID, COLUMN_ID_ALUNO, COLUMN_ID_TURMA, COLUMN_ID_MATERIA},
				key, value);
	}*/
	
	/**
	 * Cria um novo registro de matrícula na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param matriculaVO DAO com os dados da matrícula.
	 * @return rowID ou -1 se falhou.
	 *
	public long inserirMatricula(MatriculaVO matriculaVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes das mat�rias devem ser �nicos.
		if(consultarMateria(matriculaVO.getIdMateria()) == null) {
			ContentValues initialValues = new ContentValues();
			//initialValues.put(COLUMN_ID, matriculaVO.getId());
			initialValues.put(COLUMN_ID_ALUNO, matriculaVO.getIdAluno());
			initialValues.put(COLUMN_ID_TURMA, matriculaVO.getIdTurma());
			initialValues.put(COLUMN_ID_MATERIA, matriculaVO.getIdMateria());
			
			return mDb.insert(TABLE_MATRICULA, null, initialValues);
		} else {
			return -1;
		}
	}*/
	
	/**
	 * Remove a matrícula com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerMatricula(long id) {
		return remover(TABLE_MATRICULA, id);
	}
	
	/**
	 * Deleta as matriculas que possuam o valor <b>id</b> na coluna <b>column</b>.
	 * 
	 * @param column Coluna a ser considerada para definir as entradas que ser�o deletadas.
	 * @param id Valor a ser considerado nas entradas que ser�o deletadas.
	 * @return <b>TRUE</b>.
	 */
	public boolean removerMatricula(String column, long id) {
		mDb.execSQL("DELETE FROM " + TABLE_MATRICULA + " WHERE " + column + " = ?", new String[]{String.valueOf(id)});
		return true;
	}
	
	/**
	 * Atualiza o registro de nota com os dados fornecidos.
	 * 
	 * @param notaVO
	 * @return
	 */
	public boolean atualizarNota(NotaVO notaVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, notaVO.getId());
		updatedValues.put(COLUMN_ID_MATRICULA, notaVO.getIdMatricula());
		updatedValues.put(COLUMN_PERIODO, notaVO.getPeriodo());
		updatedValues.put(COLUMN_NOTA, notaVO.getNota());
			
		return mDb.update(TABLE_NOTA, updatedValues, COLUMN_ID + " = " + notaVO.getId(), null) > 0;
	}
	
	/**
	 * Consulta as notas de uma matricula em um determinado periodo.
	 * 
	 * @param matricula
	 * @param periodo
	 * @return
	 */
	public List<NotaVO> consultarNota(long matricula, int periodo) {
		List<NotaVO> notas = new ArrayList<NotaVO>();
		Cursor c = consultarNota(new String[]{COLUMN_ID_MATRICULA, COLUMN_PERIODO},
				new String[]{String.valueOf(matricula), String.valueOf(periodo)});
		
		
		if(c != null) {
			c.moveToFirst();
			
			while(!c.isAfterLast()) {
				NotaVO nota = new NotaVO();
				nota.setId(c.getInt(0));
				nota.setIdMatricula(c.getInt(1));
				nota.setPeriodo(c.getInt(2));
				nota.setNota(c.getFloat(3));
				notas.add(nota);
				c.moveToNext();
			}
		}
		return notas;
	}
	
	/**
	 * M�todo privado para realizar consultas de notas.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
//	@Deprecated
//	private Cursor consultarNota(String key, String value) {
//		return consultar(TABLE_NOTA, 
//				new String[] {COLUMN_ID, COLUMN_ID_MATRICULA, COLUMN_PERIODO, COLUMN_NOTA},
//				key, value);
//	}
	
	/**
	 * M�todo privado para realizar consultas de notas.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarNota(String[] key, String[] value) {
		return consultar(TABLE_NOTA, 
				new String[] {COLUMN_ID, COLUMN_ID_MATRICULA, COLUMN_PERIODO, COLUMN_NOTA},
				key, value);
	}

	/**
	 * Cria um novo registro de nota na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param notaVO DAO com os dados da nota.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirNota(NotaVO notaVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes dos alunos devem ser �nicos.
		if(consultarNota(notaVO.getIdMatricula(), notaVO.getPeriodo()).isEmpty()) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID_MATRICULA, notaVO.getIdMatricula());
			initialValues.put(COLUMN_PERIODO, notaVO.getPeriodo());
			initialValues.put(COLUMN_NOTA, notaVO.getNota());
			
			return mDb.insert(TABLE_NOTA, null, initialValues);
		} else {
			return -1;
		}
	}
	
	/**
	 * Remove a nota com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerNota(long id) {
		return remover(TABLE_NOTA, id);
	}
	
	/**
	 * Remove as notas que possuam o valor <b>value</b> na coluna <b>column</b>.
	 * 
	 * @param column
	 * @param id
	 * @return
	 */
	public boolean removerNota(String column, String value) {
		Cursor cursor = consultar(TABLE_NOTA, new String[]{COLUMN_ID}, column, String.valueOf(value));
		
		if(cursor != null) {
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()) {
				removerNota(cursor.getLong(0));
				cursor.moveToNext();
			}
		}
		
		return true;
	}
	
	/**
	 * Consulta todas as presen�as de uma matricula.
	 * 
	 * @param matricula
	 * @return
	 */
	public List<PresencaVO> consultarPresenca(long matricula) {
		List<PresencaVO> notas = new ArrayList<PresencaVO>();
		Cursor c = consultarPresenca(new String[]{COLUMN_ID_ALUNO},
				new String[]{String.valueOf(matricula)});
				
		if(c != null) {
			c.moveToFirst();
			
			while(!c.isAfterLast()) {
				PresencaVO presenca = new PresencaVO();
				presenca.setId(c.getInt(0));
				presenca.setData(Date.valueOf(c.getString(1)));
				presenca.setIdAluno(c.getInt(2));
				//presenca.setPresente(c.getInt(3) > 0 ? true : false);
				presenca.setFalta(c.getInt(3));
				notas.add(presenca);
				c.moveToNext();
			}
		}
		return notas;
	}
	
	/**
	 * Consulta as presen�as de uma matricula para uma determinada data.
	 * 
	 * @param idAluno
	 * @param data
	 * @return
	 */
	public List<PresencaVO> consultarPresenca(long idAluno, Date data) {
		List<PresencaVO> presencas = new ArrayList<PresencaVO>();
		Cursor c = consultarPresenca(new String[]{COLUMN_ID_ALUNO, COLUMN_DATA},
				new String[]{String.valueOf(idAluno), data.toString()});
				
		if(c != null) {
			c.moveToFirst();
			
			while(!c.isAfterLast()) {
				PresencaVO presenca = new PresencaVO();
				presenca.setId(c.getInt(0));
				presenca.setData(Date.valueOf(c.getString(1)));
				presenca.setIdAluno(c.getInt(2));
				//presenca.setPresente(c.getInt(3) > 0 ? true : false);
				presenca.setFalta(c.getInt(3));
				presencas.add(presenca);
				c.moveToNext();
			}
		}
		return presencas;
	}
	
	/**
	 * M�todo privado para realizar consultas de presen�as.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarPresenca(String[] key, String[] value) {
		return consultar(TABLE_PRESENCA, 
				new String[] {COLUMN_ID, COLUMN_DATA, COLUMN_ID_ALUNO, COLUMN_FALTA},
				key, value);
	}
	
	/**
	 * Cria um novo registro de presen�a na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param presencaoVO DAO com os dados da presen�a.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirPresenca(PresencaVO presencaoVO) {
		// Apesar de ID ser a verdadeira chave do registro, as datas da presen�a nas mat�rias devem ser �nicas.
		if(consultarPresenca(presencaoVO.getIdAluno(),presencaoVO.getData()).isEmpty()) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID_ALUNO, presencaoVO.getIdAluno());
			initialValues.put(COLUMN_DATA, presencaoVO.getData().toString());
			initialValues.put(COLUMN_FALTA, presencaoVO.getFalta());
			
			return mDb.insert(TABLE_PRESENCA, null, initialValues);
		} else {
			return -1;
		}
	}
	
	/**
	 * Remove a presenca com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerPresenca(long id) {
		return remover(TABLE_PRESENCA, id);
	}
	
	/**
	 * Remove as presen�as que possuam o valor <b>value</b> na coluna <b>column</b>.
	 * 
	 * @param column
	 * @param id
	 * @return
	 */
	public boolean removerPresenca(String column, String value) {
		Cursor cursor = consultar(TABLE_PRESENCA, new String[]{COLUMN_ID}, column, String.valueOf(value));
		
		if(cursor != null) {
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()) {
				removerNota(cursor.getLong(0));
				cursor.moveToNext();
			}
		}
		
		return true;
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
	 * @return null se n�o encontrar o professor especificado.
	 */
	public ProfessorVO consultarProfessor(long id) {
		ProfessorVO professor = null;
		Cursor c = consultarProfessor(COLUMN_ID, String.valueOf(id));
		
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
	 * @param isLogin <b>True</b> indica que o valor passado como chave � um login. <b>False</b> indica que � um nome.
	 * @return null se n�o encontrar o professor especificado.
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
	 * M�todo privado para realizar consultas de professores.
	 * 
	 * @param key Nome da coluna usada como parâmetro na consulta.
	 * @param value Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private Cursor consultarProfessor(String key, String value) {
		return consultar(TABLE_PROFESSOR, 
				new String[] {COLUMN_ID, COLUMN_LOGIN, COLUMN_NOME, COLUMN_SENHA},
				key, value);
	}
	
	/**
	 * Cria um novo registro de professor na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param professorVO DAO com os dados do professor.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirProfessor(ProfessorVO professorVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes e logins dos professores devem ser �nicos.
		if(consultarProfessor(professorVO.getNome(), false) == null &&
				consultarProfessor(professorVO.getLogin(), true) == null) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_LOGIN, professorVO.getLogin());
			initialValues.put(COLUMN_NOME, professorVO.getNome());
			initialValues.put(COLUMN_SENHA, professorVO.getSenha());
			
			return mDb.insert(TABLE_PROFESSOR, null, initialValues);
		} else {
			return -1;
		}
	}
	
	/**
	 * Remove o professor com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerProfessor(long id) {
		boolean resultado = false;
		mDb.beginTransaction();
		try {
			removerMateria(COLUMN_ID_PROFESSOR, id);
			remover(TABLE_PROFESSOR, id);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}
		return resultado;
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
	 * @return null se n�o encontrar a turma especificada.
	 */
	public TurmaVO consultarTurma(long id) {
		TurmaVO turma = null;
		Cursor c = consultarTurma(COLUMN_ID, String.valueOf(id));
		
		if(c != null) {
			c.moveToFirst();
			
			if(!c.isAfterLast()) {
				turma = new TurmaVO();
				turma.setId(c.getLong(0));
				turma.setNome(c.getString(1));
				turma.setDescricao(c.getString(2));
				
				turma.setIdMaterias(consultarMateriasPorTurma(turma.getId()));
			}
			
			c.close();
		}
		
		return turma;
	}	
	
	/**
	 * Retorna os dados da turma indicada.
	 * 
	 * @param nome
	 * @return null se n�o encontrar a turma especificada.
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
				
				turma.setIdMaterias(consultarMateriasPorTurma(turma.getId()));
				
				c.close();
			}
		}
		
		return turma;
	}
	
	/**
	 * M�todo privado para realizar consultas de turmas.
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
	 * Cria um novo registro de turma na tabela. Se o registro for incluído com
	 * sucesso, o RowID ser� retornado. Em caso de erro, retorna -1.
	 * 
	 * @param turmaVO DAO com os dados da turma.
	 * @return rowID ou -1 se falhou.
	 */
	public long inserirTurma(TurmaVO turmaVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes das turmas devem ser �nicos.
		if(consultarTurma(turmaVO.getNome()) == null) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_NOME, turmaVO.getNome());
			initialValues.put(COLUMN_DESCRICAO, turmaVO.getDescricao());
			
			return mDb.insert(TABLE_TURMA, null, initialValues);
		} else {
			return -1;
		}
	}
	
	/**
	 * Remove a turma com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public boolean removerTurma(long id) {
		boolean resultado = false;
		mDb.beginTransaction();
		
		try {
			//removerTurmaMateria(id);
			removerMatricula(id);
			removerAluno(COLUMN_ID_TURMA, id);
			remover(TABLE_TURMA, id);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}
		return resultado;
	}
	
	/**
	 * M�todo gen�rico para efetuar consultas �s tabelas, utilizando a chave fornecida.
	 * 
	 * @param table Tabela onde ser� executada a busca.
	 * @param colunas Colunas que devem ser consideradas no retorno da busca.
	 * @param key Coluna que dever� conter a palavra-chave definida como <b>value</b>.
	 * @param value Palavra-chave da busca.
	 * @return Cursor na primeira posi��o, caso algum dado tenha sido encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	private Cursor consultar(String table, String[] colunas, String key, String value) {
		return consultar(table, colunas, new String[]{key}, new String[]{value});

	}
	
	/**
	 * M�todo gen�rico para efetuar consultas �s tabelas, utilizando a chave fornecida.
	 * 
	 * @param table Tabela onde ser� executada a busca.
	 * @param colunas Colunas que devem ser consideradas no retorno da busca.
	 * @param key Coluna que dever� conter a palavra-chave definida como <b>value</b>.
	 * @param value Palavra-chave da busca.
	 * @return Cursor na primeira posi��o, caso algum dado tenha sido encontrado. Se nenhum dado foi encontrado, retorna <b>null</b>.
	 */
	private Cursor consultar(String table, String[] colunas, String[] key, String[] value) {
		String condicao = new String();
		int parada = key.length < value.length ? key.length : value.length;
		
		for(int i = 0; i < parada; i++) { 
			condicao += key[i] + " = '" + value[i] + "' AND ";
		}
		
		// O loop-for acima deixar� um " AND " sobrando, ent�o deve-se remove-lo.
		condicao = condicao.substring(0, condicao.length() - 5);
		
		Cursor mCursor = 
			mDb.query(false, table, colunas, condicao, null, null, null, null, null);
		
		if(mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;

	}
	
	/**
	 * Retorna um Cursor para todos os registros encontrados para a tabela definida.
	 * 
	 * @param tabela Tabela de onde ser�o consultados os registros.
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
	 * @param tabela Tabela de onde ser�o consultados os registros.
	 * @param coluna Coluna a ser exibida.
	 * @return Lista com os valores encontrados. Retorna uma lista vazia se n�o encontrar nenhum valor v�lido.
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
	
	/**
	 * M�todo gen�rico para remover entradas nas tabelas baseadas no id fornecido.
	 * 
	 * @param table Tabela onde ser� executada a dele��o.
	 * @param id Chave da linha a ser deletada
	 * @return <b>True</b> se a opera��o foi bem-sucedida; <b>false</b> em caso de erro.
	 */
	private boolean remover(String table, long id) {
		return mDb.delete(table, COLUMN_ID + " = " + id, null) > 0;
	}

	/**
	 * Cadastra as mat�rias padr�o para a turma definida.
	 * 
	 * @param id C�digo de identifica��o da turma.
	 */
	public void cadastrarMateriasPadrao(long id) {
		Cursor c = consultarMateria(COLUMN_PADRAO, "S");
		
		if(c != null) {
			c.moveToFirst();
			
			while(!c.isAfterLast()) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_ID_MATERIA, c.getLong(0));
				values.put(COLUMN_ID_TURMA, id);
				
				mDb.insert(TABLE_MATRICULA, null, values);
				c.moveToNext();
			}
		}	
	}
}
