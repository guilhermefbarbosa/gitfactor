package br.com.guilhermebarbosa.gitfactor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitTagSearchResult {
	private List<GitRepositoryTag> tags;

	public GitTagSearchResult(List<GitRepositoryTag> tags) {
		this.tags = tags;
	}

	public List<GitRepositoryTag> getTags() {
		return tags;
	}

	public void setTags(List<GitRepositoryTag> tags) {
		this.tags = tags;
	}
}