package org.android.brasil.projetos.control;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.gui.R;
import org.android.brasil.projetos.model.Emprestimo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class EmprestimoController extends Controller {
	// Random unique number.
	private static final int EMPRESTIMO_CONTROLLER_ID = 1232130;

	public EmprestimoController(FragmentActivity activity) {
		super(activity);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { EmprestimoDAO.COLUNA_ITEM };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		adapter = new SimpleCursorAdapter(act, R.layout.linha_listview, null,
				from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}

	public SimpleCursorAdapter getAdapter(long id) {
		Bundle bundle = new Bundle();
		bundle.putLong(EmprestimoDAO.COLUNA_ID_CATEGORIA, id);
		if (act.getSupportLoaderManager().getLoader(EMPRESTIMO_CONTROLLER_ID) == null
				|| !act.getSupportLoaderManager()
						.getLoader(EMPRESTIMO_CONTROLLER_ID).isStarted()) {
			act.getSupportLoaderManager().initLoader(EMPRESTIMO_CONTROLLER_ID,
					bundle, this);
		} else {
			act.getSupportLoaderManager().restartLoader(
					EMPRESTIMO_CONTROLLER_ID, bundle, this);
		}
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

	public long consultarQtdeEmprestimosPorCategoria(long idCategoria) 
	{
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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		long idCat = arg1.getLong(EmprestimoDAO.COLUNA_ID_CATEGORIA,
				CategoriaDAO.TODAS_ID);
		if (idCat == CategoriaDAO.TODAS_ID) {
			return new EmprestimoDAO(act).getLoaderAllContents();
		} else {
			return new EmprestimoDAO(act).getLoaderContents(idCat);
		}
	}

}
