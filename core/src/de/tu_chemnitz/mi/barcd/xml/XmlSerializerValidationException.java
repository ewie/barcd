package de.tu_chemnitz.mi.barcd.xml;

import java.util.Collections;
import java.util.List;

/**
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class XmlSerializerValidationException extends XmlSerializerException {
    private static final long serialVersionUID = 1L;

    private List<XmlSerializerValidationError> validationErrors;

    public XmlSerializerValidationException(List<XmlSerializerValidationError> validationErrors, Throwable cause) {
        super(cause);
        this.validationErrors = validationErrors;
    }

    public XmlSerializerValidationException(List<XmlSerializerValidationError> validationErrors) {
        this(validationErrors, null);
    }

    public List<XmlSerializerValidationError> getValidationErrors() {
        if (validationErrors == null) {
            return null;
        }
        return Collections.unmodifiableList(validationErrors);
    }
}
