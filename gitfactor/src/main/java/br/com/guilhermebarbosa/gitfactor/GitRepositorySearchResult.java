package br.com.guilhermebarbosa.gitfactor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitRepositorySearchResult {
	private String totalResults;
	private List<GitRepository> repositories;

	@JsonCreator
	public GitRepositorySearchResult(@JsonProperty("total_count") String totalResults, @JsonProperty("items") List<GitRepository> repositories) {
		this.totalResults = totalResults;
		this.repositories = repositories;
	}

	public String getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(String totalResults) {
		this.totalResults = totalResults;
	}

	public List<GitRepository> getRepositories() {
		return repositories;
	}

	public void setRepositories(List<GitRepository> repositories) {
		this.repositories = repositories;
	}
}