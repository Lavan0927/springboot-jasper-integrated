package com.example.springbootjasperreportapp.service;

import com.example.springbootjasperreportapp.entity.Student;
import com.example.springbootjasperreportapp.repository.StudentRepo;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private StudentRepo studentRepository;

    public String generatePdfReport() throws FileNotFoundException, JRException {
        String path = "/Users/lavanya/Desktop/report";

        List<Student> students = studentRepository.findAll();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);

        //Load File and compile it
        File file= ResourceUtils.getFile("classpath:student.jrxml");
        JasperReport jasperReport=JasperCompileManager.compileReport(file.getAbsolutePath());

        Map<String,Object> parameters =new HashMap<>();
        parameters.put("createdBy","Lavanya");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,dataSource);

        JasperExportManager.exportReportToPdfFile(jasperPrint,path + "student.pdf");

        return "report generated in path: " + path;
    }

    public ResponseEntity<byte[]> generatePdf1Report() throws JRException, IOException {
        List<Student> students = studentRepository.findAll();
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);

        // Load the JRXML template file
        File templateFile = ResourceUtils.getFile("classpath:student.jrxml");

        // Compile the JRXML template
        JasperReport jasperReport = JasperCompileManager.compileReport(new FileInputStream(templateFile));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Lavanya");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report to a byte array (PDF)
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        // Set response headers for PDF download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "student.pdf");

        // Return the PDF as a response entity
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    public byte[] generateXlsReport() throws JRException, FileNotFoundException {
        // Load the JRXML template
//        JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/Users/lavanya/Downloads/springboot-jasperreport-app/src/main/resources/student.jrxml"));

        // Load the JRXML template file
        File templateFile = ResourceUtils.getFile("classpath:student.jrxml");

        // Compile the JRXML template
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(new FileInputStream(templateFile));
        } catch (JRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Fetch data from the database or another data source
        List<Student> students = studentRepository.findAll();

        // Convert data to a JRBeanCollectionDataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);

        // Set parameters, if needed
        Map<String, Object> parameters = new HashMap<>();

        // Generate the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export the report to XLSX
        byte[] xlsReport;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

            exporter.exportReport();

            xlsReport = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return xlsReport;
    }
}
