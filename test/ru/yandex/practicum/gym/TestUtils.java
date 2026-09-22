package ru.yandex.practicum.gym;

import java.time.DayOfWeek;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {

    private static final List<String> GROUP_TITLES = List.of("Акробатика", "Стэп", "Растяжка");
    private static final List<String> COACH_SURNAMES = List.of("Васильев", "Петров", "Иванов");
    private static final List<String> COACH_NAMES = List.of("Василий", "Петр", "Иван");
    private static final List<String> COACH_PATRONYMICS = List.of("Васильев", "Петров", "Иванов");

    /**
     * Метод для получения случайного значения из enum.
     *
     * @param enumClass тип enum
     * @return случайное значения из values
     */
    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        int randomIndex = ThreadLocalRandom.current().nextInt(values.length);
        return values[randomIndex];
    }

    public static TrainingSession createTrainingSession(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        Coach coach = new Coach(
            COACH_SURNAMES.get(ThreadLocalRandom.current().nextInt(COACH_SURNAMES.size())),
            COACH_NAMES.get(ThreadLocalRandom.current().nextInt(COACH_NAMES.size())),
            COACH_PATRONYMICS.get(ThreadLocalRandom.current().nextInt(COACH_PATRONYMICS.size()))
        );
        return new TrainingSession(getGroup(), coach, dayOfWeek, timeOfDay);
    }

    private static Group getGroup() {
        return new Group(
            GROUP_TITLES.get(ThreadLocalRandom.current().nextInt(GROUP_TITLES.size())),
            getRandomEnumValue(Age.class),
            ThreadLocalRandom.current().nextInt(60) + 60
        );
    }

    public static TrainingSession createTrainingSession(DayOfWeek dayOfWeek, TimeOfDay timeOfDay, Coach coach) {
        return new TrainingSession(getGroup(), coach, dayOfWeek, timeOfDay);
    }

}
