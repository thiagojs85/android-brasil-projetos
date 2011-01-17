package org.android.group.escolarmobile.turma;

/**
 * Esta classe � apenas um acesso para os dados de uma turma.
 * 
 * @author Otavio
 *
 */
public class TurmaVO {
	private String nome;
	private String descricao;
	private int id;
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
