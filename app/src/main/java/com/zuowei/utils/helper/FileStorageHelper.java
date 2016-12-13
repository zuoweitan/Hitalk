package com.zuowei.utils.helper;

import com.avos.avoscloud.AVFile;
import com.lzy.ninegrid.ImageInfo;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.RxjavaUtils;
import com.zuowei.utils.common.TagUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by zuowei on 16-8-17.
 */
public class FileStorageHelper {
    private static FileStorageHelper sInstance;
    public static FileStorageHelper getInstance(){
        if (sInstance == null) {
            synchronized (FileStorageHelper.class){
                if (sInstance == null) {
                    sInstance = new FileStorageHelper();
                }
            }
        }
        return sInstance;
    }

    private FileStorageHelper(){
    }

    public void uploadImagesAsync(final List<String> imagesPaths, final DoneCallback<ImageInfo> callback){
        RxjavaUtils.AsyncJob(new Runnable() {
            @Override
            public void run() {
                if (imagesPaths != null) {
                    List<ImageInfo> imageInfos = new ArrayList<>();
                    for (String imagesPath : imagesPaths) {
                        String imageType = getImageType(imagesPath);
                        if (imageType != null){
                            try {
                                AVFile avFile = AVFile.withAbsoluteLocalPath(getImageName(imageType), imagesPath);
                                final String thumbNailUrl = avFile.getThumbnailUrl(true,100,100);
                                avFile.save();
                                ImageInfo imageInfo = new ImageInfo();
                                imageInfo.setThumbnailUrl(thumbNailUrl);
                                imageInfo.setBigImageUrl(avFile.getUrl());
                                imageInfos.add(imageInfo);
                            }catch (Exception e){
                                NLog.e(TagUtil.makeTag(FileStorageHelper.class),e);
                            }
                        }
                    }

                    callback.done(imageInfos,null);
                }
            }
        });
    }

    public Task<List<ImageInfo>> uploadImagesTask(final List<String> imagesPaths){
        return Task.forResult(imagesPaths).continueWithTask(new Continuation<List<String>, Task<List<ImageInfo>>>() {
            @Override
            public Task<List<ImageInfo>> then(Task<List<String>> task) throws Exception {
                Exception e = task.getError();
                NLog.i(TagUtil.makeTag(getClass()),"uploadImagesTask e = "+e);
                if (e != null) {
                    return Task.forError(e);
                }

                List<String> ipaths = task.getResult();
                List<ImageInfo> imageInfos = new ArrayList<>();
                if (ipaths != null) {
                    for (String ipath : ipaths) {
                        String imageType = getImageType(ipath);
                        if (imageType != null){
                            try {
                                AVFile avFile = AVFile.withAbsoluteLocalPath(getImageName(imageType), ipath);
                                AvObjectSaveHelper.runSaveFileSync(avFile);
                                String thumbNailUrl = avFile.getThumbnailUrl(true,100,100);
                                ImageInfo imageInfo = new ImageInfo();
                                imageInfo.setThumbnailUrl(thumbNailUrl);
                                imageInfo.setBigImageUrl(avFile.getUrl());
                                imageInfos.add(imageInfo);
                            }catch (Exception exception){
                                return Task.forError(exception);
                            }
                        }
                    }
                }
                return Task.forResult(imageInfos);
            }
        },Task.BACKGROUND_EXECUTOR);
    }

    public static List<AVFile> makeAvFileList(List<String> imagesPaths){
        List<AVFile> avFiles = new ArrayList<>();
        if (imagesPaths != null){
            for (String imagesPath : imagesPaths) {
                String imageType = getImageType(imagesPath);
                if (imageType != null) {
                    AVFile avFile = null;
                    try {
                        avFile = AVFile.withAbsoluteLocalPath(getImageName(imageType), imagesPath);
                        avFile.getThumbnailUrl(true,100,100);
                        avFiles.add(avFile);
                    } catch (FileNotFoundException e) {
                        NLog.i(TagUtil.makeTag(FileStorageHelper.class),e);
                    }
                }
            }
        }
        return avFiles;
    }

    public List<ImageInfo> uploadImages (final List<String> imagesPaths){
        if (imagesPaths != null) {
            List<ImageInfo> imageInfos = new ArrayList<>();
            for (String imagesPath : imagesPaths) {
                String imageType = getImageType(imagesPath);
                if (imageType != null){
                    try {
                        AVFile avFile = AVFile.withAbsoluteLocalPath(getImageName(imageType), imagesPath);
                        AvObjectSaveHelper.runSaveFileSync(avFile);
                        ImageInfo imageInfo = new ImageInfo();
                        imageInfo.setThumbnailUrl(avFile.getThumbnailUrl(true,100,100));
                        imageInfo.setBigImageUrl(avFile.getUrl());
                        imageInfos.add(imageInfo);
                    }catch (Exception e){
                        NLog.e(TagUtil.makeTag(FileStorageHelper.class),e);
                    }
                }
            }
            return imageInfos;
        }
        return null;
    }

    private static String getImageName(String imageType) {
        return "hitalk_bn"+imageType;
    }

    private static String getImageType(String imagesPath) {
        Pattern pattern = Pattern.compile(".*(\\.jpg|\\.png|\\.gif|\\.bmp|\\.jpeg|\\.webp)");
        Matcher matcher = pattern.matcher(imagesPath.toLowerCase());
        if (matcher.find() && matcher.groupCount() > 0){
            return matcher.group(1);
        }
        return null;
    }
}
