package br.com.guilhermebarbosa.git.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "operation")
public class Operation {
	private Integer idOperation;
	private String name;
	private Refactoring refactoring;
	private String className;
	private String visibility;

	public Integer getIdOperation() {
		return idOperation;
	}

	public void setIdOperation(Integer idOperation) {
		this.idOperation = idOperation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Refactoring getRefactoring() {
		return refactoring;
	}

	public void setRefactoring(Refactoring refactoring) {
		this.refactoring = refactoring;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
}