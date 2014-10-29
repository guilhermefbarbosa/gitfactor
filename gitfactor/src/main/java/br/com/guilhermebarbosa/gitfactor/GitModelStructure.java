package br.com.guilhermebarbosa.gitfactor;

import gr.uom.java.xmi.UMLModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitModelStructure {
	private Map<String, UMLModel> mapFatherModel;
	private Map<String, UMLModel> mapChildrenModel;
	private List<GitSrcFolderComparissonRef> listUmlDiff;

	public GitModelStructure() {
		this.mapFatherModel = new HashMap<String, UMLModel>();
		this.mapChildrenModel = new HashMap<String, UMLModel>();
		this.listUmlDiff = new ArrayList<GitSrcFolderComparissonRef>();
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

	public List<GitSrcFolderComparissonRef> getListUmlDiff() {
		return listUmlDiff;
	}

	public void setListUmlDiff(List<GitSrcFolderComparissonRef> listUmlDiff) {
		this.listUmlDiff = listUmlDiff;
	}
}