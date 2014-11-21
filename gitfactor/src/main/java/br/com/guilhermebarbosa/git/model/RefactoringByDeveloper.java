package br.com.guilhermebarbosa.git.model;

public class RefactoringByDeveloper {
	private String id;
	private String repository;
	private String author;
	private String className;

	public RefactoringByDeveloper(String id, String repository, String author, String className) {
		this.id = id;
		this.repository = repository;
		this.author = author;
		this.className = className;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}