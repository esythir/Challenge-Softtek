package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.FormResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FormResponseRepository extends JpaRepository<FormResponse, Long> {

    @Query("SELECT fr FROM FormResponse fr " +
            "WHERE fr.form.code = 'CHECKIN' " +
            "  AND fr.userUuid = :uuid " +
            "  AND (:from IS NULL OR fr.answeredAt >= :from) " +
            "  AND (:to   IS NULL OR fr.answeredAt <= :to)")
    Page<FormResponse> listCheckins(
            @Param("uuid") byte[] uuid,
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to,
            Pageable page
    );

    @Query("SELECT DISTINCT fr FROM FormResponse fr " +
            "LEFT JOIN FETCH fr.answers a " +
            "LEFT JOIN FETCH a.option o " +
            "LEFT JOIN FETCH a.question q " +
            "WHERE fr.form.code = 'CHECKIN' " +
            "  AND fr.userUuid = :uuid " +
            "  AND fr.answeredAt >= :from " +
            "  AND fr.answeredAt <  :to")
    List<FormResponse> findCheckinsBetween(
            @Param("uuid") byte[] uuid,
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to
    );

    @Query("SELECT MAX(fr.answeredAt) " +
            "FROM FormResponse fr " +
            "WHERE fr.form.id = :formId " +
            "  AND fr.userUuid = :uuid")
    LocalDateTime lastAnswered(
            @Param("formId") Long formId,
            @Param("uuid")   byte[] uuid
    );
}
