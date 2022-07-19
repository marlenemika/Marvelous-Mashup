package de.uulm.sopra.team08.server.data;

import java.util.Objects;

public class PlayerBase {

    private final String name;
    private final String deviceId;


    public PlayerBase(String name, String deviceId) {
        this.name = name;
        this.deviceId = deviceId;
    }


    public String getName() {
        return name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Creates an unique id based on {@link Player#deviceId} and {@link Player#name}
     *
     * @return an unique id based on {@link Player#deviceId} and {@link Player#name}
     */
    public String getUniqueID() {
        return String.format("%s:%d:%s:%d", deviceId, deviceId.length(), name, name.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerBase)) return false;

        final PlayerBase pb = (PlayerBase) o;

        return getName().equals(pb.getName())
               && getDeviceId().equals(pb.getDeviceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, deviceId);
    }

    @Override
    public String toString() {
        return String.format("%s{name=%s,id=%s}",
                getClass().getSimpleName(),
                getName(),
                getDeviceId()
        );
    }

}
