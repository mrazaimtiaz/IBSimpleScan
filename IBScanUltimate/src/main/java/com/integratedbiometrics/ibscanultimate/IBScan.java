/* *************************************************************************************************
 * IBScan.java
 * 
 * DESCRIPTION:
 *     Android Java wrapper for IBScanUltimate library
 *     http://www.integratedbiometrics.com
 *
 * NOTES:
 *     Copyright (c) Integrated Biometrics, 2013
 *     
 * HISTORY:
 *     2013/03/01  First version.
 *     2013/04/06  Added method to enable or disable trace log.
 *                 Modified various variable declarations with "final".
 *                 Added method setContext() to set the context.
 *     2015/04/09  Added method updateUsbPermission() to update USB permission on rooted device.
 *                 Added method unloadLibrary() to release the library manaually.
 *     2018/11/19  Added method getRequiredSDKVersion()
 *     2020/09/21  Added method setCustomerKey(), getErrorString()
 ************************************************************************************************ */

package com.integratedbiometrics.ibscanultimate;

import java.util.HashMap;
import java.util.Iterator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import org.libusb.LibUsbManager;

/**
 * Principal class for managing IB scanners.
 * <p>
 * The single instance of this class may be gotten with <code>getInstance()</code>.  The application 
 * will typically register a <code>IBScanListener</code> to receive notifications for events such as 
 * device count change and device communication failure.  Device instances should be obtained by
 * either the blocking <code>openDevice()</code> method or non-blocking <code>openDeviceAsync()</code> 
 * method.  The activity accessing IB scanners must set the context for operations with 
 * <code>setContext()</code>.
 */
public class IBScan
{
    /* *********************************************************************************************
     * PUBLIC INNER CLASSES
     ******************************************************************************************** */

    /**
     * Container to hold version information.
     */
    public static final class SdkVersion
    {
        /**
         * Product version string.
         */
        public final String product;

        /**
         * File version string.
         */
        public final String file;

        /* Instantiate version & initialize version information. */
        protected SdkVersion(final String product, final String file)
        {
            this.product = product;
            this.file = file;
        }

        @Override
        public String toString()
        {
            final String s = "Product: " + this.product + "\n" + 
                             "File: "    + this.file    + "\n";
            return (s);
        }
    }

    /**
     * Basic device description structure.
     */
    public static final class DeviceDesc
    {
        /**
         * Device serial number.
         */
        public final String serialNumber;

        /**
         * Device product name.
         */
        public final String productName;

        /**
         * Device interface type (USB, Firewire).
         */
        public final String interfaceType;

        /**
         * Device firmware version.
         */
        public final String fwVersion;

        /**
         * Device revision.
         */
        public final String devRevision;

        /**
         * Indicates whether device is opened.
         */
        public final boolean isOpened;

        /**
         * Indicates whether device is locked.
         */
        public final boolean isLocked;

        /**
         * Customer string to display.
         */
        public final String customerString;
        
        /**
         * ID of the device.  This is the ID that Android assigns to the device, obtained 
         * through the <code>UsbDevice</code> <code>getDeviceId()</code> method.
         */
        public final int deviceId;
        
        /* Instantiate device description & initialize device description information. */
        protected DeviceDesc(final String serialNumber, final String productName,
            final String interfaceType, final String fwVersion,
            final String devRevision, final boolean isOpened,
            final int deviceId, final boolean isLocked, final String customerString)
        {
            this.serialNumber  = serialNumber;
            this.productName   = productName;
            this.interfaceType = interfaceType;
            this.fwVersion     = fwVersion;
            this.devRevision   = devRevision;
            this.isOpened      = isOpened;
            this.deviceId      = deviceId;
            this.isLocked      = isLocked;
            this.customerString= customerString;
        }

        @Override
        public String toString()
        {
            final String s = "Serial Number: "    + this.serialNumber  + "\n" + 
                             "Product Name: "     + this.productName   + "\n" + 
            		         "Interface Type: "   + this.interfaceType + "\n" + 
                             "Firmware Version: " + this.fwVersion     + "\n" + 
            		         "Device Revision: "  + this.devRevision   + "\n" +
                             "Device Opened: "    + this.isOpened      + "\n" +
                             "Device Locked: "    + this.isLocked      + "\n" +
                             "Customer String: "  + this.customerString + "\n"+
            		         "Device ID: "        + this.deviceId      + "\n";
            return (s);
        }
    }

    /* *********************************************************************************************
     * (OBJECT) PUBLIC INTERFACE
     ******************************************************************************************** */

    /**
     * Enables or disable trace log in native library.  The trace log is enabled
     * by default.
     *
     * @param on  <code>true</code> to enable trace log; <code>false</code> to 
     *            disable trace log
     * @throws    IBScanException
     */
    public void enableTraceLog(final boolean on) throws IBScanException
    {
        final NativeError error = new NativeError();
        enableTraceLogNative(on, error);
        handleError(error); /* throws exceptin if necessary */
    }

    /**
     * Update the permission to allow the approach to attached USB bus by libusb library.
     * It is required rooting
     * @throws    IBScanException
     */
    public void updateUsbPermission()
    {
        updateUsbPermissionNative();
    }

    /**
     * Obtains product and software version information.
     * 
     * @return SDK version
     * @throws IBScanException
     */
    public SdkVersion getSdkVersion() throws IBScanException
    {
        final NativeError error   = new NativeError();
        final SdkVersion  version = getSdkVersionNative(error);
        handleError(error); /* throws exception if necessary */

        return (version);
    }
    
    /**
     * Retrieve count of connected IB USB scanner devices.  Only the attached devices to which the
     * caller has been granted permission will be counted.
     * 
     * @return count of IB USB scanner devices
     * @throws IBScanException
     */
    public int getDeviceCount() throws IBScanException
    {
        final NativeError error = new NativeError();
        final int count = getDeviceCountNative(error);
        handleError(error); /* throws exception if necessary */

        return (count);
    }

    /**
     * Request permission to access the device.  Success or failure will be returned to the user 
     * through the registered <code>IBScanListener</code>'s <code>scanDevicePermissionGranted()</code>
     * callback.  If permission has not already been granted to the device, a dialog may be shown
     * to the user.
     * 
     * @param deviceId  ID of the device.  This is the ID that Android assigns to the device,
     *                  obtained through the <code>UsbDevice</code> <code>getDeviceId()</code> method.
     */
    public void requestPermission(final int deviceId)
    {
    	/* Request permission with the USB manager. */
    	final UsbDevice device = findDevice(deviceId);
    	if (device != null)
    	{  	
	    	/* Create intent and request permission with the USB manager. */
	    	final UsbManager    manager          = (UsbManager)this.m_context.getSystemService(Context.USB_SERVICE);
	    	final Intent        intent           = new Intent(ACTION_USB_PERMISSION);
	    	final PendingIntent permissionIntent = PendingIntent.getBroadcast(this.m_context, 0, intent, 0);
	    	manager.requestPermission(device, permissionIntent);
    	}
    }
    
    /**
     * Determines whether the caller has permission to access the device.
     * 
     * @param deviceId  ID of the device.  This is the ID that Android assigns to the device,
     *                  obtained through the <code>UsbDevice</code> <code>getDeviceId()</code> method.
     * @return          <code>true</code> if caller has permission; <code>false</code> otherwise.
     */
    public boolean hasPermission(final int deviceId)
    {
    	boolean hasPermission = false;
    	
    	final UsbDevice device = findDevice(deviceId);
    	if (device != null)
    	{  	
	    	final UsbManager manager = (UsbManager)this.m_context.getSystemService(Context.USB_SERVICE);	    	
	    	hasPermission = manager.hasPermission(device);
    	}
    	
    	return (hasPermission);
    }
        
    /**
     * Retrieve detailed device information about particular scanner by logical index.
     * 
     * @param deviceIndex  zero-based index of the device
     * @return             a description of the device
     * @throws             IBScanException
     */
    public DeviceDesc getDeviceDescription(final int deviceIndex)
        throws IBScanException
    {
        final NativeError error = new NativeError();
        final DeviceDesc  desc  = getDeviceDescNative(deviceIndex, error);
        handleError(error); /* throws exception if necessary */

        return (desc);
    }

    /**
     * Initialize device, given a particular by device index.  This function blocks until an error
     * occurs or initialization completes; meanwhile any registered <code>IBScanListener</code> will 
     * receive <code>scanDeviceInitProgress()</code> callbacks to track the initialization progress.
     * Either a device object will be returned to the application or an exception will be thrown.
     * 
     * @param  deviceIndex  zero-based index of the device
     * @return              device object, if initialization succeeds; <code>null</code> otherwise
     * @throws              IBScanException
     */
    public IBScanDevice openDevice(final int deviceIndex) throws IBScanException
    {
    	final NativeError  error      = new NativeError();
        final IBScanDevice scanDevice = openDeviceNative(deviceIndex, error);
        handleError(error); /* throws exception if necessary */

        return (scanDevice);
    }

    /**
     * See also <code>openDevice(int)</code>.
     * 
     * @param  deviceIndex        zero-based index of the device
     * @param  uniformityMaskPath uniformity mask path
     * @return                    device object, if initialization succeeds; <code>null</code> 
     *                            otherwise
     * @throws                    IBScanException
     */
    public IBScanDevice openDevice(final int deviceIndex,
        final String uniformityMaskPath) throws IBScanException
    {
        if (uniformityMaskPath == null)
        {
        	logPrintWarning(getMethodName() + ": receive null uniformityMaskPath");
        	throw (new IllegalArgumentException());
        }
        
    	final NativeError  error  = new NativeError();
        final IBScanDevice device = openDeviceExNative(deviceIndex, uniformityMaskPath, error);
        handleError(error); /* throws exception if necessary */

        return (device);
    }

    /**
     * Initialize device asynchronously, given a particular by device index.  This function returns
     * immediately.  Any registered <code>IBScanListener</code> will receive <code>scanDeviceInitProgress()</code> 
     * callbacks to track the initialization progress.  When an error occurs or initialization 
     * completes, <code>scanDeviceOpenComplete()</code> will be invoked with either a device object 
     * or a description of the error that occurred.
     * 
     * @param  deviceIndex zero-based index of the device
     * @throws             IBScanException
     */
    public void openDeviceAsync(final int deviceIndex) throws IBScanException
    {
    	final NativeError error = new NativeError();
        openDeviceAsyncNative(deviceIndex, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * See also <code>openDeviceAsync(int)</code>
     * 
     * @param  deviceIndex        zero-based index of the device
     * @param  uniformityMaskPath uniformity mask path
     * @throws                    IBScanException
     */
    public void openDeviceAsync(final int deviceIndex,
        final String uniformityMaskPath) throws IBScanException
    {
        if (uniformityMaskPath == null)
        {
        	logPrintWarning(getMethodName() + ": receive null uniformityMaskPath");
        	throw (new IllegalArgumentException());
        }
        
    	final NativeError error = new NativeError();
        openDeviceAsyncExNative(deviceIndex, uniformityMaskPath, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Get initialization progress.
     * 
     * @param  deviceIndex  zero-based index of the device
     * @return              initialization progress between 0 and 100. A value of 100 indicates that
     *                      that initialization is complete.
     * @throws              IBScanException
     */
    public int getInitProgress(final int deviceIndex) throws IBScanException
    {
    	final NativeError error    = new NativeError();
        final int         progress = getInitProgressNative(deviceIndex, error);
        handleError(error); /* throws exception if necessary */

        return (progress);
    }

    /**
     * The library is unmapped from the address space manually, and the library is no longer valid
     * So APIs will not be worked correctly after calling 
     * Some platform SDKs (Windows Mobile, Android)
     * can be needed to call IBSU_UnloadLibrary() before shutting down the application.
     * 
     * @throws              IBScanException
     */
    public void unloadLibrary() throws IBScanException
    {
    	final NativeError error    = new NativeError();
        unloadLibraryNative(error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Get minimum SDK version required for running.
     *
     * @param  deviceIndex  zero-based index of the device
     * @return    	        Minimum SDK Version to be returned
     * @throws              IBScanException
     */
    public String getRequiredSDKVersion(final int deviceIndex) throws IBScanException
    {
        final NativeError error = new NativeError();
        final String minSDKVersion = getRequiredSDKVersionNative(deviceIndex, error);
        handleError(error); /* throws exceptin if necessary */
        
        return (minSDKVersion);
    }
    
    /**
     * Enumeration of Hash Type
     */
    public static enum HashType
    {
    	/**
         * Hash type : SHA256
         */
        SHA256(0),

        /**
         * Hash type : Reserved
         */
        Reserved(1);

        /* Native value for enumeration. */
        private final int code;

    	HashType(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static HashType fromCode(int code)
        {
            for (HashType t : HashType.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        public int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Set customerkey for running of the locked device.
     * This is must performed on the locked device before open the device
     *
     * @param  deviceIndex  zero-based index of the device
     * @param  hashType     type of Hash
     * @param  customerKey  customer key to match lock info written in the locked device
     * @return    	        Minimum SDK Version to be returned
     * @throws              IBScanException
     */
    public void setCustomerKey(final int deviceIndex,
        final HashType hashType,
        final String customerKey) throws IBScanException
    {
        final NativeError error = new NativeError();
        setCustomerKeyNative(deviceIndex, hashType.toCode(), customerKey, error);
        handleError(error); /* throws exceptin if necessary */
    }
	
    /**
     * Returns a string description of the error code.
     *
     * @param  errorCode    error code
     * @throws              IBScanException
     */
    public String getErrorString(final int errorCode) throws IBScanException
    {
        final NativeError error = new NativeError();
        final String ErrorString = getErrorStringNative(errorCode, error);
        handleError(error); /* throws exceptin if necessary */
        return (ErrorString);
    }

    /**
     * Set listener for scan events.
     * 
     * @param listener  listener for scan events
     */
    public void setScanListener(final IBScanListener listener)
    {
        this.m_listener = listener;
    }
    
    /**
     * Determine whether device is a scan device.  This just checks whether the vendor and product
     * IDs match recognized devices.
     * 
     * @param device  device to investigate
     * @return        <code>true</code> if device is an IB scan device; <code>false</code> otherwise
     */
    public static boolean isScanDevice(final UsbDevice device)
    {
	    boolean isScanDevice = false;
	    int     vendorId     = device.getVendorId();
	    if (vendorId == VID_IB || vendorId == VID_DERMALOG)
	    {
	        int[] productIds = { PID_CURVE, PID_WATSON, PID_WATSON_REV1, PID_SHERLOCK, PID_SHERLOCK_AUO, 
	        		PID_WATSON_MINI, PID_WATSON_MINI_REV1, PID_COLUMBO, PID_COLUMBO_REV1, PID_KOJAK, PID_KOJAK_REV1, 
	        		PID_HOLMES, PID_FIVE0, PID_FIVE0_REV1, PID_FIVE0_DERMALOG, PID_DANNO, PID_KOJAK_LOCK, PID_KOJAK_DERMALOG, PID_COLUMBO_MINI};
	    	int   productId  = device.getProductId();
	    	for (int productIdTemp : productIds)
	    	{
	    		if (productIdTemp == productId)
	    		{
	    			isScanDevice = true;
	    			break;
	    		}
	    	}
	    }
    	
	    return (isScanDevice);
    }
    
    /**
     * Set the context for this IBScan.  This fuction must be called by an activity for scanners
     * to be recognized and accessible.  When one activity transfers control of the IB scanners
     * to another, this function should be called with a <code>null</code> argument to release
     * the reference to the context and unregister it as a USB receiver.
     * 
     * @param context  the context for the reciever and USB accesses.  If <code>null</cdde>,
     *                 the existing context will still be unregistered as a receiver and the 
     *                 reference to it will be cleared.
     */
    public void setContext(final Context context)
    {
        /* Clear current context. */
        if (this.m_context != null)
        {
            this.m_context.unregisterReceiver(this.m_usbReceiver);
            this.m_context = null;
            
            LibUsbManager.setContext(null);
        }
        
        /* Set new context. */
        if (context != null)
        {
            this.m_context = context;
            
            LibUsbManager.setContext(this.m_context);
          
            /* Register broadcast receiver to receive USB events. */
            final IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            filter.addAction(ACTION_USB_PERMISSION);
            this.m_context.registerReceiver(this.m_usbReceiver, filter);
        }
    }

    /* *********************************************************************************************
     * (CLASS) PUBLIC INTERFACE
     ******************************************************************************************** */

    /**
     * Get single instance of class.
     * 
     * @param context  the context for the reciever and USB accesses.
     * @return         single instance of <code>IBScan</code>.
     */
    public static IBScan getInstance(final Context context)
    {
        if (IBScan.m_instance == null)
        {
            IBScan.m_instance = new IBScan();
        }
        
        IBScan.m_instance.setContext(context);

        return (IBScan.m_instance);
    }

    /* *********************************************************************************************
     * PROTECTED INNER CLASSES
     ******************************************************************************************** */

    /*
     *  Container for native error value.
     */
    protected static final class NativeError
    {
        public int code = 0;
    }

    /* *********************************************************************************************
     * PRIVATE INTERFACE
     ******************************************************************************************** */

    /*
     *  Protected default constructor to prevent external instantiation.
     */
    private IBScan()
    {
        initNative();
    }
       
    /*
     * Intent action when USB device access permission is granted or denied.
     */
    private static final String ACTION_USB_PERMISSION = "ibscan.USB_PERMISSION";

    /*
     * Vendor ID and product IDs for IB scanners.
     */
    private static final int VID_IB                 = 0x113F;
	private static final int VID_DERMALOG           = 0x1FBA; // Added Dermalog Five-O / For TF10 model
	
    private static final int PID_CURVE              = 0x1004;
    private static final int PID_WATSON             = 0x1005;
    private static final int PID_WATSON_REV1        = 0x1006;
    private static final int PID_SHERLOCK           = 0x1010;
    private static final int PID_SHERLOCK_AUO       = 0x1011;
    private static final int PID_WATSON_MINI        = 0x1020;
    private static final int PID_WATSON_MINI_REV1   = 0x1021;
    private static final int PID_COLUMBO            = 0x1100;
    private static final int PID_COLUMBO_REV1       = 0x1101;
    private static final int PID_HOLMES             = 0x1200;
    private static final int PID_KOJAK              = 0x1300;
    private static final int PID_KOJAK_REV1         = 0x1301;
	private static final int PID_KOJAK_DERMALOG     = 0x0036; // For Dermalog Kojak support
    private static final int PID_FIVE0              = 0x1500;
    private static final int PID_FIVE0_REV1         = 0x1501;
	private static final int PID_FIVE0_DERMALOG     = 0x0034; // For Dermalog Five-0 support
	private static final int PID_DANNO			    = 0x1600;
    private static final int PID_KOJAK_LOCK         = 0x1A00;
    private static final int PID_COLUMBO_MINI       = 0x7100;

    /* 
     * Find device for a certain device ID.
     * 
     * @param deviceId  device ID to search for
     * @return          device, if found; <code>null</code> otherwise
     */
    private UsbDevice findDevice(final int deviceId)
    {
    	UsbDevice device = null;
    	
        if (this.m_context != null)
        {
            final UsbManager                 manager    = (UsbManager)this.m_context.getSystemService(Context.USB_SERVICE);
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
        }

    	return (device);
    }
    
    /*
     *  Handle error from native method.  If an error is returned, the appropriate exception will 
     *  be thrown.
     *  
     *  @param error  error set in native code
     *  @throws       IBScanException
     */
    private static void handleError(final NativeError error) throws IBScanException
    {
        if (error.code != 0)
        {
            IBScanException.Type type;

            type = IBScanException.Type.fromCode(error.code);
            if (type == null)
            {
            	logPrintError(getMethodName() + ": unrecognized error code(" + error.code + ") returned from native code");
            	type = IBScanException.Type.COMMAND_FAILED;
            }
            throw (new IBScanException(type));
        }
    }

    /*
     *  Log warning to System.out.
     */
    private static void logPrintWarning(final String ln)
    {
    	Log.w("IBScan", ln);
    }
    
    /* 
     * Log warning to System.out.
     */
    private static void logPrintError(final String ln)
    {
    	Log.e("IBScan", ln);
    }
    
    /*
     * The stack index at which a caller method's name will be found.
     */
    private static int METHOD_STACK_INDEX;

    /*
     *  Get name of method caller.
     */
    private static String getMethodName() 
    {
    	StackTraceElement[] stackTrace;
    	String              name;
    	
    	stackTrace = Thread.currentThread().getStackTrace();
    	/* Sanity check the index, though it should always be within bounds. */
    	if (stackTrace.length > METHOD_STACK_INDEX)
    	{
    		name = stackTrace[METHOD_STACK_INDEX].getMethodName();
    	}
    	else
    	{
    		name = "?";
    	}
        return (name);
    }
        
    /*
     *  Scan listener.
     */
    private IBScanListener m_listener = null;

    /*
     * Scan context.
     */
    private Context m_context = null;
    
    /*
     *  Singleton scan object.
     */
    private static IBScan m_instance = null;
    
    /*
     * Broadcast receiver to listen for USB device events.
     */
    private final BroadcastReceiver m_usbReceiver = new BroadcastReceiver()
    {
    	@Override
    	public void onReceive(final Context context, final Intent intent)
    	{
    		final String action = intent.getAction();
    		
    		/* Receive event about device attachment. */
    		if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED))
    		{
    			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);    		
    			if (device != null)
    			{
    				final boolean isScanDevice = isScanDevice(device);
    				if (isScanDevice)
    				{
    					callbackScanDeviceAttached(device.getDeviceId());
    				}
    			}
    		}
    		
    		/* Receive event about device detachment. */
    		else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED))
    		{
    			UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
       			if (device != null)
    			{
       				final boolean isScanDevice = isScanDevice(device);
    				if (isScanDevice)
    				{
    					callbackScanDeviceDetached(device.getDeviceId());
    				}
    			}
    		}
    		
    		/* Receive event about access permission granted or denied. */
    		else if (action.equals(ACTION_USB_PERMISSION))
    		{    			
    			/* Sanity check.  This should always be true. */
    			final boolean exists = intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED);
    			if (exists)
    			{
    				/* Alert user about status. */
					final boolean   granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
					final UsbDevice device  = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (device != null)
					{
	    				boolean isScanDevice = isScanDevice(device);
	    				if (isScanDevice)
	    				{
	    					callbackScanDevicePermissionGranted(device.getDeviceId(), granted);
	    				}
					}
    			}
    		}
    	}    	
    };

    /* *********************************************************************************************
     * INTERFACE METHODS: IBScanListener INTERMEDIATES
     ******************************************************************************************** */

    /*
     * Callback for scan device attach.
     */
    private static void callbackScanDeviceAttached(final int deviceId)
    {
    	final IBScan ibScan = IBScan.m_instance;

		if (ibScan != null)
		{
	        if (ibScan.m_listener != null)
	        {
	            ibScan.m_listener.scanDeviceAttached(deviceId);
	        }
		}
    }

    /*
     * Callback for scan device detach.
     */
    private static void callbackScanDeviceDetached(final int deviceId)
    {
    	final IBScan ibScan = IBScan.m_instance;

		if (ibScan != null)
		{
	        if (ibScan.m_listener != null)
	        {
	            ibScan.m_listener.scanDeviceDetached(deviceId);
	        }
		}
    }
    
    /*
     * Callback for device access granted or denied.
     */
    private static void callbackScanDevicePermissionGranted(final int deviceIndex, final boolean granted)
    {
    	final IBScan ibScan = IBScan.m_instance;

 		if (ibScan != null)
		{
	        if (ibScan.m_listener != null)
	        {
	            ibScan.m_listener.scanDevicePermissionGranted(deviceIndex, granted);
	        }
		}
    }
    
    /*
     * Callback for device initialization progress.  Called from native code.
     */
    private static void callbackScanDeviceInitProgress(final int deviceIndex, final int progressValue)
    {
    	final IBScan ibScan = IBScan.m_instance;

 		if (ibScan != null)
		{
	        if (ibScan.m_listener != null)
	        {
	            ibScan.m_listener.scanDeviceInitProgress(deviceIndex, progressValue);
	        }
		}
    }

    /*
     * Callback for scan device count change.  Called from native code.
     */
    private static void callbackScanDeviceCountChanged(final int deviceCount)
    {
        final IBScan ibScan = IBScan.m_instance;

		if (ibScan != null)
		{
	        if (ibScan.m_listener != null)
	        {
	            ibScan.m_listener.scanDeviceCountChanged(deviceCount);
	        }
		}
    }

    /*
     * Callback for device open completion.  Called from native code.
     */
    private static void callbackScanDeviceOpenComplete(final int deviceIndex, final IBScanDevice device,
    		final int exceptionCode)
    {
    	final IBScan ibScan = IBScan.m_instance;

 		if (ibScan != null)
		{
	        if (ibScan.m_listener != null)
	        {
	            IBScanException.Type type = IBScanException.Type.fromCode(exceptionCode);
	            ibScan.m_listener.scanDeviceOpenComplete(deviceIndex, device, new IBScanException(type));
	        }
		}
    }
    /* *********************************************************************************************
     * NATIVE METHODS
     ******************************************************************************************** */

    /* Native method for constructor. */
    private native void initNative();

    /* Native method for enableTraceLog(). */
    private native void enableTraceLogNative(boolean on, NativeError error);

    /* Native method for updateUsbPermissionNative(). */
    private native void updateUsbPermissionNative();

    /* Native method for getSDKVersion(). */
    private native SdkVersion getSdkVersionNative(NativeError error);

    /* Native method for getDeviceCount(). */
    private native int getDeviceCountNative(NativeError error);

    /* Native method for getDeviceDesc(). */
    private native DeviceDesc getDeviceDescNative(int deviceIndex, NativeError error);

    /* Native method for openDevice(int). */
    private native IBScanDevice openDeviceNative(int deviceIndex, NativeError error);

    /* Native method for openDevice(int, String). */
    private native IBScanDevice openDeviceExNative(int deviceIndex, String uniformityMaskPath,
    		NativeError error);

    /* Native method for openDeviceAsync(int). */
    private native void openDeviceAsyncNative(int deviceIndex, NativeError error);

    /* Native method for openDeviceAsync(int, String). */
    private native void openDeviceAsyncExNative(int deviceIndex, String uniformityMaskPath,
    		NativeError error);

    /* Native method for getInitProgress(). */
    private native int getInitProgressNative(int deviceIndex, NativeError error);

    /* Native method for unloadLibrary(). */
    private native int unloadLibraryNative(NativeError error);
    
    /* Native method for getRequiredSDKVersion(). */
    private native String getRequiredSDKVersionNative(int deviceIndex, NativeError error);
    
    /* Native method for setCustomerKey(). */
    private native void setCustomerKeyNative(int deviceIndex, int hashType, String customerKey, NativeError error);
	
	/* Native method for getErrorString(). */
    private native String getErrorStringNative(int errorCode, NativeError error);

    /* *********************************************************************************************
     * STATIC BLOCKS
     ******************************************************************************************** */
    
    /* 
     * Helper block to get method name for debug messages. 
     */
    static 
    {
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) 
        {
            i++;
            if (ste.getClassName().equals(IBScan.class.getName())) 
            {
                break;
            }
        }
        METHOD_STACK_INDEX = i;
    }
    
    /* 
     * Load native library.
     */
    static
    {
        System.loadLibrary("usb");
        if (System.getProperty("PPI_BUILD") != null) {
            System.loadLibrary("DeviceParallel");
        }
        System.loadLibrary("IBScanUltimate");
        System.loadLibrary("ibscanultimatejni");
    }
}
