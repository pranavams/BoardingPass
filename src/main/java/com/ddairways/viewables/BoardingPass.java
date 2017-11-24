package com.ddairways.viewables;

import com.ddairways.model.Channel;
import com.ddairways.model.TravelTicket;
import com.ddairways.writers.AirportWriter;
import com.ddairways.writers.DesktopWriter;
import com.ddairways.writers.MobileWriter;
import com.ddairways.writers.Writer;

import lombok.Getter;

@Getter
public class BoardingPass {
	private TravelTicket ticketInfo = null;

	public static BoardingPass checkin(TravelTicket ticketInfo) {
		return new BoardingPass(ticketInfo);
	}

	private BoardingPass(TravelTicket ticketInfo) {
		this.ticketInfo =  ticketInfo;
	}

	public void generateBoardingPass(Channel channel) throws Exception {
		Writer writer = Writer.None;
		switch (channel) {
		case AIRPORT_COUNTER:
			writer = new AirportWriter(getTicketInfo());
			break;
		case DESKTOP:
			writer = new DesktopWriter(getTicketInfo());
			break;
		case MOBILE:
			writer = new MobileWriter(getTicketInfo());
			break;
		case KIOSK:
			writer = new AirportWriter(getTicketInfo());
			break;
		}
		writer.write();
	}
}