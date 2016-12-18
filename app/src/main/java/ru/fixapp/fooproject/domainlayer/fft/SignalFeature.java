package ru.fixapp.fooproject.domainlayer.fft;

public class SignalFeature {
	private final double[] fftCeps;
	private final double[] melCeps;

	public SignalFeature(double[] fftCeps, double[] melCeps) {

		this.fftCeps = fftCeps;
		this.melCeps = melCeps;
	}

	public double[] getFftCeps() {
		return fftCeps;
	}

	public double[] getMelCeps() {
		return melCeps;
	}
}
