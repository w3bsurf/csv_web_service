package com.w3bsurf.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("files")
public class ServerCSVWebService {
	
	// Location for files uploaded through the web service
	private static final String UPLOAD_LOCATION = "c:/uploaded/";
	
	// /upload method uploads file submitted by html form
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
		
		// Check that all necessary form data parameters are provided
		if (uploadedInputStream == null || fileDetail.getFileName().equals("")) {
			return Response.status(400).entity("Form data is invalid").build();
		}
		
		// Tries to create upload folder
		try {
			File folder = new File(UPLOAD_LOCATION);
			if (!folder.exists()) {
				folder.mkdir();
			}
		} catch(SecurityException s) {
			return Response.status(500).entity("Could not create destination folder on server").build();
		}
		
		String uploadLocation = UPLOAD_LOCATION + fileDetail.getFileName();
		// Save uploaded file on hard drive
		saveToServer(uploadedInputStream, fileDetail);
		
		// Start new thread to parse uploaded csv file and insert rows to database
		Thread parserThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Parser.parseCsv(uploadLocation);
			}
		});
				
		parserThread.start();
		
		return Response.status(200).entity("File uploaded to server at location:  " + uploadLocation).build();
	}
	
	// Calls queryCsv and returns last "count" records from "field" from database
	@GET
	@Path("top")
	@Produces(MediaType.TEXT_HTML)
	public Response topField(
			@QueryParam("field") String field,
			@QueryParam("count") int count) {
		
		// Store result from MySQL query into arraylist
		ArrayList<String> fields = Query.queryCsv(field, count);
		String result = "";
		for (String r : fields) {
			result += r + "<br/>";
		}
		
		return Response.status(200).entity("<html>" + "<title>Query result</title><body><b>" + result + "</b></body></html>").build();
	}
	
	// Saves uploaded file to hard drive
	private void saveToServer(InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail) {
		
		String uploadedFileLocation = UPLOAD_LOCATION + fileDetail.getFileName();
		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException i) {
			i.printStackTrace();
		} 
	}
}
