package com.net.annotation;

public enum TimerUnit {
	MILLISECOND("ms"), SECOND("s"), MINUTE("m"), HOUR("h");

	private String value;

	private TimerUnit(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
