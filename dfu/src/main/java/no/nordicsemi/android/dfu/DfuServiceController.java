/*************************************************************************************************************************************************
 * Copyright (c) 2016, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************************************************************************************/

package no.nordicsemi.android.dfu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * A controller class allows you to pause, resume or abort the DFU operation in a easy way.
 * <p>Keep in mind that there may be only one DFU operation at a time, and other instances of a DfuServiceController (for example obtained with a previous DFU)
 * will work for all DFU processes, but the {@link #isPaused()} and {@link #isAborted()} methods may report incorrect values.</p>
 * <p>Added in DFU Library version 1.0.2.</p>
 */
public class DfuServiceController implements DfuController, ServiceConnection {
	private boolean mPaused;
	private boolean mAborted;

	private DfuBaseService dfuBaseService;


	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		DfuBaseService.LocalBinder binder = (DfuBaseService.LocalBinder) service;
		dfuBaseService = binder.getService();
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		dfuBaseService = null;
	}


	/* package */ DfuServiceController(final Context context, Intent serviceIntent) {

		context.bindService(serviceIntent, this, 0);
	}

	@Override
	public void pause() {
		if (!mAborted && !mPaused) {
			mPaused = true;
			dfuBaseService.pause();
		}
	}

	@Override
	public void resume() {
		if (!mAborted && mPaused) {
			mPaused = false;
			dfuBaseService.resume();
		}
	}

	@Override
	public void abort() {
		if (!mAborted) {
			mAborted = true;
			mPaused = false;
			dfuBaseService.abort();
		}
	}

	/**
	 * Returns true if the DFU operation was paused.
	 * It can be now resumed using {@link #resume()}.
	 */
	public boolean isPaused() {
		return mPaused;
	}

	/**
	 * Returns true if DFU was aborted.
	 */
	public boolean isAborted() {
		return mAborted;
	}
}
