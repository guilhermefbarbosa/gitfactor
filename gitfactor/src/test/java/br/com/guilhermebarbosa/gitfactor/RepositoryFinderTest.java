package br.com.guilhermebarbosa.gitfactor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class RepositoryFinderTest {
	private static final String GIT_HUB_QUERY_REPOS = "https://api.github.com/search/repositories?q=language:Java&sort=stars&order=desc&per_page=100";
	private static final String GIT_HUB_QUERY_TAGS = "https://api.github.com/repos/%1$s/%2$s/tags";

	@Test
	public void testFindGitHubRepositories() {
		RestTemplate restTemplate = new RestTemplate();
        GitRepositorySearchResult gitSearchResult = restTemplate.getForObject(GIT_HUB_QUERY_REPOS, GitRepositorySearchResult.class);
        List<GitRepository> javaRepos = getJavaRepositories(gitSearchResult);
        for (GitRepository gitRepository : javaRepos) {
			System.out.println(gitRepository.getName() + " - stars: " + gitRepository.getStars());
			String urlTags = obterUrlTags(gitRepository.getOwner().getLogin(), gitRepository.getName());
			String json = restTemplate.getForObject(urlTags, String.class);
			ObjectMapper mapper = new ObjectMapper();
			try {
				List<GitRepositoryTag> tags = (List<GitRepositoryTag>) mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, GitRepositoryTag.class));
				for (GitRepositoryTag gitRepositoryTag : tags) {
					System.out.println(String.format("repo (%1$s), user(%2$s), tag: %3$s", gitRepository.getName(), gitRepository.getOwner().getLogin(), gitRepositoryTag.getName()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<GitRepository> getJavaRepositories(GitRepositorySearchResult gitSearchResult) {
		List<GitRepository> repositories = gitSearchResult.getRepositories();
        List<GitRepository> javaRepos = new ArrayList<GitRepository>();
        for (GitRepository gitRepository : repositories) {
			if ( gitRepository.getStars() != null && Integer.parseInt(gitRepository.getStars()) >= 1000 ) {
				javaRepos.add(gitRepository);
			}
		}
		return javaRepos;
	}
	
	private String obterUrlTags(String userName, String repo) {
		return String.format(GIT_HUB_QUERY_TAGS, userName, repo);
	}
}