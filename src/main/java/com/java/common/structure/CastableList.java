package com.java.common.structure;

import java.util.Optional;

public interface CastableList<E> {
    /**
     * Retourne la potentielle valeur
     * @param index Index de la valeur
     * @return Potentielle valeur
     */
    Optional<E> toOption(int index);
    /**
     * Retourne la potentielle valeur casté
     * @param index Index de la valeur
     * @param type Type de sortie
     * @param <T>
     * @return Potentielle valeur castée
     */
    <T> Optional<T> toOption(int index, Class<T> type);

    /**
     * Retourne la valeur castée
     * @param index Index de la valeur
     * @param type Type de sortie
     * @param <T>
     * @return Valeur castée
     */
    <T> T get(int index, Class<T> type);

    /**
     * Verifie s'il existe une prochaine valeur
     * @return
     */
    boolean hasNext();

    /**
     * Vérifie s'il existe une valeur précédente
     * @return
     */
    boolean hasPrevious();

    /**
     * Récupére la prochaine valeur en incrémentant le curseur interne
     * @return
     */
    E next();

    /**
     * Récupére la prochaine valeur casté dans le type désiré, en incrémentant le curseur interne
     * @param type type de sortie désiré
     * @param <T>
     * @return
     */
    <T> T next(Class<T> type);

    /**
     * Récupére la valeur précédente en décrémentant le curseur interne
     * @return
     */
    E previous();

    /**
     * Récupére la valeur précédente casté dans le type désiré, en décrémentant le curseur interne
     * @param type type de sortie désiré
     * @param <T>
     * @return
     */
    <T> T previous(Class<T> type);

    /**
     * Retourne la valeur courante, sans déplacé le curseur interne
     * @return
     */
    E get();

    /**
     * Retourne la valeur courante casté dans le type désiré, sans déplacé le curseur interne
     * @param type type de sortie désiré
     * @param <T>
     * @return
     */
    <T> T get(Class<T> type);

    /**
     * Retourne le premier élément de la liste sans déplacé le curseur interne
     * @return
     */
    E first();

    /***
     * Retourne le dernier element de la liste sans déplacé le curseur interne
     * @return
     */
    E last();
}
