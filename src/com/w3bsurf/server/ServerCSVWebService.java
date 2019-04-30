package com.w3bsurf.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

public class ServerCSVWebService {
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {
			
		// save the file object to the local disk
		saveToDatabase(uploadedInputStream, fileDetail);
		
		return "File uploaded successfully!";
	}
	
	private void saveToDatabase(InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail) {
		
		String uploadedFileLocation = "C:\\Users\\joona\\eclipse-workspaces\\eclipse-jee-workspace\\RESTfulWS\\" + fileDetail.getFileName();
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
