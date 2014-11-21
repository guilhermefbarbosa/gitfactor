package br.com.guilhermebarbosa.git;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.guilhermebarbosa.git.dao.GitHubDAO;
import br.com.guilhermebarbosa.git.model.RefactoringByDeveloper;

public class DistributionMapTest {
	public static final Logger LOGGER = Logger.getLogger(DistributionMapTest.class);
	
	public static void main(String[] args) throws FileNotFoundException {
		// initialize spring
		ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		// shutdownhook to stop context gracefully
		classPathXmlApplicationContext.registerShutdownHook();
		classPathXmlApplicationContext.start();
		
		GitHubDAO gitHubDAO = (GitHubDAO) ApplicationContext.getInstance().getBean("gitHubDAO");
		
		generateDistributionMap(gitHubDAO, "storm");
		generateDistributionMap(gitHubDAO, "elasticsearch");
		generateDistributionMap(gitHubDAO, "SlidingMenu");
		
		classPathXmlApplicationContext.close();
	}

	private static void generateDistributionMap(GitHubDAO gitHubDAO, String repository)
			throws FileNotFoundException {
		List<RefactoringByDeveloper> refactorings = gitHubDAO.findClassesByDevelopers(repository);
		// author -> class names
		Map<String, List<String>> mapDeveloperClasses = new HashMap<String, List<String>>();
		for (RefactoringByDeveloper refactoringByDeveloper : refactorings) {
			if ( !mapDeveloperClasses.containsKey(refactoringByDeveloper.getAuthor()) ) {
				mapDeveloperClasses.put(refactoringByDeveloper.getAuthor(), new ArrayList<String>());
			}
			mapDeveloperClasses.get(refactoringByDeveloper.getAuthor()).add(formatClassName(refactoringByDeveloper.getClassName()));
		}
		// for each developer, generate a list separeted by comma for tool
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(repository + ".data"));
		for(String author : mapDeveloperClasses.keySet()) {
			List<String> list = mapDeveloperClasses.get(author);
			String line = StringUtils.join(list, ",");
			printWriter.write("\"" + line + " \\n" + "\"" + "\n");
		}
		printWriter.flush();
		printWriter.close();
	}

	private static String formatClassName(String className) {
		String replaceAll = className.replaceAll("\\.", "/");
		return replaceAll + ".java";
	}
}
