package com.electoral.transparency_service.repository;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.electoral.transparency_service.entity.TransparencyRecord;

public interface TransparencyRepository extends JpaRepository<TransparencyRecord, Long> {

    List<TransparencyRecord> findByElectionId(Long electionId);

    Page<TransparencyRecord> findByElectionId(Long electionId, Pageable pageable);

        @Query(value = """
                        select count(*)
                        from transparency_record
                        where provider = :originComponent
                            and record_timestamp > :timestamp
                        """, nativeQuery = true)
        int countByOriginComponentAndTimestampAfter(@Param("originComponent") String originComponent,
                                                                                                @Param("timestamp") LocalDateTime timestamp);
}