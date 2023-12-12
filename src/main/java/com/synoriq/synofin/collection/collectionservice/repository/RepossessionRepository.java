package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.RepossessionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepossessionRepository extends JpaRepository<RepossessionEntity, Long> {
    public RepossessionEntity findTop1ByLoanIdOrderByCreatedDateDesc(Long loanId);

    @Query(nativeQuery = true, value = "select cast(u.\"name\" as text) as name from master.users u where u.user_id = :userId")
    public String getNameFromUsers(@Param("userId") Long userId);
}
