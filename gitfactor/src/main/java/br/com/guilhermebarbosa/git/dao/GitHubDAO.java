package br.com.guilhermebarbosa.git.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import br.com.guilhermebarbosa.git.model.Commit;
import br.com.guilhermebarbosa.git.model.GitfactorMoveMethodRefactoring;
import br.com.guilhermebarbosa.git.model.Operation;
import br.com.guilhermebarbosa.git.model.Refactoring;
import br.com.guilhermebarbosa.git.model.Tag;

@Repository
public class GitHubDAO {
	@PersistenceContext
    private EntityManager em;
	
	@Transactional(value = TxType.REQUIRED)
	public void saveRepository(br.com.guilhermebarbosa.git.model.Repository repository) {
		em.persist(repository);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void saveTag(br.com.guilhermebarbosa.git.model.Tag tag) {
		em.persist(tag);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void mergeRepository(br.com.guilhermebarbosa.git.model.Repository repository) {
		em.merge(repository);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void saveCommit(Commit commit) {
		em.persist(commit);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void mergeCommit(Commit commit) {
		em.merge(commit);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void saveRefactoring(Refactoring refactoring) {
		em.persist(refactoring);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void saveOperation(Operation operation) {
		em.persist(operation);
	}
	
	@SuppressWarnings("unchecked")
	public br.com.guilhermebarbosa.git.model.Repository findByName(String name) {
		Query query = em.createNamedQuery("Repository.findByName");
		query.setParameter("name", name);
		List<br.com.guilhermebarbosa.git.model.Repository> resultList = query.getResultList();
		if ( resultList != null && !resultList.isEmpty() ) {
			return resultList.get(0);
		} else return null;
	}
	
	@SuppressWarnings("unchecked")
	public Commit findByRepositoryByHash(br.com.guilhermebarbosa.git.model.Repository repository, String hash) {
		Query query = em.createNamedQuery("Commit.findByRepositoryByHash");
		query.setParameter("idRepository", repository.getIdRepository());
		query.setParameter("hash", hash);
		List<Commit> resultList = query.getResultList();
		if ( resultList != null && !resultList.isEmpty() ) {
			return resultList.get(0);
		} else return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Commit> findByRepository(br.com.guilhermebarbosa.git.model.Repository repository) {
		Query query = em.createNamedQuery("Commit.findByRepository");
		query.setParameter("idRepository", repository.getIdRepository());
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public Tag findTagByName(String name, Integer idRepository) {
		Query query = em.createNamedQuery("Tag.findByName");
		query.setParameter("name", name);
		query.setParameter("idRepository", idRepository);
		List<Tag> resultList = query.getResultList();
		if ( resultList != null && !resultList.isEmpty() ) {
			return resultList.get(0);
		} else return null;
	}

	@Transactional(value = TxType.REQUIRED)
	public void mergeTag(Tag tag) {
		em.merge(tag);
	}
	
	public List<GitfactorMoveMethodRefactoring> getMoveMethodRefactoring(List<String> repositories) {
		String queryStr = "select r.id_refactoring, rep.name, rep.url, rep.default_branch, c.hash as hash_c, p.hash as hash_pai, r.description " +
			" from gitfactor_barbosa.refactoring r " +
			" join gitfactor_barbosa.commit c on c.id_commit = r.id_commit " +
			" join gitfactor_barbosa.commit p on p.id_commit = c.parent " +
			" join gitfactor_barbosa.repository rep on rep.id_repository = c.id_repository " +
			" where rep.name in (:repositories) " +
			" and r.name = 'Move Operation'";
		Query query = em.createNativeQuery(queryStr);
		query.setParameter("repositories", repositories);
		List<Object[]> list = query.getResultList();
		List<GitfactorMoveMethodRefactoring> lista = new ArrayList<GitfactorMoveMethodRefactoring>();
		for (Object[] item : list) {
			GitfactorMoveMethodRefactoring gitfactorMoveMethodRefactoring = new GitfactorMoveMethodRefactoring();
			gitfactorMoveMethodRefactoring.setIdRefactoring((Integer) item[0]);
			gitfactorMoveMethodRefactoring.setRepositoryName((String) item[1]);
			gitfactorMoveMethodRefactoring.setRepositoryCloneUrl((String) item[2]);
			gitfactorMoveMethodRefactoring.setDefaultBranch((String) item[3]);
			gitfactorMoveMethodRefactoring.setHashCommit((String) item[4]);
			gitfactorMoveMethodRefactoring.setHashParentCommit((String) item[5]);
			gitfactorMoveMethodRefactoring.setRefactoringDescription((String) item[6]);
			gitfactorMoveMethodRefactoring.setOperations(findOperationByRefactoring(gitfactorMoveMethodRefactoring.getIdRefactoring()));
			lista.add(gitfactorMoveMethodRefactoring);
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Operation> findOperationByRefactoring(Integer idRefactoring) {
		Query query = em.createNamedQuery("Repository.findOperationByRefactoring");
		query.setParameter("idRefactoring", idRefactoring);
		return query.getResultList();
	}
}
