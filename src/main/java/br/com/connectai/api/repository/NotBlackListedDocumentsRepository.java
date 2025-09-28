package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.BlackListedDocument;
import br.com.connectai.api.models.db.NotBlackListedDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotBlackListedDocumentsRepository extends JpaRepository<NotBlackListedDocuments, Integer> {
    Optional<NotBlackListedDocuments> findByCode(Long code);
}
