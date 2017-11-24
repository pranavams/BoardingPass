package com.ddairways.writers;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ddairways.model.TravelTicket;
import com.ddairways.viewables.CalendarWithBoardingPass;
import com.ddairways.viewables.Electronic;

public class DesktopWriter implements Writer {
	private TravelTicket travelInfo;

	public DesktopWriter(TravelTicket travelInfo) {
		super();
		this.travelInfo = travelInfo;
	}

	public void write() throws Exception {
		String fileName = "desktop-boarding-passes-" + this.travelInfo.getPnr() + "-"
				+ this.travelInfo.getPassenger().getLastName() + ".zip";
		try (FileOutputStream fos = new FileOutputStream(fileName)) {

			ZipOutputStream zip = new ZipOutputStream(fos);
			final java.util.List<byte[]> bytes = Arrays.asList(new Electronic(this.travelInfo).create(),
					new CalendarWithBoardingPass(this.travelInfo).create());
			final java.util.List<String> fileNames = Arrays.asList(
					"electronic-boarding-pass-" + this.travelInfo.getPnr() + "-"
							+ this.travelInfo.getPassenger().getLastName() + ".pdf",
					"calendar-boarding-event-" + this.travelInfo.getPnr() + "-"
							+ this.travelInfo.getPassenger().getLastName() + ".ics");
			for (int i = 0; i < fileNames.size(); i++) {
				final byte[] nextBytes = bytes.get(i);
				ZipEntry zipEntry = new ZipEntry(fileNames.get(i));
				zip.putNextEntry(zipEntry);
				zip.write(nextBytes);
				zip.closeEntry();
			}
			zip.finish();
			zip.close();
			fos.flush();
		}
	}
}
