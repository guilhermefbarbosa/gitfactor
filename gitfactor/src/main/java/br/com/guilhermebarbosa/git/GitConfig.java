package br.com.guilhermebarbosa.git;

public enum GitConfig {
	BARBOSA("https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000000&sort=stars&order=desc&per_page=100", "barbosa"),
	JUVENAL("https://api.github.com/search/repositories?q=language:java stars:225..1000 size:<1000000&sort=stars&order=desc&per_page=100", "juvenal"),
//	TINY("https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100", "tiny"),
	// joda-time
//	TINY("https://api.github.com/search/repositories?q=joda-time stars:>900&sort=stars&order=desc&per_page=100", "tiny"),
	TINY("https://api.github.com/search/repositories?q=cassandra language:java stars:>1000&sort=stars&order=desc&per_page=100", "tiny"),
	BIOCEV("https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000000&sort=stars&order=desc&per_page=100", "biocev");
	
	private String code;
	private String url;

	private GitConfig(String url, String code) {
		this.url = url;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getUrl() {
		return url;
	}
	
	public static GitConfig fromString(String code) {
		for(GitConfig g : GitConfig.values()) {
			if ( g.getCode().equals(code) ) {
				return g;
			}
		}
		return null;
	}
}