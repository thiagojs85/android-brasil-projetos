package org.android.brasil.projetos.escolarmobile.gui.materia;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.MateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.ProfessorVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaMateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;

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

public class CadastroMateria extends Activity {

	private static final int DIALOG_CANCELAR = 0;
	private Button ok, cancelar, cadastrarAlunos;
	private EditText materia, descricao, horasAula;
	private Spinner sp_professor;
	private CheckBox padrao;
	private long idMateria;
	private long idTurma;
	private Cursor c;
	private MateriaVO materiaVO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.titulo_cadastro_materia);

		idMateria = getIntent().getLongExtra(MateriaVO.TABLE_MATERIA, 0);
		idTurma = getIntent().getLongExtra(TurmaVO.TABLE_TURMA, 0);

		Log.w(getTitle().toString(), "Valor do idMateria: " + idMateria);
		Log.w(getTitle().toString(), "Valor do idTurma: " + idTurma);

		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);
		LinearLayout rl = (LinearLayout) findViewById(R.id.container);
		LayoutInflater layoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rl.addView(layoutInflater.inflate(R.layout.cadastro_materias, null,
				false));

		ok = (Button) findViewById(R.id.bt_ok);
		cancelar = (Button) findViewById(R.id.bt_cancelar);
		cadastrarAlunos = (Button) findViewById(R.id.bt_cadastrar);
		materia = (EditText) findViewById(R.id.et_nome_materia);
		horasAula = (EditText) findViewById(R.id.et_horas_aula);
		descricao = (EditText) findViewById(R.id.et_descricao);
		sp_professor = (Spinner) findViewById(R.id.et_professor);
		padrao = (CheckBox) findViewById(R.id.materia_padrao);

		cadastrarAlunos.setVisibility(4);// ocultando o botão cadastrar

		cadastrarAlunos.setText(R.string.cadastrar_alunos);// sobrescrevendo a
															// string original
															// do botão
															// cadastrar

		ProfessorVO.open(CadastroMateria.this);
		c = ProfessorVO.consultarTodos(new String[] { ProfessorVO.COLUMN_ID,
				ProfessorVO.COLUMN_NOME });
		ProfessorVO.close();

		sp_professor.setAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, c,
				new String[] { ProfessorVO.COLUMN_NOME },
				new int[] { android.R.id.text1 }));

		materiaVO = new MateriaVO(CadastroMateria.this);

		if (idMateria > 0) {
			MateriaVO.open(CadastroMateria.this);
			materiaVO = MateriaVO.consultarMateriaPorId(idMateria);
			MateriaVO.close();
			
			if (materiaVO != null) {
				materia.setText(materiaVO.getNome());
				horasAula.setText(String.valueOf(materiaVO.getHoras()));
				descricao.setText(materiaVO.getDescricao());
				padrao.setChecked(materiaVO.isPadrao());

				for (int i = 0; i < sp_professor.getCount(); i++) {
					if (materiaVO.getIdProfessor() == sp_professor
							.getItemIdAtPosition(i)) {
						sp_professor.setSelection(i);
					}
				}
			} else {
				Toast.makeText(this,
						"Informações sobre a matéria não encontradas!",
						Toast.LENGTH_LONG).show();
				this.finish();
			}
		}

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!validarMateria()) {
					return;
				}

				materiaVO.setNome(materia.getText().toString().trim());
				materiaVO.setHoras(Integer.parseInt(horasAula.getText()
						.toString().trim()));
				materiaVO.setDescricao(descricao.getText().toString().trim());
				materiaVO.setIdProfessor(sp_professor.getSelectedItemId());
				materiaVO.setPadrao(padrao.isChecked());

				// Se não houver id, é uma nova entrada; caso contrário, é
				// atualização de um registro existente.
				if (idMateria <= 0) {
					MateriaVO.open(CadastroMateria.this);
					
					if (MateriaVO.inserirMateria(materiaVO) > 0) {
						MateriaVO.close();
						
						TurmaMateriaVO.open(CadastroMateria.this);
						
						if (TurmaMateriaVO.inserirRelacionamento(idTurma, materiaVO.getId())) {
							Toast.makeText(CadastroMateria.this,
									R.string.inserir_dados_successo,
									Toast.LENGTH_LONG).show();
							
							TurmaMateriaVO.close();
							
							CadastroMateria.this.finish();
						}else{
							TurmaMateriaVO.close();
						}
					}else{
						MateriaVO.close();
					}

				} else {
					materiaVO.setId(idMateria);
					MateriaVO.open(CadastroMateria.this);
					MateriaVO.atualizarMateria(materiaVO);
					MateriaVO.close();
					Toast.makeText(CadastroMateria.this,
							R.string.atualizar_dados_successo,
							Toast.LENGTH_LONG).show();
					CadastroMateria.this.finish();
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
				Toast.makeText(CadastroMateria.this,
						"Botão cadastro de alunos foi Pressionado!",
						Toast.LENGTH_SHORT).show();
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
			builder.setPositiveButton(R.string.sim,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CadastroMateria.this.finish();
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

	private boolean validarMateria() {
		// Valida as informações antes de salvar no banco.
		if (materia.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroMateria.this, R.string.erro_nome_invalido,
					Toast.LENGTH_LONG).show();
			return false;
		} else if (horasAula.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroMateria.this, R.string.erro_hora_invalida,
					Toast.LENGTH_LONG).show();
			return false;

		} else if (descricao.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroMateria.this,
					R.string.erro_descricao_invalido, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (materiaVO != null) {
			MateriaVO.close();
		}
		if (c != null) {
			c.close();
		}
	}
}
