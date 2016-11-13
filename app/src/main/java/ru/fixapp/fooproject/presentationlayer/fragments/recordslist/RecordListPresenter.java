package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;


import java.util.List;

import javax.inject.Inject;

import ru.fixapp.fooproject.datalayer.subscriber.ViewSubscriber;
import ru.fixapp.fooproject.domainlayer.interactors.AudioPlayerInteractor;
import ru.fixapp.fooproject.domainlayer.interactors.AudioStorageInteractor;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordListPresenter extends BasePresenter<RecordListView> {

	private AudioStorageInteractor storageInteractor;

	@Inject
	public RecordListPresenter(AudioStorageInteractor storageInteractor) {
		this.storageInteractor = storageInteractor;
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();
		subscribeUI(storageInteractor.getAudio(), new RecordListViewListViewSubscriber(view()));
	}

	public void openRecordDetail(String absolutePath) {
		view().openRecordDetail(absolutePath);
	}

	public void deleteFile(AudioModel audioModel) {
		storageInteractor.deleteFileByPath(audioModel);
		view().delete(audioModel);
	}

	private static class RecordListViewListViewSubscriber
			extends ViewSubscriber<RecordListView, List<AudioModel>> {
		public RecordListViewListViewSubscriber(RecordListView view) {
			super(view);
		}

		@Override
		public void onNext(List<AudioModel> items) {
			view().bind(items);
		}
	}
}
