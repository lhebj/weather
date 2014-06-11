package com.weather.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Random;

import javax.imageio.ImageIO;

import magick.ImageInfo;
import magick.MagickApiException;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public final class FileUtil {
	private static Log log = LogFactory.getLog(StringUtil.class);
	private static Log deleteLogicLog = LogFactory.getLog(FileUtil.class);
	public static String fileSeparator = System.getProperty("file.separator");
	

	static {
		System.setProperty("jmagick.systemclassloader", "no");
	}

	/**
	 * Check if is a file.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean checkfile(String path) {
		if (path == null) {
			log.error("file path is null");
			return false;
		}
		File file = new File(path);
		if (!file.isFile()) {
			log.error("file:" + path + " doesn't exist.");
			return false;
		}
		return true;
	}

	/**
	 * store file
	 * 
	 * @param inputstream
	 *            file content buffer
	 * @param url
	 *            file location
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void store(String url, InputStream inputstream) {
		File newFile = new File(url);
		String directoryPath = StringUtil.getDirectoryName(url);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		try {
			newFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream writter = null;
		try {
			writter = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int readed = -1;
			while ((readed = inputstream.read(buffer)) > 0) {
				writter.write(buffer, 0, readed);
			}
			writter.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inputstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void store(String url, FileItem fileItem) throws Exception {
		File file = new File(url);
		String directoryPath = StringUtil.getDirectoryName(url);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		file.createNewFile();
		fileItem.write(file);
	}
	
	/**
	 * 等比例保存图片
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	public static String storeImageByScaleSize(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		if (width == 0 && height == 0) {
			return saveRawImage(original, fileSuffix, filePath);
		} else if (height == -1) {
			if (KnxConfig.USE_IMAGEMAGICK) {
				return scaleImageWithImageMagickNoCut(original, fileSuffix, width, height, filePath);
			} else {
				return scaleImageWithJDKNoCut(original, fileSuffix, width, height, filePath);
			}
		} else {
			if (KnxConfig.USE_IMAGEMAGICK) {
				return scaleImageWithImageMagickScale(original, fileSuffix, width, height, filePath);
			} else {
				return scaleImageWithJDKScale(original, fileSuffix, width, height, filePath);
			}
		}
	}
	
	/**
	 * Using JDK AWT to scale image, if no imagemagick in environment
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	private static String scaleImageWithJDKScale(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		String directoryPath = StringUtil.getDirectoryName(filePath);
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File newFile = new File(filePath);
        if (fileSuffix.equalsIgnoreCase("gif")) {
            fileSuffix = "png";
        }
        newFile.createNewFile();

        BufferedImage rawImage = ImageIO.read(original);
        int rawHeight = rawImage.getHeight();
        int rawWidth = rawImage.getWidth();
        double standardRate = (double) width / height;
        double rawRate = (double) rawWidth / rawHeight;
        if (standardRate < rawRate) {
            height = (int) (width / rawRate);
        } else {
            width = (int) (height * rawRate);
        }
        Image result = rawImage.getScaledInstance(width, height, rawImage.SCALE_SMOOTH);
        double ratio1 = (1.0d * width) / rawWidth;
        double ratio2 = (1.0d * height) / rawHeight;
        AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio1, ratio2), null);
        result = op.filter(rawImage, null);
        ImageIO.write((BufferedImage) result, fileSuffix, newFile);
        return new Integer(width).toString() + "×" + new Integer(height).toString();
	}
	
	/**
	 * Using ImageMagick to scale image without cut
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	private static String scaleImageWithImageMagickScale(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		if (original == null) {
			return null;
		}
		String directoryPath = StringUtil.getDirectoryName(filePath);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		String originalFilePath = StringUtil.getOriginalFilePath(filePath);
		if (fileSuffix.equalsIgnoreCase("gif")) {
			fileSuffix = "png";
		}
		store(originalFilePath, original);
		try {
			ImageInfo info = new ImageInfo(originalFilePath);
			MagickImage mImage = new MagickImage(info);
			Dimension dimension = mImage.getDimension();
			int rawHeight = dimension.height;
			int rawWidth = dimension.width;
			double standardRate = (double) width / height;
	        double rawRate = (double) rawWidth / rawHeight;
	        if (standardRate < rawRate) {
	            height = (int) (width / rawRate);
	        } else {
	            width = (int) (height * rawRate);
	        }
			MagickImage scaled = mImage.scaleImage(width, height);
			scaled.setFileName(filePath);
			scaled.writeImage(info);
		} catch (MagickApiException ex) {
			ex.printStackTrace();
		} catch (MagickException ex) {
			ex.printStackTrace();
		}
		return new Integer(width).toString() + "×" + new Integer(height).toString();
	}
	
	public static String storeImageWithSpeicifiedSize(InputStream original, String fileSuffix, int size, String filePath) throws IOException {
		return storeImageByZoomedSize(original, fileSuffix, size, size, filePath);
	}
	
	/**
	 * If width=0 and height=0, it will save the raw image
	 * height=-1,等比例缩放图片,目标区域为正方形 height>0,切图
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	public static String storeImageByZoomedSize(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		if (width == 0 && height == 0) {
			return saveRawImage(original, fileSuffix, filePath);
		} else if (height == -1) {
			if (KnxConfig.USE_IMAGEMAGICK) {
				return scaleImageWithImageMagickNoCut(original, fileSuffix, width, height, filePath);
			} else {
				return scaleImageWithJDKNoCut(original, fileSuffix, width, height, filePath);
			}
		} else {
			if (KnxConfig.USE_IMAGEMAGICK) {
				return scaleImageWithImageMagick(original, fileSuffix, width, height, filePath);
			} else {
				return scaleImageWithJDK(original, fileSuffix, width, height, filePath);
			}
		}
	}
	
	/**
	 * �?存原图（fileSuffix没用，暂�?删）,返回null表示�?存原图时�?��?�分辨率 （在当�?方法返回分辨率报错getOutputStream()
	 * has already been called for this response）
	 */
	private static String saveRawImage(InputStream original, String fileSuffix, String filePath) throws IOException {
		String directoryPath = StringUtil.getDirectoryName(filePath);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File newFile = new File(filePath);
		if (fileSuffix.equalsIgnoreCase("gif")) {
			fileSuffix = "png";
		}
		newFile.createNewFile();
		FileOutputStream out = new FileOutputStream(newFile);
		byte[] buffer = new byte[1024];
		int readed = -1;
		while ((readed = original.read(buffer)) > 0) {
			out.write(buffer, 0, readed);
		}
		out.flush();
		out.close();
		original.close();
		return null;
	}
	
	/**
	 * Using ImageMagick to scale image without cut
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	private static String scaleImageWithImageMagickNoCut(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		if (original == null) {
			return null;
		}
		String directoryPath = StringUtil.getDirectoryName(filePath);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		String originalFilePath = StringUtil.getOriginalFilePath(filePath);
		if (fileSuffix.equalsIgnoreCase("gif")) {
			fileSuffix = "png";
		}
		store(originalFilePath, original);
		try {
			ImageInfo info = new ImageInfo(originalFilePath);
			MagickImage mImage = new MagickImage(info);
			Dimension dimension = mImage.getDimension();
			int rawHeight = dimension.height;
			int rawWidth = dimension.width;
			if (rawHeight < rawWidth) {
				height = width * rawHeight / rawWidth;
			} else {
				height = width;
				width = height * rawWidth / rawHeight;
			}
			width = width == 0 ? 1 : width;
			height = height == 0 ? 1 : height;
			MagickImage scaled = mImage.scaleImage(width, height);
			scaled.setFileName(filePath);
			scaled.writeImage(info);
		} catch (MagickApiException ex) {
			ex.printStackTrace();
		} catch (MagickException ex) {
			ex.printStackTrace();
		}
		return new Integer(width).toString() + "×" + new Integer(height).toString();
	}
	
	/**
	 * Using JDK AWT to scale image, if no imagemagick in environment
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	private static String scaleImageWithJDKNoCut(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		String directoryPath = StringUtil.getDirectoryName(filePath);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		File newFile = new File(filePath);
		if (fileSuffix.equalsIgnoreCase("gif")) {
			fileSuffix = "png";
		}
		newFile.createNewFile();

		double ratio1 = 1.0;
		double ratio2 = 1.0;
		BufferedImage rawImage = ImageIO.read(original);

		int rawHeight = rawImage.getHeight();
		int rawWidth = rawImage.getWidth();
		if (rawHeight < rawWidth) {
			height = width * rawHeight / rawWidth;
		} else {
			height = width;
			width = width * rawWidth / rawHeight;
		}
		// BufferedImage image = toCuttedImage(rawImage, width, height); // cut
		Image result = rawImage.getScaledInstance(width, height, rawImage.SCALE_SMOOTH);
		ratio1 = (1.0d * width) / rawWidth;
		ratio2 = (1.0d * height) / rawHeight;
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio1, ratio2), null);
		result = op.filter(rawImage, null);
		ImageIO.write((BufferedImage) result, fileSuffix, newFile);
		return new Integer(width).toString() + "×" + new Integer(height).toString();
	}
	
	/**
	 * Using ImageMagick to scale image
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	private static String scaleImageWithImageMagick(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		String directoryPath = StringUtil.getDirectoryName(filePath);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		String originalFilePath = StringUtil.getOriginalFilePath(filePath);
		if (fileSuffix.equalsIgnoreCase("gif")) {
			fileSuffix = "png";
		}
		try {
			store(originalFilePath, original);

			ImageInfo info = new ImageInfo(originalFilePath);
			MagickImage mImage = new MagickImage(info);
			MagickImage scaled = mImage.scaleImage(width, height);
			scaled.setFileName(filePath);
			scaled.writeImage(info);
		} catch (MagickApiException ex) {
			ex.printStackTrace();
		} catch (MagickException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new Integer(width).toString() + "×" + new Integer(height).toString();
	}
	
	/**
	 * Using JDK AWT to scale image, if no imagemagick in environment
	 * 
	 * @param original
	 * @param fileSuffix
	 * @param width
	 * @param height
	 * @param filePath
	 * @throws IOException
	 */
	private static String scaleImageWithJDK(InputStream original, String fileSuffix, int width, int height, String filePath) throws IOException {
		String directoryPath = StringUtil.getDirectoryName(filePath);
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File newFile = new File(filePath);
		if (fileSuffix.equalsIgnoreCase("gif")) {
			fileSuffix = "png";
		}

		newFile.createNewFile();

		double ratio1 = 1.0;
		double ratio2 = 1.0;
		BufferedImage rawimage = ImageIO.read(original);
		BufferedImage image = toCuttedImage(rawimage, width, height); // cut
		Image result = image.getScaledInstance(width, height, image.SCALE_SMOOTH);
		ratio1 = (1.0d * width) / image.getWidth();
		ratio2 = (1.0d * height) / image.getHeight();
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio1, ratio2), null);
		result = op.filter(image, null);
		ImageIO.write((BufferedImage) result, fileSuffix, newFile);
		return new Integer(width).toString() + "×" + new Integer(height).toString();
	}
	
	private static BufferedImage toCuttedImage(BufferedImage image, int width, int height) throws IOException {
		double ratio1 = (double) image.getHeight() / (double) image.getWidth();
		double ratio2 = (double) height / (double) width;
		Rectangle rec = new Rectangle();

		if (ratio1 > ratio2) { // too high
			rec.width = image.getWidth();
			rec.height = (int) (image.getWidth() * ratio2);
			rec.x = 0;
			rec.y = (image.getHeight() - rec.height) / 2;
		} else { // too wide
			rec.width = (int) (image.getHeight() / ratio2);
			rec.height = image.getHeight();
			rec.x = (image.getWidth() - rec.width) / 2;
			rec.y = 0;
		}
		BufferedImage subImage = image.getSubimage(rec.x, rec.y, rec.width, rec.height);
		return subImage;
	}

	/**
	 * 获�?�上传原图的分辨率
	 */
	public static String getContentResolution(InputStream original) throws IOException {
		BufferedImage rawimage = ImageIO.read(original);
		return new Integer(rawimage.getWidth()).toString() + "×" + new Integer(rawimage.getHeight()).toString();
	}
	
	public static String getRawContentResolution(InputStream original, String filePath) throws IOException {
		String resolution = null;
		if (KnxConfig.USE_IMAGEMAGICK) {
			ImageInfo info;
			MagickImage mImage;
			Dimension dimension;
			try {
				info = new ImageInfo(filePath);
				mImage = new MagickImage(info);
				dimension = mImage.getDimension();
				int rawHeight = dimension.height;
				int rawWidth = dimension.width;
				resolution = String.valueOf(rawWidth) + "×" + String.valueOf(rawHeight);
			} catch (MagickException e) {
				e.printStackTrace();
			}
		} else {
			BufferedImage rawimage = ImageIO.read(original);
			resolution = String.valueOf(rawimage.getWidth()) + "×" + String.valueOf(rawimage.getHeight());
		}
		return resolution;
	}

	/**
	 * 获�?�上传图片的分辨率
	 */
	public static String getContentResolution(InputStream original, int width, int height) {
		BufferedImage rawimage = null;
		try {
			rawimage = ImageIO.read(original);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (width == 0 && height == 0) {
			return new Integer(rawimage.getWidth()).toString() + "×" + new Integer(rawimage.getHeight()).toString();
		} else if (height == -1) {
			int rawHeight = rawimage.getHeight();
			int rawWidth = rawimage.getWidth();
			if (rawHeight < rawWidth) {
				height = width * rawHeight / rawWidth;
			} else {
				height = width;
				width = height * rawWidth / rawHeight;
			}
			return new Integer(width).toString() + "×" + new Integer(height).toString();
		} else {
			return new Integer(width).toString() + "×" + new Integer(height).toString();
		}
	}

	/**
	 * 判断文件是�?�是图片（�?�获异常--很笨�?找替代方法�?
	 */
	public static Boolean checkIfImage(InputStream original) {
		BufferedImage rawimage;
		try {
			rawimage = ImageIO.read(original);
			if (rawimage == null) {
				return false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 文件大�?转�?�显示k,m,g
	 */
	public static String convertSize(Long size) {
		String retStr = "";
		for (int i = 3; i > 0; i--) {
			double compSize = Math.pow(1024, i);
			double remainder = size / compSize;
			if (remainder > 1) {
				if (i == 3) {
					retStr = round(remainder, 2, BigDecimal.ROUND_HALF_EVEN) + "G";
					break;
				} else if (i == 2) {
					retStr = round(remainder, 2, BigDecimal.ROUND_HALF_EVEN) + "M";
					break;
				} else {
					retStr = round(remainder, 2, BigDecimal.ROUND_HALF_EVEN) + "K";
					break;
				}
			}
			retStr = round(remainder, 2, BigDecimal.ROUND_HALF_EVEN) + "K";
		}
		return retStr;
	}

	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double retValue = bd.doubleValue();
		bd = null;
		return retValue;
	}

	/**
	 * load file
	 * 
	 * @param url
	 *            file store location
	 * @param stream
	 *            outputstream for writting
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void load(String url, OutputStream stream) {
		File file = new File(url);
		if (!file.exists()) {
			log.error("Can't find file:" + url);
			return;
		}
		log.debug("load(String url, OutputStream stream): OPEN file:" + url);
		FileInputStream reader = null;
		try {
			reader = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int readed = -1;
			while ((readed = reader.read(buffer)) > 0) {
				stream.write(buffer, 0, readed);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			try {
				log.debug("load(String url, OutputStream stream): close file:" + url);
				reader.close();
			} catch (IOException e) {
				// log.error(e.getMessage());
			}
			try {
				stream.flush(); // stream.write(buffer, 0,
				// readed)将数�?�写入缓存，stream.flush()刷缓存，将数�?�写入目标，如目标丢失产生IOException
			} catch (IOException e) {
				// e.printStackTrace();
			}
			try {
				stream.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}

	public static byte[] load(String url) {
		File file = new File(url);
		if (!file.exists()) {
			log.error("Can't find file:" + url);
			return null;
		}
		log.debug("load(String url): OPEN file:" + url);
		FileInputStream reader = null;
		byte[] buffer = new byte[1];
		try {
			reader = new FileInputStream(file);
			buffer = new byte[reader.available()];
			reader.read(buffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			log.debug("load(String url): close file:" + url);
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// log.error(e.getMessage());
			}
		}
		return buffer;
	}

	public static String loadFile(String path) {
		String str = "";
		byte[] b = load(path);
		try {
			str = new String(b, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 创建普�?文件
	 * 
	 * @param content
	 * @param filePath
	 */
	public static void createFile(String content, String filePath) {

		if (StringUtil.isNull(content)) {
			content = "";
		}

		OutputStreamWriter ow = null;
		BufferedWriter out = null;
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			ow = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
			out = new BufferedWriter(ow);
			out.write(content);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (ow != null) {
					ow.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * �?制文件
	 * 
	 * @param sourcePath
	 * @param targetPath
	 */
	public static void copyFile(String sourcePath, String targetPath) {
		File sourceFile = new File(sourcePath);
		File targetFile = new File(targetPath);
		if (!sourceFile.exists()) {
			log.error("Can't find file:" + sourcePath);
			return;
		}
		FileInputStream input = null;
		BufferedInputStream inBuff = null;
		FileOutputStream output = null;
		BufferedOutputStream outBuff = null;
		try {
			if (!targetFile.exists()) {
				targetFile.createNewFile();
			}
			// 新建文件输入�?并对它进行缓冲
			input = new FileInputStream(sourceFile);
			inBuff = new BufferedInputStream(input);

			// 新建文件输出�?并对它进行缓冲
			output = new FileOutputStream(targetFile);
			outBuff = new BufferedOutputStream(output);

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出�?
			outBuff.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭�?
			try {
				if (inBuff != null) {
					inBuff.close();
				}
				if (outBuff != null) {
					outBuff.close();
				}
				if (output != null) {
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static final byte[] FLV_HEADER = new byte[] { (byte) 0x46, (byte) 0x4C, (byte) 0x56, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09 };


	public static long getFileSize(String url) throws IOException, URISyntaxException {
		File file = new File(url);
		if (!file.exists()) {
			log.error("Can't find file:" + url);
			return 0;
		}
		return file.length();
	}

	public static boolean deleteFileLogic(File file) {
		if (file.exists()) {
			deleteLogicLog.info(file.toString());
		}
		return Boolean.TRUE;
	}

	public static boolean deleteFilePhysical(File file) {
		if (!file.exists()) {
			log.error("Can't find file:" + file);
			return false;
		}
		boolean deleteFlag = false;
		if (!(deleteFlag = file.delete())) {
			File delFile[] = file.listFiles();
			if (delFile != null) {
				for (int i = 0; i < delFile.length; i++) {
					if (delFile[i].isDirectory()) {
						deleteFilePhysical(delFile[i]);
					}
					deleteFlag = delFile[i].delete();
				}
				deleteFlag = file.delete();
			}
		}
		if (deleteFlag) {
			log.info("The file or directory is successfully deleted: " + file);
		} else {
			log.info("The file or directory is failed deleted: " + file);
		}
		return deleteFlag;
	}

	public static void clearTmpFile(File tmpFile) {
		if (!tmpFile.exists()) {
			System.out.println("file is not exist!");
			return;
		}
		if (tmpFile.isDirectory()) {
			File[] fileList = tmpFile.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					clearTmpFile(fileList[i]);
				}
				if (fileList[i].delete()) {
					log.info("Temp file:" + fileList[i].getAbsolutePath() + "has been deleted.");
				}
			}
			if (tmpFile.delete()) {
				log.info("Temp file:" + tmpFile.getAbsolutePath() + " has been deleted.");
			}
		} else {
			if (tmpFile.delete()) {
				log.info("Temp file:" + tmpFile.getAbsolutePath() + "has been deleted.");
			}
		}
	}

	public static void zoomImage(String filePath, String outPutFile, int width, int height, boolean replace) throws Exception, FileNotFoundException {
		File inPutFile = new File(filePath);
		File outPut = new File(outPutFile);
		zoomImage(inPutFile, outPut, width, height, replace);
	}

	public static void zoomImage(File inPutFile, File outPutFile, int width, int height, boolean replace) throws Exception, FileNotFoundException {
		if (!inPutFile.isFile()) {
			log.error("File Not Exist" + inPutFile);
			throw new FileNotFoundException("File Not Exist" + inPutFile);
		}
		if (!outPutFile.exists() || replace) {
			if (!outPutFile.exists())
				outPutFile.createNewFile();
			zoomImage(inPutFile, outPutFile, height, width);
		}
	}

	/** */
	/**
	 * 按指定大�?缩放图�?
	 * 
	 * @param inPutFile
	 * @param outPutFile
	 * @param height
	 * @param width
	 * @throws Exception
	 */
	public static void zoomImage(File inPutFile, File outPutFile, int width, int height) throws Exception {
		ImageIO.setUseCache(false);
		BufferedImage source = ImageIO.read(inPutFile);
		if (source == null) {
			return;
		}
		double hx = (double) height / source.getHeight();
		double wy = (double) width / source.getWidth();
		if (hx < wy) {
			wy = hx;
			width = (int) (source.getWidth() * wy);
		} else {
			hx = wy;
			height = (int) (source.getHeight() * hx);
		}

		int type = source.getType();
		BufferedImage target = null;
		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else {
			target = new BufferedImage(width, height, type);
		}
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		g.drawRenderedImage(source, AffineTransform.getScaleInstance(wy, hx));
		g.dispose();
		try {
			ImageIO.write(target, "JPEG", outPutFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String bin2hex(String bin) {
		char[] digital = "0123456789ABCDEF".toCharArray();
		StringBuffer sb = new StringBuffer("");
		byte[] bs = bin.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(digital[bit]);
			bit = bs[i] & 0x0f;
			sb.append(digital[bit]);
		}
		return sb.toString();
	}


	public static int getFileHeadStreamLength(String url) {
		File file = new File(url);
		if (!file.exists()) {
			log.error("Can't find file:" + url);
			return 0;
		}
		log.debug("loadByKey(String url, OutputStream stream, long start): OPEN file:" + url);
		RandomAccessFile r = null;
		try {
			r = new RandomAccessFile(file, "r");
			byte[] b = new byte[3000];
			int lenth = r.read(b);
			int j = 0;
			boolean ret = false;
			if (lenth != -1) {
				for (int i = 0; i < lenth; ++i) {
					Byte bt = new Byte(b[i]);
					if (bt.intValue() == 9 && !ret) {
						if (i + 5 < lenth) {
							Byte bti1 = new Byte(b[i + 1]);
							Byte bti2 = new Byte(b[i + 2]);
							Byte bti3 = new Byte(b[i + 3]);
							Byte bti4 = new Byte(b[i + 4]);
							Byte bti5 = new Byte(b[i + 5]);
							if (bti1.intValue() == 0 && bti2.intValue() == 0 && bti3.intValue() == 0 && bti4.intValue() == 0 && bti5.intValue() != 0) {
								j = i + 5;
								i = j;
								ret = true;
								continue;
							}
						} else {
							return -1;
						}
					} else {
						if (!ret) {
							continue;
						}
					}
					int offLength = i - j;
					if (i + 4 < lenth) {
						Byte bti1 = new Byte(b[i]);
						Byte bti2 = new Byte(b[i + 1]);
						Byte bti3 = new Byte(b[i + 2]);
						Byte bti4 = new Byte(b[i + 3]);
						String bin = getBinStringFromInt(bti1) + getBinStringFromInt(bti2) + getBinStringFromInt(bti3) + getBinStringFromInt(bti4);
						long l = Long.parseLong(bin, 16);
						if (l == offLength) {
							return i;
						}
						// System.out.println(offLength+"----"+l);
						bin = "";
					} else {
						return -1;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static String getBinStringFromInt(Byte b) {
		String hex = Integer.toHexString(b.intValue());
		if (b.intValue() < 0 && hex.length() == 8) {
			hex = hex.substring(6, 8);
		}
		return hex;
	}

	static byte[] int2bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		for (byte by : b) {
			System.out.println(by);
		}
		return b;
	}

	public static boolean encryptionFile(String sourceFile, String targetFile) {
		FileInputStream input = null;
		FileOutputStream output = null;
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			File source = new File(sourceFile);
			if (!source.exists()) {
				return false;
			}
			File target = new File(targetFile);
			// 新建文件输入流并对它进行缓冲
			input = new FileInputStream(source);
			inBuff = new BufferedInputStream(input);

			// 新建文件输出流并对它进行缓冲
			output = new FileOutputStream(target);
			outBuff = new BufferedOutputStream(output);

			Random random = new Random();
			int a = random.nextInt(500);
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			int i = 1;
			while ((len = inBuff.read(b)) != -1) {
				if (i == 1) {
					byte[] first = new byte[a + b.length];
					System.arraycopy(b, 0, first, 0, 13);
					System.arraycopy(b, 0, first, 13, a);
					System.arraycopy(b, 13, first, a + 13, b.length - 13);
					outBuff.write(first, 0, first.length);
					i++;
				} else {
					outBuff.write(b, 0, len);
				}
			}
			// 刷新此缓冲的输出�?
			outBuff.flush();
			return true;
		} catch (Exception e) {
			//
		} finally {
			try {
				if (inBuff != null) {
					inBuff.close();
				}
				if (outBuff != null) {
					outBuff.close();
				}
				if (output != null) {
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void copyFile(File sourceFile, File targetFile, File recover) throws IOException {
		// 新建文件输入流并对它进行缓冲
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		Random random = new Random();
		int a = random.nextInt(500);
		// 缓冲数组
		byte[] b = new byte[1024 * 5];
		int len;
		int i = 1;
		while ((len = inBuff.read(b)) != -1) {
			if (i == 1) {
				byte[] first = new byte[a + b.length + 4];
				System.arraycopy(b, 0, first, 0, 13);
				System.arraycopy(b, 0, first, 13, a);
				System.arraycopy(b, 13, first, a + 13, b.length - 13);
				outBuff.write(first, 0, first.length);

				i++;
			} else {
				outBuff.write(b, 0, len);
			}

		}
		// 刷新此缓冲的输出�?
		outBuff.flush();

		// 关闭�?
		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
		i = 1;
		// 新建文件输入流并对它进行缓冲
		FileInputStream input1 = new FileInputStream(targetFile);
		BufferedInputStream inBuff1 = new BufferedInputStream(input1);

		// 新建文件输出流并对它进行缓冲
		FileOutputStream output1 = new FileOutputStream(recover);
		BufferedOutputStream outBuff1 = new BufferedOutputStream(output1);

		byte[] c = new byte[1024 * 5];
		while ((len = inBuff1.read(c)) != -1) {
			if (i == 1) {
				byte[] first = new byte[c.length - a];
				System.arraycopy(c, 0, first, 0, 13);
				System.arraycopy(c, a + 13, first, 13, c.length - a - 13);
				outBuff1.write(first, 0, first.length);
				System.out.println(first);
				i++;
			} else {
				outBuff1.write(c, 0, len);
			}

		}

		input1.close();
		outBuff1.close();
		output1.close();
		inBuff1.close();
	}


}