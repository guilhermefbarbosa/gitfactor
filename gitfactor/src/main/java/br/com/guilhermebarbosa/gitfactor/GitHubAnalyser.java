package br.com.guilhermebarbosa.gitfactor;

import gr.uom.java.xmi.ASTReader;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.Refactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CheckoutResult.Status;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import br.com.guilhermebarbosa.git.GitRepositoryUtils;

import com.google.common.collect.Iterables;

public class GitHubAnalyser {
	private static final Logger LOGGER = Logger.getLogger(GitHubAnalyser.class);

	private static AtomicInteger countCommits;

	public static void analyseGitHubByQueryUrl(String queryUrl, int totalThreads, String tmpFolder, boolean analyse) throws Exception {
		// search for repositories
		List<GitRepository> javaRepos = searchRepositories(queryUrl);
		// wait some time
		Thread.sleep(Constants.WAIT_TIME);
		// create dirs
		new File(Constants.TEMP_FOLDER).mkdirs();
		// git repo
		Git git = null;
		int totalCommits = 0;
		// total of repositories
		LOGGER.info(String.format("Total repositories: %1$d", javaRepos.size()));
		for (GitRepository gitRepository : javaRepos) {
			LOGGER.info("Repository: " + gitRepository.getName() + " - stars: " + gitRepository.getStars());
			// folder for checkout
			File gitRepoPath = new File(Constants.TEMP_FOLDER + File.separator + gitRepository.getName());
			// clone repo to folder
			git = GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath);
			// get logs
			LOGGER.info(String.format("Getting commit logs for repository %1$s.", gitRepository.getName()));
			Iterable<RevCommit> call = git.log().call();
			int commits = Iterables.size(git.log().call());
			totalCommits += commits;
			LOGGER.info(String.format("Repository: %1$s - Commits: %2$d.", gitRepository.getName(), commits));
			if ( analyse ) {
				// inicializa o count commits
				countCommits = new AtomicInteger(0);
				// analisa cada commit
				for (RevCommit revCommit : call) {
					// se possui apenas um pai, faz a comparacao
					if (revCommit.getParentCount() == 1) {
						// analisa o commit
						GitHubAnalyser.analyseCommit(gitRepoPath, git, revCommit);
						// increment count commits
						GitHubAnalyser.getCountCommits().set(GitHubAnalyser.getCountCommits().get() + 1);
					}
					// give a hint to garbage collection
					System.gc();
				}
			}
			// excluir o repositorio
			boolean delete = gitRepoPath.delete();
			if ( delete ) {
				LOGGER.info(String.format("Repository deleted sucessfull: %1$s.", gitRepoPath.getAbsolutePath()));
			}
		}
		LOGGER.info(String.format("Total Commits: %1$d.", totalCommits));
	}

	private static List<GitRepository> searchRepositories(String queryUrl) throws RestClientException, UnsupportedEncodingException {
		GitRepositorySearchResult gitSearchResult = queryGitHub(queryUrl);
		LOGGER.info("Searching git repositories...");
		List<GitRepository> javaRepos = filterRepositories(gitSearchResult);
		LOGGER.info(String.format("Found %1$d repositories.", javaRepos.size()));
		return javaRepos;
	}

	private static List<GitRepository> filterRepositories(GitRepositorySearchResult gitSearchResult) {
		List<GitRepository> repositories = gitSearchResult.getRepositories();
		List<GitRepository> javaRepos = new ArrayList<GitRepository>();
		for (GitRepository gitRepository1 : repositories) {
			javaRepos.add(gitRepository1);
		}
		return javaRepos;
	}

	private static GitRepositorySearchResult queryGitHub(String queryUrl) throws RestClientException, UnsupportedEncodingException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject(Constants.GIT_HUB_AUTHENTICATION, Object.class);
		// get repositories
		return searchRepositoriesWithPagination(queryUrl, restTemplate);
	}

	private static GitRepositorySearchResult searchRepositoriesWithPagination(
			String queryUrl, RestTemplate restTemplate) {
		GitRepositorySearchResult searchResult = restTemplate.getForObject(queryUrl, GitRepositorySearchResult.class);
		// pagination (100 per page)
		if ( Integer.parseInt(searchResult.getTotalResults()) > 100 ) {
			int pagesToGet = new Double(Math.ceil((Integer.parseInt(searchResult.getTotalResults()) / 100.0))).intValue();
			GitRepositorySearchResult searchResult2 = null;
			for(int i = 2; i <= pagesToGet; i++) {
				String query = queryUrl + "&page=" + i;
				searchResult2 = restTemplate.getForObject(query, GitRepositorySearchResult.class);
				searchResult.getRepositories().addAll(searchResult2.getRepositories());
			}
		}
		return searchResult;
	}

	public static void analyseCommit(File gitRepoPath, Git git, RevCommit revCommit) throws Exception {
		long init = System.currentTimeMillis();
		LOGGER.info("Building model structure.");
		GitModelStructure modelStructure = buildModelStructure(gitRepoPath, git, revCommit);
		long end = System.currentTimeMillis();
		LOGGER.info(String.format("Tempo total buildModelStructure(): %1$s [s].", ((end - init) / 1000.0)));
		init = System.currentTimeMillis();
		LOGGER.info("Comparing models to get refactorings.");
		// para cada entrada do filho, verifica se existe uma entrada para o pai e compara
		analyseModelRefactorings(modelStructure);
		end = System.currentTimeMillis();
		LOGGER.info(String.format("Tempo total analyseModelRefactorings(): %1$s [s].", ((end - init) / 1000.0)));
		// log
		LOGGER.info(String.format("%1$d commits analysed.", countCommits.get()));
	}

	private static void analyseModelRefactorings(GitModelStructure modelStructure) {
		for (String src : modelStructure.getMapChildrenModel().keySet()) {
			if (modelStructure.getMapFatherModel().containsKey(src)) {
				try {
					UMLModel umlModelChild = modelStructure.getMapChildrenModel().get(src);
					UMLModel umlModelFather = modelStructure.getMapFatherModel().get(src);
					UMLModelDiff diff = umlModelChild.diff(umlModelFather);
					if (diff.getRefactorings().size() > 0) {
						LOGGER.info(String.format("%1$d refactorings encontrados.", diff.getRefactorings().size()));
						for (Refactoring refactoring : diff.getRefactorings()) {
							LOGGER.info(refactoring.toString());
						}
					}
				} catch (Throwable t) {
					LOGGER.error(t.getMessage(), t);
					continue;
				}
			}
		}
	}

	private static GitModelStructure buildModelStructure(File gitRepoPath, Git git, RevCommit revCommit) throws Exception {
		// cria objeto que armazena as comparacoes
		GitModelStructure modelStructure = new GitModelStructure();
		// faz checkout do filho
		checkout(git, revCommit);
		// obtem o map model do filho
		modelStructure.setMapChildrenModel(obterMapModel(gitRepoPath));
		// faz o checkout do pai
		checkout(git, revCommit.getParent(0));
		// obtem o model do pai
		modelStructure.setMapFatherModel(obterMapModel(gitRepoPath));
		return modelStructure;
	}

	private static Ref checkout(Git git, RevCommit revCommit) throws Exception {
		// apaga a branch
		deleteBranch(git, revCommit);
		// cria a branch
		Ref ref = git
				.checkout()
				.setCreateBranch(true)
				.setName(revCommit.getName())
				.setStartPoint(revCommit.getName())
				.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).call();
		LOGGER.info("Ref branch checkout: " + ref.getName());
		return ref;
	}

	private static void deleteBranch(Git git, RevCommit revCommit) throws Exception {
		List<Ref> call = git.branchList().call();
		for (Ref ref : call) {
			if (ref.getName().contains(revCommit.getName())) {
				// checkout master
				git.checkout().setName("master").call();
				LOGGER.info("Checkout master branch.");
				// apaga os branchs
				List<String> deletedBranches = git.branchDelete().setBranchNames(revCommit.getName()).call();
				LOGGER.info("Deleted branchs: " + deletedBranches);
			}
		}
	}

	private static Map<String, UMLModel> obterMapModel(File gitRepoPath) {
		Map<String, UMLModel> mapModel = new HashMap<String, UMLModel>();
//		if (isCheckoutOk(checkout)) {
			// getting UML Model
			List<String> list1 = new ArrayList<String>(getSrcFolder(gitRepoPath, new HashSet<String>()));
			for (String srcFolder : list1) {
				// cria um objeto para armazenar a comparacao
				UMLModel model = new ASTReader(new File(srcFolder)).getUmlModel();
				String relativePathDir = srcFolder.substring(srcFolder.indexOf(gitRepoPath.getAbsolutePath()) + gitRepoPath.getAbsolutePath().length());
				// LOGGER.info("UMLModel -> " + relativePathDir);
				mapModel.put(relativePathDir, model);
			}
//		}
		return mapModel;
	}

	private static Set<String> getSrcFolder(File path, Set<String> pathNames) {
		if (path.isDirectory() && existsJavaFiles(path)) {
			String baseJavaSrcFolder = getBaseJavaSrcFolder(path);
			if (baseJavaSrcFolder != null) {
				pathNames.add(baseJavaSrcFolder);
			}
		} else if (path.isDirectory()) {
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
			if (file.getAbsolutePath().endsWith(".java")) {
				String packageStr = obterPackageJavaFile(file);
				if (StringUtils.isEmpty(packageStr)) {
					continue;
				}
				packageStr = packageStr.replaceAll("\\.", File.separator);
				// System.out.println(packageStr);
				String pathForSrcFolder = path.getAbsolutePath().replaceAll(packageStr, "");
				// System.out.println(pathForSrcFolder);
				return pathForSrcFolder;
			}
		}
		return null;
	}

	private static String obterPackageJavaFile(File path) {
		BufferedReader bf = null;
		String packageStr = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			bf = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bf.readLine()) != null) {
				if (line.startsWith("package ")) {
					packageStr = line.substring(line.indexOf("package ") + "package ".length(), line.indexOf(";")).trim();
					break;
				}
			}
			fileInputStream.close();
			inputStreamReader.close();
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packageStr;
	}

	private static boolean existsJavaFiles(File path) {
		File[] listFiles = path.listFiles();
		for (File file : listFiles) {
			if (file.getAbsolutePath().endsWith(".java")) {
				return true;
			}
		}
		return false;
	}

	private static boolean isCheckoutOk(CheckoutCommand checkout) {
		return checkout.getResult().getStatus().equals(Status.OK);
	}

	private static List<GitRepository> getJavaRepositories(GitRepositorySearchResult gitSearchResult) {
		List<GitRepository> javaRepos = filterRepositories(gitSearchResult);
		return javaRepos;
	}

	public static void registerNewCommit() {
		countCommits.set(countCommits.get() + 1);
	}

	public static AtomicInteger getCountCommits() {
		return countCommits;
	}
}
