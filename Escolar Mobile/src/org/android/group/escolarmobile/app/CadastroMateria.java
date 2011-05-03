package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.app.subject.MateriaVO;
import org.android.group.escolarmobile.conn.DbAdapter;
import org.group.dev.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CadastroMateria extends Activity{
	
	private static final int DIALOG_CANCELAR = 0;
	private static final String CADASTRO_MATERIA	 = "cadastromateria";//usado usado pra imprimir logs no logcat
	private Button ok, cancelar, cadastrarAlunos;
	//private EditText turma, materia, horasAula, descricao;
	private EditText materia, descricao, horasAula;
	private Spinner professor;
	private CheckBox padrao;
	private long editId = -1;
	private DbAdapter mDbAdapter = null;
	private long[] idTurmas;

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
		//turma = (EditText) findViewById(R.id.et_turma);
		materia = (EditText) findViewById(R.id.et_nome_materia);
		horasAula = (EditText) findViewById(R.id.et_horas_aula);
		descricao = (EditText) findViewById(R.id.et_descricao);
		professor = (Spinner) findViewById(R.id.et_professor);
		padrao = (CheckBox) findViewById(R.id.materia_padrao);
		
		cadastrarAlunos.setVisibility(4);//ocultando o botão cadastrar
		
		cadastrarAlunos.setText(R.string.cadastrar_alunos);//sobrescrevendo a string original do botão cadastrar

		mDbAdapter = new DbAdapter(this).open();
		Cursor c = mDbAdapter.consultarTodos(DbAdapter.TABLE_PROFESSOR, 
				new String[]{DbAdapter.COLUMN_ID, DbAdapter.COLUMN_NOME});
		
		professor.setAdapter(new SimpleCursorAdapter(this, 
				android.R.layout.simple_spinner_item,
				c,
				new String[]{DbAdapter.COLUMN_NOME},
				new int[]{android.R.id.text1}));
		
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			editId = bundle.getLong(DbAdapter.COLUMN_ID);
			Log.w(CADASTRO_MATERIA, "Valor do Id vindo do bundle: " + editId);
			if(editId != 0) {			
				MateriaVO materiaVO = mDbAdapter.consultarMateria(editId);
				if (materiaVO != null) {
					idTurmas = materiaVO.getIdTurmas();
					materia.setText(materiaVO.getNome());
					horasAula.setText(materiaVO.getNome());
					descricao.setText(materiaVO.getDescricao());
					padrao.setChecked(materiaVO.getPadrao().compareToIgnoreCase("S") == 0);
					
					for(int i = 0; i < professor.getCount(); i++) {
						if(materiaVO.getIdProfessor() == professor.getItemIdAtPosition(i)) {
							professor.setSelection(i);
						}
					}
				} else {
					Toast.makeText(this, "Informações sobre a matéria não encontradas!", Toast.LENGTH_LONG).show();
					this.finish();
				}
			} else {
				idTurmas = new long[]{bundle.getLong(DbAdapter.COLUMN_ID_TURMA)};
			}
		}

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MateriaVO materiaVo = new MateriaVO();

				// Valida as informações antes de salvar no banco.
				if (materia.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroMateria.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
					return;
				}else if (horasAula.getText().toString().trim().length() < 1) {
						Toast.makeText(CadastroMateria.this, R.string.error_hour_invalid, Toast.LENGTH_LONG).show();
					return;
				
				} else if (descricao.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroMateria.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
					return;
				}

				materiaVo.setIdTurmas(idTurmas);
				materiaVo.setNome(materia.getText().toString().trim());
				materiaVo.setHoras(Integer.parseInt(horasAula.getText().toString().trim()));
				materiaVo.setDescricao(descricao.getText().toString().trim());
				materiaVo.setIdProfessor(professor.getSelectedItemId());
				materiaVo.setPadrao(padrao.isChecked() ? "S" : "N");

				mDbAdapter = new DbAdapter(CadastroMateria.this).open();

				boolean registroOk = false;

				// Se não houver id, é uma nova entrada; caso contrário, é
				// atualização de um registro existente.
				if (editId < 1) {
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
				// TODO: Chamar a tela de alunos
				Toast.makeText(CadastroMateria.this, "Botão cadastro de alunos foi Pressionado!", Toast.LENGTH_SHORT)
						.show();
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
