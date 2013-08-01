/**
 *项目名称：JavaLengthTool
 *功能：长度单位转化和计算
 *作者：曹慧斌
 *创建日期：2013-08-01
 *修改日期：2013-08-02
 *优点：	1. 通用性：主动识别输入文件里的转换关系；不受单位的单复数影响；完善的异常处理机制
 *		2. 高效性：对字符串进行模块化处理，而不是逐字符处理；充分利用每一个变量，减少开销
 *		3. 维护性：规范的代码风格，详细到位的注释；定义常量，便于修改；良好的代码可重用性
 */

package org.bisoncao.length;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Length {
	// 常量
	private static final String MY_EMAIL = "bisoncao@qq.com"; // 渣打编程马拉松官网上报名的注册邮箱
	private static final String INPUT_FILE = "input.txt"; // 输入文件的相对路径（相对项目根目录）
	private static final String OUTPUT_FILE = "output.txt"; // 输出文件的相对路径（相对项目根目录）
	private static final String[] OPERATOR = { "+", "-" }; // 要识别的运算符

	private static List<HashMap<String, Object>> conversions = 
		new ArrayList<HashMap<String, Object>>(); // 存放换算关系
	private static Vector<Double> results = new Vector<Double>(); // 存放所有计算结果

	public static void main(String[] args) {

		BufferedReader fileReader = null;
		boolean readException = false; // 记录读取输入文件时是否发生了异常
		try {
			/* 第一步：从输入文件读取换算关系并存储 */
			fileReader = new BufferedReader(new FileReader(INPUT_FILE));

			String line; // 存放每次读入的行
			
			StringTokenizer token; // 依据某个定界符将字符串分隔成几个部分
			String tokenLeft; // 当分隔的字符串只有两部分时存放它的第一部分
			String tokenRight; // 当分隔的字符串只有两部分时存放它的第二部分
			
			Vector<Double> calValues = new Vector<Double>(); // 顺序存放一道计算题中各个数值
			Vector<String> calUnits = new Vector<String>(); // 顺序存放一道计算题中各个单位
			Vector<String> calOperators = new Vector<String>(); // 顺序存放一道计算题中各个运算符
			
			HashMap<String, Object> map; // 存放某个换算关系的被换算单位和换算因子

			line = fileReader.readLine();
			while (!line.equals("")) { // 换算关系与题目之间有一空行，以此确定换算关系的结尾
				map = new HashMap<String, Object>();
				
				// 在换算关系里，以等号为界，从左边提取被换算单位，从右边提取换算因子
				token = new StringTokenizer(line, "=");
				tokenLeft = token.nextToken();
				tokenRight = token.nextToken();

				// 左边以空格为界，第二部分为被换算单位
				token = new StringTokenizer(tokenLeft, " ");
				token.nextToken(); // 过滤掉第一部分				
				// 提取第二部分并以键unit标识存储在键值对里
				map.put("unit", token.nextToken());

				// 右边亦以空格为界，第一部分为换算因子
				token = new StringTokenizer(tokenRight, " ");
				// 提取第二部分以键factor标识存储在键值对里
				map.put("factor", Double.parseDouble(token.nextToken()));

				// 将以上两个键值对存放到集合中，即为一个换算关系（由于都是转换为米，因此不必存储米）
				conversions.add(map);

				// 读入下一行，以解析下一个换算关系
				line = fileReader.readLine();
			}

			/* 第二步：读取每个题目进行单位换算或计算，保存结果 */
			line = fileReader.readLine();
			// 判断是否读完了最后一题
			// （根据输入文件的实际情况，最后一题后有空行，因此不能通过文件尾来判断最后一题）
			while (!line.equals("")) {
				
				if (isCalculation(line)) { // 如果是计算题
					
					// 顺序存放一个计算题里的各个数值、单位和运算符
					token = new StringTokenizer(line, " ");

					while (token.hasMoreTokens()) {
						
						calValues.add(Double.parseDouble(token.nextToken())); // 存放数值
						calUnits.add(token.nextToken()); // 存放单位

						// 因为运算符比运算数少一个，所以读完运算数后要检查后面是否有运算符
						if (token.hasMoreTokens()) {
							calOperators.add(token.nextToken()); // 存放运算符
						}
						
					}
					
					// 调用处理计算题的方法得到结果并存放到集合中
					results.add(caculator(calValues, calUnits, calOperators));

					// 每次使用完向量后清空，以便下次添加数据时不会遗留上次数据
					calValues.clear(); // 清空存放数值的向量
					calUnits.clear(); // 清空存放单位的向量
					calOperators.clear(); // 清空存放运算符的向量

				} else { // 如果是单位换算题（由于只有计算题和单位换算题两种，因此非此即彼）
					// 单位转换题中数值与单位以空格为界，以此提取数值和单位
					token = new StringTokenizer(line, " ");
					tokenLeft = token.nextToken(); // 提取数值
					tokenRight = token.nextToken(); // 提取单位
					
					// 调用处理单位换算题的方法得到结果并存放到集合中
					results.add(convertor(Double.parseDouble(tokenLeft),
							tokenRight));
				}

				// 读入下一行，以计算下一题
				line = fileReader.readLine();
			}

		} catch (IOException ioe) { // 当读取输入文件发生IOException异常时
			System.out.println("[Error] I/O Exception in reading \""
					+ INPUT_FILE + "\"."); // 输出错误提示信息到控制台
			readException = true; // 读取输入文件时发生了异常，故此变量置为true，供下文判断

		} finally {
			if (fileReader != null) {
				try {
					fileReader.close(); // 关闭输入文件，释放资源

				} catch (IOException e) { // 当关闭输入文件发生IOException异常时
					System.out.println("[Error] I/O Exception in closing \""
							+ INPUT_FILE + "\"."); // 输出错误提示信息到控制台
					System.exit(-1); // 非正常退出程序
				}
			}

			if (readException) { // 如果读取输入文件时发生了异常，则退出程序
				System.exit(-1); // 非正常退出程序
			}

		}

		/* 第三步：按要求输出结果到输出文件 */
		PrintWriter fileWriter = null;
		boolean writeException = false; // 记录写输出文件时是否发生了异常
		try {
			fileWriter = new PrintWriter(new FileWriter(OUTPUT_FILE));
			
			/*
			 * 【要求】 该文件的格式共有12行，并严格遵守以下规则：
			 * 第1行是您在渣打编程马拉松官网上报名时的注册邮箱，比如myName@gmail.com
			 * 第2行是空行
			 * 第3行至第12行，每行显示1个计算结果，比如1931.21 m 计算结果要求精确到小数点后两位
			 * 计算结果均以字母m结尾，请注意数字和字母m之间有一个空格
			 */
			fileWriter.println(MY_EMAIL);
			fileWriter.println("");
			// 需要注意输出最后一个结果时不应该再换行（即后面还有结果时才换行）
			for (Iterator<Double> i = results.iterator(); i.hasNext();) {
				
				fileWriter.print(String.format("%.2f", i.next()) + " m"); // 格式化输出，不换行
				
				if (i.hasNext()) { // 后面还有结果
					fileWriter.print("\r\n"); // 换行（输出一个回车符和一个换行符）
				}
				
			}

		} catch (IOException e) {
			System.out.println("[Error] I/O Exception in writing \""
					+ OUTPUT_FILE + "\"."); // 输出错误提示信息到控制台
			writeException = true; // 写输出文件时发生了异常，故此变量置为true，供下文判断

		} finally {
			if (fileWriter != null) {
				fileWriter.close(); // 关闭输出文件，释放资源

			}

			if (writeException) { // 如果写输出文件时发生了异常，则退出程序
				System.exit(-1); // 非正常退出程序

			}

		}

	}

	/**
	 * 判断是否是计算题
	 * 
	 * @param line
	 *            要判断的字符串
	 * @return true 是计算题; false 不是计算题
	 */
	public static boolean isCalculation(String line) {
		
		// 通过判断字符串是否包含要识别的运算符来判断是否是计算题
		for (int i = 0; i < OPERATOR.length; i++) {
			
			if (line.contains(OPERATOR[i])) {
				return true;
			}

		}

		// 不包含要识别的运算符即判定为非计算题
		return false;
	}

	/**
	 * 处理计算题
	 * 
	 * @param values
	 *            计算题里的所有数值（按从左到右顺序存放）
	 * @param units
	 *            计算题里的所有单位（按从左到右顺序存放）
	 * @param operators
	 *            计算题里的所有符号（按从左到右顺序存放）
	 * @return 计算结果的数值（单位为米）
	 */
	public static double caculator(Vector<Double> values, Vector<String> units,
			Vector<String> operators) {
		double tempResult; // 存放最近一步运算结果

		// 先把数值转换成以米为单位时的值
		for (int i = 0; i < values.size(); i++) {
			// 调用处理单位换算题的方法得到换算结果并替换向量中原有数值
			values.set(i, convertor(values.get(i), units.get(i)));
		}

		// 再按从左到右的顺序进行加减运算
		tempResult = values.get(0); // 初始化为第一个数值的值
		for (int i = 0; i < operators.size(); i++) {
			
			if (operators.get(i).equals("+")) { // 做加法
				tempResult += values.get(i + 1); // 最近一步运算结果 + 右运算数 = 新一步运算结果
			} else { // 做减法
				tempResult -= values.get(i + 1); // 最近一步运算结果 - 右运算数 = 新一步运算结果
			}
			
		}

		return tempResult; // 返回最后一步运算结果，即为最终结果
	}

	/**
	 * 处理单位换算题
	 * 
	 * @param value
	 *            单位换算题里的数值
	 * @param unit
	 *            单位换算题里的单位
	 * @return 转换结果的数值（单位为米）; 转换失败时返回-1（假设转换的都是正数）
	 */
	public static double convertor(double value, String unit) {
		// 对于单位的复数是不规则变化时要先处理
		if (unit.equals("feet")) {
			unit = "foot";
		}

		// 从保存的换算关系中找到匹配的并提取换算因子进行计算
		for (int i = 0; i < conversions.size(); i++) {
			// 注意这里不能用equals()方法，因为单位可能是复数形式（规则变化为加s或es）
			if (unit.contains((String) conversions.get(i).get("unit"))) {
				// 提取换算因子
				double factor = Double.parseDouble(String.valueOf(conversions
						.get(i).get("factor")));
				// 数值与换算因子的乘积即为换算结果
				return value * factor;
			}
		}

		// 转换失败时返回-1（假设转换的都是正数）（失败原因：找不到匹配的换算关系）
		return -1;
	}

}
