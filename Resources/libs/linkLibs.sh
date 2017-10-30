#!/bin/bash
MBV_SDK="/home/kyriazis/install/lib/"
ln -s $MBV_SDK/libAcquisition.so 
ln -s $MBV_SDK/libCore.so 
ln -s $MBV_SDK/libDecoding.so 
ln -s $MBV_SDK/libHandTrackerComm.so
ln -s $MBV_SDK/libHandTrackerLib.so
ln -s $MBV_SDK/libHandTrackerPoll.so
ln -s $MBV_SDK/libLibraries.so
ln -s $MBV_SDK/libOpenMeshCore.so.2.4
ln -s $MBV_SDK/libOpenNI.so
ln -s $MBV_SDK/libOptimization.so
ln -s $MBV_SDK/libParticleFilter.so 
ln -s $MBV_SDK/libRendering.so
ln -s /home/kyriazis/projects/HandTrackerJNI/build/libHandTrackerJNI.so
ln -s /home/kyriazis/projects/HandTrackerJNI/build/HandTracker.jar

RGBDACQUISITION="/home/kyriazis/Documents/Programming/RGBDAcquisition"


ln -s $RGBDACQUISITION/acquisition/libRGBDAcquisition.so
ln -s $RGBDACQUISITION/openni2_acquisition_shared_library/libOpenNI2Acquisition.so
ln -s $RGBDACQUISITION/openni1_acquisition_shared_library/libOpenNI1Acquisition.so
ln -s $RGBDACQUISITION/template_acquisition_shared_library/libTemplateAcquisition.so
ln -s $RGBDACQUISITION/opengl_acquisition_shared_library/libOpenGLAcquisition.so
ln -s $RGBDACQUISITION/editor/Editor
ln -s $RGBDACQUISITION/viewer/Viewer

ln -s $RGBDACQUISITION/3dparty/OpenNI2/Bin/x64-Release/OpenNI2/
ln -s $RGBDACQUISITION/3dparty/OpenNI2/Bin/x64-Release/libOpenNI2.so
ln -s $RGBDACQUISITION/3dparty/OpenNI2/Config/OpenNI.ini


ln -s $RGBDACQUISITION/3dparty/OpenNI/Platform/Linux/Bin/x64-Release/libOpenNI.so
ln -s $RGBDACQUISITION/3dparty/OpenNI/Platform/Linux/Bin/x64-Release/libnimCodecs.so
ln -s $RGBDACQUISITION/3dparty/OpenNI/Platform/Linux/Bin/x64-Release/libnimMockNodes.so
ln -s $RGBDACQUISITION/3dparty/OpenNI/Platform/Linux/Bin/x64-Release/libnimRecorder.so
ln -s $RGBDACQUISITION/3dparty/OpenNI/Platform/Linux/Bin/x64-Release/libOpenNI.jni.so


exit 0 
