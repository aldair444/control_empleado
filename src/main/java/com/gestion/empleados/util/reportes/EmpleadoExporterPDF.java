package com.gestion.empleados.util.reportes;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.gestion.empleados.entidades.Empleado;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class EmpleadoExporterPDF {

	private List<Empleado> listaEmpleados;

	public EmpleadoExporterPDF(List<Empleado> listaEmpleados) {
		super();
		this.listaEmpleados = listaEmpleados;
	}

	private void escribirCabeceraDeLaTabla(PdfPTable tabla) {
		PdfPCell celda = new PdfPCell();

		celda.setBackgroundColor(Color.BLUE);
		celda.setPadding(5);

		Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
		fuente.setColor(Color.WHITE);

		celda.setPhrase(new Phrase("ID", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Nombre", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Apellido", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Email", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Fecha", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Telefono", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Sexo", fuente));
		tabla.addCell(celda);

		celda.setPhrase(new Phrase("Salario", fuente));
		tabla.addCell(celda);
	}

	private void escribirDatosDeLaTabla(PdfPTable tabla) {
		for (Empleado empleado : listaEmpleados) {
			tabla.addCell(String.valueOf(empleado.getId()));
			tabla.addCell(empleado.getNombre());
			tabla.addCell(empleado.getApellido());
			tabla.addCell(empleado.getEmail());
			tabla.addCell(empleado.getFecha().toString());
			tabla.addCell(String.valueOf(empleado.getTelefono()));
			tabla.addCell(empleado.getSexo());
			tabla.addCell(String.valueOf(empleado.getSalario()));
		}
	}

	public void exportar(HttpServletResponse response) throws DocumentException, IOException {
		File tempFile = File.createTempFile("lista_empleados_", ".pdf");
		tempFile.deleteOnExit();
	
		Document documento = new Document(PageSize.A4);
		//PdfWriter.getInstance(documento, new FileOutputStream(tempFile));
		PdfWriter.getInstance(documento, response.getOutputStream());

		documento.open();

		Font fuente = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		fuente.setColor(Color.BLUE);
		fuente.setSize(18);

		Paragraph titulo = new Paragraph("Lista de empleados", fuente);
		titulo.setAlignment(Paragraph.ALIGN_CENTER);
		documento.add(titulo);

		PdfPTable tabla = new PdfPTable(8);
		tabla.setWidthPercentage(100);
		tabla.setSpacingBefore(15);
		tabla.setWidths(new float[] { 1f, 2.3f, 2.3f, 6f, 2.9f, 3.5f, 2f, 2.2f });
		tabla.setWidthPercentage(110);

		escribirCabeceraDeLaTabla(tabla);
		escribirDatosDeLaTabla(tabla);

		documento.add(tabla);
		documento.close();

		subirArchivoAPinata(tempFile.getAbsolutePath());
	}

	private void subirArchivoAPinata(String pathArchivo) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.add("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiI0MDk3MDdmOC03NjkwLTQxYjMtYjM3OS1hMDhkNzI1NzY1MDEiLCJlbWFpbCI6ImFsZGFpcmJhcm9qYXMzNEBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicGluX3BvbGljeSI6eyJyZWdpb25zIjpbeyJpZCI6IkZSQTEiLCJkZXNpcmVkUmVwbGljYXRpb25Db3VudCI6MX0seyJpZCI6Ik5ZQzEiLCJkZXNpcmVkUmVwbGljYXRpb25Db3VudCI6MX1dLCJ2ZXJzaW9uIjoxfSwibWZhX2VuYWJsZWQiOmZhbHNlLCJzdGF0dXMiOiJBQ1RJVkUifSwiYXV0aGVudGljYXRpb25UeXBlIjoic2NvcGVkS2V5Iiwic2NvcGVkS2V5S2V5IjoiYjVhYzk5Y2UyNmFlODE0NzEyMWYiLCJzY29wZWRLZXlTZWNyZXQiOiI0YWMyMzVmMzBhOTNjN2ExNzNlNzU2ODM3ZWJjZmIxYjMzMjc0M2E1YzNmYTA5M2ZiNWQ0YTZiOWNlZjY1MDdiIiwiaWF0IjoxNzEyNzg4Njk4fQ.dyGR4HTYqB4CHPi-5VD6HON6ikGHEaMw8JWs_t6VAyI");
	
		LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(pathArchivo));
	
		// Ajusta estos valores según sea necesario
		body.add("pinataMetadata", "{\"name\": \"nombredelarchivo.pdf\"}");
		body.add("pinataOptions", "{\"cidVersion\": 1}");
	
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
	
		String serverUrl = "https://api.pinata.cloud/pinning/pinFileToIPFS";
	
		ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
	
		// Maneja la respuesta según sea necesario
		try {
			JSONObject jsonResponse = new JSONObject(response.getBody());
			String ipfsHash = jsonResponse.getString("IpfsHash");
			System.out.println("ipfsHash: " + ipfsHash);
		} catch (JSONException e) {
			System.out.println("Error al parsear la respuesta JSON: " + e.getMessage());
		}
	}
}
