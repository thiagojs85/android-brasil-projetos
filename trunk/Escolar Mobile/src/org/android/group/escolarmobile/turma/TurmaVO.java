package org.android.group.escolarmobile.turma;

/**
 * Esta classe é apenas um acesso para os dados de uma turma.
 * 
 * @author Otavio
 *
 */
public class TurmaVO {
	private String nome;
	private String descricao;
	private long id;
	private long[] idMaterias;
	
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
	public long getId() {
		return id;
	}
	public void setId(long l) {
		this.id = l;
	}
	public long[] getIdMaterias() {
		return idMaterias;
	}
	public void setIdMaterias(long[] idMaterias) {
		this.idMaterias = idMaterias;
	}
}
