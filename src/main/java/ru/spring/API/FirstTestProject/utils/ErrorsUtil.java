package ru.spring.API.FirstTestProject.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ErrorsUtil {
    // Этот статический метод предназначен для возвращения ошибок клиенту, возникших при
    // валидации входных данных.

    // Создаем объект StringBuilder, который будет
    // использоваться для построения строки с ошибками.
    public static void returnErrorsToClient(BindingResult bindingResult) {
        // Создаем объект StringBuilder, который будет использоваться
        // для построения строки с ошибками.
        StringBuilder errorMsg = new StringBuilder();

        // Получаем список ошибок валидации из объекта BindingResult.
        List<FieldError> errors = bindingResult.getFieldErrors();

        // Проходим по списку ошибок и формируем строку с информацией об ошибках.
        for (FieldError error : errors) {
            errorMsg.append(error.getField())
                    .append(" — ").append(error.getDefaultMessage())
                    .append("; ");
        }
        // Бросаем исключение TaskNotCreatedException, которое содержит сформированную
        // строку с информацией об ошибках.
        throw new TaskNotCreatedException(errorMsg.toString());
    }
}
