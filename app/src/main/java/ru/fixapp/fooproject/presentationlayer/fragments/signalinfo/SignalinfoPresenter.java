package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.domainlayer.fft.SignalFeature;
import ru.fixapp.fooproject.domainlayer.interactors.SignalProcessorInteractor;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.fragments.recording.LoadTrackGraphSubscriber;
import rx.Observable;

public class SignalinfoPresenter extends BasePresenter<SignalinfoView> {

	private final SignalProcessorInteractor signProI;
	private final SignalinfoPresenterCache cache;


	@Inject
	public SignalinfoPresenter(SignalProcessorInteractor signalProcessorInteractor,
							   SignalinfoPresenterCache cache) {
		this.signProI = signalProcessorInteractor;
		this.cache = cache;
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();

//		List<double[]> mel =
//				Arrays.asList(
//						new double[]{2, 0},
//						new double[]{1, 4},
//						new double[]{2, 4},
//						new double[]{10, 10});
//		cache.setMel(mel);
//		calcCos();
		Observable<List<Entry>> graphObs2 = signProI.getGraphInfo(cache.getPath());
		subscribeUI(graphObs2, new LoadTrackGraphSubscriber(view()));

		Observable<FFTModel> graphObs = signProI.getGraphFFTInfo(cache.getPath());
		subscribeUI(graphObs, new ViewSubscriber<SignalinfoView, FFTModel>(view()) {
			@Override
			public void onNext(FFTModel model) {
				view().drawSpectr(model);

				List<SignalFeature> list = model.getList();
				int size = list.size();
				List<double[]> mels = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					mels.add(list.get(i)
							.getMelCeps());
				}
				cache.setMel(mels);
				calcCos();
			}
		});
	}

	private void calcCos() {
		subscribeUI(signProI.calcCos(cache.getMel()), new ViewSubscriber<>(view()));
	}
}
