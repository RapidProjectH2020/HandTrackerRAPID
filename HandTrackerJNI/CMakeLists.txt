project (HandTrackerJNI)

cmake_minimum_required (VERSION 2.8)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)
include(UseJava)

find_path(HandTrackerCMAKE FindHandTracker.cmake)
list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake ${MBV_FIND_PATH} ${MBV_APPS_FIND_PATH} ${HandTrackerCMAKE})
find_package(HandTracker COMPONENTS Lib)


add_jar(HandTracker eu/project/rapid/handtracker/HandTrackerJNI.java
   ENTRY_POINT eu.project.rapid.handtracker.HandTrackerJNI)
get_target_property(_jarFile HandTracker JAR_FILE)
get_target_property(_classDir HandTracker CLASSDIR)

set(_stubDir "${CMAKE_CURRENT_BINARY_DIR}")
add_custom_command(
    OUTPUT eu_project_rapid_handtracker_HandTrackerJNI.h
    COMMAND ${Java_JAVAH_EXECUTABLE}
    -classpath ${_classDir}
        -d ${_stubDir}
        -jni eu.project.rapid.handtracker.HandTrackerJNI
        DEPENDS eu/project/rapid/handtracker/HandTrackerJNI.java
)

include_directories(${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2} ${_classDir} ${_stubDir})
include_directories(${MBV_INCLUDE_DIR})
include_directories(${HandTracker_INCLUDE_DIR})

add_definitions(-DBOOST_LOG_DYN_LINK)
add_library(HandTrackerJNI MODULE eu_project_rapid_handtracker_HandTrackerJNI.h HandTracker.cpp JNIHelper.h HandTrackerThread.h HandTrackerThread.cpp)
#set_target_properties(HandTrackerJNI PROPERTIES SUFFIX ".jnilib")
target_link_libraries(HandTrackerJNI ${JNI_LIBRARIES} ${MBV_LIBRARIES} ${HandTracker_LIBRARIES}) 

install(TARGETS HandTrackerJNI
    LIBRARY DESTINATION bin)
install(FILES ${_jarFile} DESTINATION bin)
