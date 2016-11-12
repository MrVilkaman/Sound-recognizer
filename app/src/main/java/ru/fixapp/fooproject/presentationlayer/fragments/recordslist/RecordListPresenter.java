package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;


import javax.inject.Inject;

import ru.fixapp.fooproject.presentationlayer.fragments.core.BasePresenter;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public class RecordListPresenter extends BasePresenter<RecordListView> {

//	private GetAudioListUseCase listUseCase;
//	private DeleteFileUseCase deleteFileUseCase;

	@Inject
	public RecordListPresenter(
//			GetAudioListUseCase listUseCase,
//							   DeleteFileUseCase deleteFileUseCase
	) {
//		this.listUseCase = listUseCase;
//		this.deleteFileUseCase = deleteFileUseCase;

//		addUseCases(deleteFileUseCase, listUseCase);
	}

	@Override
	public void onViewAttached() {
		super.onViewAttached();
//		listUseCase.execute(new ViewSubscriber<RecordListView, List<AudioModel>>(view()) {
//			@Override
//			public void onNext(List<AudioModel> items) {
//				view().bind(items);
//			}
//		});
	}

	public void openRecordDetail(String absolutePath) {
		view().openRecordDetail(absolutePath);
	}

	public void deleteFile(AudioModel category) {
//		deleteFileUseCase.setModel(category)
//				.execute(new ViewSubscriber<RecordListView, AudioModel>(view()) {
//					@Override
//					public void onNext(AudioModel audioModel) {
//						view().delete(audioModel);
//					}
//				});
	}
}
