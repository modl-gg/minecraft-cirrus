package dev.simplix.cirrus.model;

public record SimpleSound(CirrusSound sound, CirrusSoundCategory soundCategory, float volume, float pitch) {

    public static SimpleSound of(CirrusSound sound) {
        return new SimpleSound(sound, CirrusSoundCategory.MASTER, 1.0f, 1.0f);
    }

    public static SimpleSound of(CirrusSound sound, float volume, float pitch) {
        return new SimpleSound(sound, CirrusSoundCategory.MASTER, volume, pitch);
    }
}
