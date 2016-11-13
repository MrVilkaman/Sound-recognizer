package ru.fixapp.fooproject.domainlayer.exceptions;

public class MediaPlayerError extends Throwable {
	public MediaPlayerError(int what, int extra) {
		super(String.format("what = %s; extra = %s",what,extra));
	}
}
