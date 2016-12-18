package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import com.github.mikephil.charting.data.Entry;

import java.util.List;

import javax.inject.Inject;

import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractor;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.fragments.recording.LoadTrackGraphSubscriber;
import rx.Observable;

public class SignalinfoPresenter extends BasePresenter<SignalinfoView> {

	private final AudioStorageInteractor storageInteractor;
	private SignalinfoPresenterCache cache;


	@Inject
	public SignalinfoPresenter(AudioStorageInteractor storageInteractor,
							   SignalinfoPresenterCache cache) {
		this.storageInteractor = storageInteractor;
		this.cache = cache;
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();

		Observable<List<Entry>> graphObs = storageInteractor.getGraphInfo(cache.getPath());
		subscribeUI(graphObs, new LoadTrackGraphSubscriber(view()));
	}
}
