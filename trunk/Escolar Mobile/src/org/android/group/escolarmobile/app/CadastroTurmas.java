package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.conn.DbAdapter;
import org.android.group.escolarmobile.turma.TurmaVO;
import org.group.dev.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CadastroTurmas extends Activity {

	private static final int DIALOG_CANCELAR = 0;
	private static final String CADASTRO_TURMAS	 = "cadastroturmas";//usado usado pra imprimir logs no logcat
	private TurmaVO turmaVO = null;
	private Button ok, cancelar, cadastrarMaterias;
	private EditText turma, descricao;
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
		rl.addView(layoutInflater.inflate(R.layout.cadastro_turmas, null, false));

		ok = (Button) findViewById(R.id.bt_ok);
		cancelar = (Button) findViewById(R.id.bt_cancelar);
		cadastrarMaterias = (Button) findViewById(R.id.bt_cadastrar);
		turma = (EditText) findViewById(R.id.et_turma);
		descricao = (EditText) findViewById(R.id.et_descricao);
		
		cadastrarMaterias.setText(R.string.cadastrar_materias);

		mDbAdapter = new DbAdapter(this).open();
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			//editId = bundle.getLong(DbAdapter.COLUMN_ID_TURMA);
			editId = bundle.getLong(DbAdapter.COLUMN_ID);
			
			//TurmaVO turmaVO = mDbAdapter.consultarTurma(editId);
			turmaVO = mDbAdapter.consultarTurma(editId);

			if (turmaVO != null) {
				turma.setText(turmaVO.getNome());
				descricao.setText(turmaVO.getDescricao());
			}
		} else {
			turmaVO = new TurmaVO();
		}

		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* @deprecated
				TurmaVO turmaVO = new TurmaVO();

				// Valida as informações antes de salvar no banco.
				if (turma.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroTurmas.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
					return;
				} else if (descricao.getText().toString().trim().length() < 1) {
					Toast.makeText(CadastroTurmas.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
					return;
				}

				turmaVO.setNome(turma.getText().toString().trim());
				turmaVO.setDescricao(descricao.getText().toString().trim());

				mDbAdapter = new DbAdapter(CadastroTurmas.this).open();

				boolean registroOk = false;

				// Se não houver id, é uma nova entrada; caso contrário, é
				// atualização de um registro existente.
				if (editId == -1) {
					if(mDbAdapter.inserirTurma(turmaVO) > -1) {
						registroOk = true;
					}
				} else {
					turmaVO.setId(editId);
					registroOk = mDbAdapter.atualizarTurma(turmaVO);
				}

				if (registroOk) {
					Toast.makeText(CadastroTurmas.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
					CadastroTurmas.this.finish();
				} else {
					Toast.makeText(CadastroTurmas.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
				}*/
				if(!salvarTurma()) {
					Toast.makeText(CadastroTurmas.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
					CadastroTurmas.this.finish();
				}
				
				Cursor c = mDbAdapter.acessarMateriasPorTurma(turmaVO.getId());
				
				if(c == null || (c != null && c.getCount() < 1)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(CadastroTurmas.this);
					
					builder.setMessage(R.string.salvar_turma_sem_materia).setCancelable(false);
					builder.setPositiveButton(R.string.usar_materias_padrao,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// Inserir Matérias padrão.
									mDbAdapter.cadastrarMateriasPadrao(turmaVO.getId());
									CadastroTurmas.this.finish();
								}
							});
					builder.setNegativeButton(R.string.cadastrar_materias,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// Abrir tela de cadastro de matérias.
									Intent i = new Intent(CadastroTurmas.this, CadastroMateria.class).putExtra(DbAdapter.COLUMN_ID_TURMA, turmaVO.getId());
									
									startActivity(i);
								}
							});
					AlertDialog alert = builder.create();
					alert.show();

					if(c != null) {
						c.close();
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

		cadastrarMaterias.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(CadastroTurmas.this);
				
				builder.setMessage(R.string.salvar_turma)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								/* @deprecated
								TurmaVO turmaVO = new TurmaVO();

								// Valida as informações antes de salvar no banco.
								if (turma.getText().toString().trim().length() < 1) {
									Toast.makeText(CadastroTurmas.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
									return;
								} else if (descricao.getText().toString().trim().length() < 1) {
									Toast.makeText(CadastroTurmas.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
									return;
								}

								turmaVO.setNome(turma.getText().toString().trim());
								turmaVO.setDescricao(descricao.getText().toString().trim());

								mDbAdapter = new DbAdapter(CadastroTurmas.this).open();

								boolean registroOk = false;
								
								long idDaTurma = -1;
								// Se não houver id, é uma nova entrada; caso contrário, é
								// atualização de um registro existente.
								if (editId == -1) {
									idDaTurma = mDbAdapter.inserirTurma(turmaVO);
									Log.w(CADASTRO_TURMAS, "Valor do Id da turma: " + idDaTurma);
									if ( idDaTurma > -1) {
										registroOk = true;
									}
								} else {
									turmaVO.setId(editId);
									registroOk = mDbAdapter.atualizarTurma(turmaVO);
								}

								if (registroOk) {
									Toast.makeText(CadastroTurmas.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
									CadastroTurmas.this.finish();
								} else {
									Toast.makeText(CadastroTurmas.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
								}
								*/
								
								if(salvarTurma()) {
									Toast.makeText(CadastroTurmas.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
									CadastroTurmas.this.finish();
								} else {
									Toast.makeText(CadastroTurmas.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
								}
								
								//Intent i = new Intent(CadastroTurmas.this, CadastroMateria.class).putExtra(DbAdapter.COLUMN_ID_TURMA, idDaTurma);
								Intent i = new Intent(CadastroTurmas.this, CadastroMateria.class).putExtra(DbAdapter.COLUMN_ID_TURMA, turmaVO.getId());
								
								startActivity(i);
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
			}
		});
	}
	
	private boolean salvarTurma() {
		//TurmaVO turmaVO = new TurmaVO();

		// Valida as informações antes de salvar no banco.
		if (turma.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroTurmas.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
			return false;
		} else if (descricao.getText().toString().trim().length() < 1) {
			Toast.makeText(CadastroTurmas.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
			return false;
		}

		turmaVO.setNome(turma.getText().toString().trim());
		turmaVO.setDescricao(descricao.getText().toString().trim());

		//mDbAdapter = new DbAdapter(CadastroTurmas.this).open();

		boolean registroOk = false;

		Log.v(CADASTRO_TURMAS, "Valor do ID da turma antes do IF: " + turmaVO.getId());
		
		// Se não houver id, é uma nova entrada; caso contrário, é
		// atualização de um registro existente.
		//if (editId == -1) {
		if(turmaVO.getId() == 0) {
			turmaVO.setId(mDbAdapter.inserirTurma(turmaVO));
			Log.v(CADASTRO_TURMAS, "Valor do ID da turma antes dentro do IF: " + turmaVO.getId());
			if(turmaVO.getId() > -1) {
				registroOk = true;
			}
		} else {
			//turmaVO.setId(editId);
			Log.v(CADASTRO_TURMAS, "Valor do ID da turma no ELSE: " + turmaVO.getId());
			registroOk = mDbAdapter.atualizarTurma(turmaVO);
		}

		/*
		if (registroOk) {
			Toast.makeText(CadastroTurmas.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
			CadastroTurmas.this.finish();
		} else {
			Toast.makeText(CadastroTurmas.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
		}
		*/
		return registroOk;
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
		if (mDbAdapter != null) {
			mDbAdapter.close();
		}
	}
}
