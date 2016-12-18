package ru.fixapp.fooproject.presentationlayer.fragments.signalinfo;


import ru.fixapp.fooproject.domainlayer.fft.FFTModel;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;
import ru.fixapp.fooproject.presentationlayer.fragments.recording.SpectrGraghView;

public interface SignalinfoView extends BaseView,SpectrGraghView {

	void drawSpectr(FFTModel model);
}