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

public class Mobile extends PdfBoardingPass {

	/**
	 * 
	 */
	private final TravelTicket ticketInfo;

	public Mobile(TravelTicket ticketInfo) {
		super(new Rectangle(220, 340));
		this.ticketInfo = ticketInfo;
	}

	public void setFontAndSize(PdfContentByte contentByte) throws Exception {
		contentByte.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 16);
	}

	public void writeHeader(PdfContentByte contentByte) {
		// Boarding Pass Header
		contentByte.beginText();
		contentByte.moveText(70, 315);
		contentByte.showText("DD Airways");
		contentByte.endText();
	}

	public void writeFooter(PdfContentByte contentByte) {
		// Boarding Pass Footer
		contentByte.beginText();
		contentByte.moveText(35, 10);
		contentByte.showText("Mobile Boarding Pass");
		contentByte.endText();
	}

	public void writeBody(Document document, PdfContentByte contentByte) throws Exception {

		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix bitMatrix = writer.encode(getBarCodeText(), BarcodeFormat.AZTEC, 135, 135);
		BufferedImage aztecBarcode = MatrixToImageWriter.toBufferedImage(bitMatrix);
		ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
		ImageIO.write(aztecBarcode, "jpg", jpeg);
		Image itextImage = Image.getInstance(jpeg.toByteArray());
		document.add(itextImage);

		// Boarding Pass Info as Table
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(new float[] { 110, 110 }, pageSize);
		addRow(table, this.ticketInfo.getPassenger().getFirstName(), this.ticketInfo.getPassenger().getLastName());
		addRow(table, this.ticketInfo.getFlight().getCode(), this.ticketInfo.getFlight().getNumber());
		addRow(table, this.ticketInfo.getFlight().originAirportCode(), this.ticketInfo.getFlight().destinationAirportCode());
		addRow(table, this.ticketInfo.getFlight().getDepartureTime(), this.ticketInfo.getFlight().getArrivalTime());
		addRow(table, this.ticketInfo.getPassenger().getTravelClass(), this.ticketInfo.getSeat());
		addRow(table, "DATE", this.ticketInfo.getFlight().getDepartureDate());
		addRow(table, "PNR", this.ticketInfo.getPnr());
		document.add(table);
	}

	private PdfPCell cell(Phrase phrase, float height) {
		PdfPCell cell = new PdfPCell(phrase);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setFixedHeight(height);
		return cell;
	}

	private void addRow(PdfPTable table, String s1, String s2) {
		table.addCell(cell(new Phrase(s1), 18f));
		table.addCell(cell(new Phrase(s2), 18f));
	}

	private String getBarCodeText() {
		return this.ticketInfo.getFlight().getBarcodeData() + this.ticketInfo.getSeat() + this.ticketInfo.getSeqNo() + this.ticketInfo.getPassenger().fullName();
	}
}