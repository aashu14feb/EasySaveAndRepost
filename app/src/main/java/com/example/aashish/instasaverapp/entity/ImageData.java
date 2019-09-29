package com.example.aashish.instasaverapp.entity;

import com.example.aashish.instasaverapp.database.AppDataBase;
import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.PrimaryKey;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.query.Select;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Table(name = "Items", database = AppDataBase.class)
public class ImageData extends Model implements Serializable {

    @PrimaryKey
    private Long timestamp;

    @Column(name = "Name")
    public String name;

    @Column(name = "Description")
    public String description;

    @Column(name = "Url")
    public String url;

    @Column(name = "Is_Video")
    public boolean is_Video;

    @Column(name = "Width")
    public int width;

    @Column(name = "Height")
    public int height;

    @Column(name = "Video_Url")
    public String video_url;

    @Column(name = "ProfileName")
    public String profileName;

    @Column(name = "ProfileId")
    public String profileId;

    @Column(name = "ProfileUrl")
    public String profileUrl;

    @Column(name = "Likes")
    public int likes;


    public static ImageData getImageLastDownload() {
        return imageLastDownload;
    }

    public static void setImageLastDownload(ImageData imageLastDownload) {
        ImageData.imageLastDownload = imageLastDownload;
    }

    public static ImageData imageLastDownload = null;
    private static List<ImageData> allImageList = new ArrayList<>();
    private static List<ImageData> allProfileList = new ArrayList<>();

    public static List<ImageData> getAllImageList() {
        Collections.sort(allImageList, (lhs, rhs) -> Long.valueOf(rhs.timestamp).compareTo(Long.valueOf(lhs.timestamp)));
        return allImageList;
    }

    public static List<ImageData> getAllProfileList() {
        return allProfileList;
    }

    public static List<ImageData> init() {
        allImageList = Select.from(ImageData.class).fetch();
        if (allImageList == null) {
            allImageList = new ArrayList<>();
        }
        getAllCategory(allImageList);
        Collections.reverse(allImageList);
        return allImageList;
    }

    public ImageData() {
    }

    public ImageData(String name, String description, String url, boolean is_Video, String video_url, int width, int height, String profileId, String profileName, String profileUrl, int likes) {

        this.timestamp = System.currentTimeMillis();
        this.name = name;
        this.description = description;
        this.url = url;
        this.is_Video = is_Video;
        this.video_url = video_url;
        this.width = width;
        this.height = height;
        this.profileId = profileId;
        this.profileName = profileName;
        this.profileUrl = profileUrl;
        this.likes = likes;

        if (!isExists(url)) {
            save();
            allImageList.add(this);
        }
        getAllCategory(allImageList);

    }

    public List<ImageData> sortAllFromCategory(String profileId) {
        List<ImageData> allFromCat = new ArrayList<>();
        for (ImageData imagedata : allImageList) {
            if (imagedata.profileId.equals(profileId))
                allFromCat.add(imagedata);
        }
        return allFromCat;
    }

    public boolean isExists(String url) {
        for (ImageData imagedata : allImageList) {
            if (imagedata.url.equals(url))
                return true;
        }
        return false;
    }

    public static void getAllCategory(List<ImageData> allImageList) {

        allProfileList.clear();
        for (ImageData imagedata : allImageList) {
            boolean exist = false;
            for (ImageData groupImages : allProfileList) {
                if (imagedata.profileId.equals(groupImages.profileId)) {
                    exist = true;
                }
            }
            if (!exist)
                allProfileList.add(imagedata);
        }
    }

    public static List<ImageData> getAllFromCategory(String catId) {
        List<ImageData> allFromCat = new ArrayList<>();
        for (ImageData imagedata : allImageList) {
            if (imagedata.profileId.equals(catId))
                allFromCat.add(imagedata);
        }
        return allFromCat;
    }

    public static void deleteSafe(ImageData imageDataToDelete) {
        Iterator<ImageData> iterator = ImageData.getAllImageList().iterator();
        while (iterator.hasNext()) {
            ImageData imageData1 = iterator.next();
            if (imageData1.name.equals(imageDataToDelete.name)) {
                ImageData.getAllImageList().remove(imageDataToDelete);
                imageDataToDelete.delete();
                break;
                // To Bad
            }
        }
    }


}