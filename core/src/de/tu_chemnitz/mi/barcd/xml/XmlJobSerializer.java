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
import de.tu_chemnitz.mi.barcd.source.ImageSnapshotServiceSource;
import de.tu_chemnitz.mi.barcd.source.VideoDeviceSource;
import de.tu_chemnitz.mi.barcd.source.VideoStreamSource;
import de.tu_chemnitz.mi.barcd.util.Range;
import de.tu_chemnitz.mi.barcd.util.TemplatedUrlSequence;
import de.tu_chemnitz.mi.barcd.xml.binding.ImageCollectionSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ImageSequenceSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.JobElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ObjectFactory;
import de.tu_chemnitz.mi.barcd.xml.binding.RangeElement;
import de.tu_chemnitz.mi.barcd.xml.binding.ResourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.SnapshotSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.SourceChoiceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.UrlTemplateElement;
import de.tu_chemnitz.mi.barcd.xml.binding.VideoDeviceSourceElement;
import de.tu_chemnitz.mi.barcd.xml.binding.VideoStreamSourceElement;

/**
 * XML serializer/unserializer for {@link Job}.
 *
 * @author Erik Wienhold <ewie@hrz.tu-chemnitz.de>
 */
public class XmlJobSerializer extends XmlSerializer<Job> {
    private ObjectFactory elements = new ObjectFactory();

    public XmlJobSerializer() {
        super(JobElement.class.getPackage().getName());
    }

    @Override
    protected Job restoreModel(JAXBElement<?> e)
        throws XmlSerializerException
    {
        JobElement je = (JobElement) e.getValue();
        Source source = restoreImageSource(je.getSource());
        Integer nextFrameNumber = je.getNextFrameNumber();
        if (nextFrameNumber == null) {
            return new Job(source);
        } else {
            return new Job(source, nextFrameNumber);
        }
    }

    @Override
    protected JAXBElement<JobElement> createRootElement(Job job)
        throws XmlSerializerException
    {
        return elements.createJob(createJobElement(job));
    }

    private JobElement createJobElement(Job job)
        throws XmlSerializerException
    {
        JobElement je = elements.createJobElement();
        je.setSource(createSourceChoiceElement(job.getSource()));
        je.setNextFrameNumber(job.nextFrameNumber());
        return je;
    }

    private SourceChoiceElement createSourceChoiceElement(Source source)
        throws XmlSerializerException
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
        } else if (source instanceof ImageSnapshotServiceSource) {
            se.setSnapshot(createSnapshotSourceElement((ImageSnapshotServiceSource) source));
        } else {
            throw new XmlSerializerException(
                String.format("cannot serialize source (%s)",
                    source.getClass().getName()));
        }
        return se;
    }

    private ImageCollectionSourceElement createImageCollectionSourceElement(ImageCollectionSource source)
        throws XmlSerializerException
    {
        ImageCollectionSourceElement ie = elements.createImageCollectionSourceElement();
        List<ResourceElement> fes = ie.getImage();
        for (URL f : source.getUrlCollection()) {
            fes.add(createResourceElement(f));
        }
        return ie;
    }

    private ImageSequenceSourceElement createImageSequenceSourceElement(ImageSequenceSource source)
        throws XmlSerializerException
    {
        TemplatedUrlSequence tus = source.getSequence();

        UrlTemplateElement ute = elements.createUrlTemplateElement();
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
        return ise;
    }

    private VideoDeviceSourceElement createVideoDeviceSourceElement(VideoDeviceSource source) {
        VideoDeviceSourceElement vde = elements.createVideoDeviceSourceElement();
        vde.setNumber(source.getDeviceNumber());
        return vde;
    }

    private VideoStreamSourceElement createVideoStreamSourceElement(VideoStreamSource source)
        throws XmlSerializerException
    {
        VideoStreamSourceElement vsse = elements.createVideoStreamSourceElement();
        URL rurl = relativizeUrl(source.getUrl());
        vsse.setUrl(rurl.toString());
        return vsse;
    }

    private SnapshotSourceElement createSnapshotSourceElement(ImageSnapshotServiceSource source)
        throws XmlSerializerException
    {
        SnapshotSourceElement sse = elements.createSnapshotSourceElement();
        URL rurl = relativizeUrl(source.getUrl());
        sse.setUrl(rurl.toString());
        return sse;
    }

    private ResourceElement createResourceElement(URL url)
        throws XmlSerializerException
    {
        ResourceElement fe = elements.createResourceElement();
        URL rurl = relativizeUrl(url);
        fe.setUrl(rurl.toString());
        return fe;
    }

    private Source restoreImageSource(SourceChoiceElement e)
        throws XmlSerializerException
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
                    url = new URL(getUrlContext(), fe.getUrl());
                } catch (MalformedURLException ex) {
                    throw new XmlSerializerException(ex);
                }
                return new VideoStreamSource(url);
            }
        }
        {
            SnapshotSourceElement fe = e.getSnapshot();
            if (fe != null) {
                try {
                    URL url = new URL(getUrlContext(), fe.getUrl());
                    return new ImageSnapshotServiceSource(url);
                } catch (MalformedURLException ex) {
                    throw new XmlSerializerException(ex);
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
                        files.add(new URL(getUrlContext(), f.getUrl()));
                    } catch (MalformedURLException ex) {
                        throw new XmlSerializerException("malformed URL", ex);
                    }
                }
                return new ImageCollectionSource(files);
            }
        }
        {
            ImageSequenceSourceElement se = e.getSequence();
            if (se != null) {
                RangeElement re = se.getRange();
                UrlTemplateElement ute = se.getTemplate();

                URL url = resolveUrl(ute.getValue());

                Range range = new Range(re.getStart(),
                    re.getEnd().intValue(), re.getStep());
                TemplatedUrlSequence seq = new TemplatedUrlSequence(url,
                    ute.getTag(), range, ute.getPadding());
                return new ImageSequenceSource(seq);
            }
        }
        throw new XmlSerializerException();
    }
}
