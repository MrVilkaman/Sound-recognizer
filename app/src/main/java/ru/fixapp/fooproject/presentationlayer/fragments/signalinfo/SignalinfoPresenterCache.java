package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;

import java.util.List;

public class SignalinfoPresenterCache {
	private String path;
	private List<double[]> mel;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setMel(List<double[]> mel) {
		this.mel = mel;
	}

	public List<double[]> getMel() {
		return mel;
	}
}
