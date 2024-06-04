/*
 * Android backend for libusb
 *
 * This Java class interfaces between the C-code backend and the 
 * Android UsbManager.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.libusb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Arrays;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.SparseArray;
import android.util.Log;

/**
 * Manager for devices accessed through libusb.
 */
public class LibUsbManager 
{	
	private static final String TAG                               = "LibUsb";
	
	
    /*
     *  Log warning to System.out.
     */
    private static void logPrintWarning(String ln)
    {
        Log.w("IBScanDevice", ln);
    }
    
    /*
     *  Log error to System.out.
     */
    private static void logPrintError(String ln)
    {
        Log.e("IBScanDevice", ln);
    }

    /* *********************************************************************************************
     * (CLASS) PUBLIC INTERFACE
     ******************************************************************************************** */

    /**
     * Assign the context associated with libusb, which must be known before accessing any USB 
     * devices.
     * 
     * @param context  Application context for LibUsbManager operations.
     */
    public static void setContext(Context context)
    {  	
        m_context = context;
    }

    /* *********************************************************************************************
     * PROTECTED INTERFACE
     ******************************************************************************************** */

    /*
     * A descriptor of the attached devices.
     */
    protected static class DeviceDesc
    {
    	/**
    	 * Unique ID used to find devices within UsbManager.
    	 */
    	final public int    deviceId;
    	/** 
    	 * Raw device descriptors.
    	 */
    	final public byte[] descriptors;
    	
    	protected DeviceDesc(int deviceId, byte[] descriptors)
    	{
    		this.deviceId    = deviceId;
    		this.descriptors = descriptors;
    	}
    }
    
    /* *********************************************************************************************
     * PRIVATE INTERFACE
     ******************************************************************************************** */

    /*
     * Find device for a certain device ID.
     * 
     * @param deviceId  device ID to search for
     * @return          device, if found; <code>null</code> otherwise
     */
    private static UsbDevice findDevice(int deviceId)
    {
    	UsbDevice device = null;
    	
    	final UsbManager                 manager    = (UsbManager)m_context.getSystemService(Context.USB_SERVICE);
    	final HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

		/* Iterate through list to find device. */
		final Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext())
		{
		    final UsbDevice deviceTemp   = deviceIterator.next();
		    final int       deviceIdTemp = deviceTemp.getDeviceId();
		    
		    if (deviceIdTemp == deviceId)
		    {
		    	device = deviceTemp;
		    	break;
		    }
		}
		    	
    	return (device);
    }
    
    /*
     *  Private default constructor to prevent external instantiation.
     */
    private LibUsbManager()
    {

    }
    
    /*
     * List of connected devices.
     */
    private static SparseArray<UsbDeviceConnection> m_connectionMap = new SparseArray<UsbDeviceConnection>();
    
    /*
     * Scan context.
     */
    private static Context m_context = null;
            
	private static Vector<DeviceDesc>         m_descVector = new Vector<DeviceDesc>();
	private static int m_hasPermitUsbDevice=0;
    	
    /* *********************************************************************************************
     * CALLBACKS FROM libusb
     ******************************************************************************************** */
    
    /*
     * Get array of device descriptors for accessible devices.
     */
    protected static DeviceDesc[] getDeviceArray()
    {
//    	DeviceDesc[] descArray = new DeviceDesc[0];
    	
    	if (m_context != null)
    	{
	    	final UsbManager                 manager    = (UsbManager)m_context.getSystemService(Context.USB_SERVICE);
	    	final HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
	    	final Vector<DeviceDesc>         descVector = new Vector<DeviceDesc>();
	    	int                              hasPermitUsbDevice=0;
	
			/* Iterate through list to count devices. */
			Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
			while (deviceIterator.hasNext())
			{
			    final UsbDevice device        = deviceIterator.next();
			    final boolean   hasPermission = manager.hasPermission(device);
	
			    if (hasPermission)
			    {
			    	hasPermitUsbDevice++;
			    }
			}
			
			if (hasPermitUsbDevice != m_hasPermitUsbDevice)
			{
//				logPrintWarning(">>>>>>>>>>>>>>> USB device was changed " + hasPermitUsbDevice);        		
				m_hasPermitUsbDevice = hasPermitUsbDevice;
				m_descVector.clear();
				/* Iterate through list to count devices. */
				deviceIterator = deviceList.values().iterator();
				while (deviceIterator.hasNext())
				{
				    final UsbDevice device        = deviceIterator.next();
				    final boolean   hasPermission = manager.hasPermission(device);
		
				    if (hasPermission)
				    {
				    	if (device.getVendorId() == 0x113f || device.getVendorId() == 0x1fba )
				    	{
					    	/* Get device descriptors then close connection. */
						    final UsbDeviceConnection connection = manager.openDevice(device);
						    if (connection != null)
						    {
						    	byte[] descriptors = connection.getRawDescriptors();
						    	connection.close();
						    	m_descVector.add(new DeviceDesc(device.getDeviceId(), descriptors));
//								logPrintWarning(">>>>>>>>>>>>>>> ADDED VECTOR m_descVector size = " + m_descVector.size() + ", USB device lists vid = " + device.getVendorId() + ", pid = " + device.getProductId());
						    }
						}
				    }
				}
			}
			
//			descArray = m_descVector.toArray(descArray);
   		}
    	
		DeviceDesc[] descArray = m_descVector.toArray(new DeviceDesc[m_descVector.size()]);
		
    	return (descArray);
    }
    
    /*
     * Open raw file descriptor for device.
     */
    protected static int openDevice(int deviceId)
    {
    	int fd = -1;

    	if (m_context != null)
    	{
			/* Find the description for the device. */
    		final UsbDevice device = findDevice(deviceId);    		
    		
    		if (device != null)
    		{
    	    	final UsbManager manager = (UsbManager)m_context.getSystemService(Context.USB_SERVICE);

    	    	/* Check whether we have permission for the device. */
    			if (manager.hasPermission(device))
    			{
    				final UsbDeviceConnection connection = manager.openDevice(device);
    				if (connection != null)
    				{
    					/* Return file descriptor and save connection for closing. */
    					fd = connection.getFileDescriptor();
    					m_connectionMap.put(device.getDeviceId(), connection);
    				}
    			}
    		}
	    }
    	
    	return (fd);
    }
    
    /*
     * Close connection for device.
     */
    protected static void closeDevice(int deviceId)
    {
    	if (m_context != null)
    	{
			/* Find the connection for the device. */
    		final UsbDeviceConnection connection = m_connectionMap.get(deviceId);
    		
    		if (connection != null)
    		{
    			/* Close connection and remove from map. */
	   			connection.close();
	   			m_connectionMap.remove(deviceId);
	   		}
	    }
    }
    
    /* *********************************************************************************************
     * STATIC BLOCKS
     ******************************************************************************************** */
    
    /* 
     * Load native library.
     */
    static
    {
        System.loadLibrary("usb");
    }
}
