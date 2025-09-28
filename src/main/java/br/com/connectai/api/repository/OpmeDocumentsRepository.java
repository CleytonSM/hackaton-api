package br.com.connectai.api.repository;

import br.com.connectai.api.models.db.NotBlackListedDocuments;
import br.com.connectai.api.models.db.OpmeDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpmeDocumentsRepository extends JpaRepository<OpmeDocuments, Integer> {
    Optional<OpmeDocuments> findByCode(Long code);
}
