package ru.fixapp.fooproject.presentationlayer.fragments.recordslist;


import java.util.List;

import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BindType;
import ru.fixapp.fooproject.presentationlayer.models.AudioModel;

public interface RecordListView extends BaseView,BindType<List<AudioModel>> {
	void openRecordDetail(String absolutePath);

	void delete(AudioModel audioModel);
}