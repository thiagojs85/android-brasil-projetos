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

		
		if (id == TODOS) {
			cursorCategoria = new CategoriaDAO(act).consultarTodasCategorias();
		} else {
			cursorCategoria = new CategoriaDAO(act).consultarCategoria(id);
		}

		

		act.startManagingCursor(cursorCategoria);

		if (checkbox) {

			SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
					R.layout.simplerow, cursorCategoria,
					new String[] { new CategoriaDAO(act).COLUNA_DESCRICAO },
					new int[] { R.id.rowTextView });

			return adapter;

		} else {
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
					R.layout.linha_listview, cursorCategoria,
					new String[] { new CategoriaDAO(act).COLUNA_DESCRICAO },
					new int[] { R.id.text1 });

			return adapter;
		}

	}

	public Categoria getCategoria(long id) {
		Categoria cat = new CategoriaDAO(act).consultar(id);
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
		if (idCategoria == TipoCategoria.OUTRA.getId()
				|| idCategoria == TipoCategoria.TODOS.getId()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDescricaoCategoriaJaExiste(String descricao) {
		
		boolean existe = new CategoriaDAO(act).isDescricaoCategoriaJaExiste(descricao);
		

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
