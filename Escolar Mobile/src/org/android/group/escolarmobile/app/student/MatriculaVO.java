package org.android.group.escolarmobile.app.student;

public class MatriculaVO {
	private long id;
	private long idAluno;
	private long idTurma;
	private long idMateria;
	private String turma;
	private String materia;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdAluno() {
		return idAluno;
	}
	public void setIdAluno(long idAluno) {
		this.idAluno = idAluno;
	}
	public long getIdTurma() {
		return idTurma;
	}
	public void setIdTurma(long idTurma) {
		this.idTurma = idTurma;
	}
	public long getIdMateria() {
		return idMateria;
	}
	public void setIdMateria(long idMateria) {
		this.idMateria = idMateria;
	}
	public String getTurma() {
		return turma;
	}
	public void setTurma(String turma) {
		this.turma = turma;
	}
	public String getMateria() {
		return materia;
	}
	public void setMateria(String materia) {
		this.materia = materia;
	}
}
