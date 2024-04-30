package de.ckehl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
//import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.gl.CLGLContext;
import com.jogamp.opencl.CLCommandQueue.Mode;
import com.jogamp.opencl.CLPlatform;

public class CubrilleView implements Listener, ViewUpdateInterface {
	
	public static int SCALEX = 1;
	public static int SCALEZ = 1;
	public static int SCALEY = 1;

	protected static final int MODE_INTERACT_MODEL = 0;
	protected static final int MODE_INTERACT_CLIPPING = 1;
	protected static final int MODE_INTERACT_RAYCAST = 2;
	protected int _currentInteractionMode = 0;
	
	protected Composite _parent = null;
	
	protected GLData gldata;
	protected GLCanvas glcanvas;
	protected GLProfile glprofile;
	protected GLContext glcontext;
	
	protected String _currentText = "";
	
	private boolean _isOpen = false;
	private boolean _useInstancedRendering = false;
	protected boolean _isLightOn = false;
	protected boolean _activeNormalVis = false;

	protected int num_elements = 0;

	protected ProjectionView _view = null;
	protected Geometry _geometry = null;
	protected Texture1D _lutGeometry = null;
	protected FunctionTexture _gaussTexture = null;
	protected ClippingPlane _clipPlaneGeometry = null;
	private InstancedCubrille cubeGeometry = null;
	
	protected LUTinterface _LUTproducer = null;
	protected LUTinterface _LUTconsumer = null;
	protected LUTinterface _LUTtexConsumer = null;
	protected GaussCurveInterface _gaussProducer = null;
	protected GaussCurveInterface _gaussConsumer = null;
	
	protected RaySelectionInterface raySelectionAdaptors[] = null;
	protected HistogramSelectionInterface histoSelectionInterface = null;
	protected IntersectVolume _volumeIntersection = null;
	protected ClippingMatrixInterface _clippingInteraction = null;
	
	protected ViewPlaneInterface _VPproducer = null;
	protected ViewPlaneInterface _VPconsumer = null;
	
	protected LightInformationInterface _LIproducer = null;
	protected LightInformationInterface _LIconsumer = null;
	
	protected NormalUpdateInterface _NormalConsumer = null;
	
	protected int counter = 0;
	protected Point[] offset = new Point[1];
	
    private CLGLContext clContext = null;
    protected CLCommandQueue _commandQueue = null;
    
    protected static String ModeToText(int mode)
    {
    	String result = "";
    	switch(mode)
    	{
	    	case MODE_INTERACT_MODEL:
	    	{
	    		result = "Scene Interaction";
	    		break;
	    	}
	    	case MODE_INTERACT_CLIPPING:
	    	{
	    		result = "Clipping";
	    		break;
	    	}
	    	case MODE_INTERACT_RAYCAST:
	    	{
	    		result = "Raycasting Statistics";
	    		break;
	    	}
	    	default:
	    		break;
    	}
    	MessageBox mb = new MessageBox(AppWindow.getShell(), SWT.ICON_INFORMATION | SWT.OK);
    	mb.setMessage(result);
    	mb.open();
    	
    	return result;
    }
	
	public CubrilleView(Composite arg0, int arg1, LUTinterface lutGetter)
	{
		_LUTproducer = lutGetter;
		gldata = new GLData();
		gldata.doubleBuffer = true;
		
		gldata.depthSize = 16;
		
		_parent = arg0;
		
		glcanvas = new GLCanvas(_parent, SWT.NO_BACKGROUND, gldata);
		_view = new ProjectionView();
		_VPproducer = _view;
		_geometry = new GeometryContainer();
		_lutGeometry = new Texture1D();
		_gaussTexture = new FunctionTexture();
		
		raySelectionAdaptors = new RaySelectionInterface[2];
		
		// to be removed - later added at runtime ? or hide ? - HIDE, but also make function
		// to get leaf childs in geometry graph to add cubrilles to last instance without double-copy
		_clipPlaneGeometry = new ClippingPlane();
		_clippingInteraction = _clipPlaneGeometry;
		_lutGeometry.add(_clipPlaneGeometry);

		_geometry.add(_lutGeometry);
		_LUTconsumer = _lutGeometry;
		
		_volumeIntersection = new IntersectVolume();
		_volumeIntersection.setCubeTranslation(0f, 0f, 0f);
		_isOpen = true;
	}
	
	public void dispose()
	{
		if((glcanvas!=null) && (glcanvas.isDisposed()==false))
		{
			glcanvas.setCurrent();
			glcontext.makeCurrent();
			if(_geometry != null)
			{
				_geometry.dispose(glcontext.getGL().getGL2());
				_geometry = null;
			}
			if((_useInstancedRendering) && (_geometry!=null))
			{
				_geometry.dispose(glcontext.getGL().getGL3(), _commandQueue);
				_commandQueue.release();
				_commandQueue = null;
				clContext.release();
				clContext = null;
			}
			glcontext.release();
			glcanvas.dispose();
			glcanvas = null;
			
			_volumeIntersection = null;
		}
		_parent = null;
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
	
	public void setRaySelectionInterface(RaySelectionInterface adaptor)
	{
		raySelectionAdaptors[0] = adaptor;
	}

	public void setGaussProducer(GaussCurveInterface producer)
	{
		_gaussProducer = producer;
	}
	
	public void setLightInformationProducer(LightInformationInterface producer)
	{
		_LIproducer = producer;
	}
	
	public HistogramSelectionInterface getHistrogramSelectionAdaptor()
	{
		return histoSelectionInterface;
	}
	
	public void initialiseData()
	{
		if(AppWindow.getDataStorage().getDimensions()!=null)
		{
			short[] dims = AppWindow.getDataStorage().getDimensions();
			_view.setDimensions((float)dims[0], (float)dims[1], (float)dims[2]);
			_view.setCenter(((float)dims[0])/2.0f, ((float)dims[1])/2.0f, 0f);
			_view.setViewPoint(((float)dims[0])/2.0f, ((float)dims[1])/2.0f, ((float)dims[2])+10.0f);
			
			_volumeIntersection.setDimensions((int)dims[0], (int)dims[1], (int)dims[2]);
		}
		if((AppWindow.getDataStorage().getSpacing()!=null) && (AppWindow.getDataStorage().getDimensions()!=null))
		{
			short[] dims = AppWindow.getDataStorage().getDimensions();
			float[] spacs = AppWindow.getDataStorage().getSpacing();
			_view.setCenter(((float)dims[0])*spacs[0]/2.0f, ((float)dims[1])*spacs[1]/2.0f, 0f);
			_view.setViewPoint(((float)dims[0])*spacs[0]/2.0f, ((float)dims[1])*spacs[1]/2.0f, ((float)dims[2])*spacs[2]+10.0f);
			_volumeIntersection.setSpacing(spacs[0], spacs[1], spacs[2]);
		}
	}
	
	public void paint()
	{
		glcanvas.redraw();
	}
	
	public void setup()
	{
		Rectangle pRect = _parent.getClientArea();
		glcanvas.setCurrent();
		glprofile = GLProfile.getDefault();
		try {
			glcontext = GLDrawableFactory.getFactory(glprofile).createExternalGLContext();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		
		boolean gl3Available = true;
		try
		{
			GL3 glTry = glcontext.getGL().getGL3();
		}
		catch (GLException e)
		{
			e.printStackTrace();
			System.out.println("No GL3 available - using normal cubrilles.");
			gl3Available = false;
		}
		
		if(gl3Available)
		{
			try
			{
				//glcontext.getGL().getGL3();
				
				//-------------
				// OpenCL part
				//-------------
				if(InstancedCubrille.renderMode==InstancedCubrille.RENDERMODE_ORDER_GPU)
				{
					CLDevice[] devices = CLPlatform.getDefault().listCLDevices();
					CLDevice device = null;
					for(CLDevice d : devices)
					{
						if(d.isGLMemorySharingSupported())
						{
							device = d;
							break;
						}
					}
					if(device == null)
					{
						throw new GLException("couldn't find any CL/GL memory sharing devices ..");
					}
					
					clContext = CLGLContext.create(glcontext, device);
					try
					{
						_commandQueue = clContext.getMaxFlopsDevice().createCommandQueue();
						System.out.println("CommQueue: ID: "+_commandQueue.getID());
						for(Mode entry:  _commandQueue.getProperties())
						{
							System.out.println("Property: "+entry.toString());
						}
						System.out.println("Device: "+_commandQueue.getDevice().getID()+", "+_commandQueue.getDevice().getName());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
		            // enable GL error checking using the composable pipeline
		            //drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
					GL3 gl = glcontext.getGL().getGL3();
					gl.setSwapInterval(1);
					
					System.out.println("created shared CL-GL context.");
				}
				_useInstancedRendering = true;
			}
			catch (GLException e)
			{
				e.printStackTrace();
				System.out.println("No OpenCL available.");
				//glcontext = GLDrawableFactory.getFactory(glprofile).createExternalGLContext();
			}
		}
		
		
		
		
		_view.setup(glcontext.getGL().getGL2());
		_view.setWidth(pRect.width, glcontext.getGL().getGL2());
		_view.setHeight(pRect.height, glcontext.getGL().getGL2());
		_view.render(glcontext.getGL().getGL2());
		
    	//_textRenderer.beginRendering(pRect.width, pRect.height);
    	//_textRenderer.draw(_currentText, 0, 0);
    	//_textRenderer.endRendering();
		
		glcontext.release();
		
		glcanvas.addListener(SWT.Resize, this);
		glcanvas.addListener(SWT.Paint, this);
		glcanvas.addListener(SWT.KeyDown, this);
		glcanvas.addListener(SWT.KeyUp, this);
		glcanvas.addListener(SWT.MouseWheel, this);
		glcanvas.addListener(SWT.MouseDown, this);
		glcanvas.addListener(SWT.MouseUp, this);
		glcanvas.addListener(SWT.MouseMove, this);
	}
	
	
	
	public void close()
	{
		if(_isOpen==true)
		{
			this.dispose();
		}
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
				_view.setWidth(rectangle.width, glcontext.getGL().getGL2());
				_view.setHeight(rectangle.height, glcontext.getGL().getGL2());
				_view.render(glcontext.getGL().getGL2());
				
		    	//_textRenderer.beginRendering(rectangle.width, rectangle.height);
		    	//_textRenderer.draw(_currentText, 0, 0);
		    	//_textRenderer.endRendering();
				
				glcanvas.swapBuffers();
				glcontext.release();
				update();
				break;
			}
			case SWT.Paint: {
				update();
				break;
			}
			case SWT.MouseMove: {
				
				Point deltaP = null;
				boolean doX = false;
				//if(counter==2)
				//{
					if(offset[0]!=null)
					{
						deltaP = new Point(event.x - offset[0].x, event.y- offset[0].y);
						//System.out.printf("dP: %d, %d\n", deltaP.x, deltaP.y);
						offset[0] = new Point(event.x, event.y);
						if(Math.abs(deltaP.x) > Math.abs(deltaP.y))
							doX = true;
						else
							doX = false;
					}
					//counter=0;
				//}
				//else
				//	counter++;
				
				if(deltaP==null)
				{
					break;
				}
				
				switch (_currentInteractionMode) {
					case MODE_INTERACT_MODEL:
					{
						/*
						 * Mouse Interaction for doing model interaction
						 */
						if((event.stateMask & SWT.BUTTON1) != 0) // LEFT
						{
							// x -> yRotations
							if(doX)
							{
								if(deltaP.x>0)
								{
									_view.turnRight();
								}
								else if(deltaP.x<0)
								{
									_view.turnLeft();
								}
							}
							else
							{
								if(deltaP.y>0)
								{
									_view.turnUp();
								}
								else if(deltaP.y<0)
								{
									_view.turnDown();
								}
							}
						}
						else if((event.stateMask & SWT.BUTTON2) != 0) // MIDDLE
						{
							
						}
						else if((event.stateMask & SWT.BUTTON3) != 0) // RIGHT
						{
							// x -> yRotations
							if(doX)
							{
								if(deltaP.x>0)
								{
									_view.strafeRight();
								}
								else if(deltaP.x<0)
								{
									_view.strafeLeft();
								}
							}
							else
							{
								if(deltaP.y>0)
								{
									_view.strafeUp();
								}
								else if(deltaP.y<0)
								{
									_view.strafeDown();
								}
							}
						}
						break;
					}
					case MODE_INTERACT_CLIPPING:
					{
						//if(deltaP!=null)
						//	System.out.printf("dP: %d, %d\n", deltaP.x, deltaP.y);
						/*
						 * Mouse Interaction for doing clipping interaction
						 */
						if((event.stateMask & SWT.BUTTON1) != 0) // LEFT
						{
							// x -> yRotations
							if(doX)
							{
								if(deltaP.x>0)
								{
									_clippingInteraction.IncreaseYRotation();
								}
								else if(deltaP.x<0)
								{
									_clippingInteraction.DecreaseYRotation();
								}
							}
							else
							{
								if(deltaP.y>0)
								{
									_clippingInteraction.IncreaseXRotation();
								}
								else if(deltaP.y<0)
								{
									_clippingInteraction.DecreaseXRotation();
								}
							}
							System.out.printf("Rotation Clipping: %f,  %f, %f\n", _clippingInteraction.getRotateXModel(), _clippingInteraction.getRotateYModel(), _clippingInteraction.getRotateZModel());
						}
						else if((event.stateMask & SWT.BUTTON2) != 0) // MIDDLE
						{
							
						}
						else if((event.stateMask & SWT.BUTTON3) != 0) // RIGHT
						{
							
						}
						break;
					}
					case MODE_INTERACT_RAYCAST:
					{
						if((event.stateMask & SWT.BUTTON1) != 0)
						{
							if(doX)
							{
								if(deltaP.x>0)
								{
									_view.turnRight();
								}
								else if(deltaP.x<0)
								{
									_view.turnLeft();
								}
							}
							else
							{
								if(deltaP.y>0)
								{
									_view.turnUp();
								}
								else if(deltaP.y<0)
								{
									_view.turnDown();
								}
							}
							break;
						}
					}
					default:
						break;
				}
				update();
				break;
			}
			case SWT.MouseDown: {
				System.out.println("Button pressed: "+event.button);
				/*
				if(event.button == SWT.BUTTON1)
					System.out.println("equals SWT.BUTTON1");
				else if(event.button == SWT.BUTTON2)
					System.out.println("Button pressed: "+event.button+", equals SWT.BUTTON2");
				else if(event.button == SWT.BUTTON3)
					System.out.println("Button pressed: "+event.button+", equals SWT.BUTTON3");
				else if(event.button == SWT.BUTTON4)
					System.out.println("Button pressed: "+event.button+", equals SWT.BUTTON4");
				else if(event.button == SWT.BUTTON5)
					System.out.println("Button pressed: "+event.button+", equals SWT.BUTTON5");
				*/
				if(offset[0] == null)
					offset[0] = new Point(event.x, event.y);
				if(_currentInteractionMode==MODE_INTERACT_CLIPPING)
				{
					switch(event.button)
					{
						case 1:	// LEFT
						{
							
							break;
						}
						case 2:	// MIDDLE
						{
							_clippingInteraction.Toggle();
							System.out.println("Clipping activated.");
							break;
						}
						case 3:	// RIGHT
						{
							_clippingInteraction.invertNormalDirection();
							System.out.println(_clipPlaneGeometry.getNormalVector().toString(AppWindow._vecStdFmt));
							break;
						}
					}
					//_currentText = ModeToText(_currentInteractionMode);
				}
				else if(_currentInteractionMode==MODE_INTERACT_RAYCAST)
				{
					switch(event.button)
					{
						case 1:	// LEFT
						{
							
							break;
						}
						case 2:	// MIDDLE
						{
							break;
						}
						case 3:	// RIGHT
						{
							break;
						}
					}
					//_currentText = ModeToText(_currentInteractionMode);
				}
				update();
				break;
			}
			case SWT.MouseUp: {
				// stop any move-thingy
				offset[0] = null;
				counter=0;
				
				if(_currentInteractionMode==MODE_INTERACT_RAYCAST)
				{
					switch(event.button)
					{
						case 1:	// LEFT
						{

							break;
						}
						case 2:	// MIDDLE
						{

							break;
						}
						case 3:	// RIGHT
						{
							Ray r = _view.getViewRay(event.x, event.y);
							//System.out.println(r.toString());
							_volumeIntersection.setRay(r);
							_volumeIntersection.setModelMatrix(_view.getModelMatrix());
							_volumeIntersection.setViewMatrix(_view.getViewMatrix());
							List<Vector3i> intersections = _volumeIntersection.computeIntersections();
							//System.out.println("# intersections: "+Integer.toString(intersections.size()));
							for(RaySelectionInterface adaptor: raySelectionAdaptors)
							{
								//System.out.println("# intersections: "+Integer.toString(intersections.size()));
								if(adaptor!=null)
								{
									if((SCALEX==SCALEY)&&(SCALEX==SCALEZ)&&(SCALEY==SCALEZ)&&(SCALEX==1))
									{
										//System.out.println("Marking in original scale. Isize: "+Integer.toString(intersections.size()));
										adaptor.setRayInformation(intersections);
									}
									else
									{
										//System.out.println("Marking in scale ("+Integer.toString(SCALEX)+","+Integer.toString(SCALEY)+","+Integer.toString(SCALEZ)+"). Isize: "+Integer.toString(intersections.size()));
										adaptor.setRayInformation(scaleAdaptIntersections(intersections));
									}
								}
							}
							break;
						}
					}
				}
				else if(_currentInteractionMode==MODE_INTERACT_CLIPPING)
				{
					switch(event.button)
					{
						case 1:	// LEFT
						{

							break;
						}
						case 2:	// MIDDLE
						{

							break;
						}
						case 3:	// RIGHT
						{

							break;
						}
					}
				}
				update();
				break;
			}
			case SWT.MouseWheel: {
				switch (_currentInteractionMode) {
					case MODE_INTERACT_MODEL:
					{
						/*
						 * Mouse Interaction for doing model interaction
						 */
						if(event.count<0)
							_view.stepForward();
						else
							_view.stepBackward();
						break;
					}
					case MODE_INTERACT_CLIPPING:
					{
						if(event.count<0)
							_clippingInteraction.ForwardZ();
						else
							_clippingInteraction.BackwardZ();
						
						//_currentText = ModeToText(_currentInteractionMode);
						System.out.println(_clippingInteraction.getTranslateModel().toString(AppWindow._vecStdFmt));
						break;
					}
				}
				update();
				break;
			}
			case SWT.KeyDown: {
				if(event.keyCode >=97 && event.keyCode <=122)
				{
					// characters
					switch(event.character)
					{
						case 'w':
						{
							_view.strafeUp();
							break;
						}
						case 's':
						{
							_view.strafeDown();
							break;
						}
						case 'a':
						{
							_view.strafeLeft();
							break;
						}
						case 'd':
						{
							_view.strafeRight();
							break;
						}
					}
				}
				else if(event.keyCode >=48 && event.keyCode <=57)
				{
					//check digit
				}
				else
				{
					switch(event.keyCode)
					{
						case SWT.SHIFT:
						{
							break;
						}
						case SWT.CONTROL:
						{
							if(_currentInteractionMode!=MODE_INTERACT_CLIPPING)
								_currentInteractionMode = MODE_INTERACT_CLIPPING;
							else
								_currentInteractionMode = MODE_INTERACT_MODEL;
							
							_currentText = ModeToText(_currentInteractionMode);
							update();
							
							break;
						}
						case SWT.ALT:
						{
							if(_currentInteractionMode!=MODE_INTERACT_RAYCAST)
								_currentInteractionMode = MODE_INTERACT_RAYCAST;
							else
							{
								_currentInteractionMode = MODE_INTERACT_MODEL;
								for(RaySelectionInterface adaptor: raySelectionAdaptors)
								{
									//System.out.println("# intersections: "+Integer.toString(intersections.size()));
									if(adaptor!=null)
										adaptor.resetRayInformation();
								}

							}
							
							_currentText = ModeToText(_currentInteractionMode);
							update();
							
							break;
						}
						case SWT.ARROW_UP:
						{
							_view.turnUp();
							break;
						}
						case SWT.ARROW_DOWN:
						{
							_view.turnDown();
							break;
						}
						case SWT.ARROW_LEFT:
						{
							_view.turnLeft();
							break;
						}
						case SWT.ARROW_RIGHT:
						{
							_view.turnRight();
							break;
						}
						case SWT.BS:
						{
							break;
						}
					}
				}
				update();
				break;
			}
			case SWT.KeyUp: {
				switch(event.keyCode)
				{
					case SWT.SHIFT:
					{
						break;
					}
					case SWT.CONTROL:
					{
						break;
					}
					case SWT.ALT:
					{
						break;
					}
					case SWT.ARROW_UP:
					{

						break;
					}
					case SWT.ARROW_DOWN:
					{

						break;
					}
					case SWT.ARROW_LEFT:
					{

						break;
					}
					case SWT.ARROW_RIGHT:
					{

						break;
					}
					case SWT.BS:
					{
						break;
					}
				}
				break;
			}
			case SWT.Dispose: {
				System.out.println("Closing ...");
				close();
				break;
			}
		}
	}
	
	public void resetView()
	{
		
	}
	
	public void setBackgroundColour(float r, float g, float b)
	{
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		_view.setBackgroundColour(new Vector3f(r,g,b), glcontext.getGL().getGL2());
		//glcanvas.swapBuffers();
		glcontext.release();
		update();
	}
	
	public void makeLight()
	{
		if(_LIconsumer!=null)
		{
			if(_LIproducer!=null)
			{
				_LIconsumer.setLightPosition(_LIproducer.getLightPosition());
				_LIconsumer.setLightImpact(_LIproducer.getLightImpact());
			}
			_LIconsumer.EnableLighting();
		}
		_geometry.forceUpdate();
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		synchronized (_geometry) {
			_geometry.setup(glcontext.getGL().getGL2());
			if(_useInstancedRendering)
			{
				_geometry.setup(glcontext.getGL().getGL3(), _commandQueue);
			}
		}
		glcontext.release();
		update();
		update();
	}
	
	public void removeLight()
	{
		if(_LIconsumer!=null)
		{
			_LIconsumer.DisableLighting();
		}
		_geometry.forceUpdate();
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		synchronized (_geometry) {
			_geometry.setup(glcontext.getGL().getGL2());
			if(_useInstancedRendering)
			{
				_geometry.setup(glcontext.getGL().getGL3(), _commandQueue);
			}
		}
		glcontext.release();
		update();
		update();
	}
	
	public void switchLight()
	{
		if(_isLightOn)
		{
			removeLight();
			_isLightOn = false;
		}
		else
		{
			makeLight();
			_isLightOn = true;
		}
	}
	
	public boolean isLightOn()
	{
		return _isLightOn;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		GL3 gl3 = null;
		if(_useInstancedRendering)
			gl3 = glcontext.getGL().getGL3();
		GL2 gl2 = glcontext.getGL().getGL2();
		/*
		 * First step: compute viewing plane
		 * Comment: done per-point inside geometry now
		 */
		if(_useInstancedRendering && (InstancedCubrille.renderMode==InstancedCubrille.RENDERMODE_PRECOMPUTE))
		{
			_view.computePlaneViewpoint();
		}
		else if(_useInstancedRendering && (InstancedCubrille.renderMode==InstancedCubrille.RENDERMODE_ORDER_GPU))
		{
			gl3.glFinish();
			_geometry.compute(_commandQueue);
		}
		
		if((_VPproducer!=null) && (_VPconsumer!=null))
		{
			//if(! _VPconsumer.getPlaneViewpoint().equals(_VPproducer.getPlaneViewpoint()))
			//{
				_VPconsumer.setPlaneViewpoint(_VPproducer.getPlaneViewpoint());
				_VPconsumer.update(glcontext.getGL().getGL2());
				if(_useInstancedRendering)
				{
					_VPconsumer.update(glcontext.getGL().getGL3());
				}
			//}
		}
		
		if((_LIconsumer!=null) && (_LIproducer!=null) && (_isLightOn))
		{
			Vector3f vLIcurCon = _LIconsumer.getLightPosition(), vLIcurPro = _LIproducer.getLightPosition();
			if((vLIcurCon.x != vLIcurPro.x) || (vLIcurCon.y != vLIcurPro.y) || (vLIcurCon.z != vLIcurPro.z))
			{
				_LIconsumer.setLightPosition(_LIproducer.getLightPosition());
				_LIconsumer.updateLightPosition();
			}
			_LIconsumer.setLightImpact(_LIproducer.getLightImpact());
			_LIconsumer.updateLightImpact();
		}
		

		_view.render(gl2);
		
		/*
		 * LUT step
		 */
		if(_LUTproducer!=null)
		{
			if(_LUTproducer.isUpdated())
			{
				if(_LUTconsumer!=null)
					_LUTconsumer.Update(false);
				_LUTproducer.Update(false);
			}
		}
		if((_LUTconsumer!=null) && (_LUTproducer!=null))
		{
			if(_LUTconsumer.isUpdated()==false)
			{
				_LUTconsumer.setLUT(_LUTproducer.getLUT());
				// the consumer (a geometry) updates its new
				// state during the setup() procedure,
				// hence no exterior update call
			}
		}

		if(_gaussProducer!=null)
		{
			if(_gaussProducer.isUpdated())
			{
				if(_gaussConsumer!=null)
					_gaussConsumer.Update(false);
				_gaussProducer.Update(false);
			}
		}
		if((_gaussConsumer!=null) && (_gaussProducer!=null))
		{
			if(_gaussConsumer.isUpdated()==false) {
				if(_gaussProducer.isCurveSelected()) {
					System.out.println("Gauss curve selected. Curve params: "+_gaussProducer.getSelectedGaussCurve().toString());
					_gaussConsumer.setGaussCurve(_gaussProducer.getSelectedGaussCurve());
					_gaussConsumer.setNumberOfCurves(1);
				} else {
					System.out.println("Gauss curve unselected.");
					_gaussConsumer.setNumberOfCurves(0);
				}
				_gaussConsumer.Update(true);
			}
		}
		
		synchronized (_geometry) {
			_geometry.setup(gl2);
			_geometry.render(gl2);
		}
		/*
		if((_gaussConsumer!=null) && (_gaussTexConsumer!=null))
		{
			if(_gaussConsumer.isUpdated()==true) {
				_gaussTexConsumer.setNumberOfCurves(_gaussConsumer.getNumberOfCurves());
				_gaussTexConsumer.setBoundTexture(_gaussConsumer.getBoundTexture());
			}
		}

		if((_LUTconsumer!=null) && (_LUTtexConsumer!=null))
		{
			if(_LUTconsumer.isUpdated()==true)
			{
				_LUTtexConsumer.setLUTtexUnit(_LUTconsumer.getLUTtexUnit());
			}
		}
		*/
		synchronized (_geometry) {
			if(_useInstancedRendering)
			{
				_geometry.setup(gl3, _commandQueue);
				_geometry.render(gl3);
			}
		}
		
		//Rectangle rectangle = glcanvas.getClientArea();
    	//_textRenderer.beginRendering(rectangle.width, rectangle.height);
    	//System.out.println(_currentText);
    	//_textRenderer.draw(_currentText, 0, 0);
    	//_textRenderer.endRendering();
		
		glcanvas.swapBuffers();
		glcontext.release();
	}
	
	public void generateGradients()
	{
		if(_NormalConsumer!=null)
		{
			_NormalConsumer.setNormals(null);
		}
		_geometry.forceUpdate();
		//cubeGeometry.setScaling(SCALEX*0.5f, SCALEY*0.5f, SCALEZ*0.5f);
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		synchronized (_geometry) {
			_geometry.setup(glcontext.getGL().getGL2());
			if(_useInstancedRendering)
			{
				_geometry.setup(glcontext.getGL().getGL3(), _commandQueue);
			}
		}
		glcontext.release();
		update();
	}

	public void ActivateNormalVis() {
		// TODO Auto-generated method stub
		if(_NormalConsumer!=null)
		{
			_NormalConsumer.ActivateNormalVis();
		}
		_geometry.forceUpdate();
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		synchronized (_geometry) {
			_geometry.setup(glcontext.getGL().getGL2());
			if(_useInstancedRendering)
			{
				_geometry.setup(glcontext.getGL().getGL3(), _commandQueue);
			}
		}
		glcontext.release();
		update();
		update();
	}


	public void DeactivateNormalVis() {
		// TODO Auto-generated method stub
		if(_NormalConsumer!=null)
		{
			_NormalConsumer.DeactivateNormalVis();
		}
		_geometry.forceUpdate();
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		synchronized (_geometry) {
			_geometry.setup(glcontext.getGL().getGL2());
			if(_useInstancedRendering)
			{
				_geometry.setup(glcontext.getGL().getGL3(), _commandQueue);
			}
		}
		glcontext.release();
		update();
		update();
	}
	
	public void ToggleNormalVis()
	{
		if(_activeNormalVis)
		{
			DeactivateNormalVis();
			_activeNormalVis = false;
		}
		else
		{
			ActivateNormalVis();
			_activeNormalVis = true;
		}
	}
	
	public boolean isNormalVis() {
		// TODO Auto-generated method stub
		return _activeNormalVis;
	}
	
	public List<Vector3i> scaleAdaptIntersections(List<Vector3i> intersections)
	{
		List<Vector3i> result = new ArrayList<>();
		for(Vector3i entry: intersections)
		{
			Vector3i tI = new Vector3i((int)Math.round(((float)entry.x)/((float)SCALEX)), (int)Math.round(((float)entry.y)/((float)SCALEY)), (int)Math.round(((float)entry.z)/((float)SCALEZ)));
			result.add(tI);
		}
		return result;
	}
	
	public void reduceGeometry()
	{
		SCALEX*=2; SCALEY*=2; SCALEZ*=2;
	}
	
	public void increaseGeometry()
	{
		SCALEX/=2; SCALEY/=2; SCALEZ/=2;
	}
	
	public void createGeometry()
	{
		glcanvas.setCurrent();
		glcontext.makeCurrent();
		GL3 gl3 = null;
		if(_useInstancedRendering)
			gl3 = glcontext.getGL().getGL3();
		GL2 gl2 = glcontext.getGL().getGL2();
		if(cubeGeometry!=null)
			cubeGeometry.dispose(gl3, _commandQueue);
		if(_clipPlaneGeometry!=null)
			_clipPlaneGeometry.remove(cubeGeometry);
		glcanvas.swapBuffers();
		glcontext.release();
		
		
		
		cubeGeometry = null;
		try
		{
			short dimensions[] = AppWindow.getDataStorage().getDimensions();
			float spacing[] = AppWindow.getDataStorage().getSpacing();
			int noCubes = 0;
			if((dimensions!=null) && (spacing!=null))
			{
				if(_useInstancedRendering)
				{
					//num_elements = (int)Math.ceil((float)_dimensions[0]/SCALEX)*(int)Math.ceil((float)_dimensions[1]/SCALEY)*(int)Math.ceil((float)_dimensions[2]/SCALEZ);
					//num_elements = (_dimensions[0]/SCALEX)*(_dimensions[1]/SCALEY)*(_dimensions[2]/SCALEZ);
					//num_elements = (int)Math.round((float)_dimensions[0]/SCALEX)*(int)Math.round((float)_dimensions[1]/SCALEY)*(int)Math.round((float)_dimensions[2]/SCALEZ);
					num_elements = Math.max((dimensions[0]/SCALEX)*(dimensions[1]/SCALEY)*(dimensions[2]/SCALEZ), (int)Math.ceil((float)(dimensions[0]-SCALEX/2)/SCALEX)*(int)Math.ceil((float)(dimensions[1]-SCALEY/2)/SCALEY)*(int)Math.ceil((float)(dimensions[2]-SCALEZ/2)/SCALEZ));
					
					/*
					_spacing[0] = _spacing[0]*SCALEX;
					_spacing[1] = _spacing[1]*SCALEY;
					_spacing[2] = _spacing[2]*SCALEZ;
					*/
					cubeGeometry = new InstancedCubrille(num_elements, _view, _view);
					cubeGeometry.setMaxValue((short)4095);

					for(int i = SCALEX/2; i< dimensions[0]; i+=SCALEX)
					{
						for(int j = SCALEY/2; j< dimensions[1]; j+=SCALEY)
						{
							for(int k = SCALEZ/2; k< dimensions[2]; k+=SCALEZ)
							{
								synchronized (_geometry) {
									cubeGeometry.add(i, j, k, AppWindow.getDataStorage().getData()[i][j][k]);
								}
							}
						}
					}
					
					short renderDim[] = {(short)(dimensions[0]/SCALEX), (short)(dimensions[1]/SCALEY), (short)(dimensions[2]/SCALEZ)};
					
					cubeGeometry.setDimensions(renderDim);
					cubeGeometry.setScaling(SCALEX*0.5f, SCALEY*0.5f, SCALEZ*0.5f);
					//cubeGeometry.setNormalUpdater(this);
					_LIconsumer = cubeGeometry;
					_VPconsumer = cubeGeometry;
					raySelectionAdaptors[1] = cubeGeometry;
					histoSelectionInterface = cubeGeometry;
					_NormalConsumer = cubeGeometry;
					_gaussConsumer = cubeGeometry;
					_LUTtexConsumer = cubeGeometry;
					if((AppWindow.getDataStorage().hasNormals() || (AppWindow.getDataStorage().hasNormalTexture())) && (_NormalConsumer!=null))
						_NormalConsumer.setNormals(null);

					glcanvas.setCurrent();
					glcontext.makeCurrent();
					_clipPlaneGeometry.add(cubeGeometry);
					cubeGeometry.setup(gl3, _commandQueue);
					glcanvas.swapBuffers();
					glcontext.release();
				}
				else
				{
					Cubrille.setExtentX(spacing[0]*new Float(SCALEX));
					Cubrille.setExtentY(spacing[1]*new Float(SCALEY));
					Cubrille.setExtentZ(spacing[2]*new Float(SCALEZ));
					Cubrille.setSpacingX(spacing[0]);
					Cubrille.setSpacingY(spacing[1]);
					Cubrille.setSpacingZ(spacing[2]);
					
					for(int i = SCALEX/2; i< dimensions[0]; i+=SCALEX)
					{
						for(int j = SCALEY/2; j< dimensions[1]; j+=SCALEY)
						{
							for(int k = SCALEZ/2; k< dimensions[2]; k+=SCALEZ)
							{
								synchronized (_geometry) {
									if(AppWindow.getDataStorage().getData()[i][j][k]!=0)
									{
										//Cubille.render(glcontext.getGL().getGL2(), new Float(i), new Float(j), new Float(k), _data[i][j][k]);
										Cubrille geometry = new Cubrille((float)i, (float)j, (float)k, AppWindow.getDataStorage().getData()[i][j][k]);
										//if(_lutGeometry==null)
										//	_geometry.add(geometry);
										//else
										//	_lutGeometry.add(geometry);
										_clipPlaneGeometry.add(geometry);
										noCubes++;
									}
								}
							}
						}
					}
				}
				glcanvas.redraw();
			}
			System.out.printf("Added %d cubrilles.\n", noCubes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		glcanvas.setCurrent();
		glcontext.makeCurrent();

		_view.setup(glcontext.getGL().getGL2());
		_view.render(glcontext.getGL().getGL2());
		synchronized (_geometry) {
			_geometry.setup(gl2);
			_geometry.render(gl2);
			if(_useInstancedRendering)
			{
				_geometry.setup(gl3, _commandQueue);
				_geometry.render(gl3);
			}
		}

		glcanvas.swapBuffers();
		glcontext.release();
	}

}
