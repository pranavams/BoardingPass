package com.ddairways.writers;

public interface Writer {
	Writer None = new Writer() {
		@Override
		public void write() throws Exception {
		}
	};

	void write() throws Exception;
}