package br.com.connectai.api.controllers;

import br.com.connectai.api.models.dto.DocumentResponse;
import br.com.connectai.api.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload/{patientId}/{doctorId}")
    public void uploadDocument(@PathVariable int patientId, @RequestParam("file") MultipartFile file, @PathVariable int doctorId) throws IOException {
        documentService.uploadDocument(patientId, doctorId, file);
    }

    // update documebnt by patientId and documentId


    @GetMapping("/{patientId}")
    public ResponseEntity<DocumentResponse> listDocumentsByPatientId(@PathVariable int patientId) {
        return new ResponseEntity<>(documentService.listDocumentsByPatientId(patientId), HttpStatus.OK);
    }

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<?> listDocumentsByDoctorId(@PathVariable int doctorId) {
        return new ResponseEntity<>(documentService.listDocumentsByDoctorId(doctorId), HttpStatus.OK);
    }

    @PutMapping("/{documentId}/status")
    public ResponseEntity<?> updateDocumentStatus(@PathVariable int documentId, @RequestParam("status") String status) {
        documentService.updateDocumentStatus(documentId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
