package com.sanerzone.common.support.sequence;

import com.sanerzone.common.support.utils.SystemClock;

public enum CachedMillisecondClock {
	INS;

	public long now() {
		return SystemClock.now();
	}

}
