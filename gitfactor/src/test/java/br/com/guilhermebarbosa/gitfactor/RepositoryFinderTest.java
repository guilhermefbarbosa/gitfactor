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
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import br.com.guilhermebarbosa.git.GitRepositoryUtils;

public class RepositoryFinderTest {
	private static final Logger LOGGER = Logger.getLogger(RepositoryFinderTest.class);
	
	private static final int WAIT_TIME = 30000;
	private static final String GIT_HUB_QUERY_REPOS = "https://api.github.com/search/repositories?q=language:Java&sort=stars&order=desc&per_page=100";
	private static final String GIT_HUB_QUERY_REPOS_SPRING = "https://api.github.com/search/repositories?q=spring-framework&language:Java&sort=stars&order=desc&per_page=100";
	private static final String GIT_HUB_QUERY_TAGS = "https://api.github.com/repos/%1$s/%2$s/tags";
	private static final String GIT_HUB_AUTHENTICATION = "https://api.github.com/user?access_token=ea604eb7230a230d3e13080b500c2d931cffd593";
	private static final String TEMP_FOLDER = "/var/tmp/git";

//	@Ignore
//	@Test
//	public void findSourceFolders() {
//		List<GitSrcFolderComparissonRef> listaSrcComparisson = obterFolderListForComparisson();
//		// para cada src folder, chama o RefDetector
//		for (GitSrcFolderComparissonRef gitSrcFolderComparissonRef : listaSrcComparisson) {
//			System.out.println("Comparando folders");
//			System.out.println(String.format("folder1 = %1$s", gitSrcFolderComparissonRef.getSrcPath1()));
//			System.out.println(String.format("folder2 = %1$s", gitSrcFolderComparissonRef.getSrcPath2()));
//			// compara usando o RefDetector
//			compareVersions(gitSrcFolderComparissonRef.getSrcPath1(), gitSrcFolderComparissonRef.getSrcPath2());
//		}
//	}

//	private List<GitSrcFolderComparissonRef> obterFolderListForComparisson() {
//		String srcBasePath1 = TEMP_FOLDER + File.separator + "spring-framework_1";
//		String srcBasePath2 = TEMP_FOLDER + File.separator + "spring-framework_2";
//		List<String> list1 = new ArrayList<String>(getSrcFolder(new File(srcBasePath1), new HashSet<String>()));
//		List<String> list2 = new ArrayList<String>(getSrcFolder(new File(srcBasePath2), new HashSet<String>()));
//		// ordena
//		Collections.sort(list1);
//		// ordena
//		Collections.sort(list2);
//		List<GitSrcFolderComparissonRef> listaSrcComparisson = new ArrayList<GitSrcFolderComparissonRef>();
//		for(String folder1 : list1) {
//			for(String folder2 : list2) {
//				if ( folder1.replaceAll(srcBasePath1, "").equals(folder2.replaceAll(srcBasePath2, "")) ) {
//					listaSrcComparisson.add(new GitSrcFolderComparissonRef(folder1, folder2));
//				}
//			}
//		}
//		return listaSrcComparisson;
//	}
	
	@Test
	public void testFindGitHubRepositories() throws InterruptedException, InvalidRemoteException, TransportException, IOException, GitAPIException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject(GIT_HUB_AUTHENTICATION, Object.class);
		// aguarda 1min
		Thread.sleep(WAIT_TIME);
		// get repositories
        GitRepositorySearchResult gitSearchResult = restTemplate.getForObject(GIT_HUB_QUERY_REPOS_SPRING, GitRepositorySearchResult.class);
        LOGGER.info("Searching git repositories...");
        List<GitRepository> javaRepos = getJavaRepositories(gitSearchResult);
        LOGGER.info(String.format("Found %1$d repositories.", javaRepos.size()));
        for (GitRepository gitRepository : javaRepos) {
			LOGGER.info("Repository: " + gitRepository.getName() + " - stars: " + gitRepository.getStars());
			// folder for checkout
			File gitRepoPath = new File(TEMP_FOLDER + File.separator + gitRepository.getName());
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
			LOGGER.info(String.format("Getting commit logs for repository %1$s.", gitRepository.getName()));
			for (RevCommit revCommit : call) {
				LOGGER.info("revCommit = " + revCommit.getName());
				// se possui apenas um pai, faz a comparacao
				if ( revCommit.getParentCount() == 1 ) {
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
					// para cada entrada do filho, verifica se existe uma entrada para o pai e compara
					for(String src : modelStructure.getMapChildrenModel().keySet()) {
						if ( modelStructure.getMapFatherModel().containsKey(src) ) {
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
						}
					}
				}
			}
		}
	}

	private CheckoutCommand checkout(Git git, RevCommit revCommit)
			throws GitAPIException, RefAlreadyExistsException,
			RefNotFoundException, InvalidRefNameException,
			CheckoutConflictException, IOException {
		LOGGER.info("Checkout " + revCommit.getName());
		CheckoutCommand checkout = git.checkout();
		List<Ref> call = git.branchList().call();
		for (Ref ref : call) {
			if ( ref.getName().contains(revCommit.getName()) ) {
				// checkout master
				Ref refMaster = git.checkout().setName("master").call();
				// apaga os branchs
				List<String> deletedBranchs = git.branchDelete().setBranchNames(revCommit.getName()).call();
				LOGGER.info("Deleted branchs: " + deletedBranchs);
			}
		}
		
		Ref ref = checkout.
				setCreateBranch(true).
				setName(revCommit.getName()).
				setStartPoint(revCommit.getName()).
				setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).call();
		LOGGER.info("Ref: " + ref.getName());
		return checkout;
	}

	private Map<String, UMLModel> obterMapModel(File gitRepoPath, CheckoutCommand checkout) {
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

	private Set<String> getSrcFolder(File path, Set<String> pathNames) {
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

	private String getBaseJavaSrcFolder(File path) {
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

	private String obterPackageJavaFile(File path) {
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

	private boolean existsJavaFiles(File path) {
		File[] listFiles = path.listFiles();
		for (File file : listFiles) {
			if ( file.getAbsolutePath().endsWith(".java") ) {
				return true;
			}
		}
		return false;
	}

	private boolean isCheckoutOk(CheckoutCommand checkout) {
		return checkout.getResult().getStatus().equals(Status.OK);
	}

//	private void compareVersions(String version0SrcFolder, String version1SrcFolder) {
//		UMLModel model0 = new ASTReader(new File(version0SrcFolder)).getUmlModel();
//		UMLModel model1 = new ASTReader(new File(version1SrcFolder)).getUmlModel();
//		
//		UMLModelDiff modelDiff = model0.diff(model1);
//		List<Refactoring> refactorings = modelDiff.getRefactorings();
//		for (Refactoring refactoring : refactorings) {
//			System.out.println(refactoring.toString());
//		}
//	}

	private List<GitRepository> getJavaRepositories(GitRepositorySearchResult gitSearchResult) {
		List<GitRepository> repositories = gitSearchResult.getRepositories();
        List<GitRepository> javaRepos = new ArrayList<GitRepository>();
        for (GitRepository gitRepository : repositories) {
			if ( gitRepository.getStars() != null && Integer.parseInt(gitRepository.getStars()) >= 1000 ) {
				javaRepos.add(gitRepository);
			}
		}
		return javaRepos;
	}
	
	private String obterUrlTags(String userName, String repo) {
		return String.format(GIT_HUB_QUERY_TAGS, userName, repo);
	}
}