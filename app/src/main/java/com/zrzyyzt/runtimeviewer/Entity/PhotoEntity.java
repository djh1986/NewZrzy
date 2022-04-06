package com.zrzyyzt.runtimeviewer.Entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "photo_info")
public class PhotoEntity {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    private Long id;

    private String photoName;

    private String imagePath;

    private String takeTime;

    private boolean isExist;



    @Generated(hash = 1082754900)
    public PhotoEntity(Long id, String photoName, String imagePath, String takeTime,
            boolean isExist) {
        this.id = id;
        this.photoName = photoName;
        this.imagePath = imagePath;
        this.takeTime = takeTime;
        this.isExist = isExist;
    }

    @Generated(hash = 1889335700)
    public PhotoEntity() {
    }

   

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoName() {
        return this.photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTakeTime() {
        return this.takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public boolean getIsExist() {
        return this.isExist;
    }

    public void setIsExist(boolean isExist) {
        this.isExist = isExist;
    }

}
