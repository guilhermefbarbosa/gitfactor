package br.com.guilhermebarbosa.git;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitRepositoryUtils {
	private static final Logger LOGGER = Logger.getLogger(GitRepositoryUtils.class);
	
	public static Git cloneGitRepo(String remoteUrl, File localPath) throws Exception {
		// se existir, apenas abre
		if ( localPath.exists() ) {
			LOGGER.info(String.format("Repository %1$s already exists.", localPath));
			LOGGER.info(String.format("Openning repository %1$s.", localPath));
			// open repo
			return Git.open(localPath);
		} 
		// senao faz checkout
		else {
			// recria pasta com conteúdo vazio
			recreateFolders(localPath);
			// then clone
			LOGGER.info("Cloning from " + remoteUrl + " to " + localPath);
			Git.cloneRepository()
					.setURI(remoteUrl)
					.setDirectory(localPath)
					.call();
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
	        Repository repository = builder.setGitDir(localPath)
	                .readEnvironment() // scan environment GIT_* variables
	                .findGitDir() // scan up the file system tree
	                .build();
	        repository.close();
	        return Git.open(localPath);
		}
	}

	private static void recreateFolders(File localPath) {
		// apaga se existir
		if ( localPath.exists() ) {
			localPath.delete();
		}
		// recria o diretorio
		localPath.mkdirs();
	}
}