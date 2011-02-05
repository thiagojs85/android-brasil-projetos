package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.app.subject.MateriaVO;
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

public class CadastroMateria extends Activity{
	
	private static final int DIALOG_CANCELAR = 0;
	private Button ok, cancelar, cadastrarAlunos;
	private EditText materia, horasAula, descricao;
	private long editId = -1;
	private DbAdapter mDbAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);
		LinearLayout rl = (LinearLayout) findViewById(R.id.container);
		LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Essa parte pode ser controlada por um metodo que retorne qual o
		// layout a ser inserido
		// no layout padrão para cadastro
		rl.addView(layoutInflater.inflate(R.layout.cadastro_materias, null, false));

		ok = (Button) findViewById(R.id.bt_ok);
		cancelar = (Button) findViewById(R.id.bt_cancelar);
		cadastrarAlunos = (Button) findViewById(R.id.bt_cadastrar);
		materia = (EditText) findViewById(R.id.et_turma);
		horasAula = (EditText) findViewById(R.id.et_horas_aula);
		descricao = (EditText) findViewById(R.id.et_descricao);
		
		cadastrarAlunos.setVisibility(4);//ocultando o bot�o cadastrar
		
		cadastrarAlunos.setText(R.string.cadastrar_alunos);//sobrescrevendo a string original do botão cadastrar

		mDbAdapter = new DbAdapter(this).open();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			editId = bundle.getLong(DbAdapter.COLUMN_ID);
			MateriaVO materiaVO = mDbAdapter.consultarMateria(editId);

			if (materiaVO != null) {
				materia.setText(materiaVO.getNome());
				descricao.setText(materiaVO.getDescricao());
			}
		}

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MateriaVO materiaVo = new MateriaVO();

				// Valida as informações antes de salvar no banco.
				if (materia.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroMateria.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
					return;
				} else if (descricao.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroMateria.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
					return;
				}

				materiaVo.setNome(materia.getText().toString().trim());
				materiaVo.setDescricao(descricao.getText().toString().trim());

				mDbAdapter = new DbAdapter(CadastroMateria.this).open();

				boolean registroOk = false;

				// Se não houver id, é uma nova entrada; caso contrário, é
				// atualização de um registro existente.
				if (editId == -1) {
					if (mDbAdapter.inserirMateria(materiaVo) > -1) {
						registroOk = true;
					}
				} else {
					materiaVo.setId(editId);
					registroOk = mDbAdapter.atualizarMateria(materiaVo);
				}

				if (registroOk) {
					Toast.makeText(CadastroMateria.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
					CadastroMateria.this.finish();
				} else {
					Toast.makeText(CadastroMateria.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
				}
			}
		});

		cancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Invoca a caixa de diálogo e sai sem salvar nada.
				showDialog(DIALOG_CANCELAR);
			}
		});

		cadastrarAlunos.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO: Chamar a tela de cadastro de matérias
				Toast.makeText(CadastroMateria.this, "Botão cadastro de matérias Pressionado!", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	/**
	 * Fun��o que cria os di�logos utilizados nesta activity.
	 * 
	 * @param id
	 *            identifica��o do di�logo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CANCELAR:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_cancel).setCancelable(false);
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					CadastroMateria.this.finish();
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
