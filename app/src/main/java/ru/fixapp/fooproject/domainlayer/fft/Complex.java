package ru.fixapp.fooproject.domainlayer.fft;


public class Complex {

	public static Complex One = new Complex(1.0, .0);
	private double real;
	private double imaginary;

	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	public double getImaginary() {
		return imaginary;
	}

	public double getReal() {
		return real;
	}

	public double getMagnitude() {
		return Math.sqrt(real * real + imaginary * imaginary);
	}

	public double getPhase() {
		return Math.atan2(imaginary, real);
	}

	public Complex plus(Complex right) {
		return new Complex(real + right.real, imaginary + right.imaginary);
	}

	public Complex mult(Complex right) {
		double real = this.real * right.real - imaginary * right.imaginary;
		double imaginary = this.imaginary * right.real + this.real * right.imaginary;
		return new Complex(real, imaginary);
	}

	public Complex minus(Complex right) {
		return new Complex(real - right.real, imaginary - right.imaginary);
	}


	public Complex div(Complex right) {
		double num1 = real;
		double num2 = imaginary;
		double num3 = right.real;
		double num4 = right.imaginary;
		if (Math.abs(num4) < Math.abs(num3)) {
			double num5 = num4 / num3;
			return new Complex((num1 + num2 * num5) / (num3 + num4 * num5),
					(num2 - num1 * num5) / (num3 + num4 * num5));
		} else {
			double num5 = num3 / num4;
			return new Complex((num2 + num1 * num5) / (num4 + num3 * num5),
					(-num1 + num2 * num5) / (num4 + num3 * num5));
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Complex{");
		sb.append("real=")
				.append(real)
				.append(", imaginary=")
				.append(imaginary)
				.append('}');
		return sb.toString();
	}
}
