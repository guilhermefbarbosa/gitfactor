package br.com.guilhermebarbosa.git;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.guilhermebarbosa.gitfactor.Constants;
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
			// obtem a query dependendo do ambiente
			String query = obterQueryString();
			if ( query == null ) {
				LOGGER.error("Error getting query string. Program will terminate.");
				classPathXmlApplicationContext.stop();
			} else {
				LOGGER.info(String.format("Query String: %1$s", query));
				// get service
				GitHubAnalyser gitHubAnalyser = (GitHubAnalyser) br.com.guilhermebarbosa.git.ApplicationContext.getInstance().getBean("gitHubAnalyser"); 
				gitHubAnalyser.analyseGitHubByQueryUrl(query, tmpFolder, analyse);
			}
			// close resources
			classPathXmlApplicationContext.close();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static String obterQueryString() throws IOException {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("env.properties");
		Properties properties = new Properties();
		properties.load(inputStream);
		String env = (String) properties.get("env");
		if ( env.equals("juvenal") ) {
			return Constants.GIT_HUB_QUERY_REPOS_JUVENAL;
		} else if ( env.equals("barbosa") ) {
			return Constants.GIT_HUB_QUERY_REPOS_BARBOSA;
		}
		return null;
	}
}
