package ru.fixapp.fooproject.domainlayer.interactors;


import com.github.mikephil.charting.data.Entry;

import java.util.List;

import rx.Observable;

public interface SignalProcessorInteractor {

	Observable<List<Entry>> getGraphInfo(String path);

	Observable<List<Entry>> getGraphFFTInfo(String path);
}
