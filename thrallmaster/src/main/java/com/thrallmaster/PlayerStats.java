package com.thrallmaster;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerStats 
{
    private UUID playerID;
    private HashSet<ThrallState> thralls;
    private HashSet<UUID> allies;
    
    public PlayerStats(UUID playerID) 
    {
        this.playerID = playerID;
        this.thralls = new HashSet<>();
        this.allies = new HashSet<>();
    }

    public Stream<ThrallState> getThralls()
    {
        return this.thralls.stream();
    }

    public ThrallState getThrall(UUID entityID)
    {
        return getThralls()
            .filter(x -> x.getEntityID().equals(entityID))
            .findFirst()
            .orElse(null);
    }

    public void addThrall(ThrallState state)
    {
        this.thralls.add(state);
    }
    public boolean removeThrall(ThrallState state)
    {
        return this.thralls.remove(state);
    }


    public void addAlly(UUID playerID)
    {
        this.allies.add(playerID);
    }
    public boolean removeAlly(UUID playerID)
    {
        return this.allies.remove(playerID);
    }
    public Stream<UUID> getAllies()
    {
        return this.allies.stream();
    }
}
