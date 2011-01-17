package org.android.group.escolarmobile.app.subject;



import org.group.dev.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Classe que exibirá as matérias já cadastradas no sistema.
 * @author Otavio
 *
 */
public class Subjects extends ListActivity implements OnItemLongClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		// FIXME Botão não aparece; se forçar para aparecer, a lista de matérias não aparece...
//		button = (Button) findViewById(R.id.add);
//		button.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				Intent intent = new Intent().setClass(Subjects.this, AddSubject.class);
//				
//				// TODO Aqui deverá ser um forResult... q deverá atualizar a lista caso seja dado um OK.
//				startActivity(intent);				
//			}
//		});
		
		//TODO Implementar acesso a banco de dados para recuperar matérias cadastradas.
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DUMMY_SUBJECTS));
		
		list = getListView();
		list.setTextFilterEnabled(true);
		list.setOnItemLongClickListener(this);
	}
	
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// Quando um item é pressionado por um tempo, abre-se um diálogo de opções.
		return showDialog(EDIT_DELETE_DIALOG, null);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
		case EDIT_DELETE_DIALOG:
			builder.setTitle("Selecione a ação:");
			builder.setItems(DIALOG_OPTIONS, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
			    	// TODO verificar se é para editar ou deletar e executar a função correspondente.
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
	private final static String[] DUMMY_SUBJECTS = new String[]{"Matéria 1", "Matéria 2", "Matéria 3"};
	
	private ListView list;
}
