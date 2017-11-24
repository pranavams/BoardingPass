package com.ddairways.viewables;

import java.io.ByteArrayOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public abstract class PdfBoardingPass implements Viewable {

	protected Rectangle pageSize = null;

	public PdfBoardingPass(Rectangle pageSize) {
		this.pageSize = pageSize;
	}

	public final byte[] create() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Document document = new Document(pageSize);
		final PdfWriter pdfWriter = PdfWriter.getInstance(document, bos);
		document.open();

		PdfContentByte contentByte = pdfWriter.getDirectContent();
		setFontAndSize(contentByte);
		writeHeader(contentByte);
		writeBody(document, contentByte);
		writeFooter(contentByte);
		document.close();

		return bos.toByteArray();
	}

	public abstract void writeBody(Document document, PdfContentByte contentByte) throws Exception;

	public void writeFooter(PdfContentByte contentByte) {
	}

	public void writeHeader(PdfContentByte contentByte) {
	}

	public void setFontAndSize(PdfContentByte contentByte) throws Exception {
	}

}
