/* *************************************************************************************************
 * IBCommon.java
 * 
 * DESCRIPTION:
 *     Android Java wrapper for common definitions among IBScan libraries
 *     http://www.integratedbiometrics.com
 *
 * NOTES:
 *     Copyright (c) Integrated Biometrics, 2013
 *     
 * HISTORY:
 *     2013/03/26  First version.
 *     2022/04/14  Added StandardFormatData Class structure
 *                        Added enumeration value to StandardFormat
 *							(STANDARD_FORMAT_ISO_19794_2_2005,
 *                           STANDARD_FORMAT_ISO_19794_4_2005,
 *                           STANDARD_FORMAT_ISO_19794_2_2011,
 *                           STANDARD_FORMAT_ISO_19794_4_2011,
 *                           STANDARD_FORMAT_ANSI_INCITS_378_2004,
*                            STANDARD_FORMAT_ANSI_INCITS_381_2004)
 ************************************************************************************************ */

package com.integratedbiometrics.ibscancommon;

/**
 * Class encapsulating matcher functionality.
 */
public class IBCommon 
{
    /* *********************************************************************************************
     * PUBLIC INNER CLASSES
     ******************************************************************************************** */

	/**
	 * Container to hold image data together with extended meta data.
	 */
	public static class ImageDataExt
	{
		public final ImageFormat         imageFormat;
		public final ImpressionType      impressionType;
		public final FingerPosition      fingerPosition;
		public final CaptureDeviceTechId captureDeviceTechId;
		public final short               captureDeviceVendorId;
		public final short               captureDeviceTypeId;
		public final short               scanSamplingX;
		public final short               scanSamplingY;
		public final short               imageSamplingX;
		public final short               imageSamplingY;
		public final short               imageSizeX;
		public final short               imageSizeY;
		public final byte                scaleUnit;
		public final byte                bitDepth;
		public final int				 imageDataLength;
		public final byte[]              imageData;

		protected ImageDataExt(int imageFormatCode, int impressionTypeCode, int fingerPositionCode,
				int captureDeviceTechIdCode, short captureDeviceVendorId, short captureDeviceTypeId,
				short scanSamplingX, short scanSamplingY, short imageSamplingX, short imageSamplingY,
				short imageSizeX, short imageSizeY, byte scaleUnit, byte bitDepth, int imageDataLength, byte[] imageData)
		{
			this.imageFormat           = ImageFormat.fromCode(imageFormatCode);
			this.impressionType        = ImpressionType.fromCode(impressionTypeCode);
			this.fingerPosition        = FingerPosition.fromCode(fingerPositionCode);
			this.captureDeviceTechId   = CaptureDeviceTechId.fromCode(captureDeviceTechIdCode);
			this.captureDeviceVendorId = captureDeviceVendorId;
			this.captureDeviceTypeId   = captureDeviceTypeId;
			this.scanSamplingX         = scanSamplingX;
			this.scanSamplingY         = scanSamplingY;
			this.imageSamplingX        = imageSamplingX;
			this.imageSamplingY        = imageSamplingY;
			this.imageSizeX            = imageSizeX;
			this.imageSizeY            = imageSizeY;
			this.scaleUnit             = scaleUnit;
			this.bitDepth              = bitDepth;
			this.imageDataLength       = imageDataLength;
			this.imageData             = imageData;
		}
	
		@Override
		public String toString()
		{
			final String s = "Image format = "             + this.imageFormat.toString()                       + "\n"
					       + "Impression type = "          + this.impressionType.toString()                    + "\n"
					       + "Finger position = "          + this.fingerPosition.toString()                    + "\n"
					       + "Capture device tech ID = "   + this.captureDeviceTechId.toString()               + "\n"
					       + "Capture device vendor ID = " + this.captureDeviceVendorId                        + "\n"
					       + "Capture device type ID = "   + this.captureDeviceTypeId                          + "\n"
					       + "Scan sampling = "            + this.scanSamplingX  + " x " + this.scanSamplingY  + "\n"
					       + "Image sampling = "           + this.imageSamplingX + " x " + this.imageSamplingY + "\n"
					       + "Image size = "               + this.imageSizeX     + " x " + this.imageSizeY     + "\n"
					       + "Scale unit = "               + this.scaleUnit                                    + "\n";
			return (s);
		}
	}
	
	/**
	 * Container to hold image data StandardFormatData (IBSM_StandardFormatData)
	 */
	public static class StandardFormatData
	{
		/* Pointer to data buffer.  If this structure is supplied by a callback function, this pointer 
		 * must not be retained; the data should be copied to an application buffer for any processing
		 * after the callback returns. */
		public final byte[]              Data;
		
		/* Data Length (in bytes). */
		public final long		         DataLength;
		
		/* Standard Format 
		 * (ISO 19794-2:2005, ISO 19794-4:2005, ISO 19794-2:2011, ISO 19794-4:2011, ANSI/INCITS 378:2004, ANSI/INCITS 381:2004) */
		StandardFormat     Format;
		
		protected StandardFormatData(byte[] Data, long DataLength, int Format)
		{
			this.Data = Data;
			this.DataLength = DataLength;
			this.Format = StandardFormat.fromCode(Format);
		}
	}
	
	/**
     * Image formats.
     */
    public static enum ImageFormat
    {
        NO_BIT_PACKING(0),
        BIT_PACKED(1),
        WSQ(2),
        JPEG_LOSSY(3),
        JPEG2000_LOSSY(4),
        JPEG2000_LOSSLESS(5),
        PNG(6);

        /* Native value for enumeration. */
        private final int code;

        ImageFormat(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        public static ImageFormat fromCode(int code)
        {
            for (ImageFormat t : ImageFormat.values())
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
     * Impression types.
     */
    public static enum ImpressionType
    {
    	LIVE_SCAN_PLAIN(0),
    	LIVE_SCAN_ROLLED(1),
    	NONLIVE_SCAN_PLAIN(2),
    	NONLIVE_SCAN_ROLLED(3),
    	LATENT_IMPRESSION(4),
    	LATENT_TRACING(5),
    	LATENT_PHOTO(6),
    	LATENT_LIFT(7),
    	LIVE_SCAN_SWIPE(8),
    	LIVE_SCAN_VERTICAL_ROLL(9),
    	LIVE_SCAN_PALM(10),
    	NONLIVE_SCAN_PALM(11),
    	LATENT_PALM_IMPRESSION(12),
    	LATENT_PALM_TRACING(13),
    	LATENT_PALM_PHOTO(14),
    	LATENT_PALM_LIFT(15),
    	LIVE_SCAN_OPTICAL_CONTRCTLESS_PLAIN(24),
    	OTHER(28),
    	UNKNOWN(29);

        /* Native value for enumeration. */
        private final int code;

        ImpressionType(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        public static ImpressionType fromCode(int code)
        {
            for (ImpressionType t : ImpressionType.values())
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
     * Finger positions.
     */
    public static enum FingerPosition
    {
    	UNKNOWN(0),
    	RIGHT_THUMB(1),
    	RIGHT_INDEX_FINGER(2),
    	RIGHT_MIDDLE_FINGER(3),
    	RIGHT_RING_FINGER(4),
    	RIGHT_LITTLE_FINGER(5),
    	LEFT_THUMB(6),
    	LEFT_INDEX_FINGER(7),
    	LEFT_MIDDLE_FINGER(8),
    	LEFT_RING_FINGER(9),
    	LEFT_LITTLE_FINGER(10),
    	PLAIN_RIGHT_FOUR_FINGERS(13),
    	PLAIN_LEFT_FOUR_FINGERS(14),
    	PLAIN_THUMBS(15),
    	UNKNOWN_PALM(20),
    	RIGHT_FULL_PALM(21),
    	RIGHT_WRITERS_PALM(22),
    	LEFT_FULL_PALM(23),
    	LEFT_WRITERS_PALM(24),
    	RIGHT_LOWER_PALM(25),
    	RIGHT_UPPER_PALM(26),
    	LEFT_LOWER_PALM(27),
    	LEFT_UPPER_PALM(28),
    	RIGHT_OTHER(29),
    	LEFT_OTHER(30),
    	RIGHT_INTERDIGITAL(31),
    	RIGHT_THENAR(32),
    	RIGHT_HYPOTHENAR(33),
    	LEFT_INTERDIGITAL(34),
    	LEFT_THENAR(35),
    	LEFT_HYPOTHENAR(36),
    	RIGHT_INDEX_AND_MIDDLE(40),
    	RIGHT_MIDDLE_AND_RING(41),
    	RIGHT_RING_AND_LITTLE(42),
    	LEFT_INDEX_AND_MIDDLE(43),
    	LEFT_MIDDLE_AND_RING(44),
    	LEFT_RING_AND_LITTLE(45),
    	RIGHT_INDEX_AND_LEFT_INDEX(46),
    	RIGHT_INDEX_AND_MIDDLE_AND_RING(47),
    	RIGHT_MIDDLE_AND_RING_AND_LITTLE(48),
    	LEFT_INDEX_AND_MIDDLE_AND_RING(49),
    	LEFT_MIDDLE_AND_RING_AND_LITTLE(50);

        /* Native value for enumeration. */
        private final int code;

        FingerPosition(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        public static FingerPosition fromCode(int code)
        {
            for (FingerPosition t : FingerPosition.values())
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
     * Capture device technology ID.
     */
    public static enum CaptureDeviceTechId
    {
    	UNKNOWN_OR_UNSPECIFIED(0),
    	WHITE_LIGHT_OPTICAL_TIR(1),
    	WHITE_LIGHT_OPTICAL_DIRECT_VIEW_ON_PLATEN(2),
    	WHITE_LIGHT_OPTICAL_TOUCHLESS(3),
    	MONOCHROMATIC_VISIBLE_OPTICAL_TIR(4),
    	MONOCHROMATIC_VISIBLE_OPTICAL_DIRECT_VIEW_ON_PLATEN(5),
    	MONOCHROMATIC_VISIBLE_OPTICAL_TOUCHLESS(6),
    	MONOCHROMATIC_IR_OPTICAL_TIR(7),
    	MONOCHROMATIC_IR_OPTICAL_DIRECT_VIEW_ON_PLATEN(8),
    	MONOCHROMATIC_IR_OPTICAL_TOUCHLESS(9),
    	MULTISPECTRAL_OPTICAL_TIR(10),
    	MULTISPECTRAL_OPTICAL_DIRECT_VIEW_ON_PLATEN(11),
    	MULTISPECTRAL_OPTICAL_TOUCHLESS(12),
    	ELECTRO_LUMINESCENT(13),
    	SEMICONDUCTOR_CAPACITIVE(14),
    	SEMICONDUCTOR_RF(15),
    	SEMICONDUCTOR_THEMAL(16),
    	PRESSURE_SENSITIVE(17),
    	ULTRASOUND(18),
    	MECHANICAL(19),
    	GLASS_FIBER(20);

        /* Native value for enumeration. */
        private final int code;

        CaptureDeviceTechId(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        public static CaptureDeviceTechId fromCode(int code)
        {
            for (CaptureDeviceTechId t : CaptureDeviceTechId.values())
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
     * StandardFormat (IBSM_StandardFormat)
     */
    public static enum StandardFormat
    {
    	STANDARD_FORMAT_ISO_19794_2_2005(0),
    	STANDARD_FORMAT_ISO_19794_4_2005(1),
    	STANDARD_FORMAT_ISO_19794_2_2011(2),
    	STANDARD_FORMAT_ISO_19794_4_2011(3),
    	STANDARD_FORMAT_ANSI_INCITS_378_2004(4),
    	STANDARD_FORMAT_ANSI_INCITS_381_2004(5);

        /* Native value for enumeration. */
        private final int code;

        StandardFormat(int code)
        {
            this.code = code;
        }

        /* Find Java object from native value. */
        public static StandardFormat fromCode(int code)
        {
            for (StandardFormat t : StandardFormat.values())
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

    /* *********************************************************************************************
     * PRIVATE INTERFACE
     ******************************************************************************************** */

    /*
     *  Protected default constructor to prevent external instantiation.
     */
    private IBCommon()
    {

    }
}
