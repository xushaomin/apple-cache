package com.appleframework.cache.core.utils;

import com.appleframework.cache.core.sequence.LinuxSequenceService;
import com.appleframework.cache.core.sequence.MacSequenceService;
import com.appleframework.cache.core.sequence.SequenceService;
import com.appleframework.cache.core.sequence.SolarisSequenceService;
import com.appleframework.cache.core.sequence.WindowsSequenceService;

public class SequenceUtility {

	private static SequenceService service;

	static {
		if (OSUtility.isWindows()) {
			service = new WindowsSequenceService();
		} else if (OSUtility.isLinux()) {
			service = new LinuxSequenceService();
		} else if (OSUtility.isMacOSX()) {
			service = new MacSequenceService();
		} else {
			service = new SolarisSequenceService();
		}
	}

	public static String getSequence() {
		return service.getSequence();
	}
}