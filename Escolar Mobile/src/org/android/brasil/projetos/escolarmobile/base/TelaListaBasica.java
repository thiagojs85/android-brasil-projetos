package org.android.brasil.projetos.escolarmobile.base;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.AlunoVO;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public abstract class TelaListaBasica extends ListActivity implements
		OnClickListener {

	protected static final int ADD_ID = Menu.FIRST;
	protected static final int EDIT_ID = ADD_ID + 1;
	protected static final int DELETE_ID = EDIT_ID + 1;

	protected Button btAdd;
	protected Cursor c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(setTitle());

		setContentView(R.layout.base_list_window);
		btAdd = (Button) findViewById(R.id.add);
		registerForContextMenu(getListView());

		updateItens();
		btAdd.setOnClickListener(this);
	}

	protected boolean isMultiItensSelectable() {
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(Menu.NONE, EDIT_ID, 0, R.string.base_menu_edit);
		menu.add(Menu.NONE, DELETE_ID, 0, R.string.base_menu_delete);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT_ID:
			setActionOnEditItem(item);
			break;
		case DELETE_ID:
			setActionOnDeleteItem(item);
			break;
		}
		return super.onContextItemSelected(item);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (c != null) {
			c.close();
		}
	}

	/**
	 * Este método procura os ítens que farão parte da lista exibida pela
	 * atividade. Ele é chamado automaticamente na criação da tela, mas para se
	 * atualizar a lista de ítens posteriormente, deve-se chamar este método.
	 */
	public void updateItens() {
		c = getItensCursor();

		if (c != null) {

			c.moveToFirst();
			while(!c.isAfterLast()){
				for (int j = 0; j < c.getColumnNames().length; ++j) {
					Log.w(getTitle().toString(), c.getColumnName(j) + ": "
						+ String.valueOf(c.getLong(j)));
				}
				c.moveToNext();
			}

			c.moveToFirst();
			startManagingCursor(c);
		}
		if (isMultiItensSelectable()) {

			setListAdapter(new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_multiple_choice, c,
					new String[] { AlunoVO.COLUMN_NOME },
					new int[] { android.R.id.text1 }));

			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

			for (int i = 0; i < getListAdapter().getCount(); ++i) {
				getListView().setItemChecked(i, true);
			}

		} else {
			// A criação deste ListAdapter é praticamente a mesma utilizada
			// no tutorial do Notepad.
			// Mais informações no Step 12 em
			// http://developer.android.com/resources/tutorials/notepad/notepad-ex1.html

			if (getItensOfLine().length < 2) {
				setListAdapter(new SimpleCursorAdapter(this,
						R.layout.base_list_item, c, getItensOfLine(),
						new int[] { R.id.n_prontuario }));
			} else {
				setListAdapter(new SimpleCursorAdapter(this,
						R.layout.base_list_item, c, getItensOfLine(),
						new int[] { R.id.n_prontuario, R.id.n_sub }));
			}
		}
		if (c != null) {
			stopManagingCursor(c);
		}

	}

	protected String[] getItensOfLine() {
		return new String[] { AlunoVO.COLUMN_NOME };

	}

	public abstract void onClick(View v);

	public Button getBtAdd() {
		return btAdd;
	}

	public void setBtAdd(Button btAdd) {
		this.btAdd = btAdd;
	}

	/**
	 * Recupera o cursor apontando para os itens que deverão ser exibidos na
	 * lista da Activity.
	 * 
	 * @return Cursor resultante da consulta à tabela para recuperar os itens a
	 *         serem exibidos.
	 */
	public abstract Cursor getItensCursor();

	/**
	 * Metodos que devem ser implementados ao se extender uma classe para as
	 * ações de Editar um item da lista e para Deletar um item
	 */
	public abstract void setActionOnEditItem(MenuItem item);

	public abstract int setTitle();

	public abstract void setActionOnDeleteItem(MenuItem item);

	/**
	 * Sempre que uma Activity for iniciada com startActivityForResult ao
	 * terminar a sua execução o método onActivityResult é chamado, precisamos
	 * atualizar os itens da lista, pois normalmente chamamos uma Activity para
	 * add,editar ou remover um item da lista.
	 * */

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		updateItens();
	}
}
