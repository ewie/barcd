input metauml;

iClassNameOnly.iName.top := 3;
iClassNameOnly.iName.bottom := 5;

%iClass.foreColor := (.95, .95, .95);
%iClassNameOnly.foreColor := (.95, .95, .95);
%iInterface.foreColor := (.95, .95, .95);
%iPackage.foreColor := (.95, .95, .95);

beginfig(0);
  save Barcode,
       Extractor,
       Frame,
       FrameHandler,
       ImageProvider,
       ImproveImage,
       Job,
       Region,
       Source;

  ClassName.Barcode("Barcode");
  ClassName.Extractor("Extractor");
  ClassName.Frame("Frame");
  ClassName.FrameHandler("FrameHandler");
  ClassName.ImageProvider("ImageProvider");
  ClassName.Job("Job");
  ClassName.Region("Region");
  ClassName.Source("Source {abstract}");

  classStereotypes.FrameHandler("<<interface>>");
  FrameHandler.info.iName.top := 0;

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.iName.top := 0;

  topToBottom(30)(Job, Extractor);
  topToBottom(100)(Source, ImageProvider);

  leftToRight(30)(Source, Job);
  leftToRight(80)(Job, Frame);
  leftToRight(50)(ImageProvider, FrameHandler);

  Region.n = Frame.s + (-20, -30);
  Barcode.n = Region.s + (20, -30);

  drawObjects(
    Barcode,
    Extractor,
    Frame,
    FrameHandler,
    ImageProvider,
    Job,
    Region,
    Source);

  clink(dependency)(Extractor, ImageProvider);
  clink(dependency)(Source, ImageProvider);
  clink(dependency)(Extractor, Frame);
  clink(dependency)(Extractor, Job);
  clink(dependency)(Extractor, FrameHandler);
  clink(dependency)(Extractor, Barcode);
  clink(dependency)(Extractor, Region);
  link(dependency)(pathStepX(FrameHandler.e, Frame.e, 80));

  clink(aggregation)(Source, Job);
  clink(aggregation)(Frame, Job);
  link(aggregation)(pathVertical(Barcode.n + (10, 0), Frame.bottom));
  link(aggregation)(pathVertical(Barcode.n + (-10, 0), Region.bottom));
  link(aggregation)(pathVertical(Region.n + (10, 0), Frame.bottom));

  item(iStereo)("<<consume>>")(obj.w = .5[ImageProvider.s, Extractor.n]);
  item(iStereo)("<<notify>>")(obj.w = .5[Extractor.e, FrameHandler.w]);
  item(iStereo)("<<create>>")(obj.e = .5[ImageProvider.s, Source.n]);
  item(iStereo)("<<create>>")(obj.s = .5[Frame.s, Extractor.n]);
  item(iStereo)("<<create>>")(obj.s = .5[Extractor.n, Region.s]);
  item(iStereo)("<<create>>")(obj.w = .5[Extractor.n, Barcode.s]);

  item(iAssoc)("*")(obj.se = Frame.w);
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
    ("-images: BufferedSourcemage[*]")
    ("+BufferedImageSource(images: BufferedImage[*])",
     "+createImageProvider(frameNumber: int): BufferedImageProvider");

  ClassName.Source("Source {abstract}");
  ClassName.BufferedImageProvider("BufferedImageProvider");

  topToBottom(20)(Source, BufferedImageSource, BufferedImageProvider);

  drawObjects(Source, BufferedImageSource, BufferedImageProvider);

  clink(inheritance)(BufferedImageSource, Source);
  clink(dependency)(BufferedImageSource, BufferedImageProvider);

  item(iStereo)("<<create>>")(obj.w = .5[BufferedImageSource.s, BufferedImageProvider.n]);
endfig;

beginfig(10);
  save
    ImageCollectionSource,
    Source,
    RemoteImageProvider;

  Class.ImageCollectionSource("ImageCollectionSource")
    ("-urls: URL[*]")
    ("+ImageCollectionSource(urls: URL[*])",
     "+createImageProvider(frameNumber: int): RemoteImageProvider",
     "+getUrls(): URL[*]");

  ClassName.Source("Source {abstract}");
  ClassName.RemoteImageProvider("RemoteImageProvider");

  topToBottom(20)(Source, ImageCollectionSource, RemoteImageProvider);

  drawObjects(Source, ImageCollectionSource, RemoteImageProvider);

  clink(inheritance)(ImageCollectionSource, Source);
  clink(dependency)(ImageCollectionSource, RemoteImageProvider);

  item(iStereo)("<<create>>")(obj.w = .5[ImageCollectionSource.s, RemoteImageProvider.n]);
endfig;

beginfig(11);
  save
    ImageSequenceSource,
    Source,
    RemoteImageProvider,
    TemplatedUrlSequence,
    G;

  Class.ImageSequenceSource("ImageSequenceSource")
    ("-sequence: TemplatedUrlSequence")
    ("+ImageSequenceSource(sequence: TemplatedUrlSequence)",
     "+createImageProvider(frameNumber: int): RemoteImageProvider",
     "+getUrlSequence(): TemplatedUrlSequence");

  ClassName.Source("Source {abstract}");
  ClassName.RemoteImageProvider("RemoteImageProvider");
  ClassName.TemplatedUrlSequence("TemplatedUrlSequence");

  Group.G(RemoteImageProvider, TemplatedUrlSequence);

  topToBottom(20)(Source, ImageSequenceSource, G);
  leftToRight(30)(TemplatedUrlSequence, RemoteImageProvider);

  drawObjects(Source, ImageSequenceSource, G);

  clink(inheritance)(ImageSequenceSource, Source);
  link(dependency)(rpathVertical(RemoteImageProvider.n, ImageSequenceSource.bottom));
  link(dependency)(rpathVertical(TemplatedUrlSequence.n, ImageSequenceSource.bottom));

  item(iStereo)("<<create>>")(obj.w = RemoteImageProvider.n + (0, 10));
endfig;

beginfig(12);
  save
    ImageSnapshotServiceSource,
    Source,
    RemoteImageProvider;

  Class.ImageSnapshotServiceSource("ImageSnapshotServiceSource")
    ("-url: URL")
    ("+ImageSnapshotServiceSource(url: URL)",
     "+createImageProvider(frameNumber: int): RemoteImageProvider",
     "+getUrl(): URL");

  ClassName.Source("Source {abstract}");
  ClassName.RemoteImageProvider("RemoteImageProvider");

  topToBottom(20)(Source, ImageSnapshotServiceSource, RemoteImageProvider);

  drawObjects(Source, ImageSnapshotServiceSource, RemoteImageProvider);

  clink(inheritance)(ImageSnapshotServiceSource, Source);
  clink(dependency)(ImageSnapshotServiceSource, RemoteImageProvider);

  item(iStereo)("<<create>>")(obj.w = .5[ImageSnapshotServiceSource.s, RemoteImageProvider.n]);
endfig;

beginfig(13);
  save
    VideoDeviceSource,
    Source,
    VideoImageProvider,
    OpenCvDeviceFrameReader,
    G;

  Class.VideoDeviceSource("VideoDeviceSource")
    ("-deviceId: int")
    ("+VideoDeviceSource(deviceId: int)",
     "+createImageProvider(frameNumber: int): VideoImageProvider",
     "+getDeviceId(): int");

  ClassName.Source("Source {abstract}");
  ClassName.VideoImageProvider("VideoImageProvider");
  ClassName.OpenCvDeviceFrameReader("OpenCvDeviceFrameReader");

  Group.G(VideoImageProvider, OpenCvDeviceFrameReader);

  topToBottom(20)(Source, VideoDeviceSource, G);
  leftToRight(30)(OpenCvDeviceFrameReader, VideoImageProvider);

  drawObjects(Source, VideoDeviceSource, G);

  clink(inheritance)(VideoDeviceSource, Source);
  link(dependency)(rpathVertical(VideoImageProvider.n, VideoDeviceSource.bottom));
  link(dependency)(rpathVertical(OpenCvDeviceFrameReader.n, VideoDeviceSource.bottom));

  item(iStereo)("<<create>>")(obj.w = VideoImageProvider.n + (0, 10));
  item(iStereo)("<<create>>")(obj.w = OpenCvDeviceFrameReader.n + (0, 10));
endfig;

beginfig(14);
  save
    VideoStreamSource,
    Source,
    VideoImageProvider,
    OpenCvFileFrameReader,
    G;

  Class.VideoStreamSource("VideoStreamSource")
    ("-url: URL")
    ("+VideoStreamSource(url: URL)",
     "+createImageProvider(frameNumber: int): VideoImageProvider",
     "+getUrl(): URL");

  ClassName.Source("Source {abstract}");
  ClassName.VideoImageProvider("VideoImageProvider");
  ClassName.OpenCvFileFrameReader("OpenCvFileFrameReader");

  Group.G(VideoImageProvider, OpenCvFileFrameReader);

  topToBottom(20)(Source, VideoStreamSource, G);
  leftToRight(30)(OpenCvFileFrameReader, VideoImageProvider);

  drawObjects(Source, VideoStreamSource, G);

  clink(inheritance)(VideoStreamSource, Source);
  link(dependency)(rpathVertical(VideoImageProvider.n, VideoStreamSource.bottom));
  link(dependency)(rpathVertical(OpenCvFileFrameReader.n, VideoStreamSource.bottom));

  item(iStereo)("<<create>>")(obj.w = VideoImageProvider.n + (0, 10));
  item(iStereo)("<<create>>")(obj.w = OpenCvFileFrameReader.n + (0, 10));
endfig;

beginfig(15);
  save
    BufferedImageProvider,
    ImageProvider;

  Class.BufferedImageProvider("BufferedImageProvider")
    ("-images: Iterator<BufferedImage>")
    ("+BufferedImageProvider(images: Iterator<BufferedImage>)",
     "+consume(): BufferedImage",
     "+hasMore(): boolean");

  ClassName.ImageProvider("ImageProvider");

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  topToBottom(20)(ImageProvider, BufferedImageProvider);

  drawObjects(ImageProvider, BufferedImageProvider);

  clink(realization)(BufferedImageProvider, ImageProvider);
endfig;

beginfig(16);
  save
    RemoteImageProvider,
    ImageProvider;

  Class.RemoteImageProvider("RemoteImageProvider")
    ("-urls: Iterator<URL>")
    ("+RemoteImageProvider(urls: Iterator<URL>)",
     "+consume(): BufferedImage",
     "+hasMore(): boolean");

  ClassName.ImageProvider("ImageProvider");

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  topToBottom(20)(ImageProvider, RemoteImageProvider);

  drawObjects(ImageProvider, RemoteImageProvider);

  clink(realization)(RemoteImageProvider, ImageProvider);
endfig;

beginfig(17);
  save
    VideoImageProvider,
    ImageProvider,
    FrameReader;

  Class.VideoImageProvider("VideoImageProvider")
    ("-frameReader: FrameReader")
    ("+VideoImageProvider(frameReader: FrameReader)",
     "+consume(): BufferedImage",
     "+hasMore(): boolean");

  ClassName.ImageProvider("ImageProvider");
  ClassName.FrameReader("FrameReader");

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.info.iName.top := 0;

  classStereotypes.FrameReader("<<interface>>");
  FrameReader.info.iName.top := 0;

  topToBottom(20)(ImageProvider, VideoImageProvider, FrameReader);

  drawObjects(ImageProvider, VideoImageProvider, FrameReader);

  clink(realization)(VideoImageProvider, ImageProvider);
  clink(dependency)(VideoImageProvider, FrameReader);
endfig;

beginfig(101);
  save Barcode,
       BarcodeType,
       Point,
       G;

  Class.Barcode("Barcode")
    ("-anchorPoints: Point[*]",
     "-bytes: byte[*]",
     "-text: string",
     "-type: BarcodeType")
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

  leftToRight(30)(Point, BarcodeType);
  topToBottom(20)(Barcode, G);

  drawObjects(Barcode, G);

  link(dependency)(rpathVertical(Point.n, Barcode.bottom));
  link(dependency)(rpathVertical(BarcodeType.n, Barcode.bottom));
endfig;

beginfig(102);
  save BarcodeReader, Barcode;

  Interface.BarcodeReader("BarcodeReader")
    ("+read(image: BufferedImage): Barcode",
     "+readMultiple(image: BufferedImage): Barcode[*]");

  classStereotypes.BarcodeReader("<<interface>>");

  ClassName.Barcode("Barcode");

  leftToRight(50)(BarcodeReader, Barcode);

  drawObjects(BarcodeReader, Barcode);

  clink(dependency)(BarcodeReader, Barcode);

  item(iStereo)("<<create>>")(obj.s = .5[BarcodeReader.e, Barcode.w]);
endfig;

beginfig(103);
  save BarcodeType;

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
    ZXing,
    G;

  Class.DefaultBarcodeReader("DefaultBarcodeReader")
    ("-multiFormatReader: MultiFormatReader",
     "-multipleBarcodeReader: MultipleBarcodeReader")
    ("-createBitmap(image: BufferedImage): BinaryBitmap",
     "+read(image: BufferedImage): Barcode",
     "+readMultiple(image: BufferedImage): Barcode[*]",
     "-translateBarcodeFormat(format: BarcodeFormat): BarcodeType",
     "-translateResult(result: Result): Barcode",
     "-translateResultPoints(points: ResultPoint[*]): Point[*]",
     "-translateResults(results: Result[*]): Barcode[*]");

  ClassName.BarcodeReader("BarcodeReader");
  classStereotypes.BarcodeReader("<<interface>>");
  BarcodeReader.info.iName.top := 0;

  Package.ZXing("com.google.ZXing")();

  Group.G(BarcodeReader, ZXing);

  leftToRight(20)(DefaultBarcodeReader, G);
  topToBottom(30)(BarcodeReader, ZXing);

  drawObjects(DefaultBarcodeReader, G);

  link(realization)(rpathHorizontal(BarcodeReader.w, DefaultBarcodeReader.right));
  link(dependency)(rpathHorizontal(ZXing.w, DefaultBarcodeReader.right));
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

  leftToRight(30)(DefaultGrayscaler, Grayscaler);

  drawObjects(DefaultGrayscaler, Grayscaler);

  clink(realization)(DefaultGrayscaler, Grayscaler);
endfig;

beginfig(106);
  save
    DefaultImageEnhancer,
    ImageEnhancer,
    ImproveImage,
    G;

  Class.DefaultImageEnhancer("DefaultImageEnhancer")
    ("-improve: ImproveImage")
    ("+enhanceImage(image: BufferedImage): BufferedImage");

  ClassName.ImageEnhancer("ImageEnhancer");
  classStereotypes.ImageEnhancer("<<interface>>");
  ImageEnhancer.info.iName.top := 0;

  ClassName.ImproveImage("ImproveImage");

  Group.G(ImageEnhancer, ImproveImage);

  leftToRight(20)(DefaultImageEnhancer, G);
  topToBottom(10)(ImageEnhancer, ImproveImage);

  drawObjects(DefaultImageEnhancer, G);

  link(realization)(rpathHorizontal(ImageEnhancer.w, DefaultImageEnhancer.right));
  link(dependency)(rpathHorizontal(ImproveImage.w, DefaultImageEnhancer.right));
endfig;

beginfig(107);
  save
    DefaultRegionExtractor,
    Region,
    DilationOperator,
    ScalingOperator,
    G;

  Class.DefaultRegionExtractor("DefaultRegionExtractor")
    ("-gx: ConvolveOp",
     "-gy: ConvolveOp",
     "-dilate: DilationOperator",
     "-scale: ScalingOperator")
    ("-createRegions(pixels: int[*], width: int, height: int, scale: double): Region[*]",
     "-extractEdges(raster: Raster): int[*]",
     "+extractRegions(image: BufferedImage): Region[*]",
     "-performSegmentation(pixels: int[*], width: int, height: int)");

  ClassName.Region("Region");
  ClassName.DilationOperator("DilationOperator");
  ClassName.ScalingOperator("ScalingOperator");
  ClassName.RegionExtractor("RegionExtractor");
  classStereotypes.RegionExtractor("<<interface>>");
  RegionExtractor.info.iName.top := 0;

  Group.G(Region, DilationOperator, ScalingOperator);

  topToBottom(20)(RegionExtractor, DefaultRegionExtractor, G);
  leftToRight(20)(Region, DilationOperator, ScalingOperator);

  drawObjects(DefaultRegionExtractor, RegionExtractor, G);

  link(realization)(rpathVertical(RegionExtractor.s, DefaultRegionExtractor.top));
  link(dependency)(rpathVertical(Region.n, DefaultRegionExtractor.bottom));
  link(dependency)(rpathVertical(DilationOperator.n, DefaultRegionExtractor.bottom));
  link(dependency)(rpathVertical(ScalingOperator.n, DefaultRegionExtractor.bottom));

  item(iStereo)("<<create>>")(obj.e = Region.n + (0, 15));
endfig;

beginfig(108);
  save
    DefaultRegionSelector,
    Region,
    RegionSelector;

  Class.DefaultRegionSelector("DefaultRegionSelector")
    ()
    ("+selectRegion(region: Region): boolean");

  ClassName.RegionSelector("RegionSelector");
  classStereotypes.RegionSelector("<<interface>>");
  RegionSelector.info.iName.top := 0;

  ClassName.Region("Region");

  leftToRight(30)(RegionSelector, DefaultRegionSelector, Region);

  drawObjects(DefaultRegionSelector, RegionSelector, Region);

  clink(realization)(DefaultRegionSelector, RegionSelector);
  clink(dependency)(DefaultRegionSelector, Region);
endfig;

beginfig(109);
  save Extractor,
       BarcodeReader,
       Grayscaler,
       ImageEnhancer,
       RegionExtractor,
       RegionSelector;

  Class.Extractor("Exractor")
    ("-enhancer: ImageEnhancer",
     "-frameHandler: FrameHandler",
     "-grayscaler: Grayscaler",
     "-imageProvider: ImageProvider",
     "-job: Job",
     "-reader: BarcodeReader",
     "-regionExtractor: RegionExtractor",
     "-regionSelector: RegionSelector")
    ("+Extractor(job: Job)",
     "+getJob(): Job",
     "+hasMoreImages(): boolean",
     "+processNextImage()",
     "-reportFrame(frame: Frame, image: BufferedImage)",
     "+setBarcodeReader(reader: BarcodeReader)",
     "+setFrameHandler(handler: FrameHandler)",
     "+setGrayscaler(grayscaler: Grayscaler)",
     "+setRegionExtractor(extractor: RegionExtractor)",
     "+setRegionSelector(selector: RegionSelector)");

  ClassName.BarcodeReader("BarcodeReader");
  ClassName.Grayscaler("Grayscaler");
  ClassName.ImageEnhancer("ImageEnhancer");
  ClassName.RegionExtractor("RegionExtractor");
  ClassName.RegionSelector("RegionSelector");

  classStereotypes.BarcodeReader("<<interface>>");
  BarcodeReader.info.iName.top := 0;

  classStereotypes.Grayscaler("<<interface>>");
  Grayscaler.info.iName.top := 0;

  classStereotypes.ImageEnhancer("<<interface>>");
  ImageEnhancer.info.iName.top := 0;

  classStereotypes.RegionExtractor("<<interface>>");
  RegionExtractor.info.iName.top := 0;

  classStereotypes.RegionSelector("<<interface>>");
  RegionSelector.info.iName.top := 0;

  leftToRight(30)(Extractor, ImageEnhancer);

  topToBottom(20)(
    BarcodeReader,
    Grayscaler,
    ImageEnhancer,
    RegionExtractor,
    RegionSelector);

  drawObjects(
    Extractor,
    BarcodeReader,
    Grayscaler,
    ImageEnhancer,
    RegionExtractor,
    RegionSelector);

  link(dependency)(rpathHorizontal(BarcodeReader.w, Extractor.right));
  link(dependency)(rpathHorizontal(Grayscaler.w, Extractor.right));
  link(dependency)(rpathHorizontal(ImageEnhancer.w, Extractor.right));
  link(dependency)(rpathHorizontal(RegionExtractor.w, Extractor.right));
  link(dependency)(rpathHorizontal(RegionSelector.w, Extractor.right));
endfig;

beginfig(110);
  save
    Frame;

  Class.Frame("Frame")
    ("-number: int",
     "-regionlessBarcodes: Barcode[*]",
     "-regions: Region[*]")
    ("+Frame(number: int)",
     "+Frame(number: int, regions: Region[*], barcodes: Barcode[*])",
     "+addRegion(region: Region)",
     "+addRegionlessBarcode(barcode: Barcode)",
     "+getBarcodes(): Barcode[*]",
     "+getNumber(): int",
     "+getRegionBarcodes(): Barcode[*]",
     "+getRegionlessBarcodes(): Barcode[*]",
     "+getRegions(): Region[*]");

  ClassName.Barcode("Barcode");
  ClassName.Region("Region");

  Group.G(Barcode, Region);

  topToBottom(30)(Region, Barcode);
  leftToRight(30)(Frame, G);

  drawObjects(Frame, G);

  link(aggregation)(pathHorizontal(Region.w, Frame.right));
  link(aggregation)(pathHorizontal(Barcode.w, Frame.right));

  item(iAssoc)("*")(obj.se = Region.w);
  item(iAssoc)("*")(obj.se = Barcode.w);
endfig;

beginfig(111);
  save
    FrameHandler,
    Frame;

  Interface.FrameHandler("FrameHandler")
    ("+handleFrame(frame: Frame, image: BufferedImage): BufferedImage");

  classStereotypes.FrameHandler("<<interface>>");

  ClassName.Frame("Frame");

  leftToRight(30)(FrameHandler, Frame);

  drawObjects(FrameHandler, Frame);

  clink(dependency)(FrameHandler, Frame);
endfig;

beginfig(112);
  save I;

  Interface.I("Grayscaler")
    ("+convertToGrayscale(image: BufferedImage): BufferedImage");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(113);
  save I;

  Interface.I("ImageEnhancer")
    ("+enhanceImage(): BufferedImage");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(114);
  save I;

  Interface.I("ImageProvider")
    ("+consume(): BufferedImage",
     "+hasMore(): boolean");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(115);
  save
    Job,
    Frame,
    Source,
    G;

  Class.Job("Job")
    ("-frames: Frame[*]",
     "-initialFrameNumber: int",
     "-nextFrameNumber: int",
     "-source: Source")
    ("+Job(source: Source, initialFrameNumber: int = 0)",
     "+createFrame(): Frame",
     "+createImageProvider(): ImageProvider",
     "+getFrames(): Frame[*]",
     "+getSource(): Source",
     "+nextFrameNumber(): int");

  ClassName.Frame("Frame");
  ClassName.Source("Source");

  classStereotypes.Source("<<interface>>");
  Source.info.iName.top := 0;

  Group.G(Frame, Source);
  topToBottom(30)(Source, Frame);

  leftToRight(30)(Job, G);

  drawObjects(Job, G);

  link(aggregation)(pathHorizontal(Frame.w, Job.right));
  link(aggregation)(pathHorizontal(Source.w, Job.right));

  item(iAssoc)("*")(obj.se = Frame.w);
endfig;

beginfig(116);
  save
    Region,
    Barcode,
    ConvexPolygon,
    AxisAlignedRectangle,
    OrientedRectangle,
    G,
    F;

  Class.Region("Region")
    ("-axisAlignedRectangle: AxisAlignedRectangle",
     "-barcode: Barcode",
     "-convexPolygon: ConvexPolygon",
     "-coverage: double",
     "-orientedRectangle: OrientedRectangle")
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

  Group.G(Barcode, ConvexPolygon);
  Group.F(AxisAlignedRectangle, OrientedRectangle);

  topToBottom.left(20)(Region, G);
  topToBottom.right(50)(Region, F);
  leftToRight(10)(Barcode, ConvexPolygon);
  leftToRight(10)(AxisAlignedRectangle, OrientedRectangle);

  drawObjects(Region, G, F);

  link(aggregation)(pathVertical(Barcode.n, Region.bottom));
  link(aggregation)(pathVertical(ConvexPolygon.n, Region.bottom));
  link(dependency)(rpathVertical(AxisAlignedRectangle.ne + (-10, 0), Region.bottom));
  link(dependency)(rpathVertical(OrientedRectangle.n, Region.bottom));

  item(iStereo)("<<create>>")(obj.w = AxisAlignedRectangle.ne + (-10, 20));
  item(iStereo)("<<create>>")(obj.w = OrientedRectangle.n + (0, 20));

  item(iAssoc)("0..1")(obj.se = Barcode.n);

  % mark static methods
  draw Region.sw+(13,13.5) -- Region.se+(-112,13.5);
  draw Region.sw+(13,3) -- Region.se+(-4,3);
endfig;

beginfig(117);
  save
    RegionSelector,
    Region;

  Interface.RegionSelector("RegionSelector")
    ("+selectRegion(region: Region): boolean");

  classStereotypes.RegionSelector("<<interface>>");

  ClassName.Region("Region");

  leftToRight(30)(RegionSelector, Region);

  drawObjects(RegionSelector, Region);

  clink(dependency)(RegionSelector, Region);
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

  item(iStereo)("<<create>>")(obj.s = .5[RegionExtractor.e, Region.w]);
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

  item(iStereo)("<<create>>")(obj.e = .5[Source.s, ImageProvider.n]);
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

  item(iStereo)("<<create>>")(obj.w = SourceFactory.s + (0, -10));

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
    ("-min: Point",
     "-max: Point")
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
  link(dependency)(rpathVertical(Point.n, AxisAlignedRectangle.bottom));

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
    ("-origin: Point",
     "-height: Vector",
     "-width: Vector")
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
  link(dependency)(rpathVertical(Point.n, OrientedRectangle.bottom));
  link(dependency)(rpathVertical(Vector.n, OrientedRectangle.bottom));

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

  clink(realization)(Rectangle, ConvexPolygon);
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
    ConvexPolygon;

  Class.GenericConvexPolygon("GenericConvexPolygon")
    ("-vertices: Point[*]")
    ("+GenericConvexPolygon(vertices: Point[*])",
     "+getVertices(): Point[*]",
     "+createFromConvexHull(point: Point[*]): GenericConvexPolygon");

  ClassName.ConvexPolygon("ConvexPolygon {abstract}");

  topToBottom(20)(ConvexPolygon, GenericConvexPolygon);

  drawObjects(GenericConvexPolygon, ConvexPolygon);

  clink(inheritance)(GenericConvexPolygon, ConvexPolygon);

  draw GenericConvexPolygon.sw+(13,3) -- GenericConvexPolygon.se+(-20,3);
endfig;

end

