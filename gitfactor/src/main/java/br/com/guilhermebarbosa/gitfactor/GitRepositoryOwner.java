package br.com.guilhermebarbosa.gitfactor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitRepositoryOwner {
	private String login;
	private String id;

	@JsonCreator
	public GitRepositoryOwner(
			@JsonProperty("login") String login, 
			@JsonProperty("id") String id) {
		super();
		this.login = login;
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}