package br.com.guilhermebarbosa.git;

import br.com.guilhermebarbosa.gitfactor.Constants;
import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;

public class GitfactorMain {
	public static void main(String[] args) {
		try {
			int totalThreads = Integer.parseInt(args[0]);
			String tmpFolder = args[1];
			GitHubAnalyser.analyseGitHubByQueryUrl(Constants.GIT_HUB_QUERY_REPOS, totalThreads, tmpFolder, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
