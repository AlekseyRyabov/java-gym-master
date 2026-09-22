package ru.yandex.practicum.gym;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TimetableTest {

    private Timetable timetable;

    @BeforeEach
    void setTimetable() {
        timetable = new Timetable();
    }

    @AfterEach
    void testCountByCoaches() {
        testGetCountByCoaches();
    }

    @Test
    void testAddNullTrainingSession() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> timetable.addNewTrainingSession(null));
    }

    @Test
    void testAddSameTrainingSession() {
        Coach coach = new Coach("Васильев", "Петр", "Иванович");
        TimeOfDay timeOfDay = new TimeOfDay(13, 45);
        TrainingSession firstTrainingSession = new TrainingSession(
            new Group(
                "Акробатика",
                Age.ADULT,
                60
            ),
            coach,
            DayOfWeek.FRIDAY,
            timeOfDay
        );
        TrainingSession secondTrainingSession = new TrainingSession(
            new Group(
                "Растяжка",
                Age.CHILD,
                120
            ),
            coach,
            DayOfWeek.FRIDAY,
            new TimeOfDay(11, 45)
        );
        timetable.addNewTrainingSession(firstTrainingSession);
        timetable.addNewTrainingSession(secondTrainingSession);
        Assertions.assertEquals(
            1,
            timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY).size()
        );
    }

    @Test
    void testGetTrainingSessionsForNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> timetable.getTrainingSessionsForDay(null));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "1;13;0",
        "3;12;30",
        "4;17;45",
        "7;10;30"
    }, delimiter = ';')
    void testGetTrainingSessionsForDaySingleSession(int dayOfWeek, int hours, int minutes) {

        timetable.addNewTrainingSession(
            TestUtils.createTrainingSession(DayOfWeek.of(dayOfWeek), new TimeOfDay(hours, minutes))
        );

        //Проверить, что за выбранный день вернулось одно занятие
        Assertions.assertEquals(1L, getCount(DayOfWeek.of(dayOfWeek)));

        //Проверить, что в остальные дни не вернулось занятий
        IntStream.range(1, 8)
            .filter(value -> value != dayOfWeek)
            .forEach(value ->
                Assertions.assertEquals(0, getCount(DayOfWeek.of(value)))
            );

    }

    private long getCount(DayOfWeek dayOfWeek) {
        return timetable.getTrainingSessionsForDay(dayOfWeek).values().stream()
            .flatMap(Collection::stream)
            .count();
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {

        timetable.addNewTrainingSession(
            TestUtils.createTrainingSession(DayOfWeek.THURSDAY, new TimeOfDay(20, 0))
        );
        timetable.addNewTrainingSession(
            TestUtils.createTrainingSession(DayOfWeek.MONDAY, new TimeOfDay(13, 0))
        );
        timetable.addNewTrainingSession(
            TestUtils.createTrainingSession(DayOfWeek.THURSDAY, new TimeOfDay(13, 0))
        );timetable.addNewTrainingSession(
            TestUtils.createTrainingSession(DayOfWeek.SATURDAY, new TimeOfDay(10, 0))
        );

        // Проверить, что за понедельник вернулось одно занятие
        Assertions.assertEquals(1, getCount(DayOfWeek.MONDAY));
        // Проверить, что за четверг вернулось два занятия в правильном порядке: сначала в 13:00, потом в 20:00
        Assertions.assertEquals(2, getCount(DayOfWeek.THURSDAY));
        Assertions.assertEquals(
            new TimeOfDay(13, 0),
            ((TreeMap<TimeOfDay, Set<TrainingSession>>) timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY))
                .navigableKeySet().getFirst()
        );
        // Проверить, что за вторник не вернулось занятий
        Assertions.assertEquals(0, getCount(DayOfWeek.TUESDAY));
    }

    @Test
    void testGetTrainingSessionsForDayAndTimeForNull() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> timetable.getTrainingSessionsForDayAndTime(null, new TimeOfDay(12, 0))
        );
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, null)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
        "1;13;0",
        "2;12;30",
        "5;17;45"
    }, delimiter = ';')
    void testGetTrainingSessionsForDayAndTime(int dayOfWeek, int hours, int minutes) {
        timetable.addNewTrainingSession(
            TestUtils.createTrainingSession(DayOfWeek.of(dayOfWeek), new TimeOfDay(hours, minutes))
        );

        //Проверить, что в указанные день и время вернулось одно занятие
        Assertions.assertEquals(
            1,
            timetable.getTrainingSessionsForDayAndTime(DayOfWeek.of(dayOfWeek), new TimeOfDay(hours, minutes)).size()
        );
        //Проверить, что в указанный день в другое время не вернулось занятий
        Assertions.assertEquals(
            0,
            timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.of(dayOfWeek),
                new TimeOfDay(hours + 1, minutes)
            ).size()
        );
    }

    @Test
    void testGetTrainingSessionsForDayAndTimeWithSameTime() {
        TimeOfDay timeOfDay = new TimeOfDay(12, 0);
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.MONDAY, timeOfDay, new Coach("Петров", "Василий", "Иванович"))
        );
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.MONDAY, timeOfDay, new Coach("Иванов", "Василий", "Иванович"))
        );

        //Проверить, что вернулось два занятие
        Assertions.assertEquals(
            2,
            timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(12, 0)).size()
        );
    }

    @Test
    void testGetCountByCoachesForOneCoach() {
        Coach coach = new Coach("Иванов", "Василий", "Иванович");
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.MONDAY, new TimeOfDay(12, 30), coach)
        );
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.TUESDAY, new TimeOfDay(13, 0), coach)
        );
        Assertions.assertTrue(timetable.getCountByCoaches().contains(new CounterOfTrainings(coach, 2)));
    }

    @Test
    void testGetCountByCoachesSort() {
        Coach coach1 = new Coach("Иванов", "Василий", "Иванович");
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.MONDAY, new TimeOfDay(12, 30), coach1)
        );
        Coach coach2 = new Coach("Васильев", "Василий", "Иванович");
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.MONDAY, new TimeOfDay(12, 30), coach2)
        );
        timetable.addNewTrainingSession(TestUtils.createTrainingSession(
            DayOfWeek.TUESDAY, new TimeOfDay(14, 0), coach2)
        );
        Assertions.assertEquals(
            2,
            ((TreeSet<CounterOfTrainings>) timetable.getCountByCoaches()).first().count()
        );
    }

    private void testGetCountByCoaches() {
        Assertions.assertDoesNotThrow(() -> timetable.getCountByCoaches());
    }

}
