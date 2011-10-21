package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;
import org.android.brasil.projetos.model.Categoria;
import org.android.brasil.projetos.model.TipoCategoria;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CategoriaUI extends ListActivity {
	// private static final int ACTIVITY_CREATE = 0;
	// private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private Long mRowId;
	private String descricaoCategoria;
	private Cursor cursorCategoria;

	private EditText etDescricao;
	private Button btnConfirmar;
	private Button btnCancelar;

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

			mRowId = null;
			descricaoCategoria = "";
			etDescricao.setText("");

			showCustomDialog();
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

			mRowId = info.id;

			if (mRowId == TipoCategoria.OUTRA.getId()
					|| mRowId == TipoCategoria.TODOS.getId()) {
				Toast.makeText(this, "Esta categoria não pode ser excluída!",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			EmprestimoDAO.open(getApplicationContext());
			long qtde = EmprestimoDAO
					.consultarQtdeEmprestimosPorCategoria(info.id);
			EmprestimoDAO.close();

			if (qtde > 0) {
				AlertDialog.Builder alerta = new AlertDialog.Builder(
						CategoriaUI.this);
				alerta.setIcon(R.drawable.im_atencao);
				alerta.setTitle("Exclusão");
				alerta.setMessage("Deseja excluir esta categoria e \n " + qtde
						+ " empréstimo(s) com esta categoria ?");

				alerta.setPositiveButton("Sim",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								CategoriaDAO.open(getApplicationContext());
								CategoriaDAO.deleteCategoria(mRowId);
								CategoriaDAO.close();

								EmprestimoDAO.open(getApplicationContext());
								EmprestimoDAO
										.deleteEmprestimoPorCategoria(mRowId);
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
			CategoriaDAO.deleteCategoria(mRowId);
			CategoriaDAO.close();
			fillData();
			Toast.makeText(CategoriaUI.this, "Excluído com sucesso!",
					Toast.LENGTH_SHORT).show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		CategoriaDAO.open(getApplicationContext());
		Categoria cat = CategoriaDAO.consultar(id);
		CategoriaDAO.close();

		descricaoCategoria = cat.getNomeCategoria();
		mRowId = id;

		showCustomDialog();

	}

	private void showCustomDialog() {

		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.custom_dialog);

		dialog.setTitle("Categorias");

		btnConfirmar = (Button) dialog.findViewById(R.id.bt_confirmar);
		btnCancelar = (Button) dialog.findViewById(R.id.bt_cancel);
		etDescricao = (EditText) dialog.findViewById(R.id.inputText);

		etDescricao.setText(descricaoCategoria);

		btnConfirmar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				saveState();
				dialog.dismiss();
			}
		});

		btnCancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();// encerra o dialog
			}
		});

		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	private void saveState() {
		CategoriaDAO.open(getApplicationContext());
		Categoria cat = new Categoria();

		cat.setNomeCategoria(etDescricao.getText().toString().trim());

		if (mRowId == null) {
			
			long id = CategoriaDAO.inserir(cat);
			
			if(id >0) {
				mRowId = id;
			}
			
		} else {
			cat.setId(mRowId);
			CategoriaDAO.atualizar(cat);
		}	
		CategoriaDAO.close();
		
		fillData();
		
	}
}
