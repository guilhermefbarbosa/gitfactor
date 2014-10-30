package br.com.guilhermebarbosa.gitfactor;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.junit.Test;

public class RepositoryFinderTest {
	@Test
	public void testFindGitHubRepositories() throws InterruptedException, InvalidRemoteException, TransportException, IOException, GitAPIException {
		GitHubAnalyser.analyseGitHubByQueryUrl(Constants.GIT_HUB_QUERY_REPOS, 10, Constants.TEMP_FOLDER);
	}
}