package br.com.guilhermebarbosa.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;

public class GitfactorMain {
	public static Logger LOGGER = Logger.getLogger(GitfactorMain.class);
	
	public static void main(String[] args) {
		try {
			String tmpFolder = args[0];
			Boolean analyse = Boolean.valueOf(args[1]);
			// initialize spring
			ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			// shutdownhook to stop context gracefully
			classPathXmlApplicationContext.registerShutdownHook();
			classPathXmlApplicationContext.start();
			// read from command line
			System.out.println("Type the url to search git repositories in GitHub:");
			// read line with url
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String url = bufferRead.readLine();
			// obtem a query dependendo do ambiente
			GitConfig gitConfig = obterGitConfig();
			if ( gitConfig == null ) {
				LOGGER.error("Error getting query string. Program will terminate.");
				classPathXmlApplicationContext.stop();
			} else {
				LOGGER.info(String.format("Query String: %1$s", gitConfig.name()));
				// get service
				GitHubAnalyser gitHubAnalyser = (GitHubAnalyser) br.com.guilhermebarbosa.git.ApplicationContext.getInstance().getBean("gitHubAnalyser"); 
				gitHubAnalyser.analyseGitHubByQueryUrl(gitConfig, url, tmpFolder, analyse);
			}
			// close resources
			classPathXmlApplicationContext.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static GitConfig obterGitConfig() throws IOException {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("env.properties");
		Properties properties = new Properties();
		properties.load(inputStream);
		String env = (String) properties.get("env");
		return GitConfig.fromString(env);
	}
}
