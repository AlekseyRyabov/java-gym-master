package ru.yandex.practicum.gym;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Расписание.
 */
public class Timetable {

    private Map<DayOfWeek, Map<TimeOfDay, Set<TrainingSession>>> timetable = new HashMap<>();
    private Map<Coach, Integer> countSessionsByCoach = new HashMap<>();


    /**
     * Добавляет новую тренировку.
     *
     * @throws IllegalArgumentException - если тренировка некорректно создана
     * @param trainingSession тренировка с указанием группы, тренера, дня недели и времени начала занятия
     */
    public void addNewTrainingSession(TrainingSession trainingSession) {
        //сохраняем занятие в расписании
        if (isInvalid(trainingSession)) {
            throw new IllegalArgumentException("Невозможно добавить некорректное занятие в расписание");
        }
        DayOfWeek dayOfWeek = trainingSession.getDayOfWeek();
        if (timetable.containsKey(dayOfWeek)) {
            TreeMap<TimeOfDay, Set<TrainingSession>> trainingSessionsForDay =
                (TreeMap<TimeOfDay, Set<TrainingSession>>) timetable.get(dayOfWeek);
            TimeOfDay timeOfDay = trainingSession.getTimeOfDay();
            if (checkIntersectSessionsForOneCoach(trainingSession, trainingSessionsForDay, timeOfDay)) {
                return;
            }
            if (trainingSessionsForDay.containsKey(trainingSession.getTimeOfDay())) {
                Set<TrainingSession> sessions = trainingSessionsForDay.get(timeOfDay);
                addSession(trainingSession, sessions);
                return;
            }
            Set<TrainingSession> sessions = new HashSet<>();
            addSession(trainingSession, sessions);
            trainingSessionsForDay.put(timeOfDay, sessions);
            return;
        }
        Set<TrainingSession> sessions = new HashSet<>();
        addSession(trainingSession, sessions);
        Map<TimeOfDay, Set<TrainingSession>> trainingSessionsForDay = new TreeMap<>();
        trainingSessionsForDay.put(trainingSession.getTimeOfDay(), sessions);
        timetable.put(trainingSession.getDayOfWeek(), trainingSessionsForDay);
    }

    //Проверяем, что тренировки для одного тренера не пересекаются по времени
    private boolean checkIntersectSessionsForOneCoach(
        TrainingSession trainingSession,
        TreeMap<TimeOfDay, Set<TrainingSession>> trainingSessionsForDay,
        TimeOfDay timeOfDay
    ) {
        Map.Entry<TimeOfDay, Set<TrainingSession>> flourEntry = trainingSessionsForDay.floorEntry(timeOfDay);
        Map.Entry<TimeOfDay, Set<TrainingSession>> ceilingEntry = trainingSessionsForDay.ceilingEntry(timeOfDay);
        return (Objects.nonNull(flourEntry) && flourEntry.getValue().stream()
            .anyMatch(
                session -> session.getCoach().equals(trainingSession.getCoach())
                    && (flourEntry.getKey().getHours() * 60
                    + flourEntry.getKey().getMinutes()
                    + session.getGroup().getDuration()) >=
                    (timeOfDay.getHours() * 60 + timeOfDay.getMinutes())
            ))
            || (Objects.nonNull(ceilingEntry) && ceilingEntry.getValue().stream()
            .anyMatch(
                session -> session.getCoach().equals(trainingSession.getCoach())
                    && (ceilingEntry.getKey().getHours() * 60
                    + ceilingEntry.getKey().getMinutes()) <=
                    (timeOfDay.getHours() * 60 + timeOfDay.getMinutes() + trainingSession.getGroup().getDuration())));
    }

    //Вспомогательный метод для заполнения карты тренера к кол-ву тренировок.
    private void addSession(TrainingSession trainingSession, Set<TrainingSession> sessions) {
        sessions.add(trainingSession);
        Coach coach = trainingSession.getCoach();
        if (countSessionsByCoach.containsKey(coach)) {
            countSessionsByCoach.put(coach, countSessionsByCoach.get(coach) + 1);
            return;
        }
        countSessionsByCoach.put(coach, 1);
    }

    //Метод для проверки валидности проверяемой тренировки.
    private boolean isInvalid(TrainingSession trainingSession) {
        if (Objects.isNull(trainingSession)
            || Objects.isNull(trainingSession.getDayOfWeek()) ||
            Objects.isNull(trainingSession.getTimeOfDay())) {
            return true;
        }
        if (trainingSession.getTimeOfDay().getHours() < 0 || trainingSession.getTimeOfDay().getHours() > 23) {
            return true;
        }
        if (trainingSession.getTimeOfDay().getMinutes() < 0 || trainingSession.getTimeOfDay().getMinutes() > 59) {
            return true;
        }
        if (Objects.isNull(trainingSession.getCoach()) || Objects.isNull(trainingSession.getGroup())) {
            return true;
        }
        return trainingSession.getGroup().getDuration() < 0;
    }

    /**
     * Метод, возвращающий расписание тренировок за указанный день недели.
     *
     * @param dayOfWeek день недели
     * @throws IllegalArgumentException - если в метод передан null
     * @return расписание в этот день
     */
    public Map<TimeOfDay, Set<TrainingSession>> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        if (Objects.isNull(dayOfWeek)) {
            throw new IllegalArgumentException();
        }
        return timetable.getOrDefault(dayOfWeek, Collections.emptyMap());
    }

    /**
     * Метод, возвращающий тренировки за указанный день недели в указанное время.
     *
     * @param dayOfWeek день недели
     * @param timeOfDay время начала занятия
     * @throws IllegalArgumentException - если один из параметров null
     * @return тренировки в это время дня
     */
    public Set<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        if (Objects.isNull(dayOfWeek) || Objects.isNull(timeOfDay)) {
            throw new IllegalArgumentException();
        }
        Map<TimeOfDay, Set<TrainingSession>> sessionsSchedule = getTrainingSessionsForDay(dayOfWeek);
        return sessionsSchedule.isEmpty()
            ? Collections.emptySet()
            : sessionsSchedule.getOrDefault(timeOfDay, Collections.emptySet());
    }

    /**
     * Метод, возвращающий кол-во тренировок в неделю по каждому тренеру по убыванию кол-ва.
     */
    public Set<CounterOfTrainings> getCountByCoaches() {
        return countSessionsByCoach.entrySet().stream()
            .map(entry -> new CounterOfTrainings(entry.getKey(), entry.getValue()))
            .collect(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparingInt(CounterOfTrainings::count).reversed()))
            );
    }
}
