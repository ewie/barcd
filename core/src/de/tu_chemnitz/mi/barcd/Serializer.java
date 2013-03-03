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
    /**
     * Serialize a model to a byte stream.
     *
     * @param model the model to serialize
     * @param out the output byte stream
     *
     * @throws SerializerException if the serialization fails
     */
    public void serialize(T model, OutputStream out) throws SerializerException;

    /**
     * Serialize a model to a character stream.
     *
     * @param model the model to serialize
     * @param out the writer on a character stream
     *
     * @throws SerializerException if the serialization fails
     */
    public void serialize(T model, Writer out) throws SerializerException;

    /**
     * Unserialize a model from a byte stream.
     *
     * @param in the input byte stream
     *
     * @return the unserialized model
     *
     * @throws SerializerException if the unserialization fails
     */
    public T unserialize(InputStream in) throws SerializerException;

    /**
     * Unserialize a model from a byte stream.
     *
     * @param in the reader on a character stream
     *
     * @return the unserialized model
     *
     * @throws SerializerException if the unserialization fails
     */
    public T unserialize(Reader in) throws SerializerException;
}
