//
// Created by admin on 2025/7/19.
//
#include "FileUtil.h"

#include <jni.h>
#include <string>
#include <dirent.h>
#include <unistd.h>
#include <sys/stat.h>

// 递归删除文件夹
bool delete_directory(const char *path) {
    DIR *dir = opendir(path);
    if (dir == nullptr) {
        return false;
    }

    struct dirent *entry;
    while ((entry = readdir(dir)) != nullptr) {
        // 跳过 "." 和 ".."
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) {
            continue;
        }

        // 构建子路径
        std::string sub_path = std::string(path) + "/" + entry->d_name;

        if (entry->d_type == DT_DIR) {
            // 递归删除子文件夹
            if (!delete_directory(sub_path.c_str())) {
                closedir(dir);
                return false;
            }
        } else {
            // 删除文件
            if (unlink(sub_path.c_str())) {
                closedir(dir);
                return false;
            }
        }
    }

    closedir(dir);

    // 删除空文件夹
    return rmdir(path) == 0;
}


