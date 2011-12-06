package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.model.Categoria;
import org.android.brasil.projetos.model.TipoCategoria;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

public class CategoriaUI extends ListActivity {
	// private static final int ACTIVITY_CREATE = 0;
	// private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private Long idCategoria;

	private EditText etDescricao;
	private Button btnConfirmar;
	private Button btnCancelar;

	private CategoriaController cc;
	private EmprestimoController ec;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_categoria);
		setTitle(R.string.ListaCategoria);
		cc = new CategoriaController(this);
		ec = new EmprestimoController(this);
		fillData();
		registerForContextMenu(getListView());

	}

	private void fillData() {
		if (cc == null) {
			cc = new CategoriaController(this);
		}
		setListAdapter(cc.getCategoriaAdapter(CategoriaController.TODOS));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_inserirCategoria).setIcon(
				R.drawable.adicionar);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:

			idCategoria = null;

			if (etDescricao != null) {
				etDescricao.setText("");
			}

			dialogEditarCategoria(null);
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
			long qtde = ec.consultarQtdeEmprestimosPorCategoria(info.id);

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
								cc.deleteCategoria(idCategoria);
								ec.deleteEmprestimoPorCategoria(idCategoria);
							
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
			cc.deleteCategoria(idCategoria);
			fillData();
			Toast.makeText(CategoriaUI.this, "Excluído com sucesso!",
					Toast.LENGTH_SHORT).show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Categoria cat = cc.getCategoria(id);
		idCategoria = id;
		dialogEditarCategoria(cat);

	}

	private void dialogEditarCategoria(Categoria cat) {

		final Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.custom_dialog);

		dialog.setTitle("Categorias");

		btnConfirmar = (Button) dialog.findViewById(R.id.bt_confirmar);
		btnCancelar = (Button) dialog.findViewById(R.id.bt_cancel);
		etDescricao = (EditText) dialog.findViewById(R.id.inputText);

		if (cat != null) {
			etDescricao.setText(cat.getNomeCategoria());
		}

		btnConfirmar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (validarDescricao()) {
					saveState();
					dialog.dismiss();
				}

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
		Categoria cat = new Categoria();
		cat.setNomeCategoria(etDescricao.getText().toString().trim());

		if (idCategoria == null) {

			long id = cc.inserir(cat);

			if (id > 0) {
				idCategoria = id;
			}

		} else {
			cat.setId(idCategoria);
			cc.atualizar(cat);
		}
		CategoriaDAO.close();

		fillData();

	}

	private boolean validarDescricao() {

		if (etDescricao.getText().toString().equals("")) {
			Toast.makeText(CategoriaUI.this, "Preencha a descrição!",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}
}
