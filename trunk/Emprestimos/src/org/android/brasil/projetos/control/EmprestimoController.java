package org.android.brasil.projetos.control;

import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.gui.R;
import org.android.brasil.projetos.model.Emprestimo;

import android.app.Activity;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class EmprestimoController {

	public static final int TODOS = 2;
	private Cursor cursorEmprestimos;
	private Activity act;

	public EmprestimoController(Activity activity) {
		act = activity;
	}

	public SimpleCursorAdapter getEmprestimoAdapter(long id) {

		// Fix para Android 3.0 ou superiores
		if (cursorEmprestimos != null && !cursorEmprestimos.isClosed()) {
			act.stopManagingCursor(cursorEmprestimos);
			cursorEmprestimos.close();
		}

		EmprestimoDAO.open(act);
		if (id == TODOS) {
			cursorEmprestimos = EmprestimoDAO.consultarTodos();
		} else {
			cursorEmprestimos = EmprestimoDAO
					.consultarEmprestimoPorCategoria(id);
		}
		EmprestimoDAO.close();

		act.startManagingCursor(cursorEmprestimos);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { EmprestimoDAO.COLUNA_ITEM };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
				R.layout.linha_listview, cursorEmprestimos, from, to);

		return adapter;
	}

	public boolean deletarEmprestimo(long id) {
		EmprestimoDAO.open(act);
		boolean resp = EmprestimoDAO.deleteEmprestimo(id);
		EmprestimoDAO.close();

		return resp;
	}

	public Emprestimo getEmprestimo(long id) {
		EmprestimoDAO.open(act);
		Emprestimo emprestimo = EmprestimoDAO.consultar(id);
		EmprestimoDAO.close();

		return emprestimo;
	}

	public boolean existe(long id) {
		EmprestimoDAO.open(act);
		boolean isValido = EmprestimoDAO.existe(id);
		EmprestimoDAO.close();

		return isValido;
	}

	public void atualizarEmprestimo(Emprestimo emp) {
		EmprestimoDAO.open(act);
		EmprestimoDAO.atualizarEmprestimo(emp);
		EmprestimoDAO.close();
	}

	public long inserirEmprestimo(Emprestimo emp) {
		EmprestimoDAO.open(act);
		long id = EmprestimoDAO.inserirEmprestimo(emp);
		EmprestimoDAO.close();
		return id;
	}

	public void close() {
		if (cursorEmprestimos != null) {
			act.stopManagingCursor(cursorEmprestimos);
			cursorEmprestimos.close();
		}

	}

	public long consultarQtdeEmprestimosPorCategoria(long idCategoria) {
		EmprestimoDAO.open(act);
		long qtde = EmprestimoDAO
				.consultarQtdeEmprestimosPorCategoria(idCategoria);
		EmprestimoDAO.close();

		return qtde;
	}

	public void deleteEmprestimoPorCategoria(Long idCategoria) {
		EmprestimoDAO.open(act);
		EmprestimoDAO.deleteEmprestimoPorCategoria(idCategoria);
		EmprestimoDAO.close();

	}
	
	public long inserirOuAtualizar(Emprestimo emp) {
		if (emp.getIdEmprestimo() == 0) {

			long id = inserirEmprestimo(emp);
			if (id > 0) {
				emp.setIdCategoria(id);
			}
		} else {
			atualizarEmprestimo(emp);
		}
		
		return emp.getIdEmprestimo();
	}
}
