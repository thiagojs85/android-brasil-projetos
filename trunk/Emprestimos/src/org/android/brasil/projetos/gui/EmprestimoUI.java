package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Categoria;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class EmprestimoUI extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	
	private Spinner spCategoria;

	private Cursor cursorEmprestimos;
	private Cursor cursorCategoria;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_emprestimo);
		
		spCategoria = (Spinner) findViewById(R.id.spCategoria);
		
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		
		//Fix para Android 3.0 ou superiores
		if(cursorEmprestimos != null && !cursorEmprestimos.isClosed()){
			stopManagingCursor(cursorEmprestimos);
			cursorEmprestimos.close();
		}
		
		EmprestimoDAO.open(getApplicationContext());
		cursorEmprestimos = EmprestimoDAO.consultarTodos();
		EmprestimoDAO.close();
		
		startManagingCursor(cursorEmprestimos);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { EmprestimoDAO.COLUNA_ITEM };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.linha_emprestimo_listview,
				cursorEmprestimos, from, to);
		setListAdapter(adapter);
		
		spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				boolean todos = false;
				
				
				//Busca o objeto categoria selecionado
				CategoriaDAO.open(getApplicationContext());
				Categoria cat = CategoriaDAO.consultar(id);
				CategoriaDAO.close();
				
				//Verificação mais confiavel se a opção "Todos" foi escolhida				
				if(cat.getNomeCategoria().equals(CategoriaDAO.TODOS)){
					todos = true;
				}
				
				//Fix para Android 3.0 ou superiores
				if(cursorEmprestimos != null && !cursorEmprestimos.isClosed()){
					stopManagingCursor(cursorEmprestimos);
					cursorEmprestimos.close();
				}
				
				EmprestimoDAO.open(getApplicationContext());
				
				if (!todos) {
					cursorEmprestimos = EmprestimoDAO.consultarEmprestimoPorCategoria(id);
				
				} else {
					cursorEmprestimos = EmprestimoDAO.consultarTodos();
				}
				
				EmprestimoDAO.close();
				
				startManagingCursor(cursorEmprestimos);

				String[] from = new String[] { EmprestimoDAO.COLUNA_ITEM };
				int[] to = new int[] { R.id.text1 };

				SimpleCursorAdapter adt = new SimpleCursorAdapter(getApplicationContext(), R.layout.linha_emprestimo_listview,cursorEmprestimos, from, to);
				setListAdapter(adt);
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});

		//Fix para Android 3.0 ou superiores
		if(cursorCategoria != null && !cursorCategoria.isClosed()){
			stopManagingCursor(cursorCategoria);
			cursorCategoria.close();
		}
		

		CategoriaDAO.open(this);
		cursorCategoria = CategoriaDAO.consultarTodasCategorias();
		CategoriaDAO.close();
		
		if (cursorCategoria != null && cursorCategoria.getCount() > 0) {
			startManagingCursor(cursorCategoria);
			spCategoria.setEnabled(true);
		} else {
			spCategoria.setEnabled(false);
		}	
		
		spCategoria.setAdapter(new SimpleCursorAdapter(this,
				R.layout.linha_categoria_spinner, cursorCategoria,
				new String[] { CategoriaDAO.COLUNA_DESCRICAO }, 
				new int[] { R.id.text1 }));
		
		if (cursorCategoria != null && cursorCategoria.getCount() > 0) {
			spCategoria.setSelection(1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_inserir);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			editarEmprestimo();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			EmprestimoDAO.open(getApplicationContext());
			EmprestimoDAO.deleteEmprestimo(info.id);
			EmprestimoDAO.close();
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void editarEmprestimo() {
		Intent i = new Intent(this, EditarEmprestimo.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, EditarEmprestimo.class);
		i.putExtra(EmprestimoDAO.COLUNA_ID_EMPRESTIMO, id);
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(cursorEmprestimos != null && !cursorEmprestimos.isClosed()){
			stopManagingCursor(cursorEmprestimos);
			cursorEmprestimos.close();
		}
		
		if(cursorCategoria != null && !cursorCategoria.isClosed()){
			stopManagingCursor(cursorCategoria);
			cursorCategoria.close();
		}
	}
}
