package de.ckehl;


import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.events.PaintListener;
//import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.opengl.GLCanvas;
//import org.eclipse.swt.opengl.GLData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
//import org.eclipse.swt.widgets.Shell;



import javax.jnlp.*;
//import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.SWTResourceManager;
import org.omg.stub.java.rmi._Remote_Stub;

public class AppWindow {

	public static final int INTRO_STAGE = 0;
	public static final int STAGE_3D = 1;
	public static final int STAGE_AASLICE = 2;
	public static final int STAGE_COMPARATOR = 3;
	
	public static Shell shell = null;
	public static Shell getShell() { return shell; }
	
	//public static DataStorage _dataStorage = null;
	public static DataStorage _dataStorage = new DataStorage();
	public static DataStorage getDataStorage() { return _dataStorage; }
	public static DecimalFormat _vecStdFmt = new DecimalFormat( "#,###,###,##0.000" );
	
	protected Display display = null;
	protected SashForm form = null;
	protected SashForm top = null;
	protected SashForm bottom = null;
	protected int _curStage = 0;
	
	//--------- Intro View ------------//
	protected GraphView _IntroTopLeft = null;
	protected SliceView _IntroTopRight = null;
	protected SliceView _IntroBottomLeft = null;
	protected SliceView _IntroBottomRight = null;
	
	//----------- 3D View -------------//
	MenuItem cascadeRenderMenu = null;
	MenuItem higherResItem = null;
	MenuItem calculateNormalItem = null;
	MenuItem switchLightingItem = null;
	MenuItem switchNormalItem = null;
	protected int reductionNumber = 0;
	protected CubrilleView _3Drenderer = null;
	protected GraphView _rayInformation = null;
	protected TransferFunctionPresetView _TF = null;
	protected LightingView _LV = null;
	//protected 

	
	//--- Axes-Aligned Slice Views ----//
	protected GraphView _AASTopLeft = null;
	protected SliceView _AASTopRight = null;
	protected SliceView _AASBottomLeft = null;
	protected SliceView _AASBottomRight = null;
	
	//protected short[][][] _data;
	//protected float[] spacing = null;
	//protected short[] dimensions = null;
	protected boolean _initialised = false;

	/*
	 * Histogram parts
	 */
	//protected int[] _selectedHistogram = null;
	//protected int _selHistoMax, _selHistoMin;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		AppWindow window = null;
		try {
			window = new AppWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(window!=null)
				window.close();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = new Display();
		createContents();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void close()
	{
		if(display!=null)
		{
			display.dispose();
			disposeIntro();
			dispose3D();
			disposeAASlices();
			disposeComparator();
		}
	}
	
	protected void createShell()
	{
		shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setSize(800, 600);
		shell.setLayout(new GridLayout());
		shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		shell.setText("OpenGL Application");
	}
	
	protected void createForms()
	{
		if(bottom!=null)
		{
			bottom.dispose();
			bottom=null;
		}
		if(top!=null)
		{
			top.dispose();
			top=null;
		}
		if(form!=null)
		{
			form.dispose();
			form=null;
		}
		
		form = new SashForm(shell, SWT.VERTICAL);
		form.setLayout(new GridLayout(1, false));
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		

		top = new SashForm(form, SWT.HORIZONTAL);
		GridLayout top_layout = new GridLayout(2, false);
		top_layout.marginHeight = top_layout.marginWidth = 0;
		top.setLayout(top_layout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));


		bottom = new SashForm(form, SWT.HORIZONTAL);
		GridLayout bottom_layout = new GridLayout(2, false);
		bottom_layout.marginHeight = bottom_layout.marginWidth = 0;
		bottom.setLayout(bottom_layout);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		createShell();
		
		Menu menu = new Menu(shell, SWT.BAR);
		menu.setLocation(new Point(0, 0));
		MenuItem cascadeFileMenu = new MenuItem(menu, SWT.CASCADE);
		cascadeFileMenu.setText("&File");
		
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		cascadeFileMenu.setMenu(fileMenu);
		
		MenuItem openFileItem = new MenuItem(fileMenu, SWT.PUSH);
		openFileItem.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				//MessageBox choiceDLG = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
				//choiceDLG.setMessage("Which format do you choose ?");
				//choiceDLG.
				
		        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		        String[] filterNames = new String[] 
		            {"Ini files (*.ini)", "RMS ini files (*.rms)", "All Files (*)"};

		        String[] filterExtensions = new String[] 
		            {"*.ini", "*.rms", "*"};

		        dialog.setFilterNames(filterNames);
		        dialog.setFilterExtensions(filterExtensions);
		        String path = dialog.open();
		        
		        if (path != null) {
		        	_dataStorage = new DataStorage();
		        	if(path.contains("ini"))
		        	{
						LoadCT_Stefan reader = new LoadCT_Stefan();
						reader.setFilename(path);
						reader.read();
						Cubille.setMaxValue((short)4095);
						
						_dataStorage.setDimensions(reader.getDimensions());
						_dataStorage.setSpacing(reader.getSpacing());
						_dataStorage.setData(reader.getData());
		        	}
		        	else
		        	{
		        		LoadRoxarText reader = new LoadRoxarText();
						reader.setFilename(path);
						reader.read();
						Cubille.setMaxValue((short)4095);

						_dataStorage.setDimensions(reader.getDimensions());
						_dataStorage.setSpacing(reader.getSpacing());
						_dataStorage.setData(reader.getData());
		        	}
					
					
					System.out.printf("Spacing: %f,  %f, %f\n", _dataStorage.getSpacing()[0], _dataStorage.getSpacing()[1], _dataStorage.getSpacing()[2]);
					System.out.printf("Dimensions: %d, %d, %d\n", _dataStorage.getDimensions()[0], _dataStorage.getDimensions()[1], _dataStorage.getDimensions()[2]);
					_initialised = true;
					
					loadDataIntoCurrentUI();
		        }
			}
		});
		openFileItem.setText("&Open ...");
		
		MenuItem reduceGeometryItem = new MenuItem(fileMenu, SWT.PUSH);
		reduceGeometryItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				reductionNumber++;
				if(reductionNumber>0)
					higherResItem.setEnabled(true);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		reduceGeometryItem.setText("reduce data upfront");
		
		MenuItem cascadeViewMenu = new MenuItem(menu, SWT.CASCADE);
		cascadeViewMenu.setText("&View ...");
		
		Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
		cascadeViewMenu.setMenu(viewMenu);
		
		MenuItem globalViewItem = new MenuItem(viewMenu, SWT.PUSH);
		globalViewItem.setText("&global");
		globalViewItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				_curStage = INTRO_STAGE;
				createIntroUI();
				//shell.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		MenuItem view3DItem = new MenuItem(viewMenu, SWT.PUSH);
		view3DItem.setText("3&D");
		view3DItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				_curStage = STAGE_3D;
				create3DImagingUI();
				//shell.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		MenuItem axesSliceViews = new MenuItem(viewMenu, SWT.PUSH);
		axesSliceViews.setText("&Axes-aligned slices");
		axesSliceViews.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				_curStage = STAGE_AASLICE;
				createAxisAlignedSliceViews();
				//shell.redraw();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		/*
		MenuItem comparatorView = new MenuItem(viewMenu, SWT.PUSH);
		comparatorView.setText("dataset comparator");
		comparatorView.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				_curStage = STAGE_COMPARATOR;
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		*/
		
		MenuItem resetViewItem = new MenuItem(viewMenu, SWT.PUSH);
		resetViewItem.setText("Reset vie&w");
		resetViewItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				resetCurrentView();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		MenuItem backgroundColourSetupItem = new MenuItem(viewMenu, SWT.PUSH);
		backgroundColourSetupItem.setText("&Background colour");
		backgroundColourSetupItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				ColorDialog cd = new ColorDialog(shell);
				cd.setText("Color Selection");
				cd.setRGB(new RGB(0, 0, 0));
				RGB newColor = cd.open();
				if (newColor == null) {
					return;
				}
				else
				{
					switch (_curStage) {
					case INTRO_STAGE:
					{
						
						break;
					}
					case STAGE_3D:
					{
						_3Drenderer.setBackgroundColour(((float)newColor.red)/255.0f, ((float)newColor.green)/255.0f, ((float)newColor.blue)/255.0f);
						break;
					}
					case STAGE_AASLICE:
					{
						
						break;
					}
					case STAGE_COMPARATOR:
					{
						
						break;
					}
					default:
						break;
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		cascadeRenderMenu = new MenuItem(menu, SWT.CASCADE);
		cascadeRenderMenu.setText("&Rendering ...");
		
		Menu RenderMenu = new Menu(shell, SWT.DROP_DOWN);
		cascadeRenderMenu.setMenu(RenderMenu);
		
		MenuItem lowerResItem = new MenuItem(RenderMenu, SWT.PUSH);
		lowerResItem.setText("lower resolution");
		lowerResItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
					_3Drenderer.reduceGeometry();
					_3Drenderer.createGeometry();
					_3Drenderer.paint();
					_rayInformation.removeLastAdaptor();
					if(_3Drenderer.getHistrogramSelectionAdaptor()!=null)
						_rayInformation.addAdaptor(_3Drenderer.getHistrogramSelectionAdaptor());
					_rayInformation.setup();
					
					reductionNumber++;
					if(reductionNumber>0)
						higherResItem.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		higherResItem = new MenuItem(RenderMenu, SWT.PUSH);
		higherResItem.setText("higher resolution");
		higherResItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
					if(reductionNumber>0)
					{
						_3Drenderer.increaseGeometry();
						_3Drenderer.createGeometry();
						_3Drenderer.paint();
						_rayInformation.removeLastAdaptor();
						if(_3Drenderer.getHistrogramSelectionAdaptor()!=null)
							_rayInformation.addAdaptor(_3Drenderer.getHistrogramSelectionAdaptor());
						_rayInformation.setup();
					}
					
					reductionNumber = Math.max(reductionNumber-1, 0);
					if(reductionNumber==0)
						higherResItem.setEnabled(false);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		if(reductionNumber==0)
			higherResItem.setEnabled(false);
		
		calculateNormalItem = new MenuItem(RenderMenu, SWT.PUSH);
		calculateNormalItem.setText("calculate Normals");
		calculateNormalItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
					//_dataStorage.computeNormals();
					_dataStorage.computeNormalTexture();
					_3Drenderer.generateGradients();
					_rayInformation.resetView();
					calculateNormalItem.setEnabled(false);
					switchLightingItem.setEnabled(true);
					switchNormalItem.setEnabled(true);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		switchLightingItem = new MenuItem(RenderMenu, SWT.PUSH);
		switchLightingItem.setText("switch Light on/off");
		switchLightingItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
					_3Drenderer.switchLight();
					if(_3Drenderer.isLightOn())
					{
						_rayInformation.resetView();
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		switchLightingItem.setEnabled(false);
		
		switchNormalItem = new MenuItem(RenderMenu, SWT.PUSH);
		switchNormalItem.setText("show Normal Vis on/off");
		switchNormalItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
					_3Drenderer.ToggleNormalVis();
					if(_3Drenderer.isNormalVis())
					{
						_rayInformation.resetView();
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		switchNormalItem.setEnabled(false);
		
		MenuItem storePresets = new MenuItem(RenderMenu, SWT.PUSH);
		storePresets.setText("store current presets ...");
		storePresets.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
			        FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			        String[] filterNames = new String[] 
			            {"Preset files (*.preset)", "All Files (*)"};

			        String[] filterExtensions = new String[] 
			            {"*.preset", "*"};

			        dialog.setFilterNames(filterNames);
			        dialog.setFilterExtensions(filterExtensions);
			        String path = dialog.open();
			        if(path!=null)
			        {
			        	_TF.savePresets(path);
			        }
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		MenuItem loadPresets = new MenuItem(RenderMenu, SWT.PUSH);
		loadPresets.setText("load presets ...");
		loadPresets.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(_curStage==STAGE_3D)
				{
			        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			        String[] filterNames = new String[] 
			            {"Preset files (*.preset)", "All Files (*)"};

			        String[] filterExtensions = new String[] 
			            {"*.preset", "*"};

			        dialog.setFilterNames(filterNames);
			        dialog.setFilterExtensions(filterExtensions);
			        String path = dialog.open();
			        
			        if (path != null) {
			        	_TF.loadPresets(path);
			        }
			        dialog = null;
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		
		shell.setMenuBar(menu);
		createForms();

		cascadeRenderMenu.setEnabled(false);
	    createIntroUI();

		
		shell.open();
	}
	
	private void createIntroUI()
	{
		dispose3D();
		disposeAASlices();
		disposeComparator();
		createForms();
		
		
		/*
	    SashForm form = new SashForm(shell, SWT.VERTICAL);
	    form.setLayout(new GridLayout(1, false));
	    form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    SashForm top = new SashForm(form, SWT.HORIZONTAL);
	    GridLayout top_layout = new GridLayout(2, false);
	    top_layout.marginHeight = top_layout.marginWidth = 0;
	    top.setLayout(top_layout);
	    top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    
	    SashForm bottom = new SashForm(form, SWT.HORIZONTAL);
	    GridLayout bottom_layout = new GridLayout(2, false);
	    bottom_layout.marginHeight = bottom_layout.marginWidth = 0;
	    bottom.setLayout(bottom_layout);
	    bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    */
	    
		_IntroTopLeft = new GraphView(top, SWT.BORDER);
		_IntroTopLeft.setSize(320, 240);
		Layout _tlL = new FillLayout();
		GridData _tlLD = new GridData();
		_tlLD.horizontalAlignment = SWT.LEFT;
		_tlLD.verticalAlignment = SWT.TOP;
		_tlLD.horizontalSpan = 1;
		_tlLD.verticalSpan = 1;
		_IntroTopLeft.setLayout(_tlL);
		_IntroTopLeft.setLayoutData(_tlLD);
		_IntroTopLeft.DisableProfile();
		_IntroTopLeft.DisableSemivariograms();
		_IntroTopLeft.setup();
		
		_IntroTopRight = new SliceView(top, SWT.BORDER, Texture3DPane.XY_PANE, SliceView.TYPE_XRAY);
		_IntroTopRight.setSize(320, 240);
		Layout _trL = new FillLayout();
		GridData _trLD = new GridData();
		_trLD.horizontalAlignment = SWT.LEFT;
		_trLD.verticalAlignment = SWT.TOP;
		_trLD.horizontalSpan = 1;
		_trLD.verticalSpan = 1;
		_IntroTopRight.setLayout(_trL);
		_IntroTopRight.setLayoutData(_trLD);
		_IntroTopRight.setup();
		_IntroTopLeft.addAdaptor(_IntroTopRight);
		
		top.setWeights(new int[] {1, 1});
		
		_IntroBottomLeft = new SliceView(bottom, SWT.BORDER, Texture3DPane.XZ_PANE, SliceView.TYPE_XRAY, _IntroTopRight.getCanvasAsSharedHead());
		_IntroBottomLeft.setSize(320, 240);
		Layout _blL = new FillLayout();
		GridData _blLD = new GridData();
		_blLD.horizontalAlignment = SWT.LEFT;
		_blLD.verticalAlignment = SWT.TOP;
		_blLD.horizontalSpan = 1;
		_blLD.verticalSpan = 1;
		_IntroBottomLeft.setLayout(_blL);
		_IntroBottomLeft.setLayoutData(_blLD);
		_IntroBottomLeft.setup();
		_IntroTopLeft.addAdaptor(_IntroBottomLeft);
		
		_IntroBottomRight = new SliceView(bottom, SWT.BORDER, Texture3DPane.YZ_PANE, SliceView.TYPE_XRAY, _IntroTopRight.getCanvasAsSharedHead());
		_IntroBottomRight.setSize(320, 240);
		Layout _brL = new FillLayout();
		GridData _brLD = new GridData();
		_brLD.horizontalAlignment = SWT.LEFT;
		_brLD.verticalAlignment = SWT.TOP;
		_brLD.horizontalSpan = 1;
		_brLD.verticalSpan = 1;
		_IntroBottomRight.setLayout(_brL);
		_IntroBottomRight.setLayoutData(_brLD);
		_IntroBottomRight.setup();
		_IntroTopLeft.addAdaptor(_IntroBottomRight);
		
		//_IntroTopLeft.setDataHolder(_IntroTopRight);
		//_IntroTopLeft.setDataHolder(_dataStorage);
		
		bottom.setWeights(new int[] {1, 1});
		
		if(_initialised)
			loadDataIntoIntro();
		
		shell.layout();
		_IntroTopLeft.paint();
		_IntroTopRight.paint();
		_IntroBottomLeft.paint();
		_IntroBottomRight.paint();
	}
	
	private void create3DImagingUI()
	{
		disposeIntro();
		disposeAASlices();
		disposeComparator();
		createForms();

		cascadeRenderMenu.setEnabled(true);

	    

		
		bottom.dispose();
		bottom = new SashForm(form, SWT.HORIZONTAL);
		GridLayout bottom_layout = new GridLayout(3, false);
		bottom_layout.marginHeight = bottom_layout.marginWidth = 0;
		bottom.setLayout(bottom_layout);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		_rayInformation = new GraphView(bottom, SWT.BORDER);
		_rayInformation.setSize(320, 240);
		Layout _blL = new FillLayout();
		GridData _blLD = new GridData();
		_blLD.horizontalAlignment = SWT.LEFT;
		_blLD.verticalAlignment = SWT.TOP;
		_blLD.horizontalSpan = 1;
		_blLD.verticalSpan = 1;
		_rayInformation.setLayout(_blL);
		_rayInformation.setLayoutData(_blLD);
		_rayInformation.setup();
		_rayInformation.setFontSize(8);
		
		
		_TF = new TransferFunctionPresetView(bottom, SWT.BORDER, display);
		
		//Canvas BBr = new Canvas(bottom, SWT.BORDER);
		_LV = new LightingView(bottom, SWT.BORDER);
		bottom.setWeights(new int[] {1,2,1});
		
	    _3Drenderer = new CubrilleView(top, SWT.BORDER, _TF);
	    _3Drenderer.setSize(320, 240);
		Layout _tlL = new FillLayout();
		GridData _tlLD = new GridData();
		_tlLD.horizontalAlignment = SWT.LEFT;
		_tlLD.verticalAlignment = SWT.TOP;
		_tlLD.horizontalSpan = 1;
		_tlLD.verticalSpan = 1;
		_3Drenderer.setLayout(_tlL);
		_3Drenderer.setLayoutData(_tlLD);
		_3Drenderer.setup();
		_3Drenderer.setRaySelectionInterface(_rayInformation);
		_3Drenderer.setLightInformationProducer(_LV);
		
		_3Drenderer.setGaussProducer(_rayInformation);
		
		top.setWeights(new int[] {1});
		
		
		form.setWeights(new int[]{4,1});
		
		//_rayInformation.setDataHolder(_dataStorage);
		_TF.setViewerConnection(_3Drenderer);
		_LV.setViewerConnection(_3Drenderer);
		if(_initialised)
		{
			loadDataInto3D();
		}
		
		
		
		shell.layout();
		_3Drenderer.paint();
	}
	
	private void createAxisAlignedSliceViews()
	{
		disposeIntro();
		disposeAASlices();
		disposeComparator();
		createForms();

		
	    _AASTopLeft = new GraphView(top, SWT.BORDER);
	    _AASTopLeft.setSize(320, 240);
		Layout _tlL = new FillLayout();
		GridData _tlLD = new GridData();
		_tlLD.horizontalAlignment = SWT.LEFT;
		_tlLD.verticalAlignment = SWT.TOP;
		_tlLD.horizontalSpan = 1;
		_tlLD.verticalSpan = 1;
		_AASTopLeft.setLayout(_tlL);
		_AASTopLeft.setLayoutData(_tlLD);
		_AASTopLeft.DisableProfile();
		_AASTopLeft.DisableSemivariograms();
		_AASTopLeft.setup();
		
		_AASTopRight = new SliceView(top, SWT.BORDER, Texture3DPane.XY_PANE, SliceView.TYPE_SLICE);
		_AASTopRight.setSize(320, 240);
		Layout _trL = new FillLayout();
		GridData _trLD = new GridData();
		_trLD.horizontalAlignment = SWT.LEFT;
		_trLD.verticalAlignment = SWT.TOP;
		_trLD.horizontalSpan = 1;
		_trLD.verticalSpan = 1;
		_AASTopRight.setLayout(_trL);
		_AASTopRight.setLayoutData(_trLD);
		_AASTopRight.setup();
		_AASTopLeft.addAdaptor(_AASTopRight);
		_AASTopRight.setSliceSelectionInterface(_AASTopLeft);
		
		top.setWeights(new int[] {1, 1});
		
		_AASBottomLeft = new SliceView(bottom, SWT.BORDER, Texture3DPane.XZ_PANE, SliceView.TYPE_SLICE, _AASTopRight.getCanvasAsSharedHead());
		_AASBottomLeft.setSize(320, 240);
		Layout _blL = new FillLayout();
		GridData _blLD = new GridData();
		_blLD.horizontalAlignment = SWT.LEFT;
		_blLD.verticalAlignment = SWT.TOP;
		_blLD.horizontalSpan = 1;
		_blLD.verticalSpan = 1;
		_AASBottomLeft.setLayout(_blL);
		_AASBottomLeft.setLayoutData(_blLD);
		_AASBottomLeft.setup();
		_AASTopLeft.addAdaptor(_AASBottomLeft);
		_AASBottomLeft.setSliceSelectionInterface(_AASTopLeft);
		
		_AASBottomRight = new SliceView(bottom, SWT.BORDER, Texture3DPane.YZ_PANE, SliceView.TYPE_SLICE, _AASTopRight.getCanvasAsSharedHead());
		_AASBottomRight.setSize(320, 240);
		Layout _brL = new FillLayout();
		GridData _brLD = new GridData();
		_brLD.horizontalAlignment = SWT.LEFT;
		_brLD.verticalAlignment = SWT.TOP;
		_brLD.horizontalSpan = 1;
		_brLD.verticalSpan = 1;
		_AASBottomRight.setLayout(_brL);
		_AASBottomRight.setLayoutData(_brLD);
		_AASBottomRight.setup();
		_AASTopLeft.addAdaptor(_AASBottomRight);
		_AASBottomRight.setSliceSelectionInterface(_AASTopLeft);
		
		bottom.setWeights(new int[] {1, 1});
		
		//_AASTopLeft.setDataHolder(_AASTopRight);
		//_AASTopLeft.setDataHolder(_dataStorage);
		
		if(_initialised)
			loadDataIntoAASlices();
		
		shell.layout();
		_AASTopLeft.paint();
		_AASTopRight.paint();
		_AASBottomLeft.paint();
		_AASBottomRight.paint();
	}
	
	private void disposeIntro()
	{
		if(_IntroTopLeft!=null)
		{
			_IntroTopLeft.dispose();
		}
		if(_IntroTopRight!=null)
			_IntroTopRight.dispose();
		if(_IntroBottomLeft!=null)
			_IntroBottomLeft.dispose();
		if(_IntroBottomRight!=null)
			_IntroBottomRight.dispose();
		_IntroTopLeft = null;
		_IntroTopRight = null;
		_IntroBottomLeft = null;
		_IntroBottomRight = null;
	}
	
	private void dispose3D()
	{
		if((cascadeRenderMenu!=null) && (cascadeRenderMenu.isDisposed()==false))
			cascadeRenderMenu.setEnabled(false);
		if(_3Drenderer!=null)
			_3Drenderer.dispose();
		if(_TF!=null)
			_TF.dispose();
		
	}
	
	private void disposeAASlices()
	{
		if(_AASTopLeft!=null)
			_AASTopLeft.dispose();
		if(_AASTopRight!=null)
			_AASTopRight.dispose();
		if(_AASBottomLeft!=null)
			_AASBottomLeft.dispose();
		if(_AASBottomRight!=null)
			_AASBottomRight.dispose();
		_AASTopLeft = null;
		_AASTopRight = null;
		_AASBottomLeft = null;
		_AASBottomRight = null;
	}
	
	private void disposeComparator()
	{
		
	}
	
	private void loadDataIntoIntro()
	{
		if(_IntroTopRight!=null)
		{
			_IntroTopRight.initialiseData();
			_IntroTopRight.paint();
		}
		if(_IntroBottomLeft!=null)
		{
			_IntroBottomLeft.initialiseData();
			_IntroBottomLeft.paint();
		}
		if(_IntroBottomRight!=null)
		{
			_IntroBottomRight.initialiseData();
			_IntroBottomRight.paint();
		}

		if(_IntroTopLeft!=null)
		{
			_IntroTopLeft.setDataHolder(_dataStorage);
			_IntroTopLeft.setup();
		}
	}
	
	private void loadDataInto3D()
	{
		if(_3Drenderer!=null)
		{
			_3Drenderer.initialiseData();
			for(int i=0; i<reductionNumber; i++)
				_3Drenderer.reduceGeometry();
			_3Drenderer.createGeometry();
			_3Drenderer.paint();
		}
		if(_rayInformation!=null)
		{
			_rayInformation.setDataHolder(_dataStorage);
			if(_3Drenderer.getHistrogramSelectionAdaptor()!=null)
				_rayInformation.addAdaptor(_3Drenderer.getHistrogramSelectionAdaptor());
			_rayInformation.addAdaptor(_TF);
			_rayInformation.setup();
		}
	}
	
	private void loadDataIntoAASlices()
	{
		if(_AASTopRight!=null)
		{
			_AASTopRight.initialiseData();
			_AASTopRight.paint();
		}
		if(_AASBottomLeft!=null)
		{
			_AASBottomLeft.initialiseData();
			_AASBottomLeft.paint();
		}
		if(_AASBottomRight!=null)
		{
			_AASBottomRight.initialiseData();
			_AASBottomRight.paint();
		}

		if(_AASTopLeft!=null)
		{
			_AASTopLeft.setDataHolder(_dataStorage);
			_AASTopLeft.setup();
		}
	}
	
	private void loadDataIntoComparator()
	{

	}
	
	private void resetIntroView()
	{
		if(_IntroTopLeft!=null)
		{
			_IntroTopLeft.resetView();
		}
		if(_IntroTopRight!=null)
		{
			_IntroTopRight.center(0, 0);
		}
		if(_IntroBottomLeft!=null)
		{
			_IntroBottomLeft.center(0, 0);
		}
		if(_IntroBottomRight!=null)
		{
			_IntroBottomRight.center(0, 0);
		}
	}
	
	private void resetAASliceViews()
	{
		if(_AASTopLeft!=null)
		{
			_AASTopLeft.resetView();
		}
		if(_AASTopRight!=null)
		{
			_AASTopRight.center(0, 0);
		}
		if(_AASBottomLeft!=null)
		{
			_AASBottomLeft.center(0, 0);
		}
		if(_AASBottomRight!=null)
		{
			_AASBottomRight.center(0, 0);
		}
	}
	
	private void reset3Dview()
	{
		
	}
	
	private void resetComparatorView()
	{
		
	}
	
	private void loadDataIntoCurrentUI()
	{
		switch (_curStage) {
		case INTRO_STAGE:
		{
			loadDataIntoIntro();
			break;
		}
		case STAGE_3D:
		{
			loadDataInto3D();
			break;
		}
		case STAGE_AASLICE:
		{
			loadDataIntoAASlices();
			break;
		}
		case STAGE_COMPARATOR:
		{
			loadDataIntoComparator();
			break;
		}
		default:
			break;
		}
	}
	
	private void resetCurrentView()
	{
		switch (_curStage) {
		case INTRO_STAGE:
		{
			resetIntroView();
			break;
		}
		case STAGE_3D:
		{
			reset3Dview();
			break;
		}
		case STAGE_AASLICE:
		{
			resetAASliceViews();
			break;
		}
		case STAGE_COMPARATOR:
		{
			resetComparatorView();
			break;
		}
		default:
			break;
		}
	}
}
