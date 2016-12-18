package ru.fixapp.fooproject.domainlayer.interactors;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.fft.Complex;
import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.domainlayer.fft.MFCC;
import ru.fixapp.fooproject.domainlayer.fft.SignalFeature;
import ru.fixapp.fooproject.domainlayer.fft.Window;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import rx.Observable;

public class SignalProcessorInteractorImpl implements SignalProcessorInteractor {

	public static final double DoublePi = 2 * Math.PI;
	private final AudioRepo recordDP;
	private final AudioSettings audioSettings;
	private final int frameSize;
	private final float overlapPresent;
	private final MFCC mfcc;
	private final int melBands;

	public SignalProcessorInteractorImpl(AudioRepo recordDP, AudioSettings audioSettings) {
		this.recordDP = recordDP;
		this.audioSettings = audioSettings;
		frameSize = 512;
		overlapPresent = 0.5f;
		int numCoeffs = 13;
		melBands = 13;
		mfcc = new MFCC(frameSize, numCoeffs, melBands, audioSettings.getSampleRate());
	}

	private FFTModel getFrame(double[] shortBuff) {

		List<SignalFeature> spectr = new ArrayList<>();

		int currentPos = 0;
		boolean work = true;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

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
			int len = complices.length / 2 + 1;
			double[] fr = new double[len];
			double[] doubleBuff2 = new double[complices.length];
			double[] doubleBuff2Im = new double[complices.length];

			for (int i = 0; i < complices.length; i++) {
				Complex complice = complices[i];
				doubleBuff2[i] = complice.getReal();
				doubleBuff2Im[i] = complice.getImaginary();
				if (i < len) {
					double magnitude = complice.getMagnitude();
					fr[i] = magnitude;
					min = Math.min(magnitude, min);
					max = Math.max(magnitude, max);
				}
			}
			currentPos = currentPos + (int) (frameSize * (1 - overlapPresent));
			double[] cepstrum = mfcc.cepstrum(doubleBuff2, doubleBuff2Im);
			spectr.add(new SignalFeature(fr, cepstrum));
		}


		return new FFTModel(spectr, min, max);
	}

	private double getWindow(double n) {
		return Window.hamming(n, frameSize);
	}

	private Complex[] decimationInTime(double[] frame) {
		Complex[] complices = new Complex[frame.length];
		for (int i = 0; i < frame.length; i++) {
			complices[i] = new Complex(frame[i], 0);
		}
		Complex[] complices1 = decimationInTime(complices, true);
		//
		//		Complex right = new Complex(frameSize, 0);
		//		for (int i = 0; i < frameSize; i++){
		//			complices1[i] = complices1[i].div(right);
		//		}

		return complices1;
	}

	private Complex[] decimationInTime(Complex[] frame, boolean direct) {
		if (frame.length == 1)
			return frame;
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

	@Override
	public Observable<List<Entry>> getGraphInfo(String path) {
		return recordDP.getFileStreamObservable(path)
				.map(shortBuffer -> {
					shortBuffer.rewind();
					short[] shortBuff = new short[shortBuffer.limit()];
					shortBuffer.get(shortBuff);
					List<Entry> entries = new ArrayList<>();
					if (audioSettings.isPCM16BIT()) {
						for (int i = 0; i < shortBuff.length; i++) {
							//							float phase = (float) frame[i]
							// .getMagnitude();
							entries.add(new Entry(i, shortBuff[i]));
						}
					}
					return entries;
				});
	}

	@Override
	public Observable<FFTModel> getGraphFFTInfo(String path) {
		return recordDP.getFileStreamObservable(path)
				.map(shortBuffer -> {
					shortBuffer.rewind();
					short[] shortBuff = new short[shortBuffer.limit()];
					shortBuffer.get(shortBuff);
					int length = shortBuffer.limit();
					double[] doubleBuff = new double[length];
					//					double min = Double.MAX_VALUE;
					//					double max = Double.MIN_VALUE;
					for (int i = 0; i < length; i++) {
						double magnitude = shortBuff[i];
						magnitude /= Short.MAX_VALUE;
						doubleBuff[i] = magnitude;
						//						min = Math.min(magnitude, min);
						//						max = Math.max(magnitude, max);
					}

					return getFrame(doubleBuff);
				});
	}

}
