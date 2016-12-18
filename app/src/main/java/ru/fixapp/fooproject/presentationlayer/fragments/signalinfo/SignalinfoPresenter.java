package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import javax.inject.Inject;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractor;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
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

		Observable<FFTModel> graphObs = signalProcessorInteractor.getGraphFFTInfo(cache.getPath());
		subscribeUI(graphObs, new ViewSubscriber<SignalinfoView, FFTModel>(view()){
			@Override
			public void onNext(FFTModel model) {
				view().drawSpectr(model);
			}
		});
	}
}
