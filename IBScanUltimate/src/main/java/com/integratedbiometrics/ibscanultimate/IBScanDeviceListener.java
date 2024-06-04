/* *************************************************************************************************
 * IBScanDeviceListener.java
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
 *     2013/10/18  Added method for extended result information.
 *     2015/12/11  Added method for detecting the key button of device was pressed.
 ************************************************************************************************ */

package com.integratedbiometrics.ibscanultimate;

import com.integratedbiometrics.ibscanultimate.IBScanDevice.FingerCountState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.FingerQualityState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageData;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.ImageType;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.PlatenState;
import com.integratedbiometrics.ibscanultimate.IBScanDevice.SegmentPosition;

/**
 * Listener for scan events on a <code>IBScanDevice</code>. This listener should be registered by an
 * application using the <code>setScanDeviceListener(IBScanDeviceListener)</code> method.
 * <p>
 * Most of these events occur after <code>beginCaptureImage()</code> has been called.  If fingers 
 * are touching the platen when the capture is begun, <code>deviceImagePreviewAvailable()</code>
 * will be called immediately and again once no fingers are touching.  Periodically, until a final 
 * image is achieved, <code>deviceImagePreviewAvailable()</code> will return the current scanner 
 * image.  Changes in the quantity and quality of finger presses will result in 
 * <code>deviceFingerCountChanged()</code> or <code>deviceFingerQualityChanged()</code> calls.  If 
 * the selected scan type is a rolled finger scan, then <code>deviceAcquisitionBegun()</code>
 * will be called when a flat finger scan has been acquired and the user should begin rolling his 
 * or her finger to the left; when the left-roll is complete, <code>deviceAcquisitionCompleted()</code>
 * will be called, and the user should begin rolling back toward the right.  When a quality scan 
 * with the correct number of fingers (and a full finger roller, if applicable) is available or 
 * <code>captureImageManually()</code> is called, <code>deviceImageResultAvailable()</code> or
 * <code>deviceImageResultExtendedAvailable()</code> will supply a final scan image to the application.
 */
public interface IBScanDeviceListener
{
    /**
     * Communication break notification.  This method is called when communication with the device
     * is broken while a capture is in progress.
     * 
     * @param device  device with which communication has been broken
     */
    public void deviceCommunicationBroken(IBScanDevice device);

    /**
     * Image preview available notification.
     * 
     * @param device  device for which preview image is available
     * @param image   preview image
     */
    public void deviceImagePreviewAvailable(IBScanDevice device, ImageData image);

    /**
     * Finger count change notification.
     * 
     * @param device       device for which finger count has changed
     * @param fingerState  state of finger count
     */
    public void deviceFingerCountChanged(IBScanDevice device, FingerCountState fingerState);

    /**
     * Finger quality change notification.
     * 
     * @param device           device for which finger quality has changed
     * @param fingerQualities  array of qualities for fingers
    */
    public void deviceFingerQualityChanged(IBScanDevice device, FingerQualityState[] fingerQualities);

    /**
     * Device roll acquisition begun notification.  If an image type of <code>ROLL_SINGLE_FINGER</code>
     * is being captured, this method will be called when a flat-finger scan has been acquired and
     * the user should begin rolling his or her finger to the left.
     * 
     * @param device     device for which acquisition has begun
     * @param imageType  type of image
    */
    public void deviceAcquisitionBegun(IBScanDevice device, ImageType imageType);

    /**
     * Device roll acquisition complete notification.  If an image type of <code>ROLL_SINGLE_FINGER</code>
     * is being captured, this method will be called when the left-roll has been completed and the
     * user should begin roller his or her finger to the left to capture the right side of the 
     * finger.
     * 
     * @param device     device for which acquisition has completed
     * @param imageType  type of image
     */
    public void deviceAcquisitionCompleted(IBScanDevice device, ImageType imageType);

    /**
     * Result image available notification.
     * 
     * @param device           device for which result image is available
     * @param image            result image data
     * @param imageType        type of image
     * @param splitImageArray  array of split result image data
     */
    public void deviceImageResultAvailable(IBScanDevice device, ImageData image,
    		ImageType imageType, ImageData[] splitImageArray);

    /**
     * Result extended image available notification.
     * 
     * @param device                device for which result image is available
     * @param imageStatus           status from result image acquisition
     * @param image                 result image data
     * @param imageType             type of image
     * @param detectedFingerCount   detected finger count
     * @param segmentImageArray     array of segment result image data
     * @param segmentPositionArray  array of segment position data
     */
    public void deviceImageResultExtendedAvailable(IBScanDevice device, IBScanException imageStatus,
    		ImageData image, ImageType imageType, int detectedFingerCount, ImageData[] segmentImageArray,
    		SegmentPosition[] segmentPositionArray);

    /**
     * Platen state changed notification.  If fingers are touching the platen when a capture is 
     * begun, this method will be called immediately and once again when no fingers are touching.
     * Subsequent state changes from touches will not be notified.
     * 
     * @param device       device for which platen state has changed
     * @param platenState  new platen state
     */
    public void devicePlatenStateChanged(IBScanDevice device, PlatenState platenState);

    /**
     * Warning message notification.
     * 
     * @param device   device for which warning was received
     * @param warning  warning received from device
     */
    public void deviceWarningReceived(IBScanDevice device, IBScanException warning);
     /**
     * Key button notification.
     * 
     * @param device             device for which key button was pressed
     * @param pressedKeyButtons  The key button index which is pressed
     */
    public void devicePressedKeyButtons(IBScanDevice device, int pressedKeyButtons);
}
