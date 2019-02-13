package com.sanerzone.common.support.utils;

import io.netty.util.concurrent.Future;

public interface ExitUnlimitCirclePolicy {
	boolean notOver(Future future);
}
