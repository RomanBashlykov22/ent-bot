package kz.roman.enttgbot.repository;

import kz.roman.enttgbot.model.entity.Session;
import kz.roman.enttgbot.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByUserOrderBySessionDateDesc(User user, Pageable pageable);
}
