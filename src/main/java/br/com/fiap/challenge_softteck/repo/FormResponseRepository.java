package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.FormResponse;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import java.time.*;

public interface FormResponseRepository extends JpaRepository<FormResponse,Long> {

    @Query("SELECT fr FROM FormResponse fr WHERE fr.form.code = 'CHECKIN' AND fr.userUuid = :uuid AND (:from IS NULL OR fr.answeredAt >= :from) AND (:to IS NULL OR fr.answeredAt <= :to)")
    Page<FormResponse> listCheckins(byte[] uuid, LocalDateTime from, LocalDateTime to, Pageable page);

    @Query("SELECT MAX(fr.answeredAt) FROM FormResponse fr WHERE fr.form.id = :formId AND fr.userUuid = :uuid")
    LocalDateTime lastAnswered(Long formId, byte[] uuid);

}