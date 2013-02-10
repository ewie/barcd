package de.tu_chemnitz.mi.barcd;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Provides serialization and unserialization of business models.
 * 
 * @param <T> the model class
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public interface Serializer<T> {
    public void serialize(T model, OutputStream out) throws SerializerException;
    
    public void serialize(T model, Writer out) throws SerializerException;
    
    public T unserialize(InputStream in) throws SerializerException;
    
    public T unserialize(Reader in) throws SerializerException;
}
