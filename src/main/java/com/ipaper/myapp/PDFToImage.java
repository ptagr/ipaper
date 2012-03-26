package com.ipaper.myapp;

//$Id: PDFToImage.java 13072 2011-04-14 16:47:46Z mike $

import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.Transparency;
import java.awt.image.ComponentColorModel;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.imageio.*;
import org.faceless.pdf2.*;

/**
 * This example converts a PDF to a multi-page TIFF or a series of bitmap
 * images, using the PDF Viewer extension.
 * 
 * Usage is:
 * 
 * java PDFToImage [--dpi <dpi>] [--pages <pagerange>] [--format <format>]
 * [--model <model>] file.pdf
 * 
 * where "dpi" is the DPI of the final image (defaults to 200), "format" is the
 * file format - one of tiff, png or jpeg, and "model" is the color model to use
 * - one of bw, gray, rgb, rgba, cmyk or bwNNN, where "NNN" is a number between
 * 1 and 254 and determines the threshold where gray is considered to be black
 * or white. TIFF can use any of these models, jpeg and rgb will only use RGB or
 * Gray.
 * 
 * For TIFF images, the output filename is of the form "file.tif". For PNG or
 * JPEG images, the files are of the form "file-nnn.png", where "nnn" is the
 * page number.
 * 
 * @since 2.8.2
 */
public class PDFToImage {

	public static byte[] convertFirstPageToImage(InputStream in)
			throws IOException {
		return convertToImage("50", "rgb", "1", "png", in);
	}

	public static byte[] convertToImage(String dpis, String model,
			String pages, String formatimg, InputStream in) throws IOException {
		ByteArrayOutputStream _boutStreamPNG = new ByteArrayOutputStream();
		ColorModel cm = PDFParser.RGB;
		float dpi = 200;
		String format = "png";
		String pagerange = null;
		if (dpis != null && dpis.length() > 0) {
			try {
				dpi = Integer.parseInt(dpis);
			} catch (NumberFormatException e) {
				usage();
			}
		}

		if (model != null && model.length() > 0) {
			// String model = args[++i];
			if (model.equals("bw")) {
				cm = PDFParser.BLACKANDWHITE;
			} else if (model.equals("gray")) {
				cm = PDFParser.GRAYSCALE;
			} else if (model.equals("rgb")) {
				cm = PDFParser.RGB;
			} else if (model.equals("cmyk")) {
				cm = PDFParser.CMYK;
			} else if (model.equals("rgba")) {
				cm = PDFParser.RGBA;
			} else if (model.equals("direct")) {
				cm = ColorModel.getRGBdefault();
			} else if (model.equals("intent")) {
				cm = null;
			} else if (model.startsWith("bw")) {
				try {
					int threshold = Integer.parseInt(model.substring(2));
					cm = PDFParser.getBlackAndWhiteColorModel(threshold);
				} catch (NumberFormatException e) {
					usage();
				}
			} else {
				usage();
			}
		}

		if (pages != null && pages.length() > 0) {
			pagerange = pages;
		}

		if (formatimg != null && formatimg.length() > 0) {
			// format = args[++i];
			if (formatimg.equals("tiff"))
				format = "tif";
			if (formatimg.equals("jpeg"))
				format = "jpg";
			if (!formatimg.equals("tif") && !formatimg.equals("jpg")
					&& !formatimg.equals("png")) {
				usage();
			}
		}

		// String infile = args[i];
		// String prefix = filename;
		// if (filename.endsWith(".pdf") || filename.endsWith(".PDF")) {
		// prefix = filename.substring(0, filename.length() - 4);
		// }
		// System.out.print("Reading \"" + filename + "\"... ");
		ICC_Profile icc = null;
		PDF pdf = new PDF(new PDFReader(in));
		if (cm == null) {
			// "intent" specified as the ColorModel: extract the model from
			// the PDF OutputIntent and use that. Only works with TIFF!
			OutputProfile profile = pdf.getBasicOutputProfile();
			if (profile.isSet(OutputProfile.Feature.HasOutputIntentGTS_PDFX)) {
				icc = profile.getOutputIntentDestinationProfile("GTS_PDFX");
			} else if (profile
					.isSet(OutputProfile.Feature.HasOutputIntentGTS_PDFA1)) {
				icc = profile.getOutputIntentDestinationProfile("GTS_PDFA1");
			} else if (profile
					.isSet(OutputProfile.Feature.HasOutputIntentGTS_PDFA)) {
				icc = profile.getOutputIntentDestinationProfile("GTS_PDFA");
			}
			if (icc == null) {
				throw new IllegalArgumentException("No OutputIntent profile");
			} else {
				ICC_ColorSpace cs = new ICC_ColorSpace(icc);
				cm = new ComponentColorModel(cs, false, false,
						Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
			}
		}
		List origpages = new ArrayList(pdf.getPages());
		if (pagerange != null) {
			limitPages(pdf, pagerange);
			pagerange = null;
		}
		PDFParser parser = new PDFParser(pdf);

		if (format.equals("tif")) {
			// System.out.print("Writing \"" + prefix + ".tif\"... ");
			// FileOutputStream out = new FileOutputStream(prefix + ".tif");
			parser.writeAsTIFF(_boutStreamPNG, (int) dpi, cm);
			_boutStreamPNG.close();
		} else {
			// DecimalFormat pageformat = new DecimalFormat("000");
			if (cm != ColorModel.getRGBdefault() && cm != PDFParser.RGB
					&& cm != PDFParser.GRAYSCALE
					&& (!format.equals("png") || cm != PDFParser.RGBA)) {
				// ColorModel for PNG must be rgb, rgba or grayscale
				// ColorModel for JPEG must be rgb or grayscale
				cm = PDFParser.RGB;
			}
			for (int j = 0; j < pdf.getNumberOfPages(); j++) {
				// int origpagenumber = origpages.indexOf(pdf.getPage(j)) + 1;
				// String outfile = prefix + "-"
				// + pageformat.format(origpagenumber) + "." + format;
				// System.out.print("Writing \"" + outfile + "\"...\n");
				PagePainter painter = parser.getPagePainter(j);
				BufferedImage image = painter.getImage(dpi, cm);
				ImageIO.write(image, format, _boutStreamPNG);
			}
		}
		System.out.println();

		return _boutStreamPNG.toByteArray();

	}

	public static byte[] convertToImage2(InputStream in) {
		ByteArrayOutputStream _boutStreamPNG = new ByteArrayOutputStream();
		ColorModel cm = PDFParser.RGB;
		float dpi = 25;
		String format = "png";

		PDF pdf;
		try {
			pdf = new PDF(new PDFReader(in));
			PDFParser parser = new PDFParser(pdf);

			PagePainter painter = parser.getPagePainter(0);
			BufferedImage image = painter.getImage(dpi, cm);
			ImageIO.write(image, format, _boutStreamPNG);

			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}

		
		return _boutStreamPNG.toByteArray();

	}

	private static void limitPages(PDF pdf, String pagerange) {
		List pagelist = pdf.getPages();
		List worklist = new ArrayList(pagelist);
		pagelist.clear();
		for (StringTokenizer st = new StringTokenizer(pagerange, ","); st
				.hasMoreTokens();) {
			String token = st.nextToken();
			int ix = token.indexOf('-');
			int p1, p2;
			if (ix == -1) {
				p1 = p2 = Integer.parseInt(token);
			} else if (ix == 0) {
				p1 = 0;
				p2 = Integer.parseInt(token.substring(1));
			} else if (ix == token.length() - 1) {
				p1 = Integer.parseInt(token.substring(0, token.length() - 1));
				p2 = worklist.size() - 1;
			} else {
				p1 = Integer.parseInt(token.substring(0, ix));
				p2 = Integer.parseInt(token.substring(ix + 1));
			}
			pagelist.addAll(worklist.subList(p1 - 1, p2));
		}
	}

	private static void usage() {
		System.out
				.println("\nUsage: java PDFToImage [--pages <pagerange>] [--dpi <dpi>]");
		System.out
				.println("                         [--format <format>] [--model <model>]  file.pdf\n");
		System.out
				.println("where \"dpi\" is the DPI of the final image (defaults to 200), \"format\" is the");
		System.out
				.println("file format - one of tiff, png or jpeg, and \"model\" is the color model to use;");
		System.out
				.println("one of bw, gray, rgb, rgba, cmyk or bwNNN, where \"NNN\" is a number between 1");
		System.out
				.println("and 254 and sets the threshold where gray is considered to be black or white.");
		System.out
				.println("Alternatively, if the PDF has an Output Intent specified (eg PDF/X or PDF/A)");
		System.out
				.println("the value 'intent' can be used to create an image matching the specified intent");
		System.out
				.println("TIFF can use any of these models, JPEG and PNG will always be RGB or Grayscale.");
		System.out
				.println("Pagerange can be used to limit the pages converted - example values are \"1\",");
		System.out
				.println("\"2-5\", \"8,9,14-18,24\" (page numbers start at 1).\n");
		System.out
				.println("For TIFF images, the output filename is of the form \"file.tif\". For PNG or JPEG");
		System.out
				.println("images the files are of the form \"file-nnn.png\", where \"nnn\" is the pagenumber.\n");
		System.exit(-1);
	}
}
