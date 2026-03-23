package dev.simplix.cirrus.model;

public final class SimpleSound {

    private final CirrusSound sound;
    private final CirrusSoundCategory soundCategory;
    private final float volume;
    private final float pitch;

    public SimpleSound(CirrusSound sound, CirrusSoundCategory soundCategory, float volume, float pitch) {
        this.sound = sound;
        this.soundCategory = soundCategory;
        this.volume = volume;
        this.pitch = pitch;
    }

    public CirrusSound sound() {
        return this.sound;
    }

    public CirrusSoundCategory soundCategory() {
        return this.soundCategory;
    }

    public float volume() {
        return this.volume;
    }

    public float pitch() {
        return this.pitch;
    }

    public static SimpleSound of(CirrusSound sound) {
        return new SimpleSound(sound, CirrusSoundCategory.MASTER, 1.0f, 1.0f);
    }

    public static SimpleSound of(CirrusSound sound, float volume, float pitch) {
        return new SimpleSound(sound, CirrusSoundCategory.MASTER, volume, pitch);
    }
}
