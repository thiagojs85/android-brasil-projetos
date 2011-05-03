package org.android.group.escolarmobile.app;

import org.android.group.escolarmobile.app.teacher.ProfessorVO;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author Diego
 *
 */
public class CadastroProfessor extends Activity implements OnClickListener{

	private static final int DIALOG_CANCELAR = 0;
	private Button botaoOk, botaoCancelar, botaoCadastrar;
	private EditText nome, login, senha;
	private long idProfessor = -1;
	private DbAdapter mDbAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);
		LinearLayout rl = (LinearLayout) findViewById(R.id.container);
		
		LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rl.addView(layoutInflater.inflate(R.layout.cadastro_professor, null, false));

		// Recupera os componentes da Tela
		recuperaComponentesTela();

		// Recupera a lista de parametros enviados pela tela de seleção de professores
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			
			// Recupera informações para edição
			recuperaInformacoesParaEdicao(bundle);
		}
		
	}

	protected Dialog onCreateDialog(int id, Bundle args) {
		
		switch (id) {
			// Dialog exibido ao clicar no botão cancelar
			case DIALOG_CANCELAR:
				
				// Cria o Dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(CadastroProfessor.this);
				builder.setMessage(R.string.dialog_cancel).setCancelable(false);
				
				// Opção Positiva
				builder.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CadastroProfessor.this.finish();
						}
					}
				);
				
				// Opção Negativa
				builder.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}
				);
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

	
	public void onClick(View v) {
		
		// Botão "OK" acionado
		if(v.getId() == R.id.bt_ok){
			
			// Executa as ações referente ao Botão "OK"
			executaAcoesBotaoOK();
		}
		// Botão "CANCELAR" acionado
		else if(v.getId() == R.id.bt_cancelar){
			
			// Executa as ações referente ao Botão "CANCELAR"
			executaAcoesBotaoCancelar();
		}
	}
	
	/**
	 * Recupera informações para edição
	 *
	 * @author Diego
	 * @since Jan 27, 2011
	 * @param bundle
	 * 	Parametros recebidos
	 */
	private void recuperaInformacoesParaEdicao(Bundle bundle) {
		
		// Abre a conexão com o banco de dados
		mDbAdapter = new DbAdapter(CadastroProfessor.this).open();
		
		// Recupera o Parametro ID
		idProfessor = bundle.getLong(DbAdapter.COLUMN_ID,0);
		
		// Recupera o professor de acordo com o ID informado
		ProfessorVO professorVO = mDbAdapter.consultarProfessor(idProfessor);

		// Adiciona aos componentes os Dados do Professor
		if (professorVO != null) {
			nome.setText(professorVO.getNome());
			login.setText(professorVO.getLogin());
			senha.setText(professorVO.getSenha());
		}
	}

	/**
	 * Recupera os componentes da Tela
	 *
	 * @author Diego
	 * @since Jan 27, 2011
	 */
	private void recuperaComponentesTela() {
		botaoOk = (Button) findViewById(R.id.bt_ok);
		botaoCancelar = (Button) findViewById(R.id.bt_cancelar);
		botaoCadastrar = (Button) findViewById(R.id.bt_cadastrar);
		botaoCadastrar.setVisibility(4); //deixa o botão cadastrar invisível
		
		// Registra um evento de click para os botões
		botaoOk.setOnClickListener(this);
		botaoCancelar.setOnClickListener(this);

		nome = (EditText) findViewById(R.id.et_nome_professor);
		login = (EditText) findViewById(R.id.et_login_professor);
		senha = (EditText) findViewById(R.id.et_senha_professor);
	}

	/**
	 * Executa as ações referente ao Botão "CANCELAR"
	 *
	 * @author Diego
	 * @since Jan 27, 2011
	 */
	private void executaAcoesBotaoCancelar() {
		// Exibe um Dialog de confirmação
		showDialog(DIALOG_CANCELAR);
	}

	/**
	 * Executa as ações referentes ao Botão "OK"
	 *
	 * @author Diego
	 * @since Jan 27, 2011
	 */
	private void executaAcoesBotaoOK() {
		
		// Valida os campos de preenchimento obrigatório
		if(!validaCamposObrigatorios()){
			return;
		}

		// Preenche o VO com os campos da tela
		ProfessorVO professorVO = new ProfessorVO();
		professorVO.setNome(nome.getText().toString().trim());
		professorVO.setLogin(login.getText().toString().trim());
		professorVO.setSenha(senha.getText().toString().trim());

		// Abre a conexão com o banco de dados
		mDbAdapter = new DbAdapter(CadastroProfessor.this).open();

		// Indica se o registro foi salvo com sucesso
		boolean registroOk = false;

		// Verifica se o Registro está sendo criado ou atualizado
		if (idProfessor == -1) {
			if (mDbAdapter.inserirProfessor(professorVO) > -1) {
				registroOk = true;
			}
		} else {
			professorVO.setId(idProfessor);
			registroOk = mDbAdapter.atualizarProfessor(professorVO);
		}

		// Verifica se o registro foi inserido ao atualizado com sucesso
		if (registroOk) {
			Toast.makeText(CadastroProfessor.this, R.string.data_inserted_success, Toast.LENGTH_LONG).show();
			CadastroProfessor.this.finish();
		} else {
			Toast.makeText(CadastroProfessor.this, R.string.data_inserted_error, Toast.LENGTH_LONG).show();
		}
		
	}

	/**
	 * Valida os campos de preenchimento obrigatório
	 *
	 * @author Diego
	 * @since Jan 27, 2011
	 */
	private boolean validaCamposObrigatorios() {
		
		if (nome.getText().toString().trim().equals("")) {
			Toast.makeText(CadastroProfessor.this, R.string.error_name_invalid, Toast.LENGTH_LONG).show();
			return false;
		} 
		if (login.getText().toString().equals("")) {
			Toast.makeText(CadastroProfessor.this, R.string.error_description_invalid, Toast.LENGTH_LONG).show();
			return false;
		} 
		return true;
	}
}
