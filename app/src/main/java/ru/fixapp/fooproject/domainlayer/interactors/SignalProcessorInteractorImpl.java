package ru.fixapp.fooproject.domainlayer.interactors;

import java.util.Arrays;

import ru.fixapp.fooproject.domainlayer.fft.Complex;
import ru.fixapp.fooproject.domainlayer.fft.Window;

public class SignalProcessorInteractorImpl implements SignalProcessorInteractor {

	public static final double DoublePi = 2 * Math.PI;
	private final int frameSize;

	public SignalProcessorInteractorImpl() {
		frameSize = 512;
	}

	@Override
	public short[] getFrame(short[] shortBuff) {
		short[] shorts = new short[shortBuff.length];

		int currentPos = 0;
		boolean work = true;
		while (work) {
			int newPos = currentPos + frameSize;
			work = newPos < shortBuff.length;
			short[] range = Arrays.copyOfRange(shortBuff, currentPos, currentPos + frameSize);
			short[] afterWindow = new short[range.length];
			for (int i = 0; i < frameSize; i++) {
				double gausse = getWindow(range[i]);
				afterWindow[i] = (short) (gausse * range[i]);

//				if(Math.signum(range[i]) == -1){
//					int curre= currentPos;
//					curre++;
//				}
			}

//			Complex[] complices = decimationInTime(afterWindow);
//			for (int i = 0; i < frameSize; i++) {
//				afterWindow[i] = (short) complices[i].getPhase();
//			}

				if (work) {
				System.arraycopy(afterWindow, 0, shorts, currentPos, afterWindow.length);
			} else {
				int length = shortBuff.length - currentPos;
				System.arraycopy(afterWindow, 0, shorts, currentPos, length);
			}
			currentPos = newPos;
		}
//		Arrays.fill(shorts,frameSize,shortBuff.length, (short) 0);

		return shorts;
	}

	@Override
	public Complex[] getFrame(double[] shortBuff) {
		Complex[] shorts = new Complex[shortBuff.length/2];

		int currentPos = 0;
		boolean work = true;
		while (work) {
			int newPos = currentPos + frameSize;
			work = newPos < shortBuff.length;
			double[] range = Arrays.copyOfRange(shortBuff, currentPos, currentPos + frameSize);
			double[] afterWindow = new double[range.length];
			for (int i = 0; i < frameSize; i++) {
				double gausse = getWindow(range[i]);
				afterWindow[i] = (gausse * range[i]);

//				if(Math.signum(range[i]) == -1){
//					int curre= currentPos;
//					curre++;
//				}
			}

			Complex[] complices = decimationInTime(afterWindow);
//			for (int i = 0; i < frameSize; i++) {
//				afterWindow[i] = (short) complices[i].getPhase();
//			}

			if (work) {
				System.arraycopy(complices, 0, shorts, currentPos/2, afterWindow.length/2);
			} else {
				int length = shortBuff.length - currentPos;
				System.arraycopy(complices, 0, shorts, currentPos/2, length/2);
			}
			currentPos = newPos;
		}
//		Arrays.fill(shorts,frameSize,shortBuff.length, (short) 0);

		return shorts;
	}



	private double getWindow(double n) {return Window.rectangle(n, frameSize);}


	public Complex[] decimationInTime(double[] frame) {
		Complex[] complices = new Complex[frame.length];
		for (int i = 0; i < frame.length; i++) {
			complices[i] = new Complex(frame[i],0);
		}
		Complex[] complices1 = decimationInTime(complices, true);
//
//		Complex right = new Complex(frameSize, 0);
//		for (int i = 0; i < frameSize; i++){
//			complices1[i] = complices1[i].div(right);
//		}

		return complices1;
	}

	public Complex[] decimationInTime(short[] frame) {
		Complex[] complices = new Complex[frame.length];
		for (int i = 0; i < frame.length; i++) {
			complices[i] = new Complex(frame[i],0);
		}
		Complex[] complices1 = decimationInTime(complices, true);

			Complex right = new Complex(frameSize, 0);
		for (int i = 0; i < frameSize; i++){
			complices1[i] = complices1[i].div(right);
		}

		return complices1;
	}

		public Complex[] decimationInTime(Complex[] frame, boolean direct) {
		if (frame.length == 1) return frame;
		int frameHalfSize = frame.length >> 1; // frame.length/2
		int frameFullSize = frame.length;

		Complex[] frameOdd = new Complex[frameHalfSize];
		Complex[] frameEven = new Complex[frameHalfSize];
		for (int i = 0; i < frameHalfSize; i++) {
			int j = i << 1; // i = 2*j;
			frameOdd[i] = frame[j + 1];
			frameEven[i] = frame[j];
		}

		Complex[] spectrumOdd = decimationInTime(frameOdd, direct);
		Complex[] spectrumEven = decimationInTime(frameEven, direct);

		double arg = direct ? -DoublePi / frameFullSize : DoublePi / frameFullSize;
		Complex omegaPowBase = new Complex(Math.cos(arg), Math.sin(arg));
		Complex omega = Complex.One;
		Complex[] spectrum = new Complex[frameFullSize];

		for (int j = 0; j < frameHalfSize; j++) {
			spectrum[j] = spectrumEven[j].plus(omega.mult(spectrumOdd[j]));
			spectrum[j + frameHalfSize] = spectrumEven[j].minus(omega.mult(spectrumOdd[j]));
			omega = omega.mult(omegaPowBase);
		}

		return spectrum;
	}

}
