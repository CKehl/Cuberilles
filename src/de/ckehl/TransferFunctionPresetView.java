package de.ckehl;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.forms.widgets.Form;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.mihalis.opal.rangeSlider.RangeSlider;
import org.omg.stub.java.rmi._Remote_Stub;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.jogamp.common.util.IOUtil;

class Preset
{
	public int lowerValue, upperVal;
	public int opacity;
	public Color colour;
	public String name;
	
	public Preset()
	{
		Device device = Display.getCurrent();
		colour = new Color(device, 0, 0, 0);
		opacity = 0;
		lowerValue = 0;
		upperVal = 1;
		name = "";
	}
	
	public Preset(Preset value)
	{
		colour = value.colour;
		opacity = value.opacity;
		lowerValue = value.lowerValue;
		upperVal = value.upperVal;
		name = value.name;
	}
	
	public Preset(int lValue, int hValue, int Opacity, Color Colour, String pName)
	{
		lowerValue = lValue;
		upperVal = hValue;
		opacity = Opacity;
		colour = Colour;
		name = pName;
	}
	
	public String fileString()
	{
		//1150 4000 100 (229,229,229) hard_bone
		return Integer.toString(lowerValue)+" "+Integer.toString(upperVal)+" "+Integer.toString(opacity)+" ("+Integer.toString(colour.getRed())+","+Integer.toString(colour.getGreen())+","+Integer.toString(colour.getBlue())+") "+name;
	}
	
	public String toString()
	{
		return ("lV: "+Integer.toString(lowerValue)+",hV: "+Integer.toString(upperVal)+", o: "+Integer.toString(opacity)+", n: "+name);
	}
}

public class TransferFunctionPresetView implements LUTinterface, KeyListener, HistogramSelectionInterface {

	protected Composite _parent = null;
	protected SashForm box = null;
	protected Button _btnAddColour = null;
	protected Composite presetColours = null;
	protected ScrolledComposite scrollBar = null;
	//protected ScrolledComposite presetColours = null;
	//protected Slider _opacityScale = null;
	protected Scale _opacityScale = null;
	protected Combo _presetSelector = null;
	protected Text _presetNameField = null;
	protected Button _presetAdder = null;
	protected RangeSlider _valueSelector = null;
	protected Text _valueRangeText = null;
	protected Button addPreset = null;
	protected int valueSelectorRange = 5;
	protected int valueSelectorUpper = 10;
	protected int valueSelectorLower = 0;
	protected List<Color> _presetColourList = null;
	protected List<Preset> _presets = null;
	protected Color _currentColor = null;
	protected boolean _isOpen = false;
	protected List<Short> _selectedPresets = null;
	/*
	 * if busy, block the "adding presets" to hold the last-in-the-list
	 * preset being the currently modified
	 */
	private boolean _busy = false;
	protected ViewUpdateInterface _viewerConnection = null;
	
	/*
	 * here, _isUpdated is true if new information are set;
	 * and _isUpdated is false if all children have adapted to the last change
	 * (producer behaviour)
	 */
	protected boolean _isUpdated = true;
	
	protected boolean _isEditMode = false;
	protected int _editIndex = 0;
	
	private int _maxValue = 4095;
	protected byte _lut[] = null;
	private int _boundTexUnit = 0;
	
	public TransferFunctionPresetView(Composite arg0, int arg1, Display arg2)
	{
		_parent = arg0;
		
		/*
		 * create LUT
		 */
		_lut = new byte[_maxValue*4];
		for(int i = 0; i<_maxValue; i++)
		{
			_lut[(i*4)+0] = (byte)(((float)i/(float)_maxValue)*255.0f);
			_lut[(i*4)+1] = (byte)(((float)i/(float)_maxValue)*255.0f);
			_lut[(i*4)+2] = (byte)(((float)i/(float)_maxValue)*255.0f);
			_lut[(i*4)+3] = (byte)(255.0f);
		}
		
		/*
		 * get preset colour list
		 */
		
		_presetColourList = new ArrayList<>();
		loadAssetColours();
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_RED));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_GREEN));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_BLUE));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_CYAN));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_MAGENTA));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_YELLOW));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_BLACK));
		//_presetColourList.add(arg2.getSystemColor(SWT.COLOR_WHITE));
		
		_presets = new ArrayList<>();
		_selectedPresets = new ArrayList<>();
		loadAssetPresets("medical");
		//Device device = Display.getCurrent();
		//_presets.add(new Preset(1150, 4000, 100, new Color(device, 229, 229, 229), "hard_bone"));
		//_presets.add(new Preset(1020, 1149, 55, new Color(device, 255, 245, 204), "dense liquid"));
		//_presets.add(new Preset(800, 1020, 70, arg2.getSystemColor(SWT.COLOR_DARK_RED), "tissue"));
		
		//_presets.add(new Preset(316, 800, 30, arg2.getSystemColor(SWT.COLOR_RED), "soft_tissue"));
		//_presets.add(new Preset(70, 315, 20, new Color(device, 240, 182, 127), "skin"));
		
		//_presets.add(new Preset(70, 800, 75, new Color(device, 240, 182, 127), "skin"));
		//_presets.add(new Preset(0, 70, 0, arg2.getSystemColor(SWT.COLOR_BLACK), "background"));
		
		/*
		 * Create Layout
		 */
		box = new SashForm(_parent,SWT.HORIZONTAL);
		box.setLayout(new FillLayout(SWT.BORDER));
		
		Composite colorBar = new Composite(box, SWT.PUSH);
		//RowLayout colorBarLayout = new RowLayout(SWT.BORDER);
		//colorBarLayout.type = SWT.VERTICAL;
		//colorBarLayout.wrap = true;
		//colorBarLayout.pack = true;
		FillLayout colorBarLayout = new FillLayout(SWT.BORDER);
		colorBarLayout.type = SWT.VERTICAL;
		colorBar.setLayout(colorBarLayout);
		
		_btnAddColour = new Button(colorBar, SWT.PUSH);
		_btnAddColour.setText("Add Colour");
		_btnAddColour.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				ColorDialog cd = new ColorDialog(AppWindow.getShell());
				cd.setText("Color Selection");
				cd.setRGB(new RGB(0, 0, 0));
				RGB newColor = cd.open();
				if (newColor == null) {
					return;
				}
				else
				{
					Device device = Display.getCurrent();
					Color nColour = new Color(device, newColor.red, newColor.green, newColor.blue);
					_presetColourList.add(nColour);
					
					Button colCanvas = new Button(presetColours, SWT.PUSH);
					colCanvas.setBackground(nColour);
					colCanvas.setForeground(nColour);
					colCanvas.setSize(10, 10);
					colCanvas.setLayoutData(new GridData(10, 10));
					colCanvas.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							_currentColor = ((Button)(arg0.widget)).getBackground();
							
							if(_busy)
							{
								_presets.get(_presets.size()-1).colour = _currentColor;
								InteractiveUpdateLUT();
							}
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					//presetColours.setSize(presetColours.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					//presetColours.layout();
					scrollBar.setMinSize(presetColours.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					scrollBar.setContent(presetColours);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		scrollBar = new ScrolledComposite(colorBar, SWT.H_SCROLL | SWT.V_SCROLL);
		//presetColours = new ScrolledComposite(colorBar, SWT.H_SCROLL | SWT.V_SCROLL | SWT.PUSH);
		//presetColours.setExpandVertical(true);
		//presetColours.setExpandHorizontal(true);
		presetColours = new Composite(scrollBar, SWT.BORDER);
		//scrollBar.setContent(presetColours);
		GridLayout presetColourLayout = new GridLayout();
		presetColourLayout.numColumns = 3;
		GridData presetColourLayoutData = new GridData();
		presetColourLayoutData.horizontalAlignment = SWT.FILL;
		presetColourLayoutData.verticalAlignment = SWT.FILL;

		presetColours.setLayout(presetColourLayout);
		
		if(_presetColourList!=null)
		{
			for(Color entry : _presetColourList)
			{
				//Canvas colCanvas = new Canvas(presetColours, SWT.PUSH);
				Button colCanvas = new Button(presetColours, SWT.PUSH);
				colCanvas.setBackground(entry);
				colCanvas.setForeground(entry);
				colCanvas.setSize(10, 10);
				colCanvas.setLayoutData(new GridData(10, 10));
				colCanvas.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
						_currentColor = ((Button)(arg0.widget)).getBackground();
						
						if(_busy)
						{
							_presets.get(_presets.size()-1).colour = _currentColor;
							InteractiveUpdateLUT();
						}
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
		}
		//scrollBar.setSize(presetColours.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//scrollBar.setSize(100,60);
		
		//presetColours.setSize(presetColours.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		//presetColours.setMinWidth(100);
		//presetColours.setMinHeight(60);
		//scrollBar.setMinWidth(100);
		//scrollBar.setMinHeight(60);
		//presetColours.layout();
		//scrollBar.setMinSize(70, 60);
		scrollBar.setMinSize(presetColours.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrollBar.setExpandVertical(true);
		scrollBar.setExpandHorizontal(true);
		scrollBar.setContent(presetColours);
		
		Composite rangePresetBox = new Composite(box, SWT.PUSH | SWT.BORDER);
		RowLayout rangePresetBoxLayout = new RowLayout();
		rangePresetBoxLayout.type = SWT.VERTICAL;
		rangePresetBoxLayout.wrap = true;
		rangePresetBoxLayout.pack = true;
		rangePresetBox.setLayout(rangePresetBoxLayout);
		rangePresetBox.layout();
		
		Composite presetListBox = new Composite(rangePresetBox, SWT.PUSH);
		RowLayout presetListBoxLayout = new RowLayout();
		presetListBoxLayout.type = SWT.HORIZONTAL;
		presetListBoxLayout.wrap = true;
		presetListBoxLayout.pack = true;
		presetListBox.setLayout(presetListBoxLayout);
		presetListBox.setLayoutData(new RowData(300, 35));
		presetListBox.layout();
		
		
		_presetSelector = new Combo(presetListBox, SWT.H_SCROLL | SWT.DROP_DOWN | SWT.READ_ONLY | SWT.PUSH);
		_presetSelector.setText("<Presets to select ...>");
		_presetSelector.setTextLimit(166); 
		//_presetSelector.setSize(_presetSelector.computeSize(166, _presetSelector.getSize().y));
		//_presetSelector.setBounds( 0, 0, 166, _presetSelector.getSize().y );
		_presetSelector.setLayoutData(new RowData(166, _presetSelector.getSize().y));
		for(Preset entry : _presets)
		{
			_presetSelector.add(entry.name);
		}
		_presetSelector.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int index = _presetSelector.getSelectionIndex();
				if((_presetSelector.getItemCount()>0) && (index>=0))
				{
					_isEditMode = true;
					_editIndex = index;
					_busy = false;

					
					_valueSelector.setLowerValue(_presets.get(index).lowerValue);
					_valueSelector.setUpperValue(_presets.get(index).upperVal);
					_valueRangeText.setText("("+Integer.toString(_valueSelector.getLowerValue())+", "+Integer.toString(_valueSelector.getUpperValue())+")");
					_opacityScale.setSelection(_presets.get(index).opacity);
					_currentColor = _presets.get(index).colour;
					//_presetNameField.setText(_presets.get(index).name);
					_presetNameField.setText(_presetSelector.getText());
					
					EnableAdding();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_presetAdder = new Button(presetListBox, SWT.PUSH);
		_presetAdder.setText("->");
		_presetAdder.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
				// handle edits
				
				_isEditMode = false;
				_editIndex = 0;
				
				int index = _presetSelector.getSelectionIndex();
				if((_presetSelector.getItemCount()>0) && (index>=0))
				{
					_selectedPresets.add((short)index);
				}
				
				for(int i = 0; i<_maxValue; i++)
				{
					_lut[(i*4)+0] = (byte)(((float)i/(float)_maxValue)*255.0f);
					_lut[(i*4)+1] = (byte)(((float)i/(float)_maxValue)*255.0f);
					_lut[(i*4)+2] = (byte)(((float)i/(float)_maxValue)*255.0f);
					_lut[(i*4)+3] = (byte)(255.0f);
				}
				
				for(Short _indexedPreset : _selectedPresets)
				{
					Preset p = _presets.get((int)_indexedPreset);
					byte r = (byte)(p.colour.getRed());
					byte g = (byte)(p.colour.getGreen());
					byte b = (byte)(p.colour.getBlue());
					byte a = (byte)((int)(((float)p.opacity/100.0f)*255.0f));
					for(int i = p.lowerValue; i<p.upperVal; i++)
					{
						_lut[(i*4)+0] = r;
						_lut[(i*4)+1] = g;
						_lut[(i*4)+2] = b;
						_lut[(i*4)+3] = a;
					}
				}
				_isUpdated = true;
				_busy = false;
				ResetWidget();
				_presetSelector.clearSelection();
				_presetSelector.setText("");
				if(_viewerConnection!=null)
				{
					_viewerConnection.update();
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		if(_presetSelector.getItemCount()==0)
			_presetAdder.setEnabled(false);
		
		
		Composite addingSettings = new Composite(rangePresetBox, SWT.PUSH);
		RowLayout addingSettingsLayout = new RowLayout();
		addingSettingsLayout.type = SWT.HORIZONTAL;
		addingSettingsLayout.wrap = true;
		addingSettingsLayout.pack = true;
		addingSettings.setLayout(addingSettingsLayout);
		addingSettings.setLayoutData(new RowData(300, 35));
		addingSettings.layout();
		
		addPreset = new Button(addingSettings, SWT.PUSH);
		addPreset.setText("Add as preset");
		addPreset.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				// handle edits
				
				_presets.remove(_presets.size()-1);
				_selectedPresets.remove(_selectedPresets.size()-1);
				
				Preset p = new Preset();
				if(_currentColor!=null)
				{
					p.name = _presetNameField.getText();
					p.lowerValue = _valueSelector.getLowerValue();
					p.upperVal = _valueSelector.getUpperValue();
					p.colour = _currentColor;
					p.opacity = _opacityScale.getSelection();
					_presetNameField.setText("<preset name>");
					
					if(_isEditMode==false)
					{
						_presetSelector.add(p.name);
						if(_presets!=null)
						{
							_presets.add(p);
							_selectedPresets.add((short)(_presets.size()-1));
						}
					}
					else
					{
						_presetSelector.setItem(_editIndex, p.name);
						_presets.set(_editIndex, p);
						//_selectedPresets.add((short)(_editIndex));
						_isEditMode = false;
						_editIndex = 0;
					}
				}
				
				for(int i = 0; i<_maxValue; i++)
				{
					_lut[(i*4)+0] = (byte)(((float)i/(float)_maxValue)*255.0f);
					_lut[(i*4)+1] = (byte)(((float)i/(float)_maxValue)*255.0f);
					_lut[(i*4)+2] = (byte)(((float)i/(float)_maxValue)*255.0f);
					_lut[(i*4)+3] = (byte)(255.0f);
				}
				
				for(Short _indexedPreset : _selectedPresets)
				{
					p = _presets.get((int)_indexedPreset);
					byte r = (byte)(p.colour.getRed());
					byte g = (byte)(p.colour.getGreen());
					byte b = (byte)(p.colour.getBlue());
					byte a = (byte)((int)(((float)p.opacity/100.0f)*255.0f));
					for(int i = p.lowerValue; i<p.upperVal; i++)
					{
						_lut[(i*4)+0] = r;
						_lut[(i*4)+1] = g;
						_lut[(i*4)+2] = b;
						_lut[(i*4)+3] = a;
					}
				}
				_isUpdated = true;
				_busy = false;
				ResetWidget();
				if(_viewerConnection!=null)
				{
					_viewerConnection.update();
				}
				

			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_presetNameField = new Text(addingSettings, SWT.PUSH);
		_presetNameField.setText("<preset name>");
		_presetNameField.setEditable(true);
		_presetNameField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub
				if(_busy==false)
				{
					if(_isEditMode==true)
					{
						//System.out.println("Preset size: "+Integer.toString(_presets.size())+"; selected Presets size: "+Integer.toString(_selectedPresets.size()));
						//_presets.add(new Preset(_presets.get(_editIndex)));
						Preset oP = _presets.get(_editIndex);
						//_presets.add(new Preset(oP.lowerValue, oP.upperVal, oP.opacity, oP.colour, oP.name));
						_presets.add(new Preset());
						_presets.get(_presets.size()-1).lowerValue = oP.lowerValue;
						_presets.get(_presets.size()-1).upperVal = oP.upperVal;
						_presets.get(_presets.size()-1).opacity = oP.opacity;
						_presets.get(_presets.size()-1).colour = oP.colour;
						_presets.get(_presets.size()-1).name = oP.name;
						_selectedPresets.add((short)(_presets.size()-1));
						//System.out.println("Preset size: "+Integer.toString(_presets.size())+"; selected Presets size: "+Integer.toString(_selectedPresets.size()));
						//_busy = true;
						//DisableAdding();
						addPreset.setText("Confirm edit");
					}
					else
					{
						_presets.add(new Preset());
						_selectedPresets.add((short)(_presets.size()-1));
					}
				}
				_presets.get(_presets.size()-1).name = _presetNameField.getText();
				_busy = true;
				DisableAdding();
			}
		});
				
		
		_valueSelector = new RangeSlider(rangePresetBox, SWT.HORIZONTAL | SWT.PUSH);
		_valueSelector.setMinimum(0);
		_valueSelector.setMaximum(_maxValue);
		_valueSelector.setIncrement(1);
		_valueSelector.setLowerValue(valueSelectorLower);
		_valueSelector.setUpperValue(valueSelectorUpper);
		_valueSelector.setLayoutData(new RowData(220, 30));
		_valueSelector.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(_busy==false)
				{
					if(_isEditMode==true)
					{
						//System.out.println("Preset size: "+Integer.toString(_presets.size())+"; selected Presets size: "+Integer.toString(_selectedPresets.size()));
						//_presets.add(new Preset(_presets.get(_editIndex)));
						Preset oP = _presets.get(_editIndex);
						//_presets.add(new Preset(oP.lowerValue, oP.upperVal, oP.opacity, oP.colour, oP.name));
						_presets.add(new Preset());
						_presets.get(_presets.size()-1).lowerValue = oP.lowerValue;
						_presets.get(_presets.size()-1).upperVal = oP.upperVal;
						_presets.get(_presets.size()-1).opacity = oP.opacity;
						_presets.get(_presets.size()-1).colour = oP.colour;
						_presets.get(_presets.size()-1).name = oP.name;
						_selectedPresets.add((short)(_presets.size()-1));
						//System.out.println("Preset size: "+Integer.toString(_presets.size())+"; selected Presets size: "+Integer.toString(_selectedPresets.size()));
						//_busy = true;
						//DisableAdding();
						addPreset.setText("Confirm edit");
					}
					else
					{
						_presets.add(new Preset());
						_selectedPresets.add((short)(_presets.size()-1));
					}
				}
				_presets.get(_presets.size()-1).lowerValue = _valueSelector.getLowerValue();
				_presets.get(_presets.size()-1).upperVal = _valueSelector.getUpperValue();
				_valueRangeText.setText("("+Integer.toString(_valueSelector.getLowerValue())+", "+Integer.toString(_valueSelector.getUpperValue())+")");
				_busy = true;
				InteractiveUpdateLUT();
				DisableAdding();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		_valueRangeText = new Text(rangePresetBox, SWT.PUSH | SWT.READ_ONLY);
		_valueRangeText.setLayoutData(new RowData(80, 18));
		
		
		
		//_opacityScale = new Slider(box, SWT.VERTICAL | SWT.PUSH);
		_opacityScale = new Scale(box, SWT.VERTICAL | SWT.PUSH);
		//_opacityScale.setOrientation(SWT.RIGHT_TO_LEFT);
		_opacityScale.setMinimum(0);
		_opacityScale.setMaximum(100);
		_opacityScale.setIncrement(1);
		_opacityScale.setSelection(0);
		_opacityScale.redraw();
		_opacityScale.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				if(_busy==false)
				{
					if(_isEditMode==true)
					{
						//System.out.println("Preset size: "+Integer.toString(_presets.size())+"; selected Presets size: "+Integer.toString(_selectedPresets.size()));
						//_presets.add(new Preset(_presets.get(_editIndex)));
						Preset oP = _presets.get(_editIndex);
						//_presets.add(new Preset(oP.lowerValue, oP.upperVal, oP.opacity, oP.colour, oP.name));
						_presets.add(new Preset());
						_presets.get(_presets.size()-1).lowerValue = oP.lowerValue;
						_presets.get(_presets.size()-1).upperVal = oP.upperVal;
						_presets.get(_presets.size()-1).opacity = oP.opacity;
						_presets.get(_presets.size()-1).colour = oP.colour;
						_presets.get(_presets.size()-1).name = oP.name;
						_selectedPresets.add((short)(_presets.size()-1));
						//System.out.println("Preset size: "+Integer.toString(_presets.size())+"; selected Presets size: "+Integer.toString(_selectedPresets.size()));
						System.out.println("opacity scale: "+_presets.get(_presets.size()-1).toString());
						//_busy = true;
						//DisableAdding();
						addPreset.setText("Confirm edit");
					}
					else
					{
						_presets.add(new Preset());
						_selectedPresets.add((short)(_presets.size()-1));
					}
				}
				_presets.get(_presets.size()-1).opacity = _opacityScale.getSelection();
				_busy = true;
				InteractiveUpdateLUT();
				DisableAdding();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//_opacityScale.setSelection(100);
		//_opacityScale.
		
		box.setWeights(new int[]{2,3,1});
		box.addKeyListener(this);
		_btnAddColour.addKeyListener(this);
		presetColours.addKeyListener(this);
		_opacityScale.addKeyListener(this);
		_presetSelector.addKeyListener(this);
		_presetNameField.addKeyListener(this);
		_presetAdder.addKeyListener(this);
		_valueSelector.addKeyListener(this);
	}
	
	private void loadAssetColours()
	{
		Device device = Display.getCurrent();
		
		URLConnection conn = IOUtil.getResource("res/assets/colours.txt", this.getClass().getClassLoader());
		int lineno = 0;
		
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineno++;
                line = line.substring(1, line.length()-1);
                //System.out.println(line);
                String valueStrings[] = line.split(",");
                if(valueStrings.length==3)
                {
                	Vector3i colorValues = new Vector3i(Integer.parseInt(valueStrings[0]), Integer.parseInt(valueStrings[1]), Integer.parseInt(valueStrings[2]));
                	//System.out.println("Colour "+Integer.toString(lineno)+": "+colorValues.toString(new DecimalFormat( "#,###,###,##0.00" )));
                	_presetColourList.add(new Color(device, colorValues.x, colorValues.y, colorValues.z));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find "+conn.getURL().getFile());
        }
	}
	
	private void loadAssetPresets(String headName)
	{
		loadPresets("res/assets/"+headName+".preset");
	}
	
	public void loadPresets(String fileURL)
	{
		if(_presets.isEmpty()==false)
			_presets.clear();
		Device device = Display.getCurrent();
		URLConnection conn = IOUtil.getResource(fileURL, this.getClass().getClassLoader());
		int lineno = 0;
		
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineno++;
                String partialStrings[] = line.split(" ");
                int lV = Integer.parseInt(partialStrings[0]);
                int uV = Integer.parseInt(partialStrings[1]);
                int opac = Integer.parseInt(partialStrings[2]);
                Color col = null;
                String valueStrings[] = partialStrings[3].substring(1, partialStrings[3].length()-1).split(",");
                if(valueStrings.length==3)
                {
                	Vector3i colorValues = new Vector3i(Integer.parseInt(valueStrings[0]), Integer.parseInt(valueStrings[1]), Integer.parseInt(valueStrings[2]));
                	//System.out.println("Colour "+Integer.toString(lineno)+": "+colorValues.toString(new DecimalFormat( "#,###,###,##0.00" )));
                	col = new Color(device, colorValues.x, colorValues.y, colorValues.z);
                }
                String nm = partialStrings[4];
                _presets.add(new Preset(lV,uV,opac,col,nm));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find "+conn.getURL().getFile());
        }
        
        if(_presetSelector!=null)
        {
	        _presetSelector.removeAll();
			for(Preset entry : _presets)
			{
				_presetSelector.add(entry.name);
			}
        }
	}
	
	public void savePresets(String path)
	{
		if(_presets.isEmpty()==true)
			return;
		
		try
		{
			File file = new File(path);
			if(!file.exists())
				file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			// 1150 4000 100 (229,229,229) hard_bone
			for(Preset p : _presets)
			{
				bw.write(p.fileString()+"\n");
			}
			bw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setViewerConnection(ViewUpdateInterface viewConnection)
	{
		_viewerConnection = viewConnection;
	}
	
	private void EnableAdding()
	{
		if(_presets.isEmpty()==false)
			_presetAdder.setEnabled(true);
	}
	
	private void DisableAdding()
	{
		_presetAdder.setEnabled(false);
	}
	
	private void ResetWidget()
	{
		valueSelectorLower = _valueSelector.getUpperValue();
		valueSelectorUpper = Math.min(_maxValue, _valueSelector.getUpperValue()+50);
		_valueSelector.setLowerValue(valueSelectorLower);
		_valueSelector.setUpperValue(valueSelectorUpper);
		_opacityScale.setSelection(0);
		_currentColor = null;
		_presetNameField.setText("<preset name>");
		_valueRangeText.setText("");
		addPreset.setText("Add as preset");
		_presetSelector.clearSelection();
		_presetSelector.deselectAll();
		_busy = false;
		
		/*
		for(Preset p : _presets)
		{
			byte r = (byte)(p.colour.getRed());
			byte g = (byte)(p.colour.getGreen());
			byte b = (byte)(p.colour.getBlue());
			byte a = (byte)((int)(((float)p.opacity/100.0f)*255.0f));
			for(int i = p.lowerValue; i<p.upperVal; i++)
			{
				_lut[(i*4)+0] = r;
				_lut[(i*4)+1] = g;
				_lut[(i*4)+2] = b;
				_lut[(i*4)+3] = a;
			}
		}
		*/
		_isUpdated = true;
		if(_viewerConnection!=null)
		{
			_viewerConnection.update();
		}
		EnableAdding();
	}
	
	private void InteractiveUpdateLUT()
	{
		if(_busy)
		{
			/*
			for(Short _indexedPreset : _selectedPresets)
			{
				Preset p = _presets.get((int)_indexedPreset);
				byte r = (byte)(p.colour.getRed());
				byte g = (byte)(p.colour.getGreen());
				byte b = (byte)(p.colour.getBlue());
				byte a = (byte)((int)(((float)p.opacity/100.0f)*255.0f));
				for(int i = p.lowerValue; i<p.upperVal; i++)
				{
					_lut[(i*4)+0] = r;
					_lut[(i*4)+1] = g;
					_lut[(i*4)+2] = b;
					_lut[(i*4)+3] = a;
				}
			}
			*/
			for(int i = 0; i<_maxValue; i++)
			{
				_lut[(i*4)+0] = (byte)255;
				_lut[(i*4)+1] = (byte)255;
				_lut[(i*4)+2] = (byte)255;
				_lut[(i*4)+3] = (byte)0;
			}
			Preset p = _presets.get(_presets.size()-1);
			//System.out.println("InteractiveLUT: "+p.toString());
			byte r = (byte)(p.colour.getRed());
			byte g = (byte)(p.colour.getGreen());
			byte b = (byte)(p.colour.getBlue());
			byte a = (byte)((int)(((float)p.opacity/100.0f)*255.0f));
			for(int i = p.lowerValue; i<p.upperVal; i++)
			{
				_lut[(i*4)+0] = r;
				_lut[(i*4)+1] = g;
				_lut[(i*4)+2] = b;
				_lut[(i*4)+3] = a;
			}
			_isUpdated = true;
			if(_viewerConnection!=null)
			{
				_viewerConnection.update();
			}
		}
	}
	
	public void dispose()
	{
		box.dispose();
		_lut = null;
		_parent = null;
	}

	@Override
	public byte[] getLUT() {
		// TODO Auto-generated method stub
		return _lut;
	}

	@Override
	public void setLUT(byte[] lut) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return _isUpdated;
	}

	@Override
	/**
	 * here, _isUpdated is true if new information are set;
	 * and _isUpdated is false if all children have adapted to the last change
	 * (producer behaviour)
	 */
	public void Update(boolean state) {
		// TODO Auto-generated method stub
		_isUpdated = state;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.keyCode == SWT.ESC)
		{
			if(_busy)
				_presets.remove(_presets.size()-1);
			_busy = false;
			ResetWidget();
		}
	}

	@Override
	public void center(int value, int occurence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void markValues(Vector2i range) {
		// TODO Auto-generated method stub
		_valueSelector.setLowerValue(range.x);
		_valueSelector.setUpperValue(range.y);
		_valueRangeText.setText("("+Integer.toString(_valueSelector.getLowerValue())+", "+Integer.toString(_valueSelector.getUpperValue())+")");
		if(_busy)
		{
			_presets.get(_presets.size()-1).lowerValue = _valueSelector.getLowerValue();
			_presets.get(_presets.size()-1).upperVal = _valueSelector.getUpperValue();
			InteractiveUpdateLUT();
			DisableAdding();
		}
	}

	@Override
	public void resetHistogramSelection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLUTtexUnit() {
		// TODO Auto-generated method stub
		return _boundTexUnit;
	}

	@Override
	public void setLUTtexUnit(int texUnit) {
		// TODO Auto-generated method stub
		_boundTexUnit = texUnit;
	}
}
