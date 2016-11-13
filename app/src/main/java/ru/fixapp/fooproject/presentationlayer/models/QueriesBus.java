package ru.fixapp.fooproject.presentationlayer.models;


import net.jokubasdargis.rxbus.Queue;

public class QueriesBus {
	public static final Queue<AudioEvents> AUDIO_EVENTS_QUEUE = Queue.of(AudioEvents.class).build();
}
