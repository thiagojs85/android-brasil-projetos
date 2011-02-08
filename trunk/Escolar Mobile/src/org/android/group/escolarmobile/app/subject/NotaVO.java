package org.android.group.escolarmobile.app.subject;

public class NotaVO {
	private long id;
	private long idMatricula;
	private int periodo;
	private float nota;
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdMatricula() {
		return idMatricula;
	}
	public void setIdMatricula(long idMatricula) {
		this.idMatricula = idMatricula;
	}
	public float getNota() {
		return nota;
	}
	public void setNota(float nota) {
		this.nota = nota;
	}
	public int getPeriodo() {
		return periodo;
	}
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
}
