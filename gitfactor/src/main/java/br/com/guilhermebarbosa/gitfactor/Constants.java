package br.com.guilhermebarbosa.gitfactor;

public class Constants {
	public static final int WAIT_TIME = 30000;
	// java repositories having more than 1000 stars 
	public static final String GIT_HUB_QUERY_REPOS = "https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000000&sort=stars&order=desc&per_page=100";
	public static final String GIT_HUB_QUERY_REPOS_TINY = "https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100";
	public static final String GIT_HUB_AUTHENTICATION = "https://api.github.com/user?access_token=ea604eb7230a230d3e13080b500c2d931cffd593";
}
