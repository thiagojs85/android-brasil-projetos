package org.android.brasil.projetos.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.android.brasil.projetos.dao.util.TableBuilder;
import org.android.brasil.projetos.model.Emprestimo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.content.Loader;

public class EmprestimoDAO extends BasicoDAO {
	public static final String COLUNA_ID_EMPRESTIMO = "_id";
	public static final String COLUNA_ITEM = "item";
	public static final String COLUNA_DESCRICAO = "descricao";
	public static final String COLUNA_STATUS = "status";
	public static final String COLUNA_ATIVAR_ALARME = "status_alarme";
	public static final String COLUNA_DATA_DEVOLUCAO = "devolucao";
	public static final String COLUNA_ID_CONTATO = "id_contato";
	public static final String COLUNA_ID_CATEGORIA = "id_categoria";
	public static final String COLUNA_CONTATO = "contato";

	public static final int STATUS_DEVOLVIDO = 1;
	public static final String TABELA_EMPRESTIMOS = "emprestimos";

	private static String defineTable() {
		TableBuilder tb = new TableBuilder(TABELA_EMPRESTIMOS);
		try {
			tb.setPrimaryKey(COLUNA_ID_EMPRESTIMO, "INTEGER",true);
			tb.addColuna(COLUNA_ITEM, "TEXT", true);
			tb.addColuna(COLUNA_DESCRICAO, "TEXT", true);
			tb.addColuna(COLUNA_STATUS, "INTEGER", true);
			tb.addColuna(COLUNA_ATIVAR_ALARME, "INTEGER", true);
			tb.addColuna(COLUNA_DATA_DEVOLUCAO, "INTEGER", false);
			tb.addColuna(COLUNA_ID_CONTATO, "INTEGER", true);
			tb.addColuna(COLUNA_CONTATO, "TEXT", false);
			tb.addFK(COLUNA_ID_CATEGORIA, "INTEGER", CategoriaDAO.TABELA_CATEGORIA,
					CategoriaDAO.COLUNA_ID, tb.CASCADE, tb.CASCADE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tb.toString();
	}

	public static final String CREATE_TABELA_EMPRESTIMO = defineTable();

	public EmprestimoDAO(Context ctx) {
		super(ctx);
	}

	private static Emprestimo deCursorParaEmprestimo(Cursor c) {
		if (c == null || (c != null && c.getCount() == 0)) {
			return null;
		}
		Emprestimo emp = new Emprestimo();
		emp.setAtivarAlarme(c.getInt(c.getColumnIndex(COLUNA_ATIVAR_ALARME)));

		emp.setData(new Date(c.getLong(c.getColumnIndex(COLUNA_DATA_DEVOLUCAO))));

		emp.setDescricao(c.getString(c.getColumnIndex(COLUNA_DESCRICAO)));
		emp.setIdCategoria(c.getLong(c.getColumnIndex(COLUNA_ID_CATEGORIA)));
		emp.setIdContato(c.getLong(c.getColumnIndex(COLUNA_ID_CONTATO)));
		emp.setIdEmprestimo(c.getLong(c.getColumnIndex(COLUNA_ID_EMPRESTIMO)));
		emp.setItem(c.getString(c.getColumnIndex(COLUNA_ITEM)));
		emp.setContato(c.getString(c.getColumnIndex(COLUNA_CONTATO)));

		emp.setStatus(c.getInt(c.getColumnIndex(COLUNA_STATUS)) == Emprestimo.STATUS_EMPRESTAR ? Emprestimo.STATUS_EMPRESTAR
				: Emprestimo.STATUS_PEGAR_EMPRESTADO);
		return emp;
	}

	private static ContentValues deEmprestimoParaContentValues(Emprestimo emp) {
		ContentValues values = new ContentValues();
		// values.put(COLUNA_ID_EMPRESTIMO, emp.getIdEmprestimo());
		values.put(COLUNA_ITEM, emp.getItem());
		values.put(COLUNA_DESCRICAO, emp.getDescricao());
		values.put(COLUNA_DATA_DEVOLUCAO, emp.getData().getTime());
		values.put(COLUNA_STATUS, emp.getStatus());
		values.put(COLUNA_ATIVAR_ALARME, emp.getAtivarAlarme());
		values.put(COLUNA_ID_CONTATO, emp.getIdContato());
		values.put(COLUNA_ID_CATEGORIA, emp.getIdCategoria());
		values.put(COLUNA_CONTATO, emp.getContato());
		return values;
	}

	public long inserirEmprestimo(Emprestimo emp) {
		ContentValues values = deEmprestimoParaContentValues(emp);
		return inserir(TABELA_EMPRESTIMOS, values);
	}

	public boolean deleteEmprestimo(long id) {

		return remover(TABELA_EMPRESTIMOS, COLUNA_ID_EMPRESTIMO, id);
	}

	public boolean deleteEmprestimoPorCategoria(long idCategoria) {

		return remover(TABELA_EMPRESTIMOS, COLUNA_ID_CATEGORIA, idCategoria);
	}

	public Cursor consultarTodos() {

		return consultarTodos(TABELA_EMPRESTIMOS);
	}

	public Cursor consultarEmprestimo(long idEmprestimo) {
		Cursor mCursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_EMPRESTIMO,
				String.valueOf(idEmprestimo));
		return mCursor;

	}

	public Emprestimo consultar(long idEmprestimo) {
		Cursor mCursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_EMPRESTIMO,
				String.valueOf(idEmprestimo));
		Emprestimo emp = deCursorParaEmprestimo(mCursor);
		mCursor.close();
		return emp;

	}

	public boolean existe(long idEmprestimo) {
		boolean existe;

		Cursor mCursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_EMPRESTIMO,
				String.valueOf(idEmprestimo));

		if (mCursor.getCount() > 0) {
			existe = true;
		} else {
			existe = false;
		}

		mCursor.close();

		return existe;

	}

	public long atualizarEmprestimo(Emprestimo emp) {
		ContentValues values = deEmprestimoParaContentValues(emp);
		return atualizar(TABELA_EMPRESTIMOS, values,
				new String[] { EmprestimoDAO.COLUNA_ID_EMPRESTIMO },
				new String[] { String.valueOf(emp.getIdEmprestimo()) });
	}

	public void atualizarAlarme(long id) {
		ContentValues values = new ContentValues();
		values.put(COLUNA_ATIVAR_ALARME, Emprestimo.DESATIVAR_ALARME);
		atualizar(TABELA_EMPRESTIMOS, values,
				new String[] { EmprestimoDAO.COLUNA_ID_EMPRESTIMO },
				new String[] { String.valueOf(id) });

	}
	
	public void atualizarStatus(long id) {
		ContentValues values = new ContentValues();
		values.put(COLUNA_STATUS, STATUS_DEVOLVIDO);
		atualizar(TABELA_EMPRESTIMOS, values,
				new String[] { EmprestimoDAO.COLUNA_ID_EMPRESTIMO },
				new String[] { String.valueOf(id) });

	}	

	public List<Emprestimo> listarEmprestimoPorCategoria(long idCategoria) {
		List<Emprestimo> lista = new ArrayList<Emprestimo>();
		Cursor cursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_CATEGORIA,
				String.valueOf(idCategoria));

		while (!cursor.isAfterLast()) {
			lista.add(deCursorParaEmprestimo(cursor));
			cursor.moveToNext();
		}

		cursor.close();

		return lista;
	}

	public Cursor consultarEmprestimoPorCategoria(long idCategoria)
			throws SQLException {
		Cursor mCursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_CATEGORIA,
				String.valueOf(idCategoria));

		return mCursor;
	}

	public long consultarQtdeEmprestimosPorCategoria(long idCategoria)
			throws SQLException {
		Cursor mCursor = consultar(TABELA_EMPRESTIMOS, COLUNA_ID_CATEGORIA,
				String.valueOf(idCategoria));
		long qtde = mCursor.getCount();
		mCursor.close();

		return qtde;
	}

	public Loader<Cursor> getLoaderAllContents() {
		return getLoader(TABELA_EMPRESTIMOS);
	}

	public Loader<Cursor> getLoaderContents(long idCat) {
		return getLoader(TABELA_EMPRESTIMOS,EmprestimoDAO.COLUNA_ID_CATEGORIA,String.valueOf(idCat));
	}


	
	

}
