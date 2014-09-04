package br.com.guilhermebarbosa.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitRepositoryUtils {
	public static void cloneGitRepo(String remoteUrl, File localPath) throws IOException, GitAPIException, InvalidRemoteException, TransportException {
		// recria pasta com conteúdo vazio
		recreateFolders(localPath);
        // then clone
        System.out.println("Cloning from " + remoteUrl + " to " + localPath);
        Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(localPath)
                .call();

        // now open the created repository
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(localPath)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        System.out.println("Having repository: " + repository.getDirectory());

        repository.close();
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