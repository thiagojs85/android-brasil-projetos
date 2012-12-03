package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.model.Categoria;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public class CategoriaFragment extends SherlockListFragment {

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private long idCategoria;

	private CategoriaController cc;
	private EmprestimoController ec;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container != null) {
			container.removeAllViews();
		}
		return inflater.inflate(R.layout.lista_categoria, container, false);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ec = new EmprestimoController(this.getActivity());
		if (cc == null) {
			cc = new CategoriaController(this.getActivity());
		}
		setListAdapter(cc.getAdapter(CategoriaDAO.TODAS_ID));
		// fillData();
	}

	@Override
	public void onStart() {
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.base) == null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			registerForContextMenu(getListView());
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { super.onCreateOptionsMenu(menu); menu.add(0, INSERT_ID,
	 * 0, R.string.menu_inserirCategoria).setIcon( R.drawable.adicionar); return true; }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item) { switch (item.getItemId()) { case
	 * INSERT_ID:
	 * 
	 * idCategoria = null;
	 * 
	 * if (etDescricao != null) { etDescricao.setText(""); }
	 * 
	 * dialogEditarCategoria(new Categoria()); return true; }
	 * 
	 * return super.onMenuItemSelected(featureId, item); }
	 */

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_deleteCategoria);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case DELETE_ID:

			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

			idCategoria = info.id;

			if (cc.isCategoriaPadrao(idCategoria)) {
				Toast.makeText(this.getActivity(), R.string.esta_categoria_nao_pode_ser_excluida, Toast.LENGTH_SHORT)
						.show();
				return false;
			}

			long qtde = ec.consultarQtdeEmprestimosPorCategoria(info.id);

			if (qtde > 0) {
				AlertDialog.Builder alerta = new AlertDialog.Builder(this.getActivity());
				alerta.setIcon(R.drawable.im_atencao);
				alerta.setTitle(R.string.exclusao);
				alerta.setMessage(getActivity().getResources().getString(R.string.deseja_excluir_esta_categoria) + qtde
						+ getActivity().getResources().getString(R.string.emprestimo_com_esta_categoria));

				alerta.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						cc.deleteCategoria(idCategoria);
						ec.deleteEmprestimoPorCategoria(idCategoria);

						// fillData();
						Toast.makeText(CategoriaFragment.this.getActivity(), R.string.excluido_com_sucesso,
								Toast.LENGTH_SHORT).show();
					}
				});

				alerta.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

				alerta.show();

				return super.onContextItemSelected(item);
			}
			cc.deleteCategoria(idCategoria);
			// fillData();
			Toast.makeText(this.getActivity(), R.string.excluido_com_sucesso, Toast.LENGTH_SHORT).show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Categoria cat = cc.getCategoria(id);
		idCategoria = id;
		dialogEditarCategoria(CategoriaFragment.this.getActivity(), cat, id);

	}

	public static void dialogEditarCategoria(final FragmentActivity act, Categoria cat, final long idCategoria) {

		final CategoriaController cc = new CategoriaController(act);

		final Dialog dialog = new Dialog(act);

		dialog.setContentView(R.layout.custom_dialog);

		dialog.setTitle(R.string.ListaCategoria);

		Button btnConfirmar = (Button) dialog.findViewById(R.id.bt_confirmar);
		Button btnCancelar = (Button) dialog.findViewById(R.id.bt_cancel);
		EditText etDescricao = (EditText) dialog.findViewById(R.id.inputText);

		if (cat != null) {
			etDescricao.setText(cat.getNomeCategoria());
		}

		btnConfirmar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				boolean isValid = true;
				EditText etDescricao = (EditText) dialog.findViewById(R.id.inputText);
				if (etDescricao.getText().toString().equals("")) {
					Toast.makeText(act, R.string.preencher_descricao, Toast.LENGTH_SHORT).show();
					isValid = false;
				} else if (cc.isDescricaoCategoriaJaExiste(etDescricao.getText().toString())) {
					Toast.makeText(act, R.string.descricao_categoria_ja_existe, Toast.LENGTH_SHORT).show();
					isValid = false;
				}
				if (isValid) {
					Categoria cat = new Categoria();
					cat.setNomeCategoria(etDescricao.getText().toString().trim());

					if (idCategoria < 0) {
						cat.setId(0);
					} else {
						cat.setId(idCategoria);
					}

					cc.inserirOuAtualizar(cat);
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
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// fillData();
	}

}
