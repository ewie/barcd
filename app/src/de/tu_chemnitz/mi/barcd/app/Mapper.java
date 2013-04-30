package de.tu_chemnitz.mi.barcd.app;

/**
 * Maps objects from a domain to objects of a codomain.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Mapper<Domain, Codomain> {
    public Codomain map(Domain value) throws MapperException;
}
