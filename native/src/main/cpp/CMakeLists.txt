cmake_minimum_required(VERSION 3.22.1)
project("miko")

add_library(${CMAKE_PROJECT_NAME} SHARED
        c_init.cpp
        miko_core/miko_core_bridge.cpp
        util/JniUtils.cpp)

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR})

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)