package com.beehivebi.amit.repository;

import com.beehivebi.amit.model.PlayerDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRepository extends JpaRepository<PlayerDeposit,Long>
{
    List<PlayerDeposit> findByPlayerId(Integer playerId);
}
