package br.com.guilhermebarbosa.git.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "commit")
public class Commit {
	private Integer idCommit;
	private String hash;
	private Date date;
	private Commit parent;
	private String message;
	private String authorName;
	private Repository repository;
	private StatusCommit status;
	private Tag tag;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_commit", insertable = true, updatable = false, nullable = false)
	public Integer getIdCommit() {
		return idCommit;
	}

	public void setIdCommit(Integer idCommit) {
		this.idCommit = idCommit;
	}

	@Column(name = "hash")
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent", referencedColumnName = "id_commit")
	public Commit getParent() {
		return parent;
	}

	public void setParent(Commit parent) {
		this.parent = parent;
	}

	@Column(name = "message")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_repository")
	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	public StatusCommit getStatus() {
		return status;
	}

	public void setStatus(StatusCommit status) {
		this.status = status;
	}

	@Column(name = "author_name")
	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tag")
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}