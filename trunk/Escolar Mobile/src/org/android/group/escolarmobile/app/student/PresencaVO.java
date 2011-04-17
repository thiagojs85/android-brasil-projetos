package org.android.group.escolarmobile.app.student;

import java.sql.Date;

public class PresencaVO {
	private long id;
	private Date data;
	private long idAluno;
	private int falta;
	
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public long getIdAluno() {
		return idAluno;
	}
	public void setIdAluno(long idMatricula) {
		this.idAluno = idMatricula;
	}
	public int getFalta() {
		return falta;
	}
	public void setFalta(int falta) {
		this.falta = falta;
	}
}
