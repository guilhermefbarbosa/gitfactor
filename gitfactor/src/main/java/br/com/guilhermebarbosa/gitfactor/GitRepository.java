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
	private String cloneUrl;
	private String defaultBranch;
	private String size;
	private GitRepositoryOwner owner;

	@JsonCreator
	public GitRepository(
			@JsonProperty("id") String id,
			@JsonProperty("name") String name,
			@JsonProperty("language") String language,
			@JsonProperty("stargazers_count") String stars,
			@JsonProperty("owner") GitRepositoryOwner owner,
			@JsonProperty("clone_url") String cloneUrl,
			@JsonProperty("default_branch") String defaultBranch,
			@JsonProperty("size") String size) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.stars = stars;
		this.owner = owner;
		this.cloneUrl = cloneUrl;
		this.defaultBranch = defaultBranch;
		this.size = size;
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

	public String getCloneUrl() {
		return cloneUrl;
	}

	public void setCloneUrl(String cloneUrl) {
		this.cloneUrl = cloneUrl;
	}

	public String getDefaultBranch() {
		return defaultBranch;
	}

	public void setDefaultBranch(String defaultBranch) {
		this.defaultBranch = defaultBranch;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}