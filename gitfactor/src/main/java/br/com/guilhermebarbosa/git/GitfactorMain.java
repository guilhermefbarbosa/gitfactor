package br.com.guilhermebarbosa.git;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;

public class GitfactorMain {
	public static Logger LOGGER = Logger.getLogger(GitfactorMain.class);
	
	public static void main(String[] args) {
		try {
			String tmpFolder = args[0];
			Boolean analyse = true;
			// initialize spring
			ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			// shutdownhook to stop context gracefully
			classPathXmlApplicationContext.registerShutdownHook();
			classPathXmlApplicationContext.start();
			// read from command line
			System.out.println("Type the url to search git repositories in GitHub:");
			// read line with url
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			// Example URL`s
			// https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000000&sort=stars&order=desc&per_page=100
			// https://api.github.com/search/repositories?q=language:java stars:225..1000 size:50000..1000000&sort=stars&order=desc&per_page=100
			// https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
			// https://api.github.com/search/repositories?q=joda-time stars:>900&sort=stars&order=desc&per_page=100
			// https://api.github.com/search/repositories?q=cassandra language:java stars:>1000&sort=stars&order=desc&per_page=100
			// https://api.github.com/search/repositories?q=joda-time language:java stars:>700&sort=stars&order=desc&per_page=100
			// https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000000&sort=stars&order=desc&per_page=100
		    String url = bufferRead.readLine();
			// obtem a query dependendo do ambiente
			if ( url == null ) {
				LOGGER.error("Error getting query string. Program will terminate.");
				classPathXmlApplicationContext.stop();
			} else {
				LOGGER.info(String.format("Query String: %1$s", url));
				// get service
				GitHubAnalyser gitHubAnalyser = (GitHubAnalyser) br.com.guilhermebarbosa.git.ApplicationContext.getInstance().getBean("gitHubAnalyser"); 
				gitHubAnalyser.analyseGitHubByQueryUrl(url, tmpFolder, analyse);
			}
			// close resources
			classPathXmlApplicationContext.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
