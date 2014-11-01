package br.com.guilhermebarbosa.git.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "commit")
public class Commit {
	private Integer idCommit;
	private String hash;
	private Date date;
	private Commit parent;
	private String message;
	private Repository repository;
	private StatusCommit status;

	public Integer getIdCommit() {
		return idCommit;
	}

	public void setIdCommit(Integer idCommit) {
		this.idCommit = idCommit;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Commit getParent() {
		return parent;
	}

	public void setParent(Commit parent) {
		this.parent = parent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public StatusCommit getStatus() {
		return status;
	}

	public void setStatus(StatusCommit status) {
		this.status = status;
	}
}