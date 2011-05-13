package org.android.brasil.projetos.escolarmobile.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfessorVO extends VOBasico {

	public ProfessorVO(Context ctx) {
		super(ctx);
	}

	private long id;
	private String login;
	private String nome;
	private String senha;

	public static final String COLUMN_ID = "_id";
	public static final String TABLE_PROFESSOR = "Professor";
	public static final String COLUMN_LOGIN = "login";
	public static final String COLUMN_NOME = "nome";
	public static final String COLUMN_SENHA = "senha";

	private static final String CREATE_PROFESSOR = "CREATE TABLE "
			+ TABLE_PROFESSOR + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_LOGIN
			+ " TEXT NOT NULL, " + COLUMN_NOME + " TEXT NOT NULL, "
			+ COLUMN_SENHA + " TEXT NOT NULL);";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public static String createTableString() {
		return CREATE_PROFESSOR;
	}

	public static String upgradeTableString() {
		return "DROP TABLE IF EXISTS " + TABLE_PROFESSOR;
	}

	/**
	 * Atualiza o registro de professor com os dados fornecidos.
	 * 
	 * @param professorVO
	 * @return
	 */

	public static boolean atualizarProfessor(ProfessorVO professorVO) {
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(COLUMN_ID, professorVO.getId());
		updatedValues.put(COLUMN_LOGIN, professorVO.getLogin());
		updatedValues.put(COLUMN_NOME, professorVO.getNome());
		updatedValues.put(COLUMN_SENHA, professorVO.getSenha());

		return mDb.update(TABLE_PROFESSOR, updatedValues, COLUMN_ID + " = "
				+ professorVO.getId(), null) > 0;
	}

	public static ProfessorVO consultarProfessorPorId(long id) {
		ProfessorVO professorVO = new ProfessorVO(mCtx);
		Cursor c = consultarProfessor(COLUMN_ID, String.valueOf(id));

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			professorVO.setId(c.getInt(0));
			professorVO.setLogin(c.getString(1));
			professorVO.setNome(c.getString(2));
			professorVO.setSenha(c.getString(3));
			c.close();
			return professorVO;
		}
		return null;
	}

	/**
	 * Retorna os dados do professor indicado.
	 * 
	 * @param key
	 *            Nome ou login para ser procurado.
	 * @param isLogin
	 *            <b>True</b> indica que o valor passado como chave é um login.
	 *            <b>False</b> indica que é um nome.
	 * @return null se não encontrar o professor especificado.
	 */
	public static ProfessorVO consultarProfessor(String key, boolean isLogin) {
		ProfessorVO professorVO = new ProfessorVO(mCtx);
		Cursor c = consultarProfessor(isLogin ? COLUMN_LOGIN : COLUMN_NOME, key);

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();

			if (!c.isAfterLast()) {
				professorVO.setId(c.getLong(0));
				professorVO.setLogin(c.getString(1));
				professorVO.setNome(c.getString(2));
				professorVO.setSenha(c.getString(3));
			}
			c.close();
			return professorVO;
		}
		return null;

	}

	/**
	 * Método privado para realizar consultas de professores.
	 * 
	 * @param key
	 *            Nome da coluna usada como parâmetro na consulta.
	 * @param value
	 *            Valor a ser procurado na coluna especificada.
	 * @return
	 */
	private static Cursor consultarProfessor(String key, String value) {
		return consultar(TABLE_PROFESSOR, new String[] { COLUMN_ID,
				COLUMN_LOGIN, COLUMN_NOME, COLUMN_SENHA }, key, value);
	}

	/**
	 * Cria um novo registro de professor na tabela. Se o registro for incluído
	 * com sucesso, o RowID será retornado. Em caso de erro, retorna -1.
	 * 
	 * @param professorVO
	 *            DAO com os dados do professor.
	 * @return rowID ou -1 se falhou.
	 */
	public static long inserirProfessor(ProfessorVO professorVO) {
		// Apesar de ID ser a verdadeira chave do registro, os nomes e logins
		// dos professores devem ser únicos.
		if (consultarProfessor(professorVO.getNome(), false) == null
				&& consultarProfessor(professorVO.getLogin(), true) == null) {
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
	 * @param idProfessor
	 * @return
	 */
	public static boolean removerProfessor(long idProfessor) {
		boolean resultado = false;
		mDb.beginTransaction();
		try {
			remover(TABLE_PROFESSOR, ProfessorVO.COLUMN_ID, idProfessor);
			mDb.setTransactionSuccessful();
			resultado = true;
		} finally {
			mDb.endTransaction();
		}
		return resultado;
	}

	public static Cursor consultarTodos(String[] colunas) {
		Cursor resultado = ProfessorVO.consultarTodos(TABLE_PROFESSOR, colunas);
		return resultado;
	}

	public static void createDummyData(SQLiteDatabase db) {
		String sql = "INSERT INTO " + ProfessorVO.TABLE_PROFESSOR + "("
				+ ProfessorVO.COLUMN_LOGIN + ", " + ProfessorVO.COLUMN_NOME
				+ ", " + ProfessorVO.COLUMN_SENHA + ") VALUES(?,?,?)";
		db.execSQL(sql, new String[] { "otavio", "Otavio K Rofatto", "123" });
		db.execSQL(sql, new String[] { "julio", "Julio Cotta", "123" });
		db.execSQL(sql, new String[] { "neto", "Neto", "123" });

	}

}
