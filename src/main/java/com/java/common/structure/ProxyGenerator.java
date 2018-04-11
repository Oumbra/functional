package com.java.common.structure;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ProxyGenerator {

    private HashMap<String, Object> properties;

    private ProxyGenerator(Map<String, Object> properties) {
        this.properties = new HashMap<>(properties);
    }

    public InvocationHandler handler() {
        return (Object o, Method method, Object[] objects) -> {
            if (StringUtils.startsWith(method.getName(), "get")) {
                String attributeName = WordUtils.uncapitalize(method.getName().substring(3));
                Object value = properties.getOrDefault(attributeName, null);
                return cast(value, method.getReturnType());
            }
            throw new UnsupportedOperationException("Uniquement les getter sont pris en compte! ["+ method.getName() +"]");
        };
    }

    /**
     * Créé un objet proxy à partir d'un tableau associatif.
     * Seul les getter seront pris en compte par l'objet proxy retourné.
     * @param properties Tableau associatif à proxifier
     * @param type Classe ou interface affichée qui fera office de proxy au tableau associatif
     * @param <T>
     * @return Instance de la classe/interface dont les getter seront proxifier pour récupérer les valeurs du tableau associatif
     */
    public static <T> T createProxy(Map<String, Object> properties, Class<T> type) {
//        Assert.notNull(type, "La classe est indispensable pour la création d'un proxy");
        return (T) Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[]{type},
            new ProxyGenerator(properties).handler()
        );
    }

    private <T> T cast(Object source, Class<T> targetType) {
        switch (targetType.getSimpleName()) {
            case "Date": return (T) castToDate(source);
            case "Integer": case "int": return (T) castToInt(source);
            case "Long": case "long": return (T) castToLong(source);
            case "Float": case "float": return (T) castToFloat(source);
            case "Double": case "double": return (T) castToDouble(source);
        }
        // string
        return source != null? (T)source.toString(): null;
    }

    private Date castToDate(Object obj) {
        Date d = obj instanceof Date? (Date) obj:
            obj instanceof Long? new Date((Long) obj):
            obj instanceof String && Pattern.matches("\\d{4}-\\d{2}-\\d{2}", (String) obj)? string2Date((String) obj):
            null;
        if (obj != null && d == null)
            throw new UnsupportedOperationException("Impossible de cast "+ obj.getClass().getSimpleName() +" ("+ obj +") en Date!");
        return d;
    }

    private Integer castToInt(Object obj) {
        Integer i = obj instanceof Number? ((Number) obj).intValue():
            obj instanceof String && Pattern.matches("\\d*", (String) obj)? Integer.parseInt((String) obj):
            null;
        if (obj != null && i == null)
            throw new UnsupportedOperationException("Impossible de cast "+ obj.getClass().getSimpleName() +" ("+ obj +") en Integer!");
        return i;
    }

    private Long castToLong(Object obj) {
        Long l = obj instanceof Number? ((Number) obj).longValue():
            obj instanceof String && Pattern.matches("\\d*", (String) obj)? Long.parseLong((String) obj):
            null;
        if (obj != null && l == null)
            throw new UnsupportedOperationException("Impossible de cast "+ obj.getClass().getSimpleName() +" ("+ obj +") en Long!");
        return l;
    }

    private Float castToFloat(Object obj) {
        Float f = obj instanceof Number? ((Number) obj).floatValue():
            obj instanceof String && Pattern.matches("\\d*", (String) obj)? Float.parseFloat((String) obj):
            null;
        if (obj != null && f == null)
            throw new UnsupportedOperationException("Impossible de cast "+ obj.getClass().getSimpleName() +" ("+ obj +") en Float!");
        return f;
    }

    private Double castToDouble(Object obj) {
        Double d = obj instanceof Number? ((Number) obj).doubleValue():
            obj instanceof String && Pattern.matches("\\d*", (String) obj)? Double.parseDouble((String) obj):
            null;
        if (obj != null && d == null)
            throw new UnsupportedOperationException("Impossible de cast "+ obj.getClass().getSimpleName() +" ("+ obj +") en Double!");
        return d;
    }

    private Date string2Date(String strDate) {
		LocalDate date = LocalDate.parse(strDate, DateTimeFormatter.ISO_DATE);
		Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
    }
}
