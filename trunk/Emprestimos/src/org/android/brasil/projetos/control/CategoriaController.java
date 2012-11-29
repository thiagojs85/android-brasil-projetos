package org.android.brasil.projetos.control;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.gui.R;
import org.android.brasil.projetos.model.Categoria;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;


public class CategoriaController extends Controller {
	public CategoriaController(FragmentActivity activity) {
		super(activity);
		
		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { CategoriaDAO.COLUNA_DESCRICAO };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		adapter = new SimpleCursorAdapter(act,
				R.layout.linha_spinner, null, from, to,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	}
	public SimpleCursorAdapter getAdapter(long id) {
		Bundle bundle = new Bundle();
		bundle.putLong(CategoriaDAO.COLUNA_ID, id);
		act.getSupportLoaderManager().initLoader(getControllerIdentifier(), bundle, this);
		return adapter;

	}

	public Categoria getCategoria(long id) {
		Categoria cat = new CategoriaDAO(act).consultar(id);
		return cat;
	}

	public void deleteCategoria(Long idCategoria) {

		new CategoriaDAO(act).deleteCategoria(idCategoria);

	}

	public void atualizar(Categoria cat) {

		new CategoriaDAO(act).atualizar(cat);

	}

	public long inserir(Categoria cat) {

		long id = new CategoriaDAO(act).inserir(cat);

		return id;
	}

	public boolean isCategoriaPadrao(long idCategoria) {
		if (idCategoria == CategoriaDAO.OUTRA_ID
				|| idCategoria == CategoriaDAO.TODAS_ID) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDescricaoCategoriaJaExiste(String descricao) {

		boolean existe = new CategoriaDAO(act)
				.isDescricaoCategoriaJaExiste(descricao);

		return existe;
	}

	public long inserirOuAtualizar(Categoria cat) {
		if (cat.getId() == 0) {

			long id = inserir(cat);

			if (id > 0) {
				cat.setId(id);
			}

		} else {
			atualizar(cat);
		}

		return cat.getId();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		long idCat = arg1.getLong(CategoriaDAO.COLUNA_ID, CategoriaDAO.TODAS_ID);
		if(idCat == CategoriaDAO.TODAS_ID) {
			return new CategoriaDAO(act).getLoaderAllContents();
		}else{
			return new CategoriaDAO(act).getLoaderContents(idCat);
		}
	}
}
