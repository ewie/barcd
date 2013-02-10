package de.tu_chemnitz.mi.barcd.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.tu_chemnitz.mi.barcd.Serializer;

/**
 * Provides serialization and unserialization of business models using JAXB.
 * 
 * @param <T> the model class
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public abstract class XMLSerializer<T extends Object> implements Serializer<T> {
    private JAXBContext context;
    
    private boolean validate;
    
    private Schema schema;
    
    private URL urlContext;

    private URL schemaLocation;

    private boolean includeSchemaLocation;

    private boolean includeXmlDeclaration;

    private boolean pretty;

    /**
     * @param contextPath the context path used to initialize the JAXB context
     * 
     * @throws XMLSerializerException
     */
    public XMLSerializer(String contextPath) {
        try {
            context = JAXBContext.newInstance(contextPath);
        } catch (JAXBException ex) {
            throw new RuntimeException("could not create JABX context", ex);
        }
    }
    
    /**
     * Restore a model from an unmarshalled XML document.
     * 
     * @param e the root element of the unmarshalled XML document
     * 
     * @return the restored model
     * 
     * @throws XMLSerializerException
     */
    protected abstract T restoreModel(JAXBElement<?> e)
        throws XMLSerializerException;
    
    /**
     * Create a root element from the model to be serialized.
     * 
     * @param model the model to be serialized
     * 
     * @return the root element to be marshalled
     * 
     * @throws XMLSerializerException
     */
    protected abstract JAXBElement<?> createRootElement(T model)
        throws XMLSerializerException;

    /**
     * @param model the model to be serialized
     * 
     * @param out the stream where to write the data
     * 
     * @throws XMLSerializerException
     */
    @Override
    public void serialize(T model, OutputStream out)
        throws XMLSerializerException
    {
        marshal(model, out);
    }
    
    /**
     * @param model the model to be serialized
     * @param out the writer used to write the XML representation
     * 
     * @throws XMLSerializerException
     */
    @Override
    public void serialize(T model, Writer out)
        throws XMLSerializerException
    {
        marshal(model, out);
    }

    /**
     * Append the XML representation of a model to a DOM node.
     * 
     * @param model the model to be serialized
     * @param node the node to hold the model's XML representation
     * 
     * @throws XMLSerializerException
     */
    public void appendTo(T model, Node node)
        throws XMLSerializerException
    {
        marshal(model, node);
    }
    
    /**
     * @param in the stream from which to read the XML data
     * 
     * @return the restored model
     * 
     * @throws XMLSerializerException
     */
    @Override
    public T unserialize(InputStream in)
        throws XMLSerializerException
    {
        return unmarshal(in);
    }

    /**
     * @param in the reader from which to read the XML data
     * 
     * @return the restored model
     * 
     * @throws XMLSerializerException
     */
    @Override
    public T unserialize(Reader in)
        throws XMLSerializerException
    {
        return unmarshal(in);
    }
    
    /**
     * @param node the DOM node whose child element to use as model data
     * 
     * @return the restored model
     * 
     * @throws XMLSerializerException
     */
    public T extractFrom(Node node)
        throws XMLSerializerException
    {
        return unmarshal(node);
    }
    
    /**
     * Set the schema location and use the schema.
     * 
     * @param url the schema location
     * 
     * @throws XMLSerializerException if the schema could no be loaded
     */
    public void setSchemaLocation(URL url)
        throws XMLSerializerException
    {
        setSchemaLocation(url, true);
    }
    
    /**
     * @param url the schema location
     * @param useSchema true if to use the schema
     * 
     * @throws XMLSerializerException if the schema could not be loaded
     */
    public void setSchemaLocation(URL url, boolean useSchema)
        throws XMLSerializerException
    {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema s;
        try {
            s = sf.newSchema(url);
        } catch (SAXException ex) {
            throw new XMLSerializerException("could not open XML schema", ex);
        }
        schemaLocation = url;
        if (useSchema) {
            setSchema(s);
        }
    }
    
    /**
     * @return the schema location
     */
    public URL getSchemaLocation() {
        return schemaLocation;
    }
    
    /**
     * @param include true if to include the schema location
     * 
     * @see xsi:schemaLocation
     */
    public void setIncludeSchemaLocation(boolean include) {
        this.includeSchemaLocation = include;
    }
    
    /**
     * @return true if the schema location is to be included
     */
    public boolean getIncludeSchemaLocation() {
        return includeSchemaLocation;
    }
    
    /**
     * @param include true if to include the XML declaration
     */
    public void setIncludeXMLDeclaration(boolean include) {
        this.includeXmlDeclaration = include;
    }
    
    /**
     * @return true if the XML declaration is to be included
     */
    public boolean getIncludeXMLDeclaration() {
        return includeXmlDeclaration;
    }

    /**
     * Set the URL context to resolve relative URLs.
     * 
     * @param context the URL context
     */
    public void setURLContext(URL context) {
        this.urlContext = context;
    }
    
    /**
     * @return the URL context
     */
    public URL getURLContext() {
        return urlContext;
    }
    
    /**
     * @param schema the schema to be used for validation
     */
    public void setSchema(Schema schema) {
        this.schema = schema;
    }
    
    /**
     * @return the schema used for validation
     */
    public Schema getSchema() {
        return schema;
    }
    
    /**
     * @return true if to validate the XML on marshalling and unmarshalling
     */
    public boolean getValidation() {
        return validate;
    }
    
    /**
     * Enable or disable schema validation.
     * 
     * @param validate true to enable, false to disable validation
     * 
     * @throws XMLSerializerException if no schema has been specified
     */
    public void setValidation(boolean validate)
        throws XMLSerializerException
    {
        if (validate && schema == null) {
            throw new XMLSerializerException("no XML schema loaded");
        }
        this.validate = validate;
    }
    
    /**
     * Enable or disable pretty printing. Applies only to a serialized
     * representation.
     * 
     * @param pretty true if to pretty print the XML representation
     */
    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }
    
    /**
     * @return true if to pretty print the XML representation
     */
    public boolean getPretty() {
        return pretty;
    }
    
    protected URL resolveUrl(String rawUrl)
        throws XMLSerializerException
    {
        URL url;
        try {
            url = new URL(rawUrl);
        } catch (MalformedURLException ex) {
            throw new XMLSerializerException("invalid URL", ex);
        }
        return resolveUrl(url);
    }
    
    protected URL resolveUrl(URL url)
        throws XMLSerializerException
    {
        if (context == null) {
            return url;
        } else {
            URI uri;
            try {
                uri = url.toURI();
            } catch (URISyntaxException ex) {
                throw new XMLSerializerException("invalid URL", ex);
            }
            URI context;
            try {
                context = urlContext.toURI();
            } catch (URISyntaxException ex) {
                throw new XMLSerializerException("invalid URL", ex);
            }
            URI ruri = context.resolve(uri);
            URL rurl;
            try {
                rurl = ruri.toURL();
            } catch (MalformedURLException ex) {
                throw new XMLSerializerException("invalid URL", ex);
            }
            return rurl;
        }
    }
    
    protected URL relativizeUrl(URL url)
        throws XMLSerializerException
    {
        if (urlContext == null) {
            return url;
        } else {
            URI context;
            URI uri;
            try {
                uri = url.toURI();
            } catch (URISyntaxException ex) {
                throw new XMLSerializerException("invalid URL", ex);
            }
            try {
                context = urlContext.toURI();
            } catch (URISyntaxException ex) {
                throw new XMLSerializerException("invalid URL context", ex);
            }
            URI ruri = context.relativize(uri);
            URL rurl;
            try {
                rurl = ruri.toURL();
            } catch (MalformedURLException ex) {
                throw new XMLSerializerException("invalid URL", ex);
            }
            return rurl;
        }
    }
    
    /**
     * Unmarshall a model from an XML representation.
     * 
     * @param source an object providing XML data
     * 
     * @return the unmarshalled model
     * 
     * @throws XMLSerializerException
     */
    private T unmarshal(Object source)
        throws XMLSerializerException
    {
        Unmarshaller u = createUnmarshaller();
        ValidationEventCollector v;
        
        try {
            v = (ValidationEventCollector) u.getEventHandler();
        } catch (JAXBException ex) {
            throw new RuntimeException("could not retrieve validation event collector of the JAXB unmarshaller", ex);
        }

        JAXBElement<?> e;
        
        try {
            Object o;
            // XXX Not very pretty but the only way without resorting to code
            // duplication.
            if (source instanceof InputStream) {
                o = u.unmarshal((InputStream) source);
            } else if (source instanceof Reader) {
                o = u.unmarshal((Reader) source);
            } else if (source instanceof Node) {
                o = u.unmarshal((Node) source);
            } else {
                throw new RuntimeException();
            }
            e = (JAXBElement<?>) o;
        } catch (UnmarshalException ex) {
            throw new XMLSerializerValidationException(translateValidationErrors(v), ex);
        } catch (JAXBException ex) {
            throw new XMLSerializerException(ex);
        }
        
        return restoreModel(e);
    }
    
    /**
     * Marshall a model to an XML representation.
     * 
     * @param model the model to marshall
     * @param target an object receiving the XML data
     * 
     * @throws XMLSerializerException
     */
    private void marshal(T model, Object target)
        throws XMLSerializerException
    {
        JAXBElement<?> e = createRootElement(model);
        Marshaller m = createMarshaller(e);
        ValidationEventCollector v = null;
        
        if (validate) {
            try {
                v = (ValidationEventCollector) m.getEventHandler();
            } catch (JAXBException ex) {
                throw new RuntimeException("could not retrieve validation event collector of the JAXB marshaller", ex);
            }
        }
        
        try {
            // XXX Not very pretty but the only way without resorting to code
            // duplication.
            if (target instanceof Writer) {
                m.marshal(e, (Writer) target);
            } else if (target instanceof OutputStream) {
                m.marshal(e, (OutputStream) target);
            } else if (target instanceof Node) {
                m.marshal(e, (Node) target);
            } else {
                throw new RuntimeException();
            }
        } catch (MarshalException ex) {
            throw new XMLSerializerValidationException(translateValidationErrors(v), ex);
        } catch (JAXBException ex) {
            throw new XMLSerializerException("unexpected error during marshalling", ex);
        }
    }
    
    /**
     * Create and configure an unmarshaller.
     * 
     * @return an unmarshaller
     * 
     * @throws XMLSerializerException
     */
    private Unmarshaller createUnmarshaller() {
        try {
            Unmarshaller u;
            u = context.createUnmarshaller();
            if (validate) {
                ValidationEventCollector v = new ValidationEventCollector();
                u.setEventHandler(v);
            }
            return u;
        } catch (JAXBException ex) {
            throw new RuntimeException("could not create JAXB unmarshaller", ex);
        }
    }
    
    /**
     * Create and configure an marshaller.
     * 
     * @return an marshaller
     */
    private Marshaller createMarshaller(JAXBElement<?> element) {
        try {
            Marshaller m = context.createMarshaller();
            if (validate) {
                ValidationEventCollector v = new ValidationEventCollector();
                m.setEventHandler(v);
            }
            if (includeSchemaLocation && schemaLocation != null) {
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                    element.getName().getNamespaceURI() + " " + schemaLocation.toString());
            }
            if (includeXmlDeclaration) {
                m.setProperty(Marshaller.JAXB_FRAGMENT, !includeXmlDeclaration);
            }
            if (pretty) {
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, pretty);
            }
            return m;
        } catch (JAXBException ex) {
            throw new RuntimeException("could not create JAXB marshaller", ex);
        }
    }
    
    /**
     * Translate the validation errors reported by an marshaller/unmarshaller.
     * 
     * @param validation provides the validation events
     * 
     * @return the validation errors
     */
    private List<XMLSerializerValidationError> translateValidationErrors(ValidationEventCollector validation)
    {
        ValidationEvent[] events = validation.getEvents();
        XMLSerializerValidationError[] errors = new XMLSerializerValidationError[events.length];
        for (int i = 0; i < events.length; ++i) {
            ValidationEventLocator loc = events[i].getLocator();
            XMLSerializerValidationError e = new XMLSerializerValidationError(
                events[i].getMessage(), loc.getLineNumber(), loc.getColumnNumber());
            errors[i] = e;
        }
        return Arrays.asList(errors);
    }
}
