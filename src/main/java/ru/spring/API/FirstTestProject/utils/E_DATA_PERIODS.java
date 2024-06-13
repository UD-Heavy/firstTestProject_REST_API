package ru.spring.API.FirstTestProject.utils;

import java.util.HashMap;
import java.util.Map;

public enum E_DATA_PERIODS {
    // enum определяет периоды данных, такие как день, неделя и месяц.
    // Каждый период имеет свое числовое значение.
    DAY((int) 0), WEEK((int) 6), MONTH((int) 29);

    final int value; // Это поле хранит числовое значение для каждого периода данных

    // Мапа, которая сопоставляет строковое представление периода данных с его числовым значением.
    private static final Map<String, Integer> stringToValueMap = new HashMap<>();

    // Мапа, которая сопоставляет числовое значение периода данных с его enum-константой.
    private static final Map<Integer, E_DATA_PERIODS> dataPeriodsHashMap = new HashMap<>();

    // Этот блок статического инициализатора заполняет карты stringToValueMap и dataPeriodsHashMap
    // для всех констант enum.
    static {
        for (E_DATA_PERIODS eDataPeriods : values()) {
            stringToValueMap.put(eDataPeriods.name(), eDataPeriods.value);
            dataPeriodsHashMap.put(eDataPeriods.value, eDataPeriods);
        }
    }

    // Это конструктор, который инициализирует числовое значение периода данных.
    E_DATA_PERIODS(final int value) {
        this.value = value;
    }

    // Этот метод принимает строковое представление
    // периода данных и возвращает его числовое значение.
    // Если переданная строка не соответствует ни одному из периодов данных,
    // будет выброшено исключение IncorrectDateException.
    public static int getValueFromString(String str) throws IncorrectDateException {
        Integer value = stringToValueMap.get(str);
        if (value == null) {
            throw new IncorrectDateException("Invalid date string: " + str);
        }
        return value;
    }

    // Этот метод принимает числовое значение периода данных и возвращает соответствующую ему enum-константу.
    public static E_DATA_PERIODS valueOf(int value) {
        return dataPeriodsHashMap.get(value);
    }
}
