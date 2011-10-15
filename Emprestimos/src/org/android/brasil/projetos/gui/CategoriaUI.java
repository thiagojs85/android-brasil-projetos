package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.TipoCategoria;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CategoriaUI extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private long idCategoria;
	private Cursor cursorCategoria;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_categoria);
		setTitle(R.string.ListaCategoria);

		fillData();
		registerForContextMenu(getListView());

	}

	private void fillData() {

		// Fix para Android 3.0 ou superiores
		if (cursorCategoria != null && !cursorCategoria.isClosed()) {
			stopManagingCursor(cursorCategoria);
			cursorCategoria.close();
		}

		CategoriaDAO.open(getApplicationContext());
		cursorCategoria = CategoriaDAO.consultarTodasCategorias();
		CategoriaDAO.close();
		startManagingCursor(cursorCategoria);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { CategoriaDAO.COLUNA_DESCRICAO };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.linha_categoria_listview, cursorCategoria, from, to);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_inserirCategoria);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createCategoria();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_deleteCategoria);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case DELETE_ID:

			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();

			idCategoria = info.id;

			if (idCategoria == TipoCategoria.OUTRA.getId()
					|| idCategoria == TipoCategoria.TODOS.getId()) {
				Toast.makeText(this, "Esta categoria não pode ser excluída!",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			EmprestimoDAO.open(getApplicationContext());
			//TODO: Fazer método que retorne o número de Emprestimos naquela categoria e não o cursor!
			
			long qtde = EmprestimoDAO.consultarQtdeEmprestimosPorCategoria(info.id);
			

			
			if (qtde > 0) {
				AlertDialog.Builder alerta = new AlertDialog.Builder(
						CategoriaUI.this);
				alerta.setIcon(R.drawable.im_atencao);
				alerta.setTitle("Exclusão");
				alerta.setMessage("Deseja excluir esta categoria e \n "
						+ qtde
						+ " empréstimo(s) com esta categoria ?");

				alerta.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								CategoriaDAO.open(getApplicationContext());
								CategoriaDAO.deleteCategoria(idCategoria);
								CategoriaDAO.close();
								
								EmprestimoDAO.open(getApplicationContext());
								EmprestimoDAO
										.deleteEmprestimoPorCategoria(idCategoria);
								EmprestimoDAO.close();
								
								fillData();
								Toast.makeText(CategoriaUI.this,
										"Excluído com sucesso!",
										Toast.LENGTH_SHORT).show();
							}
						});

				alerta.setNegativeButton("Não",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						});

				alerta.show();

				return super.onContextItemSelected(item);
			}

			CategoriaDAO.open(getApplicationContext());
			CategoriaDAO.deleteCategoria(idCategoria);
			CategoriaDAO.close();
			fillData();
			Toast.makeText(CategoriaUI.this, "Excluído com sucesso!",
					Toast.LENGTH_SHORT).show();
		}
		return super.onContextItemSelected(item);
	}

	private void createCategoria() {
		Intent i = new Intent(this, EditarCategoria.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, EditarCategoria.class);
		i.putExtra(CategoriaDAO.COLUNA_ID, id);
		Log.w("CategoriaUI id: ", String.valueOf(id));
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}
}
