package ubergesundheit.owrds.elems;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

public class OwrdApplication extends Application {
	
	private Words words;

	public OwrdApplication(){
		Log.i("application","initialized");
		init();
	}
	
	private void init(){
		new AsyncTask<Void,Void,Words>() {

			@Override
			protected Words doInBackground(Void... params) {
				Long start = System.nanoTime();
				words = new Words(getBaseContext());
				Log.i("loaded after",System.nanoTime()-start+" ns");
				return null;
			}
			
		}.execute((Void)null);
		
	}
	
	public Words getWords(){
		return words;
	}

}
