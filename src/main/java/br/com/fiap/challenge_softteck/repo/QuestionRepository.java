package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("""
        SELECT DISTINCT q
          FROM Question q
          JOIN q.form f
         WHERE f.code       = :code
           AND q.ordinal IN :ordinals
    """)
    List<Question> findByFormCodeAndOrdinalIn(
            @Param("code") String code,
            @Param("ordinals") List<Integer> ordinals
    );
}
