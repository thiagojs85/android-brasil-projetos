package org.android.brasil.projetos.escolarmobile.gui.materia;

import org.android.brasil.projetos.escolarmobile.R;
import org.android.brasil.projetos.escolarmobile.base.TelaListaBasica;
import org.android.brasil.projetos.escolarmobile.dao.MateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaMateriaVO;
import org.android.brasil.projetos.escolarmobile.dao.TurmaVO;
import org.android.brasil.projetos.escolarmobile.gui.aluno.ListaAlunos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Classe responsavel por listar as materias cadastradas e adicionar mais se
 * necessário
 * 
 * @author Neto
 */
public class ListaMaterias extends TelaListaBasica {

	protected static final int LARCAR_NOTAS_ID = DELETE_ID + 1;
	protected static final int FAZER_CHAMADA_ID = LARCAR_NOTAS_ID + 1;
	private static final int DIALOG_DELETAR = 0;
	private long idMateria;
	private long idTurma;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// idTurma deve ser setado antes de se chamar o super!
		idTurma = this.getIntent().getLongExtra(TurmaVO.TABLE_TURMA, 0);
		super.onCreate(savedInstanceState);

		Log.w(getTitle().toString(), "Valor do idTurma: " + idTurma);

		Button ibt = (Button) findViewById(R.id.add);
		// sobrescreve a string original do xml
		ibt.setText(R.string.adicionar_materias);

		// Quando o usuário clica em uma das matérias da lista, exibe a lista de
		// alunos para chamada.
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> context, View view,
					int position, long id) {
				Intent i = new Intent(ListaMaterias.this, ListaAlunos.class);
				i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
				i.putExtra(MateriaVO.TABLE_MATERIA, id);
				startActivity(i);
			}
		});
	}

	@Override
	protected boolean isMultiItensSelectable() {
		// Caso queira que os itens da lista sejam selecionaveis, fazer retornar
		// true
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		menu.add(Menu.NONE, LARCAR_NOTAS_ID, 0, R.string.lancar_notas);
		menu.add(Menu.NONE, FAZER_CHAMADA_ID, 0, R.string.fazer_chamada);

		super.onCreateContextMenu(menu, v, menuInfo);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case LARCAR_NOTAS_ID:
			setActionOnLancarNotaItem(item);
			break;
		case FAZER_CHAMADA_ID:
			setActionOnChamadaItem(item);
			break;
		}
		return super.onContextItemSelected(item);

	}

	// Ação a ser executada quando clicar no botão superior da lista!
	@Override
	public void onClick(View v) {

		Intent i = new Intent(this, CadastroMateria.class);
		i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
		startActivityForResult(i, ADD_ID);
	}

	@Override
	public Cursor getItensCursor() {

		// Se não houver id nos Extras, mostre todas as matérias existentes.
		if (idTurma <= 0) {
			return null;
		} else {
			TurmaMateriaVO.open(ListaMaterias.this);
			Cursor c = TurmaMateriaVO.acessarMateriasPorTurma(idTurma);
			TurmaMateriaVO.close();
			return c;
		}
	}

	@Override
	protected String[] getItensOfLine() {
		return new String[] { MateriaVO.COLUMN_NOME, MateriaVO.COLUMN_DESCRICAO };

	}

	@Override
	public void setActionOnEditItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		// Repassa o id da linha selecionada para a tela de edição
		Intent i = new Intent(this, CadastroMateria.class);
		i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
		Log.v(getTitle().toString(), "idTurma: " + idTurma);

		i.putExtra(MateriaVO.TABLE_MATERIA, info.id);
		Log.v(getTitle().toString(), "idMateria: " + info.id);

		startActivityForResult(i, EDIT_ID);
	}

	@Override
	public void setActionOnDeleteItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		idMateria = (new Long(info.id).longValue());
		showDialog(DIALOG_DELETAR);
	}

	public void setActionOnLancarNotaItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		idMateria = (new Long(info.id).longValue());
		// TODO Depois que criar a classe NotasMateria tem que descomentar e
		// testar aqui!
		// Intent i = new Intent(ListaMaterias.this,
		// NotasMateria.class).putExtra(DbAdapter.COLUMN_ID_ALUNO, idMateria);
		// startActivity(i);
		Toast.makeText(ListaMaterias.this,
				"Lançar notas dos alunos ainda não implementado!",
				Toast.LENGTH_LONG).show();
	}

	public void setActionOnChamadaItem(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		idMateria = (new Long(info.id).longValue());
		Intent i = new Intent(ListaMaterias.this, ListaAlunos.class);
		i.putExtra("chamada", true);
		i.putExtra(TurmaVO.TABLE_TURMA, idTurma);
		i.putExtra(MateriaVO.TABLE_MATERIA, idMateria);
		startActivity(i);
	}

	/**
	 * Função que cria os diálogos utilizados nesta activity.
	 * 
	 * @param id
	 *            identificação do diálogo que deve ser criado.
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DELETAR:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.dialog_delete).setCancelable(false);
			builder.setPositiveButton(R.string.sim,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							TurmaMateriaVO.open(ListaMaterias.this);
							TurmaMateriaVO.removerMateriaDeRelacionamento(idTurma,
									idMateria);

							// Se não houver nenhuma turma com essa matéria,
							// remove-la do sistema.
							if (!TurmaMateriaVO.estaEmMaisRelacionamentos(idMateria)) {

								MateriaVO.open(ListaMaterias.this);
								MateriaVO.removerMateria(idMateria);
								MateriaVO.close();
							}
							TurmaMateriaVO.close();
							ListaMaterias.super.updateItens();
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
	public int setTitle() {
		return R.string.titulo_lista_materias;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
