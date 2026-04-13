package com.example.votcha.options.domain.repository;
import com.example.votcha.options.domain.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,String> {
    List<Option> findAllByEvent_Id(String eventId);
    List<Option> findAllByEvent_IdIn(List<String> eventIds);

    @Query("SELECT COUNT(o) FROM Option o WHERE o.event.id= :eventId")
    long countByEventId(@Param("eventId") String eventId);
}
