package de.uulm.sopra.team08.server.data;

import de.uulm.sopra.team08.util.Role;

import java.util.Objects;

public class Player extends PlayerBase {

    private final Role role;


    public Player(String name, String deviceId, Role role) {
        super(name, deviceId);
        this.role = role;
    }


    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;

        final Player p = (Player) o;

        return getName().equals(p.getName())
               && getDeviceId().equals(p.getDeviceId())
               && getRole().equals(p.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDeviceId(), getRole());
    }

    @Override
    public String toString() {
        return String.format("Player{name=%s,id=%s,role=%s}",
                getName(),
                getDeviceId(),
                getRole()
        );
    }

}
