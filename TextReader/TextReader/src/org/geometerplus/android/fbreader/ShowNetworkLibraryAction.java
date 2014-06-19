package org.geometerplus.android.fbreader;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

import com.svo.laohan.HomeTabActivity;

class ShowNetworkLibraryAction extends RunActivityAction {
	ShowNetworkLibraryAction(FBReader baseActivity, FBReaderApp fbreader) {
		super(baseActivity, fbreader, HomeTabActivity.class);
	}
}