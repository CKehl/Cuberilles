package de.ckehl;

import java.nio.channels.UnresolvedAddressException;

public class VersionHelper {
	static public int FloatBYTES()
	{
        int bytes = 0;
        try
        {
        	//bytes = Float.BYTES;
        	bytes = 4;
        }
        catch(Error e)
        {
        	bytes = 4;
        }
        catch (Exception e) {
			// TODO: handle exception
        	bytes = 4;
		}
        return bytes;
	}
	static public int ShortBYTES()
	{
        int bytes = 0;
        try
        {
        	//bytes = Short.BYTES;
        	bytes = 2;
        }
        catch(Error e)
        {
        	bytes = 2;
        }
        catch (Exception e) {
			// TODO: handle exception
        	bytes = 2;
		}
        return bytes;
	}
	static public int ByteBYTES()
	{
        int bytes = 0;
        try
        {
        	//bytes = Byte.BYTES;
        	bytes = 1;
        }
        catch(Error e)
        {
        	bytes = 1;
        }
        catch (Exception e) {
			// TODO: handle exception
        	bytes = 1;
		}
        return bytes;
	}
	static public int IntBYTES()
	{
        int bytes = 0;
        try
        {
        	//bytes = Byte.BYTES;
        	bytes = 4;
        }
        catch(Error e)
        {
        	bytes = 4;
        }
        catch (Exception e) {
			// TODO: handle exception
        	bytes = 4;
		}
        return bytes;
	}
}
