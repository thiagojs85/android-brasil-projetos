package org.android.brasil.projetos.escolarmobile.dao;

import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Equipe Boletim Escolar Mobile
 * Grupo Android Brasil - Projetos
 *
 */
public class AlunoVO extends VOBasico {

	private long id;
	private String registro;
	private String nome;
	private long idTurma;
	private String dataNascimento;

	public static final String TABLE_ALUNO = "Aluno";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_REGISTRO = "registro";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_DATA_NASCIMENTO = "dt_nascimento";
	public static final String COLUMN_ID_TURMA = "id_turma";

	private static final String CREATE_ALUNO = "CREATE TABLE " + TABLE_ALUNO
			+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_REGISTRO + " TEXT NOT NULL, " + COLUMN_NOME
			+ " TEXT NOT NULL, " + COLUMN_ID_TURMA + " INTEGER NOT NULL, "
			+ COLUMN_DATA_NASCIMENTO + " DATE);";

	public AlunoVO(Context ctx) {
		super(ctx);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getIdTurma() {
		return idTurma;
	}

	public void setIdTurma(long idTurma) {
		this.idTurma = idTurma;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * @return a string de criação da tabela Aluno.
	 */
	public static String createTableString() {
		return CREATE_ALUNO;
	}

	/**
	 * @return a string de remoção da tabela Aluno.
	 */
	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_ALUNO;
	}

	/**
	 * Recupera todos os alunos matriculados em uma turma.
	 * 
	 * @param idTurma
	 *            Código de identificação da matéria na tabela.
	 * @return Cursor apontando para o primeiro elemento encontrado. NULL se não
	 *         houver nenhuma entrada.
	 */
	public static Cursor acessarAlunosPorTurma(long idTurma) {
		String sql = "SELECT * FROM " + TABLE_ALUNO + "  WHERE "
				+ COLUMN_ID_TURMA + " = ? ";
		Cursor c = mDb.rawQuery(sql, new String[] { String.valueOf(idTurma) });

		if (c != null && c.getCount() > 0) {
			return c;
		} else {
			return null;
		}
	}

	/**
	 * @param nome do aluno
	 * @param idTurma turma do aluno
	 * @return id do aluno, ou -1 caso não exista um aluno com o nome passado na turma passada.
	 */
	public static long getIdAlunoPorNomeETurma(String nome,
			long idTurma) {
		 AlunoVO alunoVO = consultarAluno(nome);
		 if(alunoVO != null && alunoVO.getIdTurma() == idTurma){
			 return alunoVO.getId();
		 }
		return -1;
	}
	public static Cursor consultarTodos(String[] colunas) {
		return consultarTodos(TABLE_ALUNO, colunas);
	}

	/**
	 * Recupera os nomes de todos os alunos matriculados em uma Turma.
	 * 
	 * @param idTurma
	 *            Código de identificação da matéria na tabela.
	 * @return Uma lista com o nome de todos os alunos da turma, NULL se não houver alunos.
	 */
	public static ArrayList<String> getNomeDeAlunosPorTurma(long idTurma) {
		Cursor c = AlunoVO.acessarAlunosPorTurma(idTurma);

		ArrayList<String> resultado = null;
		if (c != null) {
			resultado = new ArrayList<String>();
			c.moveToFirst();
			while (!c.isAfterLast()) {
				resultado.add(c.getString((2)));
				c.moveToNext();
			}
			c.close();
		}
		return resultado;
	}

	
	/**
	 * Atualiza o registro de aluno com os dados fornecidos.
	 * 
	 * @param alunoVO é uma instância de AlunoVO
	 * @return Se conseguir atualizar o registro no banco retorna TRUE, caso contrário FALSE.
	 */
	public static boolean atualizarAluno(AlunoVO alunoVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, alunoVO.getId());
		updatedValues.put(COLUMN_REGISTRO, alunoVO.getRegistro());
		updatedValues.put(COLUMN_NOME, alunoVO.getNome());
		updatedValues.put(COLUMN_ID_TURMA, alunoVO.getIdTurma());
		updatedValues.put(COLUMN_DATA_NASCIMENTO, alunoVO.getDataNascimento());

		return mDb.update(TABLE_ALUNO, updatedValues, COLUMN_ID + " = "
				+ alunoVO.getId(), null) > 0;
	}


	/**
	 * Retorna o registro do aluno com o ID fornecido, se existir.
	 * 
	 * @param idAluno
	 * @return null se não encontrar o aluno especificado.
	 */
	public static AlunoVO consultarAluno(long idAluno) {
		AlunoVO alunoVO = new AlunoVO(mCtx);
		Cursor c = consultarAluno(COLUMN_ID, String.valueOf(idAluno));

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			alunoVO.setId(c.getInt(0));
			alunoVO.setRegistro(c.getString(1));
			alunoVO.setNome(c.getString(2));
			alunoVO.setIdTurma(c.getInt(3));
			alunoVO.setDataNascimento(c.getString(4));
			return alunoVO;
		}

		return null;
	}

	/**
	 * Retorna os dados do aluno indicado.
	 * 
	 * @param nome
	 *            Nome para ser procurado.
	 * @return null se não encontrar o aluno especificado.
	 */
	public static AlunoVO consultarAluno(String nome) {
		AlunoVO alunoVO = new AlunoVO(mCtx);
		Cursor c = consultarAluno(COLUMN_NOME, nome);

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			if (!c.isAfterLast()) {
				alunoVO.setId(c.getInt(0));
				alunoVO.setRegistro(c.getString(1));
				alunoVO.setNome(c.getString(2));
				alunoVO.setIdTurma(c.getInt(3));
				alunoVO.setDataNascimento(c.getString(4));
				return alunoVO;
			}
		}

		return null;
	}

	/**
	 * Método privado para realizar consultas de alunos.
	 * 
	 * @param key
	 *            Nome da coluna usada como parâmetro na consulta.
	 * @param value
	 *            Valor a ser procurado na coluna especificada.
	 * @return Retorna um Cursor para a pesquisa contendo a key passada.
	 */
	private static Cursor consultarAluno(String key, String value) {
		return consultar(TABLE_ALUNO, new String[] { COLUMN_ID,
				COLUMN_REGISTRO, COLUMN_NOME, COLUMN_ID_TURMA,
				COLUMN_DATA_NASCIMENTO }, key, value);
	}

	/**
	 * Cria um novo registro de aluno na tabela. Se o registro for incluído com
	 * sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param alunoVO
	 *            DAO com os dados do aluno.
	 * @return rowID ou -1 se falhou.
	 */
	public static long inserirAluno(AlunoVO alunoVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes dos alunos
		// devem ser únicos.
		if (consultarAluno(alunoVO.getNome()) == null) {
			ContentValues initialValues = new ContentValues();
			// initialValues.put(COLUMN_ID, alunoVO.getId());
			initialValues.put(COLUMN_REGISTRO, alunoVO.getRegistro());
			initialValues.put(COLUMN_NOME, alunoVO.getNome());
			initialValues.put(COLUMN_ID_TURMA, alunoVO.getIdTurma());
			initialValues.put(COLUMN_DATA_NASCIMENTO,
					alunoVO.getDataNascimento());

			return mDb.insert(TABLE_ALUNO, null, initialValues);
		} else {
			return -1;
		}
	}

	/**
	 * Remove o aluno com o id especificado.
	 * 
	 * @param id
	 * @return Se conseguir remover o registro do banco retorna TRUE, caso contrário FALSE.
	 */
	public static boolean removerAluno(long id) {
		boolean resultado = false;
		mDb.beginTransaction();

		try {
			remover(TABLE_ALUNO, AlunoVO.COLUMN_ID, id);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}

		return resultado;
	}

	/**
	 * @param db é uma instância aberta de um SQLiteDatabase
	 * Esté método é utilizado para popular o banco com Alunos "padrões" durante a instalação.
	 * Dê uma olhada em DbAdapter.java
	 */
	public static void createDummyData(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

}
