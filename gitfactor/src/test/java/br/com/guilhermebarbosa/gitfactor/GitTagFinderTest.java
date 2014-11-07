package br.com.guilhermebarbosa.gitfactor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

public class GitTagFinderTest {
	public static final Logger LOGGER = Logger.getLogger(GitTagFinderTest.class);
	
	@Test
	public void findTagForEachCommit() throws IOException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File repoPath = new File("/var/tmp/git/joda-time");
		Repository repository = builder.setGitDir(repoPath)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        repository.close();
        Git git = Git.open(repoPath);
        List<Ref> list = git.tagList().call();
        Map<Ref, List<RevCommit>> mapCommits = new HashMap<Ref, List<RevCommit>>();
        Iterable<RevCommit> listCommits = git.log().call();
        for(RevCommit commit : listCommits) {
        	for (Ref tag : list) {
        		if ( !mapCommits.containsKey(tag) ) {
        			mapCommits.put(tag, new ArrayList<RevCommit>());
        		}
                if (tag.getObjectId().equals(commit.getId())) {;
                	LOGGER.info(String.format("Commit: %1$s - Tag: %2$s", commit.getName(), tag.getName()));
                	mapCommits.get(tag).add(commit);
                }
            }
        }
        
	}
}
