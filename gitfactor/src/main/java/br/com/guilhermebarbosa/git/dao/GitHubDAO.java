package br.com.guilhermebarbosa.git.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.stereotype.Repository;

import br.com.guilhermebarbosa.git.model.Commit;
import br.com.guilhermebarbosa.git.model.Operation;
import br.com.guilhermebarbosa.git.model.Refactoring;

@Repository
public class GitHubDAO {
	@PersistenceContext
    private EntityManager em;
	
	@Transactional(value = TxType.REQUIRED)
	public void saveRepository(br.com.guilhermebarbosa.git.model.Repository repository) {
		em.persist(repository);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void saveCommit(Commit commit) {
		em.persist(commit);
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
}
