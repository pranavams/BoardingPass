package com.ddairways.writers;

import java.io.FileOutputStream;

import com.ddairways.model.TravelTicket;
import com.ddairways.viewables.Kiosk;

public class AirportWriter implements Writer {
	private TravelTicket travelInfo;

	public AirportWriter(TravelTicket travelInfo) {
		super();
		this.travelInfo = travelInfo;
	}

	public void write() throws Exception {
		String fileName = "kiosk-boarding-pass-" + travelInfo.getPnr() + "-" + travelInfo.getPassenger().getLastName()
				+ ".pdf";
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(new Kiosk(travelInfo).create());
			fos.flush();
		}
	}
}
