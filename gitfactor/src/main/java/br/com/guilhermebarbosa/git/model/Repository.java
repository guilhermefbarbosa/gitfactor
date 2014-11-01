package br.com.guilhermebarbosa.git.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "repository")
public class Repository {
	private Integer idRepository;
	private String url;
	private String author;
	private Integer totalCommits;
	private Date start;
	private Date end;
	private RepositoryStatus status;

	public Integer getIdRepository() {
		return idRepository;
	}

	public void setIdRepository(Integer idRepository) {
		this.idRepository = idRepository;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getTotalCommits() {
		return totalCommits;
	}

	public void setTotalCommits(Integer totalCommits) {
		this.totalCommits = totalCommits;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public RepositoryStatus getStatus() {
		return status;
	}

	public void setStatus(RepositoryStatus status) {
		this.status = status;
	}
}