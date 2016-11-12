package ru.fixapp.fooproject.presentationlayer.resolution;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PicassoImageLoader implements ImageLoader {
	private final Picasso picasso;

	public PicassoImageLoader(Picasso picasso) {
		this.picasso = picasso;
	}

	@Override
	public void showFromFile(@NonNull String path, @NonNull ImageView target) {
		picasso.load(new File(path)).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
				.into(target);
	}
}
