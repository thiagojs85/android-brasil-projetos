package org.android.brasil.projetos.escolarmobile.gui.turma;

import java.util.List;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.MateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaMateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;
import org.android.brasil.projetos.escolarmobile.gui.materia.ListaMaterias;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CadastroTurmas extends Activity {

	private static final int DIALOG_CANCELAR = 0;
	private Button ok, cancelar, adicionarMaterias;
	private EditText turma, descricao;
	private long idTurma = 0;
	private TurmaVO turmaVO;

	private Cursor c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.titulo_cadastro_turma);

		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);
		LinearLayout rl = (LinearLayout) findViewById(R.id.container);
		LayoutInflater layoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Essa parte pode ser controlada por um metodo que retorne qual o
		// layout a ser inserido
		// no layout padrão para cadastro
		rl.addView(layoutInflater
				.inflate(R.layout.cadastro_turmas, null, false));

		ok = (Button) findViewById(R.id.bt_ok);
		cancelar = (Button) findViewById(R.id.bt_cancelar);
		adicionarMaterias = (Button) findViewById(R.id.bt_cadastrar);
		turma = (EditText) findViewById(R.id.et_turma);
		descricao = (EditText) findViewById(R.id.et_descricao);

		adicionarMaterias.setText(R.string.adicionar_materias);
		idTurma = getIntent().getLongExtra(TurmaVO.TABLE_TURMA, 0);

		turmaVO = new TurmaVO(CadastroTurmas.this);

		if (idTurma > 0) {
			TurmaVO.open(CadastroTurmas.this);
			turmaVO = TurmaVO.consultarTurmaPorId(idTurma);
			TurmaVO.close();

			if (turmaVO != null) {
				turma.setText(turmaVO.getNome());
				descricao.setText(turmaVO.getDescricao());
			}
		}

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!validarTurma()) {
					return;
				}
				if (!salvarTurma()) {
					return;
				} else {

					// Verificar se a turma possui materias, se não houver,
					// mostrar o dialog abaixo.
					TurmaMateriaVO.open(CadastroTurmas.this);
					long[] ids = TurmaMateriaVO.getIdsMateriaPorTurma(idTurma);
					TurmaMateriaVO.close();
					if (ids == null || (ids != null && ids.length == 0)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								CadastroTurmas.this);

						builder.setMessage(R.string.salvar_turma_sem_materia)
								.setCancelable(false);
						builder.setPositiveButton(
								R.string.usar_materias_padrao,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										// Busca as matérias padrão do sistema
										MateriaVO.open(CadastroTurmas.this);
										List<Integer> idMaterias = MateriaVO
												.getMateriasPadrão();
										MateriaVO.close();

										// Inserir Matérias padrão.
										TurmaMateriaVO
												.open(CadastroTurmas.this);
										TurmaMateriaVO.adicionarMateriasPadrao(
												idTurma, idMaterias);
										TurmaMateriaVO.close();
										CadastroTurmas.this.finish();
									}
								});

						builder.setNegativeButton(R.string.adicionar_materias,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										// Abrir tela de cadastro de matérias.
										Intent i = new Intent(
												CadastroTurmas.this,
												ListaMaterias.class);
										i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
										startActivity(i);
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
					}else{
						CadastroTurmas.this.finish();
					}
				}
			}
		});

		cancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Invoca a caixa de diálogo e sai sem salvar nada.
				showDialog(DIALOG_CANCELAR);
			}
		});

		adicionarMaterias.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!validarTurma()) {
					return;
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CadastroTurmas.this);

					builder.setMessage(R.string.salvar_turma)
							.setCancelable(false)
							.setPositiveButton(R.string.sim,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											if (salvarTurma()) {
												Toast.makeText(
														CadastroTurmas.this,
														R.string.inserir_dados_successo,
														Toast.LENGTH_LONG)
														.show();

												Intent i = new Intent(
														CadastroTurmas.this,
														ListaMaterias.class);
												i.putExtra(TurmaVO.TABLE_TURMA,
														idTurma);
												startActivity(i);
											} else {
												Toast.makeText(
														CadastroTurmas.this,
														R.string.inserir_dados_erro,
														Toast.LENGTH_LONG)
														.show();
												CadastroTurmas.this.finish();
											}
										}
									})
							.setNegativeButton(R.string.nao,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}
			}
		});
	}

	private boolean validarTurma() {

		// Valida as informações antes de salvar no banco.
		if (turma.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroTurmas.this, R.string.erro_nome_invalido,
					Toast.LENGTH_LONG).show();
			return false;
		} else if (descricao.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroTurmas.this,
					R.string.erro_descricao_invalido, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private boolean salvarTurma() {

		turmaVO.setNome(turma.getText().toString().trim());
		turmaVO.setDescricao(descricao.getText().toString().trim());

		// Se não houver id, é uma nova entrada; caso contrário, é
		// atualização de um registro existente.
		TurmaVO.open(CadastroTurmas.this);
		if (idTurma <= 0) {
			if (TurmaVO.inserirTurma(turmaVO) > 0) {
				Toast.makeText(CadastroTurmas.this,
						R.string.inserir_dados_successo, Toast.LENGTH_LONG)
						.show();
				return true;
			} else {
				return false;
			}
		} else {
			turmaVO.setId(idTurma);
			boolean resultado = TurmaVO.atualizarTurma(turmaVO);
			TurmaVO.close();
			return resultado;
		}
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
							CadastroTurmas.this.finish();
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
		if (c != null) {
			c.close();
		}
	}
}
