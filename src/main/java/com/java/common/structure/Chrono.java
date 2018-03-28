package com.java.common.structure;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Chrono {
    private static final SecureRandom random;
    private static final Map<String, Long> cache;

    static {
        random = new SecureRandom();
        cache = new ConcurrentHashMap<>();
    }

    public static String nextId() {
        return new BigInteger(130, random).toString(32);
    }

    /**
     * Démarre un chrono
     * @param uid Identifiant associé au chrono
     */
    public static void start(String uid) {
        cache.put(uid, System.currentTimeMillis());
    }

    /**
     * Démarre un chrono et retourne un identifiant unique associé (auto-généré)
     * @return Identifiant unique du chrono
     */
    public static String start() {
        final String uid = nextId();
        start(uid);
        return uid;
    }

    /**
     * Démarre un chrono, retourne un identifiant unique associé (auto-généré) et affiche un message
     * @param format Chaine de caractère qui sera afficher dans les log @see String.format
     * @param args Arguments du paramétre précédent @see String.format
     * @return Identifiant unique du chrono
     */
    public static String start(String format, Object...args) {
        return start(System.out::println, format, args);
    }

    /**
     * Démarre un chrono, retourne un identifiant unique associé (auto-généré) et affiche un message via une lambda
     * @param fn Lamba d'affichage du message (System.out::println par exemple)
     * @param format Chaine de caractère qui sera afficher dans les log @see String.format
     * @param args Arguments du paramétre précédent @see String.format
     * @return Identifiant unique du chrono
     */
    public static String start(Consumer<String> fn, String format, Object...args) {
        fn.accept(String.format(format, args));
        return start();
    }

    /**
     * Redémarre le chrono associé à l'identifiant unique et affiche un message via une lambda
     * @param uid Identifiant unique du chrono
     * @param fn Lamba d'affichage du message (System.out::println par exemple)
     * @param format Chaine de caractère qui sera afficher dans les log @see String.format
     * @param args Arguments du paramétre précédent @see String.format
     */
    public static void start(String uid, Consumer<String> fn, String format, Object...args) {
        fn.accept(String.format(format, args));
        start(uid);
    }

    /**
     * Arrete le chrono associé à l'identifiant et retourne la différence en milliseconde entre le début et l'arret du chrono
     * @param uid Identifiant unique associé au chrono
     * @return Différence en milliseconde entre le début et l'arret du chrono
     */
    public static long stop(String uid) {
        Long timeStart = cache.remove(uid);
        return timeStart != null? System.currentTimeMillis() - timeStart: 0;
    }

    /**
     * Arrete le chrono associé à l'identifiant et affiche la différence entre le début et l'arret du chrono dans un format lisible
     * @param uid Identifiant unique associé au chrono
     * @param format Chaine de caractère qui sera afficher dans les log @see String.format. l'utilisation du symbole "{}" est remplacé par la différence entre le début et l'arret du chrono dans un format lisible
     * @param args Arguments du paramétre précédent @see String.format
     */
    public static void stop(String uid, String format, Object...args) {
        stop(uid, System.out::println, format, args);
    }

    /**
     * Arrete le chrono associé à l'identifiant, affiche la différence entre le début et l'arret du chrono dans un format lisible via une lambda
     * @param uid Identifiant unique associé au chrono
     * @fn Lamba d'affichage du message (System.out::println par exemple)
     * @param format Chaine de caractère qui sera afficher dans les log @see String.format. l'utilisation du symbole "{}" est remplacé par la différence entre le début et l'arret du chrono dans un format lisible
     * @param args Arguments du paramétre précédent @see String.format
     */
    public static void stop(String uid, Consumer<String> fn, String format, Object...args) {
        String chrono = stopToHumanFormat(uid);
        String msg = String.format(format.replaceAll("\\{\\}", chrono), args);
        fn.accept(msg);
    }

    /**
     * Arrete le chrono associé à l'identifiant et retourne la différence entre le début et l'arret du chrono dans un format lisible
     * @param uid Identifiant unique associé au chrono
     * @return Différence entre le début et l'arret du chrono dans un format lisible
     */
    public static String stopToHumanFormat(String uid) {
        return humanFormat(stop(uid));
    }

    public static String displayToHumanFormat(String uid) {
        Long timeStart = cache.get(uid);
        Long diff = timeStart != null? System.currentTimeMillis() - timeStart: 0;
        return humanFormat(diff);
    }

    /**
     * Retourne la valeur humainement lisible d'une valeur en millisecondes
     * @param difference Millisecondes entre deux dates
     * @return Milliseconde formatée de manière lisible
     */
    public static String humanFormat(final Long difference) {
        Long actual = difference;
        StringBuilder sb = new StringBuilder();
        // hour
        long hours = actual / 1000 / 60 / 60;
        if (hours > 0) {
            actual -= (60 * 60 * 1000 * hours);
            sb.append(hours +"h. ");
        }
        // minutes
        long min = actual / 1000 / 60;
        if (min > 0) {
            actual -= (60 * 1000 * min);
            sb.append(min +"m. ");
        }
        // seconds
        long second = actual / 1000;
        if (second > 0) {
            actual -= (1000 * second);
            sb.append(second +"s. ");
        }
        // milliseconds
        sb.append(actual +"ms.");

        return sb.toString();
    }
}
