package de.ckehl;

public class XRayPane extends Texture3DPane {

	public XRayPane(short slicePane) {
		super(slicePane);
		// TODO Auto-generated constructor stub
		switch (_activePane) {
			case XY_PANE:
			{
				vshaderFile = "average_intensity_xy";
				fshaderFile = "average_intensity_xy";
				break;
			}
			case XZ_PANE:
			{
				vshaderFile = "average_intensity_xz";
				fshaderFile = "average_intensity_xz";
				break;
			}
			case YZ_PANE:
			{
				vshaderFile = "average_intensity_yz";
				fshaderFile = "average_intensity_yz";
				break;
			}
			default:
			{
				vshaderFile = "average_intensity_xy";
				fshaderFile = "average_intensity_xy";
				break;
			}
		}
	
		_shader = new AASliceTextureShader(vshaderFile, fshaderFile);
	}
	
	/**
	 * 
	 * @param slicePane - tells the plane to render; XY_PANE(1), XZ_PANE(2) or YZ_PANE(4)
	 * @param headView - in a shared-context setup, tells if this is the (master) head view
	 */
	public XRayPane(short slicePane, boolean headView)
	{
		super(slicePane, headView);
		switch (_activePane) {
			case XY_PANE:
			{
				vshaderFile = "average_intensity_xy";
				fshaderFile = "average_intensity_xy";
				break;
			}
			case XZ_PANE:
			{
				vshaderFile = "average_intensity_xz";
				fshaderFile = "average_intensity_xz";
				break;
			}
			case YZ_PANE:
			{
				vshaderFile = "average_intensity_yz";
				fshaderFile = "average_intensity_yz";
				break;
			}
			default:
			{
				vshaderFile = "average_intensity_xy";
				fshaderFile = "average_intensity_xy";
				break;
			}
		}
		
		_shader = new AASliceTextureShader(vshaderFile, fshaderFile);
	}

	@Override
	public String toString()
	{
		return "XRayPane";
	}
}
