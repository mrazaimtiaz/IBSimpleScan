/* *************************************************************************************************
 * IBScanDevice.java
 *
 * DESCRIPTION:
 *     Android Java wrapper for IBScanUltimate library
 *     http://www.integratedbiometrics.com
 *
 * NOTES:
 *     Copyright (c) Integrated Biometrics, 2013-2017
 *     
 * HISTORY:
 *     2013/03/01  First version.
 *     2013/03/06  Added getDescription() function to ImageType enumeration.
 *     2013/03/22  Added calculateNfiqScore() function.
 *     2013/03/25  Added getResultImageExt() function.
 *     2013/07/06  Added constants for LED bits.
 *     2013/10/18  Added new PropertyId enumerations (CAPTURE_TIMEOUT, ROLL_MIN_WIDTH).
 *                 Added new FingerQualityState enumerations (INVALID_AREA_TOP, INVALID_AREA_LEFT,
 *                 INVALID_AREA_RIGHT).
 *                 Added new toBitmapScaled() methods to add invalid area indicators.
 *                 Added captureImageExtended() method.
 *                 Added callback for extended result information.
 *                 Added SegmentPosition class.
 *                 Added EventType class and enableEvent() method.
 *     2014/02/25  Added new PropertyId emumerations (ROLL_MODE,ROLL_LEVEL)
 *     2014/06/19  The method toBitmapScaled was extened to support the thickness of rolling guide line 
 *     2014/11/24  Added generateZoomOutImageEx() method to increase speed to draw the image.
 *     2015/04/07  Added getEnhancedImageReserved() method to increase speed to draw the image.
 *                 Added setPropertyReserved() method.
 *                 Added wsqEncodeToFile() method.
 *     2015/08/07  Added getCombineImage() method.
 *     2015/12/09  Added additional LED definitions for Kojak.
 *                 Added new ImageType enumerations (FLAT_THREE_FINGERS)
 *                 Added new PropertyId enumerations (SUPER_DRY_MODE)
 *                 Added new LedType enumerations (FSCAN)
 *                 Added BeeperType and BeepPattern enum
 *                 Added callback for detecting the key button of device was pressed.
 *     2016/01/21  Added new PropertyId enumerations (MIN_CAPTURE_TIME_IN_SUPER_DRY_MODE)
 *     2016/04/20  new PropertyId enumerations
 *                 (ROLLED_IMAGE_WIDTH, ROLLED_IMAGE_HEIGHT )
 *     2016/09/22  Added new PropertyId enumerations
 *                 (NO_PREVIEW_IMAGE, ROLL_IMAGE_OVERRIDE )
 *     2017/04/27  Added getCombineImageEx() method.
 *                 Added new PropertyId enumerations
 *                 (WARNING_MESSAGE_INVALID_AREA, ENABLE_WET_FINGER_DETECT,
 *                  WET_FINGER_DETECT_LEVEL) 
 *                 Added new FingerQualityState enumerations
 *                 (INVALID_AREA_BOTTOM )
 *     2017/06/16  Added enumeration value to IBSU_PropertyId
 *                  (WET_FINGER_DETECT_LEVEL_THRESHOLD, 
 *                  START_POSITION_OF_ROLLING_AREA,
 *	               START_ROLL_WITHOUT_LOCK,
 *                  ENABLE_TOF,
 *                  RESERVED_ENABLE_TOF_FOR_ROLL, 
 *                  RESERVED_CAPTURE_BRIGHTNESS_THRESHOLD_FOR_FLAT,
 *                  RESERVED_CAPTURE_BRIGHTNESS_THRESHOLD_FOR_ROLL,
 *                  RESERVED_ENHANCED_RESULT_IMAGE)
 *     2017/12/11  Added generateDisplayImage() method.
 *     2018/04/27  Added removeFingerImageNative(), addFingerImageNative(), 
  *                    isFingerDuplicatedNative(), isValidFingerGeometryNative(),
 *                    SaveBitmapImageNative() method.
 *     2018/08/02  Added SaveBitmapImageNative() native method.
 *     2018/11/19  Updated SaveBitmapImageNative() native method.
 *                 Added SaveJP2ImageNative() native method.
 *                 Added SavePngImageNative() native method.
 *     2019/06/21  Added SetEncryptionKeyNative() method.
 *     2019/10/25  Added enumeration value to IBSU_PropertyId
 *                  (FINGERPRINT_SEGMENTATION_MODE,
                     VIEW_ENCRYPTION_IMAGE_MODE)
 *     2020/01/09  Added enumeration value to IBSU_PropertyId
 *                  (IS_SPOOF_SUPPORTED,
 *                   ENABLE_SPOOF,
 *                   SPOOF_LEVEL)
 *     2020/04/01  Added enumeration value to IBSU_PropertyId
 *                  (ROLL_METHOD,
 *                   RENEWAL_OPPOSITE_IMGAE_LEVEL,
 *                   PREVIEW_IMAGE_QUALITY_FOR_KOJAK,
 *                   RESERVED_ENHANCED_RESULT_IMAGE_LEVEL,
 *                   RESERVED_ENABLE_SLIP_DETECTION, 
 *                   RESERVED_SLIP_DETECTION_LEVEL)
 *     2020/04/01  Added enumeration value to IBSU_PropertyId
 *                  (RESERVED_ENABLE_TRICK_CAPTURE)
 *     2020/08/19  Added enumeration value to IBSU_PropertyId
 *                  (ADAPTIVE_CAPTURE_MODE,
 *                   ENABLE_KOJAK_BEHAVIOR_2_6)
 *     2021/02/17  Added wsqEncodeToMemNative() method
 *                 Added wsqDecodeToMemNative() method
 *     2021/07/16  Added enumeration value to IBSU_PropertyId
 *                  (VERTICAL_DIRECTION_SEGMENT, RESERVED_SPOOF_LEVEL_THRESHOLD )
 *	   2022/02/18  Modified enumeration name changed from 
 *                        VERTICAL_DIRECTION_SEGMENT to
 *                        DISABLE_SEGMENT_ROTATION
 *     2022/04/29  Added SaveStandardFileNative() method
 *                 Added ConvertImageToISOANSINative() method
 *********************************************************************************************** */

package com.integratedbiometrics.ibscanultimate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.integratedbiometrics.ibscancommon.IBCommon;
import com.integratedbiometrics.ibscancommon.IBCommon.ImageDataExt;
import com.integratedbiometrics.ibscancommon.IBCommon.StandardFormatData;
import com.integratedbiometrics.ibscancommon.IBCommon.ImageFormat;
import com.integratedbiometrics.ibscancommon.IBCommon.StandardFormat;

/**
 * Principal class for interfacing with particular IB scanners.
 */
public class IBScanDevice
{
    /* *********************************************************************************************
     * PUBLIC INNER CLASSES
     ******************************************************************************************** */

    /**
     * General property definitions.  Property values of an <code>IBScanDevice</code> are set with 
     * <code>setProperty()</code> and gotten with <code>getProperty()</code>.
     */
    public static enum PropertyId
    {
        /**
         * [get] Product name string (e.g. "Watson")
         */
        PRODUCT_ID(0),

        /**
         * [get] Serial number string
         */
        SERIAL_NUMBER(1),

        /**
         * [get] Device manufacturer identifier
         */
        VENDOR_ID(2),

        /**
         * [get] IBIA vendor ID
         */
        IBIA_VENDOR_ID(3),

        /**
         * [get] IBIA version information
         */
        IBIA_VERSION(4),

        /**
         * [get] IBIA device ID
         */
        IBIA_DEVICE_ID(5),

        /**
         * [get] Firmware version string
         */
        FIRMWARE(6),

        /**
         * [get] Device revision string
         */
        REVISION(7),

        /**
         * [get] Production date string
         */
        PRODUCTION_DATE(8),

        /**
         * [get] Last service date string
         */
        SERVICE_DATE(9),

        /**
         * [get] Image width value
         */
        IMAGE_WIDTH(10),

        /**
         * [get] Image height value
         */
        IMAGE_HEIGHT(11),

        /**
         * [get/set] The time in milliseconds to acquire the finger print in the auto capture mode 
         * regardless of the number of fingers.  The capture option <code>OPTION_AUTO_CAPTURE</code> 
         * must be given when capture is begun (with <code>beginCaptureImage()</code>).  
         * The default value is 4000-ms and the value may range between 2000- and 10000-ms.
         */
        IGNORE_FINGER_TIME(12),

        /**
         * [get/set] Auto contrast level value
         */
        RECOMMENDED_LEVEL(13),

        /**
         * [get] Polling time for blocking image capture (with <code>captureImage()</code>).
         */
        POLLINGTIME_TO_BGETIMAGE(14),

        /**
         * [get/set] Power save mode.  Specify the value "TRUE" to enable or "FALSE" to disable.  By 
         * default, power save mode is disabled.
         */
        ENABLE_POWER_SAVE_MODE(15),

        /**
         * [get/set] The retry count for communication failures.  The default value is 6, and 
         * the value may range between 1 and 120.
         */
        RETRY_WRONG_COMMUNICATION(16),

        /**
         * [get/set] The maximum wait time for image capture, in seconds.  If -1, the timeout is 
         * infinite.  Otherwise, the valid range is between 10- and 3600-seconds, inclusive.  The 
         * default is -1. 
         */
        CAPTURE_TIMEOUT(17),

        /** 
         * [get/set] Minimum distance of rolled fingerprint, in millimeters.  The valid range is 
         * between 10- and 30-mm, inclusive.  The default is 15-mm.
         */
        ROLL_MIN_WIDTH(18),

        /* [get/set] Roll mode. The valid range is between 0 ~ 1.  The default is 1.
         * 0 : no use smear
         * 1 : use notice
         */
        ROLL_MODE(19),

        /* [get/set] Roll level. The valid range is between 0 ~ 2.  The default is 1.
         * 0 : low level
         * 1 : medium level
         * 2 : high level
         */
        ROLL_LEVEL(20),

        /* [get/set] The area threshold for image capture for flat fingers and
         * The area threshold for beginning rolled finger.
         * The valid range is between 0 and 12, inclusive, with the default of 6.
         */
        CAPTURE_AREA_THRESHOLD(21),

        /* [get/set] Enable decimation mode (TRUE to enable or FALSE to disable).
         * Some of devices (or firmware version) does not support this feature.
         */
        ENABLE_DECIMATION(22),

        /* [get/set] Enable capture on release (TRUE to enable or FALSE to disable). The default is FALSE.
         * TRUE  : the result callback will be called when user release the finger from the sensor.
         * FALSE : the result callback will be called when the quality of finger become good
         */
        ENABLE_CAPTURE_ON_RELEASE(23),

        /*
         * [get] The device index.
         */
        DEVICE_INDEX(24),

        /*
         * [get] The device ID which has same information with UsbDevice class of Android.
         */
		DEVICE_ID(25),
	
		/* [get/set] It can be used for dry finger
	     * Some of devices (or firmware version) does not support this feature.
	     * The default is FALSE.
	     * TRUE  : Enable dry mode.
	     * FALSE : Disable dry mode 
	     */
		SUPER_DRY_MODE(26),

		/*  [Get and set.]It is a minimum capture time when the dry mode is enabled with the property ENUM_IBSU_PROPERTY_SUPER_DRY_MODE
	     * Some of devices (or firmware version) does not support this feature.
	     * The valid range is between 600- and 3000-ms, 
	     * inclusive, with the default of 2000-ms. 
	     */
		MIN_CAPTURE_TIME_IN_SUPER_DRY_MODE(27),

	    /* Rolled image width value.  [Get only.] */
	    ROLLED_IMAGE_WIDTH(28),           
	
	    /* Rolled image height value.  [Get only.] */
	    ROLLED_IMAGE_HEIGHT(29),              

        /* Enable the drawing for preview image (TRUE to enable or FALSE to disable). 
         * The default is TRUE.  [Get and set.] */
        NO_PREVIEW_IMAGE(30),

        /* Enable to override roll image (TRUE to enable or FALSE to disable). 
         * The default is FALSE.  [Get and set.] */
        ROLL_IMAGE_OVERRIDE(31),

	    /* Enable the warning message for invalid area for result image (TRUE to enable or FALSE to disable). 
         * The default is FALSE.  [Get and set.] */
	    WARNING_MESSAGE_INVALID_AREA(32),

	    /* Enable wet detect function.
	     * The default is FALSE.  [Get and set.] */
        ENABLE_WET_FINGER_DETECT(33),

	    /* Change wet detect level.
	     * The valid range is between 1 and 5. The default is 3.  [Get and set.]
	     * 1 : Lowest level for detect wet finger : less sensitive
	     * 5 : Highest level for detect wet finger : more sensitive */
	    WET_FINGER_DETECT_LEVEL(34),
	    
		/* Change threshold for each wet detect level.
	 	* The valid range is between 10 and 1000. The default is "50 100 150 200 250"  [Get and set.]
	 	* 50 : Threshold of lowest level for detect wet finger
	 	* 250 : Threshold of highest level for detect wet finger */
		WET_FINGER_DETECT_LEVEL_THRESHOLD(35),
	    
		/* Control rolling area vertically.
	 	* The valid range is between 0 and 9. The default is 0.  [Get and set.]
	 	* 0 : minimum position
	 	* 9 : maximum position */
		START_POSITION_OF_ROLLING_AREA(36),

		/* Enable rolling without lock.
	 	* The default is FALSE.  [Get and set.] */
		START_ROLL_WITHOUT_LOCK(37),

		/* Enable TOF function.
	 	* The default is set depending on the devices.  [Get and set.] */
	    ENABLE_TOF(38),

	  /* Enable Encryption for capture images
    * The default is FALSE.  [Get and set.] */
	    ENABLE_ENCRYPTION(39),

        /* Check if the device supprort spoof function or not */
		IS_SPOOF_SUPPORTED(40),

		/* Enable spoof function
		 * The default is FALSE.  [Get and set.] */
		ENABLE_SPOOF(41),

		/* Change spoof level.
		 * The valid range is between 0 and 10. The default is 5.  [Get and set.]
		 * 0 : Lowest level for spoof finger : less sensitive
		 * 10 : Highest level for spoof finger : more sensitive */
		SPOOF_LEVEL(42),
		
		/* View encrypt Image
		* The default is FALSE.  [Get and set.] */
		VIEW_ENCRYPTION_IMAGE_MODE(43),

		/* Select fingerprint segmentation mode
		 * The default is 0.  [Get and set.] */
		FINGERPRINT_SEGMENTATION_MODE(44),

		/* Enhanced roll Method
	     	 * The default values are 0.  [Get and set.] */
		ROLL_METHOD(45),

		/* Select a level of oppisite image value during roll
		 * The default values are 0.  [Get and set.] 
		 * 0 : No use
		 * 1 : renewal if roll image is moved as 1.2mm.
		 * 2 : renewal if roll image is moved as 2.4mm.
		 * 3 : renewal if roll image is moved as 3.6mm. */	
		RENEWAL_OPPOSITE_IMGAE_LEVEL(46),
		/* Enable to High quality preview image for Kojak
		 * The default values are 0.  [Get and set.] */
		PREVIEW_IMAGE_QUALITY_FOR_KOJAK(47),
		
		/* Enable Adaptive Capture
        * The default values are FALSE. [Get and set.] */
	    ADAPTIVE_CAPTURE_MODE(48),

	    /* Enable to Kojak 2.6 behavior
         * The default values are FALSE.  [Get and set.] */
	    ENABLE_KOJAK_BEHAVIOR_2_6(49),
		
		/* Disable to Segment rectangles rotation
         * The default values are FALSE.  [Get and set.] */
		DISABLE_SEGMENT_ROTATION(50),
	
		/**
		 * Reserved string for manufacturer.
		 */
		RESERVED_1(200),

		/**
		 * Reserved string for manufacturer.
		 */
		RESERVED_2(201),

		/**
		 * Reserved string for manufacturer.
		 */
		RESERVED_100(202),
        
		/* The previmage processing threshold.
		 * The valid range is between 0 and 2, inclusive, 
		 * with the default of 0 on embedded processor (ARM, Android and Windows Mobile),
		 * and with the default of 2 on PC. [Get and set.]
		 * 0  : IMAGE_PROCESS_LOW
		 * 1  : IMAGE_PROCESS_MEDIUM
		 * 2  : IMAGE_PROCESS_HIGH */
		RESERVED_IMAGE_PROCESS_THRESHOLD(400),

		/* Enable TOF for roll capture
		* The default is FALSE.  [Get and set.] */
		RESERVED_ENABLE_TOF_FOR_ROLL(401),

		/* Change brightness threshold for flat capture
		* The default values are depending on the scanner.  [Get and set.] */
		RESERVED_CAPTURE_BRIGHTNESS_THRESHOLD_FOR_FLAT(402),

		/* Change brightness threshold for roll capture
		* The default values are depending on the scanner.  [Get and set.] */
		RESERVED_CAPTURE_BRIGHTNESS_THRESHOLD_FOR_ROLL(403),
		
		/* Change result image to be enhanced
		* The default values are FALSE.  [Get and set.] */
		RESERVED_ENHANCED_RESULT_IMAGE(404),
		
		/* Select a level of image processing
		* The default values are 0.  [Get and set.] 
		0 : Minimum enhancement
		5 : Maximum enhancement */
		RESERVED_ENHANCED_RESULT_IMAGE_LEVEL(405),

        /* Enable Slip detection
         * The default values are FALSE. [Get and set.] */
		RESERVED_ENABLE_SLIP_DETECTION(406),

		/* Change level of Slip detection
	    * The default values are 3. The Ragne is from 1 to 10.  [Get and set.] */
		RESERVED_SLIP_DETECTION_LEVEL(407),

	    /* Enable Trick Capture
        * The default values are TRUE. [Get and set.] */
		RESERVED_ENABLE_TRICK_CAPTURE(408),
		
		/* Change threshold for each spoof detect level.
		* The valid range is between 0 and 1000. The default is "457 619 782 857 933"  [Get and set.]
	    * 782 : default value */
		RESERVED_SPOOF_LEVEL_THRESHOLD(409);


        /* Native value for enumeration. */
        private final int code;

        PropertyId(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static PropertyId fromCode(int code)
        {
            for (PropertyId t : PropertyId.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Supported image types.
     * 
     * This is an enumeration of the image types supported by the SDK, and particular scanner models
     * may not support all types. 
     * See <code>isCaptureAvailable()</code>, <code>beginCaptureImage()</code>.
     */
    public static enum ImageType
    {
        /**
         * Not supported yet
         */
        TYPE_NONE(0, "Unsupported image type"),
        /**
         * Rolled fingerprint image. The number of finger in result image: 1
         */
        ROLL_SINGLE_FINGER(1, "One-finger rolled fingerprint"),
        /**
         * Flat single finger. The number of finger in result image: 1
         */
        FLAT_SINGLE_FINGER(2, "One-finger flat fingerprint"),
        /**
         * Two flat fingers. The number of fingers in result image: 2
         */
        FLAT_TWO_FINGERS(3, "Two-finger flat fingerprint"),
        /**
         * Four flat fingers. The number of fingers in result image: 4
         */
        FLAT_FOUR_FINGERS(4, "Four-finger flat fingerprint"),
        /**
         * Three flat fingers. The number of fingers in result image: 3
         */
        FLAT_THREE_FINGERS(5, "Three-finger flat fingerprint");

        /* Native value for enumeration. */
        private final int    code;
        /* Description of this image type. */
        private final String description;
        
        ImageType(int code, String description)
        {
            this.code        = code;
            this.description = description;
        }

        /* Find Java object from native value. */
        protected static ImageType fromCode(int code)
        {
            for (ImageType t : ImageType.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (TYPE_NONE); // Return as default.
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
        
        /* Get description of this image type. */
        public String toDescription()
        {
        	return (this.description);
        }
    }

    /**
     * Image resolution types.
     * See <code>isCaptureAvailable()</code>, <code>beginCaptureImage()</code>.
     */
    public static enum ImageResolution
    {
        /**
         * 500ppi
         */
        RESOLUTION_500(500),
        /**
         * 1000ppi
         */
        RESOLUTION_1000(1000);

        /* Native value for enumeration. */
        private final int code;

        ImageResolution(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static ImageResolution fromCode(int code)
        {
            for (ImageResolution t : ImageResolution.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Light Emitting film's operation mode definitions.
     */
    public static enum LEOperationMode
    {
        /**
		 * 
		 */
        AUTO(0),
        /**
		 * 
		 */
        ON(1),
        /**
		 * 
		 */
        OFF(2);

        /* Native value for enumeration. */
        private final int code;

        LEOperationMode(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static LEOperationMode fromCode(int code)
        {
            for (LEOperationMode t : LEOperationMode.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);  /* Should be handled by caller. */
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * LED field types.
     * 
     * This is a list of different LED fields. IBSU_GetOperableLEDs() returns
     * the component type for the selected device.
     */
    public static enum LedType
    {
        /**
         * no LED field available
         */
        NONE(0),
        /**
         * For two scanner type e.g. Watson
         */
        TSCAN(1),
        
	    /**
         * For four-scanner type (e.g., Kojak)
         */
        FSCAN(2);

        /* Native value for enumeration. */
        private final int code;

        LedType(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static LedType fromCode(int code)
        {
            for (LedType t : LedType.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null); /* Should be handled by caller. */
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Container to hold LED information.
     */
    public static class LedState
    {
        /**
         * Type of LEDs
         */
        public final IBScanDevice.LedType ledType;
        /**
         * Number of LEDs
         */
        public final int ledCount;
        /**
         * Bit pattern of operable LEDs
         */
        public final long operableLEDs;

        protected LedState(int ledTypeCode, int ledCount, long operableLEDs)
        {
            this.ledType      = LedType.fromCode(ledTypeCode);
            this.ledCount     = ledCount;
            this.operableLEDs = operableLEDs;

            if (this.ledType == null)
            {
            	logPrintError(getMethodName() + ": unrecognized ledType code(" + ledTypeCode + ") received from native code");
            }            
        }

        @Override
        public String toString()
        {
            final String s = "LED type = "      + this.ledType                               + "\n" + 
                             "LED count = "     + this.ledCount                              + "\n" + 
            		         "Operable LEDs = " + String.format("%1$08X", this.operableLEDs) + "\n";
            return (s);
        }
    }

    /**
     * Image format constants.
     */
    public static enum ImageFormat
    {
        /**
         * Gray scale image
         */
        GRAY(0),
        /**
         * 24 bit RGB color image
         */
        RGB24(1),
        /**
         * True color RGB image
         */
        RGB32(2),
        /**
         * Format not set or unknown
         */
        UNKNOWN(3);

        /* Native value for enumeration. */
        private final int code;

        ImageFormat(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static ImageFormat fromCode(int code)
        {
            for (ImageFormat t : ImageFormat.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (UNKNOWN);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }


    /**
     * Enumeration of Encryption mode
     */
    public static enum EncyptionMode
    {
    	/**
         * Random Key generated by own library.
         */
        ENCRYPTION_KEY_RANDOM(0),
        /**
         * Custom Key provided by user.
         */
        ENCRYPTION_KEY_CUSTOM(1);
           	

        /* Native value for enumeration. */
        private final int code;

    	EncyptionMode(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static EncyptionMode fromCode(int code)
        {
            for (EncyptionMode t : EncyptionMode.values())
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
     * Container to hold WSQ encode data together with meta information.
     */
    public static class WsqImage
    {
        /**
         * Byte-array holding image data.
         */
        public final byte[] buffer;
        /**
         * Image length
         */
        public final int len;

        protected WsqImage(byte[] buffer, int len)
        {
            this.buffer   = buffer;
            this.len      = len;
        }
        

    }
    /**
     * Container to hold image data together with meta information.
     */
    public static class ImageData
    {
        /*
         * Parameters for drawing rolling line on bitmap.
         */
        private static final int TAKE_ACQUISITION_COLOR     = Color.RED;
        private static final int COMPLETE_ACQUISITION_COLOR = Color.GREEN;
        private static final int TARGET_LINE_WIDTH          = 2;
		
        /*
         * Parameters for drawing quality arrow on bitmap.
         */		
        private static final int QUALITY_ARROW_COLOR  = Color.BLUE;
        private static final int QUALITY_ARROW_LENGTH = 40;
        private static final int QUALITY_ARROW_HEIGHT = 10;
        
        /**
         * Byte-array holding image data.
         */
        public final byte[] buffer;
        /**
         * Image horizontal size
         */
        public final int width;
        /**
         * Image vertical size
         */
        public final int height;
        /**
         * Horizontal image resolution (in pixels per inch)
         */
        public final double resolutionX;
        /**
         * Vertical image resolution (in pixels per inch)
         */
        public final double resolutionY;
        /**
         * Image acquisition time (in seconds).  This value contains the time taken for acquisition 
         * from device (excluding processing time).
         */
        public final double frameTime;
        /**
         * Image line pitch (in bytes). Positive values indicate top-down line order, negative  
         * values indicate bottom-up line order
         */
        public final int pitch;
        /**
         * Number of bits per pixel
         */
        public final short bitsPerPixel;
        /**
         * Image color format
         */
        public final IBScanDevice.ImageFormat format;
        /**
         * Marks image as finally processed A value of <code>false</code> disqualifies image from 
         * further processing, e.g. interim or pre-processed result images.
         */
        public final boolean isFinal;
        /**
         * Threshold of image processing.
         */
        public final int processThres;

        protected ImageData(byte[] buffer, int width, int height, double resolutionX, double resolutionY,
            double frameTime, int pitch, short bitsPerPixel, int formatCode, boolean isFinal, int processThres)
        {
            this.buffer       = buffer;
            this.width        = width;
            this.height       = height;
            this.resolutionX  = resolutionX;
            this.resolutionY  = resolutionY;
            this.frameTime    = frameTime;
            this.pitch        = pitch;
            this.bitsPerPixel = bitsPerPixel;
            this.format       = ImageFormat.fromCode(formatCode);
            this.isFinal      = isFinal;
            this.processThres = processThres;

            if (this.format == null)
            {
            	logPrintError(getMethodName() + ": unrecognized format code(" + formatCode + ") received from native code");
            }            
        }

        /**
         * Create image from the image data.
         * 
         * @return image if successful; otherwise <code>null</code>
         */
        public Bitmap toBitmap()
        {
        	final Bitmap bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        	
    		if (bitmap != null)
    		{
            	final byte[] imageBuffer = new byte[this.width * this.height * 4];
            	/* 
            	 * The image in the buffer is flipped vertically from what the Bitmap class expects; 
            	 * we will flip it to compensate while moving it into the buffer. 
            	 */
        		for (int y = 0; y < this.height; y++) 
        		{
        			for (int x = 0; x < this.width; x++) 
        			{
        				imageBuffer[(y * this.width + x) * 4] = 
        						imageBuffer[(y * this.width + x) * 4 + 1] = 
        								imageBuffer[(y * this.width + x) * 4 + 2] = 
        										this.buffer[(this.height - y - 1) * this.width + x];
        				imageBuffer[(y * this.width + x) * 4 + 3] = (byte)255;
        			}
        		}        	
        		bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(imageBuffer));
    		}
    		return (bitmap);
        }

        /**
         * Create scaled image from the image data.
         * 
         * @param dstWidth   width of scaled image
         * @param dstHeight  height of scaled image
         * @return           image if successful; otherwise <code>null</code>
         */
        public Bitmap toBitmapScaled(int dstWidth, int dstHeight)
        {
        	if ((dstWidth <= 0) || (dstHeight <= 0))
        	{
        		throw (new IllegalArgumentException());
        	}
        	
        	Bitmap bitmapScaled = null;
        	
        	final Bitmap bitmap = this.toBitmap();        	
        	if (bitmap != null)
        	{
        		bitmapScaled = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
        	}
        	
        	return (bitmapScaled);
        }
        
        /**
         * Create scaled image from the image data with rolling line.
         * 
         * @param dstWidth      width of scaled image
         * @param dstHeight     height of scaled image
         * @param rollingState  state of the rolling acquisition
         * @param rollingLineX  x-coordinate of rolling line
         * @return              image if successful; otherwise <code>null</code>
         */
        public Bitmap toBitmapScaled(int dstWidth, int dstHeight, RollingState rollingState, int rollingLineX)
        {
        	if ((dstWidth <= 0) || (dstHeight <= 0))
        	{
        		throw (new IllegalArgumentException());
        	}
        	if (rollingState == null)
        	{
        		throw (new IllegalArgumentException());
        	}
        	
        	Bitmap bitmapScaled = this.toBitmapScaled(dstWidth, dstHeight);
        	
        	if (bitmapScaled != null)
        	{
        		drawRollingLine(bitmapScaled, dstWidth, dstHeight, rollingState, rollingLineX, TARGET_LINE_WIDTH);        		
        	}
        	
        	return (bitmapScaled);
        }
        
        /**
         * Create scaled image from the image data with finger quality state indicator.
         * 
         * @param dstWidth   width of scaled image
         * @param dstHeight  height of scaled image
         * @param top        indicates whether invalid arrow should be drawn on top edge
         * @param left       indicates whether invalid arrow should be drawn on left edge
         * @param right      indicates whether invalid arrow should be drawn on right edge
         * @return           image if successful; otherwise <code>null</code>
         */
        public Bitmap toBitmapScaled(int dstWidth, int dstHeight, boolean top, boolean left, 
        		boolean right)
        {
        	if ((dstWidth <= 0) || (dstHeight <= 0))
        	{
        		throw (new IllegalArgumentException());
        	}
        	
        	Bitmap bitmapScaled = this.toBitmapScaled(dstWidth, dstHeight);
        	
        	if (bitmapScaled != null)
        	{
        		drawQualityArrow(bitmapScaled, dstWidth, dstHeight, top, left, right);
         	}
        	
        	return (bitmapScaled);
        }
        
        /**
         * Create scaled image from the image data with rolling line.
         * 
         * @param dstWidth      width of scaled image
         * @param dstHeight     height of scaled image
         * @param rollingState  state of the rolling acquisition
         * @param rollingLineX  x-coordinate of rolling line
         * @param top           indicates whether invalid arrow should be drawn on top edge
         * @param left          indicates whether invalid arrow should be drawn on left edge
         * @param right         indicates whether invalid arrow should be drawn on right edge
         * @return              image if successful; otherwise <code>null</code>
         */
        public Bitmap toBitmapScaled(int dstWidth, int dstHeight, RollingState rollingState, int rollingLineX, 
        		boolean top, boolean left, boolean right)
        {
        	if ((dstWidth <= 0) || (dstHeight <= 0))
        	{
        		throw (new IllegalArgumentException());
        	}
        	if (rollingState == null)
        	{
        		throw (new IllegalArgumentException());
        	}
        	
        	Bitmap bitmapScaled = this.toBitmapScaled(dstWidth, dstHeight);
        	
        	if (bitmapScaled != null)
        	{
        		drawRollingLine(bitmapScaled, dstWidth, dstHeight, rollingState, rollingLineX, TARGET_LINE_WIDTH);  
        		drawQualityArrow(bitmapScaled, dstWidth, dstHeight, top, left, right);
        	}
        	
        	return (bitmapScaled);
        }
        
        /**
         * Create scaled image from the image data with rolling line.
         * 
         * @param dstWidth          width of scaled image
         * @param dstHeight         height of scaled image
         * @param rollingState      state of the rolling acquisition
         * @param rollingLineX      x-coordinate of rolling line
         * @param rollingLineWidth  thickness of rolling line
         * @param top               indicates whether invalid arrow should be drawn on top edge
         * @param left              indicates whether invalid arrow should be drawn on left edge
         * @param right             indicates whether invalid arrow should be drawn on right edge
         * @return                  image if successful; otherwise <code>null</code>
         */
        public Bitmap toBitmapScaled(int dstWidth, int dstHeight, RollingState rollingState, int rollingLineX, 
        		int rollingLineWidth, boolean top, boolean left, boolean right)
        {
        	if ((dstWidth <= 0) || (dstHeight <= 0))
        	{
        		throw (new IllegalArgumentException());
        	}
        	if (rollingState == null)
        	{
        		throw (new IllegalArgumentException());
        	}
        	if ((rollingLineWidth < 1) || (rollingLineWidth > 6))
        	{
        		throw (new IllegalArgumentException());
        	}
        	
        	Bitmap bitmapScaled = this.toBitmapScaled(dstWidth, dstHeight);
        	
        	if (bitmapScaled != null)
        	{
        		drawRollingLine(bitmapScaled, dstWidth, dstHeight, rollingState, rollingLineX, rollingLineWidth);  
        		drawQualityArrow(bitmapScaled, dstWidth, dstHeight, top, left, right);
        	}
        	
        	return (bitmapScaled);
        }
        
       /**
         * Save image to file.
         * 
         * @param output      the file to which the image will be saved
         * @param fileFormat  the format of the file (JPEG, PNG, WEBP)
         * @return            <code>true</code> if image was saved; false otherwise
         * @throws            IOException
         */
        public boolean saveToFile(File output, String fileFormat) throws IOException
        {
            /* Check for null arguments. */
            if ((output == null) || (fileFormat == null))
            {
                throw (new IllegalArgumentException());
            }

            boolean  ok = false;

            /* Check whether format is supported. */
        	Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.valueOf(fileFormat.toUpperCase());

            if (compressFormat != null)
            {
            	Bitmap bitmap = this.toBitmap();
            	
            	if (bitmap != null)
            	{
        			FileOutputStream stream = new FileOutputStream(output);

                	ok = bitmap.compress(compressFormat, 100, stream);
            	}
            }

            return (ok);
        }
        
        /*
         * Draw rolling line on bitmap.
         */
    	private void drawRollingLine(Bitmap bitmapScaled, int dstWidth, int dstHeight, 
    			RollingState rollingState, int rollingLineX, int rollingLineWidth)
    	{
    		if ((rollingState.equals(RollingState.TAKE_ACQUISITION) || rollingState.equals(RollingState.COMPLETE_ACQUISITION)) && (rollingLineX >= 0))
    		{
				int targetLineX     = (rollingLineX * dstWidth) / this.width;
				int targetLineColor = (rollingState.equals(RollingState.TAKE_ACQUISITION)) ? TAKE_ACQUISITION_COLOR : COMPLETE_ACQUISITION_COLOR;
				
				for (int y = 0; y < dstHeight; y++) 
				{
					for (int x = targetLineX - (rollingLineWidth / 2); x < targetLineX - (rollingLineWidth / 2) + rollingLineWidth; x++) 
					{
						if ((x >= 0) && (x < dstWidth))
						{
							bitmapScaled.setPixel(x, y, targetLineColor);
						}
					}
				}
    		}
    	}
    	
        /*
         * Draw quality arrow on bitmap.
         */
        public void drawQualityArrow(Bitmap bitmapScaled, int dstWidth, int dstHeight, 
        		boolean top, boolean left, boolean right)
        {
       		if (left)
       		{
				for (int x = 0; x < QUALITY_ARROW_LENGTH; x++)
				{
					for (int y = (dstHeight / 2) - (QUALITY_ARROW_HEIGHT / 2); y < (dstHeight / 2) + (QUALITY_ARROW_HEIGHT / 2); y++)
					{
						if ((y > 0) && (y < dstHeight))
						{
							bitmapScaled.setPixel(x,  y, QUALITY_ARROW_COLOR);
						}
					}
				}
       		}
       		
       		if (right)
       		{
				for (int x = dstWidth - QUALITY_ARROW_LENGTH - 1; x < dstWidth; x++)
				{
					for (int y = (dstHeight / 2) - (QUALITY_ARROW_HEIGHT / 2); y < (dstHeight / 2) + (QUALITY_ARROW_HEIGHT / 2); y++)
					{
						if ((y > 0) && (y < dstHeight))
						{
							bitmapScaled.setPixel(x,  y, QUALITY_ARROW_COLOR);
						}
					}
				}
       		}
       		
       		if (top)
       		{
				for (int y = 0; y < QUALITY_ARROW_LENGTH; y++)
				{
					for (int x = (dstWidth / 2) - (QUALITY_ARROW_HEIGHT / 2); x < (dstWidth / 2) + (QUALITY_ARROW_HEIGHT / 2); x++)
					{
						if ((x > 0) && (x < dstWidth))
						{
							bitmapScaled.setPixel(x,  y, QUALITY_ARROW_COLOR);
						}
					}
				}
    		}      	
        }        
    }

    /**
     * Finger count state definitions.
     */
    public static enum FingerCountState
    {
        /**
		 * Expected number of fingers on platen.
		 */
        FINGER_COUNT_OK(0),
        /**
		 * Too many fingers on platen.
		 */
        TOO_MANY_FINGERS(1),
        /**
		 * Too few fingers on platen.
		 */
        TOO_FEW_FINGERS(2),
        /**
		 * No fingers on platen.
		 */
        NON_FINGER(3);

        /* Native value for enumeration. */
        private final int code;

        FingerCountState(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static FingerCountState fromCode(int code)
        {
            for (FingerCountState t : FingerCountState.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Finger quality state definitions.
     */
    public static enum FingerQualityState
    {
        /**
		 * 
		 */
        FINGER_NOT_PRESENT(0),
        /**
		 * 
		 */
        GOOD(1),
        /**
		 * 
		 */
        FAIR(2),
        /**
		 * 
		 */
        POOR(3),
        /**
         * Finger position is not valid on top side. 
         */
        INVALID_AREA_TOP(4),
        /**
         * Finger position is not valid on left side.
         */
        INVALID_AREA_LEFT(5),
        /** 
         * Finger position is not valid on right side.
         */
        INVALID_AREA_RIGHT(6),
        /** 
         * Finger position is not valid on bottom side.
         */
        INVALID_AREA_BOTTOM(7);

        /* Native value for enumeration. */
        private final int code;

        FingerQualityState(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static FingerQualityState fromCode(int code)
        {
            for (FingerQualityState t : FingerQualityState.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        // Get native value for Java object.
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Platen state definitions.
     */
    public static enum PlatenState
    {
        /**
		 * 
		 */
        CLEARD(0),
        /**
		 * 
		 */
        HAS_FINGERS(1);

        /* Native value for enumeration. */
        private final int code;

        PlatenState(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static PlatenState fromCode(int code)
        {
            for (PlatenState t : PlatenState.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Rolling state definitions.
     */
    public static enum RollingState
    {
        /**
		 * Acquisition has not begun.
		 */
        NOT_PRESENT(0),
        /**
		 * Acquisition of scan for roll is occurring. 
		 */
        TAKE_ACQUISITION(1),
        /**
	     * Acquisition of scan for roll has completed.
	     */
        COMPLETE_ACQUISITION(2),
        /**
	     * A result image is already available.
	     */
        RESULT_IMAGE(3);

        /* Native value for enumeration. */
        private final int code;

        RollingState(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static RollingState fromCode(int code)
        {
            for (RollingState t : RollingState.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

    /**
     * Rolling state data.
     */
    public static class RollingData
    {
        /**
         * The rolling state.
         */
        public final RollingState rollingState;
        /**
		 * The horizontal position of the vertical rolling line.
		 */
        public final int rollingLineX;

        protected RollingData(int rollingStateCode, int rollingLineX)
        {
            this.rollingState = RollingState.fromCode(rollingStateCode);
            this.rollingLineX = rollingLineX;

            if (this.rollingState == null)
            {
            	logPrintError(getMethodName() + ": unrecognized rollingState code(" + rollingStateCode + ") received from native code");
            }            
         }
    }
    /**
     * Enumeration of Beeper types.
     */
    public static enum BeeperType
    {
        /**
		 * No Beeper field.
		 */
        BEEPER_TYPE_NONE(0),
        /**
		 * Monotone type. 
		 */
       BEEPER_TYPE_MONOTONE(1);

        /* Native value for enumeration. */
        private final int code;

        BeeperType(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static BeeperType fromCode(int code)
        {
            for (BeeperType t : BeeperType.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }
      /**
     * Enumeration of the beep pattern.
     */
    public static enum BeepPattern
    {
        /**
		 * Generic type.
		 */
     	BEEP_PATTERN_GENERIC(0),
        /**
		 * Repeat type. 
		 */
        BEEP_PATTERN_REPEAT(1);

        /* Native value for enumeration. */
        private final int code;

        BeepPattern(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static BeepPattern fromCode(int code)
        {
            for (BeepPattern t : BeepPattern.values())
            {
                if (t.code == code)
                {
                    return (t);
                }
            }
            return (null);
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }
    /**
     * Segment position.
     */
    public static class SegmentPosition
    {
        /**
         * The x-coordinate of the first vertex.
         */
        public final int x1;
        /**
		 * The y-coordinate of the first vertex.
		 */
        public final int y1;
        /**
         * The x-coordinate of the second vertex.
         */
        public final int x2;
        /**
		 * The y-coordinate of the second vertex.
		 */
        public final int y2;
        /**
         * The x-coordinate of the third vertex.
         */
        public final int x3;
        /**
		 * The y-coordinate of the third vertex.
		 */
        public final int y3;
        /**
         * The x-coordinate of the fourth vertex.
         */
        public final int x4;
        /**
		 * The y-coordinate of the fourth vertex.
		 */
        public final int y4;

        protected SegmentPosition(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4)
        {
        	this.x1 = x1;
        	this.y1 = y1;
        	this.x2 = x2;
        	this.y2 = y2;
        	this.x3 = x3;
        	this.y3 = y3;
        	this.x4 = x4;
        	this.y4 = y4;
        }
    }

    /**
     * Event types.
     * 
     * This is an enumeration of the events that are triggered for individual devices. 
     * See <code>enableEvent()</code>.
     */
    public static enum EventType
    {
        /**
         * Communication with a device is interrupted.
         */
        COMMUNICATION_BROKEN(1),
        /**
         * A new preview image is available from a device.
         */
        PREVIEW_IMAGE_AVAILABLE(2),
        /**
         * Rolled print acquisition when rolling should begin.
         */
        ACQUISITION_BEGUN(3),
        /**
         * Rolled print acquisition when rolling completes.
         */
        ACQUISITION_COMPLETED(4),
        /**
         * Result image is available for a capture.
         */
        RESULT_IMAGE_AVAILABLE(5),
        /**
         * A finger quality changes.
         */
        FINGER_QUALITY_CHANGED(6),
        /**
         * The finger count changes.
         */
        FINGER_COUNT_CHANGED(7),
        /**
         * The platen was not clear when capture started or has since become clear.
         */
        PLATEN_STATE_CHANGED(9),
        /**
         * A warning message is generated.
         */
        WARNING_RECEIVED(11),
        /**
         * Result image is available for a capture (with extended information).
         */
        RESULT_IMAGE_EXTENDED_AVAILABLE(12),
        /**
         * Callback when key buttons are pressed.
         */
	    KEYBUTTON(13);

        /* Native value for enumeration. */
        private final int code;
        
        EventType(int code)
        {
            this.code = code;
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }

 	/**
    ****************************************************************************************************
    * IBSU_CombineImageWhichHand
    *
    * DESCRIPTION:
    *     Enumeration of hand to use for combining two images into one.
    ****************************************************************************************************
    */
    public static enum CombineImageWhichHand
    {
        /**
         * LEFT HAND IMAGE.
         */
    	COMBINE_IMAGE_LEFT_HAND(0),
        /**
         * RIGHT HAND IMAGE.
         */
    	COMBINE_IMAGE_RIGHT_HAND(1);

        /* Native value for enumeration. */
        private final int code;
        
        CombineImageWhichHand(int code)
        {
            this.code = code;
        }

        /* Get native value for Java object. */
        protected int toCode()
        {
            return (this.code);
        }
    }
    
    /* *********************************************************************************************
     * (OBJECT) PUBLIC INTERFACE
     ******************************************************************************************** */

    /**
     * Release a device.
     * 
     * @throws IBScanException
     */
    public void close() throws IBScanException
    {
        final NativeError error = new NativeError();

        closeNative(error);
        handleError(error); /* throws exception if necessary */

        this.m_isOpened = false;
    }

    /**
     * Check if a particular device is open/initialized.
     * 
     * @return true if the device is open; false otherwise
     */
    public boolean isOpened()
    {
        final boolean opened = this.m_isOpened;
        return (opened);
    }

    /**
     * Enable or disable a low-level event for this device.  When a device is opened, all events are
     * enabled.  Disabling an event will effectively disable the associated method in the configured 
     * <code>IBScanDeviceListener</code>.
     * 
     * @param event   event to enable or disable
     * @param enable  <code>true</code> to enable event; <code>false</code> to disable event
     * @throws IBScanException
     */
    public void enableEvent(final EventType event, final boolean enable) throws IBScanException
    {
        if (event == null)
        {
        	logPrintWarning(getMethodName() + ": received null event");
            throw (new IllegalArgumentException("Received null event"));
        }

        final NativeError error = new NativeError();
    	
    	enableEventNative(event.toCode(), enable, error);
    	handleError(error); /* throws exception if necessary */
    }
    
    /**
     * Set a property value of the device.
     * 
     * @param propertyId     the ID of the property to set
     * @param propertyValue  the value to set for the property, as a string
     * @throws               IBScanException
     */
    public void setProperty(final PropertyId propertyId, final String propertyValue) throws IBScanException
    {
        if (propertyId == null)
        {
        	logPrintWarning(getMethodName() + ": received null propertyId");
            throw (new IllegalArgumentException("Received null propertyId"));
        }
        else if (propertyValue == null)
        {
        	logPrintWarning(getMethodName() + ": received null propertyValue");
            throw (new IllegalArgumentException("Received null propertyValue"));
        }
        
        final NativeError error = new NativeError();

        setPropertyNative(propertyId.toCode(), propertyValue, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Set a reserved property value of the device.
     * 
     * @param reservedKey    the reserved key to set for the property, as a string
     * @param propertyId     the ID of the property to set
     * @param propertyValue  the value to set for the property, as a string
     * @throws               IBScanException
     */
    public void setPropertyReserved(final String reservedKey, final PropertyId propertyId, final String propertyValue) throws IBScanException
    {
        if (reservedKey == null)
        {
        	logPrintWarning(getMethodName() + ": received null reservedKey");
            throw (new IllegalArgumentException("Received null reservedKey"));
        }
        else if (propertyId == null)
        {
        	logPrintWarning(getMethodName() + ": received null propertyId");
            throw (new IllegalArgumentException("Received null propertyId"));
        }
        else if (propertyValue == null)
        {
        	logPrintWarning(getMethodName() + ": received null propertyValue");
            throw (new IllegalArgumentException("Received null propertyValue"));
        }
        
        final NativeError error = new NativeError();

        setPropertyReservedNative(reservedKey, propertyId.toCode(), propertyValue, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Retrieves a property value from the device.
     * 
     * @param propertyId  the ID of the property to get
     * @return            the value of the property, as a string
     * @throws            IBScanException
     */
    public String getProperty(final PropertyId propertyId) throws IBScanException
    {
        if (propertyId == null)
        {
        	logPrintWarning(getMethodName() + ": received null propertyId");
            throw (new IllegalArgumentException("Received null propertyId"));
        }
        
        final NativeError error         = new NativeError();
        final String      propertyValue = getPropertyNative(propertyId.toCode(), error);
        handleError(error); /* throws exception if necessary */

        return (propertyValue);
    }

    /**
     * Check if requested capture mode is supported by the device.
     * 
     * @param imageType        the image type of the mode to check
     * @param imageResolution  the image resolution of the mode to check
     * @return                 <code>true</code> if the mode is supported; <code>false</code> otherwise
     * @throws                 IBScanException
     */
    public boolean isCaptureAvailable(final ImageType imageType, final ImageResolution imageResolution)
        throws IBScanException
    {
        if (imageType == null)
        {
        	logPrintWarning(getMethodName() + ": received null imageType");
            throw (new IllegalArgumentException("Received null imageType"));
        }
        else if (imageResolution == null)
        {
        	logPrintWarning(getMethodName() + ": received null imageResolution");
            throw (new IllegalArgumentException("Received null imageResolution"));
        }
        
        final NativeError error       = new NativeError();
        final boolean     isAvailable = isCaptureAvailableNative(imageType.toCode(), imageResolution.toCode(), error);
        handleError(error); /* throws exception if necessary */

        return (isAvailable);
    }

    /**
     * Start image acquisition for the device.  This function will return immediately, but this 
     * device's <code>IBScanDeviceListener</code> will inform the application about scanning progress 
     * with the methods <code>deviceFingerCountChanged()</code>, <code>deviceFingerQualityChanged</code>,
     * <code>devicePlatenStateChanged()</code>, and <code>deviceImagePreviewAvailable()</code>.  
     * When a quality scan with the correct number of fingers is available or <code>captureImageManually()</code> 
     * prematurely aborts the scan, the listener's <code>deviceImageResultAvailable()</code>
     * method will supply a final scan to the application.
     * 
     * @param imageType        the type of the image to acquire
     * @param imageResolution  the resolution of the image to acquire
     * @param captureOptions   a bit-mapped value indicating capture options, consisting of zero or
     *                         more options OR'd together
     *                         <p>
     *                         <ul>
     *                             <li><code>OPTION_AUTO_CAPTURE</code>  auto capture
     *                             <li><code>OPTION_AUTO_CONTRAST</code> auto contrast
     *                             <li><code>OPTION_IGNORE_FINGER_COUNT</code> ignore finger count
     *                         </ul>
     * @throws                 IBScanException
     */
    public void beginCaptureImage(final ImageType imageType, final ImageResolution imageResolution,
    		final int captureOptions) throws IBScanException
    {
        if (imageType == null)
        {
        	logPrintWarning(getMethodName() + ": received null imageType");
            throw (new IllegalArgumentException("Received null imageType"));
        }
        else if (imageResolution == null)
        {
        	logPrintWarning(getMethodName() + ": received null imageResolution");
            throw (new IllegalArgumentException("Received null imageResolution"));
        }
        
        final NativeError error = new NativeError();

        beginCaptureImageNative(imageType.toCode(), imageResolution.toCode(), captureOptions, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Abort image acquisition on the device.  After <code>beginCaptureImage()</code> is called, 
     * image capture can be prematurely terminated with this function.
     * 
     * @throws IBScanException
     */
    public void cancelCaptureImage() throws IBScanException
    {
        final NativeError error = new NativeError();

        cancelCaptureImageNative(error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Check if capture is active on the device.
     * 
     * @return <code>true</code> if capture is active; <code>false</code> otherwise
     * @throws IBScanException
     */
    public boolean isCaptureActive() throws IBScanException
    {
        final NativeError error    = new NativeError();
        final boolean     isActive = isCaptureActiveNative(error);
        handleError(error); /* throws exception if necessary */

        return (isActive);
    }

    /**
     * Capture current scanner image as result image.  After <code>beginCaptureImage()</code> 
     * is called, scanning typically continues until a quality scan with the correct number of 
     * fingers is available or an error occurs.  This function will prematurely terminate the
     * process and return the current scanner image to the application with the <code>IBScanDeviceListener</code>'s
     * <code>deviceImageResultAvailable()</code> method.
     * 
     * @throws IBScanException
     */
    public void captureImageManually() throws IBScanException
    {
        final NativeError error = new NativeError();

        captureImageManuallyNative(error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Get the contrast value for the device.
     * 
     * @return contrast value between <code>MIN_CONTRAST_VALUE</code> and <code>MAX_CONTRAST_VALUE</code>,
     *         inclusive
     * @throws IBScanException
     */
    public int getContrast() throws IBScanException
    {
        final NativeError error         = new NativeError();
        final int         contrastValue = getContrastNative(error);
        handleError(error); /* throws exception if necessary */

        return (contrastValue);
    }

    /**
     * Set the contrast value for the device.
     * 
     * @param contrastValue  contrast value between <code>MIN_CONTRAST_VALUE</code> and 
     *                       <code>MAX_CONTRAST_VALUE</code>, inclusive, to set
     * @throws               IBScanException
     */
    public void setContrast(final int contrastValue) throws IBScanException
    {
        final NativeError error = new NativeError();

        setContrastNative(contrastValue, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Set the Light-Emitting (LE) film operation mode (On, Off, or Auto) the device.
     * 
     * @param  leOperationMode  light-emitting film operation mode to set
     * @throws IBScanException
     */
    public void setLEOperationMode(final LEOperationMode leOperationMode) throws IBScanException
    {
        if (leOperationMode == null)
        {
        	logPrintWarning(getMethodName() + ": received null leOperationMode");
            throw (new IllegalArgumentException("Received null leOperationMode"));
        }
        
        final NativeError error = new NativeError();

        setLEOperationModeNative(leOperationMode.toCode(), error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Get the Light-Emitting (LE) film operation mode (On, Off, or Auto) for the device.
     * 
     * @return light-emitting film operation mode
     * @throws IBScanException
     */
    public LEOperationMode getLEOperationMode() throws IBScanException
    {
        final NativeError error               = new NativeError();
        final int         leOperationModeCode = getLEOperationModeNative(error);
        handleError(error); /* throws exception if necessary */
        
		final LEOperationMode leOperationMode = LEOperationMode.fromCode(leOperationModeCode);
        
        /* Check for library or JNI interface error. */
        if (leOperationMode == null) 
        {
            logPrintError(getMethodName() + ": unrecognized leOperationMode code (" + leOperationModeCode + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }
        
        return (leOperationMode);
    }

    /**
     * Determines if one or more fingers is currently touching the detector.  
     * 
     * @return <code>true</code> if a finger is on the detector; <code>false</code> otherwise
     * @throws IBScanException
     */
    public boolean isFingerTouching() throws IBScanException
    {
        final NativeError error     = new NativeError();
        final boolean     isTouched = isFingerTouchingNative(error);
        handleError(error); /* throws exception if necessary */

        return (isTouched);
    }

    /**
     * Get a description of the operable status LEDs of the device.
     * 
     * @return a description of the status LEDs
     * @throws IBScanException
     */
    public LedState getOperableLEDs() throws IBScanException
    {
        final NativeError error    = new NativeError();
        final LedState    ledState = getOperableLEDsNative(error);
        handleError(error); /* throws exception if necessary */

        // Check for library or JNI interface error.
        if ((ledState == null) || (ledState.ledType == null))
        {
            logPrintError(getMethodName() + ": null or invalid ledState returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }
        
        return (ledState);
    }

    /**
     * Get the active status LEDs of the device.
     * 
     * @return the bit-mapped status of the LEDs; set bits indicate "on" LEDs
     * @throws IBScanException
     */
    public long getLEDs() throws IBScanException
    {
        final NativeError error      = new NativeError();
        final long        activeLEDs = getLEDsNative(error);
        handleError(error); /* throws exception if necessary */

        return (activeLEDs);
    }

    /**
     * Set the active status of LEDs of the device.
     * 
     * @param activeLEDs  the bit-mapped status of the LEDs; set bits indicate LEDs to
     *                    turn on, clear bits indicate LEDs to turn off
     * @throws            IBScanException
     */
    public void setLEDs(final long activeLEDs) throws IBScanException
    {
        final NativeError error = new NativeError();

        setLEDsNative(activeLEDs, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
     * Capture an image from scanner.
     * 
     * @return an array containing information about captured image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>ImageData image</code> - image data of preview image or result image 
     *         <li><code>ImageType imageType</code> - image type 
     *         <li><code>ImageData[] splitImageArray</code> - finger array split from the result image
     *         <li><code>FingerCountState fingerCountState</code> - finger count state
     *         <li><code>FingerQualityState[] qualityArray</code> - finger quality states
     *         </ul>
     * @throws IBScanException
     */
    public Object[] captureImage() throws IBScanException
    {
        final NativeError error   = new NativeError();
        final Object[]    returns = captureImageNative(error);
        handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((returns == null) || (returns.length != 5) || (returns[0] == null) || 
        		(returns[1] == null) || (returns[2] == null) || (returns[3] == null) || (returns[4] == null))
        {
            logPrintError(getMethodName() + ": null or invalid image information returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        /* Convert return values to Java types. */
        Object[]             returnsJ            = new Object[5];
        FingerQualityState[] fingerQualities;

		int[]                fingerQualityCodes = (int[]) returns[4];
        ImageType            imageType          = ImageType.fromCode(((Integer)returns[1]).intValue());
        FingerCountState     fingerCountState   = FingerCountState.fromCode(((Integer) returns[3]).intValue());
        boolean              ok                 = true;
        
        returnsJ[0] = returns[0];  /* image */
        returnsJ[1] = imageType;
        returnsJ[2] = returns[2];  /* split image array */
        returnsJ[3] = fingerCountState;

        fingerQualities = new FingerQualityState[fingerQualityCodes.length];
        for (int i = 0; i < fingerQualityCodes.length; i++)
        {
            fingerQualities[i] = FingerQualityState.fromCode(fingerQualityCodes[i]);
            if (fingerQualities[i] == null)
            {
                logPrintError(getMethodName() + ": unrecognized fingerQuality code (" + fingerQualityCodes[i] + ") returned from native code");
                
                ok = false;
                break;
            }
        }
         
        returnsJ[4] = fingerQualities;
        
        /* Check for library or JNI interface error. */
        if (fingerCountState == null)
        {
            logPrintError(getMethodName() + ": unrecognized fingerCountState code (" + ((Integer)returns[3]).intValue() + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);       
        } 
        else if (imageType == null)
        {
            logPrintError(getMethodName() + ": unrecognized imageType code (" + ((Integer)returns[1]).intValue() + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);       
        }
        else if (!ok)
        {
            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        return (returnsJ);
    }

    /**
     * Capture an image from scanner, returning extended information.
     * 
     * @return an array containing information about captured image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>IBScanException imageStatus</code> - status from result image acquisition
     *         <li><code>ImageData image</code> - image data of preview image or result image 
     *         <li><code>ImageType imageType</code> - image type 
     *         <li><code>Integer detectedFingerCount</code> - detected finger count
     *         <li><code>ImageData[] segmentImageArray</code> - finger array split from the result image
     *         <li><code>SegmentPosition[] segmentPositionArray</code> - position data for individual fingers split from result image
     *         <li><code>FingerCountState fingerCountState</code> - finger count state
     *         <li><code>FingerQualityState[] qualityArray</code> - finger quality states
     *         </ul>
     * @throws IBScanException
     */
    public Object[] captureImageExtended() throws IBScanException
    {
        final NativeError error   = new NativeError();
        final Object[]    returns = captureImageExtendedNative(error);
        handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((returns == null) || (returns.length != 8) || (returns[0] == null) || 
        		(returns[1] == null) || (returns[2] == null) || (returns[3] == null) || (returns[4] == null) ||
        		(returns[5] == null) || (returns[6] == null) || (returns[7] == null))
        {
            logPrintError(getMethodName() + ": null or invalid image information returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        /* Convert return values to Java types. */
        Object[]             returnsJ            = new Object[8];
        FingerQualityState[] fingerQualities;

		int imageStatusCode = ((Integer)returns[0]).intValue();
        IBScanException.Type imageStatusType    = IBScanException.Type.fromCode(((Integer)returns[0]).intValue());
		IBScanException      imageStatus        = (imageStatusType == null) ? null : (new IBScanException(imageStatusType));
		int[]                fingerQualityCodes = (int[])returns[7];
        ImageType            imageType          = ImageType.fromCode(((Integer)returns[2]).intValue());
        FingerCountState     fingerCountState   = FingerCountState.fromCode(((Integer)returns[6]).intValue());
        boolean              ok                 = true;
        
        returnsJ[0] = imageStatus;
        returnsJ[1] = returns[1]; /* image */
        returnsJ[2] = imageType;
        returnsJ[3] = returns[3]; /* detected finger count */
        returnsJ[4] = returns[4]; /* segment image array */
        returnsJ[5] = returns[5]; /* segment position array */
        returnsJ[6] = fingerCountState;

        fingerQualities = new FingerQualityState[fingerQualityCodes.length];
        for (int i = 0; i < fingerQualityCodes.length; i++)
        {
            fingerQualities[i] = FingerQualityState.fromCode(fingerQualityCodes[i]);
            if (fingerQualities[i] == null)
            {
                logPrintError(getMethodName() + ": unrecognized fingerQuality code (" + fingerQualityCodes[i] + ") returned from native code");
                
                ok = false;
                break;
            }
        }
         
        returnsJ[7] = fingerQualities;
        
        /* Check for library or JNI interface error. */
        if (fingerCountState == null)
        {
            logPrintError(getMethodName() + ": unrecognized fingerCountState code (" + ((Integer)returns[6]).intValue() + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);       
        } 
        else if (imageType == null)
        {
            logPrintError(getMethodName() + ": unrecognized imageType code (" + ((Integer)returns[2]).intValue() + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);       
        }
        else if (imageStatus == null && imageStatusCode !=0)
        {
            logPrintError(getMethodName() + ": unrecognized imageStatus code (" + ((Integer)returns[0]).intValue() + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);       
        }
        else if (!ok)
        {
            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        return (returnsJ);
    }

    /**
     * Get information about platen state when capture was started.
     * 
     * @return information about platen state
     * @throws IBScanException
     */
    public PlatenState getPlatenStateAtCapture() throws IBScanException
    {
        final NativeError error           = new NativeError();
        final int         platenStateCode = getPlatenStateAtCaptureNative(error);
        handleError(error); /* throws exception if necessary */
        
		final PlatenState platenState = PlatenState.fromCode(platenStateCode);

        /* Check for library or JNI interface error. */
        if (platenState == null)
        {
            logPrintError(getMethodName() + ": unrecognized platenState code (" + platenStateCode + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        return (platenState);
    }

    /**
     * Get information about rolling status.
     * 
     * @return information about rolling status
     * @throws IBScanException
     */
    public RollingData getRollingInfo() throws IBScanException
    {
        final NativeError error       = new NativeError();
        final RollingData rollingData = getRollingInfoNative(error);
        handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((rollingData == null) || (rollingData.rollingState == null))
        {
            logPrintError(getMethodName() + ": null or invalid rollingData returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        return (rollingData);
    }
    
    /**
     * Calculate NFIQ score for image.
     * 
     * @param image   image for which to calculate NFIQ score
     * @return        NFIQ score, between 1 and 5, inclusive
     * @throws        IBScanException
     */
    public int calculateNfiqScore(ImageData image) throws IBScanException
    {
        if (image == null)
        {
        	logPrintWarning(getMethodName() + ": received null image");
            throw (new IllegalArgumentException());
        }
        
        final NativeError error     = new NativeError();
        final int         nfiqScore = calculateNfiqScoreNative(image, error);
        handleError(error); /* throws exception if necessary */ 
        
        return (nfiqScore);
    }
    
    /**
     * Get extendec result image information.
     * 
     * @param fingerPosition  finger position of finger(s) captured
     * @return an array containing information about captured image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>IBScanMatcher.ImageDataExt image</code> - image data of preview image or result image 
     *         <li><code>IBScanMatcher.ImageDataExt[] splitImageArray</code> - finger array split from the result image
     *         </ul>
     * @throws IBScanException
     */
    public Object[] getResultImageExt(IBCommon.FingerPosition fingerPosition) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final Object[]    returns = getResultImageExtNative(fingerPosition.toCode(), error);
        handleError(error); /* throws exception if necessary */
        
        return (returns);
    }
	
	/**
     * Convert Image Data to Standard Format for write file.
     * (ISO 19794-2:2005, ISO 19794-4:2005, ISO 19794-2:2011, ISO 19794-4:2011, ANSI/INCITS 378:2004, ANSI/INCITS 381:2004)
     * 
     * @param image       input image data for roll to slap comparison.
     * @param imageCount  Number of image.
     * @param imageFormat Image compression format of output data
     * @param STDformat   ISO format of output data
     * @return output data will receive as IBSM_StandardFormatData
     * @throws IBScanException
     */
    public Object ConvertImageToISOANSI(ImageDataExt[] image, int imageCount, IBCommon.ImageFormat imageFormat,StandardFormat STDformat) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final Object      returns = ConvertImageToISOANSINative(image, imageCount, imageFormat.toCode(),STDformat.toCode(), error);
        handleError(error); /* throws exception if necessary */
        
        return (returns);
    }

    /**
     * WSQ compresses grayscale fingerprint image save to File
     * 
     * @param image  Pointer to image buffer.
     * @throws IBScanException
     */
    public int wsqEncodeToFile(String filename,byte[] image, int width, int height, int pitch,
    		int bitPerPixel, int pixelPerInch, double bitRate, String commentText) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final int    wsqfilepath = wsqEncodeToFileNative(filename,image, width, height, pitch, bitPerPixel, pixelPerInch, bitRate, commentText, error);
        handleError(error); /* throws exception if necessary */
        
        return  (wsqfilepath);
    }
    /**
     * WSQ compresses grayscale fingerprint image save to Memory
     * 
     * @param image				Pointer to image buffer.
     * @param width				Width of original image (in pixels).
     * @param height 			Height of original image (in pixels).
     * @param pitch				Image line pitch (in bytes).
     * @param bitsPerPixel 		Bits per pixel of original image.
     * @param pixelPerInch		Pixel per inch of original image.
     * @param bitRate           Determines the amount of lossy compression.
     * 							Suggested settings:
     *							bitRate = 2.25 yields around 5:1 compression
     *							bitRate = 0.75 yields around 15:1 compression
     * @param commentText		Comment to write compressed data.
     * @return an array containing information about captured image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>byte[] compressedData</code> - Pointer of image which is compressed from original image by WSQ compression.
     *         <li><code>int compressedLength</code> - Length of image which is compressed from original image by WSQ compression.
     *         </ul>
     * @throws IBScanException
     */
    public Object[] wsqEncodeToMem(byte[] image, int width, int height, int pitch,
    		int bitPerPixel, int pixelPerInch, double bitRate, String commentText) throws IBScanException
    {
    	if (image == null)
    	{
    		logPrintWarning(getMethodName() + ": received null inputImage");
            throw (new IllegalArgumentException("Received null inputImage"));
    	}
    	
        final NativeError error   = new NativeError();
        final Object[]    returns = wsqEncodeToMemNative(image, width, height, pitch, bitPerPixel, 
        		pixelPerInch, bitRate, commentText, error);
        handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((returns == null) || (returns.length != 2) || (returns[0] == null) || (returns[1] == null))
        {
            logPrintError(getMethodName() + ": null or invalid WSQ information returned from native code");
            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        /* Convert return values to Java types. */
        Object[] returnsJ = new Object[2];
        
        returnsJ[0] = returns[0]; /* byte[] : Result WSQ Compressed image */
        returnsJ[1] = returns[1]; /* int : WSQ Compressed Length */
        
        return  (returns);
    }
    
    /**
     * WSQ de-compresses grayscale fingerprint image save to Memory
     * 
     * @param compressedImage  	WSQ-encoded image.
     * @param compressedLength  Length of WSQ-encoded image.
     * @return an array containing information about captured image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>byte[] decompressedImage</code> - Pointer of image which is decompressed from WSQ-encoded image.
     *         <li><code>int outWidth</code> - Width of decompressed image (in pixels)
     *         <li><code>int outHeight</code> - Height of decompressed image (in pixels)
     *         <li><code>int outPitch</code> - Image line pitch (in bytes)
     *         <li><code>int outBitsPerPixel</code> - Bits per pixel of decompressed image.
     *         <li><code>int outPixelPerInch</code> - Pixel per inch of decompressed image.
     *         </ul>
     * @throws IBScanException	
     */
    public Object[] wsqDecodeToMem(byte[] compressedImage, int compressedLength) throws IBScanException
    {
    	if (compressedImage == null)
        {
        	logPrintWarning(getMethodName() + ": received null compressedImage");
            throw (new IllegalArgumentException("Received null compressedImage"));
        }
    	
        final NativeError error   = new NativeError();
        final Object[]    returns = wsqDecodeToMemNative(compressedImage, compressedLength,error);
        handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((returns == null) || (returns.length != 6) || (returns[0] == null) || (returns[1] == null) || (returns[2] == null) 
        		|| (returns[3] == null) || (returns[4] == null) || (returns[5] == null))
        {
            logPrintError(getMethodName() + ": null or invalid WSQ information returned from native code");
            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        /* Convert return values to Java types. */
        Object[] returnsJ = new Object[6];
        
        returnsJ[0] = returns[0]; /* byte[] : Result WSQ de-Compressed image */
        returnsJ[1] = returns[1]; /* int : WSQ de-Compressed Width */
        returnsJ[2] = returns[2]; /* int : WSQ de-Compressed Height */
        returnsJ[3] = returns[3]; /* int : WSQ de-Compressed Pitch */
        returnsJ[4] = returns[4]; /* int : WSQ de-Compressed BitsPerPixel */
        returnsJ[5] = returns[5]; /* int : WSQ de-Compressed PixelPerInch */
        
        return  (returns);
    }
    
    /**
     * Call C language fwrite function via JNI
     * 
     * @param compressedImage  	WSQ-encoded image.
     * @param compressedLength  Length of WSQ-encoded image.
     * @param decompressedImage Pointer of image which is decompressed from WSQ-encoded image.
     * 							This pointer is deallocated by IBSU_FreeMemory() after using it.
     * @param outWidth			Width of decompressed image (in pixels)
     * @param outHeight			Height of decompressed image (in pixels)
     * @param outPitch			Image line pitch (in bytes)
     * @param outBitsPerPixel	Bits per pixel of decompressed image.
     * @param outPixelPerInch	Pixel per inch of decompressed image.
     * @throws IBScanException	
     */
    public int SaveRAWImage(byte[] writeBuffer, int writeLength, String fileName) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final int    nRc = SaveRAWImageNative(writeBuffer, writeLength, fileName, error); 
        handleError(error); /* throws exception if necessary */
        
        return  (nRc);
    }


	/**
     * Save StandardFileData as File
     * 
     * @param STDFormatData  StandardFormatData structure
     * @param filename       File save name
     * @throws IBScanException
     */
    public int SaveStandardFile(StandardFormatData STDFormatData, String filename ) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final int    filepath = SaveStandardFileNative(STDFormatData, filename , error);
        handleError(error); /* throws exception if necessary */
        
        return  (filepath);
    }
    /**
     * Generate scaled version of image.
     * 
     * @return scaled image
     * @throws IBScanException
     */
   public int generateZoomOutImageEx(byte[] image, int inWidth, int inHeight, byte[]outImage,int outWidth,
    		int outHeight,byte bkColor) throws IBScanException
    {
    	final NativeError error   = new NativeError();
    	final int  Img_ss = generateZoomOutImageExNative(image, inWidth, inHeight,outImage, outWidth, outHeight,bkColor, error);
    	handleError(error); 
    	return  (Img_ss);
    }


	public void createBmpEx(byte[] inImage,Bitmap bitmap)throws IBScanException
	{
		final NativeError error   = new NativeError();
		createBmpExNative(inImage, bitmap,error);
		handleError(error); 
	}

    /**
     * Generate enhanced image from preview, returning extended information.
     * 
     * @return an array containing information about enhanced image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>ImageData enhancedImage</code> - enhanced image data from preview image 
     *         <li><code>Integer detectedFingerCount</code> - detected finger count
     *         <li><code>ImageData[] segmentImageArray</code> - finger array split from the enhanced image
     *         <li><code>SegmentPosition[] segmentPositionArray</code> - position data for individual fingers split from enhanced image
     *         </ul>
     * @throws IBScanException
     */
    public Object[] getEnhancedImageReserved(final String reservedKey, ImageData image) throws IBScanException
    {
        if (reservedKey == null)
        {
        	logPrintWarning(getMethodName() + ": received null reservedKey");
            throw (new IllegalArgumentException("Received null reservedKey"));
        }
        else if (image == null)
        {
        	logPrintWarning(getMethodName() + ": received null image");
            throw (new IllegalArgumentException());
        }
        
        final NativeError error   = new NativeError();
        final Object[]    returns = getEnhancedImageReservedNative(reservedKey, image, error);
        handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((returns == null) || (returns.length != 4) || (returns[0] == null) || 
        		(returns[1] == null) || (returns[2] == null) || (returns[3] == null))
        {
            logPrintError(getMethodName() + ": null or invalid image information returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        /* Convert return values to Java types. */
        Object[] returnsJ = new Object[4];
        
        returnsJ[0] = returns[0]; /* enhanced image */
        returnsJ[1] = returns[1]; /* detected finger count */
        returnsJ[2] = returns[2]; /* segment image array */
        returnsJ[3] = returns[3]; /* segment position array */

        return (returnsJ);
    }

 /**
    ****************************************************************************************************
    * Generate Combine two images (2 flat fingers) into a single image
	*
    * @return
    *     IBScanDevice.ImageData (1600*1500)
	*
	* @throws IBScanException
    ****************************************************************************************************
    */
    public Object getCombineImage(ImageData image1,ImageData image2,CombineImageWhichHand whichHand) throws IBScanException
    {
    	if (image1 == null)
        {
        	logPrintWarning(getMethodName() + ": received null ImageData1");
            throw (new IllegalArgumentException("Received null ImageData1"));
        }
        else if (image2 == null)
        {
        	logPrintWarning(getMethodName() + ": received null ImageData2");
            throw (new IllegalArgumentException("Received null ImageData2"));
        }
    	
    	 final NativeError error   = new NativeError();
         final Object    returns = getCombineImageNative(image1, image2, whichHand.toCode() , error);
         handleError(error); /* throws exception if necessary */
		
         
         return (returns);

    }
    
    /**
    ****************************************************************************************************
    * Get characteristics of operable Beeper on a device.
	*
    * @return
    *     information about Beeper type
	*
	* @throws IBScanException
    ****************************************************************************************************
    */
    public BeeperType getOperableBeeper() throws IBScanException
    {
        final NativeError error               = new NativeError();
        final int         code                = getOperableBeeperNative(error);
        handleError(error); /* throws exception if necessary */
        
		final BeeperType beeperType = BeeperType.fromCode(code);
        
        /* Check for library or JNI interface error. */
        if (beeperType == null) 
        {
            logPrintError(getMethodName() + ": unrecognized beeperType code (" + code + ") returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }
        
        return (beeperType);
    }

    /**
    ****************************************************************************************************
    * Set the value of Beeper on a device.
	*
    * @param beepPattern  Pattern of beep
    *        soundTone    The frequency of the sound, in specific value. The parameter must be
    *                     in the range 0 through 2.
    *        duration     The duration of the sound, in 25 miliseconds. The parameter must be
    *                     in the range 1 through 200 at ENUM_IBSU_BEEP_PATTERN_GENERIC,
    *                     in the range 1 through 7 at ENUM_IBSU_BEEP_PATTERN_REPEAT.
    *        reserved_1   Reserved
    *        reserved_2   Reserved
    *                     If you set beepPattern to ENUM_IBSU_BEEP_PATTERN_REPEAT
    *                     reserved_1 can use the sleep time after duration of the sound, in 25 miliseconds.
    *                     The parameter must be in the range 1 through 8
    *                     reserved_2 can use the operation(start/stop of pattern repeat), 1 to start; 0 to stop 
	*
	* @throws IBScanException
    ****************************************************************************************************
    */
    public void setBeeper(BeepPattern beepPattern, int soundTone, int duration, int reserved_1, int reserved_2) throws IBScanException
    {
        final NativeError error = new NativeError();

        setBeeperNative(beepPattern.toCode(), soundTone, duration, reserved_1, reserved_2, error);
        handleError(error); /* throws exception if necessary */
    }

    /**
    ****************************************************************************************************
    * Combine two images (2 flat fingers) into a single image (left/right hands)
    * and return segment information as well
	*
     * @return an array containing information about combined image. The contents of the returned 
     *         array, in order, are
     *         <p><ul>
     *         <li><code>ImageData combinedImage</code> - combined image data (1600*1500) 
     *         <li><code>ImageData[] segmentImageArray</code> - finger array split from the combined image
     *         <li><code>SegmentPosition[] segmentPositionArray</code> - position data for individual fingers split from combined image
     *         <li><code>Integer segmentImageArrayCount</code> - detected segment count
     *         </ul>
	*
	* @throws IBScanException
    ****************************************************************************************************
    */
    public Object[] getCombineImageEx(ImageData image1,ImageData image2,CombineImageWhichHand whichHand) throws IBScanException
    {
    	if (image1 == null)
        {
        	logPrintWarning(getMethodName() + ": received null ImageData1");
            throw (new IllegalArgumentException("Received null ImageData1"));
        }
        else if (image2 == null)
        {
        	logPrintWarning(getMethodName() + ": received null ImageData2");
            throw (new IllegalArgumentException("Received null ImageData2"));
        }
    	
    	 final NativeError error   = new NativeError();
         final Object[]    returns = getCombineImageExNative(image1, image2, whichHand.toCode() , error);
         handleError(error); /* throws exception if necessary */
        
        /* Check for library or JNI interface error. */
        if ((returns == null) || (returns.length != 4) || (returns[0] == null) || 
        		(returns[1] == null) || (returns[2] == null) || (returns[3] == null))
        {
            logPrintError(getMethodName() + ": null or invalid image information returned from native code");

            error.code = IBScanException.Type.COMMAND_FAILED.toCode();
            handleError(error);
        }

        /* Convert return values to Java types. */
        Object[] returnsJ = new Object[4];
        
        returnsJ[0] = returns[0]; /* result image */
        returnsJ[1] = returns[1]; /* segment image array */
        returnsJ[2] = returns[2]; /* segment position array */
        returnsJ[3] = returns[3]; /* detected segment count */

        return (returnsJ);
    }

    /**
     * Generate scaled image in various formats for fast image display on canvas.
     * 
     * @return scaled image
     * @throws IBScanException
     */
    public int generateDisplayImage(byte[] image, int inWidth, int inHeight, byte[]outImage,int outWidth,
    		int outHeight,byte bkColor, int outFormat, int outQualityLevel, boolean outVerticalFlip) throws IBScanException
    {
    	final NativeError error   = new NativeError();
    	final int  Img_ss = generateDisplayImageNative(image, inWidth, inHeight,
    							outImage, outWidth, outHeight,bkColor, outFormat, outQualityLevel, outVerticalFlip, error);
    	handleError(error); 
    	return  (Img_ss);
    }

	/**
     * remove a finger image
     * 
     * @return <code>IBSU_STATUS_OK</code> if it is successful; <code>false</code> otherwise
     * @throws IBScanException
     */
    public int removeFingerImage(long fIndex) throws IBScanException
    {
        final NativeError 	error    = new NativeError();
        final int     		nRc = removeFingerImageNative(fIndex, error);
        handleError(error); /* throws exception if necessary */

        return nRc;
    }

	/**
     * Add an finger image for the fingerprint duplicate check and roll to slap comparison.
     * It can have only ten prints 
     * 
     * @return <code>IBSU_STATUS_OK(0)</code> if it is successful; 
     * @throws IBScanException
     */
    public int addFingerImage(ImageData image, long fIndex, ImageType imageType, boolean flagForce) throws IBScanException
    {
        final NativeError 	error    = new NativeError();
        final int     		nRc = addFingerImageNative(image, fIndex, imageType.toCode(), flagForce, error);
        handleError(error); // throws exception if necessary

        return nRc;
    }

	/**
     * Checks for the fingerprint duplicate from the stored prints by IBSU_AddFingerImage(). 
     * 
     * @return <code> matched position, bit-patterned finger index </code>
     * @throws IBScanException
     */
    public long isFingerDuplicated(ImageData image, long fIndex, ImageType imageType, int securityLevel) throws IBScanException
    {
        final NativeError	error    	= new NativeError();
        final long     	duplicated 	= isFingerDuplicatedNative(image, fIndex, imageType.toCode(), securityLevel, error);
        handleError(error); // throws exception if necessary

        return duplicated;
    }

	/**
     * Check if the fingerposition is valid or not.
     * 
     * @return <code>true(if the position is valid), false(invalid)
     * @throws IBScanException
     */
    public boolean isValidFingerGeometry(ImageData image, long fIndex, ImageType imageType) throws IBScanException
    {
        final NativeError 	error    = new NativeError();
        final boolean  		Valid = isValidFingerGeometryNative(image, fIndex, imageType.toCode(), error);
        handleError(error); // throws exception if necessary

        return Valid;
    }


    /**
     * Get a spoof score for the finger.
     *
     * @param image   image for which to calculate NFIQ score
     * @return        Spoof score, Pointer to return spoof score (the score range is from 0 to 1000)
     *                The closer to 1000 score, it means Live finger.
     * @throws        IBScanException
     */
    public int GetSpoofScore(String reservedKey,ImageData image) throws IBScanException
    {
        if (reservedKey == null)
        {
            logPrintWarning(getMethodName() + ": received null reservedKey");
            throw (new IllegalArgumentException());
        }

        if (image == null)
        {
            logPrintWarning(getMethodName() + ": received null image");
            throw (new IllegalArgumentException());
        }

        final NativeError error     = new NativeError();
        final int         spoofScore   = GetSpoofScoreNative(reservedKey, image, error);
        handleError(error);

        return (spoofScore);
    }
    
    /**
     * Spoof finger detection
     *
     * @param image   image to check for spoof finger
     * @return        result for the spoof finger check, (True or False)
     *                True	: Spoof finger
     *                False : Live finger
     * @throws        IBScanException
     */
    public boolean IsSpoofFingerDetected(ImageData image) throws IBScanException
    {
        if (image == null)
        {
            logPrintWarning(getMethodName() + ": received null image");
            throw (new IllegalArgumentException());
        }

        final NativeError error     			= new NativeError();
        final boolean     bIsSpoofFinger	= IsSpoofFingerDetectedNative(image, error);
        handleError(error);

        return (bIsSpoofFinger);
    }

    /**
     * Save scanned image result as Bitmap(.bmp)
     * 
     * @return <code>true(if the position is valid), false(invalid)
     * @throws IBScanException
     */
    public int SaveBitmapImage(String filename,byte[] image, int width, int height,
                               int pitch, double resX, double resY) throws IBScanException
    {
        final NativeError   error    = new NativeError();
        final int nRC = SaveBitmapImageNative(filename,image,width,height,pitch,resX,resY,error);
        handleError(error); // throws exception if necessary

        return nRC;
    }

    /**
     * Save JPEG-2000 image to specific file path.
     *
     * @param image  Pointer to image buffer.
     * @throws IBScanException
     */
    public int SaveJP2Image(String filename,byte[] image, int width, int height,
                            int pitch, double resX, double resY, int fQuality ) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final int nRc = SaveJP2ImageNative(filename, image, width, height, pitch, resX, resY ,fQuality, error);
        handleError(error); /* throws exception if necessary */

        return  nRc;
    }

    /**
     * Save png image to specific file path.
     *
     * @param image  Pointer to image buffer.
     * @throws IBScanException
     */
    public int SavePngImage(String filename,byte[] image, int width, int height,
                            int pitch, double resX, double resY) throws IBScanException
    {
        final NativeError error   = new NativeError();
        final int nRc = SavePngImageNative(filename,image, width, height, pitch, resX, resY, error);
        handleError(error); /* throws exception if necessary */

        return  nRc;
    }

	/**
     * Set encryption key and mode
     * 
     * @param encyrptionKey input data for encryption key (should be 32 bytes)
     *        encMode    	input data for encryption mode. (random, custom)
     * @return <code>true(if the position is valid), false(invalid)
     * @throws IBScanException
     */
    public int SetEncryptionKey(byte[] encyrptionKey, EncyptionMode encMode) throws IBScanException
    {
        final NativeError 	error    = new NativeError();
        final int  		nRc = SetEncryptionKeyNative(encyrptionKey,encMode.toCode(), error);
        handleError(error); // throws exception if necessary

        return nRc;
    }

    /**
     * Register listener for scan device events.
     * 
     * @param listener  listener for events
     */
    public void setScanDeviceListener(IBScanDeviceListener listener)
    {
        this.m_listener = listener;
    }
	
    /* *********************************************************************************************
     * (CLASS) PUBLIC INTERFACE
     ******************************************************************************************** */

    /**
     * Minimum contrast value.  @see #getContrast, #setContrast
     */
    public static final int MIN_CONTRAST_VALUE = 0;

    /**
     * Maximum contrast value.  @see #getContrast, #setContrast
     */
    public static final int MAX_CONTRAST_VALUE = 34;

    /**
     * Capture option constraint for auto contrast.  @see #beginCaptureImage
     */
    public static final int OPTION_AUTO_CONTRAST = 1;

    /**
     * Capture option constraint for auto capture.  @see #beginCaptureImage
     */
    public static final int OPTION_AUTO_CAPTURE = 2;

    /**
     * Capture option constraint for ignore finger count.  @see #beginCaptureImage
     */
    public static final int OPTION_IGNORE_FINGER_COUNT = 4;
	
	/**
	 * LED constant (none).  @see #getLEDs, #setLEDs
	 */
	public static final int LED_NONE = 0;
	/**
	 * LED constant (reserved for vendor; user cannot control it).  @see #getLEDs, #setLEDs
	 */
	public static final int LED_INIT_BLUE = 1;
	
	/**
	 * LED constant (green).  @see #getLEDs, #setLEDs
	 */
	public static final int LED_SCAN_GREEN = 2;
	
	/**
	 * LED constant (red LED for Curve - TBN240 series).  @see #getLEDs, #setLEDs
	 */
	public static final int LED_SCAN_CURVE_RED = 16;
	
	/**
	 * LED constant (green LED for Curve - TBN240 series).  @see #getLEDs, #setLEDs
	 */
	public static final int LED_SCAN_CURVE_GREEN = 32;

	/**
	 * LED constant (blue LED for Curve - TBN240 series).  @see #getLEDs, #setLEDs
	 */
	public static final int LED_SCAN_CURVE_BLUE = 64;
	
	/*
	LED bit defines with LED type
	*/
	
	/* Specific LED bit defines with LED type = ENUM_IBSU_LED_TYPE_FSCAN (e.g four finger scanner, Kojak). */
	public static final long IBSU_LED_F_BLINK_GREEN              = 268435456;  /* All Green LEDs blink. */
	public static final long IBSU_LED_F_BLINK_RED                = 536870912;  /* All Red LEDs blink. */
	public static final long IBSU_LED_F_LEFT_LITTLE_GREEN        = 16777216;	  /* Green LED for left little finger. */
	public static final long IBSU_LED_F_LEFT_LITTLE_RED          = 33554432;  /* Red LED for left little finger. */
	public static final long IBSU_LED_F_LEFT_RING_GREEN          = 67108864;	  /* Green LED for left ring finger. */
	public static final long IBSU_LED_F_LEFT_RING_RED            =134217728;  /* Red LED for left ring finger. */
	public static final long IBSU_LED_F_LEFT_MIDDLE_GREEN        =1048576;  /* Green LED for left middle finger. */
	public static final long IBSU_LED_F_LEFT_MIDDLE_RED           = 2097152;	  /* Red LED for left middle finger. */
	public static final long IBSU_LED_F_LEFT_INDEX_GREEN         = 4194304;  /* Green LED for left index finger. */
	public static final long IBSU_LED_F_LEFT_INDEX_RED           = 8388608;  /* Red LED for left index finger. */
	public static final long IBSU_LED_F_LEFT_THUMB_GREEN         = 65536;	  /* Green LED for left thumb finger. */
	public static final long IBSU_LED_F_LEFT_THUMB_RED            = 131072;  /* Red LED for left thumb finger. */
	public static final long IBSU_LED_F_RIGHT_THUMB_GREEN        = 262144;/* Green LED for right thumb finger. */
	public static final long IBSU_LED_F_RIGHT_THUMB_RED          = 524288;  /* Red LED for right thumb finger. */
	public static final long IBSU_LED_F_RIGHT_INDEX_GREEN         = 4096;  /* Green LED for right index finger. */
	public static final long IBSU_LED_F_RIGHT_INDEX_RED           = 8192;  /* Red LED for right index finger. */
	public static final long IBSU_LED_F_RIGHT_MIDDLE_GREEN       = 16384;  /* Green LED for right middle finger. */
	/* 0x00008000 cannot be used at VB6 (Negative value) */
	//public static final long IBSU_LED_F_RIGHT_MIDDLE_RED         0x00008000  /* Red LED for right middle finger. */
	public static final long IBSU_LED_F_RIGHT_MIDDLE_RED         = 1073741824L;  /* Red LED for right middle finger. */
	public static final long IBSU_LED_F_RIGHT_RING_GREEN        = 256;/* Green LED for right ring finger. */
	public static final long IBSU_LED_F_RIGHT_RING_RED           =512;  /* Red LED for right ring finger. */
	public static final long IBSU_LED_F_RIGHT_LITTLE_GREEN       =1024;  /* Green LED for right little finger. */
	public static final long IBSU_LED_F_RIGHT_LITTLE_RED        =2048;  /* Red LED for right little finger. */
	public static final long IBSU_LED_F_PROGRESS_ROLL            =16;  /* LED for the roll indicator. */
	public static final long IBSU_LED_F_PROGRESS_LEFT_HAND       =32;  /* LED for the left hand indicator. */
	public static final long IBSU_LED_F_PROGRESS_TWO_THUMB       =64;  /* LED for the two thumb indicator. */
	public static final long IBSU_LED_F_PROGRESS_RIGHT_HAND      =128;  /* LED for the right hand indicator. */

	/* Bit-pattern of finger index for RemoveFingerImageNative, IBSU_AddFingerImage, IsFingerDuplicatedNative and IsValidFingerGeometryNative */
	public static final long IBSU_FINGER_NONE                    = 0;
	public static final long IBSU_FINGER_LEFT_LITTLE             = 1;
	public static final long IBSU_FINGER_LEFT_RING               = 2;
	public static final long IBSU_FINGER_LEFT_MIDDLE             = 4;
	public static final long IBSU_FINGER_LEFT_INDEX              = 8;
	public static final long IBSU_FINGER_LEFT_THUMB              = 16;
	public static final long IBSU_FINGER_RIGHT_THUMB             = 32;
	public static final long IBSU_FINGER_RIGHT_INDEX             = 64;
	public static final long IBSU_FINGER_RIGHT_MIDDLE            = 128;
	public static final long IBSU_FINGER_RIGHT_RING              = 256;
	public static final long IBSU_FINGER_RIGHT_LITTLE            = 512;
	public static final long IBSU_FINGER_LEFT_HAND               = 15;
	public static final long IBSU_FINGER_RIGHT_HAND              = 960;
	public static final long IBSU_FINGER_BOTH_THUMBS			 = 48;
	public static final long IBSU_FINGER_ALL                     = 1023;
	public static final long IBSU_FINGER_LEFT_LITTLE_RING		 = 3;
	public static final long IBSU_FINGER_LEFT_MIDDLE_INDEX		 = 12;
	public static final long IBSU_FINGER_RIGHT_INDEX_MIDDLE		 = 192;
	public static final long IBSU_FINGER_RIGHT_RING_LITTLE		 = 768;

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
     * PROTECTED INTERFACE
     ******************************************************************************************** */

    /*
     * Constructor.  Called by native code.
     */
    protected IBScanDevice(long handleNative)
    {
        this.m_handleNative = handleNative;
        this.m_isOpened     = true;
    }

    /* *********************************************************************************************
     * PRIVATE INTERFACE
     ******************************************************************************************** */

    /*
     *  The handle for this device.  Accessed from native code.
     */
    private final long m_handleNative;

    /*
     *  Indicates whether this device has been closed.
     */
    private boolean m_isOpened;

    /* 
     * Scan device listener.
     */
    private IBScanDeviceListener m_listener = null;

    /*
     *  Handle error from native method.
     */
    private static void handleError(NativeError error) throws IBScanException
    {
        if (error.code != 0)
        {
            IBScanException.Type type;

            type = IBScanException.Type.fromCode(error.code);
            if (type == null)
            {
            	logPrintError(getMethodName() + ": unrecognized error code (" + error.code + ") returned from native code");
            	type = IBScanException.Type.COMMAND_FAILED;
            }
            throw (new IBScanException(type));
        }
    }

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
    
    /* *********************************************************************************************
     * INTERFACE METHODS: IBScanDeviceListener INTERMEDIATES
     ******************************************************************************************** */

    /* 
     * Callback for break in device communication.  Called from native code.
     */
    private void callbackDeviceCommunicationBroken()
    {
    	if (this.m_listener != null)
        {
            this.m_listener.deviceCommunicationBroken(this);
        }

    }

    /* 
     * Callback when image preview is available.  Called from native code.
     */
    private void callbackDeviceImagePreviewAvailable(final ImageData image)
    {
        if (this.m_listener != null)
        {
            this.m_listener.deviceImagePreviewAvailable(this, image);
        }
    }

    /* 
     * Callback when device finger count changes.  Called from native code.
     */
    private void callbackDeviceFingerCountChanged(final int fingerStateCode)
    {
        if (this.m_listener != null)
        {
            final FingerCountState fingerState = FingerCountState.fromCode(fingerStateCode);
            if (fingerState == null)
            {
                logPrintError(getMethodName() + ": unrecognized fingerState code (" + fingerStateCode + ") returned from native code");
            }
            else
            {
                this.m_listener.deviceFingerCountChanged(this, fingerState);
            }
        }
    }

    /* 
     * Callback when device finger quality changes.  Called from native code.
     */
    private void callbackDeviceFingerQualityChanged(final int[] fingerQualityCodes)
    {
        if (this.m_listener != null)
        {

            if (fingerQualityCodes != null)
            {
                boolean ok = true;
                
                /* Convert values to Java types. */
            	final FingerQualityState[] fingerQualities = new FingerQualityState[fingerQualityCodes.length];

                for (int i = 0; i < fingerQualityCodes.length; i++)
                {
                    fingerQualities[i] = FingerQualityState.fromCode(fingerQualityCodes[i]);
                    if (fingerQualities[i] == null)
                    {
                        logPrintError(getMethodName() + ": unrecognized fingerQuality code (" + fingerQualityCodes[i] + ") returned from native code");
                        ok = false;
                        break;
                    }
                }
                if (ok)
                {
                    this.m_listener.deviceFingerQualityChanged(this, fingerQualities);
                }
            }
        }
    }

    /* 
     * Callback when acquisition has begun.  Called from native code.
     */
    private void callbackDeviceAcquisitionBegun(final int imageTypeCode)
    {
        if (this.m_listener != null)
        {
        	final ImageType imageType = ImageType.fromCode(imageTypeCode);

            if (imageType == null)
            {
                logPrintError(getMethodName() + ": unrecognized imageType code (" + imageType + ") returned from native code");
            }
            else
            {
                this.m_listener.deviceAcquisitionBegun(this, imageType);
            }
        }
    }

    /* 
     * Callback when acquisition has completed.  Called from native code.
     */
    private void callbackDeviceAcquisitionCompleted(final int imageTypeCode)
    {
        if (this.m_listener != null)
        {
        	final ImageType imageType = ImageType.fromCode(imageTypeCode);

            if (imageType == null)
            {
                logPrintError(getMethodName() + ": unrecognized imageType code (" + imageType + ") returned from native code");
            }
            else
            {
                this.m_listener.deviceAcquisitionCompleted(this, imageType);
            }
        }
    }

    /*
     * Callback when image result is available.  Called from native code.
     */
    private void callbackDeviceImageResultAvailable(final ImageData image, final int imageTypeCode,
    		final ImageData[] splitImageArray)
    {
        if (this.m_listener != null)
        {
        	final ImageType imageType = ImageType.fromCode(imageTypeCode);

            if (imageType == null)
            {
                logPrintError(getMethodName() + ": unrecognized imageType code (" + imageType + ") returned from native code");
            }
            else
            {
                this.m_listener.deviceImageResultAvailable(this, image, imageType, splitImageArray);
            }
        }
    }

    /*
     * Callback when extended image result is available.  Called from native code.
     */
    private void callbackDeviceImageResultExtendedAvailable(final int imageStatusCode, 
    		final ImageData image, final int imageTypeCode, final int detectedFingerCount,
    		final ImageData[] segmentImageArray, final SegmentPosition[] segmentPositionArray)
    {
        if (this.m_listener != null)
        {
        	final ImageType imageType = ImageType.fromCode(imageTypeCode);

            if (imageType == null)
            {
                logPrintError(getMethodName() + ": unrecognized imageType code (" + imageType + ") returned from native code");
            }
            else
            {
            	final IBScanException.Type imageStatusType = (imageStatusCode == 0) ? null : (IBScanException.Type.fromCode(imageStatusCode));
            	
            	if ((imageStatusCode != 0) && (imageStatusType == null))
            	{
                    logPrintError(getMethodName() + ": unrecognized imageStatus code (" + imageStatusCode + ") returned from native code");            		
            	}
            	else
            	{
            		final IBScanException imageStatus = (imageStatusCode == 0) ? null : (new IBScanException(imageStatusType));

            		this.m_listener.deviceImageResultExtendedAvailable(this, imageStatus, image, 
            				imageType, detectedFingerCount, segmentImageArray, segmentPositionArray);
            	}
            }
        }
    }

    /*
     * Callback when the platen state changes.  Called from native code.
     */
    private void callbackDevicePlatenStateChanged(final int platenStateCode)
    {
        if (this.m_listener != null)
        {
        	final PlatenState platenState = PlatenState.fromCode(platenStateCode);
            
            if (platenState == null)
            {
                logPrintError(getMethodName() + ": unrecognized platenState code (" + platenStateCode + ") returned from native code");
            }
            else
            {
                this.m_listener.devicePlatenStateChanged(this, platenState);
            }
        }
    }

    /* 
     * Callback when warning is received.  Called from native code.
     */
    private void callbackDeviceWarningReceived(final int warningCode)
    {
        if (this.m_listener != null)
        {
        	final IBScanException.Type type = IBScanException.Type.fromCode(warningCode);
            if (type == null)
            {
                logPrintError(getMethodName() + ": unrecognized warning code (" + warningCode + ") returned from native code");
            }
            else
            {
                this.m_listener.deviceWarningReceived(this, new IBScanException(type));
            }
        }
    }
    
     /* 
     * Callback when key button is pressed.  Called from native code.
     */
    private void callbackDevicePressedKeyButtons(final int pressedKeyCode)
    {
        if (this.m_listener != null)
        {
            this.m_listener.devicePressedKeyButtons(this, pressedKeyCode);
        }
    }

    /* *********************************************************************************************
     * NATIVE METHODS
     ******************************************************************************************** */

    /* Native method for enableEvent(). */
    private native void enableEventNative(int eventCode, boolean enable, NativeError error);
    
    /* Native method for close(). */
    private native void closeNative(NativeError error);

    /* Native method for setProperty(). */
    private native void setPropertyNative(int propertyIdCode, String propertyValue,
        NativeError error);

    /* Native method for setPropertyReserved(). */
    private native void setPropertyReservedNative(String reservedKey, int propertyIdCode, String propertyValue,
        NativeError error);

    /* Native method for getProperty(). */
    private native String getPropertyNative(int propertyIdCode, NativeError error);

    /* Native method for isCaptureAvailable(). */
    private native boolean isCaptureAvailableNative(int imageTypeCode, int imageResolutionCode,
        NativeError error);

    /* Native method for beginCapture(). */
    private native void beginCaptureImageNative(int imageTypeCode, int imageResolutionCode,
        int captureOptions, NativeError error);

    /* Native method for cancelCaptureImage(). */
    private native void cancelCaptureImageNative(NativeError error);

    /* Native method for isCaptureActive(). */
    private native boolean isCaptureActiveNative(NativeError error);

    /* Native method for captureImageMaually(). */
    private native void captureImageManuallyNative(NativeError error);

    /* Native method for getContrast(). */
    private native int getContrastNative(NativeError error);

    /* Native method for setContrast(). */
    private native void setContrastNative(int contrastValue, NativeError error);

    /* Native method for setLEOperationMode(). */
    private native void setLEOperationModeNative(int leOperationMode, NativeError error);

    /* Native method for getLEOperationMode(). */
    private native int getLEOperationModeNative(NativeError error);

    /* Native method for isFingerTouching(). */
    private native boolean isFingerTouchingNative(NativeError error);

    /* Native method for getOperableLEDs(). */
    private native LedState getOperableLEDsNative(NativeError error);

    /* Native method for getLEDs(). */
    private native long getLEDsNative(NativeError error);

    /* Native method for setLEDs(). */
    private native void setLEDsNative(long activeLEDs, NativeError error);

    /* Native method for captureImage(). */
    private native Object[] captureImageNative(NativeError error);
    
    /* Native method for captureImageExtended(). */
    private native Object[] captureImageExtendedNative(NativeError error);

    /* Native method for getPlatenStateAtCapture(). */
    private native int getPlatenStateAtCaptureNative(NativeError error);

    /* Native method for bgetRollingInfo(). */
    private native RollingData getRollingInfoNative(NativeError error);

    /* Native method for calculateNfiqScore(). */
    private native int calculateNfiqScoreNative(ImageData image, NativeError error);
    
    /* Native method for getResultImageExt(). */
    private native Object[] getResultImageExtNative(int fingerPositionCode, NativeError error);
    
    /* Native method for wsqEncodeToFile(). */
    private native int wsqEncodeToFileNative(String filename,byte[] image, int width, int height, int pitch,
    		int bitPerPixel, int pixelPerInch, double bitRate, String commentType, NativeError error);
    
    /* Native method for wsqEncodeToMem(). */
    private native Object[] wsqEncodeToMemNative(byte[] image, int width, int height, int pitch,
    		int bitPerPixel, int pixelPerInch, double bitRate, String commentText, NativeError error);
    
    /* Native method for wsqDecodeToMem(). */
    private native Object[] wsqDecodeToMemNative(byte[] compressedImage, int compressedLength, NativeError error);
    
    /* Native method for SaveRAWImage(). */
    private native int SaveRAWImageNative(byte[] writeBuffer, int writeLength, String fileName,NativeError error);

	/* Native method for SaveStandardFile(). */
    private native int SaveStandardFileNative(StandardFormatData STDFormatData, String filename, NativeError error);

    /* Native method for generateZoomOutImageEx(). */
    private native int generateZoomOutImageExNative(byte[] image, int inWidth, int inHeight,
    		byte[]outImage,int outWidth, int outHeight,byte bkColor,NativeError error);
	private native void createBmpExNative(byte[] inImage,Bitmap bitmap,NativeError error);
    
    /* Native method for getEnhancedImage(). */
    private native Object[] getEnhancedImageReservedNative(final String reservedKey, ImageData image, NativeError error);
	
	/* Native method for getcombineImage(). */
    private native Object getCombineImageNative(ImageData image1,ImageData image2,int whichHand,NativeError error);
    
    /* Native method for getOperableBeeper(). */
    private native int getOperableBeeperNative(NativeError error);
    
     /* Native method for setBeeper(). */
    private native int setBeeperNative(int bPattern,int soundTone ,int duration, int reserved_1, int reserved_2, NativeError error);
    
    /* Native method for getcombineImageEx(). */
    private native Object[] getCombineImageExNative(ImageData image1,ImageData image2,int whichHand,NativeError error);

    /* Native method for generateDisplayImage(). */
    private native int generateDisplayImageNative(byte[] image, int inWidth, int inHeight,
    		byte[]outImage,int outWidth, int outHeight,byte bkColor, int outFormat, int outQualityLevel, boolean outVerticalFlip, NativeError error);

	/* Native method for removeFingerImage(). */
    private native int removeFingerImageNative(long fIndex, NativeError error);

	/* Native method for addFingerImage(). */
    private native int addFingerImageNative(ImageData image, long fIndex, int imageType, boolean flagForce, NativeError error);

	/* Native method for isFingerDuplicated(). */
    private native long isFingerDuplicatedNative(ImageData image, long fIndex, int imageType, int securityLevel, NativeError error);

	/* Native method for isValidFingerGeometry(). */
    private native boolean isValidFingerGeometryNative(ImageData image, long fIndex, int imageType, NativeError error);

	/* Native method for GetSpoofScore(). */
    private native int GetSpoofScoreNative(String reservedKey, ImageData image, NativeError error);
    
    /* Native method for IsSpoofFingerDetected(). */
    private native boolean IsSpoofFingerDetectedNative(ImageData image, NativeError error);

	/* Native method for SaveBitmapImage(). */
	private native int SaveBitmapImageNative(String filename,byte[] image, int width, int height,
                                             int pitch, double resX, double resY, NativeError error);

    /* Native method for SaveJP2Image(). */
    private native int SaveJP2ImageNative(String filename,byte[] image, int width, int height,
                                          int pitch, double resX, double resY,int fQuality, NativeError error);

    /* Native method for SavePngImage(). */
    private native int SavePngImageNative(String filename,byte[] image, int width, int height,
                                          int pitch, double resX, double resY, NativeError error);
	
	/* Native method for SetEncryptionKeyNative(). */
    private native int SetEncryptionKeyNative(byte[] encryptionKey, int encMode, NativeError error);
	
	/* Native method for ConvertImageToISOANSI(). */
    private native Object ConvertImageToISOANSINative(ImageDataExt[] image, int imageCount, int imageFormat, int STDformat, NativeError error);

    /* *********************************************************************************************
     * STATIC BLOCKS
     ******************************************************************************************** */

    /*
     *  Helper block to get method name for debug messages. 
     */
    static 
    {
        int i = 0;
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) 
        {
            i++;
            if (ste.getClassName().equals(IBScanDevice.class.getName())) 
            {
                break;
            }
        }
        METHOD_STACK_INDEX = i;
    }
    
    /*
     *  Load native library.
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
