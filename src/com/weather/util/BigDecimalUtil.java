package com.weather.util;

import java.math.BigDecimal;


public class BigDecimalUtil {
	/*
	 BigDecimal(String val)将 BigDecimal 的字符串表示形式转换为 BigDecimal。
	 add(BigDecimal augend) 返回一个 BigDecimal，其值为 (this + augend)，其标度为 max(this.scale(), augend.scale())。
	 subtract(BigDecimal subtrahend)返回一个 BigDecimal，其值为 (this - subtrahend)，其标度为 max(this.scale(), subtrahend.scale())。
	 multiply(BigDecimal multiplicand)返回一个 BigDecimal，其值为 (this × multiplicand)，其标度为 (this.scale() + multiplicand.scale())。
	 divide(BigDecimal divisor, int roundingMode)返回一个 BigDecimal，其值为 (this / divisor)，其标度为 this.scale()。//我舍入模式roundingMode选BigDecimal.ROUND_HALF_UP
	 divide(BigDecimal divisor, int scale, int roundingMode)返回一个 BigDecimal，其值为 (this / divisor)，其标度为指定标度。	//scale保留小数点后几位
	 
	 setScale(int newScale, int roundingMode)
     返回一个 BigDecimal，其标度为指定值，其非标度值通过此 BigDecimal 的非标度值乘以或除以十的适当次幂来确定，以维护其总值。//获得指定精度的新的BigDecimal对象
     ////我舍入模式roundingMode选BigDecimal.ROUND_HALF_UP
	 */
	public static final int SCALE = 2;	//保留小数点后几位数字
	public static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;	//舍入模式，通常的四舍五入
	public static final int COMPARE_SCALE = 2;	//数字比较前，先四舍五入，保留小数点后COMPARE_SCALE位有效数字，用舍入结果进行比较
	
	public static BigDecimal newBigDecimal(String val) {
		return new BigDecimal(val);
	}
	
	public static BigDecimal newBigDecimal(float val) {
		return new BigDecimal(val).setScale(SCALE, ROUNDING_MODE);
	}
	
	public static BigDecimal newBigDecimal(double val) {
		return new BigDecimal(val).setScale(SCALE, ROUNDING_MODE);
	}
	
	/**
	 * 由于 BigDecimal 对象是不可变的，此方法的调用不会 导致初始对象被修改，这与使用名为 setX 变异字段 X 方法的常规约定相反。相反，setScale 返回具有适当标度的对象；返回的对象不一定是新分配的。 
	 * @param val
	 * @return
	 */
	public static BigDecimal newBigDecimal(BigDecimal val) {
		return val.setScale(SCALE, ROUNDING_MODE);
	}
	
	/**
	 * 对可转为类型的对象val1和val2进行比较大小。
	 * 数字比较前，先四舍五入，保留小数点后COMPARE_SCALE位有效数字，再用舍入结果进行比较。
	 * @param val1
	 * @param val2
	 * @exception NumberFormatException 如果 val1或val2 不是 BigDecimal 的有效表示形式。
	 * @return 当此 BigDecimal 在数字上小于、等于或大于 val 时，返回 -1、0 或 1。
	 */
	public static int compareTo(Object val1, Object val2) {
		return new BigDecimal(val1.toString()).setScale(COMPARE_SCALE, ROUNDING_MODE).compareTo( new BigDecimal(val2.toString()).setScale(COMPARE_SCALE, ROUNDING_MODE));
	}
	
	/**
	 * 加
	 * @param addend
	 * @param augend
	 * @exception NumberFormatException 如果 addend或augend 不是 BigDecimal 的有效表示形式。
	 * @return
	 */
	public static BigDecimal add(Object addend, Object augend) {
		return new BigDecimal(addend.toString()).add(new BigDecimal(augend.toString())).setScale(SCALE, ROUNDING_MODE);
	}
	
	/**
	 * 减
	 * @param minuend
	 * @param subtrahend
	 * @exception NumberFormatException 如果 minuend或subtrahend 不是 BigDecimal 的有效表示形式。
	 * @return
	 */
	public static BigDecimal subtract(Object minuend, Object subtrahend) {
		return new BigDecimal(minuend.toString()).subtract(new BigDecimal(subtrahend.toString())).setScale(SCALE, ROUNDING_MODE);
	}
	
	/**
	 * 乘
	 * @param multiplier
	 * @param multiplicand
	 * @exception NumberFormatException 如果 multiplier或multiplicand 不是 BigDecimal 的有效表示形式。
	 * @return
	 */
	public static BigDecimal multiply(Object multiplier, Object multiplicand) {
		return new BigDecimal(multiplier.toString()).multiply(new BigDecimal(multiplicand.toString())).setScale(SCALE, ROUNDING_MODE);
	}
	
	/**
	 * 除
	 * @param dividend
	 * @param divisor
	 * @exception NumberFormatException 如果 dividend或divisor 不是 BigDecimal 的有效表示形式。
	 * @return
	 */
	public static BigDecimal divide(Object dividend, Object divisor) {
		return new BigDecimal(dividend.toString()).divide(new BigDecimal(divisor.toString()), SCALE, ROUNDING_MODE);	//dividend.divide(divisor, SCALE, ROUNDING_MODE);
	}
	
	/**
	 * 将BigDecimal类型对象四舍五入转换为“.00”格式的数据字符串,obj为null时返回空字符串
	 * @param obj
	 * @return
	 */
	public static String format2Scale(BigDecimal obj) {
		if(obj!=null) {
			return obj.setScale(2, ROUNDING_MODE).toPlainString();
		}else {
			return "";
		}
	}
	
	/**
	 * 将BigDecimal类型对象四舍五入转换为“.00”格式的数据
	 * @param obj
	 * @return
	 */
	public static BigDecimal formatBigDecimal2Scale(BigDecimal obj) {
		if(obj!=null) {
			return obj.setScale(2, ROUNDING_MODE);
		}else {
			return obj;
		}
		
	}
	
	/**
	 * 将BigDecimal类型对象四舍五入转换为“.0000”格式的数据字符串,obj为null时返回空字符串
	 * @param obj
	 * @return
	 */
	public static String format4Scale(BigDecimal obj) {
		if(obj!=null) {
			return obj.setScale(4, ROUNDING_MODE).toPlainString();
		}else {
			return "";
		}
		
	}
	
	/**
	 * 将BigDecimal类型对象四舍五入转换为“.0000”格式的数据
	 * @param obj
	 * @return
	 */
	public static BigDecimal formatBigDecimal4Scale(BigDecimal obj) {
		if(obj!=null) {
			return obj.setScale(4, ROUNDING_MODE);
		}else {
			return obj;
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
