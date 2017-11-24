package com.ddairways.viewables;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.ddairways.model.TravelTicket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

public class Electronic extends PdfBoardingPass {

	/**
	 * 
	 */
	private final TravelTicket ticketInfo;

	public Electronic(TravelTicket ticketInfo) {
		super(new Document().getPageSize());
		this.ticketInfo = ticketInfo;
	}

	public  void writeBody(Document document, PdfContentByte canvas) throws Exception {
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(new float[] { 225, 225, 5, 190 }, document.getPageSize());

		// Generate Barcode PDF417
		MultiFormatWriter writer = new MultiFormatWriter();
		// Map<EncodeHintType, String> hints = new HashMap<>();
		// hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
		BitMatrix bitMatrix = writer.encode(getBarCodeText(), BarcodeFormat.PDF_417, 40, 15);
		BufferedImage barcode = MatrixToImageWriter.toBufferedImage(bitMatrix);

		ByteArrayOutputStream barcodeBaos = new ByteArrayOutputStream();
		ImageIO.write(barcode, "jpg", barcodeBaos);
		Image itextImage = Image.getInstance(barcodeBaos.toByteArray());

		final String name = "NAME: " + this.ticketInfo.getPassenger().fullName();
		final String flightNumber = "FLT: " + this.ticketInfo.getFlight().getCompleteNumber();
		final String arrTime = "ARR: " + this.ticketInfo.getFlight().getArrivalTime();
		final String depTime = "DEP: " + this.ticketInfo.getFlight().getDepartureTime();
		final String passengerSeat = "SEAT: " + this.ticketInfo.getSeat();
		final String depDate = "DATE: " + this.ticketInfo.getFlight().getDepartureDate();
		addRow(table, name, "CLASS: " + this.ticketInfo.getPassenger().getTravelClass(), name);
		addRow(table, flightNumber, "PNR: " + this.ticketInfo.getPnr(), flightNumber);
		addRow(table, this.ticketInfo.getFlight().getOriginCityWithAirportCode(), this.ticketInfo.getFlight().getDestinationCityWithAirportCode(),
				this.ticketInfo.getFlight().getOriginDestinationAirportCodes());
		addRow(table, depTime, arrTime, depTime + " " + arrTime);
		addRow(table, "SEQ: " + this.ticketInfo.getSeqNo(), passengerSeat, depDate);
		addRow(table, depDate, "", passengerSeat);

		// Add Agent Copy of Boarding Pass
		final Phrase agentCopy = new Phrase("Agent Copy");
		document.add(agentCopy);

		Paragraph blankLine = new Paragraph("  ");
		document.add(blankLine);
		document.add(blankLine);
		document.add(blankLine);
		document.add(table);

		// Add Agent Copy Barcode Vertically towards Right
		// separating airline stub and passenger stub
		rotate(canvas, itextImage, 275, 650, (float) Math.PI / 2, 0.75f, 0.75f);

		// Add rectangle with border to visually group agent copy
		Rectangle rect = new Rectangle(10, 630, 580, 790);
		rect.setBorder(Rectangle.BOX);
		rect.setBorderWidth(1);
		canvas.rectangle(rect);

		// Add dotted line - Marking Start of Customer Copy and End of Agent
		// Copy
		Paragraph separator = new Paragraph("cut on the dotted line below.");
		separator.setAlignment(Element.ALIGN_CENTER);
		DottedLineSeparator dottedline = new DottedLineSeparator();
		dottedline.setOffset(-2);
		dottedline.setGap(2f);
		separator.add(dottedline);
		document.add(separator);

		document.add(blankLine);
		document.add(new Phrase("Customer Copy"));

		InputStream in = getClass().getClassLoader()
				.getResourceAsStream("electronic-boarding-pass-instructions.txt");
		final DataInputStream dis = new DataInputStream(in);
		int readByte;
		StringBuilder instructions = new StringBuilder();
		while ((readByte = dis.read()) != -1) {
			instructions.append((char) readByte);
		}

		document.add(new Paragraph(instructions.toString()));

		// Add Customer Copy of Boarding Pass
		document.add(blankLine);
		document.add(blankLine);
		document.add(blankLine);

		setFontAndSize(canvas);
		canvas.beginText();
		canvas.moveText(60, 215);
		canvas.showText("DD Airways Web Check-In");
		canvas.endText();

		// Customer Copy Airline Stub - Top Header
		canvas.beginText();
		canvas.moveText(420, 215);
		canvas.showText("Web Check-In");
		canvas.endText();

		// Customer Copy Boarding Pass Details
		document.add(table);

		// contentByte.addImage(itextImage, itextImage.getWidth(), 0, 0,
		// itextImage.getHeight(), itextImage.getAbsoluteX(),
		// itextImage.getAbsoluteY());
		// Add vertical barcode to right side separating
		// airline stub and passenger stub for Customer Copy
		rotate(canvas, itextImage, 275, 100, (float) Math.PI / 2, 0.75f, 0.75f);

		rect = new Rectangle(10, 240, 580, 75);
		rect.setBorder(Rectangle.BOX);
		rect.setBorderWidth(1);
		canvas.rectangle(rect);

		// Footer
		canvas.beginText();
		canvas.moveText(25, 40);
		canvas.showText("DD Airways Electronic Boarding Pass - Wish you a Pleasant Flight");
		canvas.endText();
	}

	public void writeHeader(PdfContentByte canvas) {
		// Agent Copy Passenger Stub - Top Header
		canvas.beginText();
		canvas.moveText(60, 765);
		// cb.SetFontAndSize(bf, 12);
		canvas.showText("DD Airways Web Check-In");
		canvas.endText();

		// Agent Copy Airline Stub - Top Header
		canvas.beginText();
		canvas.moveText(420, 765);
		canvas.showText("Web Check-In");
		canvas.endText();
	}

	public void setFontAndSize(PdfContentByte canvas) throws Exception {
		canvas.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 18);
	}

	private String getBarCodeText() {
		return this.ticketInfo.getFlight().getBarcodeData() + this.ticketInfo.getSeat() + this.ticketInfo.getSeqNo() + this.ticketInfo.getPassenger().fullName();
	}

	private void rotate(PdfContentByte contentByte, Image image, int x, int y, float angleInRadians,
			float scaleWidthFactor, float scaleHeightFactor) throws Exception {
		// contentByte.addImage(image, image.getWidth(), 0, 0,
		// image.getHeight(), x, y);
		// Draw image as if the previous image was rotated around its center
		AffineTransform A = AffineTransform.getTranslateInstance(-0.5, -0.5);
		// Stretch it to its dimensions
		AffineTransform B = AffineTransform.getScaleInstance(image.getWidth() * scaleWidthFactor,
				image.getHeight() * scaleHeightFactor);
		// Rotate it
		AffineTransform C = AffineTransform.getRotateInstance(angleInRadians);
		// Move it to have the same center as above
		AffineTransform D = AffineTransform.getTranslateInstance(x + image.getWidth() / 2,
				y + image.getHeight() / 2);
		// Concatenate
		AffineTransform M = (AffineTransform) A.clone();
		M.preConcatenate(B);
		M.preConcatenate(C);
		M.preConcatenate(D);
		// Draw
		contentByte.addImage(image, M);
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