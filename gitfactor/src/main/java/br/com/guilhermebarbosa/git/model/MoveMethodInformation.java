package br.com.guilhermebarbosa.git.model;

public class MoveMethodInformation {
	private String existingMethod;
	private String nonExistingMethod;
	private String className;
	private String methodName;

	public MoveMethodInformation() {
	}
	
	public MoveMethodInformation(String existingMethod, String nonExistingMethod) {
		super();
		this.existingMethod = existingMethod;
		this.nonExistingMethod = nonExistingMethod;
	}

	public String getExistingMethod() {
		return existingMethod;
	}

	public void setExistingMethod(String existingMethod) {
		this.existingMethod = existingMethod;
	}

	public String getNonExistingMethod() {
		return nonExistingMethod;
	}

	public void setNonExistingMethod(String nonExistingMethod) {
		this.nonExistingMethod = nonExistingMethod;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}