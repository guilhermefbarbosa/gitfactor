package br.com.guilhermebarbosa.gitfactor;

import gr.uom.java.xmi.UMLModel;

import java.util.HashMap;
import java.util.Map;

public class GitModelStructure {
	private Map<String, UMLModel> mapFatherModel;
	private Map<String, UMLModel> mapChildrenModel;

	public GitModelStructure() {
		this.mapFatherModel = new HashMap<String, UMLModel>();
		this.mapChildrenModel = new HashMap<String, UMLModel>();
	}

	public Map<String, UMLModel> getMapFatherModel() {
		return mapFatherModel;
	}

	public void setMapFatherModel(Map<String, UMLModel> mapFatherModel) {
		this.mapFatherModel = mapFatherModel;
	}

	public Map<String, UMLModel> getMapChildrenModel() {
		return mapChildrenModel;
	}

	public void setMapChildrenModel(Map<String, UMLModel> mapChildrenModel) {
		this.mapChildrenModel = mapChildrenModel;
	}
}