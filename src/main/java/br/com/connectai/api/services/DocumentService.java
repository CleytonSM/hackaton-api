package br.com.connectai.api.services;

import br.com.connectai.api.models.db.BlackListedDocument;
import br.com.connectai.api.models.db.Document;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.models.dto.DocumentResponse;
import br.com.connectai.api.repository.BlackListedDocumentsRepository;
import br.com.connectai.api.repository.DocumentRepository;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository repository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private BlackListedDocumentsRepository blackListedDocumentsRepository;

    public void uploadDocument(int patientId, MultipartFile file) throws IOException {
        Patient patient = patientService.getAtomicPatientById(patientId);
        Document document = new Document();
        document.setPatient(patient);
        document.setName(file.getOriginalFilename());
        document.setPath("documents/" + patientId + "/" + file.getOriginalFilename());
        document.setStatus("approved"); // Status padrão

        // EXTRAIR TEXTO DIRETAMENTE DO ARQUIVO SEM SALVAR
        String words = extractWordsFromPdfSimple(file).toUpperCase();

        // VALIDAR CONTRA BLACKLIST
        List<BlackListedDocument> blackListedDocuments = blackListedDocumentsRepository.findAll();
        List<String> blacklistedWords = blackListedDocuments.stream()
                .map(BlackListedDocument::getProcess)
                .map(String::toUpperCase)
                .toList();

        // Se encontrar alguma palavra da blacklist, muda status para "reviewing"
        blacklistedWords.stream()
                .filter(words::contains)
                .findFirst()
                .ifPresent(x -> document.setStatus("reviewing"));

        repository.save(document);
    }

    // VERSÃO SIMPLES - Extrai texto diretamente do
    private String extractWordsFromPdfSimple(MultipartFile file) throws IOException {
        String allWords = "";
        File convFile = File.createTempFile("upload_", ".pdf"); // cria um arquivo temporário
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        try (PDDocument document = Loader.loadPDF(convFile)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String extractedText = textStripper.getText(document);

            if (extractedText != null && !extractedText.trim().isEmpty()) {
                String[] words = extractedText.toUpperCase()
                        .split("\\s+"); // Divide por espaços

                allWords = extractedText;
            }

        } catch (IOException e) {
            System.err.println("Erro ao extrair texto do PDF: " + e.getMessage());
        }

        return allWords;
    }

    public DocumentResponse listDocumentsByPatientId(int patientId) {
        Patient patient = patientService.getAtomicPatientById(patientId);
        List<Document> documents = repository.findByPatient(patient);

        DocumentResponse documentResponse = new DocumentResponse();
        documentResponse.setPatientName(patient.getName());
        documentResponse.setDocuments(documents);

        return documentResponse;
    }
}
