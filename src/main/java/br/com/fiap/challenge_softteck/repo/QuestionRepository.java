package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface QuestionRepository extends JpaRepository<Question,Long> {

    List<Question> findByFormIdOrderByOrdinal(Long formId);

}