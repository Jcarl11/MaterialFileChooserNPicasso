package com.example.windows.materialfilechoosernpicasso;

import java.io.File;

/**
 * Created by Windows on 6/9/2018.
 */

public class ImagesEntity
{
    private String title;
    private String image_url;

    public ImagesEntity()
    {

    }
    public ImagesEntity(String title, String image_url) {
        this.title = title;
        this.image_url = image_url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getImage_url()
    {
        return image_url;
    }

    public File getImage_url_file()
    {
        return new File(image_url);
    }
}
