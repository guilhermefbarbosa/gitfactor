package br.com.guilhermebarbosa.git;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.guilhermebarbosa.gitfactor.Constants;
import br.com.guilhermebarbosa.gitfactor.GitHubAnalyser;

public class GitfactorMain {
	public static void main(String[] args) {
		try {
			String tmpFolder = args[0];
			Boolean analyse = Boolean.valueOf(args[1]);
			// initialize spring
			ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			classPathXmlApplicationContext.start();
			// get service
			GitHubAnalyser gitHubAnalyser = (GitHubAnalyser) br.com.guilhermebarbosa.git.ApplicationContext.getInstance().getBean("gitHubAnalyser"); 
			gitHubAnalyser.analyseGitHubByQueryUrl(Constants.GIT_HUB_QUERY_REPOS, tmpFolder, analyse);
			classPathXmlApplicationContext.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
