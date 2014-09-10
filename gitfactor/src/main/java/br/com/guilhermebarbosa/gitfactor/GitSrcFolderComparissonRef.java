package br.com.guilhermebarbosa.gitfactor;

public class GitSrcFolderComparissonRef {
	private String srcPath1;
	private String srcPath2;
	
	public GitSrcFolderComparissonRef(String srcPath1, String srcPath2) {
		this.srcPath1 = srcPath1;
		this.srcPath2 = srcPath2;
	}

	public String getSrcPath1() {
		return srcPath1;
	}

	public void setSrcPath1(String srcPath1) {
		this.srcPath1 = srcPath1;
	}

	public String getSrcPath2() {
		return srcPath2;
	}

	public void setSrcPath2(String srcPath2) {
		this.srcPath2 = srcPath2;
	}
}