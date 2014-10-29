package br.com.guilhermebarbosa.gitfactor;

public class Constants {
	public static final String TEMP_FOLDER = "/var/tmp/git";
	public static final int WAIT_TIME = 30000;
	public static final String GIT_HUB_QUERY_REPOS = "https://api.github.com/search/repositories?q=language:Java&sort=stars&order=desc&per_page=100";
	public static final String GIT_HUB_AUTHENTICATION = "https://api.github.com/user?access_token=ea604eb7230a230d3e13080b500c2d931cffd593";
}
