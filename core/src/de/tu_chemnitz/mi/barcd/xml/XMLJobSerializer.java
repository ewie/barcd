package de.tu_chemnitz.mi.barcd.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;

import de.tu_chemnitz.mi.barcd.Job;
import de.tu_chemnitz.mi.barcd.Source;
import de.tu_chemnitz.mi.barcd.source.ImageCollectionSource;
import de.tu_chemnitz.mi.barcd.source.ImageSequenceSource;
import de.tu_chemnitz.mi.barcd.source.ImageServiceSource;
import de.tu_chemnitz.mi.barcd.source.VideoDeviceSource;
import de.tu_chemnitz.mi.barcd.source.VideoStreamSource;
import de.tu_chemnitz.mi.barcd.util.Range;
import de.tu_chemnitz.mi.barcd.util.TemplatedURLSequence;
import de.tu_chemnitz.mi.barcd.xml.binding.ImageSequenceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ImagesElement;
import de.tu_chemnitz.mi.barcd.xml.binding.JobElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ObjectFactory;
import de.tu_chemnitz.mi.barcd.xml.binding.RangeElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ResourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.SourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.URLTemplateElement;
import de.tu_chemnitz.mi.barcd.xml.binding.VideoDeviceElement;

/**
 * XML serializer/unserializer for {@link Job}.
 * 
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class XMLJobSerializer extends XMLSerializer<Job> {
    private ObjectFactory elements = new ObjectFactory();
    
    public XMLJobSerializer() {
        super(JobElement.class.getPackage().getName());
    }
    
    @Override
    protected Job restoreModel(JAXBElement<?> e)
        throws XMLSerializerException
    {
        JobElement je = (JobElement) e.getValue();
        Source source = restoreImageSource(je.getSource());
        Integer nextFrameNumber = je.getNextFrameNumber();
        if (nextFrameNumber == null) {
            return new Job(source);
        } else {
            return new Job(source, nextFrameNumber.intValue());
        }
    }

    @Override
    protected JAXBElement<JobElement> createRootElement(Job job)
        throws XMLSerializerException
    {
        return elements.createJob(createJobElement(job));
    }
    
    private JobElement createJobElement(Job job)
        throws XMLSerializerException
    {
        JobElement je = elements.createJobElement();
        je.setSource(createSourceElement(job.getSource()));
        je.setNextFrameNumber(job.getNextFrameNumber());
        return je;
    }
    
    private SourceElement createSourceElement(Source source)
        throws XMLSerializerException
    {
        SourceElement se = elements.createSourceElement();
        if (source instanceof VideoDeviceSource) {
            se.setDevice(createVideoDeviceElement((VideoDeviceSource) source));
        } else if (source instanceof VideoStreamSource) {
            se.setVideo(createResourceElement(((VideoStreamSource) source).getURL()));
        } else if (source instanceof ImageSequenceSource) {
            se.setSequence(createImageSequenceElement((ImageSequenceSource) source));
        } else if (source instanceof ImageCollectionSource) {
            se.setImages(createImagesElement(((ImageCollectionSource) source).getURLs()));
        } else if (source instanceof ImageServiceSource) {
            se.setSnapshot(createResourceElement(((ImageServiceSource) source).getURL()));
        } else {
            throw new XMLSerializerException(
                String.format("cannot serialize source (%s)",
                    source.getClass().getName()));
        }
        return se;
    }
    
    private ImagesElement createImagesElement(Collection<URL> files)
        throws XMLSerializerException
    {
        ImagesElement ie = elements.createImagesElement();
        List<ResourceElement> fes = ie.getImage();
        for (URL f : files) {
            fes.add(createResourceElement(f));
        }
        return ie;
    }

    private ImageSequenceElement createImageSequenceElement(ImageSequenceSource source)
        throws XMLSerializerException
    {
        TemplatedURLSequence tus = source.getSequence();
        
        URLTemplateElement ute = elements.createURLTemplateElement();
        String url = relativizeUrl(tus.getTemplate()).toString();
        ute.setValue(url);
        ute.setTag(tus.getTag());
        ute.setPadding(tus.getPadding());

        RangeElement re = elements.createRangeElement();
        Range r = tus.getRange();
        re.setStart(r.getStart());
        re.setEnd(r.getEnd());
        re.setStep(r.getStep());

        ImageSequenceElement ise = elements.createImageSequenceElement();
        ise.setTemplate(ute);
        ise.setRange(re);
        return ise;
    }

    private VideoDeviceElement createVideoDeviceElement(VideoDeviceSource source) {
        VideoDeviceElement vde = elements.createVideoDeviceElement();
        vde.setNumber(source.getDeviceNumber());
        return vde;
    }

    private ResourceElement createResourceElement(URL url)
        throws XMLSerializerException
    {
        ResourceElement fe = elements.createResourceElement();
        URL rurl = relativizeUrl(url);
        fe.setUrl(rurl.toString());
        return fe;
    }
    
    private Source restoreImageSource(SourceElement e)
        throws XMLSerializerException
    {
        {
            VideoDeviceElement ve = e.getDevice();
            if (ve != null) {
                return new VideoDeviceSource(ve.getNumber());
            }
        }
        {
            ResourceElement fe = e.getVideo();
            if (fe != null) {
                try {
                    URL url = new URL(getURLContext(), fe.getUrl());
                    return new VideoStreamSource(url);
                } catch (MalformedURLException ex) {
                    throw new XMLSerializerException(ex);
                }
            }
        }
        {
            ResourceElement fe = e.getSnapshot();
            if (fe != null) {
                try {
                    URL url = new URL(getURLContext(), fe.getUrl());
                    return new ImageServiceSource(url);
                } catch (MalformedURLException ex) {
                    throw new XMLSerializerException(ex);
                }
            }
        }
        {
            ImagesElement ie = e.getImages();
            if (ie != null) {
                List<ResourceElement> fe = ie.getImage();
                List<URL> files = new ArrayList<URL>(fe.size());
                for (ResourceElement f : fe) {
                    try {
                        files.add(new URL(getURLContext(), f.getUrl()));
                    } catch (MalformedURLException ex) {
                        throw new XMLSerializerException("malformed URL", ex);
                    }
                }
                return new ImageCollectionSource(files);
            }
        }
        {
            ImageSequenceElement se = e.getSequence();
            if (se != null) {
                RangeElement re = se.getRange();
                URLTemplateElement ute = se.getTemplate();
                
                URL url = resolveUrl(ute.getValue());
                
                Range range = new Range(re.getStart(),
                    re.getEnd().intValue(), re.getStep());
                TemplatedURLSequence seq = new TemplatedURLSequence(url,
                    ute.getTag(), range, ute.getPadding());
                return new ImageSequenceSource(seq);
            }
        }
        throw new XMLSerializerException();
    }
}
