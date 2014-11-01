package br.com.guilhermebarbosa.git.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "refactoring")
public class Refactoring {
	private Integer idRefactoring;
	private String name;
	private Commit commit;

	public Integer getIdRefactoring() {
		return idRefactoring;
	}

	public void setIdRefactoring(Integer idRefactoring) {
		this.idRefactoring = idRefactoring;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}
}