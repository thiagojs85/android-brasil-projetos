package org.android.brasil.projetos.escolarmobile.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author julio
 * 
 */
public class PresencaVO extends VOBasico {

	private long id;
	private Date data;
	private long idAluno;
	private int falta;
	private int periodo;
	private long idTurmaMateria;
	public static final String COLUMN_ID = "_id";
	public static final String TABLE_PRESENCA = "Presenca";
	public static final String COLUMN_ID_ALUNO = "id_aluno";
	public static final String COLUMN_FALTA = "falta";
	public static final String COLUMN_DATA = "data";
	public static final String COLUMN_PERIODO = "periodo";
	public static final String COLUMN_ID_TURMA_MATERIA = "id_turma_materia";

	public static final short SIM = 1;
	public static final short NAO = 0;

	private static final String CREATE_PRESENCA = "CREATE TABLE " + TABLE_PRESENCA + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATA + " DATE NOT NULL, "
			+ COLUMN_ID_ALUNO + " INTEGER NOT NULL, " + COLUMN_ID_TURMA_MATERIA
			+ " INTEGER NOT NULL, " + COLUMN_PERIODO + " INTEGER NOT NULL," + COLUMN_FALTA
			+ " INTEGER NOT NULL);";

	public PresencaVO(Context ctx) {
		super(ctx);
	}

	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public long getIdAluno() {
		return idAluno;
	}

	public void setIdAluno(long idAluno) {
		this.idAluno = idAluno;
	}

	public int getFalta() {
		return falta;
	}

	public void setFaltas(int falta) {
		this.falta = falta;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public long getIdTurmaMateria() {
		return idTurmaMateria;
	}

	public void setIdTurmaMateria(long idTurmaMateria) {
		this.idTurmaMateria = idTurmaMateria;
	}

	public static String createTableString() {
		return CREATE_PRESENCA;
	}

	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_PRESENCA;
	}

	/**
	 * Consulta todas as presenças de uma turma_materia.
	 * 
	 * @param idAluno
	 * @return
	 */
	public static List<PresencaVO> consultarPresenca(long idAluno) {
		List<PresencaVO> notas = new ArrayList<PresencaVO>();
		Cursor c = consultarPresenca(new String[] { COLUMN_ID_ALUNO },
				new String[] { String.valueOf(idAluno) });

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			while (!c.isAfterLast()) {
				PresencaVO presenca = PresencaVO.deCursorParaPresencaVO(c);
				notas.add(presenca);
				c.moveToNext();
			}
			c.close();
		}

		return notas;
	}

	/**
	 * Consulta todas as presenças de uma turma_materia.
	 * 
	 * @param idTurmaMateria
	 * @return
	 */
	public static Cursor consultarPresenca(long idTurmaMateria, long periodo) {
		Cursor c = consultarPresenca(new String[] { COLUMN_ID_ALUNO },
				new String[] { String.valueOf(idTurmaMateria) });

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
		}

		return c;
	}
	
	public static Cursor consultarPresencas(long idAluno, long idTurmaMateria, long periodo) {
		Cursor c = consultarPresenca(new String[] { COLUMN_ID_ALUNO,COLUMN_ID_TURMA_MATERIA,COLUMN_PERIODO },
				new String[] { String.valueOf(idAluno),String.valueOf(idTurmaMateria),String.valueOf(periodo) });

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
		}

		return c;
	}

	/**
	 * Consulta as presenças de uma turma_materia para uma determinada data.
	 * 
	 * @param idAluno
	 * @param data
	 * @return
	 */
	public static List<PresencaVO> consultarPresenca(long idAluno, Date data) {
		List<PresencaVO> presencas = new ArrayList<PresencaVO>();
		Cursor c = consultarPresenca(new String[] { COLUMN_ID_ALUNO, COLUMN_DATA }, new String[] {
				String.valueOf(idAluno), data.toString() });

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			while (!c.isAfterLast()) {
				PresencaVO presenca = new PresencaVO(mCtx);
				presenca = PresencaVO.deCursorParaPresencaVO(c);
				presencas.add(presenca);
				c.moveToNext();
			}
			c.close();
		}

		return presencas;
	}

	private static PresencaVO deCursorParaPresencaVO(Cursor c) {
		PresencaVO presenca = new PresencaVO(mCtx);
		presenca.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
		presenca.setData(Date.valueOf(c.getString(c.getColumnIndex(COLUMN_DATA))));
		presenca.setIdAluno(c.getInt(c.getColumnIndex(COLUMN_ID_ALUNO)));
		presenca.setFaltas(c.getInt(c.getColumnIndex(COLUMN_FALTA)));
		presenca.setPeriodo(c.getInt(c.getColumnIndex(COLUMN_PERIODO)));
		presenca.setIdTurmaMateria(c.getInt(c.getColumnIndex(COLUMN_ID_TURMA_MATERIA)));

		return presenca;
	}

	/**
	 * Método privado para realizar consultas de presenças.
	 * 
	 * @param key
	 *            Nome da coluna usada como parâmetro na consulta.
	 * @param value
	 *            Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private static Cursor consultarPresenca(String[] key, String[] value) {
		return consultar(TABLE_PRESENCA, null, key, value);
	}

	/**
	 * Cria um novo registro de presença na tabela. Se o registro for incluído
	 * com sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param presencaoVO
	 *            DAO com os dados da presença.
	 * @return rowID ou -1 se falhou.
	 */
	public static long inserirPresenca(PresencaVO presencaoVO) {
		// Apesar de ID ser a verdadeira chave do registro, as datas da presença
		// nas matérias devem ser únicas.
		if (consultarPresenca(presencaoVO.getIdAluno(), presencaoVO.getData()).isEmpty()) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(COLUMN_ID_ALUNO, presencaoVO.getIdAluno());
			initialValues.put(COLUMN_DATA, presencaoVO.getData().toString());
			initialValues.put(COLUMN_FALTA, presencaoVO.getFalta());
			initialValues.put(COLUMN_ID_TURMA_MATERIA, presencaoVO.getIdTurmaMateria());
			initialValues.put(COLUMN_PERIODO, presencaoVO.getPeriodo());

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
	public static boolean removerPresenca(long id) {
		return remover(TABLE_PRESENCA, PresencaVO.COLUMN_ID, id);

	}

	public static void createDummyData(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

}
