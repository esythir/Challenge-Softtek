package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface FormRepository extends JpaRepository<Form, Long> {

    Optional<Form> findByCode(String code);

    @Query("SELECT f FROM Form f WHERE (:type IS NULL OR f.formType = :type) AND f.active = 'Y'")

    List<Form> findActiveByType(String type);

}