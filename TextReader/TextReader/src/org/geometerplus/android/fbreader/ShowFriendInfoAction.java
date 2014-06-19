/*
 * Copyright (C) 2007-2012 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

import android.os.Bundle;

import com.svo.platform.share.Share;
import com.svo.platform.utils.Constants;

class ShowFriendInfoAction extends FBAndroidAction {
	ShowFriendInfoAction(FBReader baseActivity, FBReaderApp fbreader) {
		super(baseActivity, fbreader);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	protected void run(Object ... params) {
		share();
	}
	public void share() {
		Bundle bundle = new Bundle();
		bundle.putString("content", Constants.SHARE_CONTENT);
		bundle.putString("title", "老汉阅读器,不错,可以体验一下"); 
		bundle.putString("url", Constants.REN_FROM_LINK); 
		bundle.putString("imageUrl", "http://1.dubinwei.duapp.com/static/share.png");
		new Share(BaseActivity).share(bundle);
	}
}
