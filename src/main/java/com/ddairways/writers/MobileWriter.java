package com.ddairways.writers;

import java.io.FileOutputStream;

import com.ddairways.model.TravelTicket;
import com.ddairways.viewables.CalendarWithBoardingPass;

public class MobileWriter implements Writer {
	private TravelTicket travelInfo;

	public MobileWriter(TravelTicket travelInfo) {
		super();
		this.travelInfo = travelInfo;
	}

	public void write() throws Exception {
		String fileName = "calendar-boarding-event-" + this.travelInfo.getPnr() + "-"
				+ this.travelInfo.getPassenger().getLastName() + ".ics";
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(new CalendarWithBoardingPass(this.travelInfo).create());
			fos.flush();
		}
	}
}

