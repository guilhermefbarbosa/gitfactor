package br.com.guilhermebarbosa.gitfactor;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class RepositoryFinderTest {
	private static final String GIT_HUB_QUERY = "https://api.github.com/search/repositories?q=language:Java&sort=stars&order=desc&per_page=100";

	@Test
	public void testFindGitHubRepositories() {
		RestTemplate restTemplate = new RestTemplate();
        GitRepositorySearchResult gitSearchResult = restTemplate.getForObject(GIT_HUB_QUERY, GitRepositorySearchResult.class);
        List<GitRepository> javaRepos = getJavaRepositories(gitSearchResult);
        for (GitRepository gitRepository : javaRepos) {
			System.out.println(gitRepository.getName() + " - stars: " + gitRepository.getStars());
		}
	}

	private List<GitRepository> getJavaRepositories(
			GitRepositorySearchResult gitSearchResult) {
		List<GitRepository> repositories = gitSearchResult.getRepositories();
        List<GitRepository> javaRepos = new ArrayList<GitRepository>();
        for (GitRepository gitRepository : repositories) {
			if ( gitRepository.getStars() != null && Integer.parseInt(gitRepository.getStars()) >= 1000 ) {
				javaRepos.add(gitRepository);
			}
		}
		return javaRepos;
	}
}