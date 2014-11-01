package br.com.guilhermebarbosa.git;

import br.com.guilhermebarbosa.gitfactor.Constants;
import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;

public class GitfactorMain {
	public static void main(String[] args) {
		try {
			int totalThreads = Integer.parseInt(args[0]);
			String tmpFolder = args[1];
			Boolean analyse = Boolean.valueOf(args[2]);
			GitHubAnalyser.analyseGitHubByQueryUrl(Constants.GIT_HUB_QUERY_REPOS, totalThreads, tmpFolder, analyse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
