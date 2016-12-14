package ru.fixapp.fooproject.domainlayer.interactors;

import java.util.Arrays;

import ru.fixapp.fooproject.domainlayer.fft.Window;

public class SignalProcessorInteractorImpl implements SignalProcessorInteractor {

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
					afterWindow[i] = (short) (range[i] * gausse);
				}
			if (work) {
				System.arraycopy(range, 0, shorts, currentPos, range.length);
			} else {
				int length = shortBuff.length - currentPos;
				System.arraycopy(range, 0, shorts, currentPos, length);
			}
			currentPos = newPos;
		}
//		Arrays.fill(shorts,frameSize,shortBuff.length, (short) 0);

		return shorts;
	}

	private double getWindow(short n) {return Window.blackmannHarris(n, frameSize);}
}
