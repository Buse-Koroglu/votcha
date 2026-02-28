package com.example.votify_meet.options.domain.repository;
import com.example.votify_meet.options.domain.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option,String> {
}
