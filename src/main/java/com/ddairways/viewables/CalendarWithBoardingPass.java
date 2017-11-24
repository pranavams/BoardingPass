package com.ddairways.viewables;

import java.io.ByteArrayOutputStream;
import java.net.SocketException;
import java.net.URISyntaxException;

import com.ddairways.model.TravelTicket;
import com.ddairways.viewables.Viewable;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Encoding;
import net.fortuna.ical4j.model.parameter.FmtType;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attach;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Repeat;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.UidGenerator;

public class CalendarWithBoardingPass implements Viewable {

	/**
	 * 
	 */
	private final TravelTicket ticketInfo;
	private byte[] attachedBoardingPass;

	public CalendarWithBoardingPass(TravelTicket ticketInfo) throws Exception {
		this.ticketInfo = ticketInfo;
		this.attachedBoardingPass = new Mobile(ticketInfo).create();
	}

	@Override
	public byte[] create() throws Exception {
		return create(attachedBoardingPass);
	}

	byte[] create(byte[] attachBoardingPass) throws Exception {
		String eventName = String.format("Flight to %s (%s)", ticketInfo.getFlight().getDestinationCity(),
				ticketInfo.getFlight().getCompleteNumber());
		VEvent flightTravel = createEvent(eventName);

		// generate unique identifier..
		generateAndAddUniqueID(flightTravel);

		// add attendees for Event
		addAttendee(flightTravel);

		addReminders(eventName, flightTravel);

		attachBoardingPass(attachBoardingPass, flightTravel);

		net.fortuna.ical4j.model.Calendar icsCalendar = createAndAttachCalendar(flightTravel);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.setValidating(false);
		outputter.output(icsCalendar, out);
		return out.toByteArray();
	}

	private net.fortuna.ical4j.model.Calendar createAndAttachCalendar(VEvent flightTravel) {
		// Create a calendar
		net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
		icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
		icsCalendar.getProperties().add(CalScale.GREGORIAN);
		// Add the event and print
		icsCalendar.getComponents().add(flightTravel);
		return icsCalendar;
	}

	private void attachBoardingPass(byte[] attachBoardingPass, VEvent flightTravel) {
		// Attach boarding pass
		if (attachBoardingPass != null) {
			ParameterList params = new ParameterList();
			params.add(Value.BINARY);
			params.add(Encoding.BASE64);
			params.add(new FmtType("Mobile Boarding Pass.pdf"));
			Attach attach = new Attach(params, attachBoardingPass);
			flightTravel.getProperties().add(attach);
		}
	}

	private void addReminders(String eventName, VEvent flightTravel) {
		// Set Alarm for the the above Event
		// Creating an alarm to trigger one (3) hour before the scheduled
		// start
		// of the parent event
		VAlarm reminder = new VAlarm(new Dur(0, -3, 0, 0));
		// repeat reminder four (4) more times every thirty (30) minutes..
		reminder.getProperties().add(new Repeat(4));
		reminder.getProperties().add(new Duration(new Dur(0, 0, 30, 0)));

		// display a message for the Alarm
		reminder.getProperties().add(Action.AUDIO);
		reminder.getProperties().add(new Description(eventName));
		flightTravel.getAlarms().add(reminder);
	}

	private void addAttendee(VEvent flightTravel) throws URISyntaxException {
		Attendee attendee = new Attendee(ticketInfo.getPassenger().getEmailUri());
		attendee.getParameters().add(Role.REQ_PARTICIPANT);
		attendee.getParameters().add(new Cn(ticketInfo.getPassenger().fullName()));
		flightTravel.getProperties().add(attendee);
	}

	private void generateAndAddUniqueID(VEvent flightTravel) throws SocketException {
		UidGenerator ug = new UidGenerator("uidGen");
		Uid uid = ug.generateUid();
		flightTravel.getProperties().add(uid);
	}

	private VEvent createEvent(String eventName) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		net.fortuna.ical4j.model.TimeZone timezone = registry.getTimeZone("Asia/Calcutta");

		// Create the event
		org.joda.time.DateTime departure = new org.joda.time.DateTime(ticketInfo.getFlight().getDeparture());

		DateTime start = new DateTime(departure.toDate());
		DateTime end = new DateTime(departure.plusHours(2).toDate());
		VEvent flightTravel = new VEvent(start, end, eventName);

		// add timezone info..
		VTimeZone tz = timezone.getVTimeZone();
		flightTravel.getProperties().add(tz.getTimeZoneId());
		return flightTravel;
	}

}