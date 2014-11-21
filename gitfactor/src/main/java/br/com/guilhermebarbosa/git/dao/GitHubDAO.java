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
import br.com.guilhermebarbosa.git.model.Operation;
import br.com.guilhermebarbosa.git.model.Refactoring;
import br.com.guilhermebarbosa.git.model.RefactoringByDeveloper;
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
	public List<RefactoringByDeveloper> findClassesByDevelopers(String repository) {
		Query query = em.createNativeQuery("select 'barbosa' as id, rep.name as repository, com.author_name as author, " +
				" case when ref.source_class_name is not null  " +
					" then ref.source_class_name  " +
			        " else o.class_name  " +
				" end as className " +
			" from gitfactor_barbosa.refactoring ref " +
			" join gitfactor_barbosa.commit com on com.id_commit = ref.id_commit " +
			" join gitfactor_barbosa.repository rep on rep.id_repository = com.id_repository " +
			" left outer join gitfactor_barbosa.operation o on o.id_refactoring = ref.id_refactoring " +
			" where (ref.source_class_name is not null or ref.target_class_name is not null or o.class_name is not null) " +
			" and ref.name <> 'Merge Operation' " +
			" and rep.name = :repository " +
			" union " +
			" select 'juvenal' as id, rep.name as repository, com.author_name as author, " + 
				" case when ref.source_class_name is not null  " +
					" then ref.source_class_name  " +
			        " else o.class_name  " +
				" end as className " +
			" from gitfactor_juvenal.refactoring ref " +
			" join gitfactor_juvenal.commit com on com.id_commit = ref.id_commit " +
			" join gitfactor_juvenal.repository rep on rep.id_repository = com.id_repository " +
			" left outer join gitfactor_juvenal.operation o on o.id_refactoring = ref.id_refactoring " +
			" where (ref.source_class_name is not null or ref.target_class_name is not null or o.class_name is not null) " +
			" and ref.name <> 'Merge Operation' " +
			" and rep.name = :repository " +
			" union " +
			" select 'biocev' as id, rep.name as repository, com.author_name as author, " + 
				" case when ref.source_class_name is not null  " +
					" then ref.source_class_name  " +
			        " else o.class_name  " +
				" end as className " +
			" from gitfactor_biocev.refactoring ref " +
			" join gitfactor_biocev.commit com on com.id_commit = ref.id_commit " +
			" join gitfactor_biocev.repository rep on rep.id_repository = com.id_repository " +
			" left outer join gitfactor_biocev.operation o on o.id_refactoring = ref.id_refactoring " +
			" where (ref.source_class_name is not null or ref.target_class_name is not null or o.class_name is not null) " +
			" and ref.name <> 'Merge Operation' " +
			" and rep.name = :repository");
		query.setParameter("repository", repository);
		List<Object[]> list = query.getResultList();
		List<RefactoringByDeveloper> lista = new ArrayList<RefactoringByDeveloper>();
		for (Object[] objects : list) {
			String id = (String) objects[0];
			String repositoryName = (String) objects[1];
			String author = (String) objects[2];
			String className = (String) objects[3];
			lista.add(new RefactoringByDeveloper(
					id, 
					repositoryName, 
					author, 
					className));
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public Tag findTagByName(String name) {
		Query query = em.createNamedQuery("Tag.findByName");
		query.setParameter("name", name);
		List<Tag> resultList = query.getResultList();
		if ( resultList != null && !resultList.isEmpty() ) {
			return resultList.get(0);
		} else return null;
	}
}
