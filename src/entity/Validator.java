package entity;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.regex.*;

public final class Validator {
    private Validator() {}
    private static final EnumMap<Data.Type, String> patterns;
    private static final EnumMap<Data.Type, String> messages;

    static {
        patterns = new EnumMap<>(Data.Type.class);
        patterns.put(Data.Type.Group, "\\d+");
        patterns.put(Data.Type.Name, "[а-яА-Я]+\\s[а-яА-Я]\\.\\s?[а-яА-Я]\\.");
        patterns.put(Data.Type.Faculty, "[а-яА-Я\\s]+");
        patterns.put(Data.Type.Discipline, "[а-яА-Я\\s]+");
        patterns.put(Data.Type.Year, "[12][0-9]{3}/[12][0-9]{3}");
        patterns.put(Data.Type.Hours, "([12]?[0-9]{2}$)|([12]?[0-9]{2}\\s?/\\s?[1-6]\\s?з\\.е\\.)");
        patterns.put(Data.Type.Date, "[0-3][0-9]\\.[01][0-9]\\.[12][0-9]{3}");
        patterns.put(Data.Type.SerialNumber, "\\d+");
        patterns.put(Data.Type.Mark, "[0-9]|10");

        messages = new EnumMap<>(Data.Type.class);
        messages.put(Data.Type.Group, "Номер группы не соответствует формату\n" + "Пример: 10702221");
        messages.put(Data.Type.Name, "ФИО не соответствует формату\n" + "Пример: Авсиевич А. М.");
        messages.put(Data.Type.Faculty, "Факультет не соответствует формату\n" + "Пример: Автотракторный Факультет");
        messages.put(Data.Type.Discipline, "Дисциплина не соответствует формату\n" + "Пример: Психология труда");
        messages.put(Data.Type.Year, "Год не соответствует формату\n" + "Пример: 2023/2024");
        messages.put(Data.Type.Hours, "Число не соответствует формату\n" + "Пример: 108 или 108 / 3 з.е.");
        messages.put(Data.Type.Date, "Дата не соответствует формату\n" + "Пример: 01.12.2023");
        messages.put(Data.Type.SerialNumber, "Номер не соответствует формату");
        messages.put(Data.Type.Mark, "Оценка не соответствует формату");
    }

    public static void showValidationError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void validate(Object obj, Data.Type type) throws IllegalArgumentException {
        if (obj == null) return;
        if (obj instanceof String str) {
            if (str.isEmpty()) return;
            Pattern pattern = Pattern.compile(patterns.get(type));
            Matcher matcher = pattern.matcher(str);
            if (!matcher.matches())
                throw new IllegalArgumentException(messages.get(type));
        } else {
            throw new IllegalArgumentException("Obj is not a string");
        }
    }
}
