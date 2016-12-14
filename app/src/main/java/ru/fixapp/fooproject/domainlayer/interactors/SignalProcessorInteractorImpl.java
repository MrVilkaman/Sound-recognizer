package ru.fixapp.fooproject.domainlayer.interactors;

import java.util.Arrays;

public class SignalProcessorInteractorImpl implements SignalProcessorInteractor {
	@Override
	public short[] getFrame(short[] shortBuff) {
		short[] shorts = Arrays.copyOf(shortBuff,shortBuff.length);
		Arrays.fill(shorts,shortBuff.length/2,shortBuff.length, (short) 0);

		return shorts;
	}
}
