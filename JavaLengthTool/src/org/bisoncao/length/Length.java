/**
 *��Ŀ���ƣ�JavaLengthTool
 *���ܣ����ȵ�λת���ͼ���
 *���ߣ��ܻ۱�
 *�������ڣ�2013-08-01
 *�޸����ڣ�2013-08-02
 *�ŵ㣺	1. ͨ���ԣ�����ʶ�������ļ����ת����ϵ�����ܵ�λ�ĵ�����Ӱ�죻���Ƶ��쳣�������
 *		2. ��Ч�ԣ����ַ�������ģ�黯�������������ַ������������ÿһ�����������ٿ���
 *		3. ά���ԣ��淶�Ĵ�������ϸ��λ��ע�ͣ����峣���������޸ģ����õĴ����������
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
	// ����
	private static final String MY_EMAIL = "bisoncao@qq.com"; // �����������ɹ����ϱ�����ע������
	private static final String INPUT_FILE = "input.txt"; // �����ļ������·���������Ŀ��Ŀ¼��
	private static final String OUTPUT_FILE = "output.txt"; // ����ļ������·���������Ŀ��Ŀ¼��
	private static final String[] OPERATOR = { "+", "-" }; // Ҫʶ��������

	private static List<HashMap<String, Object>> conversions = 
		new ArrayList<HashMap<String, Object>>(); // ��Ż����ϵ
	private static Vector<Double> results = new Vector<Double>(); // ������м�����

	public static void main(String[] args) {

		BufferedReader fileReader = null;
		boolean readException = false; // ��¼��ȡ�����ļ�ʱ�Ƿ������쳣
		try {
			/* ��һ�����������ļ���ȡ�����ϵ���洢 */
			fileReader = new BufferedReader(new FileReader(INPUT_FILE));

			String line; // ���ÿ�ζ������
			
			StringTokenizer token; // ����ĳ����������ַ����ָ��ɼ�������
			String tokenLeft; // ���ָ����ַ���ֻ��������ʱ������ĵ�һ����
			String tokenRight; // ���ָ����ַ���ֻ��������ʱ������ĵڶ�����
			
			Vector<Double> calValues = new Vector<Double>(); // ˳����һ���������и�����ֵ
			Vector<String> calUnits = new Vector<String>(); // ˳����һ���������и�����λ
			Vector<String> calOperators = new Vector<String>(); // ˳����һ���������и��������
			
			HashMap<String, Object> map; // ���ĳ�������ϵ�ı����㵥λ�ͻ�������

			line = fileReader.readLine();
			while (!line.equals("")) { // �����ϵ����Ŀ֮����һ���У��Դ�ȷ�������ϵ�Ľ�β
				map = new HashMap<String, Object>();
				
				// �ڻ����ϵ��ԵȺ�Ϊ�磬�������ȡ�����㵥λ�����ұ���ȡ��������
				token = new StringTokenizer(line, "=");
				tokenLeft = token.nextToken();
				tokenRight = token.nextToken();

				// ����Կո�Ϊ�磬�ڶ�����Ϊ�����㵥λ
				token = new StringTokenizer(tokenLeft, " ");
				token.nextToken(); // ���˵���һ����				
				// ��ȡ�ڶ����ֲ��Լ�unit��ʶ�洢�ڼ�ֵ����
				map.put("unit", token.nextToken());

				// �ұ����Կո�Ϊ�磬��һ����Ϊ��������
				token = new StringTokenizer(tokenRight, " ");
				// ��ȡ�ڶ������Լ�factor��ʶ�洢�ڼ�ֵ����
				map.put("factor", Double.parseDouble(token.nextToken()));

				// ������������ֵ�Դ�ŵ������У���Ϊһ�������ϵ�����ڶ���ת��Ϊ�ף���˲��ش洢�ף�
				conversions.add(map);

				// ������һ�У��Խ�����һ�������ϵ
				line = fileReader.readLine();
			}

			/* �ڶ�������ȡÿ����Ŀ���е�λ�������㣬������ */
			line = fileReader.readLine();
			// �ж��Ƿ���������һ��
			// �����������ļ���ʵ����������һ����п��У���˲���ͨ���ļ�β���ж����һ�⣩
			while (!line.equals("")) {
				
				if (isCalculation(line)) { // ����Ǽ�����
					
					// ˳����һ����������ĸ�����ֵ����λ�������
					token = new StringTokenizer(line, " ");

					while (token.hasMoreTokens()) {
						
						calValues.add(Double.parseDouble(token.nextToken())); // �����ֵ
						calUnits.add(token.nextToken()); // ��ŵ�λ

						// ��Ϊ���������������һ�������Զ�����������Ҫ�������Ƿ��������
						if (token.hasMoreTokens()) {
							calOperators.add(token.nextToken()); // ��������
						}
						
					}
					
					// ���ô��������ķ����õ��������ŵ�������
					results.add(caculator(calValues, calUnits, calOperators));

					// ÿ��ʹ������������գ��Ա��´��������ʱ���������ϴ�����
					calValues.clear(); // ��մ����ֵ������
					calUnits.clear(); // ��մ�ŵ�λ������
					calOperators.clear(); // ��մ�������������

				} else { // ����ǵ�λ�����⣨����ֻ�м�����͵�λ���������֣���˷Ǵ˼��ˣ�
					// ��λת��������ֵ�뵥λ�Կո�Ϊ�磬�Դ���ȡ��ֵ�͵�λ
					token = new StringTokenizer(line, " ");
					tokenLeft = token.nextToken(); // ��ȡ��ֵ
					tokenRight = token.nextToken(); // ��ȡ��λ
					
					// ���ô���λ������ķ����õ��������ŵ�������
					results.add(convertor(Double.parseDouble(tokenLeft),
							tokenRight));
				}

				// ������һ�У��Լ�����һ��
				line = fileReader.readLine();
			}

		} catch (IOException ioe) { // ����ȡ�����ļ�����IOException�쳣ʱ
			System.out.println("[Error] I/O Exception in reading \""
					+ INPUT_FILE + "\"."); // ���������ʾ��Ϣ������̨
			readException = true; // ��ȡ�����ļ�ʱ�������쳣���ʴ˱�����Ϊtrue���������ж�

		} finally {
			if (fileReader != null) {
				try {
					fileReader.close(); // �ر������ļ����ͷ���Դ

				} catch (IOException e) { // ���ر������ļ�����IOException�쳣ʱ
					System.out.println("[Error] I/O Exception in closing \""
							+ INPUT_FILE + "\"."); // ���������ʾ��Ϣ������̨
					System.exit(-1); // �������˳�����
				}
			}

			if (readException) { // �����ȡ�����ļ�ʱ�������쳣�����˳�����
				System.exit(-1); // �������˳�����
			}

		}

		/* ����������Ҫ��������������ļ� */
		PrintWriter fileWriter = null;
		boolean writeException = false; // ��¼д����ļ�ʱ�Ƿ������쳣
		try {
			fileWriter = new PrintWriter(new FileWriter(OUTPUT_FILE));
			
			/*
			 * ��Ҫ�� ���ļ��ĸ�ʽ����12�У����ϸ��������¹���
			 * ��1�������������������ɹ����ϱ���ʱ��ע�����䣬����myName@gmail.com
			 * ��2���ǿ���
			 * ��3������12�У�ÿ����ʾ1��������������1931.21 m ������Ҫ��ȷ��С�������λ
			 * ������������ĸm��β����ע�����ֺ���ĸm֮����һ���ո�
			 */
			fileWriter.println(MY_EMAIL);
			fileWriter.println("");
			// ��Ҫע��������һ�����ʱ��Ӧ���ٻ��У������滹�н��ʱ�Ż��У�
			for (Iterator<Double> i = results.iterator(); i.hasNext();) {
				
				fileWriter.print(String.format("%.2f", i.next()) + " m"); // ��ʽ�������������
				
				if (i.hasNext()) { // ���滹�н��
					fileWriter.print("\r\n"); // ���У����һ���س�����һ�����з���
				}
				
			}

		} catch (IOException e) {
			System.out.println("[Error] I/O Exception in writing \""
					+ OUTPUT_FILE + "\"."); // ���������ʾ��Ϣ������̨
			writeException = true; // д����ļ�ʱ�������쳣���ʴ˱�����Ϊtrue���������ж�

		} finally {
			if (fileWriter != null) {
				fileWriter.close(); // �ر�����ļ����ͷ���Դ

			}

			if (writeException) { // ���д����ļ�ʱ�������쳣�����˳�����
				System.exit(-1); // �������˳�����

			}

		}

	}

	/**
	 * �ж��Ƿ��Ǽ�����
	 * 
	 * @param line
	 *            Ҫ�жϵ��ַ���
	 * @return true �Ǽ�����; false ���Ǽ�����
	 */
	public static boolean isCalculation(String line) {
		
		// ͨ���ж��ַ����Ƿ����Ҫʶ�����������ж��Ƿ��Ǽ�����
		for (int i = 0; i < OPERATOR.length; i++) {
			
			if (line.contains(OPERATOR[i])) {
				return true;
			}

		}

		// ������Ҫʶ�����������ж�Ϊ�Ǽ�����
		return false;
	}

	/**
	 * ���������
	 * 
	 * @param values
	 *            ���������������ֵ����������˳���ţ�
	 * @param units
	 *            ������������е�λ����������˳���ţ�
	 * @param operators
	 *            ������������з��ţ���������˳���ţ�
	 * @return ����������ֵ����λΪ�ף�
	 */
	public static double caculator(Vector<Double> values, Vector<String> units,
			Vector<String> operators) {
		double tempResult; // ������һ��������

		// �Ȱ���ֵת��������Ϊ��λʱ��ֵ
		for (int i = 0; i < values.size(); i++) {
			// ���ô���λ������ķ����õ����������滻������ԭ����ֵ
			values.set(i, convertor(values.get(i), units.get(i)));
		}

		// �ٰ������ҵ�˳����мӼ�����
		tempResult = values.get(0); // ��ʼ��Ϊ��һ����ֵ��ֵ
		for (int i = 0; i < operators.size(); i++) {
			
			if (operators.get(i).equals("+")) { // ���ӷ�
				tempResult += values.get(i + 1); // ���һ�������� + �������� = ��һ��������
			} else { // ������
				tempResult -= values.get(i + 1); // ���һ�������� - �������� = ��һ��������
			}
			
		}

		return tempResult; // �������һ������������Ϊ���ս��
	}

	/**
	 * ����λ������
	 * 
	 * @param value
	 *            ��λ�����������ֵ
	 * @param unit
	 *            ��λ��������ĵ�λ
	 * @return ת���������ֵ����λΪ�ף�; ת��ʧ��ʱ����-1������ת���Ķ���������
	 */
	public static double convertor(double value, String unit) {
		// ���ڵ�λ�ĸ����ǲ�����仯ʱҪ�ȴ���
		if (unit.equals("feet")) {
			unit = "foot";
		}

		// �ӱ���Ļ����ϵ���ҵ�ƥ��Ĳ���ȡ�������ӽ��м���
		for (int i = 0; i < conversions.size(); i++) {
			// ע�����ﲻ����equals()��������Ϊ��λ�����Ǹ�����ʽ������仯Ϊ��s��es��
			if (unit.contains((String) conversions.get(i).get("unit"))) {
				// ��ȡ��������
				double factor = Double.parseDouble(String.valueOf(conversions
						.get(i).get("factor")));
				// ��ֵ�뻻�����ӵĳ˻���Ϊ������
				return value * factor;
			}
		}

		// ת��ʧ��ʱ����-1������ת���Ķ�����������ʧ��ԭ���Ҳ���ƥ��Ļ����ϵ��
		return -1;
	}

}
