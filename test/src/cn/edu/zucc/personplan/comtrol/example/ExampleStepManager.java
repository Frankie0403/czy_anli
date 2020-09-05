package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

public class ExampleStepManager implements IStepManager {

	@Override
	public void add(BeanPlan plan, String name, String planstartdate,
			String planfinishdate) throws BaseException {
		if(name==null||"".equals(name)) throw new BusinessException("请输入步骤名称");
		if(planstartdate == null || "".equals(planstartdate))throw new BusinessException("计划开始时间不能为空");
		if(planfinishdate == null || "".equals(planfinishdate))throw new BusinessException("计划完成时间不能为空");
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			BeanStep s=new BeanStep();
			int plan_id=plan.getPlan_id();
			
			String sql="select step_id from tbl_step where step_name=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, name);
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("存在同名步骤");
			}
			rs.close();
			pst.close();
			
			
			sql="select max(step_order) from tbl_step where plan_id=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, plan.getPlan_id());
			rs = pst.executeQuery();
			int num;
			if(!rs.next()) num=1;
			else num=rs.getInt(1)+1;
			
			sql="insert into tbl_step(plan_id,step_order,step_name,plan_begin_time,plan_end_time) "
					+ "values(?,?,?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, plan_id);
			pst.setInt(2, num);
			pst.setString(3, name);
			pst.setString(4, planstartdate);
			pst.setString(5, planfinishdate);
			pst.execute();
			
			sql="select max(step_id) from tbl_step where plan_id=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1,plan.getPlan_id());
			rs=pst.executeQuery();
			int sid;
			if(rs.next()) sid=rs.getInt(1)+1;
			else sid=1;
			
			s.setStep_id(sid);
			s.setStep_order(num);
			s.setStep_name(name);
			s.setPlan_id(plan_id);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");	
			s.setPlan_begin_time(simpleDateFormat.parse(planstartdate));
			s.setPlan_end_time(simpleDateFormat.parse(planfinishdate));
			
			rs.close();
			pst.close();
			
		}catch (Exception e) {
			throw new DbException(e);
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public List<BeanStep> loadSteps(BeanPlan plan) throws BaseException {
		List<BeanStep> result=new ArrayList<BeanStep>();
		int plan_id = 0;
		plan_id = plan.getPlan_id();
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			
			String sql = "select * from tbl_step where plan_id = ? order by step_order";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, plan_id);
			java.sql.ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				BeanStep s = new BeanStep();
				s.setStep_id(rs.getInt(1));
				s.setPlan_id(rs.getInt(2));
				s.setStep_order(rs.getInt(3));
				s.setStep_name(rs.getString(4));
				s.setPlan_begin_time(rs.getDate(5));
				s.setPlan_end_time(rs.getDate(6));
				s.setReal_begin_time(rs.getDate(7));
				s.setReal_end_time(rs.getDate(8));
				result.add(s);
			}
			rs.close();
			pst.close();
			
		}catch(Exception ex) {
			throw new DbException(ex);
		}finally {
			if(conn != null) {
				try {
					conn.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public void deleteStep(BeanStep step) throws BaseException {
		int step_id=step.getStep_id();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
		
			String sql="select step_order,user_id,tbl_plan.plan_id from tbl_step,tbl_plan where "
					+ "tbl_plan.plan_id=tbl_step.plan_id and step_id = "+step_id;
			java.sql.Statement st =conn.createStatement();
			java.sql.ResultSet rs = st.executeQuery(sql);
			int step_ord=0;
			String plan_user_id=null;
			int plan_id=0;
			if(rs.next()) {
				step_ord=rs.getInt(1);
				plan_user_id=rs.getString(2);
				plan_id=rs.getInt(3);
			}else {
				throw new BusinessException("该步骤不存在");
			}
			rs.close();
			if(!BeanUser.currentLoginUser.getUserid().equals(plan_user_id)) {
				throw new BusinessException("不能删除他人步骤");
			}
			
			
			
			sql="delete from tbl_step where step_id = "+step_id;
			st.execute(sql);
			st.close();
			
			sql="update tbl_step set step_order=step_order-1 where plan_id=? and step_order>"+step_ord;
			java.sql.PreparedStatement pst =conn.prepareStatement(sql);
			pst.setInt(1, plan_id);
			pst.execute();
			
			sql = "update tbl_plan set step_count = step_count - 1 where plan_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, plan_id);
			pst.execute();
			pst.close();
		}catch (SQLException e) {
			throw new DbException(e);
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public void startStep(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql = "select real_end_time from tbl_step where step_id=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("该步骤已结束");
			}
			
			sql="update tbl_step set real_begin_time=now() where step_id=?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			pst.execute();
			
			sql="update tbl_plan set start_step_count=start_step_count+1 where plan_id=?";
			pst.setInt(1, step.getPlan_id());
			pst.execute();
			pst.close();
			
			
		}catch (SQLException e) {
			throw new DbException(e);
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		
		
	}

	@Override
	public void finishStep(BeanStep step) throws BaseException {
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql = "select real_begin_time from tbl_step where step_id=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("该步骤未开始");
			}
			
			sql="update tbl_step set real_end_time = now() where step_id = ?";
			pst=conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			pst.execute();
			
			sql="update tbl_plan set start_step_count=start_step_count-1,finished_step_count=finished_step_count+1 where plan_id=?";
			pst.setInt(1, step.getPlan_id());
			pst.execute();
			pst.close();
			
			
		}catch (SQLException e) {
			throw new DbException(e);
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	@Override
	public void moveUp(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		int step_id;
		step_id = step.getStep_id();
		int plan_id = 0;
		int step_order = 0;
		int step_orderdown = 0;
		int step_orderup= 0;
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = "select tbl_plan.plan_id,tbl_step.step_order"
					+ " from tbl_plan,tbl_step"
					+ " where tbl_plan.plan_id = tbl_step.plan_id"
					+ " and step_id = " + step_id;
			java.sql.Statement st = conn.createStatement();
			java.sql.ResultSet rs = st.executeQuery(sql);
			if(rs.next()) {
				plan_id = rs.getInt(1);
				step_order = rs.getInt(2);
				step_orderup = step_order - 1;
				step_orderdown = step_order + 1;
				if(step_orderup == 0) {
					rs.close();
					st.close();
					throw new BusinessException("该步骤无法上移");
				}
			}
			else {
				rs.close();
				st.close();
				throw new BusinessException("该步骤不存在");
			}
			rs.close();
			
			sql = "update tbl_step set step_order = ? where plan_id = ? and step_order = " + step_orderup;
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, step_orderdown);
			pst.setInt(2, plan_id);
			pst.execute();
			pst.close();
					
			sql = "update tbl_step set step_order = ? where step_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, step_orderup);
			pst.setInt(2, step_id);
			pst.execute();
			pst.close();
			
			
		}catch(Exception ex) {
			throw new DbException(ex);
		}finally {
			if(conn != null) {
				try {
					conn.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void moveDown(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		int step_id;
		step_id = step.getStep_id();
		int plan_id = 0;
		int step_order = 0;
		int step_orderdown = 0;
		int step_orderup= 0;
		int step_count=0;
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = "select tbl_plan.plan_id,tbl_step.step_order，tbl_plan.step_count"
					+ " from tbl_plan,tbl_step"
					+ " where tbl_plan.plan_id = tbl_step.plan_id"
					+ " and step_id = " + step_id;
			java.sql.Statement st = conn.createStatement();
			java.sql.ResultSet rs = st.executeQuery(sql);
			if(rs.next()) {
				plan_id = rs.getInt(1);
				step_order = rs.getInt(2);
				step_count = rs.getInt(3);
				step_orderup = step_order - 1;
				step_orderdown = step_order + 1;
				if(step_orderdown > step_count) {
					rs.close();
					st.close();
					throw new BusinessException("该步骤无法下+移");
				}
			}
			else {
				rs.close();
				st.close();
				throw new BusinessException("该步骤不存在");
			}
			rs.close();
			
			sql = "update tbl_step set step_order = ? where plan_id = ? and step_order = " + step_orderup;
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, step_orderup);
			pst.setInt(2, plan_id);
			pst.execute();
			pst.close();
					
			sql = "update tbl_step set step_order = ? where step_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, step_orderdown);
			pst.setInt(2, step_id);
			pst.execute();
			pst.close();
			
			
		}catch(Exception ex) {
			throw new DbException(ex);
		}finally {
			if(conn != null) {
				try {
					conn.close();
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	

	
	

}
