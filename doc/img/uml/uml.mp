input metauml;

iClassNameOnly.iName.top := 3;
iClassNameOnly.iName.bottom := 5;

%iClass.foreColor := (.95, .95, .95);
%iClassNameOnly.foreColor := (.95, .95, .95);
%iInterface.foreColor := (.95, .95, .95);
%iPackage.foreColor := (.95, .95, .95);

beginfig(0);
  save
    Barcode,
    Extractor,
    Extraction,
    ExtractionHandler,
    ImageProvider,
    ImproveImage,
    Job,
    Region,
    Source;

  ClassName.Barcode("Barcode");
  ClassName.Extractor("Extractor");
  ClassName.Extraction("Extraction");
  ClassName.ExtractionHandler("ExtractionHandler");
  ClassName.ImageProvider("ImageProvider");
  ClassName.Job("Job");
  ClassName.Region("Region");
  ClassName.Source("Source {abstract}");

  classStereotypes.ExtractionHandler("<<interface>>");
  ExtractionHandler.info.iName.top := 0;

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.iName.top := 0;

  topToBottom(30)(Job, Extractor);
  topToBottom(100)(Source, ImageProvider);

  leftToRight(30)(Source, Job);
  leftToRight(80)(Job, Extraction);
  leftToRight(50)(ImageProvider, ExtractionHandler);

  Region.n = Extraction.s + (-20, -30);
  Barcode.n = Region.s + (20, -30);

  drawObjects(
    Barcode,
    Extractor,
    Extraction,
    ExtractionHandler,
    ImageProvider,
    Job,
    Region,
    Source);

  clink(associationUni)(Extractor, ImageProvider);
  clink(dependency)(Source, ImageProvider);
  clink(dependency)(Extractor, Extraction);
  clink(associationUni)(Extractor, Job);
  clink(associationUni)(Extractor, ExtractionHandler);
  clink(dependency)(Extractor, Barcode);
  clink(dependency)(Extractor, Region);
  link(dependency)(pathStepX(ExtractionHandler.e, Extraction.e, 80));

  clink(aggregation)(Source, Job);
  clink(aggregation)(Extraction, Job);
  link(aggregation)(pathVertical(Barcode.n + (10, 0), Extraction.bottom));
  link(aggregation)(pathVertical(Barcode.n + (-10, 0), Region.bottom));
  link(aggregation)(pathVertical(Region.n + (10, 0), Extraction.bottom));

  item(iStereo)("<<create>>")(obj.nw = Source.s);
  item(iStereo)("<<create>>")(obj.sw = Extractor.e);
  item(iStereo)("<<create>>")(obj.s = .5[Extraction.s, Extractor.n] + (0, 5));
  item(iStereo)("<<create>>")(obj.s = .5[Extractor.s, Barcode.n] + (0, 5));

  item(iAssoc)("consumes")(obj.w = .5[ImageProvider.s, Extractor.n]);
  item(iAssoc)("notifies")(obj.w = .5[Extractor.s, ExtractionHandler.n]);

  item(iAssoc)("*")(obj.se = Extraction.w);
  item(iAssoc)("*")(obj.sw = Region.n + (10, 0));
  item(iAssoc)("*")(obj.sw = Barcode.n + (10, 0));
  item(iAssoc)("0..1")(obj.sw = Barcode.n + (-10, 0));
endfig;

beginfig(9);
  save
    BufferedImageSource,
    Source,
    BufferedImageProvider;

  Class.BufferedImageSource("BufferedImageSource")
    ()
    ("+BufferedImageSource(images: BufferedImage[*])",
     "+createImageProvider(frameNumber: int): BufferedImageProvider");

  ClassName.Source("Source {abstract}");
  ClassName.BufferedImageProvider("BufferedImageProvider");

  topToBottom(20)(
    Source,
    BufferedImageSource,
    BufferedImageProvider);

  drawObjects(
    Source,
    BufferedImageSource,
    BufferedImageProvider);

  clink(inheritance)(BufferedImageSource, Source);
  clink(dependency)(BufferedImageSource, BufferedImageProvider);

  item(iStereo)("<<create>>")(obj.nw = BufferedImageSource.s);
endfig;

beginfig(10);
  save
    ImageCollectionSource,
    Source,
    RemoteImageProvider;

  Class.ImageCollectionSource("ImageCollectionSource")
    ()
    ("+ImageCollectionSource(urls: URL[*])",
     "+createImageProvider(frameNumber: int): RemoteImageProvider",
     "+getUrls(): URL[*]");

  ClassName.Source("Source {abstract}");
  ClassName.RemoteImageProvider("RemoteImageProvider");

  topToBottom(20)(
    Source,
    ImageCollectionSource,
    RemoteImageProvider);

  drawObjects(
    Source,
    ImageCollectionSource,
    RemoteImageProvider);

  clink(inheritance)(ImageCollectionSource, Source);
  clink(dependency)(ImageCollectionSource, RemoteImageProvider);

  item(iStereo)("<<create>>")(obj.nw = ImageCollectionSource.s);
endfig;

beginfig(11);
  save
    ImageSequenceSource,
    Source,
    RemoteImageProvider,
    TemplatedUrlSequence,
    G;

  Class.ImageSequenceSource("ImageSequenceSource")
    ()
    ("+ImageSequenceSource(sequence: TemplatedUrlSequence)",
     "+createImageProvider(frameNumber: int): RemoteImageProvider",
     "+getUrlSequence(): TemplatedUrlSequence");

  ClassName.Source("Source {abstract}");
  ClassName.RemoteImageProvider("RemoteImageProvider");
  ClassName.TemplatedUrlSequence("TemplatedUrlSequence");

  Group.G(RemoteImageProvider, TemplatedUrlSequence);

  topToBottom(20)(
    Source,
    ImageSequenceSource,
    G);

  leftToRight(30)(
    TemplatedUrlSequence,
    RemoteImageProvider);

  drawObjects(
    Source,
    ImageSequenceSource,
    G);

  clink(inheritance)(ImageSequenceSource, Source);
  link(dependency)(rpathVertical(RemoteImageProvider.n, ImageSequenceSource.bottom));
  link(associationUni)(rpathVertical(TemplatedUrlSequence.n, ImageSequenceSource.bottom));

  item(iStereo)("<<create>>")(obj.nw = RemoteImageProvider.n + (0, 20));
endfig;

beginfig(12);
  save
    ImageSnapshotServiceSource,
    Source,
    RemoteImageProvider;

  Class.ImageSnapshotServiceSource("ImageSnapshotServiceSource")
    ()
    ("+ImageSnapshotServiceSource(url: URL)",
     "+createImageProvider(frameNumber: int): RemoteImageProvider",
     "+getUrl(): URL");

  ClassName.Source("Source {abstract}");
  ClassName.RemoteImageProvider("RemoteImageProvider");

  topToBottom(20)(
    Source,
    ImageSnapshotServiceSource,
    RemoteImageProvider);

  drawObjects(
    Source,
    ImageSnapshotServiceSource,
    RemoteImageProvider);

  clink(inheritance)(ImageSnapshotServiceSource, Source);
  clink(dependency)(ImageSnapshotServiceSource, RemoteImageProvider);

  item(iStereo)("<<create>>")(obj.nw = ImageSnapshotServiceSource.s);
endfig;

beginfig(13);
  save
    VideoDeviceSource,
    Source,
    VideoImageProvider,
    OpenCvDeviceFrameReader,
    G;

  Class.VideoDeviceSource("VideoDeviceSource")
    ()
    ("+VideoDeviceSource(deviceId: int)",
     "+createImageProvider(frameNumber: int): VideoImageProvider",
     "+getDeviceId(): int");

  ClassName.Source("Source {abstract}");
  ClassName.VideoImageProvider("VideoImageProvider");
  ClassName.OpenCvDeviceFrameReader("OpenCvDeviceFrameReader");

  Group.G(VideoImageProvider, OpenCvDeviceFrameReader);

  topToBottom(20)(
    Source,
    VideoDeviceSource,
    G);

  leftToRight(30)(
    OpenCvDeviceFrameReader,
    VideoImageProvider);

  drawObjects(
    Source,
    VideoDeviceSource,
    G);

  clink(inheritance)(VideoDeviceSource, Source);
  link(dependency)(rpathVertical(VideoImageProvider.n, VideoDeviceSource.bottom));
  link(associationUni)(rpathVertical(OpenCvDeviceFrameReader.n, VideoDeviceSource.bottom));

  item(iStereo)("<<create>>")(obj.nw = VideoImageProvider.n + (0, 20));
  item(iAssoc)("creates")(obj.nw = OpenCvDeviceFrameReader.n + (0, 20));
endfig;

beginfig(14);
  save
    VideoStreamSource,
    Source,
    VideoImageProvider,
    OpenCvFileFrameReader,
    G;

  Class.VideoStreamSource("VideoStreamSource")
    ()
    ("+VideoStreamSource(url: URL)",
     "+createImageProvider(frameNumber: int): VideoImageProvider",
     "+getUrl(): URL");

  ClassName.Source("Source {abstract}");
  ClassName.VideoImageProvider("VideoImageProvider");
  ClassName.OpenCvFileFrameReader("OpenCvFileFrameReader");

  Group.G(VideoImageProvider, OpenCvFileFrameReader);

  topToBottom(20)(
    Source,
    VideoStreamSource,
    G);

  leftToRight(30)(
    OpenCvFileFrameReader,
    VideoImageProvider);

  drawObjects(
    Source,
    VideoStreamSource,
    G);

  clink(inheritance)(VideoStreamSource, Source);
  link(dependency)(rpathVertical(VideoImageProvider.n, VideoStreamSource.bottom));
  link(associationUni)(rpathVertical(OpenCvFileFrameReader.n, VideoStreamSource.bottom));

  item(iStereo)("<<create>>")(obj.nw = VideoImageProvider.n + (0, 20));
  item(iAssoc)("creates")(obj.nw = OpenCvFileFrameReader.n + (0, 20));
endfig;

beginfig(15);
  save
    BufferedImageProvider,
    ImageProvider;

  Class.BufferedImageProvider("BufferedImageProvider")
    ()
    ("+BufferedImageProvider(images: Iterator<BufferedImage>)",
     "+consume(): BufferedImage",
     "+hasMore(): boolean");

  ClassName.ImageProvider("ImageProvider");

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  topToBottom(20)(
    ImageProvider,
    BufferedImageProvider);

  drawObjects(
    ImageProvider,
    BufferedImageProvider);

  clink(realization)(BufferedImageProvider, ImageProvider);
endfig;

beginfig(16);
  save
    RemoteImageProvider,
    ImageProvider;

  Class.RemoteImageProvider("RemoteImageProvider")
    ()
    ("+RemoteImageProvider(urls: Iterator<URL>)",
     "+consume(): BufferedImage",
     "+hasMore(): boolean");

  ClassName.ImageProvider("ImageProvider");

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  topToBottom(20)(
    ImageProvider,
    RemoteImageProvider);

  drawObjects(
    ImageProvider,
    RemoteImageProvider);

  clink(realization)(RemoteImageProvider, ImageProvider);
endfig;

beginfig(17);
  save
    VideoImageProvider,
    ImageProvider,
    FrameReader;

  Class.VideoImageProvider("VideoImageProvider")
    ()
    ("+VideoImageProvider(frameReader: FrameReader)",
     "+consume(): BufferedImage",
     "+hasMore(): boolean");

  ClassName.ImageProvider("ImageProvider");
  ClassName.FrameReader("FrameReader");

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  classStereotypes.FrameReader("<<interface>>");
  FrameReader.info.iName.top := 0;

  topToBottom(20)(
    ImageProvider,
    VideoImageProvider,
    FrameReader);

  drawObjects(
    ImageProvider,
    VideoImageProvider,
    FrameReader);

  clink(realization)(VideoImageProvider, ImageProvider);
  clink(associationUni)(VideoImageProvider, FrameReader);
endfig;

beginfig(101);
  save
    Barcode,
    BarcodeType,
    Point,
    G;

  Class.Barcode("Barcode")
    ()
    ("+Barcode(type: BarcodeType, text: string, bytes: byte[*], points: Point[*])",
     "+getAnchorPoints(): Point[*]",
     "+getBytes(): byte[*]",
     "+getText(): string",
     "+getType(): BarcodeType");

  ClassName.Point("Point");
  ClassName.BarcodeType("BarcodeType");

  classStereotypes.BarcodeType("<<enumeration>>");
  BarcodeType.info.iName.top := 0;

  Group.G(Point, BarcodeType);

  leftToRight(30)(
    Point,
    BarcodeType);

  topToBottom(20)(
    Barcode,
    G);

  drawObjects(
    Barcode,
    G);

  link(associationUni)(rpathVertical(Point.n, Barcode.bottom));
  link(associationUni)(rpathVertical(BarcodeType.n, Barcode.bottom));
endfig;

beginfig(102);
  save
    BarcodeReader,
    Barcode;

  Interface.BarcodeReader("BarcodeReader")
    ("+readBarcode(image: BufferedImage): Barcode",
     "+readMultipleBarcodes(image: BufferedImage): Barcode[*]");

  classStereotypes.BarcodeReader("<<interface>>");

  ClassName.Barcode("Barcode");

  leftToRight(50)(
    BarcodeReader,
    Barcode);

  drawObjects(
    BarcodeReader,
    Barcode);

  clink(dependency)(BarcodeReader, Barcode);

  item(iStereo)("<<create>>")(obj.sw = BarcodeReader.e);
endfig;

beginfig(103);
  save
    BarcodeType;

  Class.BarcodeType("BarcodeType")
    ("+AZTEC_CODE",
     "+CODEBAR",
     "+CODE_128",
     "+CODE_39",
     "+CODE_93",
     "+DATA_MATRIX",
     "+EAN_13",
     "+EAN_8",
     "+GS1_DATABAR_EXPANDED",
     "+GS1_DATABAR_OMNIDIRECTIONAL",
     "+INTERLEAVED_2_OF_5",
     "+PDF417",
     "+QR_CODE",
     "+UNKNOWN",
     "+UPC_A",
     "+UPC_E")
    ();

  classStereotypes.BarcodeType("<<enumeration>>");

  drawObjects(BarcodeType);
endfig;

beginfig(104);
  save
    BarcodeReader,
    DefaultBarcodeReader,
    ZXing;

  Class.DefaultBarcodeReader("DefaultBarcodeReader")
    ()
    ("+readBarcode(image: BufferedImage): Barcode",
     "+readMultipleBarcodes(image: BufferedImage): Barcode[*]");

  ClassName.BarcodeReader("BarcodeReader");
  classStereotypes.BarcodeReader("<<interface>>");
  BarcodeReader.info.iName.top := 0;

  Package.ZXing("com.google.ZXing")();

  topToBottom(20)(
    BarcodeReader,
    DefaultBarcodeReader,
    ZXing);

  drawObjects(
    DefaultBarcodeReader,
    BarcodeReader,
    ZXing);

  clink(realization)(DefaultBarcodeReader, BarcodeReader);
  clink(dependency)(DefaultBarcodeReader, ZXing);
endfig;

beginfig(105);
  save
    DefaultGrayscaler,
    Grayscaler;

  Class.DefaultGrayscaler("DefaultGrayscaler")
    ()
    ("+convertToGrayscale(image: BufferedImage): BufferedImage");

  ClassName.Grayscaler("Grayscaler");
  classStereotypes.Grayscaler("<<interface>>");
  Grayscaler.info.iName.top := 0;

  topToBottom(20)(
    Grayscaler,
    DefaultGrayscaler);

  drawObjects(
    DefaultGrayscaler,
    Grayscaler);

  clink(realization)(DefaultGrayscaler, Grayscaler);
endfig;

beginfig(106);
  save
    DefaultImageEnhancer,
    ImageEnhancer,
    ImproveImage;

  Class.DefaultImageEnhancer("DefaultImageEnhancer")
    ()
    ("+enhanceImage(image: BufferedImage): BufferedImage");

  ClassName.ImageEnhancer("ImageEnhancer");
  classStereotypes.ImageEnhancer("<<interface>>");
  ImageEnhancer.info.iName.top := 0;

  ClassName.ImproveImage("ImproveImage");

  topToBottom(20)(
    ImageEnhancer,
    DefaultImageEnhancer,
    ImproveImage);

  drawObjects(
    DefaultImageEnhancer,
    ImageEnhancer,
    ImproveImage);

  clink(realization)(DefaultImageEnhancer, ImageEnhancer);
  clink(dependency)(DefaultImageEnhancer, ImproveImage);
endfig;

beginfig(107);
  save
    DefaultRegionExtractor,
    Region,
    DilationOperator,
    ScalingOperator,
    G;

  Class.DefaultRegionExtractor("DefaultRegionExtractor")
    ()
    ("+DefaultRegionExtractor(dilationOperator: DilationOperator)",
     "+extractRegions(image: BufferedImage): Region[*]");

  ClassName.Region("Region");
  ClassName.DilationOperator("DilationOperator");
  ClassName.ScalingOperator("ScalingOperator");
  ClassName.RegionExtractor("RegionExtractor");
  classStereotypes.RegionExtractor("<<interface>>");
  RegionExtractor.info.iName.top := 0;

  Group.G(
    Region,
    DilationOperator,
    ScalingOperator);

  topToBottom(20)(
    RegionExtractor,
    DefaultRegionExtractor,
    G);

  leftToRight(20)(
    Region,
    DilationOperator,
    ScalingOperator);

  drawObjects(
    DefaultRegionExtractor,
    RegionExtractor,
    G);

  link(realization)(rpathVertical(RegionExtractor.s, DefaultRegionExtractor.top));
  link(dependency)(rpathVertical(Region.n, DefaultRegionExtractor.bottom));
  link(dependency)(rpathVertical(DilationOperator.n, DefaultRegionExtractor.bottom));
  link(dependency)(rpathVertical(ScalingOperator.n, DefaultRegionExtractor.bottom));

  item(iStereo)("<<create>>")(obj.nw = Region.n + (0, 20));
endfig;

beginfig(108);
  save
    DefaultRegionFilter,
    Region,
    Filter,
    FilterTemplate;

  Class.DefaultRegionFilter("DefaultRegionFilter")
    ()
    ("+filter(regions: Region[*]): Region[*]");

  ClassName.Filter("Filter");
  classStereotypes.Filter("<<interface>>");
  Filter.info.iName.top := 0;

  ClassName.Region("Region");
  ClassTemplate.FilterTemplate("T")(Filter);

  topToBottom(30)(
    Filter,
    DefaultRegionFilter);

  topToBottom(20)(
    DefaultRegionFilter,
    Region);

  drawObjects(
    DefaultRegionFilter,
    Filter,
    FilterTemplate,
    Region);

  clink(realization)(DefaultRegionFilter, Filter);
  clink(dependency)(DefaultRegionFilter, Region);

  item(iStereo)("<<bind>>")(obj.sw = .5[Filter.s, DefaultRegionFilter.n]);
  item(iStereo)("<T -> Region>")(obj.nw = .5[Filter.s, DefaultRegionFilter.n]);
endfig;

beginfig(109);
  save
    Extractor,
    BarcodeReader,
    Grayscaler,
    ImageEnhancer,
    RegionExtractor,
    Filter,
    G,
    F,
    Job;

  Class.Extractor("Exractor")
    ()
    ("+Extractor(job: Job)",
     "+getJob(): Job",
     "+hasMoreImages(): boolean",
     "+processNextImage()",
     "+setBarcodeReader(reader: BarcodeReader)",
     "+setExtractionHandler(handler: ExtractionHandler)",
     "+setGrayscaler(grayscaler: Grayscaler)",
     "+setRegionExtractor(extractor: RegionExtractor)",
     "+setRegionFilter(filter: Filter<Region>)");

  ClassName.Job("Job");

  ClassName.BarcodeReader("BarcodeReader");
  ClassName.Grayscaler("Grayscaler");
  ClassName.ImageEnhancer("ImageEnhancer");
  ClassName.RegionExtractor("RegionExtractor");
  ClassName.Filter("Filter");

  classStereotypes.BarcodeReader("<<interface>>");
  BarcodeReader.info.iName.top := 0;

  classStereotypes.Grayscaler("<<interface>>");
  Grayscaler.info.iName.top := 0;

  classStereotypes.ImageEnhancer("<<interface>>");
  ImageEnhancer.info.iName.top := 0;

  classStereotypes.RegionExtractor("<<interface>>");
  RegionExtractor.info.iName.top := 0;

  classStereotypes.Filter("<<interface>>");
  Filter.info.iName.top := 0;

  ClassTemplate.FilterTemplate("T")(Filter);

  Group.G(BarcodeReader, Grayscaler, ImageEnhancer);
  Group.F(Job, RegionExtractor, Filter);

  leftToRight(30)(Extractor, G);
  topToBottom(30)(Extractor, F);

  topToBottom(20)(
    BarcodeReader,
    Grayscaler,
    ImageEnhancer);

  leftToRight(20)(
    Job,
    RegionExtractor,
    Filter);

  drawObjects(
    Extractor,
    Job,
    G,
    F,
    FilterTemplate);

  link(associationUni)(rpathHorizontal(BarcodeReader.w, Extractor.right));
  link(associationUni)(rpathHorizontal(Grayscaler.w, Extractor.right));
  link(associationUni)(rpathHorizontal(ImageEnhancer.w, Extractor.right));
  link(associationUni)(rpathVertical(RegionExtractor.n, Extractor.bottom));
  link(associationUni)(rpathVertical(Filter.n, Extractor.bottom));
  link(associationUni)(rpathVertical(Job.n, Extractor.bottom));

  item(iStereo)("<<bind>>")(obj.sw = Filter.n + (0, 20));
  item(iStereo)("<T -> Region>")(obj.nw = Filter.n + (0, 20));
endfig;

beginfig(110);
  save
    Extraction;

  Class.Extraction("Extraction")
    ()
    ("+Extraction(frameNumber: int)",
     "+Extraction(frameNumber: int, regions: Region[*], barcodes: Barcode[*])",
     "+addRegion(region: Region)",
     "+addRegionlessBarcode(barcode: Barcode)",
     "+getBarcodes(): Barcode[*]",
     "+getFrameNumber(): int",
     "+getRegionBarcodes(): Barcode[*]",
     "+getRegionlessBarcodes(): Barcode[*]",
     "+getRegions(): Region[*]");

  ClassName.Barcode("Barcode");
  ClassName.Region("Region");

  Group.G(Barcode, Region);

  topToBottom(30)(Region, Barcode);
  leftToRight(30)(Extraction, G);

  drawObjects(Extraction, G);

  link(aggregation)(pathHorizontal(Region.w, Extraction.right));
  link(aggregation)(pathHorizontal(Barcode.w, Extraction.right));

  item(iAssoc)("*")(obj.se = Region.w);
  item(iAssoc)("*")(obj.se = Barcode.w);
endfig;

beginfig(111);
  save
    ExtractionHandler,
    Extraction;

  Interface.ExtractionHandler("ExtractionHandler")
    ("+handleExtraction(extraction: Extraction, image: BufferedImage): BufferedImage");

  classStereotypes.ExtractionHandler("<<interface>>");

  ClassName.Extraction("Extraction");

  topToBottom(20)(ExtractionHandler, Extraction);

  drawObjects(ExtractionHandler, Extraction);

  clink(dependency)(ExtractionHandler, Extraction);
endfig;

beginfig(112);
  save
    Grayscaler;

  Interface.Grayscaler("Grayscaler")
    ("+convertToGrayscale(image: BufferedImage): BufferedImage");

  classStereotypes.Grayscaler("<<interface>>");

  drawObjects(Grayscaler);
endfig;

beginfig(113);
  save
    ImageEnhancer;

  Interface.ImageEnhancer("ImageEnhancer")
    ("+enhanceImage(): BufferedImage");

  classStereotypes.ImageEnhancer("<<interface>>");

  drawObjects(ImageEnhancer);
endfig;

beginfig(114);
  save
    ImageProvider;

  Interface.ImageProvider("ImageProvider")
    ("+consume(): BufferedImage",
     "+hasMore(): boolean");

  classStereotypes.ImageProvider("<<interface>>");

  drawObjects(ImageProvider);
endfig;

beginfig(115);
  save
    Job,
    Extraction,
    Source,
    TemplatedUrlSequence,
    G;

  Class.Job("Job")
    ()
    ("+Job(source: Source, extractionUrlTemplate: TemplatedUrlSequence, initialFrameNumber: int = 0)",
     "+createExtraction(): Extraction",
     "+createImageProvider(): ImageProvider",
     "+getExtractions(): Extraction[*]",
     "+getExtractionUrlTemplate(): TemplatedUrlSequence",
     "+getNextFrameNumber(): int",
     "+getSource(): Source");

  ClassName.Extraction("Extraction");
  ClassName.Source("Source {abstract}");
  ClassName.TemplatedUrlSequence("TemplatedUrlSequence");

  Group.G(Extraction, Source, TemplatedUrlSequence);

  topToBottom(20)(Job, G);

  leftToRight(30)(Source, Extraction, TemplatedUrlSequence);

  drawObjects(Job, G);

  link(aggregation)(pathVertical(Extraction.n, Job.bottom));
  link(aggregation)(pathVertical(Source.n, Job.bottom));
  link(aggregation)(pathVertical(TemplatedUrlSequence.n, Job.bottom));

  item(iAssoc)("*")(obj.sw = Extraction.n);
endfig;

beginfig(116);
  save
    Region,
    Barcode,
    ConvexPolygon,
    AxisAlignedRectangle,
    OrientedRectangle,
    GenericConvexPolygon,
    G,
    F;

  Class.Region("Region")
    ()
    ("+Region(polygon: ConvexPolygon, coverage: double)",
     "+contains(point: Point): boolean",
     "+getAxisAlignedRectangle(): AxisAlignedRectangle",
     "+getBarcode(): Barcode",
     "+getConvexPolygon(): ConvexPolygon",
     "+getCoverage(): double",
     "+getOrientedRectangle(): OrientedRectangle",
     "+setBarcode(barcode: Barcode)",
     "+createFromPoints(points: Point[*]): Region",
     "+createFromPoints(points: Point[*], generatingPointCount: int): Region");

  ClassName.Barcode("Barcode");

  ClassName.ConvexPolygon("ConvexPolygon {abstract}");

  ClassName.AxisAlignedRectangle("AxisAlignedRectangle");
  ClassName.OrientedRectangle("OrientedRectangle");
  ClassName.GenericConvexPolygon("GenericConvexPolygon");

  Group.G(Barcode, ConvexPolygon);
  Group.F(AxisAlignedRectangle, OrientedRectangle);

  topToBottom.left(20)(
    Region,
    G);

  topToBottom.right(50)(
    Region,
    F);

  topToBottom.left(80)(
    Region,
    GenericConvexPolygon);

  leftToRight(10)(
    Barcode,
    ConvexPolygon);

  leftToRight(10)(
    AxisAlignedRectangle,
    OrientedRectangle);

  drawObjects(
    Region,
    GenericConvexPolygon,
    G,
    F);

  link(aggregation)(pathVertical(Barcode.n, Region.bottom));
  link(aggregation)(pathVertical(ConvexPolygon.n, Region.bottom));
  link(associationUni)(rpathVertical(AxisAlignedRectangle.ne + (-10, 0), Region.bottom));
  link(associationUni)(rpathVertical(OrientedRectangle.n, Region.bottom));
  link(associationUni)(rpathVertical(GenericConvexPolygon.n + (-1, 0), Region.bottom));

  item(iAssoc)("creates")(obj.nw = AxisAlignedRectangle.ne + (-10, 50));
  item(iAssoc)("creates")(obj.nw = OrientedRectangle.n + (0, 50));
  item(iAssoc)("creates")(obj.nw = GenericConvexPolygon.n + (0, 80));

  item(iAssoc)("0..1")(obj.se = Barcode.n);

  % mark static methods
  draw Region.sw+(13,13.5) -- Region.se+(-112,13.5);
  draw Region.sw+(13,3) -- Region.se+(-4,3);
endfig;

beginfig(117);
  save
    Filter,
    FilterTemplate;

  Interface.Filter("Filter")
    ("+filter(values: T[*]): T[*]");

  classStereotypes.Filter("<<interface>>");

  ClassTemplate.FilterTemplate("T")(Filter);

  drawObjects(Filter, FilterTemplate);
endfig;

beginfig(118);
  save
    RegionExtractor,
    Region;

  Interface.RegionExtractor("RegionExtractor")
    ("+extractRegions(image: BufferedImage): Region[*]");

  classStereotypes.RegionExtractor("<<interface>>");

  ClassName.Region("Region");

  leftToRight(50)(RegionExtractor, Region);

  drawObjects(RegionExtractor, Region);

  clink(dependency)(RegionExtractor, Region);

  item(iStereo)("<<create>>")(obj.sw = RegionExtractor.e);
endfig;

beginfig(119);
  save
    Source,
    ImageProvider;

  Class.Source("Source {abstract}")
    ()
    ("+createImageProvider(): ImageProvider",
     "+createImageProvider(frameNumber: int): ImageProvider {abstract}");

  ClassName.ImageProvider("ImageProvider");
  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  topToBottom(20)(Source, ImageProvider);

  drawObjects(Source, ImageProvider);

  clink(dependency)(Source, ImageProvider);

  item(iStereo)("<<create>>")(obj.nw = Source.s);
endfig;

beginfig(120);
  save
    SourceFactory,
    BufferedImageSource,
    ImageCollectionSource,
    ImageSequenceSource,
    ImageSnapshotServiceSource,
    VideoDeviceSource,
    VideoStreamSource,
    TemplatedUrlSequence,
    G;

  Class.SourceFactory("SourceFactory")
    ()
    ("+newBufferedImageSource(images: BufferedImage[*]): BufferedImageSource",
     "+newImageCollectionSource(urls: URL[*]): ImageCollectionSource",
     "+newImageSequenceSource(url: TemplatedUrlSequence): ImageSequenceSource",
     "+newImageSnapshotServiceSource(url: URL): ImageSnapshotServiceSource",
     "+newVideoDeviceSource(deviceId: int): VideoDeviceSource",
     "+newVideoStreamSource(url: URL): VideoStreamSource");

  ClassName.BufferedImageSource("BufferedImageSource");
  ClassName.ImageCollectionSource("ImageCollectionSource");
  ClassName.ImageSequenceSource("ImageSequenceSource");
  ClassName.ImageSnapshotServiceSource("ImageSnapshotServiceSource");
  ClassName.VideoDeviceSource("VideoDeviceSource");
  ClassName.VideoStreamSource("VideoStreamSource");
  ClassName.TemplatedUrlSequence("TemplatedUrlSequence");

  Group.G(
    BufferedImageSource,
    ImageCollectionSource,
    ImageSequenceSource,
    ImageSnapshotServiceSource,
    VideoDeviceSource,
    VideoStreamSource);

  topToBottom(10)(
    BufferedImageSource,
    ImageCollectionSource,
    ImageSequenceSource,
    ImageSnapshotServiceSource,
    VideoDeviceSource,
    VideoStreamSource);

  topToBottom.right(10)(SourceFactory, G);

  topToBottom.left(30)(SourceFactory, TemplatedUrlSequence);

  drawObjects(SourceFactory, G, TemplatedUrlSequence);

  link(dependency)(rpathManhattanX(BufferedImageSource.w, SourceFactory.s));
  link(dependency)(rpathManhattanX(ImageCollectionSource.w, SourceFactory.s));
  link(dependency)(rpathManhattanX(ImageSequenceSource.w, SourceFactory.s));
  link(dependency)(rpathManhattanX(ImageSnapshotServiceSource.w, SourceFactory.s));
  link(dependency)(rpathManhattanX(VideoDeviceSource.w, SourceFactory.s));
  link(dependency)(rpathManhattanX(VideoStreamSource.w, SourceFactory.s));
  link(dependency)(rpathVertical(TemplatedUrlSequence.n, SourceFactory.bottom));

  item(iStereo)("<<create>>")(obj.nw = SourceFactory.s);

  % mark static methods
  draw SourceFactory.sw+(13,55.) -- SourceFactory.se+(-15,55.5);
  draw SourceFactory.sw+(13,45) -- SourceFactory.se+(-56,45);
  draw SourceFactory.sw+(13,34.5) -- SourceFactory.se+(-4,34.5);
  draw SourceFactory.sw+(13,24) -- SourceFactory.se+(-21,24);
  draw SourceFactory.sw+(13,13.5) -- SourceFactory.se+(-86,13.5);
  draw SourceFactory.sw+(13,3) -- SourceFactory.se+(-99,3);
endfig;

beginfig(121);
  save
    AxisAlignedRectangle,
    Rectangle,
    ConvexPolygon,
    Point,
    G;

  Class.AxisAlignedRectangle("AxisAlignedRectangle")
    ()
    ("+AxisAlignedRectangle(min: Point, max: Point)",
     "+contains(p: Point): boolean",
     "+getHeight(): double",
     "+getMax(): Point",
     "+getMin(): Point",
     "+getVertices(): Point[4]",
     "+getWidth(): double",
     "+createFromConvexPolygon(polygon: ConvexPolygon): AxisAlignedRectangle");

  ClassName.Rectangle("Rectangle {abstract}");

  ClassName.ConvexPolygon("ConvexPolygon {abstract}");

  ClassName.Point("Point");

  Group.G(ConvexPolygon, Point);

  topToBottom(20)(Rectangle, AxisAlignedRectangle, G);
  leftToRight(20)(ConvexPolygon, Point);

  drawObjects(Rectangle, AxisAlignedRectangle, G);

  link(inheritance)(rpathVertical(Rectangle.s, AxisAlignedRectangle.top));
  link(dependency)(rpathVertical(ConvexPolygon.n, AxisAlignedRectangle.bottom));
  link(associationUni)(rpathVertical(Point.n, AxisAlignedRectangle.bottom));

  draw AxisAlignedRectangle.sw+(13, 3) -- AxisAlignedRectangle.se+(-4, 3);
endfig;

beginfig(122);
  save
    OrientedRectangle,
    Rectangle,
    ConvexPolygon,
    Point,
    Vector,
    G;

  Class.OrientedRectangle("OrientedRectangle")
    ()
    ("+OrientedRectangle(origin: Point, width: Vector, height: Vector)",
     "+OrientedRectangle(origin: Point, q: Point, r: Point)",
     "+contains(p: Point): boolean",
     "+getHeight(): double",
     "+getRotationAngle(): double",
     "+getVertices(): Point[4]",
     "+getWidth(): double",
     "+createFromConvexPolygon(polygon: ConvexPolygon): OrientedRectangle");

  ClassName.Rectangle("Rectangle {abstract}");

  ClassName.ConvexPolygon("ConvexPolygon {abstract}");

  ClassName.Point("Point");
  ClassName.Vector("Vector");

  Group.G(ConvexPolygon, Point, Vector);

  topToBottom(20)(Rectangle, OrientedRectangle, G);
  leftToRight(20)(ConvexPolygon, Point, Vector);

  drawObjects(Rectangle, OrientedRectangle, G);

  link(inheritance)(rpathVertical(Rectangle.s, OrientedRectangle.top));
  link(dependency)(rpathVertical(ConvexPolygon.n, OrientedRectangle.bottom));
  link(associationUni)(rpathVertical(Point.n, OrientedRectangle.bottom));
  link(associationUni)(rpathVertical(Vector.n, OrientedRectangle.bottom));

  draw OrientedRectangle.sw+(13, 3) -- OrientedRectangle.se+(-4, 3);
endfig;

beginfig(123);
  save
    Rectangle,
    ConvexPolygon;

  Class.Rectangle("Rectangle {abstract}")
    ()
    ("+computeArea(): double",
     "+getHeight(): double {abstract}",
     "+getSize(): int = 4 {leaf}",
     "+getWidth(): double {abstract}");

  ClassName.ConvexPolygon("ConvexPolygon {abstract}");

  topToBottom(20)(ConvexPolygon, Rectangle);

  drawObjects(Rectangle, ConvexPolygon);

  clink(inheritance)(Rectangle, ConvexPolygon);
endfig;

beginfig(124);
  save
    ConvexPolygon,
    Point;

  Class.ConvexPolygon("ConvexPolygon {abstract}")
    ()
    ("+contains(p: Point)",
     "+computeArea(): double",
     "+getSize(): int",
     "+getVertices(): Point[*] {abstract}");

  ClassName.Point("Point");

  leftToRight(30)(ConvexPolygon, Point);

  drawObjects(ConvexPolygon, Point);

  clink(dependency)(ConvexPolygon, Point);
endfig;

beginfig(125);
  save
    GenericConvexPolygon,
    ConvexPolygon,
    ConvexHullAlgorithm;

  Class.GenericConvexPolygon("GenericConvexPolygon")
    ()
    ("+GenericConvexPolygon(vertices: Point[*])",
     "+getVertices(): Point[*]",
     "+createFromConvexHull(point: Point[*]): GenericConvexPolygon");

  ClassName.ConvexPolygon("ConvexPolygon {abstract}");
  ClassName.ConvexHullAlgorithm("ConvexHullAlgorithm");

  topToBottom(20)(
    ConvexPolygon,
    GenericConvexPolygon,
    ConvexHullAlgorithm);

  drawObjects(
    GenericConvexPolygon,
    ConvexPolygon,
    ConvexHullAlgorithm);

  clink(inheritance)(GenericConvexPolygon, ConvexPolygon);
  clink(dependency)(GenericConvexPolygon, ConvexHullAlgorithm);

  draw GenericConvexPolygon.sw+(13,3) -- GenericConvexPolygon.se+(-20,3);
endfig;

beginfig(126);
  save
    ImproveImage,
    ImageProcessing;

  Class.ImproveImage("ImproveImage")
    ()
    ("+checkBlur(image: BufferedImage): BufferedImage",
     "+checkBrightness(image: BufferedImage): BufferedImage",
     "+checkImage(image: BufferedImage): BufferedImage",
     "+checkRotation(image: BufferedImage): BufferedImage",
     "+checkShadow(image: BufferedImage): BufferedImage");

  ClassName.ImageProcessing("ImageProcessing");

  leftToRight(30)(ImproveImage, ImageProcessing);

  drawObjects(ImproveImage, ImageProcessing);

  clink(dependency)(ImproveImage, ImageProcessing);
endfig;

beginfig(127);
  save
    ImageProcessing;

  Class.ImageProcessing("ImageProcessing")
    ()
    ("+blur_gaussian(image: BufferedImage): BufferedImage",
     "+blur_mean(image: BufferedImage): BufferedImage",
     "+brighten_linear(image: BufferedImage): BufferedImage",
     "+brighten_quadratic(image: BufferedImage): BufferedImage",
     "+cutoffRectangle(image: BufferedImage, x: int, y: int, width: int, height: int): BufferedImage",
     "+edgeDetect(image: BufferedImage): BufferedImage",
     "+findShades(image: BufferedImage): BufferedImage",
     "+giveHistogram(image: BufferedImage): BufferedImage",
     "+hasShade(image: BufferedImage, exactness: int): BufferedImage",
     "+interpolate(image: BufferedImage, rect: Point[4], brightness: float): BufferedImage",
     "+isBlurry(image: BufferedImage): int",
     "+isDark(image: BufferedImage): int",
     "+isRotated(image: BufferedImage): int",
     "+isSlope(image: BufferedImage): Integer",
     "+negativ(image: BufferedImage): BufferedImage",
     "+rotate(image: BufferedImage): BufferedImage",
     "+sharpen(image: BufferedImage): BufferedImage");

  drawObjects(ImageProcessing);
endfig;

beginfig(128);
  save
    ConnectedComponentsLabeler,
    UnionFind;

  Class.ConnectedComponentsLabeler("ConnectedComponentsLabeler")
    ()
    ("+process(pixels: int[*], width: int, height: int, backgroundValue: int = 0): int[*]");

  Class.UnionFind("UnionFind")
    ()
    ("+UnionFind(size: int)",
     "+find(i: int): int",
     "+getSize(): int",
     "+union(i: int, j: int)");

  topToBottom(20)(
    ConnectedComponentsLabeler,
    UnionFind);

  drawObjects(
    ConnectedComponentsLabeler,
    UnionFind);

  clink(dependency)(ConnectedComponentsLabeler, UnionFind);
endfig;

beginfig(129);
  save
    RectangularDilationOperator,
    DilationOperator;

  Class.RectangularDilationOperator("RectangularDilationOperator")
    ()
    ("+DilationOperator(size: int)",
     "+DilationOperator(width: int, height: int)",
     "+apply(pixels: int[*], width: int, height: int): int[*]",
     "+getWindowHeight(): int",
     "+getWindowWidth(): int");

  ClassName.DilationOperator("DilationOperator");
  classStereotypes.DilationOperator("<<interface>>");
  DilationOperator.info.iName.top := 0;

  topToBottom(20)(
    DilationOperator,
    RectangularDilationOperator);

  drawObjects(
    RectangularDilationOperator,
    DilationOperator);

  clink(realization)(RectangularDilationOperator, DilationOperator);
endfig;

beginfig(130);
  save
    ScalingOperator;

  Class.ScalingOperator("ScalingOperator")
    ()
    ("+apply(image: BufferedImage, width: int): BufferedImage");

  drawObjects(ScalingOperator);
endfig;

beginfig(131);
  save
    ConvexHullAlgorithm,
    Point;

  Class.ConvexHullAlgorithm("ConvexHullAlgorithm")
    ()
    ("+computeConvexHull(points: Point[*]): Point[*]");

  ClassName.Point("Point");

  leftToRight(30)(ConvexHullAlgorithm, Point);

  drawObjects(ConvexHullAlgorithm, Point);

  clink(dependency)(ConvexHullAlgorithm, Point);
endfig;

beginfig(132);
  save
    Point,
    Vector;

  Class.Point("Point")
    ()
    ("+Point(x: double, y: double)",
     "+getX(): double",
     "+getY(): double",
     "+translate(v: Vector): Point");

  ClassName.Vector("Vector");

  leftToRight(30)(Point, Vector);

  drawObjects(Point, Vector);

  clink(dependency)(Point, Vector);
endfig;

beginfig(133);
  save
    Vector,
    Point;

  Class.Vector("Vector")
    ()
    ("+Vector(x: double, y: double)",
     "+Vector(p: Point, q: Point)",
     "+computeAngle(v: Vector): double",
     "+getLength(): double",
     "+getX(): double",
     "+getY(): double");

  ClassName.Point("Point");

  leftToRight(30)(Vector, Point);

  drawObjects(Vector, Point);

  clink(dependency)(Vector, Point);
endfig;

beginfig(134);
  save
    OpenCvDeviceFrameReader,
    OpenCvFrameReader,
    JavaCV;

  Class.OpenCvDeviceFrameReader("OpenCvDeviceFrameReader")
    ()
    ("#OpenCvDeviceFrameReader(deviceNumber: int, frameGrabber: OpenCVFrameGrabber)",
     "+getDeviceNumber(): int",
     "+hasMoreFrames(): boolean",
     "+open(deviceNumber: int): OpenCvDeviceFrameReader");

  ClassName.OpenCvFrameReader("OpenCvFrameReader {abstract}");

  Package.JavaCV("com.googlecode.javacv")();

  topToBottom(20)(
    OpenCvFrameReader,
    OpenCvDeviceFrameReader,
    JavaCV);

  drawObjects(
    OpenCvDeviceFrameReader,
    OpenCvFrameReader,
    JavaCV);

  clink(inheritance)(OpenCvDeviceFrameReader, OpenCvFrameReader);
  clink(dependency)(OpenCvDeviceFrameReader, JavaCV);

  draw OpenCvDeviceFrameReader.sw+(13,3) -- OpenCvDeviceFrameReader.se+(-139,3);
endfig;

beginfig(135);
  save
    OpenCvFileFrameReader,
    OpenCvFrameReader,
    SeekableFrameReader,
    JavaCV,
    G;

  Class.OpenCvFileFrameReader("OpenCvFileFrameReader")
    ()
    ("#OpenCvFileFrameReader(url: URL, frameGrabber: OpenCVFrameGrabber)",
     "+getUrl(): URL",
     "+hasMoreFrames(): boolean",
     "+setFrameNumber(frameNumber: int)",
     "+skipFrames(count: int)",
     "+open(url: URL): OpenCvFileFrameReader");

  ClassName.OpenCvFrameReader("OpenCvFrameReader {abstract}");

  ClassName.SeekableFrameReader("SeekableFrameReader");
  classStereotypes.SeekableFrameReader("<<interface>>");
  SeekableFrameReader.info.iName.top := 0;

  Package.JavaCV("com.googlecode.javacv")();

  Group.G(OpenCvFrameReader, SeekableFrameReader);

  topToBottom(20)(
    G,
    OpenCvFileFrameReader,
    JavaCV);

  leftToRight(30)(OpenCvFrameReader, SeekableFrameReader);

  drawObjects(
    OpenCvFileFrameReader,
    G,
    JavaCV);

  link(inheritance)(rpathVertical(OpenCvFrameReader.s, OpenCvFileFrameReader.top));
  link(realization)(rpathVertical(SeekableFrameReader.s, OpenCvFileFrameReader.top));
  clink(dependency)(OpenCvFileFrameReader, JavaCV);

  draw OpenCvFileFrameReader.sw+(13,3) -- OpenCvFileFrameReader.se+(-139,3);
endfig;

beginfig(136);
  save
    OpenCvFrameReader,
    FrameReader,
    JavaCV;

  Class.OpenCvFrameReader("OpenCvFrameReader {abstract}")
    ("#frameGrabber: OpenCVFrameGrabber")
    ("#OpenCvFrameReader(frameGrabber: OpenCVFrameGrabber)",
     "+getCurrentFrameNumber(): int",
     "+getHeight(): int",
     "+getNextFrame(): BufferedImage",
     "+getWidth(): int");

  ClassName.FrameReader("FrameReader");
  classStereotypes.FrameReader("<<interface>>");
  FrameReader.info.iName.top := 0;

  Package.JavaCV("com.googlecode.javacv")();

  topToBottom(20)(
    FrameReader,
    OpenCvFrameReader,
    JavaCV);

  drawObjects(
    OpenCvFrameReader,
    FrameReader,
    JavaCV);

  clink(realization)(OpenCvFrameReader, FrameReader);
  clink(dependency)(OpenCvFrameReader, JavaCV);
endfig;

beginfig(137);
  save
    SeekableFrameReader,
    FrameReader;

  Interface.SeekableFrameReader("SeekableFrameReader")
    ("+getLengthInFrames(): int",
     "+setFrameNumber(frameNumber: int)",
     "+skipFrames(count: int)");

  classStereotypes.SeekableFrameReader("<<interface>>");

  Interface.FrameReader("FrameReader")
    ("+getCurrentFrameNumber(): int",
     "+getHeight(): int",
     "+getNextFrame(): BufferedImage",
     "+getWidth(): int",
     "+hasMoreFrames(): boolean");

  classStereotypes.FrameReader("<<interface>>");

  leftToRight(20)(FrameReader, SeekableFrameReader);

  drawObjects(
    SeekableFrameReader,
    FrameReader);

  clink(inheritance)(SeekableFrameReader, FrameReader);
endfig;

beginfig(138);
  save
    Serializer,
    SerializerTemplate;

  Interface.Serializer("Serializer")
    ("+serialize(model: T, out: OutputStream)",
     "+serialize(model: T, out: Writer)",
     "+unserialize(in: InputStream): T",
     "+unserialize(in: Reader): T");

  classStereotypes.Serializer("<<interface>>");

  ClassTemplate.SerializerTemplate("T")(Serializer);

  drawObjects(Serializer, SerializerTemplate);
endfig;

beginfig(139);
  save
    XmlSerializer,
    XmlSerializerTemplate,
    Serializer,
    SerializerTemplate,
    XmlSerializerValidationError,
    JavaXml,
    DOM,
    G;

  Class.XmlSerializer("XmlSerializer {abstract}")
    ()
    ("+XmlSerializer(contextPath: string)",
     "+appendTo(model: T, node: Node)",
     "#createRootElement(model: T): JAXBElement<E> {abstract}",
     "+extractFrom(node: Node): T",
     "+getIncludeSchemaLocation(): boolean",
     "+getIncludeXmlDeclaration(): boolean",
     "+getPretty(): boolean",
     "+getSchema(): Schema",
     "+getSchemaLocation(): URL",
     "+getUrlContext(): URL",
     "+getValidate(): boolean",
     "#relativizeUrl(url: URL): URL",
     "#resolveUrl(uri: URI): URL",
     "#resolveUrl(url: URL): URL",
     "#restoreT(element: JAXBElement<E>): T {abstract}",
     "+serialize(model: T, out: OutputStream)",
     "+serialize(model: T, out: Writer)",
     "+setIncludeSchemaLocation(include: boolean)",
     "+setIncludeXmlDeclaration(include: boolean)",
     "+setPretty(pretty: boolean)",
     "+setSchema(schema: Schema)",
     "+setSchemaLocation(url: URL, useSchema: boolean = true)",
     "+setUrlContext(context: URL)",
     "+setValidate(validate: boolean)",
     "+unserialize(in: InputStream): T",
     "+unserialize(in: Reader): T",
     "#uri2url(uri: URI): URL",
     "#url2uri(url: URL): URI");

  ClassName.Serializer("Serializer");

  ClassName.XmlSerializerValidationError("XmlSerializerValidationError");

  Package.JavaXml("javax.xml")();
  Package.DOM("org.w3c.dom")();

  Group.G(XmlSerializerValidationError, JavaXml, DOM);

  ClassTemplate.XmlSerializerTemplate("T, E")(XmlSerializer);
  ClassTemplate.SerializerTemplate("T")(Serializer);

  topToBottom(20)(
    Serializer,
    XmlSerializer,
    G);

  leftToRight(20)(XmlSerializerValidationError, JavaXml, DOM);

  drawObjects(
    XmlSerializer,
    XmlSerializerTemplate,
    Serializer,
    SerializerTemplate,
    G);

  clink(realization)(XmlSerializer, Serializer);
  link(dependency)(rpathVertical(XmlSerializerValidationError.n, XmlSerializer.bottom));
  link(dependency)(rpathVertical(JavaXml.n, XmlSerializer.bottom));
  link(dependency)(rpathVertical(DOM.n, XmlSerializer.bottom));
endfig;

beginfig(140);
  save
    XmlJobSerializer,
    XmlSerializer,
    Job,
    Bindings,
    BindingsNote,
    G;

  Class.XmlJobSerializer("XmlJobSerializer")
    ()
    ("+XmlJobSerializer()",
     "#createRootElement(job: Job): JAXBElement<JobElement>",
     "#restoreModel(element: JAXBElement<JobElement>): Job");

  ClassName.XmlSerializer("XmlSerializer {abstract}");
  ClassName.Job("Job");

  Package.Bindings("de.tu_chemnitz.mi.barcd.xml.binding")();
  Note.BindingsNote("mit JAXB, auf Basis des XML Schemas,", "automatisch erstellte Klassen");

  Group.G(Job, Bindings);

  topToBottom(30)(XmlSerializer, XmlJobSerializer);
  topToBottom(20)(XmlJobSerializer, G);
  leftToRight(30)(Job, Bindings);
  topToBottom.right(10)(Bindings, BindingsNote);

  drawObjects(XmlJobSerializer, XmlSerializer, G, BindingsNote);

  clink(inheritance)(XmlJobSerializer, XmlSerializer);
  link(dependency)(rpathVertical(Job.n, XmlJobSerializer.bottom));
  link(dependency)(rpathVertical(Bindings.n, XmlJobSerializer.bottom));
  link(dashedLink)(rpathVertical(BindingsNote.n, Bindings.bottom));

  item(iStereo)("<<bind>>")(obj.sw = .5[XmlSerializer.s, XmlJobSerializer.n]);
  item(iStereo)("<T -> Job, E -> JobElement>")(obj.nw = .5[XmlSerializer.s, XmlJobSerializer.n]);
endfig;

beginfig(141);
  save
    XmlExtractionSerializer,
    XmlSerializer,
    Extraction,
    Bindings,
    BindingsNote,
    G;

  Class.XmlExtractionSerializer("XmlExtractionSerializer")
    ()
    ("+XmlExtractionSerializer()",
     "#createRootElement(frame: Extraction): JAXBElement<ExtractionElement>",
     "#restoreModel(element: JAXBElement<ExtractionElement>): Extraction");

  ClassName.XmlSerializer("XmlSerializer {abstract}");
  ClassName.Extraction("Extraction");

  Package.Bindings("de.tu_chemnitz.mi.barcd.xml.binding")();
  Note.BindingsNote("mit JAXB, auf Basis des XML Schemas,", "automatisch erstellte Klassen");

  Group.G(Extraction, Bindings);

  topToBottom(30)(XmlSerializer, XmlExtractionSerializer);
  topToBottom(20)(XmlExtractionSerializer, G);
  leftToRight(30)(Extraction, Bindings);
  topToBottom.right(10)(Bindings, BindingsNote);

  drawObjects(XmlExtractionSerializer, XmlSerializer, G, BindingsNote);

  clink(inheritance)(XmlExtractionSerializer, XmlSerializer);
  link(dependency)(rpathVertical(Extraction.n, XmlExtractionSerializer.bottom));
  link(dependency)(rpathVertical(Bindings.n, XmlExtractionSerializer.bottom));
  link(dashedLink)(rpathVertical(BindingsNote.n, Bindings.bottom));

  item(iStereo)("<<bind>>")(obj.sw = .5[XmlSerializer.s, XmlExtractionSerializer.n]);
  item(iStereo)("<T -> Extraction, E -> ExtractionElement>")(obj.nw = .5[XmlSerializer.s, XmlExtractionSerializer.n]);
endfig;

beginfig(142);
  save
    XmlSerializerValidationError;

  Class.XmlSerializerValidationError("XmlSerializerValidationError")
    ()
    ("+XmlSerializerValidationError(message: string, line: int, column: int)",
     "+getColumn(): int",
     "+getLine(): int",
     "+getMessage(): string");

  drawObjects(XmlSerializerValidationError);
endfig;

beginfig(143);
  save
    TemplatedUrlSequence,
    TemplatedUrlSequenceIterator,
    Range,
    G;

  Class.TemplatedUrlSequence("TemplatedUrlSequence")
    ()
    ("+TemplatedUrlSequence(template: URL, tag: string, range: Range, padding: int",
     "+getPadding(): int",
     "+getRange(): Range",
     "+getTag(): string",
     "+getTemplate(): URL",
     "+getUrl(index: int): URL",
     "+iterator(): TemplatedUrlSequenceIterator");

  Class.TemplatedUrlSequenceIterator("TemplatedUrlSequenceIterator")
    ()
    ("+TemplatedUrlSequenceIterator(sequence: TemplatedUrlSequence)",
     "+hasNext(): boolean",
     "+next(): URL");

  ClassName.Range("Range");

  Group.G(
    TemplatedUrlSequenceIterator,
    Range);

  topToBottom(20)(
    TemplatedUrlSequence,
    G);

  leftToRight(20)(Range, TemplatedUrlSequenceIterator);

  drawObjects(
    TemplatedUrlSequence,
    G);

  link(dependency)(rpathVertical(TemplatedUrlSequenceIterator.n, TemplatedUrlSequence.bottom));
  link(associationUni)(rpathVertical(Range.n, TemplatedUrlSequence.bottom));

  item(iStereo)("<<create>>")(obj.nw = TemplatedUrlSequenceIterator.n + (0, 20));
endfig;

beginfig(144);
  save
    Range,
    RangeIterator;

  Class.Range("Range")
    ()
    ("+Range(start: long, end: long, step: long)",
     "+Range(start: long, end: long)",
     "+get(index: long): long",
     "+getEnd(): long",
     "+getLength(): long",
     "+getStart(): long",
     "+getStep(): long",
     "+iterator(): RangeIterator");

  Class.RangeIterator("RangeIterator")
    ()
    ("+RangeIterator(range: Range)",
     "+hasNext(): boolean",
     "+next(): long");

  topToBottom(20)(
    Range,
    RangeIterator);

  drawObjects(
    Range,
    RangeIterator);

  clink(dependency)(Range, RangeIterator);

  item(iStereo)("<<create>>")(obj.nw = Range.s);
endfig;

beginfig(145);
  save
    DilationOperator;

  Interface.DilationOperator("DilationOperator")
    ("+apply(pixels: int[*], width: int, height: int): int[*]");

  classStereotypes.DilationOperator("<<interface>>");

  drawObjects(DilationOperator);
endfig;

end

