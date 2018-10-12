package com.utils;

import java.text.DecimalFormat;

public class FileSizeUtils {

	private static DecimalFormat decimalFomat = new DecimalFormat(".00");

	/**
	 * 根据long的大小自动的输出传输速度（速度单位自动匹配）
	 * @param byt 传输的字节数
	 * @return
	 */
	public static String getSpeed(long byt) {

		if (byt < 1024) {
			return byt + "B/s";
		} else if (byt < 1024 * 1024) {
			return byt / 1024 + "KB/s";
		} else {
			//算出来的是以MB为单位的
			double tempSpeed = byt / 1024 / (1024 * 1.0);
			String format = decimalFomat.format(tempSpeed);
			return format + "MB/s";
		}

	}

	public static String getSize(long byt) {

		if (byt < 1024) {
			return byt + "B";
		} else if (byt < 1024 * 1024) {
			return byt / 1024 + "KB";
		} else {
			//算出来的是以MB为单位的
			double tempSpeed = byt / 1024 / 1024;
			return tempSpeed + "MB";
		}

	}
}
