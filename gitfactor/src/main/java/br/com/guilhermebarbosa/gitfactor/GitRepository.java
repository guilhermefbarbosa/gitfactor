package br.com.guilhermebarbosa.gitfactor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitRepository {
	private String id;
	private String name;
	private String language;
	private String stars;
	private GitRepositoryOwner owner;

	@JsonCreator
	public GitRepository(
			@JsonProperty("id") String id,
			@JsonProperty("name") String name,
			@JsonProperty("language") String language,
			@JsonProperty("stargazers_count") String stars,
			@JsonProperty("owner") GitRepositoryOwner owner) {
		super();
		this.id = id;
		this.name = name;
		this.language = language;
		this.stars = stars;
		this.owner = owner;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStars() {
		return stars;
	}

	public void setStars(String stars) {
		this.stars = stars;
	}

	public GitRepositoryOwner getOwner() {
		return owner;
	}

	public void setOwner(GitRepositoryOwner owner) {
		this.owner = owner;
	}
}