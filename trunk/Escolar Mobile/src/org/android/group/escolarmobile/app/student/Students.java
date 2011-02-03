package org.android.group.escolarmobile.app.student;

import org.group.dev.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Classe que exibir� os alunos j� cadastrados no sistema.
 * @author Otavio
 *
 */
public class Students extends ListActivity implements OnItemLongClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//FIXME Se for�ar para o bot�o aparecer, a lista de alunos n�o ser� exibida.
		//setContentView(R.layout.list);
		
		//TODO Implementar acesso a banco de dados para recuperar mat�rias cadastradas.
		setListAdapter(new ArrayAdapter<String>(this, R.layout.items, DUMMY_STUDENTS));
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemLongClickListener(this);
	}
	
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// Quando um item � pressionado por um tempo, abre-se um di�logo de op��es.
		return showDialog(EDIT_DELETE_DIALOG, null);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
		case EDIT_DELETE_DIALOG:
			builder.setTitle("Selecione a ao:");
			builder.setItems(DIALOG_OPTIONS, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	// TODO verificar se � para editar ou deletar e executar a fun��o correspondente.
			        Toast.makeText(getApplicationContext(), DIALOG_OPTIONS[item], Toast.LENGTH_SHORT).show();
			    }
			});
			break;
		default:
			return super.onCreateDialog(id, args);
		}
		
		return builder.create();		
	}
	
	private final static int EDIT_DELETE_DIALOG = 0;
	private final static String[] DIALOG_OPTIONS = new String[]{"Editar", "Excluir"};
	private final static String[] DUMMY_STUDENTS = new String[]{"Aluno 1", "Aluno 2", "Aluno 3"};
}
