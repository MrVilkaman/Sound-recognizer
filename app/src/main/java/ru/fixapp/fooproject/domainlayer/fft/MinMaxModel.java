package ru.fixapp.fooproject.domainlayer.fft;

public class MinMaxModel {

	private final double min;
	private final double max;

	public MinMaxModel(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}
}
