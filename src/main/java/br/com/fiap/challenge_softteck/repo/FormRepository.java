package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.Form;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface FormRepository extends JpaRepository<Form, Long> {

    Optional<Form> findByCode(String code);

    @Query("SELECT f FROM Form f WHERE (:type IS NULL OR f.formType = :type) AND f.active = 'Y'")
    List<Form> findActiveByType(@Param("type") String type);

    @Query("SELECT f FROM Form f WHERE f.code = :code AND f.active = 'Y'")
    Optional<Form> findActiveByCode(@Param("code") String code);

    @Query("SELECT f FROM Form f WHERE f.code IN :codes AND f.active = 'Y'")
    List<Form> findActiveByCodes(@Param("codes") List<String> codes);
}
