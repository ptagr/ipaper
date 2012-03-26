package com.ipaper.myapp;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

public class CustomPDFView extends AbstractPdfView {

	@Override
	protected void buildPdfDocument(Map<String, Object> model,
			Document document, PdfWriter writer, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

	}

	protected final void renderCustomMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		final EpaperBase epaper = (EpaperBase) model.get("epaper");
		String url = epaper.getUrl();
		if (url != null && url != "") {
			
			System.out.println("FILE FOUND ON DROPBOX +====================");
			response.sendRedirect(url);
		} else {

			EpaperTask et = new EpaperTask(epaper);
			final byte[] content = et.getDirectCompletePdf();
			

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				if (content != null)
					baos.write(content);
				// Flush to HTTP response.
				writeToResponse(response, baos);

//				new Thread(new EpaperManager(content, epaper.buildFileName(),
//						format.format(epaper.getDate()))).start();
				
				new Thread(){
					public void run(){
						System.out.println("UPLOAD TO DROPBOX STARTED");
						epaper.uploadToDropbox(content);
						System.out.println("UPLOAD TO DROPBOX ENDED");
					}
				}.start();

			} finally {
//				content = null;
				baos.close();
				baos = null;
				System.gc();
				System.out.println("FINALLY BLOCK ENDED");
			}

		}
	}


	@Override
	/**
	 * Prepares the view given the specified model, merging it with static
	 * attributes and a RequestContext attribute, if necessary.
	 * Delegates to renderMergedOutputModel for the actual rendering.
	 * @see #renderMergedOutputModel
	 */
	public void render(Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("Rendering view with name '" + this.getBeanName()
					+ "' with model " + model + " and static attributes "
					+ this.getStaticAttributes());
		}

		// Consolidate static and dynamic model attributes.
		Map<String, Object> mergedModel = new HashMap<String, Object>(this
				.getStaticAttributes().size()
				+ (model != null ? model.size() : 0));
		mergedModel.putAll(this.getStaticAttributes());
		if (model != null) {
			mergedModel.putAll(model);
		}

		// Expose RequestContext?
		if (this.getRequestContextAttribute() != null) {
			mergedModel.put(this.getRequestContextAttribute(),
					createRequestContext(request, response, mergedModel));
		}

		prepareResponse(request, response);
		renderCustomMergedOutputModel(mergedModel, request, response);

	}
}
