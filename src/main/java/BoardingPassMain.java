import com.ddairways.model.Airport;
import com.ddairways.model.Channel;
import com.ddairways.model.Flight;
import com.ddairways.model.Passenger;
import com.ddairways.model.TravelTicket;
import com.ddairways.viewables.BoardingPass;

public class BoardingPassMain {
	public static void main(String[] args) throws Exception {
		final Airport mumbai = new Airport("BOM", "Mumbai");
		final Airport chennai = new Airport("MAA", "Chennai");
		final org.joda.time.DateTime departure = org.joda.time.DateTime.now();
		final Flight flight = new Flight("9W", "465", mumbai, chennai, departure.toDate(), 2);
		final Passenger passenger = new Passenger("First", "Last", "first.last@company.com", "Economy");
		final String pnr = "A1B2C3";
		final String seat = "10D";
		final String seqNo = "0018";
		
		TravelTicket ticketInfo = TravelTicket.builder().flight(flight).passenger(passenger).pnr(pnr).seat(seat)
				.seqNo(seqNo).build();
		BoardingPass boardingPass = BoardingPass.checkin(ticketInfo);
		boardingPass.generateBoardingPass(Channel.DESKTOP);
		boardingPass.generateBoardingPass(Channel.MOBILE);

		// Kiosk and Counter boarding passes have gate information
		final String gate = "45C";
		boardingPass = BoardingPass.checkin(new TravelTicket(flight, passenger, pnr, seat, seqNo, gate));
		boardingPass.generateBoardingPass(Channel.AIRPORT_COUNTER);
		boardingPass.generateBoardingPass(Channel.KIOSK);
	}

}
