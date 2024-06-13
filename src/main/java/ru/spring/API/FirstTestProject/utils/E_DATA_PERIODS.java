package ru.spring.API.FirstTestProject.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum E_DATA_PERIODS {
    DAY((int) 0), WEEK((int) 6), MONTH((int) 29);

    final int value;
    private static final Map<String, Integer> stringToValueMap = new HashMap<>();
    private static final Map<Integer, E_DATA_PERIODS> dataPeriodsHashMap = new HashMap<>();

    static {
        for (E_DATA_PERIODS eDataPeriods : values()) {
            stringToValueMap.put(eDataPeriods.name(), eDataPeriods.value);
            dataPeriodsHashMap.put(eDataPeriods.value, eDataPeriods);
        }
    }

    E_DATA_PERIODS(final int value) {
        this.value = value;
    }

    public static int getValueFromString(String str) throws IncorrectDateException {
        Integer value = stringToValueMap.get(str);
        if (value == null) {
            throw new IncorrectDateException("Invalid date string: " + str);
        }
        return value;
    }

    public static E_DATA_PERIODS valueOf(int value) {
        return dataPeriodsHashMap.get(value);
    }
}
