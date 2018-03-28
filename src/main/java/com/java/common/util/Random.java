package com.java.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Random {
    private static final java.util.Random rand = new java.util.Random();

    private static final Character[] CHARS = IntStream.range(48, 123)
        .filter(Character::isLetterOrDigit)
        .mapToObj(i -> (char)i)
        .toArray(Character[]::new);

    public static int anyInt() {
        return rand.nextInt();
    }

    /**
     * Retourne un nombre entier aléatoire entre 0 et X
     * @param max Nombre entier maximum <b>exclusif</b>
     * @return
     */
    public static int anyInt(int max) {
        return rand.nextInt(max);
    }

    /**
     * Retourne un nombre entier aléatoire entre un minimum et un maximum
     * @param min Nombre entier minimum, <b>inclusif</b>
     * @param max Nombre entier maximum, <b>exclusif</b>
     * @return
     */
    public static int anyInt(int min, int max) {
        return rand.ints(min, max).findAny().getAsInt();
    }

    public static long anyLong() {
        return rand.nextLong();
    }

    public static float anyFloat() {
        return rand.nextFloat();
    }

    public static double anyDouble() {
        return rand.nextDouble();
    }

    public static boolean anyBoolean() {
        return rand.nextBoolean();
    }

    public static byte[] anyBytes(int length) {
        byte[] array = new byte[length];
        rand.nextBytes(array);
        return array;
    }

    public static String anyString() {
        return anyString(10);
    }

    public static String anyString(int length) {
        return IntStream.range(0,length)
            .mapToObj(i -> CHARS[anyInt(CHARS.length)])
            .map(c -> c.toString())
            .collect(Collectors.joining());
    }

    public static Date anyDate() {
        final int year = rand.nextInt(200) + 1900;
        final int month = rand.nextInt(12) + 1;
        int day = rand.nextInt(30) + 1;
        // seulement 28 jours max en février sauf année bisextile
        if (month  == 2 && day > 28)
            day = IsoChronology.INSTANCE.isLeapYear(year)? 29: 28;

        final LocalDate ld = LocalDate.of(year, month, day);
        final Instant i = ld.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Date.from(i);
    }

    public static String anyDateString() {
		return anyDateString(DateTimeFormatter.ISO_DATE);
    }

    public static String anyDateString(DateTimeFormatter formatter) {
    	Instant instant = anyDate().toInstant();
		LocalDate ld = instant.atZone(ZoneId.systemDefault()).toLocalDate();
		return ld.format(formatter);
    }

    public static <T> List<T> anyList(int length, IntFunction<T> transformer) {
        return IntStream.range(0, length)
            .mapToObj(transformer)
            .collect(Collectors.toList());
    }

    public static <T> List<T> anyList(int length) {
        return anyList(length, i -> null);
    }
}
