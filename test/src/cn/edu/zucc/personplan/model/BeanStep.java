package cn.edu.zucc.personplan.model;

public class BeanStep {
	public static final String[] tblStepTitle={"���","����","�ƻ���ʼʱ��","�ƻ����ʱ��","ʵ�ʿ�ʼʱ��","ʵ�����ʱ��"};
	/**
	 * �����и���javabean������޸ı��������룬col��ʾ�������е�����ţ�0��ʼ
	 */
	public String getCell(int col){
		if(col==0) return "1";
		else if(col==1) return "ʾ������";
		else if(col==2) return "2015-01-01";
		else if(col==3) return "2015-08-01";
		else if(col==4) return "";
		else if(col==5) return "";
		
		else return "";
	}
}
