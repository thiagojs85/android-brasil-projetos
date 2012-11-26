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
	private boolean isClosed;

	public EmprestimoController(Activity activity) {
		act = activity;
		isClosed = false;
	}

	public SimpleCursorAdapter getEmprestimoAdapter(long id) {

		// Fix para Android 3.0 ou superiores
		if (cursorEmprestimos != null && !cursorEmprestimos.isClosed()) {
			act.stopManagingCursor(cursorEmprestimos);
			cursorEmprestimos.close();
		}

		if (id == TODOS) {
			cursorEmprestimos = new EmprestimoDAO(act).consultarTodos();
		} else {
			cursorEmprestimos = new EmprestimoDAO(act)
					.consultarEmprestimoPorCategoria(id);
		}

		act.startManagingCursor(cursorEmprestimos);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { new EmprestimoDAO(act).COLUNA_ITEM };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
				R.layout.linha_listview, cursorEmprestimos, from, to);

		return adapter;
	}

	public boolean deletarEmprestimo(long id) {
		boolean resp = new EmprestimoDAO(act).deleteEmprestimo(id);
		return resp;
	}

	public Emprestimo getEmprestimo(long id) {
		Emprestimo emprestimo = new EmprestimoDAO(act).consultar(id);
		return emprestimo;
	}

	public boolean existe(long id) {
		boolean isValido = new EmprestimoDAO(act).existe(id);
		return isValido;
	}

	public void atualizarEmprestimo(Emprestimo emp) {
		new EmprestimoDAO(act).atualizarEmprestimo(emp);
	}

	public long inserirEmprestimo(Emprestimo emp) {
		long id = new EmprestimoDAO(act).inserirEmprestimo(emp);
		return id;
	}

	public void close() {
		if (cursorEmprestimos != null) {
			isClosed = true;
			act.stopManagingCursor(cursorEmprestimos);
			cursorEmprestimos.close();
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	public long consultarQtdeEmprestimosPorCategoria(long idCategoria) {
		long qtde = new EmprestimoDAO(act)
				.consultarQtdeEmprestimosPorCategoria(idCategoria);
		return qtde;
	}

	public void deleteEmprestimoPorCategoria(Long idCategoria) {
		new EmprestimoDAO(act).deleteEmprestimoPorCategoria(idCategoria);
	}

	public long inserirOuAtualizar(Emprestimo emp) {
		if (emp.getIdEmprestimo() <= 0) {
			return inserirEmprestimo(emp);
		} else {
			atualizarEmprestimo(emp);
		}
		return emp.getIdEmprestimo();
	}

	public void devolverOuReceber(Long idEmprestimo) {
		new EmprestimoDAO(act).atualizarStatus(idEmprestimo);
	}
}
