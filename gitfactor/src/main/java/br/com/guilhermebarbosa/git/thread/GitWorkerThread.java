//package br.com.guilhermebarbosa.git.thread;
//
//import java.io.File;
//
//import org.apache.log4j.Logger;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.revwalk.RevCommit;
//
//import br.com.guilhermebarbosa.git.GitRepositoryUtils;
//import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;
//import br.com.guilhermebarbosa.gitfactor.GitRepository;
//
//public class GitWorkerThread implements Runnable {
//	private static final Logger LOGGER = Logger.getLogger(GitWorkerThread.class);
//	
//	private File gitRepoPath;
//	private Git git;
//
//	private GitRepository gitRepository;
//	private RevCommit revCommit;
//	private int totalCommits;
//
//	private String tmpPath;
//
//	private int index;
//	
//	public GitWorkerThread(String tmpPath, GitRepository gitRepository, int totalCommits, RevCommit revCommit, int size, int count) {
//		this.gitRepoPath = null;
//		this.git = null;
//		// variaveis importantes para analisar usando threads
//		this.gitRepository = gitRepository;
//		this.totalCommits = totalCommits;
//		this.revCommit = revCommit;
//		this.tmpPath = tmpPath;
//		this.index = count % size;
//	}
//
//	public void run() {
//		try {
//			// folder for checkout
//			this.gitRepoPath = new File(tmpPath + File.separator + gitRepository.getName() + "_" + index);
//			// clone git repo (all tags and branches included)
//			if ( !gitRepoPath.exists() ) {
//				LOGGER.info(String.format("Cloning repository %1$s to path %2$s.", gitRepository.getName(), this.gitRepoPath));
//				// clone repo to folder
//				GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath);
//			}
//			LOGGER.info(String.format("Openning repository %1$s.", gitRepository.getName()));
//			// open repo
//			this.git = Git.open(gitRepoPath);
//			// analisa o commit
//			GitHubAnalyser.analyseCommit(gitRepoPath, git, totalCommits, revCommit);
//			// increment count commits
//			GitHubAnalyser.getCountCommits().set(GitHubAnalyser.getCountCommits().get()+1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}