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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CheckoutResult.Status;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class RepositoryFinderTest {
	private static final int WAIT_TIME = 30000;
	private static final String GIT_HUB_QUERY_REPOS = "https://api.github.com/search/repositories?q=language:Java&sort=stars&order=desc&per_page=100";
	private static final String GIT_HUB_QUERY_REPOS_SPRING = "https://api.github.com/search/repositories?q=spring-framework&language:Java&sort=stars&order=desc&per_page=100";
	private static final String GIT_HUB_QUERY_TAGS = "https://api.github.com/repos/%1$s/%2$s/tags";
	private static final String GIT_HUB_AUTHENTICATION = "https://api.github.com/user?access_token=ea604eb7230a230d3e13080b500c2d931cffd593";
	private static final String TEMP_FOLDER = "/var/tmp/git";

	@Test
	public void findSourceFolders() {
		List<GitSrcFolderComparissonRef> listaSrcComparisson = obterFolderListForComparisson();
		// para cada src folder, chama o RefDetector
		for (GitSrcFolderComparissonRef gitSrcFolderComparissonRef : listaSrcComparisson) {
			System.out.println("Comparando folders");
			System.out.println(String.format("folder1 = %1$s", gitSrcFolderComparissonRef.getSrcPath1()));
			System.out.println(String.format("folder2 = %1$s", gitSrcFolderComparissonRef.getSrcPath2()));
			// compara usando o RefDetector
			compareVersions(gitSrcFolderComparissonRef.getSrcPath1(), gitSrcFolderComparissonRef.getSrcPath2());
		}
	}

	private List<GitSrcFolderComparissonRef> obterFolderListForComparisson() {
		String srcBasePath1 = TEMP_FOLDER + File.separator + "spring-framework_1";
		String srcBasePath2 = TEMP_FOLDER + File.separator + "spring-framework_2";
		List<String> list1 = new ArrayList<String>(getSrcFolder(new File(srcBasePath1), new HashSet<String>()));
		List<String> list2 = new ArrayList<String>(getSrcFolder(new File(srcBasePath2), new HashSet<String>()));
		// ordena
		Collections.sort(list1);
		// ordena
		Collections.sort(list2);
		List<GitSrcFolderComparissonRef> listaSrcComparisson = new ArrayList<GitSrcFolderComparissonRef>();
		for(String folder1 : list1) {
			for(String folder2 : list2) {
				if ( folder1.replaceAll(srcBasePath1, "").equals(folder2.replaceAll(srcBasePath2, "")) ) {
					listaSrcComparisson.add(new GitSrcFolderComparissonRef(folder1, folder2));
				}
			}
		}
		return listaSrcComparisson;
	}
	
	@Ignore
	@Test
	public void testFindGitHubRepositories() throws InterruptedException, InvalidRemoteException, TransportException, IOException, GitAPIException {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject(GIT_HUB_AUTHENTICATION, Object.class);
		// aguarda 1min
		Thread.sleep(WAIT_TIME);
		// get repositories
        GitRepositorySearchResult gitSearchResult = restTemplate.getForObject(GIT_HUB_QUERY_REPOS_SPRING, GitRepositorySearchResult.class);
        List<GitRepository> javaRepos = getJavaRepositories(gitSearchResult);
        for (GitRepository gitRepository : javaRepos) {
			System.out.println(gitRepository.getName() + " - stars: " + gitRepository.getStars());
			// folder 1 for comparisson
			File gitRepoPath1 = new File(TEMP_FOLDER + File.separator + gitRepository.getName() + "_1");
			// folder 2 for comparisson
			File gitRepoPath2 = new File(TEMP_FOLDER + File.separator + gitRepository.getName() + "_2");
			// clone git repo (all tags and branches included)
			// clone repo to folder 1
//			GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath1);
			// clone repo to folder 2
//			GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath2);
			// get repositories tags
			Map<String, Ref> tags = Git.open(gitRepoPath1).tagList().getRepository().getTags();
			// TODO melhorar esta comparacao
			Iterator<String> tagsIterator = tags.keySet().iterator();
			String tag1 = tagsIterator.next();
			String tag2 = tagsIterator.next();
			// checkout de uma tag no folder 1
			CheckoutCommand checkout1 = Git.open(gitRepoPath1).checkout().setName(tag1);
			// faz o checkout
			checkout1.call();
			// verifica se foi ok
			if ( isCheckoutOk(checkout1) ) {
				System.out.println(String.format("Checkout tag do folder 1: %1$s ok.", tag1));
			}
			// checkout de uma tag no folder 2
			CheckoutCommand checkout2 = Git.open(gitRepoPath2).checkout().setName(tag2);
			// faz o checkout
			checkout2.call();
			// verifica se foi ok
			if ( isCheckoutOk(checkout2) ) {
				System.out.println(String.format("Checkout tag do folder 2: %1$s ok.", tag2));
			}
			Set<String> srcFolder1 = getSrcFolder(gitRepoPath1, new HashSet<String>());
			Set<String> srcFolder2 = getSrcFolder(gitRepoPath2, new HashSet<String>());
			Iterator<String> iterator1 = srcFolder1.iterator();
			Iterator<String> iterator2 = srcFolder2.iterator();
			// compara os folders um a um
			while ( iterator1.hasNext() && iterator2.hasNext() ) {
				compareVersions(iterator1.next(), iterator2.next());
			}
		}
	}

	private Set<String> getSrcFolder(File path, Set<String> pathNames) {
		if ( path.isDirectory() && existsJavaFiles(path) ) {
			pathNames.add(getBaseJavaSrcFolder(path));
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
				packageStr = packageStr.replaceAll("\\.", File.separator);
				System.out.println(packageStr);
				String pathForSrcFolder = path.getAbsolutePath().replaceAll(packageStr, "");
				System.out.println(pathForSrcFolder);
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

	private void compareVersions(String version0SrcFolder, String version1SrcFolder) {
		UMLModel model0 = new ASTReader(new File(version0SrcFolder)).getUmlModel();
		UMLModel model1 = new ASTReader(new File(version1SrcFolder)).getUmlModel();
		
		UMLModelDiff modelDiff = model0.diff(model1);
		List<Refactoring> refactorings = modelDiff.getRefactorings();
		for (Refactoring refactoring : refactorings) {
			System.out.println(refactoring.toString());
		}
	}

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