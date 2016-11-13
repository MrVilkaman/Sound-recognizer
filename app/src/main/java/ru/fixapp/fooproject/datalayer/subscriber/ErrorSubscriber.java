package ru.fixapp.fooproject.datalayer.subscriber;


import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;

public class ErrorSubscriber<Type> extends ViewSubscriber<BaseView,Type> {
	public ErrorSubscriber(BaseView view) {
		super(view);
	}

	@Override
	public void onNext(Type o) {

	}

	@Override
	public void onError(Throwable e) {
		view().handleError(e);
	}
}
