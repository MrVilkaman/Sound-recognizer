package ru.fixapp.fooproject.domainlayer.fft;


public class Complex {

	private double real;
	private double imaginary;

	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public double getMagnitude() {
		return Math.sqrt(real * real + imaginary * imaginary);
	}

	public double getPhase() {
		return Math.atan2(imaginary, real);
	}
}
