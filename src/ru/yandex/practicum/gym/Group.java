package ru.yandex.practicum.gym;

import java.util.Objects;

/**
 * Группа.
 */
public class Group {

    /**
     * Название группы.
     */
    private String title;

    /**
     * Тип (взрослая или детская).
     */
    private Age age;

    /**
     * Длительность (в минутах).
     */
    private int duration;

    public Group(String title, Age age, int duration) {
        this.title = title;
        this.age = age;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(title, group.title) && age == group.age && Objects.equals(duration, group.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, age, duration);
    }

    public String getTitle() {
        return title;
    }

    public Age getAge() {
        return age;
    }

    public int getDuration() {
        return duration;
    }
}
