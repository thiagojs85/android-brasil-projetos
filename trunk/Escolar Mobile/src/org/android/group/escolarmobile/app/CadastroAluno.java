package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.app.student.AlunoVO;
import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CadastroAluno extends Activity{
	
	private static final int DIALOG_CANCELAR = 0;
	private Button ok, cancelar, cadastrar;
	private EditText nomeAluno, idade, registroMatricula;
	private long editId = -1;
	private long idTurma;
	private DbAdapter mDbAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);
		LinearLayout rl = (LinearLayout) findViewById(R.id.container);
		LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Essa parte pode ser controlada por um metodo que retorne qual o
		// layout a ser inserido no layout padrão para cadastro
		rl.addView(layoutInflater.inflate(R.layout.cadastro_alunos, null, false));
		
		idTurma = this.getIntent().getLongExtra(DbAdapter.COLUMN_ID_TURMA, 0);
		editId = this.getIntent().getLongExtra(DbAdapter.COLUMN_ID, -1);

		ok = (Button) findViewById(R.id.bt_ok);
		cancelar = (Button) findViewById(R.id.bt_cancelar);
		cadastrar = (Button) findViewById(R.id.bt_cadastrar);
		nomeAluno = (EditText) findViewById(R.id.et_nome_aluno);
		idade = (EditText) findViewById(R.id.et_idade_aluno);
		registroMatricula = (EditText) findViewById(R.id.et_registro_matricula);
		
		if(editId > -1) {
			mDbAdapter = new DbAdapter(this).open();
			AlunoVO aluno = mDbAdapter.consultarAluno(editId);
			
			if(aluno != null) {
				nomeAluno.setText(aluno.getNome());
				idade.setText(aluno.getDataNascimento());
				registroMatricula.setText(aluno.getRegistro());
				idTurma = aluno.getIdTurma();
			}
			mDbAdapter.close();
		}
		
		cadastrar.setVisibility(4); //deixa o botão cadastrar invisível

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AlunoVO aluno = new AlunoVO();
				
				// Valida as informações antes de salvar no banco.
				if(nomeAluno.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroAluno.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
					return;
				} else if(registroMatricula.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroAluno.this, R.string.error_rm_invalid, Toast.LENGTH_LONG).show();
					return;
				} else if(idTurma == 0) {
					Toast.makeText(CadastroAluno.this, R.string.error_turma_not_found, Toast.LENGTH_LONG).show();
					return;
				}
				
				aluno.setNome(nomeAluno.getText().toString().trim());
				aluno.setRegistro(registroMatricula.getText().toString().trim());
				aluno.setIdTurma(idTurma);
				aluno.setDataNascimento(idade.getText().toString().trim());
				
				mDbAdapter = new DbAdapter(CadastroAluno.this).open();
				
				boolean registroOk = false;
				
				if(editId == -1) {
					registroOk = mDbAdapter.inserirAluno(aluno) > -1;
				} else {
					aluno.setId(editId);
					registroOk = mDbAdapter.atualizarAluno(aluno);
				}
				
				if(registroOk) {
					Toast.makeText(CadastroAluno.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
					CadastroAluno.this.finish();
				} else {
					Toast.makeText(CadastroAluno.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
				}
			}
		});

		cancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Invoca a caixa de diálogo e sai sem salvar nada.
				showDialog(DIALOG_CANCELAR);
			}
		});

		
	}

	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id
	 *            identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CANCELAR:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_cancel).setCancelable(false);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					CadastroAluno.this.finish();
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
		if (mDbAdapter != null) {
			mDbAdapter.close();
		}
	}

}
