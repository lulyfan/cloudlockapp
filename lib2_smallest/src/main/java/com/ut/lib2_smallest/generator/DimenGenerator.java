package com.ut.lib2_smallest.generator;


import com.ut.lib2_smallest.constants.DimenTypes;
import com.ut.lib2_smallest.entity.Dimen;
import com.ut.lib2_smallest.pull.PullTool;
import com.ut.lib2_smallest.utils.MakeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;


public class DimenGenerator {

    /**
     * 设计稿尺寸(将自己设计师的设计稿的宽度填入)
     */
    private static final int DESIGN_WIDTH = 414;

    /**
     * 设计稿的高度  （将自己设计师的设计稿的高度填入）
     */
    private static final int DESIGN_HEIGHT = 736;

    public static void main(String[] args) throws Exception {
        int smallest = DESIGN_WIDTH > DESIGN_HEIGHT ? DESIGN_HEIGHT : DESIGN_WIDTH;  //     求得最小宽度
        DimenTypes[] values = DimenTypes.values();
        File file = new File("");
        File srcfile = new File(file.getAbsolutePath() + "/lib2_smallest/dimen/dimens.xml");

        List<Dimen> list = PullTool.parserXml(new FileInputStream(srcfile));
        System.out.println(list);
        for (DimenTypes value : values) {
            MakeUtils.makeAll(smallest, value, file.getAbsolutePath() + "/lib2_smallest/dimens", list);
        }

    }

}
