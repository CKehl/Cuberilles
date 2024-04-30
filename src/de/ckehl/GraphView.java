package de.ckehl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.internal.gtk.OS;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.swtchart.*;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.ext.internal.SelectionRectangle;
//import assets.org.apache.commons.math3.*;
//import org.apache.commons.math.optimization.fitting.*;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class GraphView implements Listener, SliceSelectionInterface, RaySelectionInterface, GaussCurveInterface {

	protected static final int BTN_MODE_MOVE = 0;
	protected static final int BTN_MODE_SELECT = 1;
	
	protected static final int MODE_HISTOGRAM = 0;
	protected static final int MODE_LINE = 1;
	protected static final int MODE_SCATTER = 2;
	
	//protected Composite topComposite = null;
	protected SashForm topComposite = null;
	protected Composite _parent = null;
	protected Chart plot = null;
	protected double[] histo = null;
	protected double histoMax = Integer.MIN_VALUE;
	protected double histoMin = Integer.MAX_VALUE;
	protected Point[] offset = new Point[1];
	protected int counter = 0;
	protected int curMode = 0;
	
	protected double[] rebase_histo = null;
	protected double[] profile = null;
	protected double[] subHisto = null;
	protected IBarSeries barSeries = null;
	protected IBarSeries subBarSeries = null;
	protected ILineSeries lineSeries = null;
	protected ILineSeries scatterSeries = null;
	protected List<ILineSeries> gaussSeries = null;
	
	protected List<HistogramSelectionInterface> selectAdaptors = null;
	protected DataHolderInterface _dataHolder = null;

	
	private static boolean omitBackground = true;
	
	protected int _currentMode = 0;
	protected Button histoButton = null;
	protected Button profileButton = null;
	protected Button semivarioButton = null;
	protected Button gaussButton = null;
	protected Text _labelField = null;
	
	List<Vector3i> _voxelIndices = null;
	
	protected Vector2i _selection = null;
	protected int _selectInitVal = 0;
	protected boolean _doingSelection = false;
	protected ColorSelectionRectangle _visualSelection = null;
	
	protected boolean _addingGaussSpan = false;
	protected boolean _gaussUpdated = false;
	protected List<Vector2i> _gaussSpans = null;
	protected List<Vector3d> _gaussParameters = null;
	protected int _selectedGaussIndex = 0;
	protected boolean _gaussSelected = false;
	
	public GraphView(Composite arg0, int arg1)
	{
		//topComposite = new Composite(arg0, arg1);
		topComposite = new SashForm(arg0, SWT.VERTICAL);
		//topComposite.setLayout(new RowLayout(SWT.VERTICAL));
		//topComposite.setLayoutData(new RowData());
		
		Composite buttonArea = new Composite(topComposite, SWT.PUSH | SWT.BORDER);
		buttonArea.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		plot = new Chart(topComposite, SWT.PUSH | SWT.BORDER);
		//plot.getPlotArea().addListener(SWT.MouseWheel, this);
		plot.getPlotArea().addListener(SWT.MouseWheel, this);
		//plot.addListener(SWT.MouseDown, this);
		plot.getPlotArea().addListener(SWT.MouseDown, this);
		//plot.addListener(SWT.MouseMove, this);
		plot.getPlotArea().addListener(SWT.MouseMove, this);
		//plot.addListener(SWT.MouseUp, this);
		plot.getPlotArea().addListener(SWT.MouseUp, this);
		plot.getPlotArea().addListener(SWT.MouseDoubleClick, this);
		selectAdaptors = new ArrayList<>();
		
		histoButton = new Button(buttonArea, SWT.PUSH);
		histoButton.setText("H");
		histoButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				_currentMode = MODE_HISTOGRAM;
				if(_voxelIndices.isEmpty()==false)
					showRayValueHistogram(_voxelIndices);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		profileButton = new Button(buttonArea, SWT.PUSH);
		profileButton.setText("P");
		profileButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				_currentMode = MODE_LINE;
				if(_voxelIndices.isEmpty()==false)
					showRayValueProfile(_voxelIndices);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		semivarioButton = new Button(buttonArea, SWT.PUSH);
		semivarioButton.setText("S");
		semivarioButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				_currentMode = MODE_SCATTER;
				if(_voxelIndices.isEmpty()==false)
					showRayValueSemivariogram(_voxelIndices);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gaussButton = new Button(buttonArea, SWT.PUSH);
		gaussButton.setText("+Gauss");
		gaussButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(isCurveMarkingEnabled()) {
					DisableCurveMarking();
					gaussButton.setText("+Gauss");
				} else {
					gaussButton.setText("MoveGraph");
					EnableCurveMarking();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_labelField = new Text(buttonArea, SWT.READ_ONLY | SWT.BORDER);
		_labelField.setSize(240, 20);
		_labelField.setLayoutData(new RowData(140, 20));
		
		//_selection = new Vector2i();
		//_visualSelection = new ColorSelectionRectangle();
		
		histo = new double[4096];
		rebase_histo=new double[4096];
		subHisto=new double[4096];
		_voxelIndices = new ArrayList<>();
		topComposite.setWeights(new int[]{1,5});
		_addingGaussSpan = false;
		_gaussSpans = new ArrayList<>();
		_gaussParameters = new ArrayList<>();
		gaussSeries = new ArrayList<>();
	}
	
	public void dispose()
	{
		if(plot.isDisposed()==false)
			plot.dispose();
	}
	
	public void setLayoutData(Object arg0)
	{
		//plot.getPlotArea().setLayoutData(arg0);
		topComposite.setLayoutData(arg0);
	}
	
	public void setLayout(Layout arg0)
	{
		//plot.getPlotArea().setLayout(arg0);
		topComposite.setLayout(arg0);
	}
	
	public void setSize(int arg0, int arg1)
	{
		//plot.getPlotArea().setSize(arg0, arg1);
		topComposite.setSize(arg0,  arg1);
	}
	
	public void setDataHolder(DataHolderInterface dataHolder)
	{
		_dataHolder = dataHolder;
	}
	
	public void addAdaptor(HistogramSelectionInterface adaptor)
	{
		selectAdaptors.add(adaptor);
	}
	
	public void removeLastAdaptor()
	{
		if(selectAdaptors.isEmpty()==false)
			selectAdaptors.remove(selectAdaptors.size()-1);
	}
	
	public void paint()
	{
		plot.getPlotArea().redraw();
	}
	
	public void setup()
	{
		if((_dataHolder!=null))// && (histo==null)
			calculateGlobalHistogram();
		
		if(histo!=null)
		{
			plot.getTitle().setText("Histogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("receptor value");
			plot.getAxisSet().getYAxis(0).getTitle().setText("occurence");
			
			barSeries = (IBarSeries)plot.getSeriesSet().createSeries(SeriesType.BAR, "histogram");
			subBarSeries = (IBarSeries)plot.getSeriesSet().createSeries(SeriesType.BAR, "subHistogram");
			//Device device = Display.getCurrent();
			subBarSeries.setBarColor(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));
			subBarSeries.setVisible(false);
			lineSeries = (ILineSeries)plot.getSeriesSet().createSeries(SeriesType.LINE, "ray");
			lineSeries.setSymbolSize(2);
			lineSeries.setVisible(false);
			scatterSeries = (ILineSeries)plot.getSeriesSet().createSeries(SeriesType.LINE, "scatter");
			scatterSeries.setLineStyle(LineStyle.NONE);
			scatterSeries.setVisible(false);
			//barSeries.setYSeries(histo);
			barSeries.setYSeries(rebase_histo);
			barSeries.setVisible(true);
			//plot.getAxisSet().getYAxis(0).enableLogScale(true);
			plot.getAxisSet().adjustRange();
			ILegend legend = plot.getLegend();
			legend.setPosition(SWT.TOP);
			legend.setVisible(false);
			
			plot.getPlotArea().addPaintListener(new PaintListener() {
				
				@Override
				public void paintControl(PaintEvent e) {
					// TODO Auto-generated method stub
					if((_visualSelection!=null) && (_visualSelection.isDisposed()==false))
					{
						IAxis xAxis = plot.getAxisSet().getXAxis(0);
						//_selection.x = (int)(xAxis.getDataCoordinate(arg0.x));
						int xSC = xAxis.getPixelCoordinate((double)_selection.x);
						int xEC = xAxis.getPixelCoordinate((double)_selection.y);
						_visualSelection.setStartPoint(xSC, 0);
						_visualSelection.setEndPoint(xEC, plot.getPlotArea().getClientArea().height);
						_visualSelection.draw(e.gc);
					}
					else
						_labelField.setText("");
				}
			});
			plot.redraw();
		}
	}
	
	public void resetView()
	{
		if((histo!=null) && (rebase_histo!=null))
		{
			for(int i=0;i<(histo.length);i++)
			{
				if((i<=1) && (omitBackground))
					rebase_histo[i] = 0;
				else
					rebase_histo[i] = new Double(histo[i]);
			}
		}
		if(isReady())
		{
			for(HistogramSelectionInterface entry : selectAdaptors)
				entry.resetHistogramSelection();
			_visualSelection = null;
			
			plot.getTitle().setText("Histogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("receptor value");
			plot.getAxisSet().getYAxis(0).getTitle().setText("occurence");
			
			barSeries.setYSeries(rebase_histo);
			subBarSeries.setVisible(false);
			lineSeries.setVisible(false);
			barSeries.setVisible(true);
			scatterSeries.setVisible(false);
			//plot.getAxisSet().getYAxis(0).enableLogScale(true);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	public void setFontSize(int size)
	{
		if(plot!=null)
		{
			Font font, smallFont;
			String os_name = System.getProperty("os.name");
			if (os_name.contains("nix") || os_name.contains("nux") || os_name.contains("aix"))
			{
				font = new Font(Display.getDefault(), "Liberation Sans", size, SWT.BOLD);
				smallFont = new Font(Display.getDefault(), "Liberation Sans", size-2, SWT.BOLD);
			}
			else
			{
				font = new Font(Display.getDefault(), "Arial", size, SWT.BOLD);
				smallFont = new Font(Display.getDefault(), "Arial", size-2, SWT.BOLD);
			}
			plot.getAxisSet().getXAxis(0).getTitle().setFont(font);
			plot.getAxisSet().getXAxis(0).getTick().setFont(smallFont);
			plot.getAxisSet().getYAxis(0).getTitle().setFont(font);
			plot.getAxisSet().getYAxis(0).getTick().setFont(smallFont);
			plot.getTitle().setFont(font);
			plot.redraw();
		}
	}
	
	public void EnableHistograms()
	{
		histoButton.setEnabled(true);
	}
	
	public void DisableHistograms()
	{
		histoButton.setEnabled(false);
	}
	
	public void EnableProfile()
	{
		profileButton.setEnabled(true);
	}
	
	public void DisableProfile()
	{
		profileButton.setEnabled(false);
	}
	
	public void EnableSemivariogram()
	{
		semivarioButton.setEnabled(true);
	}
	
	public void DisableSemivariograms()
	{
		semivarioButton.setEnabled(false);
	}
	
	public boolean isCurveMarkingEnabled() {
		return _addingGaussSpan;
	}
	
	public void EnableCurveMarking() {
		_addingGaussSpan = true;
	}
	
	public void DisableCurveMarking() {
		_addingGaussSpan = false;
	}

	@Override
	public void handleEvent(Event arg0) {
		// TODO Auto-generated method stub
		String stateString = "";
		stateString += ((_currentMode==MODE_HISTOGRAM)?"MODE_HISTOGRAM ":"");
		stateString += ((_currentMode==MODE_LINE)?"MODE_PROFILE ":"");
		stateString += ((_currentMode==MODE_SCATTER)?"MODE_SCATTER ":"");
		stateString += ((_doingSelection==true)?"VALUE_SELECTION":"");
		stateString += ((_addingGaussSpan==true)?"ADDING_GAUSS_CURVES":"");
		//System.out.println("State: "+stateString);
		
		switch(arg0.type)
		{
			case SWT.MouseWheel:
			{
				// zoom
				IAxis yAxis = plot.getAxisSet().getYAxis(0);
				IAxis xAxis = plot.getAxisSet().getXAxis(0);
				if(arg0.count<0)
				{
					yAxis.zoomOut(yAxis.getDataCoordinate(arg0.y));
					xAxis.zoomOut(xAxis.getDataCoordinate(arg0.x));
					//yAxis.zoomOut();
					//xAxis.zoomOut();
				}
				else
				{
					yAxis.zoomIn(yAxis.getDataCoordinate(arg0.y));
					xAxis.zoomIn(xAxis.getDataCoordinate(arg0.x));
					//yAxis.zoomIn();
					//xAxis.zoomIn();
				}
				break;
			}
			case SWT.MouseDown:
			{
				// select
				//if((arg0.stateMask & SWT.BUTTON1) != 0) // LEFT
				if((arg0.button == 1) || ((arg0.stateMask & SWT.BUTTON1) != 0))
				{
					offset[0] = new Point(arg0.x, arg0.y);
					if(((arg0.stateMask & SWT.SHIFT)!=0) && (_currentMode==MODE_HISTOGRAM))
					{
						_selection = new Vector2i();
						IAxis xAxis = plot.getAxisSet().getXAxis(0);
						
						int val = (int)(xAxis.getDataCoordinate(arg0.x));
						_selectInitVal = val;
						
						if(val < _selection.x)
							_selection.x = val;
						if(val > _selection.y)
							_selection.y = val;
						
						_visualSelection = new ColorSelectionRectangle();
						_visualSelection.setStartPoint(arg0.x, 0);
					}
					else if((_addingGaussSpan) && (_currentMode==MODE_HISTOGRAM))
					{
						_selection = new Vector2i(Integer.MAX_VALUE, Integer.MIN_VALUE);
						IAxis xAxis = plot.getAxisSet().getXAxis(0);
						
						int val = (int)(xAxis.getDataCoordinate(arg0.x));
						_selectInitVal = val;
						
						if(val < _selection.x)
							_selection.x = val;
						if(val > _selection.y)
							_selection.y = val;
						_visualSelection = new ColorSelectionRectangle();
						_visualSelection.setStartPoint(arg0.x, 0);
					}
					else
					{
						IAxis xAxis = plot.getAxisSet().getXAxis(0);
						IAxis yAxis = plot.getAxisSet().getYAxis(0);
						int valX = (int)(xAxis.getDataCoordinate(arg0.x));
						int valY = (int)(yAxis.getDataCoordinate(arg0.y));
						Vector2i dataPoint = new Vector2i(valX, valY);
						if((_gaussSpans!=null) && (_gaussSpans.size()>0) && (_gaussParameters!=null) && (_gaussParameters.size()>0)) {
							for(int i=0; i<_gaussSpans.size(); i++)
							{
								if((valX>_gaussSpans.get(i).x) && (valX<_gaussSpans.get(i).y))
								{
									Vector3d gP = _gaussParameters.get(i);
									int gaussX = valX;
									int gaussY = (int)Math.floor(getGaussY((double)gaussX, gP.x, gP.y, gP.z));
									Vector2i gaussVector = new Vector2i(gaussX, gaussY);
									if(Math.abs(gaussY-valY)<500) {
										_selectedGaussIndex = i;
										_gaussSelected = true;
										_gaussUpdated = true;
									}
								}
							}
						}
					}
				}
				else if(((arg0.stateMask & SWT.BUTTON3) != 0) || (arg0.button==3) || ((arg0.stateMask & SWT.BUTTON2) != 0) || (arg0.button==2)) // RIGHT
				{
					for(HistogramSelectionInterface entry : selectAdaptors)
						entry.resetHistogramSelection();
					_visualSelection = null;
					_gaussSelected=false;
					_gaussUpdated = true;
				}
				break;
			}
			case SWT.MouseMove:
			{
				if(((arg0.stateMask & SWT.SHIFT)!=0) && ((arg0.stateMask & SWT.BUTTON1)!=0) && (_currentMode==MODE_HISTOGRAM) && (_selection!=null) && (_visualSelection!=null))
				{
					IAxis xAxis = plot.getAxisSet().getXAxis(0);
					//IAxis yAxis = plot.getAxisSet().getYAxis(0);
					_selection.y = (int)(xAxis.getDataCoordinate(arg0.x));
					if(!_visualSelection.isDisposed())
					{
						_visualSelection.setEndPoint(arg0.x, plot.getPlotArea().getClientArea().height);
						plot.redraw();
					}
					_doingSelection = true;
				}
				else if((_addingGaussSpan==true) && ((arg0.stateMask & SWT.BUTTON1)!=0) && (_currentMode==MODE_HISTOGRAM) && (_selection!=null) && (_visualSelection!=null))
				{
					IAxis xAxis = plot.getAxisSet().getXAxis(0);
					int val = (int)(xAxis.getDataCoordinate(arg0.x));
					System.out.printf("start:%d, current: %d\n", _selectInitVal, val);
					if(val < _selectInitVal) {
						_selection.x = val;
						_selection.y = _selectInitVal;
					} else {
						_selection.x = _selectInitVal;
						_selection.y = val;
					}
					if(!_visualSelection.isDisposed())
					{
						_visualSelection.setEndPoint(arg0.x, plot.getPlotArea().getClientArea().height);
						plot.redraw();
					}
				}
				else
				{
					if(counter==20)
					{
						// shift plot
						IAxis yAxis = plot.getAxisSet().getYAxis(0);
						IAxis xAxis = plot.getAxisSet().getXAxis(0);
						if(offset[0]!=null)
						{
							Point deltaP = new Point(arg0.x - offset[0].x, arg0.y- offset[0].y);
							//System.out.printf("MouseMove: (%d,%d)\n", deltaP.x, deltaP.y);
							if(Math.abs(deltaP.x) > Math.abs(deltaP.y))
							{
								if(deltaP.x < 0)
									xAxis.scrollUp();
								if(deltaP.x > 0)
									xAxis.scrollDown();
							}
							else
							{
								if(deltaP.y > 0)
									yAxis.scrollUp();
								if(deltaP.y < 0)
									yAxis.scrollDown();
							}
							offset[0] = new Point(arg0.x, arg0.y);
						}
						counter=0;
					}
					else
						counter++;
					//curMode = BTN_MODE_MOVE;
				}
				break;
			}
			case SWT.MouseUp:
			{
				offset[0] = null;
				counter=0;
				//curMode = BTN_MODE_SELECT;
				if((_doingSelection) && (_currentMode==MODE_HISTOGRAM))
				{
					IAxis xAxis = plot.getAxisSet().getXAxis(0);
					_selection.y = (int)(xAxis.getDataCoordinate(arg0.x));
					if(_selection.x>_selection.y)
						_selection = new Vector2i(_selection.y, _selection.x);
					_labelField.setText(_selection.toString(new DecimalFormat( "#,###,###,##0.00" )));
					for(HistogramSelectionInterface entry : selectAdaptors)
						entry.markValues(_selection);
					
					//_selection=null;
					if(!_visualSelection.isDisposed())
					{
						_visualSelection.setEndPoint(arg0.x, plot.getPlotArea().getClientArea().height);
						plot.redraw();
					}
					//_visualSelection = null;
					_doingSelection = false;
				}
				else if((_addingGaussSpan==true) && (_currentMode==MODE_HISTOGRAM) && (_selection!=null) && (_visualSelection!=null))
				{
					IAxis xAxis = plot.getAxisSet().getXAxis(0);
					int val = (int)(xAxis.getDataCoordinate(arg0.x));
					if(val < _selectInitVal) {
						_selection.x = val;
						_selection.y = _selectInitVal;
					} else {
						_selection.x = _selectInitVal;
						_selection.y = val;
					}
					
					if(_selection.x>_selection.y)
						_selection = new Vector2i(_selection.y, _selection.x);
					_labelField.setText(_selection.toString(new DecimalFormat( "#,###,###,##0.00" )));
					
					_gaussSpans.add(_selection);
					_visualSelection.dispose();
					_visualSelection = null;
					
					if((_selection.y-_selection.x)>10)
						calculateGaussCurves();
					
					plot.redraw();
				}
				break;
			}
			
			case SWT.MouseDoubleClick:
			{
				IAxis xAxis = plot.getAxisSet().getXAxis(0);
				
				if(_currentMode==MODE_HISTOGRAM)
				{

					int function = (int)(xAxis.getDataCoordinate(arg0.x));
					if(function > 0)
					{
						int value = (int)(histo[function]);
						for(HistogramSelectionInterface entry : selectAdaptors)
							entry.center(function, value);
						
						for(int i=0;i<(histo.length);i++)
						{
							//if(histo[i]==0)
							//	rebase_histo[i] = 0.000001;
							//else
							//	rebase_histo[i] = new Double(histoIn[i]);
							rebase_histo[i] = histo[i]-value;
							//if(i<(int)(histo.length/100.0))
							if((i<=1) &&(omitBackground))
								rebase_histo[i] = 0;
						}
						
						if(isReady())
						{
							barSeries.setYSeries(rebase_histo);
							subBarSeries.setVisible(false);
							lineSeries.setVisible(false);
							barSeries.setVisible(true);
							scatterSeries.setVisible(false);
							//plot.getAxisSet().getYAxis(0).enableLogScale(false);
							plot.getAxisSet().adjustRange();
							//plot.redraw();
							_visualSelection = null;
						}
					}
				}
				break;
			}
		}
		plot.redraw();
		//if((_doingSelection==false) && (_selection==null))
		//	_visualSelection = null;
	}
	
	public boolean isReady()
	{
		return ((lineSeries!=null)&&(barSeries!=null)&&(scatterSeries!=null)&&(plot!=null));
	}
	
	public void showRayValueSemivariogram(List<Vector3i> voxelList)
	{
		int N = voxelList.size();
		double Z[] = new double [voxelList.size()];
		
		int i = 0;
		double sum=0;
		for(Vector3i entry : voxelList)
		{
			Z[i] = new Double(_dataHolder.getDataValue(entry.x, entry.y, entry.z));
			sum+=Z[i];
			i++;
		}
		i=0;
		double mean_global = sum/(double)N;
		
		profile = new double[N];
		for(int h = 0; h < N; h++)
		{
			//int num_pairs = N/h;
			int num_pairs = N-h;
			double y_h = 0, E = 0;
			for(int x = 0; x < num_pairs; x++)
			{
				E+= (Z[x+h]-Z[x]) * (Z[x+h]-Z[x]);
			}
			E /= (double)num_pairs;
			y_h = 0.5 * E;
			profile[h] = y_h;
		}
		
		if(isReady())
		{
			plot.getTitle().setText("Semivariogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("distance [h]");
			plot.getAxisSet().getYAxis(0).getTitle().setText("semivariance [y(h)]");
			
			scatterSeries.setYSeries(profile);
			scatterSeries.setVisible(true);
			lineSeries.setVisible(false);
			barSeries.setVisible(false);
			subBarSeries.setVisible(false);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	public void showRayValueProfile(List<Vector3i> voxelList)
	{
		profile = new double[voxelList.size()];
		int i = 0;
		for(Vector3i entry : voxelList)
		{
			profile[i] = new Double(_dataHolder.getDataValue(entry.x, entry.y, entry.z));
			i++;
		}
		
		if(isReady())
		{
			plot.getTitle().setText("Profile");
			plot.getAxisSet().getXAxis(0).getTitle().setText("distance");
			plot.getAxisSet().getYAxis(0).getTitle().setText("receptor value");
			
			lineSeries.setYSeries(profile);
			lineSeries.setVisible(true);
			barSeries.setVisible(false);
			subBarSeries.setVisible(false);
			scatterSeries.setVisible(false);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	public void showRayValueHistogram(List<Vector3i> voxelList)
	{
		for(int i = 0; i < 4096; i++)
		{
			rebase_histo[i] = 0;
		}
		for(Vector3i entry : voxelList)
		{
			rebase_histo[_dataHolder.getDataValue(entry.x, entry.y, entry.z)]+=1.0;
		}
		
		if(omitBackground)
		{
			rebase_histo[0] = 0;
			rebase_histo[1] = 0;
		}
		
		if(isReady())
		{
			plot.getTitle().setText("Histogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("receptor value");
			plot.getAxisSet().getYAxis(0).getTitle().setText("occurence");
			
			barSeries.setYSeries(rebase_histo);
			lineSeries.setVisible(false);
			barSeries.setVisible(true);
			subBarSeries.setVisible(false);
			scatterSeries.setVisible(false);
			//plot.getAxisSet().getYAxis(0).enableLogScale(true);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	public void showXYSlice(int z)
	{
		for(int i = 0; i < 4096; i++)
		{
			rebase_histo[i] = 0;
		}
		for(int i = 0; i < _dataHolder.getDimensions()[0]; i++)
		{
			for(int j = 0; j < _dataHolder.getDimensions()[1]; j++)
			{
				rebase_histo[_dataHolder.getDataValue(i, j, z)]+=1.0;
			}
		}
		
		if(omitBackground)
		{
			rebase_histo[0] = 0;
			rebase_histo[1] = 0;
		}
		
		if(isReady())
		{
			plot.getTitle().setText("Histogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("receptor value");
			plot.getAxisSet().getYAxis(0).getTitle().setText("occurence");
			
			barSeries.setYSeries(rebase_histo);
			lineSeries.setVisible(false);
			barSeries.setVisible(true);
			subBarSeries.setVisible(false);
			scatterSeries.setVisible(false);
			//plot.getAxisSet().getYAxis(0).enableLogScale(true);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	public void showYZSlice(int x)
	{
		for(int i = 0; i < 4096; i++)
		{
			rebase_histo[i] = 0;
		}
		for(int j = 0; j < _dataHolder.getDimensions()[1]; j++)
		{
			for(int k = 0; k < _dataHolder.getDimensions()[2]; k++)
			{
				rebase_histo[_dataHolder.getDataValue(x, j, k)]+=1.0;
			}
		}
		
		if(omitBackground)
		{
			rebase_histo[0] = 0;
			rebase_histo[1] = 0;
		}
		
		if(isReady())
		{
			plot.getTitle().setText("Histogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("receptor value");
			plot.getAxisSet().getYAxis(0).getTitle().setText("occurence");
			
			barSeries.setYSeries(rebase_histo);
			lineSeries.setVisible(false);
			barSeries.setVisible(true);
			subBarSeries.setVisible(false);
			scatterSeries.setVisible(false);
			//plot.getAxisSet().getYAxis(0).enableLogScale(true);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	public void showXZSlice(int y)
	{
		for(int i = 0; i < 4096; i++)
		{
			rebase_histo[i] = 0;
		}
		for(int i = 0; i < _dataHolder.getDimensions()[0]; i++)
		{
			for(int k = 0; k < _dataHolder.getDimensions()[2]; k++)
			{
				rebase_histo[_dataHolder.getDataValue(i, y, k)]+=1.0;
			}
		}
		
		if(omitBackground)
		{
			rebase_histo[0] = 0;
			rebase_histo[1] = 0;
		}
		
		if(isReady())
		{
			plot.getTitle().setText("Histogram");
			plot.getAxisSet().getXAxis(0).getTitle().setText("receptor value");
			plot.getAxisSet().getYAxis(0).getTitle().setText("occurence");
			
			barSeries.setYSeries(rebase_histo);
			lineSeries.setVisible(false);
			barSeries.setVisible(true);
			subBarSeries.setVisible(false);
			scatterSeries.setVisible(false);
			//plot.getAxisSet().getYAxis(0).enableLogScale(true);
			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	protected void calculateGlobalHistogram()
	{
		if(_dataHolder==null)
			return;
		
		histo = new double[4096];
		rebase_histo=new double[4096];
		histoMax = -Double.MAX_VALUE;
		histoMin = Double.MAX_VALUE;
		//int minIdx = 0;
		for(int i = 0; i < _dataHolder.getDimensions()[0]; i++)
		{
			for(int j = 0; j < _dataHolder.getDimensions()[1]; j++)
			{
				for(int k = 0; k < _dataHolder.getDimensions()[2]; k++)
				{
					try
					{
						histo[_dataHolder.getDataValue(i, j, k)]+=1.0;
						if(histo[_dataHolder.getDataValue(i, j, k)]>histoMax)
							histoMax = histo[_dataHolder.getDataValue(i, j, k)];
					}
					catch (Exception e)
					{
						System.out.printf("data value: %d\n", _dataHolder.getDataValue(i, j, k));
						e.printStackTrace();
					}
				}
			}
		}
		for(int i = 0; i < 4096; i++)
		{
			if(histo[i] < histoMin)
			{
				histoMin = histo[i];
			}
			rebase_histo[i] = histo[i];
		}
	}

	@Override
	public void showHistogramXY(int z) {
		// TODO Auto-generated method stub
		showXYSlice(z);
	}

	@Override
	public void showHistogramXZ(int y) {
		// TODO Auto-generated method stub
		showXZSlice(y);
	}

	@Override
	public void showHistogramYZ(int x) {
		// TODO Auto-generated method stub
		showYZSlice(x);
	}

	@Override
	public void setRayInformation(List<Vector3i> intersections) {
		// TODO Auto-generated method stub
		_voxelIndices.clear();
		for(Vector3i entry : intersections)
			_voxelIndices.add(entry);
		
		switch (_currentMode) {
		case MODE_HISTOGRAM:
			showRayValueHistogram(_voxelIndices);
			break;
		case MODE_LINE:
			showRayValueProfile(_voxelIndices);
			break;
		case MODE_SCATTER:
			showRayValueSemivariogram(_voxelIndices);
		default:
			break;
		}
	}

	@Override
	public void resetRayInformation() {
		// TODO Auto-generated method stub
		resetView();
	}

	@Override
	public void graphHistogramSelection(int[] subHistogram) {
		// TODO Auto-generated method stub
		if((subHisto.length!=4096) || (subHistogram.length!=4096))
			return;
		
		for(int i = 0; i < 4096; i++)
		{
			subHisto[i] = (double)(subHistogram[i]);
		}
		
		if(isReady())
		{
			subBarSeries.setYSeries(subHisto);
			subBarSeries.setVisible(true);

			plot.getAxisSet().adjustRange();
			plot.redraw();
		}
	}
	
	private void calculateGaussCurves() {
		if(_gaussSpans==null)
			return;
		if(_gaussSpans.isEmpty())
			return;
		_gaussParameters.clear();
		for(Vector2i entry : _gaussSpans) {
			final WeightedObservedPoints obs = new WeightedObservedPoints();
			double wY = 0;
			for(int wX = entry.x; wX<entry.y; wX++) {
				wY = histo[(int)wX];
				obs.add((double)wX, wY);
			}
			final GaussianCurveFitter fitter = GaussianCurveFitter.create();
			final double[] coeff = fitter.fit(obs.toList());
			_gaussParameters.add(new Vector3d(coeff[0], coeff[1], coeff[2]));
		}
		//_gaussUpdated = true;
		generateDrawingCurves();
	}
	
	private void generateDrawingCurves() {
		//for(Vector3f entry : _gaussParameters) {
		for(int i=0; i<_gaussSpans.size(); i++) {
			//Vector2i wXs = _gaussSpans.get(i);
			Vector3d gaussParam = _gaussParameters.get(i);
			System.out.println("Gauss parameters: "+gaussParam.toString());
			//int samples = wXs.y-wXs.x;
			//int min = wXs.x;
			int variance = (int)(gaussParam.z);
			int stdDev = (int)(gaussParam.z*gaussParam.z);
			int samples = stdDev*2;
			int min = (int)(gaussParam.y)-stdDev;
			System.out.printf("Gauss curve parameters: min(%d), variance(%d), stdDev(%d), samples(%d)\n",min, variance, stdDev, samples);
			double bellCurveData[] = new double[samples];
			double samplePoints[] = new double[samples];
			//for(int wX = wXs.x; wX<wXs.y; wX++) {
			for(int wX = Math.max(min, 0); wX<Math.min(Math.max(min, 0)+samples, 4095); wX++) {
				samplePoints[wX-Math.max(min, 0)] = (double)wX;
				bellCurveData[wX-Math.max(min, 0)] = getGaussY((double)wX, gaussParam.x, gaussParam.y, gaussParam.z);
			}
			
			//ILineSeries gSeries;
			//gSeries = (ILineSeries)plot.getSeriesSet().createSeries(SeriesType.LINE, "gauss"+Integer.toString(i));
			//gSeries.setYSeries(bellCurveData);
			//gSeries.setSymbolSize(2);
			//gSeries.setVisible(true);
			gaussSeries.add((ILineSeries)plot.getSeriesSet().createSeries(SeriesType.LINE, "gauss"+Integer.toString(i)));
			gaussSeries.get(gaussSeries.size()-1).setYSeries(bellCurveData);
			gaussSeries.get(gaussSeries.size()-1).setXSeries(samplePoints);
			gaussSeries.get(gaussSeries.size()-1).setSymbolSize(2);
			gaussSeries.get(gaussSeries.size()-1).setVisible(true);
		}
	}
	
	private static double getGaussY(double x, double normScale, double mu, double sigma) {
		//return normScale*(Math.exp(-((x-mu)*(x-mu))/(2*sigma*sigma))/Math.sqrt(2.0f*Math.PI*sigma*sigma));
		return (normScale*Math.exp(-((x-mu)*(x-mu))/(2*sigma*sigma)))+mu;
	}

	@Override
	public float[] getCurveParamArray() {
		// TODO Auto-generated method stub
		float gaussParamData[] = new float[3*_gaussParameters.size()];
		for(int i=0; i<_gaussParameters.size(); i++) {
			Vector3d entry = _gaussParameters.get(i);
			gaussParamData[i*3+0]=(float)entry.x;
			gaussParamData[i*3+1]=(float)entry.y;
			gaussParamData[i*3+2]=(float)entry.z;
		}
		return gaussParamData;
	}

	@Override
	public int getNumberOfCurveParameters() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void setCurveParamArray(float[] gaussFuncParams, int paramsPerItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return _gaussUpdated;
	}

	@Override
	public void Update(boolean state) {
		// TODO Auto-generated method stub
		_gaussUpdated = state;
	}

	@Override
	public short[] getCurveParamUShortArray() {
		// TODO Auto-generated method stub
		short gaussParamData[] = new short[3*_gaussParameters.size()];
		for(int i=0; i<_gaussParameters.size(); i++) {
			Vector3d entry = _gaussParameters.get(i);
			gaussParamData[i*3+0]=(short)(((float)entry.x/4095.0f)*32767.0f);
			gaussParamData[i*3+1]=(short)(((float)entry.x/4095.0f)*32767.0f);
			gaussParamData[i*3+2]=(short)(((float)entry.x/4095.0f)*32767.0f);
		}
		return gaussParamData;
	}

	@Override
	public void setCurveParamUShortArray(short[] gaussFuncParams, int paramsPerItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNumberOfCurveParameters(int numCurveParameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNumberOfCurves() {
		// TODO Auto-generated method stub
		return _gaussParameters.size();
	}

	@Override
	public void setNumberOfCurves(int numCurves) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCurveSelected() {
		// TODO Auto-generated method stub
		return _gaussSelected;
	}

	@Override
	public Vector3d getGaussCurve(int index) {
		// TODO Auto-generated method stub
		if((_gaussParameters!=null) && (_gaussParameters.size()>index))
			return _gaussParameters.get(index);
		return null;
	}

	@Override
	public void setGaussCurve(Vector3d gaussParameters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector3d getSelectedGaussCurve() {
		// TODO Auto-generated method stub
		if((_gaussParameters!=null) && (_gaussParameters.size()>_selectedGaussIndex))
			return _gaussParameters.get(_selectedGaussIndex);
		return null;
	}
}
