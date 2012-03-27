package com.ipaper.myapp;

public class MemoryUsage {
	private static long maxMemoryUsed = 0;

	public static long getMaxMemoryUsed() {
		return maxMemoryUsed;
	}

	public static void setMaxMemoryUsed(long maxMemoryUsed) {
		if(maxMemoryUsed > MemoryUsage.maxMemoryUsed)
			MemoryUsage.maxMemoryUsed = maxMemoryUsed;
	}
	
	
}
