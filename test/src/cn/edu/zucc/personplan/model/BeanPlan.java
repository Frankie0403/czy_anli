package cn.edu.zucc.personplan.model;

import java.util.Date;

public class BeanPlan {
	public static final String[] tableTitles={"序号","名称","步骤数","已完成数"};
	
	private int plan_id;
	private String user_id;
	private int plan_order;
	private String plan_name;
	private Date create_time;
	private int step_count;
	private int start_step_count;
	private int finished_step_count;
	
	
	
	public int getPlan_id() {
		return plan_id;
	}



	public void setPlan_id(int plan_id) {
		this.plan_id = plan_id;
	}



	public String getUser_id() {
		return user_id;
	}



	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}



	public int getPlan_order() {
		return plan_order;
	}



	public void setPlan_order(int plan_order) {
		this.plan_order = plan_order;
	}



	public String getPlan_name() {
		return plan_name;
	}



	public void setPlan_name(String plan_name) {
		this.plan_name = plan_name;
	}



	public Date getCreate_time() {
		return create_time;
	}



	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}



	public int getStep_count() {
		return step_count;
	}



	public void setStep_count(int step_count) {
		this.step_count = step_count;
	}



	public int getStart_step_count() {
		return start_step_count;
	}



	public void setStart_step_count(int start_step_count) {
		this.start_step_count = start_step_count;
	}



	public int getFinished_step_count() {
		return finished_step_count;
	}



	public void setFinished_step_count(int finished_step_count) {
		this.finished_step_count = finished_step_count;
	}



	public String getCell(int col){
		if(col==0) return this.plan_order+"";
		else if(col==1) return this.plan_name;
		else if(col==2) return this.step_count+"";
		else if(col==3) return this.finished_step_count+"";
		else return "";
	}

}
