package com.ddairways.viewables;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.ddairways.model.TravelTicket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class Kiosk extends PdfBoardingPass {
	
	/**
	 * 
	 */
	private final TravelTicket ticketInfo;

	public Kiosk(TravelTicket ticketInfo) {
		super(new Rectangle(595, 220));
		this.ticketInfo = ticketInfo;
	}

	public void setFontAndSize(PdfContentByte contentByte) throws Exception {
		contentByte.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 24);
	}

	public void writeBody(Document document, PdfContentByte contentByte) throws Exception {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(new float[] { 225, 225, 5, 190 }, document.getPageSize());
		// first 2 cols for passenger copy and last column for airline copy
		final String name = "NAME: " + this.ticketInfo.getPassenger().fullName();
		final String airportGate = "GATE: " + this.ticketInfo.getGate();
		final String passengerSeat = "SEAT: " + this.ticketInfo.getSeat();
		final String flightNumber = "FLT: " + this.ticketInfo.getFlight().getCompleteNumber();
		final String arrTime = "ARR: " + this.ticketInfo.getFlight().getArrivalTime();
		final String depTime = "DEP: " + this.ticketInfo.getFlight().getDepartureTime();
		final String depDate = "DATE: " + this.ticketInfo.getFlight().getDepartureDate();
		addRow(table, name, "CLASS: " + this.ticketInfo.getPassenger().getTravelClass(), name);
		addRow(table, flightNumber, "PNR: " + this.ticketInfo.getPnr(), flightNumber);
		addRow(table, this.ticketInfo.getFlight().getOriginCityWithAirportCode(), this.ticketInfo.getFlight().getDestinationCityWithAirportCode(),
				this.ticketInfo.getFlight().getOriginDestinationAirportCodes());
		addRow(table, depTime, arrTime, depTime + " " + arrTime);
		addRow(table, "SEQ: " + this.ticketInfo.getSeqNo(), passengerSeat, depDate);
		addRow(table, depDate, airportGate, passengerSeat + "   " + airportGate);
		document.add(table);
		writeVerticalLine(contentByte);
		writeBarCode(contentByte);
	}

	public void writeHeader(PdfContentByte contentByte) {
		// Passenger Copy - Top Header
		contentByte.beginText();
		contentByte.moveText(60, 190);
		contentByte.showText("DD Airways Kiosk Check-In");
		contentByte.endText();

		// Airline Copy - Top Header
		contentByte.beginText();
		contentByte.moveText(420, 190);
		contentByte.showText("Kiosk Check-In");
		contentByte.endText();
	}

	void writeVerticalLine(PdfContentByte contentByte) throws Exception {
		contentByte.setLineDash(3, 3, 0);
		// move to right side as less space is desired for airline stub
		// and more space for passenger stub.
		contentByte.moveTo(410, 525);
		contentByte.lineTo(410, 0);
		contentByte.stroke();
	}

	private void writeBarCode(PdfContentByte contentByte) throws Exception {
		// Generate Barcode PDF417
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix bitMatrix = writer.encode(getBarCodeText(), BarcodeFormat.PDF_417, 400, 80);
		BufferedImage barcode = MatrixToImageWriter.toBufferedImage(bitMatrix);

		ByteArrayOutputStream barcodeBaos = new ByteArrayOutputStream();
		ImageIO.write(barcode, "jpg", barcodeBaos);
		Image itextImage = Image.getInstance(barcodeBaos.toByteArray());

		// contentByte.addImage(itextImage, itextImage.getWidth(), 0, 0,
		// itextImage.getHeight(), itextImage.getAbsoluteX(),
		// itextImage.getAbsoluteY());
		// barcode on left passenger copy
		contentByte.addImage(itextImage, 300, 0, 0, 60, 50, itextImage.getAbsoluteY());
		// barcode on right airline copy
		contentByte.addImage(itextImage, 120, 0, 0, 60, 420, itextImage.getAbsoluteY());
	}

	private String getBarCodeText() {
		return this.ticketInfo.getFlight().getBarcodeData() + this.ticketInfo.getGate() + this.ticketInfo.getSeat() + this.ticketInfo.getSeqNo() + this.ticketInfo.getPassenger().fullName();
	}

	private PdfPCell cell(Phrase phrase) {
		PdfPCell cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setFixedHeight(20f);
		return cell;
	}

	private void addRow(PdfPTable table, String s1, String s2, String s3) {
		table.addCell(cell(new Phrase(s1)));
		table.addCell(cell(new Phrase(s2)));
		table.addCell(cell(new Phrase("")));
		table.addCell(cell(new Phrase(s3)));
	}
}