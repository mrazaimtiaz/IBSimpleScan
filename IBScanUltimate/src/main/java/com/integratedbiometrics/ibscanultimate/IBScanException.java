/* *************************************************************************************************
 * IBScanException.java
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
 *     2013/03/22  Added new exception types.
 *     2013/10/18  Added new exceptions (DEVICE_NEED_UPDATE_FIRMWARE, API_DEPRECATED, 
 *                 NO_FINGER, INCORRECT_FINGERS, ROLLING_SMEAR).
 *     2014/02/25  Added warning to check incorrect fingers/smear
 *                       (ROLLING_SHIFTED_HORIZONTALLY,ROLLING_SHIFTED_VERTICALLY)
 *     2017/04/27  Added warning codes
 *                       (QUALITY_INVALID_AREA, INVALID_BRIGHTNESS_FINGERS, WET_FINGERS)
 *     2017/06/16  Added error codes
 *                       (DEVICE_NEED_CALIBRATE_TOF, MULTIPLE_FINGERS_DURING_ROLL)
 *     2018/04/27  Added error codes
 *                       (DEVICE_INVALID_CALIBRATION_DATA)
 *     2018/11/19  Added error codes
 *                        (DEVICE_HIGHER_SDK_REQUIRED)
 *	   2020/01/09  Added warning codes
 *                        (SPOOF_DETECTED)
 *     2020/04/01  Added warning codes
 *                        (ROLLING_SLIP_DETECTED)
 *     2020/04/01  Added warning codes
 *                        (SPOOF_INIT_FAILED)
 ************************************************************************************************ */

package com.integratedbiometrics.ibscanultimate;

/**
 * Exception thrown when error returned by IB device.
 */
@SuppressWarnings("serial")
public class IBScanException extends Exception
{
    // The type of this exception.
    private final Type type;

    /**
     * Enumeration representing type of exception.
     */
    public enum Type
    {
        /**
         * Invalid parameter value
         */
        INVALID_PARAM_VALUE(-1),
        /**
         * Insufficient memory
         */
        MEM_ALLOC(-2),
        /**
         * Requested functionality isn't supported
         */
        NOT_SUPPORTED(-3),
        /**
         * File open failed (e.g. usb handle, pipe, image file ,,,)
         */
        FILE_OPEN(-4),
        /**
         * File read failed (e.g. usb handle, pipe, image file ,,,)
         */
        FILE_READ(-5),
        /**
         * Failure due to a locked resource
         */
        RESOURCE_LOCKED(-6),
        /**
         * Failure due to a missing resource (e.g. DLL file)
         */
        MISSING_RESOURCE(-7),
        /**
         * Invalid access pointer address
         */
        INVALID_ACCESS_POINTER(-8),
        /**
         * Thread creation failed
         */
        THREAD_CREATE(-9),
        /**
         * Generic command execution failed
         */
        COMMAND_FAILED(-10),
        /**
         * The library unload failed
         */
        LIBRARY_UNLOAD_FAILED(-11),

        /**
         * Command execution failed
         */
        CHANNEL_IO_COMMAND_FAILED(-100),
        /**
         * Input communication failed
         */
        CHANNEL_IO_READ_FAILED(-101),
        /**
         * Output communication failed
         */
        CHANNEL_IO_WRITE_FAILED(-102),
        /**
         * Input command execution timed out, but device communication is alive.
         */
        CHANNEL_IO_READ_TIMEOUT(-103),
        /**
         * Output command execution timed out, but device communication is
         * alive.
         */
        CHANNEL_IO_WRITE_TIMEOUT(-104),
        /**
         * Unexpected communication failed. (Only used on IBTraceLogger)
         */
        CHANNEL_IO_UNEXPECTED_FAILED(-105),
        /**
         * I/O handle state is invalid; reinitialization required (Close + Open)
         */
        CHANNEL_IO_INVALID_HANDLE(-106),
        /**
         * I/O pipe index is invalid; reinitialization required (Close + Open)
         */
        CHANNEL_IO_WRONG_PIPE_INDEX(-107),

        /**
         * Device communication failed
         */
        DEVICE_IO(-200),
        /**
         * No device is detected/active
         */
        DEVICE_NOT_FOUND(-201),
        /**
         * No matching device is detected
         */
        DEVICE_NOT_MATCHED(-202),
        /**
         * Initialization failed because in use by another thread/process
         */
        DEVICE_ACTIVE(-203),
        /**
         * Device needs to be initialized
         */
        DEVICE_NOT_INITIALIZED(-204),
        /**
         * Device state is invalid; re-initialization required (an exit followed by an 
         * initialization)
         */
        DEVICE_INVALID_STATE(-205),
        /**
         * Another thread is currently using device functions
         */
        DEVICE_BUSY(-206),
        /**
         * No hardware support for requested function
         */
        DEVICE_NOT_SUPPORTED_FEATURE(-207),
        /**
         * The license is invalid or does not match to the device
         */
        INVALID_LICENSE(-208),
        /**
         * Device is connected to an USB full speed port but high speed is
         * required
         */
        USB20_REQUIRED(-209),
        /**
         * Power save mode has been enabled on device, so you cannot control I/O (LED, LE, contrast) 
         * until starting capture stream..
         */
        DEVICE_ENABLED_POWER_SAVE_MODE(-210),
        /** 
         * Need to update firmware.
         */
        DEVICE_NEED_UPDATE_FIRMWARE(-211),
        /** 
         * Need to calibrate TOF.
         */
        DEVICE_NEED_CALIBRATE_TOF(-212),
        /** 
         * Invalid calibration data from the device.
         */
        DEVICE_INVALID_CALIBRATION_DATA(-213),
        /** 
         * Device is required to connect higher SDK version for runnging.
         */
        DEVICE_HIGHER_SDK_REQUIRED(-214),
        /** 
         * The Lock-info buffer is not valid.
         */
        DEVICE_LOCK_INVALID_BUFF(-215),
        /** 
         * The Lock-info buffer is empty.
         */
        DEVICE_LOCK_INFO_EMPTY(-216),
        /** 
         * The Customer Key is not registered into the devices.
         */
        DEVICE_LOCK_INFO_NOT_MATCHED(-217),
        /** 
         * Checksums between buffer and calculated are different.
         */
        DEVICE_LOCK_INVALID_CHECKSUM(-218),
        /** 
         * The Customer Key is invalied.
         */
        DEVICE_LOCK_INVALID_KEY(-219),
        /** 
         * The deived is locked.
         */
        DEVICE_LOCK_LOCKED(-220),
        /** 
         * The device is not valid from the license file.
         */
        DEVICE_LOCK_ILLEGAL_DEVICE(-221),
        /**
         * Device is not accessible.
         */
        DEVICE_NOT_ACCESSIBLE(-299),

        /**
         * Image acquisition failed
         */
        CAPTURE_COMMAND_FAILED(-300),
        /**
         * Stop capture failed
         */
        CAPTURE_STOP(-301),
        /**
         * Timeout during capturing
         */
        CAPTURE_TIMEOUT(-302),
        /**
         * A capture is still running
         */
        CAPTURE_STILL_RUNNING(-303),
        /**
         * A capture is not running
         */
        CAPTURE_NOT_RUNNING(-304),
        /**
         * Capture mode is not valid or not supported
         */
        CAPTURE_INVALID_MODE(-305),
        /**
         * Generic algorithm processing failure
         */
        CAPTURE_ALGORITHM(-306),
        /**
         * Image processing failure at rolled finger print processing
         */
        CAPTURE_ROLLING(-307),
        /**
         * No roll start detected within a defined timeout period
         */
        CAPTURE_ROLLING_TIMEOUT(-308),

        /**
         * Not used in Java / Android
         * Generic client window failure
        CLIENT_WINDOW(-400),
		*/
        /**
         * Not used in Java / Android
         * Image processing failure at rolled finger print processing
        CLIENT_WINDOW_NOT_CREATE (-401),
         */
        /**
         * Not used in Java / Android
         * No roll start detected within a defined timeout period
        INVALID_OVERLAY_HANDLE(-402),
         */
        
        /**
         * Getting NFIQ score failed
         */
        NBIS_NFIQ_FAILED(-500),
        /**
         * WSQ encode failed
         */
        NBIS_WSQ_ENCODE_FAILED(-501),
        /**
         * WSQ decode failed
         */
        NBIS_WSQ_DECODE_FAILED(-502),
        /**
         * Not used in Java / Android
         * PNG encode failed
        NBIS_PNG_DECODE_FAILED(-503),
         */
        /**
         * Not used in Java / Android
         * JP2 encode failed
        NBIS_JP2_DECODE_FAILED(-504),
         */
        
        /**
         * When the extraction from the fingerimage is faild in IBSU_ADDFingerImage and DLL_IsFingerDuplicated
         */
        DUPLICATE_EXTRACTION_FAILED(-600),
        /**
         * When the image of the fingerposition is already in use. in IBSU_ADDFingerImage
         */
        DUPLICATE_ALREADY_USED(-601),
        /**
         * When found segment fingercounts are not two and more in IBSU_IsValidFingerGeometry
         */
        DUPLICATE_SEGMENTATION_FAILED(-602),
        /**
         * When found small extrations in IBSM_MatchingTemplate
         */
        DUPLICATE_MATCHING_FAILED(-603),

        /**
         * When PAD Property is not enabled.
         */
        PAD_PROPERTY_DISABLED(-700),
        
        /**
         * Missing a frame image (Only used on IBTraceLogger)
         */
        CHANNEL_IO_FRAME_MISSING(100),
        /**
         * Camera work is wrong. reset is requied (Only used on IBTraceLogger)
         */
        CHANNEL_IO_CAMERA_WRONG(101),
        /**
		 * 
		 */
        CHANNEL_IO_SLEEP_STATUS(102),
        /**
         * Device firmware version outdated
         */
        OUTDATED_FIRMWARE(200),
        /**
         * Device/component has already been initialized and is ready to be used
         */
        ALREADY_INITIALIZED(201),
        /**
         * API function was deprecated.
         */
        API_DEPRECATED(202),
        /**
         * Image has already been enhanced.
         */
        ALREADY_ENHANCED_IMAGE(203),
        /**
         * Device still do not get the first one frame image
         */
        BGET_IMAGE(300),
        /**
         * Rolling is not started
         */
        ROLLING_NOT_RUNNING(301),
        /**
         * No finger detected in result image.
         */
        NO_FINGER(302),
        /** 
         * Incorrect fingers detected in result image.
         */
        INCORRECT_FINGERS(303),
        /**
         * Smear detected in rolled result image.
         */
        ROLLING_SMEAR(304),
        /**
         * Rolled finger was shifted horizontally
         */
        ROLLING_SHIFTED_HORIZONTALLY(305),
        /**
         * Rolled finger was shifted vertically
         */
        ROLLING_SHIFTED_VERTICALLY(306),
        /**
         * Rolled finger was shifted horizontally and vertically
         */
        ROLLING_SHIFTED_HORIZONTALLY_VERTICALLY(307),
        /**
         * Empty result image
         */
        EMPTY_IBSM_RESULT_IMAGE(400),
        /**
         * Finger was located on the invalid area
         */
        QUALITY_INVALID_AREA(512),
        /**
         * Finger was located on the horizontal invalid area
         */
        QUALITY_INVALID_AREA_HORIZONTALLY(513),
        /**
         * Finger was located on the vertical invalid area
         */
        QUALITY_INVALID_AREA_VERTICALLY(514),
        /**
         * Finger was located on the both horizontal and vertical invalid area
         */
		QUALITY_INVALID_AREA_HORIZONTALLY_VERTICALLY(515),        
        /**
         * When a finger doesn't meet image brightness criteria
         */
        INVALID_BRIGHTNESS_FINGERS(600),
        /**
         * When detected wet finger
         */
        WET_FINGERS(601),
        /**
         * When detected multiple fingers during roll
         */
        MULTIPLE_FINGERS_DURING_ROLL(602),
    	/**
         * When detected spoof finger
         */
        SPOOF_DETECTED(603),
        /**
         * When detected slip finger
         */
        SLIP_DETECTED(604),
		/**
         * When Spoof initalize failed
         */
        SPOOF_INIT_FAILED(605);
        
        /* Native value for enumeration. */
        private final int code;

        Type(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        protected static Type fromCode(int code)
        {
            for (Type t : Type.values())
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

    /* Instantiate new exception from type. */
    IBScanException(Type type)
    {
        this.type = type;
    }

    /**
     * Get type of exception.
     * 
     * @return type of exception
     */
    public Type getType()
    {
        return (this.type);
    }
}
