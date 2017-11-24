package com.ddairways.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TravelTicket {
	private Flight flight;
	private Passenger passenger;
	private String pnr;
	private String seat;
	private String seqNo;
	private String gate;
	private TravelTicket(){
		
	}
}