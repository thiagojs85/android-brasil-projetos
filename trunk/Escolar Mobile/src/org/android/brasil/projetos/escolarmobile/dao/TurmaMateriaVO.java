package org.android.brasil.projetos.escolarmobile.dao;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TurmaMateriaVO extends VOBasico {

	private long id;
	private long idTurma;

	public static final String COLUMN_ID_MATERIA = "id_materia";
	public static final String COLUMN_ID = "_id";
	public static final String TABLE_TURMA_MATERIA = "turma_materia";
	public static final String COLUMN_ID_TURMA = "id_turma";

	private static final String CREATE_TURMA_MATERIA = "CREATE TABLE "
			+ TABLE_TURMA_MATERIA + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_ID_MATERIA
			+ " INTEGER NOT NULL, " + COLUMN_ID_TURMA + " INTEGER NOT NULL);";

	public TurmaMateriaVO(Context ctx) {
		super(ctx);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdTurma() {
		return idTurma;
	}

	public void setIdTurma(long idTurma) {
		this.idTurma = idTurma;
	}

	public static String createTableString() {
		return CREATE_TURMA_MATERIA;
	}

	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_TURMA_MATERIA;
	}

	/**
	 * Devolve um Cursor com todas as matérias de uma determinada turma.
	 * 
	 * @param idTurma
	 *            Código de identificação da turma.
	 * @return Cursor apontando para o primeiro elemento. Se não houver nenhuma
	 *         matéria, retorna NULL.
	 */
	public static Cursor acessarMateriasPorTurma(long idTurma) {
		String sql = "SELECT m.* FROM " + TABLE_TURMA_MATERIA + " tm,"
				+ MateriaVO.TABLE_MATERIA + " m WHERE tm." + COLUMN_ID_TURMA
				+ " = ? AND tm." + COLUMN_ID_MATERIA + " = m."
				+ MateriaVO.COLUMN_ID;
		Cursor c = mDb.rawQuery(sql, new String[] { String.valueOf(idTurma) });

		if (c != null) {
			c.moveToFirst();

			if (c.isAfterLast()) {
				return null;
			} else {
				return c;
			}
		} else {
			return null;
		}

	}

	/**
	 * Devolve um Cursor com todas as matérias de uma determinada turma.
	 * 
	 * @param idTurma
	 *            Código de identificação da turma.
	 * @return Cursor apontando para o primeiro elemento. Se não houver nenhuma
	 *         matéria, retorna NULL.
	 */
	public static Cursor getMateriasCursor(long idTurma) {

		String sql = "SELECT * FROM " + TABLE_TURMA_MATERIA + " WHERE "
				+ COLUMN_ID_TURMA + " = ?";
		Cursor c = mDb.rawQuery(sql, new String[] { String.valueOf(idTurma) });

		if (c != null) {
			c.moveToFirst();

			if (c.isAfterLast()) {
				return null;
			} else {
				return c;
			}
		} else {
			return null;
		}
	}

	/**
	 * Cria relações entre as matérias e as turmas às quais ela pertence.
	 * 
	 * @param materiaVO
	 *            Informações sobre a matéria (id da matéria e id(s) da(s)
	 *            turma(s)).
	 * @return TRUE se bem sucedido; FALSE caso contrário.
	 */
	public static boolean inserirRelacionamento(long idTurma, long idMateria) {
		boolean success = true;
		ContentValues initialValues = new ContentValues();
		initialValues.put(COLUMN_ID_MATERIA, idMateria);
		initialValues.put(COLUMN_ID_TURMA, idTurma);

		success &= mDb.insert(TABLE_TURMA_MATERIA, null, initialValues) > 0;
		return success;
	}

	/**
	 * Remove todas as incidências de uma determinada matéria na tabela de
	 * relações matérias X Turmas.
	 * 
	 * @param id
	 *            Id da matéria.
	 * @return TRUE.
	 */
	public static boolean removerMateriaDeRelacionamento(long idTurma, long idMateria) {
		mDb.execSQL(
				"DELETE FROM " + TABLE_TURMA_MATERIA + " WHERE "
						+ COLUMN_ID_MATERIA + " = ? AND " + COLUMN_ID_TURMA
						+ " = ?",
				new String[] { String.valueOf(idMateria),
						String.valueOf(idTurma) });
		return true;
	}

	
	/**
	 * Remove todas as incidências de uma determinada matéria na tabela de
	 * relações matérias X Turmas.
	 * 
	 * @param id
	 *            Id da matéria.
	 * @return TRUE.
	 */
	public static boolean removerTodasMateriasDeTurma(long idTurma) {
		mDb.execSQL(
				"DELETE FROM " + TABLE_TURMA_MATERIA + " WHERE "
						+ COLUMN_ID_TURMA
						+ " = ?",
				new String[] {String.valueOf(idTurma) });
		return true;
	}

	
	/**
	 * Consulta todas as matérias da turma dada.
	 * 
	 * @param idTurma
	 *            Código de ID da turma.
	 * @return vetor de ids das matérias encontradas.
	 */
	public static long[] consultarIdsDasMateriasDeTurma(long idTurma) {
		ArrayList<String> resultados = new ArrayList<String>();

		Cursor c = consultar(TABLE_TURMA_MATERIA,
				new String[] { COLUMN_ID_MATERIA }, COLUMN_ID_TURMA,
				String.valueOf(idTurma));

		if (c != null) {
			c.moveToFirst();

			while (!c.isAfterLast()) {
				resultados.add(String.valueOf(c.getLong(0)));
				c.moveToNext();
			}

			c.close();

			if (!resultados.isEmpty()) {
				long[] ids = new long[resultados.size()];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = Long.parseLong(resultados.get(i));
				}
				return ids;
			}
		}

		// Se o programa chegar a este ponto, é porque não foi encontrada
		// nenhuma matéria.
		return null;
	}

	/**
	 * Cadastra as matérias padrão para a turma definida.
	 * 
	 * @param idTurma
	 *            Código de identificação da turma.
	 */
	public static void adicionarMateriasPadrao(long idTurma,
			List<Integer> idMaterias) {
		if (idMaterias == null) {
			return;
		}
		for (int i = 0; i < idMaterias.size(); ++i) {
			if (!TurmaMateriaVO
					.existeRelacionamento(idTurma, idMaterias.get(i))) {
				TurmaMateriaVO
						.inserirRelacionamento(idTurma, idMaterias.get(i));
			}
		}

	}

	public static void createDummyData(SQLiteDatabase db) {
		String sql = "INSERT INTO " + TurmaMateriaVO.TABLE_TURMA_MATERIA + "("
				+ TurmaMateriaVO.COLUMN_ID_MATERIA + ", "
				+ TurmaMateriaVO.COLUMN_ID_TURMA + ") VALUES((SELECT "
				+ MateriaVO.COLUMN_ID + " FROM " + MateriaVO.TABLE_MATERIA
				+ " WHERE " + MateriaVO.COLUMN_HORAS + " = ?), " + "(SELECT "
				+ TurmaVO.COLUMN_ID + " FROM " + TurmaVO.TABLE_TURMA
				+ " WHERE " + TurmaVO.COLUMN_NOME + " = ?))";
		db.execSQL(sql, new String[] { "40", "1a. A" });
		db.execSQL(sql, new String[] { "44", "1a. A" });
		db.execSQL(sql, new String[] { "100", "1a. A" });
		db.execSQL(sql, new String[] { "85", "1a. A" });
		db.execSQL(sql, new String[] { "10", "1a. A" });
		db.execSQL(sql, new String[] { "5", "1a. A" });
		db.execSQL(sql, new String[] { "44", "2a. A" });
		db.execSQL(sql, new String[] { "85", "2a. A" });

	}

	public static boolean estaEmMaisRelacionamentos(long idMateria) {

		Cursor c = consultar(TABLE_TURMA_MATERIA, new String[] {
				COLUMN_ID_MATERIA, COLUMN_ID_TURMA }, COLUMN_ID_MATERIA,
				String.valueOf(idMateria));

		if (c != null) {
			if (c.getCount() > 0) {
				c.close();
				return true;
			}
			c.close();
		}

		return false;
	}

	public static boolean existeRelacionamento(long idTurma, long idMateria) {

		Cursor c = consultar(
				TABLE_TURMA_MATERIA,
				new String[] {},
				new String[] { COLUMN_ID_MATERIA, COLUMN_ID_TURMA },
				new String[] { String.valueOf(idMateria),
						String.valueOf(idTurma) });

		if (c != null) {
			if (c.getCount() > 0) {
				c.close();
				return true;
			}
			c.close();
		}

		return false;
	}
}
