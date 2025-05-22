package br.com.fiap.challenge_softteck.repo;

import br.com.fiap.challenge_softteck.domain.UserPref;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPrefRepository extends JpaRepository<UserPref, byte[]> {}