package br.com.guilhermebarbosa.gitfactor;

import gr.uom.java.xmi.ASTReader;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.Refactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CheckoutResult.Status;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import br.com.guilhermebarbosa.git.GitRepositoryUtils;

import com.google.common.collect.Iterables;

public class GitHubAnalyser {
	private static final Logger LOGGER = Logger.getLogger(GitHubAnalyser.class);
	
	public static void analyseGitHubByQueryUrl(String queryUrl) throws IOException, GitAPIException,
			InvalidRemoteException, TransportException, NoHeadException,
			RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, InterruptedException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject(Constants.GIT_HUB_AUTHENTICATION, Object.class);
		// create dirs
		new File(Constants.TEMP_FOLDER).mkdirs();
		// aguarda 1min
		Thread.sleep(Constants.WAIT_TIME);
		
		// get repositories
		GitRepositorySearchResult gitSearchResult = restTemplate.getForObject(queryUrl, GitRepositorySearchResult.class);
        LOGGER.info("Searching git repositories...");
        List<GitRepository> javaRepos = getJavaRepositories(gitSearchResult);
        LOGGER.info(String.format("Found %1$d repositories.", javaRepos.size()));
        for (GitRepository gitRepository : javaRepos) {
			LOGGER.info("Repository: " + gitRepository.getName() + " - stars: " + gitRepository.getStars());
			// folder for checkout
			File gitRepoPath = new File(Constants.TEMP_FOLDER + File.separator + gitRepository.getName());
			// clone git repo (all tags and branches included)
			if ( !gitRepoPath.exists() ) {
				LOGGER.info(String.format("Cloning repository %1$s.", gitRepository.getName()));
				// clone repo to folder
				GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath);
			}
			LOGGER.info(String.format("Openning repository %1$s.", gitRepository.getName()));
			// open repo
			Git git = Git.open(gitRepoPath);
			// get logs
			Iterable<RevCommit> call = git.log().call();
			int totalCommits = Iterables.size(call);
			LOGGER.info(String.format("Getting commit logs for repository %1$s. Total of commits: %2$d", gitRepository.getName(), totalCommits));
			call = git.log().call();
			int countCommits = 0;
			for (RevCommit revCommit : call) {
				countCommits ++;
//				LOGGER.info("revCommit = " + revCommit.getName());
				// se possui apenas um pai, faz a comparacao
				if ( revCommit.getParentCount() == 1 ) {
					GitModelStructure modelStructure = buildModelStructure(gitRepoPath, git, revCommit);
					// para cada entrada do filho, verifica se existe uma entrada para o pai e compara
					analyseModelRefactorings(modelStructure);
					// log
					LOGGER.info(String.format("%1$d commits analysed. %2$s", countCommits, obterPercentual(totalCommits, countCommits)));
				}
			}
		}
	}

	private static double obterPercentual(int totalCommits, int countCommits) {
		return (countCommits*1.0) / (totalCommits * 1.0) * 100.0;
	}

	private static void analyseModelRefactorings(GitModelStructure modelStructure) {
		for(String src : modelStructure.getMapChildrenModel().keySet()) {
			if ( modelStructure.getMapFatherModel().containsKey(src) ) {
				try {
					UMLModel umlModelChild = modelStructure.getMapChildrenModel().get(src);
					UMLModel umlModelFather = modelStructure.getMapFatherModel().get(src);
					UMLModelDiff diff = umlModelChild.diff(umlModelFather);
					modelStructure.getListUmlDiff().add(new GitSrcFolderComparissonRef(umlModelChild, umlModelFather, diff));
					if ( diff.getRefactorings().size() > 0 ) {
						LOGGER.info(String.format("%1$d refactorings encontrados.", diff.getRefactorings().size()));
						for(Refactoring refactoring : diff.getRefactorings()) {
							LOGGER.info(refactoring.toString());
						}
					}
				} catch(Throwable t) {
					LOGGER.error(t.getMessage(), t);
					continue;
				}
			}
		}
	}

private static GitModelStructure buildModelStructure(File gitRepoPath, Git git,
		RevCommit revCommit) throws GitAPIException, RefAlreadyExistsException,
		RefNotFoundException, InvalidRefNameException,
		CheckoutConflictException, IOException {
	// cria objeto que armazena as comparacoes
	GitModelStructure modelStructure = new GitModelStructure();
	// faz checkout do filho
	CheckoutCommand checkout = checkout(git, revCommit);
	// obtem o map model do filho
	modelStructure.setMapChildrenModel(obterMapModel(gitRepoPath, checkout));
	// faz o checkout do pai
	checkout = checkout(git, revCommit.getParent(0));
	// obtem o model do pai
	modelStructure.setMapFatherModel(obterMapModel(gitRepoPath, checkout));
	return modelStructure;
}

	private static CheckoutCommand checkout(Git git, RevCommit revCommit)
			throws GitAPIException, RefAlreadyExistsException,
			RefNotFoundException, InvalidRefNameException,
			CheckoutConflictException, IOException {
//		LOGGER.info("Checkout " + revCommit.getName());
		CheckoutCommand checkout = git.checkout();
		List<Ref> call = git.branchList().call();
		for (Ref ref : call) {
			if ( ref.getName().contains(revCommit.getName()) ) {
				// checkout master
				git.checkout().setName("master").call();
				// apaga os branchs
				git.branchDelete().setBranchNames(revCommit.getName()).call();
//				LOGGER.info("Deleted branchs: " + deletedBranchs);
			}
		}
		
		checkout.
				setCreateBranch(true).
				setName(revCommit.getName()).
				setStartPoint(revCommit.getName()).
				setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).call();
//		LOGGER.info("Ref: " + ref.getName());
		return checkout;
	}

	private static Map<String, UMLModel> obterMapModel(File gitRepoPath, CheckoutCommand checkout) {
		Map<String, UMLModel> mapModel = new HashMap<String, UMLModel>();
		if ( isCheckoutOk(checkout) ) {
			// getting UML Model
			List<String> list1 = new ArrayList<String>(getSrcFolder(gitRepoPath, new HashSet<String>()));
			for (String srcFolder : list1) {
				// cria um objeto para armazenar a comparacao
				UMLModel model = new ASTReader(new File(srcFolder)).getUmlModel();
				String relativePathDir = srcFolder.substring(srcFolder.indexOf(gitRepoPath.getAbsolutePath())+gitRepoPath.getAbsolutePath().length());
				// LOGGER.info("UMLModel -> " + relativePathDir);
				mapModel.put(relativePathDir, model);
			}
		}
		return mapModel;
	}

	private static Set<String> getSrcFolder(File path, Set<String> pathNames) {
		if ( path.isDirectory() && existsJavaFiles(path) ) {
			String baseJavaSrcFolder = getBaseJavaSrcFolder(path);
			if ( baseJavaSrcFolder != null ) {
				pathNames.add(baseJavaSrcFolder);
			}
		} else if ( path.isDirectory() ) {
			File[] listFiles = path.listFiles();
			for (File file : listFiles) {
				getSrcFolder(file, pathNames);
			}
		}
		return pathNames;
	}

	private static String getBaseJavaSrcFolder(File path) {
		// get a java file
		// get package declaration
		// get dir without package declaration
		File[] listFiles = path.listFiles();
		for (File file : listFiles) {
			if ( file.getAbsolutePath().endsWith(".java") ) {
				String packageStr = obterPackageJavaFile(file);
				if ( StringUtils.isEmpty(packageStr) ) {
					continue;
				}
				packageStr = packageStr.replaceAll("\\.", File.separator);
//				System.out.println(packageStr);
				String pathForSrcFolder = path.getAbsolutePath().replaceAll(packageStr, "");
//				System.out.println(pathForSrcFolder);
				return pathForSrcFolder;
			}
		}
		return null;
	}

	private static String obterPackageJavaFile(File path) {
		BufferedReader bf = null;
		String packageStr = null;
		try {
			bf = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			while ((line = bf.readLine()) != null) {
				if ( line.startsWith("package ") ) {
					packageStr = line.substring(line.indexOf("package ")+"package ".length(), line.indexOf(";")).trim();
				}
			}
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packageStr;
	}

	private static boolean existsJavaFiles(File path) {
		File[] listFiles = path.listFiles();
		for (File file : listFiles) {
			if ( file.getAbsolutePath().endsWith(".java") ) {
				return true;
			}
		}
		return false;
	}

	private static boolean isCheckoutOk(CheckoutCommand checkout) {
		return checkout.getResult().getStatus().equals(Status.OK);
	}

	private static List<GitRepository> getJavaRepositories(GitRepositorySearchResult gitSearchResult) {
		List<GitRepository> repositories = gitSearchResult.getRepositories();
        List<GitRepository> javaRepos = new ArrayList<GitRepository>();
        for (GitRepository gitRepository : repositories) {
			if ( gitRepository.getStars() != null && Integer.parseInt(gitRepository.getStars()) >= 1000 ) {
				javaRepos.add(gitRepository);
			}
		}
		return javaRepos;
	}
}