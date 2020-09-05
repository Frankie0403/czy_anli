package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBPool;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

public class ExamplePlanManager implements IPlanManager {

	@Override
	public BeanPlan addPlan(String name) throws BaseException {
		if(name==null||"".equals(name)) throw new BusinessException("请输入计划名称");
		
		BeanPlan p=new BeanPlan();
		Connection conn=null;
		try {
			conn=DBPool.getInstance().getConnection();
			String sql="select plan_id from tbl_plan where user_id=? and plan_name=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUserid());
			pst.setString(2, name);
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("存在同名计划");
			}
			rs.close();
			pst.close();
			
			
			sql="select max(plan_order) from tbl_plan where user_id=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUserid());
			rs = pst.executeQuery();
			int num;
			if(!rs.next()) num=1;
			else num=rs.getInt(1)+1;
			
			sql="insert into tbl_plan(user_id,plan_order,plan_name,create_time,step_count,start_step_count,finished_step_count) "
					+ "values(?,?,?,?,0,0,0)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUserid());
			pst.setInt(2, num);
			pst.setString(3, name);
			pst.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.execute();
			
			sql="select max(plan_id) from tbl_plan where user_id=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1,BeanUser.currentLoginUser.getUserid());
			rs=pst.executeQuery();
			int pid;
			if(rs.next()) pid=rs.getInt(1)+1;
			else pid=1;
			
			p.setPlan_id(pid);
			p.setPlan_order(num);
			p.setPlan_name(name);
			p.setStep_count(0);
			p.setCreate_time(new java.sql.Timestamp(System.currentTimeMillis()));
			p.setFinished_step_count(0);
			
			rs.close();
			pst.close();
			
			return p;
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
	public List<BeanPlan> loadAll() throws BaseException {
		List<BeanPlan> result=new ArrayList<BeanPlan>();
		Connection conn=null;
		try {
			conn=DBPool.getInstance().getConnection();
			String sql = "select * from tbl_plan where user_id = ? order by plan_order";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUserid());
			java.sql.ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				BeanPlan p = new BeanPlan();
				p.setPlan_id(rs.getInt(1));
				p.setUser_id(rs.getString(2));
				p.setPlan_order(rs.getInt(3));
				p.setPlan_name(rs.getString(4));
				p.setCreate_time(rs.getDate(5));
				p.setStep_count(rs.getInt(6));
				p.setStart_step_count(rs.getInt(7));
				p.setFinished_step_count(rs.getInt(8));
				result.add(p);
			}
			rs.close();
			pst.close();
			return result;
			
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
	public void deletePlan(BeanPlan plan) throws BaseException {
		int plan_id = 1;
		plan_id = plan.getPlan_id();
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			
			String sql = "select count(*) from tbl_step where plan_id = " + plan_id;
			java.sql.Statement st = conn.createStatement();
			java.sql.ResultSet rs = st.executeQuery(sql);
			if(rs.next()) {
				if(rs.getInt(1) > 0) {
					rs.close();
					st.close();
					throw new BusinessException("该计划名已存在步骤，不能删除");
				}
			}
			rs.close();
			
			sql = "select plan_order,user_id from tbl_plan where plan_id = " + plan_id;
			rs = st.executeQuery(sql);
			int plan_ord = 0;
			String plan_user_id = null;
			if(rs.next()) {
				plan_ord = rs.getInt(1);
				plan_user_id = rs.getString(2);
			}
			else {
				throw new BusinessException("该计划不存在");
			}
			rs.close();
			if(!BeanUser.currentLoginUser.getUserid().equals(plan_user_id)) {
				st.close();
				throw new BusinessException("不能删除其他用户的计划");
			}
					
			sql = "delete from tbl_plan where plan_id = " + plan_id;
			st.execute(sql);
			sql="delete from tbl_step where plan_id="+plan_id;
			st.execute(sql);
			
			sql = "update tbl_plan set plan_order = plan_order - 1 where user_id = ? and plan_order > " + plan_ord;
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, plan_user_id);
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
