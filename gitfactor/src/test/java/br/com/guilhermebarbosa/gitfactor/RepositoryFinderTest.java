package br.com.guilhermebarbosa.gitfactor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import br.com.guilhermebarbosa.git.GitRepositoryUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class RepositoryFinderTest {
	private static final String GIT_HUB_QUERY_REPOS = "https://api.github.com/search/repositories?q=language:Java&sort=stars&order=desc&per_page=100";
	private static final String GIT_HUB_QUERY_TAGS = "https://api.github.com/repos/%1$s/%2$s/tags";
	private static final String GIT_HUB_AUTHENTICATION = "https://api.github.com/user?access_token=ea604eb7230a230d3e13080b500c2d931cffd593";
	private static final String TEMP_FOLDER = "/var/tmp/git";

	@Test
	public void testFindGitHubRepositories() throws InterruptedException, InvalidRemoteException, TransportException, IOException, GitAPIException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject(GIT_HUB_AUTHENTICATION, Object.class);
		// aguarda 1min
		Thread.sleep(60000);
		// get repositories
        GitRepositorySearchResult gitSearchResult = restTemplate.getForObject(GIT_HUB_QUERY_REPOS, GitRepositorySearchResult.class);
        List<GitRepository> javaRepos = getJavaRepositories(gitSearchResult);
        for (GitRepository gitRepository : javaRepos) {
        	// aguarda 1min
    		Thread.sleep(60000);
			System.out.println(gitRepository.getName() + " - stars: " + gitRepository.getStars());
			// get tags
			List<GitRepositoryTag> tags = getRepositoryTags(restTemplate, gitRepository);
			for (GitRepositoryTag gitRepositoryTag : tags) {
				System.out.println(String.format("repo (%1$s), user(%2$s), tag: %3$s", gitRepository.getName(), gitRepository.getOwner().getLogin(), gitRepositoryTag.getName()));
			}
			// clone git repo (all tags and branches included)
			GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), new File(TEMP_FOLDER + File.separator + gitRepository.getName()));
		}
	}

	@SuppressWarnings("unchecked")
	private List<GitRepositoryTag> getRepositoryTags(RestTemplate restTemplate, GitRepository gitRepository) {
		try {
			String urlTags = obterUrlTags(gitRepository.getOwner().getLogin(), gitRepository.getName());
			String json = restTemplate.getForObject(urlTags, String.class);
			ObjectMapper mapper = new ObjectMapper();
			return (List<GitRepositoryTag>) mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, GitRepositoryTag.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<GitRepositoryTag>();
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