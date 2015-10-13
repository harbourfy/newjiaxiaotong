/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.jiaxiaotong.im.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
//	public static String getThumbnailImagePath(String imagePath) {
//		String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
//		path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
//		EMLog.d("msg", "original image path:" + imagePath);
//		EMLog.d("msg", "thum image path:" + path);
//		return path;
//	}
	
	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;
		
	}
	
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtil.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }

	private static final String TAG = "ImageUtils";

	public static void loadImage(Activity activity, ImageView imageView, Uri uri) {
		if (uri != null && !uri.toString().startsWith("content://")) {
			return;
		}
		try {
			// MediaStore
			String[] pojo = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.managedQuery(uri, pojo, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				ContentResolver cr = activity.getContentResolver();
				int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				String path = cursor.getString(colunm_index);

				if (path.endsWith(".jpg") || path.endsWith(".png")) {
					Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
					// imageView.setImageBitmap(getBitmap(path, 100, 200));
					imageView.setImageBitmap(getCompredBitmap(path));
				} else {
					Toast.makeText(activity, "Please select image...", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getCompredBitmap(String srcPath) {
		return compressDef(getResizeBitmap(srcPath));
	}

	/**
	 * get bitmap compressed to 100kb
	 *
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getCompredDefBitmap(Bitmap bitmap) {
		return getCompredBitmap(bitmap, 100);
	}

	/**
	 * get bitmap compressed
	 *
	 * @param bitmap
	 * @param target_size
	 *            unit is kb
	 * @return
	 */
	public static Bitmap getCompredBitmap(Bitmap bitmap, int target_size) {
		return compress(bitmap, target_size);
	}

	private static Bitmap compressDef(Bitmap bitmap) {
		return compress(bitmap, 100);
	}

	/**
	 * The cycle compression specify meet the specified target size(100kb)
	 *
	 * @param bitmap
	 * @param target_size
	 *            unit is kb
	 * @return
	 */
	private static Bitmap compress(Bitmap bitmap, int target_size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int quality = 100;
		int length = baos.toByteArray().length / 1024;
		// The cycle compression specify meet the specified size
		while (length > target_size) {
			baos.reset();
			quality -= 10;
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			length = baos.toByteArray().length / 1024;
		}
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		byte[] data = baos.toByteArray();
		int cp_length = data.length;
		Log.i(TAG, "compressed image length = " + cp_length / 1024 + " kb");
		return BitmapFactory.decodeByteArray(data, 0, cp_length);
	}

	/**
	 * change image size: width and height
	 *
	 * @param options
	 * @return
	 */
	private static int getSimpleSize(BitmapFactory.Options options) {
		int w = options.outWidth;
		int h = options.outHeight;
		float hh = 800f;
		float ww = 480f;
		int scale = 1;
		if (w > h && w > ww) {
			scale = (int) (options.outWidth / ww);
		} else if (w < h && h > hh) {
			scale = (int) (options.outHeight / hh);
		}
		if (scale <= 0)
			scale = 1;
		return scale;
	}

	public static Bitmap getResizeBitmap(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// start parse image only width and height, set
		// options.inJustDecodeBounds to true
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inSampleSize = getSimpleSize(newOpts);
		newOpts.inJustDecodeBounds = false;
		// restart parse image all info, set options.inJustDecodeBounds as true
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}


	public static void transImage(String fromFile, String toFile, int width, int height, int quality) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
			int bitmapWidth = bitmap.getWidth();
			int bitmapHeight = bitmap.getHeight();
			// scaling proportion
			float scaleWidth = (float) width / bitmapWidth;
			float scaleHeight = (float) height / bitmapHeight;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// resizeBitmap
			Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
			// save file
			File myCaptureFile = new File(toFile);
			FileOutputStream out = new FileOutputStream(myCaptureFile);
			if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
				out.flush();
				out.close();
			}
			// Release memory resources
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			if (!resizeBitmap.isRecycled()) {
				resizeBitmap.recycle();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 直接载入图片
	 *
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * get resized (kb) bitmap
	 *
	 * @param path
	 *            unit is kb
	 * @return
	 */
	public static Bitmap getBitmap(String path, int inSampleSize) {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inSampleSize = inSampleSize;
		return BitmapFactory.decodeFile(path, op);
	}

	/**
	 * 按寬高壓縮載入圖片
	 *
	 * @param path
	 * @param width
	 * @param heigh
	 * @return
	 */
	public static Bitmap getBitmap(String path, int width, int heigh) {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, op);
		int xScale = op.outWidth / width;
		int yScale = op.outHeight / heigh;
		op.inSampleSize = xScale > yScale ? xScale : yScale;
		op.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, op);
		return bmp;
	}

	public static Bitmap getBitmap2(String path, int width, int height) {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(path, op);

		// 编码后bitmap的宽高,bitmap除以屏幕宽度得到压缩比
		int widthRatio = (int) Math.ceil(op.outWidth / (float) width);
		int heightRatio = (int) Math.ceil(op.outHeight / (float) height);

		if (widthRatio > 1 && heightRatio > 1) {
			if (widthRatio > heightRatio) {
				// 压缩到原来的(1/widthRatios)
				op.inSampleSize = widthRatio;
			} else {
				op.inSampleSize = heightRatio;
			}
		}
		op.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, op);
		return bmp;
	}
}
