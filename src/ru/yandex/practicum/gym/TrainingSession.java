package ru.yandex.practicum.gym;

import java.time.DayOfWeek;
import java.util.Objects;

/**
 * Занятие.
 */
public class TrainingSession {

    /**
     * Группа.
     */
    private Group group;

    /**
     * Тренер.
     */
    private Coach coach;

    /**
     * День недели.
     */
    private DayOfWeek dayOfWeek;

    /**
     * Время начала занятия.
     */
    private TimeOfDay timeOfDay;

    public TrainingSession(Group group, Coach coach, DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        this.group = group;
        this.coach = coach;
        this.dayOfWeek = dayOfWeek;
        this.timeOfDay = timeOfDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingSession trainingSession = (TrainingSession) o;
        return coach.equals(trainingSession.coach)
            && dayOfWeek == trainingSession.dayOfWeek
            && group.equals(trainingSession.group)
            && timeOfDay.equals(trainingSession.timeOfDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coach, group, dayOfWeek, timeOfDay);
    }

    public Group getGroup() {
        return group;
    }

    public Coach getCoach() {
        return coach;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }
}
