package br.com.guilhermebarbosa.gitfactor;

import gr.uom.java.xmi.ASTReader;
import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;
import gr.uom.java.xmi.diff.ConvertAnonymousClassToTypeRefactoring;
import gr.uom.java.xmi.diff.ExtractAndMoveOperationRefactoring;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import gr.uom.java.xmi.diff.ExtractSuperclassRefactoring;
import gr.uom.java.xmi.diff.InlineOperationRefactoring;
import gr.uom.java.xmi.diff.IntroducePolymorphismRefactoring;
import gr.uom.java.xmi.diff.MergeOperation;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.PullUpAttributeRefactoring;
import gr.uom.java.xmi.diff.PullUpOperationRefactoring;
import gr.uom.java.xmi.diff.PushDownAttributeRefactoring;
import gr.uom.java.xmi.diff.PushDownOperationRefactoring;
import gr.uom.java.xmi.diff.Refactoring;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import gr.uom.java.xmi.diff.UMLModelDiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
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
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.guilhermebarbosa.git.GitConfig;
import br.com.guilhermebarbosa.git.GitRepositoryUtils;
import br.com.guilhermebarbosa.git.dao.GitHubDAO;
import br.com.guilhermebarbosa.git.model.Commit;
import br.com.guilhermebarbosa.git.model.Operation;
import br.com.guilhermebarbosa.git.model.Repository;
import br.com.guilhermebarbosa.git.model.RepositoryStatus;
import br.com.guilhermebarbosa.git.model.StatusCommit;
import br.com.guilhermebarbosa.git.model.Tag;

import com.google.common.collect.Iterables;

@Service
public class GitHubAnalyser {
	private static final Logger LOGGER = Logger.getLogger(GitHubAnalyser.class);

	private static AtomicInteger countCommits;
	
	@Autowired private GitHubDAO gitHubDAO;
	
	public void analyseGitHubByQueryUrl(GitConfig gitConfig, String tmpFolder, boolean analyse) throws Exception {
		// wait some time
		Thread.sleep(Constants.WAIT_TIME);
		// search for repositories
		List<GitRepository> javaRepos = searchRepositories(gitConfig.getUrl());
		// create dirs
		new File(tmpFolder).mkdirs();
		Integer totalCommits = 0;
		// total of repositories
		LOGGER.info(String.format("Total repositories: %1$d", javaRepos.size()));
		// save repositories found and checkout
		saveRepositoriesFound(tmpFolder, javaRepos, totalCommits, gitConfig);
		// for each repository, try to make an analysis
		for (GitRepository gitRepository : javaRepos) {
			// folder for checkout
			File gitRepoPath = new File(tmpFolder + File.separator + gitRepository.getName());
			// clone repo to folder
			// git repo
			Git git = GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath);
			// checkout do master
			checkoutMaster(git);
			// numero de commits
			int commits = Iterables.size(git.log().call());
			// ignore repositories that are not acceptable
			if ( !isRepositoryAcceptable(gitRepository, commits, gitConfig) ) {
				LOGGER.info(String.format("Repository %1$s is not acceptable by config %2$s. Commits: %3$d ", 
						gitRepository.getName(), gitConfig.name(), commits));
				continue;
			}
			// save the repository
			Repository repository = saveRepository(commits, gitRepository);
			Iterable<RevCommit> call = git.log().call();
 			Map<String, Commit> mapCommits = getMapCommits(repository);
			LOGGER.info(String.format("Repository: %1$s - Commits: %2$d.", gitRepository.getName(), commits));
			if ( analyse ) {
				if ( repository.getStatus() != null && repository.getStatus().equals(RepositoryStatus.PENDING) ) {
					analyseRepository(git, gitRepoPath, repository, call, mapCommits);
				}
			}
			// excluir o repositorio
			boolean delete = gitRepoPath.delete();
			if ( delete ) {
				LOGGER.info(String.format("Repository deleted sucessfull: %1$s.", gitRepoPath.getAbsolutePath()));
			}
		}
	}

	private void analyseRepository(Git git, File gitRepoPath,
			Repository repository, Iterable<RevCommit> call,
			Map<String, Commit> mapCommits) throws GitAPIException {
		List<Ref> listTags = git.tagList().call();
		// inicializa o count commits
		countCommits = new AtomicInteger(0);
		// start analysis
		repository.setStart(new Date());
		gitHubDAO.mergeRepository(repository);
		// analisa cada commit
		for (RevCommit revCommit : call) {
			// get from map for better performance
			Commit commit = saveCommit(git, listTags, repository, revCommit, StatusCommit.PENDING, mapCommits);
			// update tag in commit
			updateCommitTagInformation(git, listTags, revCommit, commit);
			// atualiza o parent and save the parent commit
			updateParentCommitInformation(repository, revCommit, commit, mapCommits);
			// se possui apenas um pai, faz a comparacao
			if (revCommit.getParentCount() == 1) {
				try {
					if ( commit != null && commit.getStatus().equals(StatusCommit.PENDING) ) {
						// analyse commit and get refactorings
						List<Refactoring> refactorings = GitHubAnalyser.analyseCommit(gitRepoPath, git, revCommit);
						// save refactorings
						saveRefactorings(commit, refactorings);
						// mark as analysed
						commit.setStatus(StatusCommit.ANALYSED);
						gitHubDAO.mergeCommit(commit);
						// log
						LOGGER.info(String.format("[%2$s] Commit %1$s analysed.", revCommit.getName(), repository.getName()));
					} else {
						// log
						LOGGER.info(String.format("[%2$s] Commit %1$s already analysed.", revCommit.getName(), repository.getName()));
					}
				} catch (Exception e) {
					commit.setStatus(StatusCommit.ERROR);
					gitHubDAO.mergeCommit(commit);
					LOGGER.error(String.format("[%2$s] Commit %1$s produced error on analysis.", revCommit.getName(), repository.getName()));
					LOGGER.error(e.getMessage(), e);
				}
			} else {
				commit.setStatus(StatusCommit.IGNORED);
				gitHubDAO.mergeCommit(commit);
				LOGGER.info(String.format("[%2$s] Commit %1$s was ignored because has many parents.", revCommit.getName(), repository.getName()));
			}
			// increment count commits
			GitHubAnalyser.getCountCommits().set(GitHubAnalyser.getCountCommits().get() + 1);
			// log
			LOGGER.info(String.format("%1$d commits analysed.", countCommits.get()));
			// give a hint to garbage collection
			System.gc();
		}
		// end analysis
		repository.setEnd(new Date());
		repository.setStatus(RepositoryStatus.ANALYSED);
		gitHubDAO.mergeRepository(repository);
	}

	private void updateParentCommitInformation(Repository repository,
			RevCommit revCommit, Commit commit, Map<String, Commit> mapCommits) {
		if ( commit.getParent() == null && revCommit.getParentCount() == 1 ) {
			LOGGER.info(String.format("Updating parent information for commit %1$s.", revCommit.getName()));
			RevCommit parentRevCommit = revCommit.getParent(0);
			Commit parent = mapCommits.get(parentRevCommit.getName());
			if ( parent == null ) {
				parent = new Commit();
				parent.setDate(new Date(new Long(parentRevCommit.getCommitTime()*1000L)));
				parent.setHash(parentRevCommit.getName());
				parent.setMessage(getMessageTruncated(parentRevCommit.getFullMessage()));
				parent.setRepository(repository);
				parent.setStatus(StatusCommit.ANALYSED);
				parent.setAuthorName(parentRevCommit.getAuthorIdent().getName());
				gitHubDAO.saveCommit(parent);
			}
			commit.setParent(parent);
			gitHubDAO.mergeCommit(commit);
		}
	}

	private void updateCommitTagInformation(Git git, List<Ref> listTags,
			RevCommit revCommit, Commit commit) {
		Ref tagByCommit = getTagByCommit(git, listTags, revCommit);
		if ( tagByCommit != null ) {
			Tag tag = gitHubDAO.findTagByName(tagByCommit.getName());
			if ( tag == null ) {
				tag = new Tag(tagByCommit.getName());
				gitHubDAO.saveTag(tag);
			}
			commit.setTag(tag);
			gitHubDAO.mergeCommit(commit);
		}
	}

	private void saveRepositoriesFound(String tmpFolder,
			List<GitRepository> javaRepos, Integer totalCommits, GitConfig gitConfig)
			throws Exception, GitAPIException, NoHeadException {
		Git git;
		for (GitRepository gitRepository : javaRepos) {
 			LOGGER.info("Repository: " + gitRepository.getName() + " - stars: " + gitRepository.getStars());
			// folder for checkout
			File gitRepoPath = new File(tmpFolder + File.separator + gitRepository.getName());
			// clone repo to folder
			git = GitRepositoryUtils.cloneGitRepo(gitRepository.getCloneUrl(), gitRepoPath);
			// get logs
			LOGGER.info(String.format("Getting commit logs for repository %1$s.", gitRepository.getName()));
			int commits = Iterables.size(git.log().call());
			if ( isRepositoryAcceptable(gitRepository, commits, gitConfig) ) {
				// sum total commits
				totalCommits += commits;
				// save the repository
				saveRepository(commits, gitRepository);
			}
		}
		LOGGER.info(String.format("Total Commits: %1$d.", totalCommits));
	}

	private boolean isRepositoryAcceptable(GitRepository gitRepository, int commits, GitConfig gitConfig) {
		switch (gitConfig) {
			case BARBOSA: return commits <= 10000;
			case BIOCEV: return commits > 10000;
			case JUVENAL: return commits <= 10000;
			case TINY: return true;
		}
		return false;
	}

	private void saveRefactorings(Commit commit, List<Refactoring> refactorings) {
		// save the refactorings found, if any
		if ( refactorings != null && !refactorings.isEmpty() ) {
			for (Refactoring r : refactorings) {
				br.com.guilhermebarbosa.git.model.Refactoring refactoring = new br.com.guilhermebarbosa.git.model.Refactoring();
				refactoring.setCommit(commit);
				refactoring.setName(r.getName());
				refactoring.setSourceClassName(getSourceClassName(r));
				refactoring.setTargetClassName(getTargetClassName(r));
				refactoring.setAttributeName(getAttributeName(r));
				refactoring.setDescription(r.toString());
				gitHubDAO.saveRefactoring(refactoring);
				// save operations
				List<UMLOperation> operations = getOperations(r);
				for (UMLOperation umlOperation : operations) {
					Operation operation = new Operation();
					operation.setClassName(umlOperation.getClassName());
					operation.setRefactoring(refactoring);
					operation.setVisibility(umlOperation.getVisibility());
					operation.setName(umlOperation.getName());
					operation.setDescription(umlOperation.toString());
					gitHubDAO.saveOperation(operation);
				}
			}
		}
	}

	private String getAttributeName(Refactoring refactoring) {
		if ( refactoring instanceof MoveAttributeRefactoring || 
				refactoring instanceof PullUpAttributeRefactoring || 
				refactoring instanceof PushDownAttributeRefactoring ) {
			// movedAttribute UMLAttribute
			// sourceClassName String
			// targetClassName String
			UMLAttribute attribute = getAttribute(refactoring, "movedAttribute");
			return attribute != null ? attribute.getName() : null;
		}
		return null;
	}

	/**
	 * Save the commit and it`s parents.
	 * 
	 * @param git
	 * @param listTag
	 * @param repository
	 * @param revCommit
	 * @param status
	 * @param mapCommits
	 * @return
	 */
	private Commit saveCommit(Git git, List<Ref> listTag, Repository repository, 
			RevCommit revCommit, StatusCommit status, 
			Map<String, Commit> mapCommits) {
		// save parent first
		Commit parent = null;
		for(RevCommit parentRevCommit : revCommit.getParents()) {
			// if parent exists, use the one already persisted
			if ( mapCommits.get(parentRevCommit.getName()) != null ) {
				parent = mapCommits.get(parentRevCommit.getName());
			} else {
				parent = new Commit();
				parent.setDate(new Date(new Long(parentRevCommit.getCommitTime()*1000L)));
				parent.setHash(parentRevCommit.getName());
				parent.setMessage(getMessageTruncated(parentRevCommit.getFullMessage()));
				parent.setRepository(repository);
				parent.setStatus(status);
				parent.setAuthorName(parentRevCommit.getAuthorIdent().getName());
				gitHubDAO.saveCommit(parent);
			}
			// to avoid duplication and faster access
			mapCommits.put(parent.getHash(), parent);
		}
				
		Commit commit = null;
		// if doesnt exist, save with parent association
		if ( mapCommits.get(revCommit.getName()) != null ) {
			commit = mapCommits.get(revCommit.getName());
		} else {
			commit = new Commit();
			commit.setDate(new Date(new Long(revCommit.getCommitTime()*1000L)));
			commit.setHash(revCommit.getName());
			commit.setMessage(getMessageTruncated(revCommit.getFullMessage()));
			commit.setRepository(repository);
			commit.setStatus(status);
			commit.setAuthorName(revCommit.getAuthorIdent().getName());
			commit.setParent(parent);
			commit.setTag(saveTagByCommit(git, listTag, revCommit));
			gitHubDAO.saveCommit(commit);
		}
		// to avoid duplication and faster access
		mapCommits.put(commit.getHash(), commit);
		return commit;
	}

	private Tag saveTagByCommit(Git git, List<Ref> listTag, RevCommit revCommit) {
		Ref refTag = getTagByCommit(git, listTag, revCommit);
		if ( refTag != null ) {
			Tag tag = new Tag(refTag.getName());
			gitHubDAO.saveTag(tag);
			return tag;
		}
		return null;
	}

	private Ref getTagByCommit(Git git, List<Ref> listTag, RevCommit revCommit) {
    	for (Ref tag : listTag) {
            if (tag.getObjectId().equals(revCommit.getId())) {
            	LOGGER.info(String.format("Found tag %1$s for commit %2$s.", tag.getName(), revCommit.getName()));
            	return tag;
            }
        }
    	return null;
	}

	private String getMessageTruncated(String fullMessage) {
		if ( fullMessage != null ) {
			if ( fullMessage.length() > 1000 ) {
				return fullMessage.substring(0, 1000);
			} else { 
				return fullMessage;
			}
		}
		return null;
	}

	private List<UMLOperation> getOperations(Refactoring refactoring) {
		List<UMLOperation> operations = new ArrayList<UMLOperation>();
		if ( refactoring instanceof ConvertAnonymousClassToTypeRefactoring ) {
			addUmlOperation("anonymousClass", refactoring, operations);
			addUmlOperation("addedClass", refactoring, operations);
		} else if ( refactoring instanceof ExtractAndMoveOperationRefactoring ) {
			addUmlOperation("extractedFromOperation", refactoring, operations);
			addUmlOperation("extractedOperation", refactoring, operations);
		} else if ( refactoring instanceof ExtractOperationRefactoring ) {
			addUmlOperation("extractedOperation", refactoring, operations);
		} else if ( refactoring instanceof ExtractSuperclassRefactoring ) {
			addUmlOperation("extractedClass", refactoring, operations);
		} else if ( refactoring instanceof InlineOperationRefactoring ) {
			addUmlOperation("inlinedOperation", refactoring, operations);
			addUmlOperation("inlinedToOperation", refactoring, operations);
		} else if ( refactoring instanceof MergeOperation ) {
			UMLOperationBodyMapper umlOperationBodyMapper = getOperationBodyMapper(refactoring);
			if ( umlOperationBodyMapper != null ) {
				UMLOperation operation1 = umlOperationBodyMapper.getOperation1();
				if ( operation1 != null ) {
					operations.add(operation1);
				}
				UMLOperation operation2 = umlOperationBodyMapper.getOperation2();
				if ( operation2 != null ) {
					operations.add(operation2);
				}
			}
		} else if ( refactoring instanceof MoveOperationRefactoring ) {
			addUmlOperation("originalOperation", refactoring, operations);
			addUmlOperation("movedOperation", refactoring, operations);
		} else if ( refactoring instanceof PullUpOperationRefactoring ) {
			addUmlOperation("originalOperation", refactoring, operations);
			addUmlOperation("movedOperation", refactoring, operations);
		} else if ( refactoring instanceof PushDownOperationRefactoring ) {
			addUmlOperation("originalOperation", refactoring, operations);
			addUmlOperation("movedOperation", refactoring, operations);
		} else if ( refactoring instanceof RenameOperationRefactoring ) {
			addUmlOperation("originalOperation", refactoring, operations);
			addUmlOperation("renamedOperation", refactoring, operations);
		}
		return operations;
	}

	private String getSourceClassName(Refactoring refactoring) {
		String fieldName = null;
		if ( refactoring instanceof MoveOperationRefactoring || 
				refactoring instanceof MoveAttributeRefactoring ) {
			fieldName = "sourceClassName";
		} else if ( refactoring instanceof PullUpAttributeRefactoring ) {
			// movedAttribute UMLAttribute
			fieldName = "sourceClassName";
			// sourceClassName String
			// targetClassName String
		} else if ( refactoring instanceof PushDownAttributeRefactoring ) {
			// movedAttribute UMLAtrribute
			fieldName = "sourceClassName";
			// sourceClassName String
			// targetClassName String
		} else if ( refactoring instanceof MoveClassRefactoring ) {
			fieldName = "originalClassName";
			// originalClassName String
			// movedClassName String
		} else if ( refactoring instanceof RenameClassRefactoring ) {
			fieldName = "originalClassName";
			// originalClassName
			// renamedClassName
		} else if ( refactoring instanceof IntroducePolymorphismRefactoring ) {
			// clientClass
			// supplierClass
			fieldName = "clientClass";
		} else {
			return null;
		}
		try {
			Field f = refactoring.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return (String) f.get(refactoring);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	private String getTargetClassName(Refactoring refactoring) {
		String fieldName = null;
		if ( refactoring instanceof MoveOperationRefactoring || 
				refactoring instanceof MoveAttributeRefactoring ) {
			fieldName = "targetClassName";
		} else if ( refactoring instanceof PullUpAttributeRefactoring ) {
			// movedAttribute UMLAttribute
			fieldName = "targetClassName";
			// sourceClassName String
			// targetClassName String
		} else if ( refactoring instanceof PushDownAttributeRefactoring ) {
			// movedAttribute UMLAtrribute
			fieldName = "targetClassName";
			// sourceClassName String
			// targetClassName String
		} else if ( refactoring instanceof MoveClassRefactoring ) {
			fieldName = "movedClassName";
			// originalClassName String
			// movedClassName String
		} else if ( refactoring instanceof RenameClassRefactoring ) {
			fieldName = "renamedClassName";
			// originalClassName
			// renamedClassName
		} else if ( refactoring instanceof IntroducePolymorphismRefactoring ) {
			// clientClass
			// supplierClass
			fieldName = "supplierClass";
		} else {
			return null;
		}
		try {
			Field f = refactoring.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return (String) f.get(refactoring);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	private UMLOperationBodyMapper getOperationBodyMapper(Refactoring refactoring) {
		try {
			Field f = refactoring.getClass().getDeclaredField("mapper");
			f.setAccessible(true);
			return (UMLOperationBodyMapper) f.get(refactoring);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} 
	}
	
	private UMLAttribute getAttribute(Refactoring refactoring, String fieldName) {
		try {
			Field f = refactoring.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			return (UMLAttribute) f.get(refactoring);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		} 
	}
	
	private void addUmlOperation(String fieldName, Refactoring refactoring, List<UMLOperation> operations) {
		try {
			Field f = refactoring.getClass().getDeclaredField(fieldName); //NoSuchFieldException
			f.setAccessible(true);
			UMLOperation umlOperation = (UMLOperation) f.get(refactoring);
			if ( umlOperation != null ) {
				operations.add(umlOperation);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private Repository saveRepository(int totalCommits, GitRepository gitRepository) {
		Repository repository = gitHubDAO.findByName(gitRepository.getName());
		if ( repository == null ) {
			repository = new Repository();
			repository.setName(gitRepository.getName());
			repository.setAuthor(gitRepository.getOwner().getLogin());
			repository.setStatus(RepositoryStatus.PENDING);
			repository.setUrl(gitRepository.getCloneUrl());
			repository.setTotalCommits(totalCommits);
			repository.setTotalStars(Integer.parseInt(gitRepository.getStars()));
			gitHubDAO.saveRepository(repository);
		} else {
			repository.setTotalCommits(totalCommits);
			repository.setTotalStars(Integer.parseInt(gitRepository.getStars()));
			gitHubDAO.mergeRepository(repository);
		}
		return repository;
	}

	private static List<GitRepository> searchRepositories(String queryUrl) throws RestClientException, UnsupportedEncodingException {
		GitRepositorySearchResult gitSearchResult = queryGitHub(queryUrl);
		LOGGER.info("Searching git repositories...");
		List<GitRepository> javaRepos = gitSearchResult.getRepositories();
		LOGGER.info(String.format("Found %1$d repositories.", javaRepos.size()));
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

	public static List<Refactoring> analyseCommit(File gitRepoPath, Git git, RevCommit revCommit) throws Exception {
		long init = System.currentTimeMillis();
		LOGGER.info("Building model structure.");
		GitModelStructure modelStructure = buildModelStructure(gitRepoPath, git, revCommit);
		long end = System.currentTimeMillis();
		LOGGER.info(String.format("Tempo total buildModelStructure(): %1$s [s].", ((end - init) / 1000.0)));
		init = System.currentTimeMillis();
		LOGGER.info("Comparing models to get refactorings.");
		// para cada entrada do filho, verifica se existe uma entrada para o pai e compara
		List<Refactoring> refactorings = analyseModelRefactorings(modelStructure);
		end = System.currentTimeMillis();
		LOGGER.info(String.format("Tempo total analyseModelRefactorings(): %1$s [s].", ((end - init) / 1000.0)));
		return refactorings;
	}

	private static List<Refactoring> analyseModelRefactorings(GitModelStructure modelStructure) {
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
						return diff.getRefactorings();
					}
				} catch (Throwable t) {
					LOGGER.error(t.getMessage(), t);
					continue;
				}
			}
		}
		return null;
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
		try {
			deleteBranch(git, revCommit);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		// cria a branch e faz checkout
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
				checkoutMaster(git);
				// apaga os branchs
				List<String> deletedBranches = git.branchDelete().setBranchNames(revCommit.getName()).call();
				LOGGER.info("Deleted branchs: " + deletedBranches);
			}
		}
	}

	private static void checkoutMaster(Git git) throws GitAPIException,
			RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException {
		git.checkout().setName("master").call();
		LOGGER.info("Checkout master branch.");
	}

	private static Map<String, UMLModel> obterMapModel(File gitRepoPath) {
		Map<String, UMLModel> mapModel = new HashMap<String, UMLModel>();
		// getting UML Model
		List<String> list1 = new ArrayList<String>(getSrcFolder(gitRepoPath, new HashSet<String>()));
		for (String srcFolder : list1) {
			// cria um objeto para armazenar a comparacao
			UMLModel model = new ASTReader(new File(srcFolder)).getUmlModel();
			String relativePathDir = srcFolder.substring(srcFolder.indexOf(gitRepoPath.getAbsolutePath()) + gitRepoPath.getAbsolutePath().length());
			// LOGGER.info("UMLModel -> " + relativePathDir);
			mapModel.put(relativePathDir, model);
		}
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

	@SuppressWarnings("unused")
	private static boolean isCheckoutOk(CheckoutCommand checkout) {
		return checkout.getResult().getStatus().equals(Status.OK);
	}

	public static void registerNewCommit() {
		countCommits.set(countCommits.get() + 1);
	}

	public static AtomicInteger getCountCommits() {
		return countCommits;
	}
	
	public Map<String, Commit> getMapCommits(Repository repository) {
		Map<String, Commit> map = new HashMap<String, Commit>();
		List<Commit> commits = gitHubDAO.findByRepository(repository);
		for (Commit commit : commits) {
			map.put(commit.getHash(), commit);
		}
		return map;
	}
}
