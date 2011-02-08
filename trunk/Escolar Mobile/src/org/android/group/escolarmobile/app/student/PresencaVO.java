package org.android.group.escolarmobile.app.student;

import java.sql.Date;

public class PresencaVO {
	private long id;
	private Date data;
	private long idMatricula;
	private boolean presente;
	
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
	public long getIdMatricula() {
		return idMatricula;
	}
	public void setIdMatricula(long idMatricula) {
		this.idMatricula = idMatricula;
	}
	public boolean isPresente() {
		return presente;
	}
	public void setPresente(boolean presente) {
		this.presente = presente;
	}
}
