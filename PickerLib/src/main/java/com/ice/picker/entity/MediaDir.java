package com.ice.picker.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaDir {
    public String path;
    public MediaType type;
    public List<String> files;
    public String dirName;

    public MediaDir(String path, MediaType type) {
        this.path = path;
        this.type = type;
        files = new ArrayList<>();
        dirName = new File(path).getName();
    }


}
