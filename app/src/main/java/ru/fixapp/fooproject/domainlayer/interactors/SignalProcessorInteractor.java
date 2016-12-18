package ru.fixapp.fooproject.domainlayer.interactors;


import com.github.mikephil.charting.data.Entry;

import java.util.List;

import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import rx.Observable;

public interface SignalProcessorInteractor {

	Observable<List<Entry>> getGraphInfo(String path);

	Observable<FFTModel> getGraphFFTInfo(String path);
}
