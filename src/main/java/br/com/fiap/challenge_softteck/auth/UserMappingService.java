package br.com.fiap.challenge_softteck.auth;

import br.com.fiap.challenge_softteck.utils.UuidUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserMappingService {

    private final JdbcTemplate jdbc;

    public UserMappingService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public UUID mapSubToUuid(String sub) {

        UUID id = jdbc.query(
                "SELECT user_uuid FROM TB_AUTH_MAP WHERE sub = ?",
                ps -> ps.setString(1, sub),
                rs -> rs.next() ? UuidUtil.bytesToUuid(rs.getBytes(1)) : null
        );

        if (id != null) return id;

        id = UUID.randomUUID();
        byte[] uuidBytes = UuidUtil.uuidToBytes(id);

        jdbc.update(
                "INSERT INTO TB_AUTH_MAP(sub, user_uuid) VALUES (?, ?)",
                ps -> {
                    ps.setString(1, sub);
                    ps.setBytes (2, uuidBytes);
                }
        );

        return id;
    }
}
