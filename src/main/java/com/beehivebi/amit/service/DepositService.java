package com.beehivebi.amit.service;

import com.beehivebi.amit.model.PlayerDeposit;
import com.beehivebi.amit.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DepositService {

    @Autowired
    private DepositRepository depositRepository;

    public void create(PlayerDeposit playerDeposit)
    {
        depositRepository.saveAndFlush(playerDeposit);
    }

    public double getAggregaetdDepositAmountByPlayer(Integer playerId)
    {
        double aggAmount =0;
        List<PlayerDeposit> playerDepositList = depositRepository.findByPlayerId(playerId);
        for (PlayerDeposit playerDeposit:playerDepositList)
        {
            aggAmount += playerDeposit.getAmount();
        }
        return aggAmount;
    }

}
