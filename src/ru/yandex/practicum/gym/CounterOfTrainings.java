package ru.yandex.practicum.gym;

/**
 * Тренер и его кол-во тренировок.
 *
 * @param coach Тренер.
 * @param count Кол-во тренировок в неделю.
 */
public record CounterOfTrainings(Coach coach, int count) {

}
