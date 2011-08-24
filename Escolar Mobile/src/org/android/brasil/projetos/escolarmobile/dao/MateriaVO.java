package org.android.brasil.projetos.escolarmobile.dao;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MateriaVO extends VOBasico {

	/**
	 * 
	 */

	private long id;
	private long idProfessor;
	private String nome;
	private int horas;
	private String descricao;
	private boolean padrao;

	public static final String COLUMN_ID = "_id";
	public static final String TABLE_MATERIA = "Materia";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_HORAS = "horas";
	public static final String COLUMN_DESCRICAO = "descricao";
	public static final String COLUMN_PADRAO = "padrao";
	public static final String COLUMN_ID_PROFESSOR = "id_professor";

	public static final String SIM = "S";
	public static final String NAO = "N";

	private static final String CREATE_MATERIA = "CREATE TABLE "
			+ TABLE_MATERIA + " (" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ID_PROFESSOR
			+ " INTEGER NOT NULL, " + COLUMN_NOME + " TEXT NOT NULL, "
			+ COLUMN_HORAS + " INTEGER, " + COLUMN_DESCRICAO + " TEXT, "
			+ COLUMN_PADRAO + " TEXT NOT NULL);";

	public MateriaVO(Context ctx) {
		super(ctx);

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdProfessor() {
		return idProfessor;
	}

	public void setIdProfessor(long idProfessor) {
		this.idProfessor = idProfessor;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getHoras() {
		return horas;
	}

	public void setHoras(int horas) {
		this.horas = horas;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isPadrao() {
		return padrao;
	}

	public void setPadrao(boolean padrao) {
		this.padrao = padrao;
	}

	public static String createTableString() {
		return CREATE_MATERIA;
	}

	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_MATERIA;
	}

	/**
	 * Atualiza o registro de matéria com os dados fornecidos.
	 * 
	 * @return
	 */
	public static boolean atualizarMateria(MateriaVO materiaVO) {
		boolean success = false;
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, materiaVO.getId());
		updatedValues.put(COLUMN_ID_PROFESSOR, materiaVO.getIdProfessor());
		updatedValues.put(COLUMN_NOME, materiaVO.getNome());
		updatedValues.put(COLUMN_HORAS, materiaVO.getHoras());
		updatedValues.put(COLUMN_DESCRICAO, materiaVO.getDescricao());
		updatedValues.put(COLUMN_PADRAO, materiaVO.isPadrao() ? SIM : NAO);

		mDb.beginTransaction();
		try {
			success = mDb.update(TABLE_MATERIA, updatedValues, COLUMN_ID
					+ " = " + materiaVO.getId(), null) > 0;
			if (success) {
				mDb.setTransactionSuccessful();
			}
		} finally {
			mDb.endTransaction();
		}

		return success;
	}

	/**
	 * Retorna o registro da matéria com o ID fornecido, se existir.
	 * 
	 * @param idMateria
	 * @return null se não encontrar o aluno especificado.
	 */
	public static MateriaVO consultarMateriaPorId(long idMateria) {
		MateriaVO materiaVO = new MateriaVO(mCtx);
		Cursor c = consultarMateria(COLUMN_ID, String.valueOf(idMateria));

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			if (!c.isAfterLast()) {
				materiaVO.setId(c.getInt(0));
				materiaVO.setIdProfessor(c.getLong(1));
				materiaVO.setNome(c.getString(2));
				materiaVO.setHoras(c.getInt(3));
				materiaVO.setDescricao(c.getString(4));
				materiaVO.setPadrao(c.getString(5).equals(SIM));
			}

			c.close();
			return materiaVO;
		}
		return null;
	}

	/**
	 * Retorna os dados da matéria indicada.
	 * 
	 * @param nome
	 *            Nome para ser procurado.
	 * @return null se não encontrar o aluno especificado.
	 */
	public static MateriaVO consultarMateriaPorNome(String nome) {
		MateriaVO materiaVO = new MateriaVO(mCtx);
		Cursor c = consultarMateria(COLUMN_NOME, nome);

		if (c != null && (c.getCount() > 0)) {
			c.moveToFirst();

			if (!c.isAfterLast()) {
				materiaVO.setId(c.getInt(0));
				materiaVO.setIdProfessor(c.getLong(1));
				materiaVO.setNome(c.getString(2));
				materiaVO.setHoras(c.getInt(3));
				materiaVO.setDescricao(c.getString(4));
				materiaVO.setPadrao(c.getString(5).equals(SIM));
			}

			c.close();
			return materiaVO;
		}

		return null;
	}

	/**
	 * Método privado para realizar consultas de alunos.
	 * 
	 * @param coluna
	 *            Nome da coluna usada como parâmetro na consulta.
	 * @param valor
	 *            Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private static Cursor consultarMateria(String coluna, String valor) {
		return consultar(TABLE_MATERIA, new String[] { COLUMN_ID,
				COLUMN_ID_PROFESSOR, COLUMN_NOME, COLUMN_HORAS,
				COLUMN_DESCRICAO, COLUMN_PADRAO }, coluna, valor);
	}

	/**
	 * Cria um novo registro de matéria na tabela. Se o registro for incluído
	 * com sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param materiaVO
	 *            DAO com os dados da matéria.
	 * @return rowID ou -1 se falhou.
	 */
	public static long inserirMateria(MateriaVO materiaVO) {

		// Apesar de ID ser a verdadeira chave do registro, os nomes das
		// matérias devem ser unicos.
		// [julio] Porque tem que ter nomes diferentes??
		if (consultarMateriaPorNome(materiaVO.getNome()) == null) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID_PROFESSOR, materiaVO.getIdProfessor());
			initialValues.put(COLUMN_NOME, materiaVO.getNome());
			initialValues.put(COLUMN_HORAS, materiaVO.getHoras());
			initialValues.put(COLUMN_DESCRICAO, materiaVO.getDescricao());
			initialValues.put(COLUMN_PADRAO, materiaVO.isPadrao() ? SIM : NAO);

			mDb.beginTransaction();
			try {
				materiaVO.setId(mDb.insert(TABLE_MATERIA, null, initialValues));
				if (materiaVO.getId() > 0) {
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
	 * Remove a matéria com o id especificado.
	 * 
	 * @param id
	 * @return
	 */
	public static boolean removerMateria(long idMateria) {
		boolean resultado = false;

		mDb.beginTransaction();

		try {
			remover(TABLE_MATERIA, MateriaVO.COLUMN_ID, idMateria);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}

		return resultado;
	}

	public static boolean removerMateriasDoProfessor(long idProfessor) {

		return remover(TABLE_MATERIA, COLUMN_ID_PROFESSOR, idProfessor);
	}

	public static void createDummyData(SQLiteDatabase db) {
		String sql = "INSERT INTO "
				+ MateriaVO.TABLE_MATERIA
				+ "("
				+ MateriaVO.COLUMN_ID_PROFESSOR
				+ ", "
				+ MateriaVO.COLUMN_NOME
				+ ", "
				+ MateriaVO.COLUMN_HORAS
				+ ", "
				+ MateriaVO.COLUMN_DESCRICAO
				+ ", "
				+ MateriaVO.COLUMN_PADRAO
				+ ") VALUES((SELECT _id FROM Professor WHERE login = ?),?,?,?,?)";
		db.execSQL(sql, new String[] { "otavio", "Português (Matéria Teste)",
				"40", "Este é um teste", MateriaVO.NAO });
		db.execSQL(sql, new String[] { "otavio", "Matemética (Matéria Teste)",
				"44", "Este é um teste", MateriaVO.NAO });
		db.execSQL(sql, new String[] { "julio", "Histéria (Matéria Teste)",
				"100", "Este é um teste", MateriaVO.NAO });
		db.execSQL(sql, new String[] { "neto", "Física (Matéria Teste)", "85",
				"Este é um teste", MateriaVO.NAO });
		db.execSQL(sql, new String[] { "neto", "Química (Matéria Teste)", "10",
				"Este é um teste", MateriaVO.NAO });
		db.execSQL(sql, new String[] { "neto", "Biologia (Matéria Teste)", "5",
				"Este é um teste", MateriaVO.NAO });
		db.execSQL(sql, new String[] { "otavio",
				"Matéria TESTE (Matéria Teste)", "20", "Este é um padrão",
				MateriaVO.SIM });

	}

	public static List<Integer> getMateriasPadrão() {
		Cursor c = consultar(TABLE_MATERIA, new String[] { MateriaVO.COLUMN_ID,
				MateriaVO.COLUMN_PADRAO }, MateriaVO.COLUMN_PADRAO, SIM);
		List<Integer> ids = new ArrayList<Integer>();

		if (c != null && (c.getCount() > 0)) {
			c.moveToFirst();

			while (!c.isAfterLast()) {
				ids.add(c.getInt(0));
				c.moveToNext();
			}

			c.close();
			return ids;
		}

		return null;

	}
	public static Cursor consultarTodos(String[] colunas) {
		return consultarTodos(TABLE_MATERIA, colunas);
	}
}
