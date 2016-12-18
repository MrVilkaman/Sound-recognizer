package ru.fixapp.fooproject.domainlayer.fft;

import java.util.List;

public class FFTModel {


	private final List<SignalFeature> list;
	private final MinMaxModel fft;
	private final MinMaxModel mfcc;

	public FFTModel(List<SignalFeature> list, MinMaxModel fft, MinMaxModel mfcc) {
		this.list = list;
		this.fft = fft;
		this.mfcc = mfcc;
	}

	public MinMaxModel getFft() {
		return fft;
	}

	public MinMaxModel getMfcc() {
		return mfcc;
	}

	public List<SignalFeature> getList() {
		return list;
	}
}
