package cn.edu.zucc.personplan.model;

public class BeanPlan {
	public static final String[] tableTitles={"���","����","������","�������"};
	/**
	 * �����и���javabean������޸ı��������룬col��ʾ�������е�����ţ�0��ʼ
	 */
	public String getCell(int col){
		if(col==0) return "1";
		else if(col==1) return "ʾ���ƻ�";
		else if(col==2) return "2";
		else if(col==3) return "1";
		else if(col==4) return "dddd";
		else return "";
	}

}
