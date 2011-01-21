package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.android.group.escolarmobile.turma.TurmaVO;
import org.group.dev.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CadastroTurmas extends Activity {
	
	private static final int DIALOG_CANCELAR = 0;
	private Button ok, cancelar, cadastrarMaterias;
	private EditText materia, descricao;
	private long editId = -1;
	private DbAdapter mDbAdapter = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);
        ok = (Button) findViewById(R.id.bt_ok);
        cancelar = (Button) findViewById(R.id.bt_cancelar);
        cadastrarMaterias = (Button) findViewById(R.id.bt_cadastrar_turmas);
		materia = (EditText) findViewById(R.id.et_nome_materia);
		descricao = (EditText) findViewById(R.id.et_descricao);
		
		mDbAdapter = new DbAdapter(this).open();
		Bundle bundle = getIntent().getExtras();
		
        if(bundle != null) {        
        	editId = bundle.getLong(DbAdapter.COLUMN_ID);
        	TurmaVO turmaVO = mDbAdapter.consultarTurma(editId);
        	
        	if(turmaVO != null) {
        		materia.setText(turmaVO.getNome());
        		descricao.setText(turmaVO.getDescricao());
        	}
        }
        
        
        
        ok.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		TurmaVO turmaVO = new TurmaVO();
        		
        		// Valida as informações antes de salvar no banco.
        		if(materia.getText().toString().trim().length() < 1) {
        			Toast.makeText(CadastroTurmas.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
        			return;
        		} else if(descricao.getText().toString().trim().length() < 1) {
        			Toast.makeText(CadastroTurmas.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
        			return;
        		}
        		
        		turmaVO.setNome(materia.getText().toString().trim());
        		turmaVO.setDescricao(descricao.getText().toString().trim());
        		
        		mDbAdapter = new DbAdapter(CadastroTurmas.this).open();
        		
        		boolean registroOk = false;
        		
        		// Se não houver id, é uma nova entrada; caso contrário, é atualização de um registro existente.
        		if(editId == -1) {
        			if(mDbAdapter.inserirTurma(turmaVO) > -1) {
        				registroOk = true;
        			}	
        		} else {
        			turmaVO.setId(editId);
        			registroOk = mDbAdapter.atualizarTurma(turmaVO);
        		}
        		
        		if(registroOk) {
        			Toast.makeText(CadastroTurmas.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
        			CadastroTurmas.this.finish();
        		} else {
        			Toast.makeText(CadastroTurmas.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
        		}
			}
        });
        
        cancelar.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		// Invoca a caixa de diálogo e sai sem salvar nada.
        		showDialog(DIALOG_CANCELAR);
			}
        });
	
        cadastrarMaterias.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		//TODO: Chamar a tela de cadastro de matÃ©rias
        		Toast.makeText(CadastroTurmas.this, "BotÃ£o cadastro de matÃ©rias Pressionado!", Toast.LENGTH_SHORT).show();
			}
        });
	}
	
	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_CANCELAR:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.dialog_cancel).setCancelable(false);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						CadastroTurmas.this.finish();
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mDbAdapter != null) {
			mDbAdapter.close();
		}
	}
}
