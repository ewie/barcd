input metauml;

iClassNameOnly.iName.top := 3;
iClassNameOnly.iName.bottom := 5;

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
  ClassName.Source("Source");

  classStereotypes.FrameHandler("<<interface>>");
  FrameHandler.info.iName.top := 0;

  classStereotypes.ImageProvider("<<interface>>");
  ImageProvider.iName.top := 0;

  classStereotypes.Source("<<interface>>");
  Source.info.iName.top := 0;

  topToBottom(30)(Job, Extractor);
  topToBottom(100)(Source, ImageProvider);

  leftToRight(50)(Source, Job);
  leftToRight(100)(Job, Frame);
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
  link(dependency)(pathStepX(FrameHandler.e, Frame.e, 100));

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

  item(iAssoc)("0..*")(obj.sw = Job.e + (0, 5));
  item(iAssoc)("0..*")(obj.n = Frame.sw + (0, -2));
  item(iAssoc)("0..*")(obj.n = Frame.se + (0, -2));
  item(iAssoc)("0..1")(obj.n = Region.s + (0, -2));
endfig;

beginfig(1);
  save BarcodeReader,
       Extractor,
       Grayscaler,
       ImageEnhancer,
       RegionExtractor,
       RegionSelector;

  ClassName.BarcodeReader("BarcodeReader");
  ClassName.Extractor("Extractor");
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

  topToBottom(30)(Extractor, RegionSelector);

  leftToRight(5)(
    Grayscaler,
    RegionExtractor,
    RegionSelector,
    ImageEnhancer,
    BarcodeReader);

  drawObjects(
    BarcodeReader,
    Grayscaler,
    ImageEnhancer,
    RegionExtractor,
    RegionSelector,
    Extractor);

  link(dependency)(pathManhattanX(Extractor.w, Grayscaler.n));
  link(dependency)(pathManhattanX(Extractor.s, RegionExtractor.n));
  clink(dependency)(Extractor, RegionSelector);
  link(dependency)(pathManhattanX(Extractor.s, ImageEnhancer.n));
  link(dependency)(pathManhattanX(Extractor.e, BarcodeReader.n));
endfig;

beginfig(2);
  save I, X;

  ClassName.I("Grayscaler");
  ClassName.X("DefaultGrayscaler");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);

  drawObjects(I, X);

  clink(realization)(X, I);
endfig;

beginfig(3);
  save I, X,
       DilationOperator,
       ScalingOperator,
       ConnectedComponentsLabeler;

  ClassName.I("RegionExtractor");
  ClassName.X("DefaultRegionExtractor");
  ClassName.DilationOperator("DilationOperator");
  ClassName.ScalingOperator("ScalingOperator");
  ClassName.ConnectedComponentsLabeler("ConnectedComponentsLabeler");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X, DilationOperator);
  topToBottom(10)(DilationOperator, ScalingOperator, ConnectedComponentsLabeler);

  drawObjects(I, X, DilationOperator, ScalingOperator, ConnectedComponentsLabeler);

  clink(realization)(X, I);

  clink(dependency)(X, DilationOperator);
  link(dependency)(pathManhattanY(X.s + (30, 0), ScalingOperator.w));
  link(dependency)(pathManhattanY(X.s + (10, 0), ConnectedComponentsLabeler.w));

  %clink(dependency)(X, DilationOperator);
  %clink(dependency)(X, ScalingOperator);
  %clink(dependency)(X, ConnectedComponentsLabeler);
endfig;

beginfig(4);
  save I, X;

  ClassName.I("RegionSelector");
  ClassName.X("DefaultRegionSelector");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);

  drawObjects(I, X);

  clink(realization)(X, I);
endfig;

beginfig(5);
  save I, X,
       ImproveImage,
       ImageProcessing;

  ClassName.I("ImageEnhancer");
  ClassName.X("DefaultImageEnhancer");
  ClassName.ImproveImage("ImproveImage");
  ClassName.ImageProcessing("ImageProcessing");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X, ImproveImage);
  topToBottom(30)(ImproveImage, ImageProcessing);

  drawObjects(I, X, ImproveImage, ImageProcessing);

  clink(realization)(X, I);
  clink(dependency)(X, ImproveImage);
  clink(dependency)(ImproveImage, ImageProcessing);
endfig;

beginfig(6);
  save I, X, B, ZXing;

  ClassName.I("BarcodeReader");
  ClassName.X("DefaultBarcodeReader");
  ClassName.B("Barcode");

  Package.ZXing("com.google.zxing")();

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X, ZXing);
  topToBottom(30)(I, B);

  drawObjects(I, X, B, ZXing);

  clink(realization)(X, I);
  clink(dependency)(X, ZXing);
  clink(dependency)(I, B);

  item(iStereo)("<<create>>")(obj.e = .5[I.s, B.n]);
endfig;

beginfig(8);
  save BufferedImageSource,
       ImageCollectionSource,
       ImageSequenceSource,
       ImageSnapshotServiceSource,
       VideoDeviceSource,
       VideoStreamSource,
       Source;

  ClassName.Source("Source");

  ClassName.BufferedImageSource("BufferedImageSource");
  ClassName.ImageCollectionSource("ImageCollectionSource");
  ClassName.ImageSequenceSource("ImageSequenceSource");
  ClassName.ImageSnapshotServiceSource("ImageSnapshotServiceSource");
  ClassName.VideoDeviceSource("VideoDeviceSource");
  ClassName.VideoStreamSource("VideoStreamSource");

  classStereotypes.Source("<<interface>>");
  Source.info.iName.top := 0;

  Source.nw = (0, 0);

  leftToRight(50)(Source, ImageSequenceSource);
  topToBottom.left(0)(
    BufferedImageSource,
    ImageCollectionSource,
    ImageSequenceSource,
    ImageSnapshotServiceSource,
    VideoDeviceSource,
    VideoStreamSource);

  drawObjects(
    BufferedImageSource,
    ImageCollectionSource,
    ImageSequenceSource,
    ImageSnapshotServiceSource,
    VideoDeviceSource,
    VideoStreamSource,
    Source);

  clink(realization)(BufferedImageSource, Source);
  clink(realization)(ImageCollectionSource, Source);
  clink(realization)(ImageSequenceSource, Source);
  clink(realization)(ImageSnapshotServiceSource, Source);
  clink(realization)(VideoDeviceSource, Source);
  clink(realization)(VideoStreamSource, Source);
endfig;

beginfig(9);
  save I, X, Y;

  ClassName.I("Source");
  ClassName.X("BufferedImageSource");
  ClassName.Y("BufferedImageProvider");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);
  leftToRight(50)(X, Y);

  drawObjects(I, X, Y);

  clink(realization)(X, I);
  clink(dependency)(X, Y);

  item(iStereo)("<<create>>")(obj.s = .5[X.e, Y.w]);
endfig;

beginfig(10);
  save I, X, Y;

  ClassName.I("Source");
  ClassName.X("ImageCollectionSource");
  ClassName.Y("RemoteImageProvider");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);
  leftToRight(50)(X, Y);

  drawObjects(I, X, Y);

  clink(realization)(X, I);
  clink(dependency)(X, Y);

  item(iStereo)("<<create>>")(obj.s = .5[X.e, Y.w]);
endfig;

beginfig(11);
  save I, X, Y;

  ClassName.I("Source");
  ClassName.X("ImageSequenceSource");
  ClassName.Y("RemoteImageProvider");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);
  leftToRight(50)(X, Y);

  drawObjects(I, X, Y);

  clink(realization)(X, I);
  clink(dependency)(X, Y);

  item(iStereo)("<<create>>")(obj.s = .5[X.e, Y.w]);
endfig;

beginfig(12);
  save I, X, Y;

  ClassName.I("Source");
  ClassName.X("ImageSnapshotServiceSource");
  ClassName.Y("RemoteImageProvider");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);
  leftToRight(50)(X, Y);

  drawObjects(I, X, Y);

  clink(realization)(X, I);
  clink(dependency)(X, Y);

  item(iStereo)("<<create>>")(obj.s = .5[X.e, Y.w]);
endfig;

beginfig(13);
  save I, X, Y, Z;

  ClassName.I("Source");
  ClassName.X("VideoDeviceSource");
  ClassName.Y("VideoImageProvider");
  ClassName.Z("OpenCvDeviceFrameReader");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);
  leftToRight(50)(X, Y);
  topToBottom(30)(X, Z);

  drawObjects(I, X, Y, Z);

  clink(realization)(X, I);
  clink(dependency)(X, Y);
  clink(dependency)(X, Z);

  item(iStereo)("<<create>>")(obj.s = .5[X.e, Y.w]);
  item(iStereo)("<<create>>")(obj.w = .5[X.s, Z.n]);
endfig;

beginfig(14);
  save I, X, Y, Z;

  ClassName.I("Source");
  ClassName.X("VideoStreamSource");
  ClassName.Y("VideoImageProvider");
  ClassName.Z("OpenCvFileFrameReader");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);
  leftToRight(50)(X, Y);
  topToBottom(30)(X, Z);

  drawObjects(I, X, Y, Z);

  clink(realization)(X, I);
  clink(dependency)(X, Y);
  clink(dependency)(X, Z);

  item(iStereo)("<<create>>")(obj.s = .5[X.e, Y.w]);
  item(iStereo)("<<create>>")(obj.w = .5[X.s, Z.n]);
endfig;

beginfig(15);
  save I, X;

  ClassName.I("ImageProvider");
  ClassName.X("BufferedImageProvider");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);

  drawObjects(I, X);

  clink(realization)(X, I);
endfig;

beginfig(16);
  save I, X;

  ClassName.I("ImageProvider");
  ClassName.X("RemoteImageProvider");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  leftToRight(30)(I, X);

  drawObjects(I, X);

  clink(realization)(X, I);
endfig;

beginfig(17);
  save I, X, FrameReader;

  ClassName.I("ImageProvider");
  ClassName.X("VideoImageProvider");
  ClassName.FrameReader("FrameReader");

  classStereotypes.I("<<interface>>");
  I.info.iName.top := 0;

  classStereotypes.FrameReader("<<interface>>");
  FrameReader.info.iName.top := 0;

  leftToRight(30)(I, X, FrameReader);

  drawObjects(I, X, FrameReader);

  clink(realization)(X, I);
  clink(dependency)(X, FrameReader);
endfig;

beginfig(101);
  save Barcode,
       BarcodeType,
       Point,
       G;

  Class.Barcode("Barcode")
    ("-anchorPoints: Point[0..*]",
     "-bytes: byte[0..*]",
     "-text: string",
     "-type: BarcodeType")
    ("+Barcode(type: BarcodeType, text: string, bytes: byte[0..*], points: Point[0..*])",
     "+getAnchorPoints(): Point[0..*]",
     "+getBytes(): byte[0..*]",
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
  save I;

  Interface.I("BarcodeReader")
    ("+read(image: BufferedImage): Barcode",
     "+readMultiple(image: BufferedImage): Barcode[0..*]");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(103);
  save E;

  Class.E("BarcodeType")
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

  classStereotypes.E("<<enumeration>>");

  drawObjects(E);
endfig;

beginfig(104);
  save X;

  Class.X("DefaultBarcodeReader")
    ("-multiFormatReader: MultiFormatReader",
     "-multipleBarcodeReader: MultipleBarcodeReader")
    ("-createBitmap(image: BufferedImage): BinaryBitmap",
     "+read(image: BufferedImage): Barcode",
     "+readMultiple(image: BufferedImage): Barcode[0..*]",
     "-translateBarcodeFormat(format: BarcodeFormat): BarcodeType",
     "-translateResult(result: Result): Barcode",
     "-translateResultPoints(points: ResultPoint[0..*]): Point[0..*]",
     "-translateResults(results: Result[0..*]): Barcode[0..*]");

  drawObjects(X);
endfig;

beginfig(105);
  save X;

  Class.X("DefaultGrayscaler")
    ()
    ("+convertToGrayscale(image: BufferedImage): BufferedImage");

  drawObjects(X);
endfig;

beginfig(106);
  save X;

  Class.X("DefaultImageEnhancer")
    ("-improve: ImproveImage")
    ("+enhanceImage(image: BufferedImage): BufferedImage");

  drawObjects(X);
endfig;

beginfig(107);
  save X;

  Class.X("DefaultRegionExtractor")
    ()
    ("-createRegions(pixels: int[0..*], width: int, height: int, scale: double)",
     "-extractEdges(raster: Raster): int[0..*]",
     "+extractRegions(image: BufferedImage): Region[0..*]",
     "-performSegmentation(pixels: int[0..*], width: int, height: int)");

  drawObjects(X);
endfig;

beginfig(108);
  save X;

  Class.X("DefaultRegionSelector")
    ()
    ("+selectRegion(region: Region): boolean");

  drawObjects(X);
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
  save Frame;

  Class.Frame("Frame")
    ("-number: int",
     "-regionlessBarcodes: Barcode[0..*]",
     "-regions: Region[0..*]")
    ("+Frame(number: int)",
     "+Frame(number: int, regions: Region[0..*], barcodes: Barcode[0..*])",
     "+addRegion(region: Region)",
     "+addRegionlessBarcode(barcode: Barcode)",
     "+getBarcodes(): Barcode[0..*]",
     "+getNumber(): int",
     "+getRegionBarcodes(): Barcode[0..*]",
     "+getRegionlessBarcodes(): Barcode[0..*]",
     "+getRegions(): Region[0..*]");

  ClassName.Barcode("Barcode");
  ClassName.Region("Region");

  Group.G(Barcode, Region);

  leftToRight(30)(Region, Barcode);
  topToBottom(20)(Frame, G);

  drawObjects(Frame, G);


  link(dependency)(rpathVertical(Region.n, Frame.bottom));
  link(dependency)(rpathVertical(Barcode.n, Frame.bottom));
endfig;

beginfig(111);
  save FrameHandler, Frame;

  Interface.FrameHandler("FrameHandler")
    ("+handleFrame(frame: Frame, image: BufferedImage): BufferedImage");

  classStereotypes.FrameHandler("<<interface>>");

  ClassName.Frame("Frame");

  topToBottom(20)(FrameHandler, Frame);

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
  save Job, Frame, Source, G;

  Class.Job("Job")
    ("-frames: Frame[0..*]",
     "-initialFrameNumber: int",
     "-nextFrameNumber: int",
     "-source: Source")
    ("+Job(source: Source, initialFrameNumber: int = 0)",
     "+createFrame(): Frame",
     "+createImageProvider(): ImageProvider",
     "+getFrames(): Frame[0..*]",
     "+getSource(): Source",
     "+nextFrameNumber(): int");

  ClassName.Frame("Frame");
  ClassName.Source("Source");

  classStereotypes.Source("<<interface>>");
  Source.info.iName.top := 0;

  Group.G(Frame, Source);
  topToBottom(30)(Frame, Source);

  leftToRight(30)(Job, G);

  drawObjects(Job, G);

  link(aggregation)(rpathHorizontal(Frame.w, Job.right));
  link(aggregation)(rpathHorizontal(Source.w, Job.right));

  item(iAssoc)("0..*")(obj.se = Frame.w + (0, 5));
endfig;

beginfig(116);
  save X;

  Class.X("Region")
    ("-axisAlignedRectangle: AxisAlignedRectangle",
     "-barcode: Barcode",
     "-convexPolygon: ConvexPolygon",
     "-coverage: double",
     "-orientedRectangle: OrientedRectangle")
    ("+contains(point: Point): boolean",
     "+getAxisAlignedRectangle(): AxisAlignedRectangle",
     "+getBarcode(): Barcode",
     "+getConvexPolygon(): ConvexPolygon",
     "+getCoverage(): double",
     "+getOrientedRectangle(): OrientedRectangle",
     "+setBarcode(barcode: Barcode)",
     "+createFromPoints(points: Point[0..*])",
     "+createFromPoints(points: Point[0..*], generatingPointCount: int)");

  drawObjects(X);

  draw X.sw+(13,13.5) -- X.se+(-112,13.5);
  draw X.sw+(13,3) -- X.se+(-4,3);
endfig;

beginfig(117);
  save I;

  Interface.I("RegionSelector")
    ("+selectRegion(region: Region): boolean");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(118);
  save I;

  Interface.I("RegionExtractor")
    ("+extractRegions(image: BufferedImage): Region[0..*]");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(119);
  save I;

  Interface.I("Source")
    ("+createImageProvider(): ImageProvider",
     "+createImageProvider(frameNumber: int): ImageProvider");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(120);
  save X;

  Class.X("SourceFactory")
    ()
    ("+newBufferedImageSource(images: BufferedImage[0..*]): BufferedImageSource",
     "+newImageCollectionSource(urls: URL[0..*]): ImageCollectionSource",
     "+newImageSequenceSource(url: TemplatedUrlSequence): ImageSequenceSource",
     "+newImageSnapshotServiceSource(url: URL): ImageSnapshotServiceSource",
     "+newVideoDeviceSource(deviceId: int): VideoDeviceSource",
     "+newVideoStreamSource(url: URL): VideoStreamSource");

  drawObjects(X);

  draw X.sw+(13,55.) -- X.se+(-4,55.5);
  draw X.sw+(13,45) -- X.se+(-45,45);
  draw X.sw+(13,34.5) -- X.se+(-4,34.5);
  draw X.sw+(13,24) -- X.se+(-21,24);
  draw X.sw+(13,13.5) -- X.se+(-86,13.5);
  draw X.sw+(13,3) -- X.se+(-99,3);
endfig;

end

