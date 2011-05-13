package org.android.brasil.projetos.escolarmobile.gui.aluno;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.AlunoVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;

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

public class CadastroAluno extends Activity {

	private static final int DIALOG_CANCELAR = 0;
	private Button ok, cancelar, cadastrar;
	private EditText nomeAluno, idade, registroMatricula;
	private long idAluno;
	private long idTurma;
	private AlunoVO alunoVO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.titulo_cadastro_aluno);

		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);

		LinearLayout rl = (LinearLayout) findViewById(R.id.container);
		LayoutInflater layoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		rl.addView(layoutInflater
				.inflate(R.layout.cadastro_alunos, null, false));

		idTurma = this.getIntent().getLongExtra(TurmaVO.TABLE_TURMA, 0);
		idAluno = this.getIntent().getLongExtra(AlunoVO.TABLE_ALUNO, 0);

		ok = (Button) findViewById(R.id.bt_ok);
		cancelar = (Button) findViewById(R.id.bt_cancelar);
		cadastrar = (Button) findViewById(R.id.bt_cadastrar);
		nomeAluno = (EditText) findViewById(R.id.et_nome_aluno);
		idade = (EditText) findViewById(R.id.et_idade_aluno);
		registroMatricula = (EditText) findViewById(R.id.et_registro_matricula);

		alunoVO = new AlunoVO(CadastroAluno.this);
		if (idAluno > 0) {
			AlunoVO.open(CadastroAluno.this);
			alunoVO = AlunoVO.consultarAluno(idAluno);
			AlunoVO.close();
			
			if (alunoVO != null) {
				nomeAluno.setText(alunoVO.getNome());
				idade.setText(alunoVO.getDataNascimento());
				registroMatricula.setText(alunoVO.getRegistro());
				idTurma = alunoVO.getIdTurma();
			}
		}

		cadastrar.setVisibility(4); // deixa o botão cadastrar invisível

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!validarAluno()) {
					return;
				}

				alunoVO.setNome(nomeAluno.getText().toString().trim());
				alunoVO.setRegistro(registroMatricula.getText().toString()
						.trim());
				alunoVO.setIdTurma(idTurma);
				alunoVO.setDataNascimento(idade.getText().toString().trim());

				boolean registroOk = false;
				
				AlunoVO.open(CadastroAluno.this);
				
				if (idAluno <= 0) {
					registroOk = AlunoVO.inserirAluno(alunoVO) >= 0;
				} else {
					alunoVO.setId(idAluno);
					registroOk = AlunoVO.atualizarAluno(alunoVO);
				}
				AlunoVO.close();
				
				if (registroOk) {
					Toast.makeText(CadastroAluno.this,
							R.string.inserir_dados_successo, Toast.LENGTH_LONG)
							.show();
					CadastroAluno.this.finish();
				} else {
					Toast.makeText(CadastroAluno.this,
							R.string.inserir_dados_erro, Toast.LENGTH_LONG)
							.show();
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

	private boolean validarAluno() {
		// Valida as informações antes de salvar no banco.
		if (nomeAluno.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroAluno.this, R.string.erro_nome_invalido,
					Toast.LENGTH_LONG).show();
			return false;
		} else if (registroMatricula.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroAluno.this, R.string.erro_rm_invalido,
					Toast.LENGTH_LONG).show();
			return false;
		} else if (idTurma == 0) {
			Toast.makeText(CadastroAluno.this, R.string.erro_turma_invalida,
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
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
			builder.setPositiveButton(R.string.sim,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CadastroAluno.this.finish();
						}
					});
			builder.setNegativeButton(R.string.nao,
					new DialogInterface.OnClickListener() {
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
		if (alunoVO != null) {
			alunoVO.close();
		}

	}
}
