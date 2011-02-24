package org.android.group.escolarmobile.app.subject;

public class MateriaVO {
	private long id;
	private long idProfessor;
	private long[] idTurmas;
	private String nome;
	private int horas;
	private String descricao;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdProfessor() {
		return idProfessor;
	}
	public void setIdProfessor(long idProfessor) {
		this.idProfessor = idProfessor;
	}
	public long[] getIdTurmas() {
		return idTurmas;
	}
	public void setIdTurmas(long[] idTurmas) {
		this.idTurmas = idTurmas;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int getHoras() {
		return horas;
	}
	public void setHoras(int horas) {
		this.horas = horas;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
