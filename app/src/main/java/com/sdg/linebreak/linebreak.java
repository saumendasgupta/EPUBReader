
package com.sdg.linebreak;


public class linebreak {

	static {
		System.loadLibrary("linebreak");
	
	}
	public static final char MUSTBREAK = 0;
	public static final char ALLOWBREAK = 1;
	public static final char NOBREAK = 2;
	public static final char INSIDEACHAR = 3;
	private static native void init();
	private static native void setLineBreaksForString(String data, String lang, byte[] breaks);
	byte[] breaks;
	String data;
    /** Called when the activity is first created. */
    public void linebreaks(String data, byte[] breaks)
    {
       setLineBreaksForString( data, null,breaks);
    	
    }
}
