package br.com.guilhermebarbosa.git;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContext implements ApplicationContextAware {
	private static org.springframework.context.ApplicationContext INSTANCE; 
	
	public static org.springframework.context.ApplicationContext getInstance() {
		return INSTANCE;
	}

	public void setApplicationContext(org.springframework.context.ApplicationContext arg0) throws BeansException {
		INSTANCE = arg0;
	}
}