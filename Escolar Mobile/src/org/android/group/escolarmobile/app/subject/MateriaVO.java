package org.android.group.escolarmobile.app.subject;

public class MateriaVO {
	private long id;
	private long idProfessor;
	private long idTurma;
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
	public long getIdTurma() {
		return idTurma;
	}
	public void setIdTurma(long idTurma) {
		this.idTurma = idTurma;
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
