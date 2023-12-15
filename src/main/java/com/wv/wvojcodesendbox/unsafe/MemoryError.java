package com.wv.wvojcodesendbox.unsafe;

import java.util.ArrayList;

/**
 * @author wv
 * @version V1.0
 * @date 2023/12/12 18:41
 */
public class MemoryError {

    public static void main(String[] args) {
        ArrayList<Byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new Byte[100000]);
        }
    }
}
