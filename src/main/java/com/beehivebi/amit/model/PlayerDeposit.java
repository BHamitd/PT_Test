package com.beehivebi.amit.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PlayersDeposits")
public class PlayerDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer playerId;
    private Date date;
    private Double amount;

    @Version
    private int version;

    public PlayerDeposit() {
    }

    public PlayerDeposit(Integer playerId, Double amount)
    {
        this(playerId,new Date(),amount);
    }

    public PlayerDeposit(Integer playerId,Date date, Double amount)
    {
        this.playerId = playerId;
        this.date = date;
        this.amount = amount;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }
}
