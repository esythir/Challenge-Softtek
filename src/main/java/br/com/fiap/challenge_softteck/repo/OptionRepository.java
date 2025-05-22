package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByQuestionIdOrderByOrdinalAsc(Long questionId);

}
