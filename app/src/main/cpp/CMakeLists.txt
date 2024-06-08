cmake_minimum_required(VERSION 3.22.1)

set(MODULE_NAME AssetSideLoader)

project(${MODULE_NAME})

if(CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(CMAKE_BUILD_TYPE "Release")
endif()

set(C_FLAGS "${C_FLAGS} -O2 -fvisibility=hidden -fvisibility-inlines-hidden")
set(LINKER_FLAGS "${LINKER_FLAGS} -Wl,-exclude-libs,ALL -Wl,--gc-sections -Wl,--strip-all")

include_directories(
        dobby/include
)

add_subdirectory(dobby dobby)

find_package(xdl REQUIRED CONFIG)

set(CMAKE_CXX_STANDARD 20)

set(CXX_FLAGS "${CXX_FLAGS} -fno-exceptions -fno-rtti")

add_library(${MODULE_NAME} SHARED
        main.cpp
        )

target_link_libraries(${MODULE_NAME} dobby xdl::xdl)

add_custom_command(TARGET ${MODULE_NAME} POST_BUILD
        COMMAND ${CMAKE_STRIP} --strip-all --remove-section=.comment "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/lib${MODULE_NAME}.so")