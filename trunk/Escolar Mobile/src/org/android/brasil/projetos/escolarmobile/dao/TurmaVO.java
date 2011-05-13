package org.android.brasil.projetos.escolarmobile.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Esta classe é apenas um acesso para os dados de uma turma.
 * 
 * @author Otavio
 * 
 */
public class TurmaVO extends VOBasico {

	private String nome;
	private String descricao;
	private long id;

	public static final String COLUMN_ID = "_id";
	public static final String TABLE_TURMA = "Turma";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_DESCRICAO = "descricao";
	private static final String CREATE_TURMA = "CREATE TABLE " + TABLE_TURMA
			+ " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NOME + " TEXT NOT NULL, " + COLUMN_DESCRICAO + " TEXT);";

	public TurmaVO(Context ctx) {
		super(ctx);
		id = 0;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public long getId() {
		return id;
	}

	public void setId(long l) {
		this.id = l;
	}

	public static String createTableString() {
		return CREATE_TURMA;
	}

	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_TURMA;
	}

	/**
	 * Atualiza o registro de turma com os dados fornecidos.
	 * 
	 * @param turmaVO
	 * @return
	 */
	public static boolean atualizarTurma(TurmaVO turmaVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, turmaVO.getId());
		updatedValues.put(COLUMN_NOME, turmaVO.getNome());
		updatedValues.put(COLUMN_DESCRICAO, turmaVO.getDescricao());

		return mDb.update(TABLE_TURMA, updatedValues, COLUMN_ID + " = "
				+ turmaVO.getId(), null) > 0;
	}

	/**
	 * Retorna o registro da turma com o ID fornecido, se existir.
	 * 
	 * @param idTurma
	 * @return null se não encontrar a turma especificada.
	 */
	public static TurmaVO consultarTurmaPorId(long idTurma) {
		TurmaVO turmaVO = new TurmaVO(mCtx);
		Cursor c = consultarTurma(COLUMN_ID, String.valueOf(idTurma));

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			if (!c.isAfterLast()) {
				turmaVO.setId(c.getLong(0));
				turmaVO.setNome(c.getString(1));
				turmaVO.setDescricao(c.getString(2));
			}

			c.close();
			return turmaVO;
		}

		return null;
	}

	/**
	 * Retorna os dados da turma indicada.
	 * 
	 * @param nome
	 * @return null se não encontrar a turma especificada.
	 */
	public static TurmaVO consultarTurmaPorNome(TurmaVO turmaVO) {
		Cursor c = consultarTurma(COLUMN_NOME, turmaVO.getNome());

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			if (!c.isAfterLast()) {
				turmaVO.setId(c.getLong(0));
				turmaVO.setNome(c.getString(1));
				turmaVO.setDescricao(c.getString(2));
				// setIdMaterias(consultarMateriasPorTurma(turma.getId()));

				c.close();
				return turmaVO;
			}
		}

		return null;
	}

	/**
	 * Método privado para realizar consultas de turmas.
	 * 
	 * @param key
	 *            Nome da coluna usada como parâmetro na consulta.
	 * @param value
	 *            Valor a ser procurado na coluna especificada.
	 * @return
	 */
	protected static Cursor consultarTurma(String key, String value) {
		return consultar(TABLE_TURMA, new String[] { COLUMN_ID, COLUMN_NOME,
				COLUMN_DESCRICAO }, key, value);
	}

	/**
	 * Cria um novo registro de turma na tabela. Se o registro for incluído com
	 * sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param turmaVO
	 *            DAO com os dados da turma.
	 * @return rowID ou -1 se falhou.
	 */
	public static long inserirTurma(TurmaVO turmaVO) {
		long resultado = -1;
		// Apesar de ID ser a verdadeira chave do registro, os nomes das turmas
		// devem ser unicos.
		if (consultarTurmaPorNome(turmaVO) == null) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_NOME, turmaVO.getNome());
			initialValues.put(COLUMN_DESCRICAO, turmaVO.getDescricao());
			mDb.beginTransaction();
			try {
				resultado = mDb.insert(TABLE_TURMA, null, initialValues);
				if (resultado > 0) {
					mDb.setTransactionSuccessful();
				}
			} finally {
				mDb.endTransaction();
			}
		}
		return resultado;
	}

	/**
	 * Remove a turma com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public static boolean removerTurma(long id) {
		boolean resultado = false;
		mDb.beginTransaction();

		try {
			if (remover(TABLE_TURMA, TurmaVO.COLUMN_ID, id)) {
				mDb.setTransactionSuccessful();
				resultado = true;
			}
		} finally {
			mDb.endTransaction();
		}
		return resultado;
	}

	public static Cursor consultarTodos(String[] colunas) {
		return consultarTodos(TABLE_TURMA, colunas);
	}

	public static void createDummyData(SQLiteDatabase db) {
		String sql = "INSERT INTO " + TurmaVO.TABLE_TURMA + "("
				+ TurmaVO.COLUMN_NOME + ", " + TurmaVO.COLUMN_DESCRICAO
				+ ") VALUES(?,?)";
		db.execSQL(sql, new String[] { "1a. A", "Primeiro Ano - Classe A" });
		db.execSQL(sql, new String[] { "1a. B", "Primeiro Ano - Classe B" });
		db.execSQL(sql, new String[] { "2a. A", "Segundo Ano - Classe A" });
		db.execSQL(sql, new String[] { "3a. A", "Terceiro Ano - Classe A" });

	}

}
