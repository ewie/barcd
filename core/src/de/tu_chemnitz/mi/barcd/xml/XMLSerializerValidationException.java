package de.tu_chemnitz.mi.barcd.xml;

import java.util.Collections;
import java.util.List;

public class XMLSerializerValidationException extends XMLSerializerException {
    private static final long serialVersionUID = 1L;

    private List<XMLSerializerValidationError> validationErrors;
    
    public XMLSerializerValidationException(List<XMLSerializerValidationError> validationErrors) {
        this(validationErrors, null);
    }

    public XMLSerializerValidationException(List<XMLSerializerValidationError> validationErrors, Throwable cause) {
        super(cause);
        this.validationErrors = validationErrors;
    }
    
    public List<XMLSerializerValidationError> getValidationErrors() {
        if (validationErrors == null) {
            return null;
        }
        return Collections.unmodifiableList(validationErrors);
    }
}
