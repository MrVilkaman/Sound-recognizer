package ru.fixapp.fooproject.domainlayer.fft;

import java.util.List;

public class FFTModel {

	private final double min;
	private final double max;
	private final List<double[]> list;

	public FFTModel(List<double[]> list, double min, double max) {
		this.list = list;
		this.min = min;
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public List<double[]> getList() {
		return list;
	}
}
