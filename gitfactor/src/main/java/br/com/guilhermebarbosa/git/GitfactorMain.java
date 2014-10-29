package br.com.guilhermebarbosa.git;

import br.com.guilhermebarbosa.gitfactor.Constants;
import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;

public class GitfactorMain {
	public static void main(String[] args) {
		try {
			GitHubAnalyser.analyseGitHubByQueryUrl(Constants.GIT_HUB_QUERY_REPOS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
