package com.electoral.transparency_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.electoral.transparency_service.entity.TransparencyRecord;

public interface TransparencyRepository extends JpaRepository<TransparencyRecord, Long> {

    List<TransparencyRecord> findByElectionId(Long electionId);

    Page<TransparencyRecord> findByElectionId(Long electionId, Pageable pageable);
}