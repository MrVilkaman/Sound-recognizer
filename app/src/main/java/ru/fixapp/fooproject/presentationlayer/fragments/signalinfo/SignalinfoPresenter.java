package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import com.github.mikephil.charting.data.Entry;

import java.util.List;

import javax.inject.Inject;

import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractor;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.fragments.recording.LoadTrackGraphSubscriber;
import rx.Observable;

public class SignalinfoPresenter extends BasePresenter<SignalinfoView> {

	private final SignalProcessorInteractor signalProcessorInteractor;
	private final SignalinfoPresenterCache cache;


	@Inject
	public SignalinfoPresenter(SignalProcessorInteractor signalProcessorInteractor,
							   SignalinfoPresenterCache cache) {
		this.signalProcessorInteractor = signalProcessorInteractor;
		this.cache = cache;
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();

		Observable<List<Entry>> graphObs = signalProcessorInteractor.getGraphFFTInfo(cache.getPath());
		subscribeUI(graphObs, new LoadTrackGraphSubscriber(view()));
	}
}
