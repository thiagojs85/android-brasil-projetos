package org.android.brasil.projetos.escolarmobile.gui.professor;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.dao.ProfessorVO;

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
public class CadastroProfessor extends Activity implements OnClickListener {

	private static final int DIALOG_CANCELAR = 0;
	private Button botaoOk, botaoCancelar, botaoCadastrar;
	private EditText nome, login, senha;
	private long idProfessor = 0;
	private ProfessorVO professorVO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setTitle(R.string.titulo_cadastro_professor);

		// Layout padrão para cadastros
		setContentView(R.layout.base_cadastro);
		LinearLayout rl = (LinearLayout) findViewById(R.id.container);

		LayoutInflater layoutInflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rl.addView(layoutInflater.inflate(R.layout.cadastro_professor, null,
				false));

		// Recupera os componentes da Tela
		recuperaComponentesTela();

		// Recupera a lista de parametros enviados pela tela de seleção de
		// professores
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			// Recupera informações para edição
			recuperaInformacoesParaEdicao(bundle);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (professorVO != null) {
			ProfessorVO.close();
		}
	}

	public void onClick(View v) {

		// Botão "OK" acionado
		if (v.getId() == R.id.bt_ok) {

			// Executa as ações referente ao Botão "OK"
			executaAcoesBotaoOK();
		}
		// Botão "CANCELAR" acionado
		else if (v.getId() == R.id.bt_cancelar) {

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
	 *            Parametros recebidos
	 */
	private void recuperaInformacoesParaEdicao(Bundle bundle) {

		professorVO = new ProfessorVO(CadastroProfessor.this);
		// Recupera o Parametro ID
		idProfessor = bundle.getLong(ProfessorVO.TABLE_PROFESSOR, 0);
		ProfessorVO.open(CadastroProfessor.this);
		professorVO = ProfessorVO.consultarProfessorPorId(idProfessor);
		ProfessorVO.close();

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
		botaoCadastrar.setVisibility(4); // deixa o botão cadastrar invisível

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
		if (!validarProfessor()) {
			return;
		}

		if(professorVO == null){
			professorVO = new ProfessorVO(CadastroProfessor.this);
		}
		// Preenche o VO com os campos da tela
		professorVO.setNome(nome.getText().toString().trim());
		professorVO.setLogin(login.getText().toString().trim());
		professorVO.setSenha(senha.getText().toString().trim());

		// Indica se o registro foi salvo com sucesso
		boolean registroOk = false;

		ProfessorVO.open(CadastroProfessor.this);
		// Verifica se o Registro está sendo criado ou atualizado
		if (idProfessor <= 0) {
			if (ProfessorVO.inserirProfessor(professorVO) > 0) {
				registroOk = true;
			}
		} else {
			professorVO.setId(idProfessor);
			registroOk = ProfessorVO.atualizarProfessor(professorVO);
		}
		ProfessorVO.close();
		
		// Verifica se o registro foi inserido ao atualizado com sucesso
		if (registroOk) {
			Toast.makeText(CadastroProfessor.this,
					R.string.inserir_dados_successo, Toast.LENGTH_LONG).show();
			CadastroProfessor.this.finish();
		} else {
			Toast.makeText(CadastroProfessor.this, R.string.inserir_dados_erro,
					Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Valida os campos de preenchimento obrigatório
	 * 
	 * @author Diego
	 * @since Jan 27, 2011
	 */
	private boolean validarProfessor() {

		if (nome.getText().toString().trim().equals("")) {
			Toast.makeText(CadastroProfessor.this, R.string.erro_nome_invalido,
					Toast.LENGTH_LONG).show();
			return false;
		}
		if (login.getText().toString().equals("")) {
			Toast.makeText(CadastroProfessor.this,
					R.string.erro_descricao_invalido, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	protected Dialog onCreateDialog(int id, Bundle args) {

		switch (id) {
		// Dialog exibido ao clicar no botão cancelar
		case DIALOG_CANCELAR:

			// Cria o Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(
					CadastroProfessor.this);
			builder.setMessage(R.string.dialog_cancel).setCancelable(false);

			// Opção Positiva
			builder.setPositiveButton(R.string.sim,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							CadastroProfessor.this.finish();
						}
					});

			// Opção Negativa
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

}
