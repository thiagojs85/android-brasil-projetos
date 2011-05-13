package org.android.brasil.projetos.escolarmobile.dao;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NotaVO extends VOBasico {

	private long id;
	private long idMatricula;
	private int periodo;
	private float nota;

	public static final String TABLE_NOTA = "Nota";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOTA = "nota";
	public static final String COLUMN_PERIODO = "periodo";
	public static final String COLUMN_ID_TURMA_MATERIA = "id_turma_materia";

	private static final String CREATE_NOTA = "CREATE TABLE " + TABLE_NOTA
			+ " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_ID_TURMA_MATERIA + " INTEGER NOT NULL, " + COLUMN_PERIODO
			+ " INTEGER NOT NULL, " + COLUMN_NOTA + " FLOAT NOT NULL);";

	public NotaVO(Context ctx) {
		super(ctx);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdMatricula() {
		return idMatricula;
	}

	public void setIdMatricula(long idMatricula) {
		this.idMatricula = idMatricula;
	}

	public float getNota() {
		return nota;
	}

	public void setNota(float nota) {
		this.nota = nota;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public static String createTableString() {
		return CREATE_NOTA;
	}

	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_NOTA;
	}

	/**
	 * Atualiza o registro de nota com os dados fornecidos.
	 * 
	 * @param notaVO
	 * @return
	 */
	public static boolean atualizarNota(NotaVO notaVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, notaVO.getId());
		updatedValues.put(COLUMN_ID_TURMA_MATERIA, notaVO.getIdMatricula());
		updatedValues.put(COLUMN_PERIODO, notaVO.getPeriodo());
		updatedValues.put(COLUMN_NOTA, notaVO.getNota());

		return mDb.update(TABLE_NOTA, updatedValues,
				COLUMN_ID + " = " + notaVO.getId(), null) > 0;
	}

	/**
	 * Consulta as notas de uma turma_materia em um determinado periodo.
	 * 
	 * @param idTurmaMateria
	 * @param periodo
	 * @return
	 */
	public static List<NotaVO> consultarNota(long idTurmaMateria, int periodo) {
		List<NotaVO> notas = new ArrayList<NotaVO>();
		Cursor c = consultarNota(new String[] { COLUMN_ID_TURMA_MATERIA,
				COLUMN_PERIODO }, new String[] { String.valueOf(idTurmaMateria),
				String.valueOf(periodo) });

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			while (!c.isAfterLast()) {
				NotaVO nota = new NotaVO(mCtx);
				nota.setId(c.getInt(0));
				nota.setIdMatricula(c.getInt(1));
				nota.setPeriodo(c.getInt(2));
				nota.setNota(c.getFloat(3));
				notas.add(nota);
				c.moveToNext();
			}
			c.close();
		}
		return notas;
	}

	/**
	 * Método privado para realizar consultas de notas.
	 * 
	 * @param key
	 *            Nome da coluna usada como parâmetro na consulta.
	 * @param value
	 *            Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private static Cursor consultarNota(String[] key, String[] value) {
		return consultar(TABLE_NOTA, new String[] { COLUMN_ID,
				COLUMN_ID_TURMA_MATERIA, COLUMN_PERIODO, COLUMN_NOTA }, key,
				value);
	}

	/**
	 * Cria um novo registro de nota na tabela. Se o registro for incluído com
	 * sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param notaVO
	 *            DAO com os dados da nota.
	 * @return rowID ou -1 se falhou.
	 */
	public static long inserirNota(NotaVO notaVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes dos alunos
		// devem ser únicos.
		if (consultarNota(notaVO.getIdMatricula(), notaVO.getPeriodo())
				.isEmpty()) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID_TURMA_MATERIA, notaVO.getIdMatricula());
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
	public static boolean removerNota(long id) {
		return remover(TABLE_NOTA, NotaVO.COLUMN_ID, id);
	}

	public static void createDummyData(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}
	public static Cursor consultarTodos(String[] colunas) {
		return consultarTodos(TABLE_NOTA, colunas);
	}
}
