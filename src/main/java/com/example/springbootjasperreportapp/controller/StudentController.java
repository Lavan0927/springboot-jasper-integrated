package com.example.springbootjasperreportapp.controller;

import com.example.springbootjasperreportapp.entity.Student;
import com.example.springbootjasperreportapp.repository.StudentRepo;
import com.example.springbootjasperreportapp.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class StudentController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private StudentRepo studentRepository;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdftestReport() {
        try {
            ResponseEntity<byte[]> pdfReportResponse = reportService.generatePdf1Report();
            return pdfReportResponse;
        } catch (Exception e) {
            // Handle exceptions appropriately and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/xls")
    public ResponseEntity<byte[]> generateAndDownloadXlsReport() {
        try {
            byte[] xlsReport = reportService.generateXlsReport();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "student_report.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xlsReport);
        } catch (Exception e) {
            // Handle exceptions appropriately and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
