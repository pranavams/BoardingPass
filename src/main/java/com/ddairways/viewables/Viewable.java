package com.ddairways.viewables;

public interface Viewable {

	Viewable None = new Viewable() {
		@Override
		public byte[] create() throws Exception {
			return new byte[] {};
		}
	};

	byte[] create() throws Exception;
}