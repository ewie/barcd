package de.tu_chemnitz.mi.barcd.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import de.tu_chemnitz.mi.barcd.xml.binding.ImageCollectionSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ImageSequenceSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.JobElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ObjectFactory;
import de.tu_chemnitz.mi.barcd.xml.binding.RangeElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ResourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.SnapshotSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.SourceChoiceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.URLTemplateElement;
import de.tu_chemnitz.mi.barcd.xml.binding.VideoDeviceSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.VideoStreamSourceElement;

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
        return new Job(source);
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
        return je;
    }
    
    private SourceChoiceElement createSourceElement(Source source)
        throws XMLSerializerException
    {
        SourceChoiceElement se = elements.createSourceChoiceElement();
        if (source instanceof VideoDeviceSource) {
            se.setDevice(createVideoDeviceSourceElement((VideoDeviceSource) source));
        } else if (source instanceof VideoStreamSource) {
            se.setVideo(createVideoStreamSourceElement((VideoStreamSource) source));
        } else if (source instanceof ImageSequenceSource) {
            se.setSequence(createImageSequenceSourceElement((ImageSequenceSource) source));
        } else if (source instanceof ImageCollectionSource) {
            se.setImages(createImageCollectionSourceElement((ImageCollectionSource) source));
        } else if (source instanceof ImageServiceSource) {
            se.setSnapshot(createSnapshotSourceElement((ImageServiceSource) source));
        } else {
            throw new XMLSerializerException(
                String.format("cannot serialize source (%s)",
                    source.getClass().getName()));
        }
        return se;
    }
    
    private ImageCollectionSourceElement createImageCollectionSourceElement(ImageCollectionSource source)
        throws XMLSerializerException
    {
        ImageCollectionSourceElement ie = elements.createImageCollectionSourceElement();
        List<ResourceElement> fes = ie.getImage();
        for (URL f : source.getURLs()) {
            fes.add(createResourceElement(f));
        }
        ie.setInitialFrameNumber(source.getInitialFrameNumber());
        return ie;
    }

    private ImageSequenceSourceElement createImageSequenceSourceElement(ImageSequenceSource source)
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

        ImageSequenceSourceElement ise = elements.createImageSequenceSourceElement();
        ise.setTemplate(ute);
        ise.setRange(re);
        ise.setInitialFrameNumber(source.getInitialFrameNumber());
        return ise;
    }

    private VideoDeviceSourceElement createVideoDeviceSourceElement(VideoDeviceSource source) {
        VideoDeviceSourceElement vde = elements.createVideoDeviceSourceElement();
        vde.setNumber(source.getDeviceNumber());
        return vde;
    }
    
    private VideoStreamSourceElement createVideoStreamSourceElement(VideoStreamSource source)
        throws XMLSerializerException
    {
        VideoStreamSourceElement vsse = elements.createVideoStreamSourceElement();
        URL rurl = relativizeUrl(source.getURL());
        vsse.setUrl(rurl.toString());
        vsse.setInitialFrameNumber(source.getInitialFrameNumber());
        return vsse;
    }
    
    private SnapshotSourceElement createSnapshotSourceElement(ImageServiceSource source)
        throws XMLSerializerException
    {
        SnapshotSourceElement sse = elements.createSnapshotSourceElement();
        URL rurl = relativizeUrl(source.getURL());
        sse.setUrl(rurl.toString());
        return sse;
    }

    private ResourceElement createResourceElement(URL url)
        throws XMLSerializerException
    {
        ResourceElement fe = elements.createResourceElement();
        URL rurl = relativizeUrl(url);
        fe.setUrl(rurl.toString());
        return fe;
    }
    
    private Source restoreImageSource(SourceChoiceElement e)
        throws XMLSerializerException
    {
        {
            VideoDeviceSourceElement ve = e.getDevice();
            if (ve != null) {
                return new VideoDeviceSource(ve.getNumber());
            }
        }
        {
            VideoStreamSourceElement fe = e.getVideo();
            if (fe != null) {
                URL url;
                try {
                    url = new URL(getURLContext(), fe.getUrl());
                } catch (MalformedURLException ex) {
                    throw new XMLSerializerException(ex);
                }
                int initialFrameNumber = fe.getInitialFrameNumber();
                return new VideoStreamSource(url, initialFrameNumber);
            }
        }
        {
            SnapshotSourceElement fe = e.getSnapshot();
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
            ImageCollectionSourceElement ie = e.getImages();
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
                int initialFrameNumber = ie.getInitialFrameNumber();
                return new ImageCollectionSource(files, initialFrameNumber);
            }
        }
        {
            ImageSequenceSourceElement se = e.getSequence();
            if (se != null) {
                RangeElement re = se.getRange();
                URLTemplateElement ute = se.getTemplate();
                
                URL url = resolveUrl(ute.getValue());
                int initialFrameNumber = se.getInitialFrameNumber();
                
                Range range = new Range(re.getStart(),
                    re.getEnd().intValue(), re.getStep());
                TemplatedURLSequence seq = new TemplatedURLSequence(url,
                    ute.getTag(), range, ute.getPadding());
                return new ImageSequenceSource(seq, initialFrameNumber);
            }
        }
        throw new XMLSerializerException();
    }
}
