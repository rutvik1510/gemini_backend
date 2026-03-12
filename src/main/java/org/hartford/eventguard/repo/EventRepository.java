package org.hartford.eventguard.repo;

import org.hartford.eventguard.entity.Event;
import org.hartford.eventguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUser(User user);
}
