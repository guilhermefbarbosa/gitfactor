package br.com.guilhermebarbosa.git.model;

import java.util.List;

public class GitfactorMoveMethodRefactoring {
	private Integer idRefactoring;
	private String repositoryName;
	private String repositoryCloneUrl;
	private String defaultBranch;
	private String hashCommit;
	private String hashParentCommit;
	private String refactoringDescription;
	private List<Operation> operations;

	public Integer getIdRefactoring() {
		return idRefactoring;
	}

	public void setIdRefactoring(Integer idRefactoring) {
		this.idRefactoring = idRefactoring;
	}

	public String getHashCommit() {
		return hashCommit;
	}

	public void setHashCommit(String hashCommit) {
		this.hashCommit = hashCommit;
	}

	public String getHashParentCommit() {
		return hashParentCommit;
	}

	public void setHashParentCommit(String hashParentCommit) {
		this.hashParentCommit = hashParentCommit;
	}

	public String getRefactoringDescription() {
		return refactoringDescription;
	}

	public void setRefactoringDescription(String refactoringDescription) {
		this.refactoringDescription = refactoringDescription;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getRepositoryCloneUrl() {
		return repositoryCloneUrl;
	}

	public void setRepositoryCloneUrl(String repositoryCloneUrl) {
		this.repositoryCloneUrl = repositoryCloneUrl;
	}

	public String getDefaultBranch() {
		return defaultBranch;
	}

	public void setDefaultBranch(String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	public List<Operation> getOperations() {
		return operations;
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}
}