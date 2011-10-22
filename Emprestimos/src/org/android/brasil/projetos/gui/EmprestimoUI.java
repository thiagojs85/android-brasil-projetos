package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.EmprestimoDAO;

import android.app.ListActivity;
import android.content.Intent;
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

	private EmprestimoController ec;
	private CategoriaController cc;
	private Spinner spCategoria;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_emprestimo);

		spCategoria = (Spinner) findViewById(R.id.spCategoria);

		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {

		ec = new EmprestimoController(this);
		cc = new CategoriaController(this);

		setListAdapter(ec.getEmprestimoAdapter(ec.TODOS));

		spCategoria
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						boolean todos = false;

						setListAdapter(ec.getEmprestimoAdapter(id));
					}

					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		SimpleCursorAdapter categoriaAdapter = cc.getCategoriaAdapter(cc.TODOS);
		if (categoriaAdapter != null && categoriaAdapter.getCount() > 0) {
			spCategoria.setEnabled(true);
		} else {
			spCategoria.setEnabled(false);
		}

		spCategoria.setAdapter(categoriaAdapter);

		if (categoriaAdapter != null && categoriaAdapter.getCount() > 0) {
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
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
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cc.close();
		ec.close();
	}
}
