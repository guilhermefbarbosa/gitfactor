package br.com.guilhermebarbosa.gitfactor;

import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;

public class GitSrcFolderComparissonRef {
	private UMLModel model1;
	private UMLModel model2;
	private UMLModelDiff diff;

	public GitSrcFolderComparissonRef(UMLModel model1, UMLModel model2, UMLModelDiff diff) {
		this.model1 = model1;
		this.model2 = model2;
		this.diff = diff;
	}

	public UMLModel getModel1() {
		return model1;
	}

	public void setModel1(UMLModel model1) {
		this.model1 = model1;
	}

	public UMLModel getModel2() {
		return model2;
	}

	public void setModel2(UMLModel model2) {
		this.model2 = model2;
	}

	public UMLModelDiff getDiff() {
		return diff;
	}

	public void setDiff(UMLModelDiff diff) {
		this.diff = diff;
	}
}