package br.com.fiap.challenge_softteck.service;

import br.com.fiap.challenge_softteck.domain.UserPref;
import br.com.fiap.challenge_softteck.dto.PrefDTO;
import br.com.fiap.challenge_softteck.repo.UserPrefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrefService {

    private final UserPrefRepository repo;

    public PrefDTO get(byte[] uuid) {
        UserPref p = repo.findById(uuid)
                .orElse(new UserPref(uuid, true, null, null, null));
        return new PrefDTO(p.getNotifEnabled());
    }

    public void save(byte[] uuid, PrefDTO dto) {
        UserPref p = repo.findById(uuid).orElse(new UserPref());
        p.setUserUuid(uuid);
        p.setNotifEnabled(dto.isNotificationsEnabled());
        repo.save(p);
    }
}
