package com.ipaper.myapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.Ostermiller.util.CircularByteBuffer;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

/**
 * 
 * @author Puneet
 */
public class MyPDFUtility {
	/*
	 * public static void mergeFiles(List<String> filesToBeMerged, String
	 * mergedFileLocation) { PDFMergerUtility pdfmu = new PDFMergerUtility();
	 * for (String s : filesToBeMerged) { pdfmu.addSource(s); }
	 * pdfmu.setDestinationFileName(mergedFileLocation); try {
	 * pdfmu.mergeDocuments(); } catch (IOException ex) {
	 * Logger.getLogger(HT.class.getName()).log(Level.SEVERE, null, ex); } catch
	 * (COSVisitorException ex) {
	 * Logger.getLogger(HT.class.getName()).log(Level.SEVERE, null, ex); } }
	 * 
	 * public static void mergeStreams(List<InputStream> streamsToBeMerged,
	 * String mergedFileLocation) {
	 * System.out.println("Number of files : "+streamsToBeMerged.size());
	 * PDFMergerUtility pdfmu = new PDFMergerUtility(); for (InputStream s :
	 * streamsToBeMerged) { pdfmu.addSource(s);
	 * 
	 * } pdfmu.setDestinationFileName(mergedFileLocation); try {
	 * pdfmu.mergeDocuments(); } catch (IOException ex) {
	 * Logger.getLogger(HT.class.getName()).log(Level.SEVERE, null, ex); } catch
	 * (COSVisitorException ex) {
	 * Logger.getLogger(HT.class.getName()).log(Level.SEVERE, null, ex); } }
	 * 
	 * public static void mergeStreams(List<InputStream> streamsToBeMerged,
	 * OutputStream outs) {
	 * System.out.println("Number of files : "+streamsToBeMerged.size());
	 * PDFMergerUtility pdfmu = new PDFMergerUtility(); for (InputStream s :
	 * streamsToBeMerged) { pdfmu.addSource(s);
	 * 
	 * } pdfmu.setDestinationStream(outs); try { pdfmu.mergeDocuments(); } catch
	 * (IOException ex) { Logger.getLogger(HT.class.getName()).log(Level.SEVERE,
	 * null, ex); } catch (COSVisitorException ex) {
	 * Logger.getLogger(HT.class.getName()).log(Level.SEVERE, null, ex); } }
	 */

	public static void concatPDFs(List<InputStream> pdfs,
			OutputStream outputStream, boolean paginate) {

		Document document = new Document();
		try {

			List<PdfReader> readers = new ArrayList<PdfReader>();
			Iterator<InputStream> iteratorPDFs = pdfs.iterator();

			// Create Readers for the pdfs.
			while (iteratorPDFs.hasNext()) {
				InputStream pdf = iteratorPDFs.next();
				PdfReader pdfReader = new PdfReader(pdf);
				readers.add(pdfReader);
			}
			
			System.out.println("Readers size : "+readers.size());

			PdfCopy writer = new PdfCopy(document, outputStream);

			document.open();

			PdfImportedPage page;
			int pageOfCurrentReaderPDF = 0;
			Iterator<PdfReader> iteratorPDFReader = readers.iterator();

			// Loop through the PDF files and add to the output.
			while (iteratorPDFReader.hasNext()) {
				PdfReader pdfReader = iteratorPDFReader.next();

				// Create a new page in the target for each source page.
				while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
					document.newPage();
					pageOfCurrentReaderPDF++;
					page = writer.getImportedPage(pdfReader,
							pageOfCurrentReaderPDF);
					writer.addPage(page);
				}
				pageOfCurrentReaderPDF = 0;
			}
			
			outputStream.flush();
			readers = null;
			document.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				for (InputStream is : pdfs) {
					is.close();
				}
				pdfs = null;
				if (document.isOpen())
					document.close();
				if (outputStream != null)
					outputStream.close();
				System.gc();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
	
	public static void concatPDFs(List<CircularByteBuffer> cbbpdfs,
			OutputStream outputStream) {
		
		Document document = new Document();
		try {

			List<PdfReader> readers = new ArrayList<PdfReader>();
			Iterator<CircularByteBuffer> iteratorPDFs = cbbpdfs.iterator();

			// Create Readers for the pdfs.
			while (iteratorPDFs.hasNext()) {
				InputStream pdf = iteratorPDFs.next().getInputStream();
				PdfReader pdfReader = new PdfReader(pdf);
				readers.add(pdfReader);
			}
			
			System.out.println("Readers size : "+readers.size());

			PdfCopy writer = new PdfCopy(document, outputStream);

			document.open();

			PdfImportedPage page;
			int pageOfCurrentReaderPDF = 0;
			Iterator<PdfReader> iteratorPDFReader = readers.iterator();

			// Loop through the PDF files and add to the output.
			while (iteratorPDFReader.hasNext()) {
				PdfReader pdfReader = iteratorPDFReader.next();

				// Create a new page in the target for each source page.
				while (pageOfCurrentReaderPDF < pdfReader.getNumberOfPages()) {
					document.newPage();
					pageOfCurrentReaderPDF++;
					page = writer.getImportedPage(pdfReader,
							pageOfCurrentReaderPDF);
					writer.addPage(page);
				}
				pageOfCurrentReaderPDF = 0;
			}
			
			outputStream.flush();
			readers = null;
			document.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (document.isOpen())
					document.close();
				if (outputStream != null)
					outputStream.close();
				System.gc();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

}
