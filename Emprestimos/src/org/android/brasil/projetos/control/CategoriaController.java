package org.android.brasil.projetos.control;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.gui.R;
import org.android.brasil.projetos.model.Categoria;
import org.android.brasil.projetos.model.TipoCategoria;

import android.app.Activity;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class CategoriaController {
	public static final int TODOS = -1;
	private Cursor cursorCategoria;
	private Activity act;
	private boolean isClosed;

	public CategoriaController(Activity activity) {
		act = activity;
		isClosed = false;
	}

	public SimpleCursorAdapter getCategoriaAdapter(long id, boolean checkbox) {
		// Fix para Android 3.0 ou superiores
		if (cursorCategoria != null && !cursorCategoria.isClosed()) {
			act.stopManagingCursor(cursorCategoria);
			cursorCategoria.close();
		}

		CategoriaDAO.open(act);
		if (id == TODOS) {
			cursorCategoria = CategoriaDAO.consultarTodasCategorias();
		} else {
			cursorCategoria = CategoriaDAO.consultarCategoria(id);
		}

		CategoriaDAO.close();

		act.startManagingCursor(cursorCategoria);

		if (checkbox) {

			SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
					R.layout.simplerow, cursorCategoria,
					new String[] { CategoriaDAO.COLUNA_DESCRICAO },
					new int[] { R.id.rowTextView });

			return adapter;

		} else {
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
					R.layout.linha_listview, cursorCategoria,
					new String[] { CategoriaDAO.COLUNA_DESCRICAO },
					new int[] { R.id.text1 });

			return adapter;
		}

	}

	public Categoria getCategoria(long id) {
		CategoriaDAO.open(act);
		Categoria cat = CategoriaDAO.consultar(id);
		CategoriaDAO.close();

		return cat;
	}

	public void close() {
		if (cursorCategoria != null) {
			isClosed = true;
			act.stopManagingCursor(cursorCategoria);
			cursorCategoria.close();
		}

	}

	public boolean isClosed() {
		return isClosed;
	}

	public void deleteCategoria(Long idCategoria) {
		CategoriaDAO.open(act);
		CategoriaDAO.deleteCategoria(idCategoria);
		CategoriaDAO.close();
	}

	public void atualizar(Categoria cat) {
		CategoriaDAO.open(act);
		CategoriaDAO.atualizar(cat);
		CategoriaDAO.close();

	}

	public long inserir(Categoria cat) {
		CategoriaDAO.open(act);
		long id = CategoriaDAO.inserir(cat);
		CategoriaDAO.close();
		return id;
	}

	public boolean isCategoriaPadrao(long idCategoria) {
		if (idCategoria == TipoCategoria.OUTRA.getId()
				|| idCategoria == TipoCategoria.TODOS.getId()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDescricaoCategoriaJaExiste(String descricao) {
		CategoriaDAO.open(act);
		boolean existe = CategoriaDAO.isDescricaoCategoriaJaExiste(descricao);
		CategoriaDAO.close();

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

}
