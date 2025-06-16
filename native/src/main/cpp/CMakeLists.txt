cmake_minimum_required(VERSION 3.22.1)
project("miko")

add_library(${CMAKE_PROJECT_NAME} SHARED
        native_hook_api.h,
        c_init.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)