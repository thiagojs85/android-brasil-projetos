package org.android.group.escolarmobile.app;

import java.sql.Date;

import org.android.group.escolarmobile.app.student.PresencaVO;
import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class ListaAlunos extends TelaListaBasica {

	private static final int DIALOG_DELETAR = 0;
	private static final String LISTA_ALUNOS = "lista_alunos";
	private long idDelete;
	private long idTurma;
	private long idMateria;
	private DatePicker datapicker;
	private boolean isMultiItensSelectable;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	isMultiItensSelectable = this.getIntent().getBooleanExtra("chamada", false);
    	Log.v(LISTA_ALUNOS, "Valor do isMultiItensSelectable: "+isMultiItensSelectable());
    	
    	super.onCreate(savedInstanceState);
        Button ibt = (Button)findViewById(R.id.add);

        if(isMultiItensSelectable()){
        	ibt.setText(R.string.fazer_chamada);
        	idTurma = this.getIntent().getLongExtra(DbAdapter.COLUMN_ID_TURMA, 0);
        	idMateria = this.getIntent().getLongExtra(DbAdapter.COLUMN_ID_MATERIA, 0);
            // Quando o usuário clica em um aluno da lista, exibe as notas do aluno.
            this.getListView().setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> context, View view, int position, long id) {
    				/*// TODO implementar a classe NotaAluno 
    				Intent i = new Intent(ListaAlunos.this, NotaAluno.class);
    				i.putExtra(DbAdapter.COLUMN_ID_TURMA, idTurma);
    				i.putExtra(DbAdapter.COLUMN_ID_MATERIA, idMateria);
    				i.putExtra(DbAdapter.COLUMN_ID_ALUNO, id);
    				startActivity(i);				*/
    			}
    		});

        }else{
        	ibt.setText(R.string.adicionar_aluno);//sobrescreve a string original do xml
        	//Quando o usuário clicar em um aluno da lista, fazer o que?Nada?
        }

    }
    
    
    @Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar true
    	return isMultiItensSelectable;
	}
    
    
	@Override
	public void onClick(View v) {
		
		if(isMultiItensSelectable()){
        	dialogFaltas();
        }else{
        	Intent i = new Intent(this, CadastroAluno.class);
        	i.putExtra(DbAdapter.COLUMN_ID_TURMA, idTurma);
        	startActivityForResult(i, ADD_ID);
        }
    	
	}


	@Override
	public Cursor getItensCursor() {
		
		// Se não houver id nos Extras, mostre todas as matérias existentes.
		if(idMateria < 1) {
			return mDbAdapter.consultarTodos(DbAdapter.TABLE_ALUNO, 
					new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		
		} else {
			return mDbAdapter.acessarAlunosPorMaterias(idMateria);
		}
	}
	
	@Override
	public void setActionOnEditItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
    	// Repassa o id do aluno selecionado para edição.		
    	Intent i = new Intent(this, CadastroAluno.class);
		i.putExtra(DbAdapter.COLUMN_ID, info.id);
    	startActivityForResult(i, EDIT_ID);
	}
	
	@Override
	public void setActionOnDeleteItem(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		idDelete = (new Long(info.id).longValue());
		showDialog(DIALOG_DELETAR);
	}
	
	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_DELETAR:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.dialog_delete).setCancelable(false);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mDbAdapter.removerAluno(idDelete);
						ListaAlunos.super.updateItens();
					}
				});
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				return builder.create();
			default:
				return null;
		}
	}
	
	private void dialogFaltas(){   	 
    	
    	final Dialog dialog = new Dialog(this);
    	
    	dialog.setContentView(R.layout.dialog_lancar_faltas);
    	dialog.setTitle(R.string.lancar_faltas);
    	
    	datapicker = (DatePicker) dialog.findViewById(R.id.datePicker);
    		
    	final EditText numeroFaltas = (EditText) dialog.findViewById(R.id.eNumeroFaltas);
    	
    	final Button lancarFaltas = (Button) dialog.findViewById(R.id.blancarFaltas);
    	final Button cancelar = (Button) dialog.findViewById(R.id.bCancelar);
    	
    	
    	
    	lancarFaltas.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Date date = new Date(datapicker.getYear() - 1900, datapicker.getMonth(), datapicker.getDayOfMonth());
				long[] ids = ListaAlunos.this.getListView().getCheckItemIds();
				PresencaVO presencaVO = new PresencaVO();
				presencaVO.setFalta(Integer.parseInt(numeroFaltas.getText().toString()));
				presencaVO.setData(date);
				for(long id : ids) {
					presencaVO.setIdAluno(id);
					mDbAdapter.inserirPresenca(presencaVO);
				}
				
				dialog.dismiss();
				Toast.makeText(ListaAlunos.this, R.string.lcto_faltas_sucesso, Toast.LENGTH_LONG).show();
			}
		});
    	
    	cancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	
	    	
    dialog.show();
    	
    	

	}

}
