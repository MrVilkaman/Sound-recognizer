package ru.fixapp.fooproject.domainlayer.interactors;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.fft.Complex;
import ru.fixapp.fooproject.domainlayer.fft.MFCC;
import ru.fixapp.fooproject.domainlayer.fft.Window;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import rx.Observable;

public class SignalProcessorInteractorImpl implements SignalProcessorInteractor {

	public static final double DoublePi = 2 * Math.PI;
	private final AudioRepo recordDP;
	private final AudioSettings audioSettings;
	private final int frameSize;

	public SignalProcessorInteractorImpl(AudioRepo recordDP, AudioSettings audioSettings) {
		this.recordDP = recordDP;
		this.audioSettings = audioSettings;
		frameSize = 512;
	}

	private short[] getFrame(short[] shortBuff) {
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

	private Complex[] getFrame(double[] shortBuff) {
		Complex[] shorts = new Complex[shortBuff.length / 2];

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
				System.arraycopy(complices, 0, shorts, currentPos / 2, afterWindow.length / 2);
			} else {
				int length = shortBuff.length - currentPos;
				System.arraycopy(complices, 0, shorts, currentPos / 2, length / 2);
			}
			currentPos = newPos;
		}
		//		Arrays.fill(shorts,frameSize,shortBuff.length, (short) 0);

		return shorts;
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
	public Observable<List<Entry>> getGraphFFTInfo(String path) {
		return recordDP.getFileStreamObservable(path)
				.map(shortBuffer -> {
					shortBuffer.rewind();
					short[] shortBuff = new short[shortBuffer.limit()];
					shortBuffer.get(shortBuff);
					int length = Math.min(shortBuffer.limit(), 512);
					double[] doubleBuff = new double[length];
					for (int i = 0; i < length; i++) {
						doubleBuff[i] = shortBuff[i];
					}

					Complex[] frame = getFrame(doubleBuff);
					MFCC mfcc = new MFCC(512, 13, 13, audioSettings.getSampleRate());

					double[] doubleBuff2 = new double[frame.length];
					double[] doubleBuff2Im = new double[frame.length];
					for (int i = 0; i < frame.length; i++) {
						doubleBuff2[i] = frame[i].getReal();
						doubleBuff2Im[i] = frame[i].getImaginary();
					}

					double[] doubleBuff2Res = mfcc.cepstrum(doubleBuff2, doubleBuff2Im);

					List<Entry> entries = new ArrayList<>();
					if (audioSettings.isPCM16BIT()) {
						for (int i = 0; i < doubleBuff2Res.length; i++) {
							entries.add(new Entry(i, (float) doubleBuff2Res[i]));
						}
					}
					return entries;
				});
	}

}
