package org.android.brasil.projetos.model;

import java.io.Serializable;
import java.util.Date;

public class Emprestimo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 831389733846366170L;
	private long idEmprestimo;
	private String item;
	private String descricao;
	private Date data;
	private int status;
	private int ativarAlarme;
	private long idContato;
	private long idCategoria;
	private String nomeContato;

	public static final int STATUS_EMPRESTAR = 0;
	public static final int STATUS_PEGAR_EMPRESTADO = 1;
	
	public static final int ATIVAR_ALARME = 0;
	public static final int DESATIVAR_ALARME = 1;

	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAtivarAlarme() {
		return ativarAlarme;
	}

	public void setAtivarAlarme(int ativarAlarme) {
		this.ativarAlarme = ativarAlarme;
	}

	public long getIdContato() {
		return idContato;
	}

	public void setIdContato(long idContato) {
		this.idContato = idContato;
	}

	public long getIdEmprestimo() {

		return idEmprestimo;
	}
	public void setIdEmprestimo(long idEmprestimo){
		this.idEmprestimo = idEmprestimo;
	}

	public long getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(long idCat) {
		idCategoria = idCat;
		
	}

	public String getNomeContato() {
		return nomeContato;
	}

	public void setNomeContato(String contato) {
		this.nomeContato = contato;
	}
}
