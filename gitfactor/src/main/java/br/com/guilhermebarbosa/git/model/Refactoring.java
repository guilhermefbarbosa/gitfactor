package br.com.guilhermebarbosa.git.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "refactoring")
public class Refactoring {
	private Integer idRefactoring;
	private String name;
	private Commit commit;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_refactoring", insertable = true, updatable = false, nullable = false)
	public Integer getIdRefactoring() {
		return idRefactoring;
	}

	public void setIdRefactoring(Integer idRefactoring) {
		this.idRefactoring = idRefactoring;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_commit")
	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}
}