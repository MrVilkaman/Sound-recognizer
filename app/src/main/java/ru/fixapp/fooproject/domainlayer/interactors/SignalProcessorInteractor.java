package ru.fixapp.fooproject.domainlayer.interactors;


import ru.fixapp.fooproject.domainlayer.fft.Complex;

public interface SignalProcessorInteractor {

	short[] getFrame(short[] shortBuff);

	Complex[] getFrame(double[] shortBuff);
}
