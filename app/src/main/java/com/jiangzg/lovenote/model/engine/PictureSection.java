package com.jiangzg.lovenote.model.engine;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.jiangzg.lovenote.model.entity.Picture;

/**
 * Created by JZG on 2019/3/9.
 */
public class PictureSection extends SectionEntity<Picture> {

    public PictureSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

}
