package de.ckehl;

import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.swtchart.IAxis;

import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.awt.TextRenderer;

public class SliceView implements Listener, HistogramSelectionInterface {
	public static final int TYPE_XRAY = 0;
	public static final int TYPE_SLICE = 1;
	
	public static final int MODE_VIEW = 0;
	public static final int MODE_MEASURE = 1;
	public static final int MODE_HIGHLIGHT = 2;
	
	protected Composite _parent = null;
	
	protected GLData gldata;
	protected GLCanvas glcanvas;
	protected GLProfile glprofile;
	protected GLContext glcontext;
	
	//private boolean _isOpen = false;
	private boolean _isHead = false;
	protected int _type = TYPE_XRAY;
	
	protected Geometry _geometry = null;
	
	// Text rendering
	protected TextRenderer _textRenderer = null;
	protected String _currentText = "";
	
	protected PaneView _paneview;
	protected short _pane;
	protected BaseLevelAdaptorInterface baseLevelAdaptor = null;
	protected TextureMatrixInterface planeAdaptor = null;
	protected WindowLevelInterface windowLevelAdaptor = null;
	protected SliceSelectionInterface sliceSelectionAdaptor = null;
	protected Texture2DInterface markerAdaptor = null;
	protected Float[] _textureTranslate = null;
	protected static Vector3f _curPlanesCoords, _selectedPoints[];
	protected static short numPointsSelected = 0;
	protected static int _currentMode = MODE_VIEW;

	protected int counter = 0;
	protected Point[] offset = new Point[1];
	
	protected float[][] marking = null;
	protected int markingBins[] = null;
	protected boolean _isMarkingActive = false;
	
	public SliceView(Composite arg0, int arg1, short pane, int type)
	{
		gldata = new GLData();
		gldata.doubleBuffer = true;
		gldata.depthSize = 8;
		
		_isHead = true;
		_textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 18));
		_pane = pane;
		_paneview = new PaneView(); //pane, _isHead
		_paneview.setPane(pane);
		_parent = arg0;
		_geometry = new GeometryContainer();
		_type = type;
		
		_textureTranslate = new Float[3];
		_textureTranslate[0] = -1.0f;
		_textureTranslate[1] = -1.0f;
		_textureTranslate[2] = 1.0f;
		
		_curPlanesCoords = new Vector3f(0f,0f,0f);
		_selectedPoints = new Vector3f[2];
		_selectedPoints[0] = new Vector3f();
		_selectedPoints[1] = new Vector3f();
		
		switch (pane) {
		case Texture3DPane.XY_PANE:
		{
			_currentText = "Axial (XY plane)";
			break;
		}
		case Texture3DPane.XZ_PANE:
		{
			_currentText = "Coronal (XZ plane)";
			break;
		}
		case Texture3DPane.YZ_PANE:
		{
			_currentText = "Saggital (YZ plane)";
			break;
		}
		default:
			break;
		}
		
		glcanvas = new GLCanvas(_parent, SWT.NO_BACKGROUND, gldata);
	}
	
	public SliceView(Composite arg0, int arg1, short pane, int type, GLCanvas contextHead)
	{
		gldata = new GLData();
		gldata.doubleBuffer = true;
		gldata.depthSize = 8;
		gldata.shareContext = contextHead;
		
		_isHead = false;
		_textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 18));
		
		_pane = pane;
		_paneview = new PaneView(); //pane
		_paneview.setPane(pane);
		_parent = arg0;
		_geometry = new GeometryContainer();
		_type = type;
	
		_textureTranslate = new Float[3];
		_textureTranslate[0] = -1.0f;
		_textureTranslate[1] = -1.0f;
		_textureTranslate[2] = 1.0f;
		
		
		
		switch (pane) {
		case Texture3DPane.XY_PANE:
		{
			_currentText = "Axial (XY plane)";
			break;
		}
		case Texture3DPane.XZ_PANE:
		{
			_currentText = "Coronal (XZ plane)";
			break;
		}
		case Texture3DPane.YZ_PANE:
		{
			_currentText = "Saggital (YZ plane)";
			break;
		}
		default:
			break;
		}
		
		glcanvas = new GLCanvas(_parent, SWT.NO_BACKGROUND, gldata);
	}	
	
	public void dispose()
	{
		close();
	}
	
	public void setLayoutData(Object arg0)
	{
		glcanvas.setLayoutData(arg0);
	}
	
	public void setLayout(Layout arg0)
	{
		glcanvas.setLayout(arg0);
	}
	
	public void setSize(int arg0, int arg1)
	{
		glcanvas.setSize(arg0, arg1);
	}
	
	public void setSliceSelectionInterface(SliceSelectionInterface adaptor)
	{
		sliceSelectionAdaptor = adaptor;
	}
	
	public void initialiseData()
	{
		Rectangle rectangle = glcanvas.getClientArea();
		
		if(_isHead)
		{
			//Texture3DPane geom = null;
			if(_type == TYPE_XRAY)
			{
				//geom = new XRayPane(_pane, _isHead);
				XRayPane geom = new XRayPane(_pane, _isHead);
				geom.initialiseData();
				geom.reshape(rectangle.width, rectangle.height);
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				//markerTexture.setup(glcontext.getGL().getGL2());
				geom.setup(glcontext.getGL().getGL2());
				glcontext.release();
				
				baseLevelAdaptor = geom;
				windowLevelAdaptor = geom;
				_geometry.add(geom);

			}
			else if(_type == TYPE_SLICE)
			{
				//geom = new SlicePane(_pane, _isHead);
				SlicePane geom = new SlicePane(_pane, _isHead);
				geom.initialiseData();
				markerAdaptor = ((SlicePane)geom).getMarkerInterface();
				planeAdaptor = ((SlicePane)geom);
				geom.reshape(rectangle.width, rectangle.height);
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				//markerTexture.setup(glcontext.getGL().getGL2());
				geom.setup(glcontext.getGL().getGL2());
				glcontext.release();
				
				baseLevelAdaptor = geom;
				windowLevelAdaptor = geom;
				_geometry.add(geom);
			}


		}
		else
		{
			//Texture3DPane geom = null;
			if(_type == TYPE_XRAY)
			{
				//geom = new XRayPane(_pane);
				XRayPane geom = new XRayPane(_pane);
				geom.reshape(rectangle.width, rectangle.height);
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				//markerTexture.setup(glcontext.getGL().getGL2());
				geom.setup(glcontext.getGL().getGL2());
				glcontext.release();
				
				baseLevelAdaptor = geom;
				windowLevelAdaptor = geom;
				_geometry.add(geom);

			}
			else if(_type == TYPE_SLICE)
			{
				//geom = new SlicePane(_pane);
				SlicePane geom = new SlicePane(_pane);
				markerAdaptor = ((SlicePane)geom).getMarkerInterface();
				planeAdaptor = ((SlicePane)geom);
				geom.reshape(rectangle.width, rectangle.height);
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				//markerTexture.setup(glcontext.getGL().getGL2());
				geom.setup(glcontext.getGL().getGL2());
				glcontext.release();
				
				baseLevelAdaptor = geom;
				windowLevelAdaptor = geom;
				_geometry.add(geom);
			}


		}
		
		switch (_pane) {
			case SlicePane.XY_PANE:
			{
				marking = new float[AppWindow.getDataStorage().getDimensions()[0]][AppWindow.getDataStorage().getDimensions()[1]];
				break;
			}
			case SlicePane.XZ_PANE:
			{
				marking = new float[AppWindow.getDataStorage().getDimensions()[0]][AppWindow.getDataStorage().getDimensions()[2]];
				break;
			}
			case SlicePane.YZ_PANE:
			{
				marking = new float[AppWindow.getDataStorage().getDimensions()[1]][AppWindow.getDataStorage().getDimensions()[2]];
				break;
			}
			default:
			{
				break;
			}
		}
		markingBins = new int[4096];
	}

	public GLCanvas getCanvasAsSharedHead()
	{
		return glcanvas;
	}
	
	public void paint()
	{

		glcanvas.setCurrent();
		glcontext.makeCurrent();
		_paneview.render(glcontext.getGL().getGL2());
		_geometry.render(glcontext.getGL().getGL2());
		
		Rectangle rectangle = glcanvas.getClientArea();
    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
    	_textRenderer.draw(_currentText, 0, 0);
    	_textRenderer.endRendering();
		
		glcanvas.swapBuffers();
		glcontext.release();
	}
	
	public void setup()
	{
		//gldata.doubleBuffer = true;
		//gldata.depthSize = 8;
		//glcanvas = new GLCanvas(_parent, SWT.NO_BACKGROUND, gldata);
		//glcanvas.setLayout(new FillLayout());
		//glcanvas.setSize(pRect.width, pRect.height);

		glcanvas.setCurrent();
		glprofile = GLProfile.getDefault();
		try {
			glcontext = GLDrawableFactory.getFactory(glprofile).createExternalGLContext();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Rectangle rectangle = glcanvas.getClientArea();
		glcontext.makeCurrent();
		
		_paneview.setup(glcontext.getGL().getGL2(), rectangle.width, rectangle.height);
		_paneview.render(glcontext.getGL().getGL2());
		_geometry.reshape(rectangle.width, rectangle.height);
		glcanvas.swapBuffers();
		glcontext.release();
		
		glcanvas.addListener(SWT.Resize, this);
		glcanvas.addListener(SWT.Paint, this);
		glcanvas.addListener(SWT.KeyDown, this);
		glcanvas.addListener(SWT.KeyUp, this);
		glcanvas.addListener(SWT.MouseDown, this);
		glcanvas.addListener(SWT.MouseUp, this);
		glcanvas.addListener(SWT.MouseMove, this);
		glcanvas.addListener(SWT.MouseWheel, this);
		glcanvas.addListener(SWT.Dispose, this);
	}
	
	public void close()
	{
		marking = null;
		markingBins = null;
		if((glcanvas!=null) && (glcanvas.isDisposed()==false))
		{
			glcanvas.setCurrent();
			glcontext.makeCurrent();
			_paneview.dispose();
			_geometry.dispose(glcontext.getGL().getGL2());
			glcanvas.dispose();
		}
		glcanvas=null;
		_parent=null;
	}
	
	private void resetMarking()
	{
		switch (_pane) {
			case SlicePane.XY_PANE:
			{
				for(int p=0; p<AppWindow.getDataStorage().getDimensions()[0]; p++)
					for(int q=0; q<AppWindow.getDataStorage().getDimensions()[1]; q++)
						marking[p][q]=0f;
				break;
			}
			case SlicePane.XZ_PANE:
			{
				for(int p=0; p<AppWindow.getDataStorage().getDimensions()[0]; p++)
					for(int q=0; q<AppWindow.getDataStorage().getDimensions()[2]; q++)
						marking[p][q]=0f;
				break;
			}
			case SlicePane.YZ_PANE:
			{
				for(int p=0; p<AppWindow.getDataStorage().getDimensions()[1]; p++)
					for(int q=0; q<AppWindow.getDataStorage().getDimensions()[2]; q++)
						marking[p][q]=0f;
				break;
			}
			default:
			{
				break;
			}
		}
		for(int i=0;i<4096;i++)
		{
			markingBins[i]=0;
		}
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		if(markerAdaptor!=null)
		{
	    	switch(_pane)
	    	{
		    	case Texture3DPane.XY_PANE: // correct
		    	{
		    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[1], glcontext.getGL().getGL2());
		    		break;
		    	}
		    	case Texture3DPane.XZ_PANE: // correct
		    	{
		    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[2], glcontext.getGL().getGL2());
		    		break;
		    	}
		    	case Texture3DPane.YZ_PANE: // correct
		    	{
		    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[1], AppWindow.getDataStorage().getDimensions()[2], glcontext.getGL().getGL2());
		    		break;
		    	}
	    	}
			
		}
		
		_isMarkingActive = false;

    	if(planeAdaptor!=null)
    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
		_paneview.render(glcontext.getGL().getGL2());
		_geometry.render(glcontext.getGL().getGL2());
		
		Rectangle rectangle = glcanvas.getClientArea();
    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
    	_textRenderer.draw(_currentText, 0, 0);
    	_textRenderer.endRendering();
		
		glcanvas.swapBuffers();
		glcontext.release();
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		switch(event.type)
		{
			case SWT.Resize: {
				Rectangle rectangle = glcanvas.getClientArea();
				glcanvas.setCurrent();
				glcontext.makeCurrent();
		    	if(planeAdaptor!=null)
		    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
		    	
				_paneview.reshape(rectangle.width, rectangle.height);
				_paneview.render(glcontext.getGL().getGL2());
				_geometry.reshape(rectangle.width, rectangle.height);
				_geometry.render(glcontext.getGL().getGL2());
				
		    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
		    	_textRenderer.draw(_currentText, 0, 0);
		    	_textRenderer.endRendering();
		    	
				glcanvas.swapBuffers();
				glcontext.release();
				if((AppWindow.getDataStorage().getDimensions()!=null) && (marking!=null) && (markingBins!=null))
					resetMarking();
				break;
			}
			case SWT.Paint: {
				glcanvas.setCurrent();
				glcontext.makeCurrent();
		    	if(planeAdaptor!=null)
		    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
				_paneview.render(glcontext.getGL().getGL2());
				_geometry.render(glcontext.getGL().getGL2());
				
				Rectangle rectangle = glcanvas.getClientArea();
		    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
		    	_textRenderer.draw(_currentText, 0, 0);
		    	_textRenderer.endRendering();
				
				glcanvas.swapBuffers();
				glcontext.release();
				break;
			}
			case SWT.KeyDown:
			{
				// select
				if(event.keyCode >=97 && event.keyCode <=122)
				{
					// characters
					switch(event.character)
					{
						case 'w':
						{
							_paneview.reduceDistance();
							break;
						}
						case 's':
						{
							_paneview.increaseDistance();
							break;
						}
						case 'a':
						{

							break;
						}
						case 'd':
						{

							break;
						}
					}
				}
				else
				{
					switch (event.keyCode) {
					case SWT.KEYPAD_ADD:
					{
						if(windowLevelAdaptor!=null)
							windowLevelAdaptor.increaseBrightness();
						break;
					}
					case SWT.KEYPAD_SUBTRACT:
					{
						if(windowLevelAdaptor!=null)
							windowLevelAdaptor.decreaseBrightness();
						break;
					}
					case SWT.CONTROL:
					{
						_currentMode = MODE_MEASURE;
						resetMarking();
						break;
					}
					case SWT.SHIFT:
					{
						_currentMode = MODE_HIGHLIGHT;
						resetMarking();
						
					}
					default:
						break;
					}
				}
				glcanvas.setCurrent();
				glcontext.makeCurrent();
		    	if(planeAdaptor!=null)
		    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
				_paneview.render(glcontext.getGL().getGL2());
				_geometry.render(glcontext.getGL().getGL2());
				
				Rectangle rectangle = glcanvas.getClientArea();
		    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
		    	_textRenderer.draw(_currentText, 0, 0);
		    	_textRenderer.endRendering();
				
				glcanvas.swapBuffers();
				glcontext.release();
				break;
			}
			case SWT.KeyUp:
			{
				switch(event.keyCode)
				{
				case SWT.CONTROL:
				{
					_currentMode = MODE_VIEW;
					break;
				}
				case SWT.SHIFT:
				{
					_currentMode = MODE_VIEW;
				}
				default:
					break;
				}
				break;
			}
			case SWT.MouseDown:
			{
				if(_currentMode==MODE_VIEW)
				{
					// select
					offset[0] = new Point(event.x, event.y);
				}
				else if((_currentMode==MODE_MEASURE) && (_type != TYPE_XRAY))
				{
					if((AppWindow.getDataStorage().getSpacing()==null)||(_paneview==null))
						break;
					
					glcanvas.setCurrent();
					glcontext.makeCurrent();
					Vector3f pointSelected;
					// Determine position
					Vector2f geomCoord = _paneview.getGeometryCoord(event.x, event.y, new Vector3f(AppWindow.getDataStorage().getSpacing()[0], AppWindow.getDataStorage().getSpacing()[1], AppWindow.getDataStorage().getSpacing()[2]), glcontext.getGL().getGL2());
			    	switch(_pane)
			    	{
				    	case Texture3DPane.XY_PANE: // correct
				    	{
				    		//pointSelected = new Vector3f(geomCoord.x*(_dimensions[0]*_spacing[0]), geomCoord.y*_dimensions[1]*_spacing[1], ((_dimensions[2]-1)-_curPlanesCoords.z)*_spacing[2]);
				    		pointSelected = new Vector3f(geomCoord.x*(AppWindow.getDataStorage().getDimensions()[0]*AppWindow.getDataStorage().getSpacing()[0]), geomCoord.y*AppWindow.getDataStorage().getDimensions()[1]*AppWindow.getDataStorage().getSpacing()[1], _curPlanesCoords.z*AppWindow.getDataStorage().getSpacing()[2]);
							_selectedPoints[numPointsSelected] = pointSelected;
				    		break;
				    	}
				    	case Texture3DPane.XZ_PANE: // correct
				    	{
				    		pointSelected = new Vector3f(((AppWindow.getDataStorage().getDimensions()[0]-1)-(geomCoord.x*AppWindow.getDataStorage().getDimensions()[0]))*AppWindow.getDataStorage().getSpacing()[0], ((AppWindow.getDataStorage().getDimensions()[1]-1)-_curPlanesCoords.y)*AppWindow.getDataStorage().getSpacing()[1], geomCoord.y*(AppWindow.getDataStorage().getDimensions()[2]*AppWindow.getDataStorage().getSpacing()[2]));
							_selectedPoints[numPointsSelected] = pointSelected;
				    		break;
				    	}
				    	case Texture3DPane.YZ_PANE: // correct
				    	{
				    		pointSelected = new Vector3f(_curPlanesCoords.x*AppWindow.getDataStorage().getSpacing()[0], ((AppWindow.getDataStorage().getDimensions()[1]-1)-(geomCoord.x*AppWindow.getDataStorage().getDimensions()[1]))*AppWindow.getDataStorage().getSpacing()[1], geomCoord.y*(AppWindow.getDataStorage().getDimensions()[2]*AppWindow.getDataStorage().getSpacing()[2]));
							_selectedPoints[numPointsSelected] = pointSelected;
				    		break;
				    	}
			    	}
			    	
				}
				else if((_currentMode == MODE_HIGHLIGHT) && (_type != TYPE_XRAY))
				{
					offset[0] = new Point(event.x, event.y);
				}
				break;
			}
			case SWT.MouseMove:
			{
				if(_currentMode==MODE_VIEW)
				{
					// x - Window; y - Level
					if(counter==10)
					{
						// shift plot
						if((offset[0]!=null) && (windowLevelAdaptor!=null))
						{
							Point deltaP = new Point(event.x - offset[0].x, event.y- offset[0].y);
							//System.out.printf("MouseMove: (%d,%d)\n", deltaP.x, deltaP.y);
							if(Math.abs(deltaP.x) > Math.abs(deltaP.y))
							{
							if(deltaP.x > 0)
								windowLevelAdaptor.increaseWindow();
							if(deltaP.x < 0)
								windowLevelAdaptor.decreaseWindow();
							}
							else
							{
							if(deltaP.y > 0)
								windowLevelAdaptor.increaseLevel();
							if(deltaP.y < 0)
								windowLevelAdaptor.decreaseLevel();
							}
							offset[0] = new Point(event.x, event.y);
						}
						counter=0;
					}
					else
					counter++;
				}
				else if((_currentMode==MODE_MEASURE) && (_type != TYPE_XRAY))
				{
					
				}
				else if((_currentMode == MODE_HIGHLIGHT) && (_type != TYPE_XRAY))
				{
					if((AppWindow.getDataStorage().getSpacing()==null)||(_paneview==null))
						break;
					
					glcanvas.setCurrent();
					glcontext.makeCurrent();
					
					// shift plot
					if((offset[0]!=null) && (_type != TYPE_XRAY))
					{
						Vector3i pointSelected = new Vector3i();
						Vector2f checkCenter = new Vector2f();
						Vector2f geomCoord = _paneview.getGeometryCoord(event.x, event.y, new Vector3f(AppWindow.getDataStorage().getSpacing()[0], AppWindow.getDataStorage().getSpacing()[1], AppWindow.getDataStorage().getSpacing()[2]), glcontext.getGL().getGL2());
				    	switch(_pane)
				    	{
					    	case Texture3DPane.XY_PANE:
					    	{
					    		//pointSelected = new Vector3i((int)(geomCoord.x*_dimensions[0]), (int)(geomCoord.y*_dimensions[1]), (int)((_dimensions[2]-1)-_curPlanesCoords.z));
					    		pointSelected = new Vector3i((int)((AppWindow.getDataStorage().getDimensions()[0]-1)-(geomCoord.x*AppWindow.getDataStorage().getDimensions()[0])), (int)(geomCoord.y*AppWindow.getDataStorage().getDimensions()[1]), (int)((AppWindow.getDataStorage().getDimensions()[2]-1)-_curPlanesCoords.z));
					    		checkCenter.x = pointSelected.x*AppWindow.getDataStorage().getSpacing()[0];
					    		checkCenter.y = pointSelected.y*AppWindow.getDataStorage().getSpacing()[1];
					    		marking[pointSelected.x][pointSelected.y] = 1f;
					    		break;
					    	}
					    	case Texture3DPane.XZ_PANE:
					    	{
					    		//pointSelected = new Vector3i((int)((_dimensions[0]-1)-(geomCoord.x*_dimensions[0])), (int)((_dimensions[1]-1)-_curPlanesCoords.y), (int)(geomCoord.y*_dimensions[2]));
					    		pointSelected = new Vector3i((int)(geomCoord.x*AppWindow.getDataStorage().getDimensions()[0]), (int)((AppWindow.getDataStorage().getDimensions()[1]-1)-_curPlanesCoords.y), (int)(geomCoord.y*AppWindow.getDataStorage().getDimensions()[2]));
					    		checkCenter.x = pointSelected.x*AppWindow.getDataStorage().getSpacing()[0];
					    		checkCenter.y = pointSelected.z*AppWindow.getDataStorage().getSpacing()[2];
					    		marking[pointSelected.x][pointSelected.z] = 1f;
					    		break;
					    	}
					    	case Texture3DPane.YZ_PANE: // correct
					    	{
					    		pointSelected = new Vector3i((int)(_curPlanesCoords.x), (int)((AppWindow.getDataStorage().getDimensions()[1]-1)-(geomCoord.x*AppWindow.getDataStorage().getDimensions()[1])), (int)(geomCoord.y*AppWindow.getDataStorage().getDimensions()[2]));
					    		checkCenter.x = pointSelected.y*AppWindow.getDataStorage().getSpacing()[1];
					    		checkCenter.y = pointSelected.z*AppWindow.getDataStorage().getSpacing()[2];
					    		marking[pointSelected.y][pointSelected.z] = 1f;
					    		break;
					    	}
				    	}
				    	
						//Vector2i texCoord = new Vector2i(geomCoord.x/)
						Vector2f checkPoint = new Vector2f();
						for(int p = -5; p < 6; p++)
						{
							for(int q = -5; q < 6; q++)
							{
						    	switch(_pane)
						    	{
							    	case Texture3DPane.XY_PANE:
							    	{
							    		checkPoint.x = (pointSelected.x+p)*AppWindow.getDataStorage().getSpacing()[0];
							    		checkPoint.y = (pointSelected.y+q)*AppWindow.getDataStorage().getSpacing()[1];
							    		if(checkCenter.distance(checkPoint)<5f)
							    		{
							    			marking[(pointSelected.x+p)][(pointSelected.y+q)]=1f;
							    		}
							    		break;
							    	}
							    	case Texture3DPane.XZ_PANE:
							    	{
							    		checkPoint.x = (pointSelected.x+p)*AppWindow.getDataStorage().getSpacing()[0];
							    		checkPoint.y = (pointSelected.z+q)*AppWindow.getDataStorage().getSpacing()[2];
							    		if(checkCenter.distance(checkPoint)<5f)
							    		{
							    			marking[(pointSelected.x+p)][(pointSelected.z+q)]=1f;
							    		}
							    		break;
							    	}
							    	case Texture3DPane.YZ_PANE: // correct
							    	{
							    		checkPoint.x = (pointSelected.y+p)*AppWindow.getDataStorage().getSpacing()[1];
							    		checkPoint.y = (pointSelected.z+q)*AppWindow.getDataStorage().getSpacing()[2];
							    		if(checkCenter.distance(checkPoint)<5f)
							    		{
							    			marking[(pointSelected.y+p)][(pointSelected.z+q)]=1f;
							    		}
							    		break;
							    	}
						    	}
							}
						}
						// send the float part off for texturing
						offset[0] = new Point(event.x, event.y);
						
					}
				}

				glcanvas.setCurrent();
				glcontext.makeCurrent();
				
				if(markerAdaptor!=null)
				{
			    	switch(_pane)
			    	{
				    	case Texture3DPane.XY_PANE: // correct
				    	{
				    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[1], glcontext.getGL().getGL2());
				    		break;
				    	}
				    	case Texture3DPane.XZ_PANE: // correct
				    	{
				    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[2], glcontext.getGL().getGL2());
				    		break;
				    	}
				    	case Texture3DPane.YZ_PANE: // correct
				    	{
				    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[1], AppWindow.getDataStorage().getDimensions()[2], glcontext.getGL().getGL2());
				    		break;
				    	}
			    	}
				}
				
		    	if(planeAdaptor!=null)
		    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
				_paneview.render(glcontext.getGL().getGL2());
				_geometry.render(glcontext.getGL().getGL2());
				
				Rectangle rectangle = glcanvas.getClientArea();
		    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
		    	_textRenderer.draw(_currentText, 0, 0);
		    	_textRenderer.endRendering();
				
				glcanvas.swapBuffers();
				glcontext.release();
				break;
			}
			case SWT.MouseUp:
			{
				if(_currentMode==MODE_VIEW)
				{
					offset[0] = null;
					counter=0;
					//curMode = BTN_MODE_SELECT;
				}
				else if((_currentMode==MODE_MEASURE) && (_type != TYPE_XRAY))
				{
					numPointsSelected++;
					if(numPointsSelected>1)
					{
						// calculate distance
						System.out.println(_selectedPoints[0].toString(new DecimalFormat( "#,###,###,##0.00" )));
						System.out.println(_selectedPoints[1].toString(new DecimalFormat( "#,###,###,##0.00" )));
						float distance = _selectedPoints[0].distance(_selectedPoints[1]);
						// print distance somewhere - Dialog ?! no, permanent, panel
						System.out.printf("Distance: %f\n", distance);
						MessageBox mb = new MessageBox(AppWindow.getShell(), SWT.ICON_INFORMATION | SWT.OK);
						mb.setText("Distance Measurement");
						mb.setMessage("Distance: "+Float.toString(distance));
						//int buttonID = mb.open();
						mb.open();
						
						numPointsSelected = 0;
					}
				}
				else if((_currentMode==MODE_HIGHLIGHT) && (_type != TYPE_XRAY))
				{
					offset[0] = null;
					counter=0;
					for(int p = 0; p < marking.length; p++)
					{
						for(int q = 0; q < marking[p].length; q++)
						{
							if(marking[p][q]>0.5f)
							{
								try
								{
							    	switch(_pane)
							    	{
								    	case Texture3DPane.XY_PANE: // correct
								    	{
								    		markingBins[(AppWindow.getDataStorage().getData()[p][q][(int)(_curPlanesCoords.z)])]++;
								    		break;
								    	}
								    	case Texture3DPane.XZ_PANE: // correct
								    	{
								    		markingBins[(AppWindow.getDataStorage().getData()[p][(int)((AppWindow.getDataStorage().getDimensions()[1]-1)-_curPlanesCoords.y)][q])]++;
								    		break;
								    	}
								    	case Texture3DPane.YZ_PANE: // correct
								    	{
								    		markingBins[(AppWindow.getDataStorage().getData()[(int)(_curPlanesCoords.x)][p][q])]++;
								    		break;
								    	}
							    	}
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
					if(sliceSelectionAdaptor!=null)
						sliceSelectionAdaptor.graphHistogramSelection(markingBins);
					
					glcanvas.setCurrent();
					glcontext.makeCurrent();
					if(markerAdaptor!=null)
					{
				    	switch(_pane)
				    	{
					    	case Texture3DPane.XY_PANE: // correct
					    	{
					    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[1], glcontext.getGL().getGL2());
					    		break;
					    	}
					    	case Texture3DPane.XZ_PANE: // correct
					    	{
					    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[0], AppWindow.getDataStorage().getDimensions()[2], glcontext.getGL().getGL2());
					    		break;
					    	}
					    	case Texture3DPane.YZ_PANE: // correct
					    	{
					    		markerAdaptor.updateFloatTexture(marking, AppWindow.getDataStorage().getDimensions()[1], AppWindow.getDataStorage().getDimensions()[2], glcontext.getGL().getGL2());
					    		break;
					    	}
				    	}
					}
					_isMarkingActive = true;

			    	if(planeAdaptor!=null)
			    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
					_paneview.render(glcontext.getGL().getGL2());
					_geometry.render(glcontext.getGL().getGL2());
					
					Rectangle rectangle = glcanvas.getClientArea();
			    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
			    	_textRenderer.draw(_currentText, 0, 0);
			    	_textRenderer.endRendering();
					
					glcanvas.swapBuffers();
					glcontext.release();
				}
				break;
			}
			case SWT.MouseWheel: {
				if(_isMarkingActive==true)
					resetMarking();
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				
		    	switch(_pane)
		    	{
			    	case Texture3DPane.XY_PANE:
			    	{
						if(event.count<0)
							backwardXY();
						else
							forwardXY();
			    		break;
			    	}
			    	case Texture3DPane.XZ_PANE:
			    	{
						if(event.count<0)
							backwardXZ();
						else
							forwardXZ();
			    		break;
			    	}
			    	case Texture3DPane.YZ_PANE:
			    	{
						if(event.count<0)
							backwardYZ();
						else
							forwardYZ();
			    		break;
			    	}
		    	}
		    	if(planeAdaptor!=null)
		    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);

		    	
				_paneview.render(glcontext.getGL().getGL2());
				_geometry.render(glcontext.getGL().getGL2());
				
				Rectangle rectangle = glcanvas.getClientArea();
		    	_textRenderer.beginRendering(rectangle.width, rectangle.height);
		    	_textRenderer.draw(_currentText, 0, 0);
		    	_textRenderer.endRendering();

				glcanvas.swapBuffers();
				glcontext.release();
				break;
			}
			case SWT.Dispose: {
				System.out.println("Closing ...");
				close();
				break;
			}
		}
	}

	@Override
	public void center(int value, int occurence) {
		// TODO Auto-generated method stub
		if(baseLevelAdaptor!=null)
		{
			baseLevelAdaptor.centerValue(value, occurence);
		}
		
		/*
		glcanvas.setCurrent();
		glcontext.makeCurrent();
    	if(planeAdaptor!=null)
    		planeAdaptor.setTranslateTexture(_textureTranslate[0], _textureTranslate[1], _textureTranslate[2]);
		_paneview.render(glcontext.getGL().getGL2());
		_geometry.render(glcontext.getGL().getGL2());
		glcanvas.swapBuffers();
		glcontext.release();
		*/
		paint();
	}
	
    public void forwardXY()
    {
    	if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getDimensions().length>=3))
    	{
    		_curPlanesCoords.z = Math.min(_curPlanesCoords.z+1f, ((float)AppWindow.getDataStorage().getDimensions()[2])-1f);
    		_textureTranslate[2] = Math.min(_textureTranslate[2]+1f, ((float)AppWindow.getDataStorage().getDimensions()[2]));
    		//_textureTranslate[2] = Math.max(_textureTranslate[2]-1f, -(_dimensions[2]));
	    	//System.out.println("XY plane: "+Float.toString(_textureTranslate[2]));
	    	
	    }
    	if(sliceSelectionAdaptor!=null)
    	{
    		//float adapt = (((float)_dimensions[2])-Math.abs(_curPlanesCoords.z)) % ((float)_dimensions[2]); 
    		//sliceSelectionAdaptor.showHistogramXY((int)adapt);
    		sliceSelectionAdaptor.showHistogramXY((int)_curPlanesCoords.z);
    		//System.out.println("current Z-Plane: "+_curPlanesCoords.z);
    		//System.out.println("TexCoordZ: "+_textureTranslate[2]);
    	}
    }
    
    public void backwardXY()
    {
    	if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getDimensions().length>=3))
    	{
    		_curPlanesCoords.z = Math.max(_curPlanesCoords.z-1f, 0f);
    		_textureTranslate[2] = Math.max(_textureTranslate[2]-1f, 1f);
    		//_textureTranslate[2] = Math.min(_textureTranslate[2]+1f, -1f);
	    	//System.out.println("XY plane: "+Float.toString(_textureTranslate[2]));
    	}
    	if(sliceSelectionAdaptor!=null)
    	{
    		//float adapt = (((float)_dimensions[2])-Math.abs(_curPlanesCoords.z)) % ((float)_dimensions[2]); 
    		//sliceSelectionAdaptor.showHistogramXY((int)adapt);
    		sliceSelectionAdaptor.showHistogramXY((int)_curPlanesCoords.z);
    		System.out.println("current Z-Plane: "+_curPlanesCoords.z);
    		System.out.println("TexCoordZ: "+_textureTranslate[2]);
    	}
    }

    public void forwardXZ()
    {
    	if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getDimensions().length>=3))
    	{
    		_curPlanesCoords.y = Math.min(_curPlanesCoords.y+1f, ((float)AppWindow.getDataStorage().getDimensions()[1])-1f);
	    	//_textureTranslate[1] -= 1.0f;
	    	_textureTranslate[1] = Math.max(_textureTranslate[1]-1f, -(AppWindow.getDataStorage().getDimensions()[1]));
	    	//System.out.println("XZ plane: "+Float.toString(_textureTranslate[1]));
	    }
    	if(sliceSelectionAdaptor!=null)
    	{
    		sliceSelectionAdaptor.showHistogramXZ((AppWindow.getDataStorage().getDimensions()[1]-1)-Math.abs((int)_curPlanesCoords.y));
    	}
    }
    
    public void backwardXZ()
    {
    	if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getDimensions().length>=3))
    	{
    		_curPlanesCoords.y = Math.max(_curPlanesCoords.y-1f, 0);
	    	//_textureTranslate[1] += 1.0f;
	    	_textureTranslate[1] = Math.min(_textureTranslate[1]+1f, -1f);
	    	//System.out.println("XZ plane: "+Float.toString(_textureTranslate[1]));
    	}
    	if(sliceSelectionAdaptor!=null)
    	{
    		sliceSelectionAdaptor.showHistogramXZ((AppWindow.getDataStorage().getDimensions()[1]-1)-Math.abs((int)_curPlanesCoords.y));
    	}
    }
    
    public void forwardYZ()
    {
    	if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getDimensions().length>=3))
    	{
    		_curPlanesCoords.x = Math.min(_curPlanesCoords.x+1f, AppWindow.getDataStorage().getDimensions()[0]-1);
    		//_textureTranslate[0] -= 1.0f;
    		_textureTranslate[0] = Math.max(_textureTranslate[0]-1f, -(AppWindow.getDataStorage().getDimensions()[0]));
    	//	System.out.println("YZ plane: "+Float.toString(_textureTranslate[0]));
    	}
    	if(sliceSelectionAdaptor!=null)
    	{
    		sliceSelectionAdaptor.showHistogramYZ(Math.abs((int)_curPlanesCoords.x));
    	}
    }
    
    public void backwardYZ()
    {
    	if((AppWindow.getDataStorage().getDimensions()!=null) && (AppWindow.getDataStorage().getDimensions().length>=3))
    		_curPlanesCoords.x = Math.max(_curPlanesCoords.x-1f, 0);
    	//_textureTranslate[0] += 1.0f;
    	_textureTranslate[0] = Math.min(_textureTranslate[0]+1f, -1f);
    	//System.out.println("YZ plane: "+Float.toString(_textureTranslate[0]));
    	if(sliceSelectionAdaptor!=null)
    	{
    		sliceSelectionAdaptor.showHistogramYZ(Math.abs((int)_curPlanesCoords.x));
    	}
    }
    
    public void resetView()
    {
    	_textureTranslate[0] = -1.0f;
    	_textureTranslate[1] = -1.0f;
    	_textureTranslate[2] = 1.0f;
    	_curPlanesCoords = new Vector3f(0f, 0f, 0f);
    }

	@Override
	public void markValues(Vector2i range) {
		// TODO Auto-generated method stub
		if(baseLevelAdaptor!=null)
		{
			baseLevelAdaptor.markValues(range);
		}
		paint();
	}

	@Override
	public void resetHistogramSelection() {
		// TODO Auto-generated method stub
		if(baseLevelAdaptor!=null)
		{
			baseLevelAdaptor.resetHistogramSelection();
		}
		paint();
	}
}
