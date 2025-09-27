package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.BlackListedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListedDocumentsRepository extends JpaRepository<BlackListedDocument, Integer> {
    Optional<BlackListedDocument> findByCode(Long code);
}
