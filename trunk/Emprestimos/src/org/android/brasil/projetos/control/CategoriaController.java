package org.android.brasil.projetos.control;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.gui.R;
import org.android.brasil.projetos.model.Categoria;

import android.app.Activity;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class CategoriaController {
	public static final int TODOS = -1;
	private Cursor cursorCategoria;
	private Activity act;

	public CategoriaController(Activity activity) {
		act = activity;
	}

	public SimpleCursorAdapter getCategoriaAdapter(long id) {
		// Fix para Android 3.0 ou superiores
		if (cursorCategoria != null && !cursorCategoria.isClosed()) {
			act.stopManagingCursor(cursorCategoria);
			cursorCategoria.close();
		}

		CategoriaDAO.open(act);
		if (id == TODOS) {
			cursorCategoria = CategoriaDAO.consultarTodasCategorias();
		}else{
			cursorCategoria = CategoriaDAO.consultarCategoria(id);
		}
		
		CategoriaDAO.close();
		
		act.startManagingCursor(cursorCategoria);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(act,
				R.layout.linha_spinner, cursorCategoria,
				new String[] { CategoriaDAO.COLUNA_DESCRICAO }, 
				new int[] { R.id.text1 });
		
		return adapter;
	}
	
	public Categoria getCategoria(long id) {
		CategoriaDAO.open(act);
		Categoria cat = CategoriaDAO.consultar(id);
		CategoriaDAO.close();
		
		return cat;
	}
	

	public void close() {
		cursorCategoria.close();
		
	}
	
}
