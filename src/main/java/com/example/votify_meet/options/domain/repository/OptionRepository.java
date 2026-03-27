package com.example.votify_meet.options.domain.repository;
import com.example.votify_meet.options.domain.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,String> {
    List<Option> findAllByEvent_Id(String eventId);
    List<Option> findAllByEvent_IdIn(List<String> eventIds);
}
