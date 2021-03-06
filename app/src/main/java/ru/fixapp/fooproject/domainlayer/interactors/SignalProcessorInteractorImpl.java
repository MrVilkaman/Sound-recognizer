package ru.fixapp.fooproject.domainlayer.interactors;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.fixapp.fooproject.datalayer.repository.AudioRepo;
import ru.fixapp.fooproject.domainlayer.fft.Complex;
import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.domainlayer.fft.MFCC;
import ru.fixapp.fooproject.domainlayer.fft.MinMaxModel;
import ru.fixapp.fooproject.domainlayer.fft.SignalFeature;
import ru.fixapp.fooproject.domainlayer.fft.Window;
import ru.fixapp.fooproject.domainlayer.models.AudioSettings;
import ru.fixapp.fooproject.domainlayer.providers.SchedulersProvider;
import rx.Observable;

public class SignalProcessorInteractorImpl implements SignalProcessorInteractor {

	public static final double DoublePi = 2 * Math.PI;

	private final AudioRepo recordDP;
	private final AudioSettings audioSettings;
	private final SchedulersProvider schedulers;

	private final int frameSize;
	private final float overlapPresent;
	private final MFCC mfcc;
	private final int melBands;

	private boolean removeFirst = true;

	public SignalProcessorInteractorImpl(AudioRepo recordDP, AudioSettings audioSettings,
										 SchedulersProvider schedulers) {
		this.recordDP = recordDP;
		this.audioSettings = audioSettings;
		this.schedulers = schedulers;
		frameSize = 512;
		overlapPresent = 0.5f;
		int numCoeffs = 25;
		melBands = 25;
		mfcc = new MFCC(frameSize, numCoeffs, melBands, audioSettings.getSampleRate());
	}

	private FFTModel getFrame(double[] shortBuff) {

		List<SignalFeature> spectr = new ArrayList<>();

		int currentPos = 0;
		boolean work = true;
		double minFFT = Double.MAX_VALUE;
		double maxFFT = Double.MIN_VALUE;
		double minMFCC = Double.MAX_VALUE;
		double maxMFCC = Double.MIN_VALUE;

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
			int len = complices.length / 2;
			double[] fr = new double[len];
			double[] doubleBuff2 = new double[len];
			double[] doubleBuff2Im = new double[len];

			for (int i = 0; i < complices.length; i++) {
				if (i < len) {
					Complex complice = complices[i];
					doubleBuff2[i] = complice.getReal();
					doubleBuff2Im[i] = complice.getImaginary();
					double magnitude = complice.getMagnitude();
					fr[i] = magnitude;
					minFFT = Math.min(magnitude, minFFT);
					maxFFT = Math.max(magnitude, maxFFT);
				}
			}
			currentPos = currentPos + (int) (frameSize * (1 - overlapPresent));
			double[] cepstrum = mfcc.cepstrum(doubleBuff2, doubleBuff2Im);

			for (int i = 0; i < cepstrum.length; i++) {
				double d1 = cepstrum[i];
				if ((!removeFirst || i != 0) && !Double.isNaN(d1) &&
						d1 != Double.NEGATIVE_INFINITY &&
						d1 != Double.POSITIVE_INFINITY) {
					minMFCC = Math.min(d1, minMFCC);
					maxMFCC = Math.max(d1, maxMFCC);
				}
			}

			spectr.add(new SignalFeature(fr, cepstrum));
		}


		return new FFTModel(spectr, new MinMaxModel(minFFT, maxFFT),
				new MinMaxModel(minMFCC, maxMFCC));
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
					int length = shortBuffer.limit();
					short[] shortBuff = new short[length];
					shortBuffer.get(shortBuff);
					double[] doubleBuff = new double[length];
					for (int i = 0; i < length; i++) {
						double magnitude = shortBuff[i];
						//						magnitude /= Short.MAX_VALUE;
						doubleBuff[i] = magnitude;
					}

					return getFrame(doubleBuff);
				});
	}

	@Override
	public Observable<Void> calcCos(List<double[]> mel) {
		return Observable.fromCallable(() -> mel)
				.subscribeOn(schedulers.computation())
				.map(doubles -> {
					double[][] res = new double[doubles.size()][doubles.size()];
					for (int i = 0; i < doubles.size(); i++) {
						res[i] = new double[doubles.size()];
						double[] mel1 = doubles.get(i);
						for (int j = i+1; j < doubles.size(); j++) {
							double[] mel2 = doubles.get(j);
							res[i][j] = doCalc(mel1, mel2);
						}
					}
					printGrid(res);
					return res;
				})
				.map(doubles -> null);
	}

	public void printGrid(double[][] res) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < res.length; i++) {
			double[] row = res[i];
			for (int j = 0; j < row.length; j++) {
				double block = row[j];
				buf.append(String.format("%2.3f ", block));
				if (j >= row.length - 1) {
					buf.append("\n");
				}
			}
		}
		Log.d("QWER", buf.toString());
	}

	private double doCalc(double[] mel1, double[] mel2) {
		if (mel1.length != mel2.length) {
			throw new ArithmeticException("size in differed");
		}
		double sum = dot(mel1, mel2);

		return sum / Math.sqrt(dot(mel1, mel1)) / Math.sqrt(dot(mel2, mel2));
	}

	private double dot(double[] mel1, double[] mel2) {
		double sum = 0;
		for (int i = 0; i < mel1.length; i++) {
			sum += mel1[i] * mel2[i];
		}
		return sum;
	}
}
