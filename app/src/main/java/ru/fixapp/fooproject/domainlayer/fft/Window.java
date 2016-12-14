package ru.fixapp.fooproject.domainlayer.fft;


public class Window {
	public static final double PI_2 = 2 * Math.PI;
	private static final double Q = 0.5;

	public static double rectangle(double n, double frameSize) {
		return 1;
	}

	public static double my(double n, double frameSize) {
		return 0.1f;
//		return n % 2 != 0 ? 1 : 0;
	}

	public static double gausse(double n, double frameSize) {
		double a = (frameSize - 1) / 2;
		double t = (n - a) / (Q * a);
		t = t * t;
		return Math.exp(-t / 2);
	}

	public static double hamming(double n, double frameSize) {
		return 0.54 - 0.46 * Math.cos((PI_2 * n) / (frameSize - 1));
	}

	public static double hann(double n, double frameSize) {
		return 0.5 * (1 - Math.cos((PI_2 * n) / (frameSize - 1)));
	}

	public static double blackmannHarris(double n, double frameSize) {
		double v = 2 * PI_2 * n;
		double v1 = frameSize - 1;
		double cos = Math.cos(v / v1);
		return 0.35875 - (0.48829 * Math.cos((PI_2 * n) / v1)) +
				(0.14128 * cos) - (0.01168 * cos);
	}
}
