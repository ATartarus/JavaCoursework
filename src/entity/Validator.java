package entity;

import exceptions.ValidationException;

import java.util.EnumMap;
import java.util.regex.*;

/**
 * Utility class for data validation and formatting.
 */
public final class Validator {
    private Validator() {}
    private static final EnumMap<Data.Type, String> patterns;
    private static final EnumMap<Data.Type, String> messages;

    static {
        patterns = new EnumMap<>(Data.Type.class);
        patterns.put(Data.Type.Group, "\\d+");
        patterns.put(Data.Type.Name, "[а-яА-Я]+\\s[а-яА-Я]((\\.)|(\\.\\s)|(\\s))[а-яА-Я]\\.?");
        patterns.put(Data.Type.Faculty, "[а-яА-Я\\s]+");
        patterns.put(Data.Type.Discipline, "[а-яА-Яa-zA-Z\\s]+");
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

    /**
     * Validates string by the given type.
     * @param str String to validate.
     * @param type Validation type.
     * @throws ValidationException If validation failed.
     */
    public static void validate(String str, Data.Type type) throws ValidationException {
        if (str == null || str.isBlank()) {
            throw new ValidationException("Элемент не может быть пустым");
        }

        Pattern pattern = Pattern.compile(patterns.get(type));
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            throw new ValidationException(messages.get(type));
        }
    }

    /**
     * Formats string by the given type.
     * @param source String to format.
     * @param type Formatting type.
     * @return Formatted string.
     */
    public static String getFormattedString(String source, Data.Type type) {
        if (source == null || type == null) return null;

        StringBuilder result = new StringBuilder();
        int len = source.length();
        if (type == Data.Type.Name) {
            int pivot = source.indexOf(' ') + 1;
            result.append(source.substring(0, 1).toUpperCase())
                    .append(source.substring(1, pivot).toLowerCase())
                    .append(source.substring(pivot, pivot + 1).toUpperCase())
                    .append('.')
                    .append(source.charAt(len - 1) == '.' ?
                            source.substring(len - 2, len - 1).toUpperCase() :
                            source.substring(len - 1).toUpperCase())
                    .append('.');
        }
        else if (type == Data.Type.Faculty || type == Data.Type.Discipline) {
            String[] words = source.split("\\s");
            for (String word : words) {
                if (word.length() == 1) {
                    result.append(word).append(' ');
                } else {
                    result.append(word.substring(0, 1).toUpperCase())
                            .append(word.substring(1).toLowerCase())
                            .append(' ');
                }
            }
            if (result.charAt(result.length() - 1) == ' ') {
                result.deleteCharAt(result.length() - 1);
            }
        }
        else {
            result.append(source);
        }

        return result.toString();
    }
}
