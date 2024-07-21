package dev.creatormind.respawntimeout.state;


public class PlayerState {

    public long deathTimestamp = 0L;

    // This is a player specific timeout to be applied when the timeout is set to random.
    public long respawnTimeout = 0L;

}
