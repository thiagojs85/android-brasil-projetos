package org.android.brasil.projetos.gui;

import org.android.brasil.projetos.control.CategoriaController;
import org.android.brasil.projetos.control.EmprestimoController;
import org.android.brasil.projetos.dao.CategoriaDAO;
import org.android.brasil.projetos.dao.EmprestimoDAO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class EmprestimoFragment extends ListFragment {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private EmprestimoController ec;
	private CategoriaController cc;
	private boolean load;
	private Spinner spCategoria;
	private OnItemSelectedListener mCallback;

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnItemSelectedListener {
		/** Called when a list item is selected */
		public void onItemSelected(long id);
	}

	@Override
	public void onResume() {
		super.onResume();
		fillData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container != null) {
			container.removeAllViews();
		}
		View view = inflater.inflate(R.layout.emprestimo, container,
				false);
		spCategoria = (Spinner) view.findViewById(R.id.spCategoria);
		return view;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		load = true;

	}

	@Override
	public void onStart() {
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.ListItensFragment) != null) {

		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnItemSelectedListener");
		}
	}

	private void fillData() {

		ec = new EmprestimoController(this.getActivity());
		cc = new CategoriaController(this.getActivity());
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		registerForContextMenu(getListView());
		setListAdapter(ec.getAdapter(CategoriaDAO.TODAS_ID));

			spCategoria
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							setListAdapter(ec.getAdapter(id));

							if (id == CategoriaDAO.TODAS_ID && !load) {
								load = false;
								Intent i = new Intent(getActivity(),
										EditarEmprestimoActivity.class);
								i.putExtra(EmprestimoDAO.TABELA_EMPRESTIMOS, id);
								startActivityForResult(i, ACTIVITY_CREATE);
							}
						}

						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});

			SimpleCursorAdapter categoriaAdapter = cc
					.getAdapter(CategoriaDAO.TODAS_ID);
			spCategoria.setAdapter(categoriaAdapter);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); menu.add(0, INSERT_ID, 0,
	 * R.string.menu_inserir).setIcon( R.drawable.adicionar); ; return true; }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { switch (item.getItemId()) { case INSERT_ID: editarEmprestimo(); return
	 * true; }
	 * 
	 * return super.onMenuItemSelected(featureId, item); }
	 */

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, INSERT_ID, 0, R.string.menu_inserir).setIcon(
				R.drawable.adicionar);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case INSERT_ID:
			editarEmprestimo(info.id);
			return true;
		case DELETE_ID:
			ec.deletarEmprestimo(info.id);
			// fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void editarEmprestimo(long id) {
		Intent i = new Intent(this.getActivity(),
				EditarEmprestimoActivity.class);
		// i.putExtra(EmprestimoDAO.TABELA_EMPRESTIMOS, id);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		/*
		 * if (getActivity().findViewById(R.id.editEmprestimoView) == null) {
		 * Intent i = new Intent(this.getActivity(),
		 * EditarEmprestimoActivity.class);
		 * i.putExtra(EmprestimoDAO.TABELA_EMPRESTIMOS, id);
		 * startActivityForResult(i, ACTIVITY_EDIT); } else {
		 */
		// Notify the parent activity of selected item
		mCallback.onItemSelected(id);
		// Set the item as checked to be highlighted when in two-pane
		// layout
		getListView().setItemChecked(position, true);
		// }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (cc == null) {
			ec = new EmprestimoController(this.getActivity());
			cc = new CategoriaController(this.getActivity());
		}
		fillData();
	}
}
