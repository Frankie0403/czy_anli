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
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.DbException;

public class ExamplePlanManager implements IPlanManager {

	@Override
	public BeanPlan addPlan(String name) throws BaseException {
		BeanPlan p=new BeanPlan();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select max(plan_order) from tbl_plan group by user_id=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUserid());
			java.sql.ResultSet rs = pst.executeQuery();
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
			
			p.setPlan_order(num);
			p.setPlan_name(name);
			p.setStep_count(0);
			p.setFinished_step_count(0);
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
		return null;
	}

	@Override
	public List<BeanPlan> loadAll() throws BaseException {
		List<BeanPlan> result=new ArrayList<BeanPlan>();
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select plan_order,plan_name,step_count,finished_step_count from tbl_plan "
					+ "where user_id=? order by plan_order";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUserid());
			java.sql.ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				BeanPlan p=new BeanPlan();
				p.setPlan_order(rs.getInt(1));
				p.setPlan_name(rs.getString(2));
				p.setStep_count(rs.getInt(3));
				p.setFinished_step_count(rs.getInt(4));
				result.add(p);
			}
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
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			if(plan.getStep_count()!=plan.getFinished_step_count()) throw new BusinessException("步骤仍在进行中");
			String sql="delete from tbl_plan where user_id=? and plan_order=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1, plan.getUser_id());
			pst.setInt(2, plan.getPlan_order());
			pst.execute();
			
			
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

}
