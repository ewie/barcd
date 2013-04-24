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

  Extractor.nw = (0, 0);

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

beginfig(11);
  save X;

  Class.X("Barcode")
    ("-anchorPoints: Point[0..*]",
     "-bytes: byte[0..*]",
     "-text: string",
     "-type: BarcodeType")
    ("+Barcode(type: BarcodeType, text: String, bytes: byte[0..*], anchorPoints: Point[0..*])",
     "+getAnchorPoints(): Point[0..*]",
     "+getBytes(): byte[0..*]",
     "+getText(): string",
     "+getType(): BarcodeType");

  drawObjects(X);
endfig;

beginfig(12);
  save I;

  Interface.I("BarcodeReader")
    ("+read(image: BufferedImage): Barcode",
     "+readMultiple(image: BufferedImage): Barcode[0..*]");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(13);
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

beginfig(14);
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

beginfig(15);
  save X;

  Class.X("DefaultGrayscaler")
    ()
    ("+convertToGrayscale(image: BufferedImage): BufferedImage");

  drawObjects(X);
endfig;

beginfig(16);
  save X;

  Class.X("DefaultImageEnhancer")
    ("-improve: ImproveImage")
    ("+enhanceImage(image: BufferedImage): BufferedImage");

  drawObjects(X);
endfig;

beginfig(17);
  save X;

  Class.X("DefaultRegionExtractor")
    ()
    ("-createRegions(pixels: int[0..*], width: int, height: int, scale: double)",
     "-extractEdges(raster: Raster): int[0..*]",
     "+extractRegions(image: BufferedImage): Region[0..*]",
     "-performSegmentation(pixels: int[0..*], width: int, height: int)");

  drawObjects(X);
endfig;

beginfig(18);
  save X;

  Class.X("DefaultRegionSelector")
    ()
    ("+selectRegion(region: Region): boolean");

  drawObjects(X);
endfig;

beginfig(19);
  save X;

  Class.X("Exractor")
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

  drawObjects(X);
endfig;

beginfig(20);
  save X;

  Class.X("Frame")
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

  drawObjects(X);
endfig;

beginfig(21);
  save I;

  Interface.I("FrameHandler")
    ("+handleFrame(image: BufferedImage): BufferedImage");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(22);
  save I;

  Interface.I("Grayscaler")
    ("+convertToGrayscale(image: BufferedImage): BufferedImage");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(23);
  save I;

  Interface.I("ImageEnhancer")
    ("+enhanceImage(): BufferedImage");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(24);
  save I;

  Interface.I("ImageProvider")
    ("+consume(): BufferedImage",
     "+hasMore(): boolean");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(25);
  save X;

  Class.X("Job")
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

  drawObjects(X);
endfig;

beginfig(26);
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

beginfig(27);
  save I;

  Interface.I("RegionSelector")
    ("+selectRegion(region: Region): boolean");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(28);
  save I;

  Interface.I("RegionExtractor")
    ("+extractRegions(image: BufferedImage): Region[0..*]");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(29);
  save I;

  Interface.I("Source")
    ("+createImageProvider(): ImageProvider",
     "+createImageProvider(frameNumber: int): ImageProvider");

  classStereotypes.I("<<interface>>");

  drawObjects(I);
endfig;

beginfig(30);
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

