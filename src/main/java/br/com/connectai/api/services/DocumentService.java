package br.com.connectai.api.services;

import br.com.connectai.api.models.db.BlackListedDocument;
import br.com.connectai.api.models.db.Doctor;
import br.com.connectai.api.models.db.Document;
import br.com.connectai.api.models.db.NotBlackListedDocuments;
import br.com.connectai.api.models.db.OpmeDocuments;
import br.com.connectai.api.models.db.Patient;
import br.com.connectai.api.models.dto.DocumentResponse;
import br.com.connectai.api.repository.BlackListedDocumentsRepository;
import br.com.connectai.api.repository.DocumentRepository;
import br.com.connectai.api.repository.NotBlackListedDocumentsRepository;
import br.com.connectai.api.repository.OpmeDocumentsRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository repository;
    @Autowired
    private PatientService patientService;
    @Autowired
    private BlackListedDocumentsRepository blackListedDocumentsRepository;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private OpmeDocumentsRepository opmeDocumentsRepository;
    @Autowired
    private NotBlackListedDocumentsRepository notBlackListedDocumentsRepository;

    public void uploadDocument(int patientId, int doctorId, MultipartFile file) throws IOException {
        Patient patient = patientService.getAtomicPatientById(patientId);
        Doctor doctor = doctorService.getAtomicDoctorById(doctorId);
        Document document = new Document();
        document.setPatient(patient);
        document.setDoctor(doctor);
        document.setName(file.getOriginalFilename());
        document.setPath("documents/" + patientId + "/" + file.getOriginalFilename());
        document.setStatus("approved"); // Status padrão
        boolean done = false;
        // EXTRAIR TEXTO DIRETAMENTE DO ARQUIVO SEM SALVAR
        String words = extractWordsFromPdfSimple(file).toUpperCase();

        // VALIDAR CONTRA BLACKLIST
        List<BlackListedDocument> blackListedDocuments = blackListedDocumentsRepository.findAll();
        List<String> blacklistedWords = blackListedDocuments.stream()
                .map(BlackListedDocument::getProcess)
                .map(String::toUpperCase)
                .toList();

        // Se encontrar alguma palavra da blacklist, muda status para "reviewing"
        String result = blacklistedWords.stream()
                .filter(words::contains)
                .findFirst()
                .orElse(null);

        if(result != null) {
            document.setStatus("reviewing");
            document.setAuditTime("5 dias para auditoria");
            repository.save(document);
            return;
        }

        List<OpmeDocuments> opmeDocuments = opmeDocumentsRepository.findAll();
        List<String> opmeDocumentsWords = opmeDocuments.stream()
                .map(OpmeDocuments::getProcess)
                .map(String::toUpperCase)
                .toList();
        String result2 = opmeDocumentsWords.stream()
                .filter(words::contains)
                .findFirst().orElse(null);
        if(result2 != null) {
            document.setStatus("reviewing");
            document.setAuditTime("10 dias para auditoria");
            repository.save(document);
            return;
        }

        List<NotBlackListedDocuments> notBlackListedDocuments = notBlackListedDocumentsRepository.findAll();
        List<String> notBlacklistedWords = notBlackListedDocuments.stream()
                .map(NotBlackListedDocuments::getProcess)
                .map(String::toUpperCase)
                .toList();
        String result3 = notBlacklistedWords.stream()
                .filter(words::contains)
                .findFirst().orElse(null);

        if(result3 != null) {
            document.setStatus("approved");
            document.setAuditTime("Aprovado automaticamente");
            repository.save(document);
            return;
        }
        document.setStatus("denied");
        repository.save(document);
        throw new RuntimeException("Documento negado" );
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

    public List<Document> listDocumentsByDoctorId(int doctorId) {
        Doctor doctor = doctorService.getAtomicDoctorById(doctorId);
        return repository.findAllByDoctor(doctor);
    }

    public void updateDocumentStatus(int documentId, String status) {
        Document document = repository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento não encontrado"));
        document.setStatus(status);
        repository.save(document);
    }
}
