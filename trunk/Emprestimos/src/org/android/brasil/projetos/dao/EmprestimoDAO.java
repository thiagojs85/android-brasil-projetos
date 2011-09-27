package org.android.brasil.projetos.dao;

import org.android.brasil.projetos.model.Emprestimo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class EmprestimoDAO extends BasicoDAO {
	public static final String COLUNA_ID_EMPRESTIMO = "_id";
	public static final String COLUNA_ITEM = "item";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String COLUNA_STATUS = "status";
	public static final String COLUNA_ATIVAR_ALARME = "status_alarme";
	public static final String COLUNA_DATA_DEVOLUCAO = "devolucao";
	public static final String COLUNA_ID_CONTATO = "id_contato";
	public static final String COLUNA_ID_CATEGORIA = "id_categoria";

	public static final String TABELA_EMPRESTIMOS = "emprestimos";

	public static final String CRIAR_TABELA_EMPRESTIMOS = "create table " + TABELA_EMPRESTIMOS
			+ " ( " + COLUNA_ID_EMPRESTIMO + " integer primary key autoincrement, " + COLUNA_ITEM
			+ " text not null, " + COLUNA_DESCRICAO + " text not null," + COLUNA_STATUS
			+ " Integer not null, " + COLUNA_ATIVAR_ALARME + " Integer not null, "
			+ COLUNA_DATA_DEVOLUCAO + " Integer, " + COLUNA_ID_CONTATO + " Integer not null, "
			+ COLUNA_ID_CATEGORIA + " Integer not null,  FOREIGN KEY (" + COLUNA_ID_CATEGORIA
			+ " )  REFERENCES " + CategoriaDAO.TABELA_CATEGORIA + " (" + CategoriaDAO.COLUNA_ID
			+ " ));";

	public EmprestimoDAO(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	private static ContentValues deEmprestimoParaContentValues(Emprestimo emp) {
		ContentValues values = new ContentValues();
		//values.put(COLUNA_ID_EMPRESTIMO, emp.getIdEmprestimo());
		values.put(COLUNA_ITEM, emp.getItem());
		values.put(COLUNA_DESCRICAO, emp.getDescricao());
		values.put(COLUNA_DATA_DEVOLUCAO, emp.getData().getTime());
		values.put(COLUNA_STATUS, emp.getStatus());
		values.put(COLUNA_ATIVAR_ALARME, emp.getAtivarAlarme());
		values.put(COLUNA_ID_CONTATO, emp.getIdContato());
		values.put(COLUNA_ID_CATEGORIA, emp.getIdCategoria());
		return values;
	}

	public static long inserirEmprestimo(Emprestimo emp) {
		ContentValues values = deEmprestimoParaContentValues(emp);
		return inserir(TABELA_EMPRESTIMOS, values);
	}

	public static boolean deleteEmprestimo(long id) {

		return mDb.delete(TABELA_EMPRESTIMOS, COLUNA_ID_EMPRESTIMO + "=" + id, null) > 0;
	}

	public static Cursor consultarTodos() {

		return consultarTodos(TABELA_EMPRESTIMOS);
	}

	public static Cursor consultarEmprestimo(long idEmprestimo) throws SQLException {
		Cursor mCursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_EMPRESTIMO, String.valueOf(idEmprestimo));
		return mCursor;

	}

	public static long atualizarEmprestimo(Emprestimo emp) {
		ContentValues values = deEmprestimoParaContentValues(emp);
		return atualizar(TABELA_EMPRESTIMOS, values, new String[]{EmprestimoDAO.COLUNA_ID_EMPRESTIMO},new String[]{ String.valueOf(emp.getIdEmprestimo()) } );
	}
	
	public static Cursor consultarEmprestimoPorCategoria(long idCategoria) throws SQLException {
		Cursor mCursor = mDb.query(false, TABELA_EMPRESTIMOS, null , " id_categoria = " + idCategoria, null, null,null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}
}
