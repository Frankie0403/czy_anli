package cn.edu.zucc.personplan.comtrol.example;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import cn.edu.zucc.personplan.util.DbException;
import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.BusinessException;
import cn.edu.zucc.personplan.util.DBUtil;

public class ExampleUserManager implements IUserManager {

	@Override
	public BeanUser reg(String userid, String pwd,String pwd2) throws BaseException {
		if(userid==null||"".equals(userid)) throw new BusinessException("�˺Ų���Ϊ��");
		if(pwd==null||"".equals(pwd)) throw new BusinessException("���벻��Ϊ��");
		if(!pwd2.equals(pwd)) throw new BusinessException("�������벻һ��");
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select user_id from tbl_user where user_id=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1,userid);
			java.sql.ResultSet rs=pst.executeQuery();
			if(rs.next()) throw new BusinessException("��½�˺��Ѿ�����");
			rs.close();
			pst.close();
			sql="insert tbl_user(user_id,user_pwd,register_time) VALUES(?,?,?)";
			pst=conn.prepareStatement(sql);
			pst.setString(1, userid);
			pst.setString(2, pwd);
			pst.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			pst.execute();
			
			BeanUser user=new BeanUser();
			user.setUserid(userid);
			user.setPwd(pwd);
			user.setRegister_time(new Date());
			
			return user;
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
	public BeanUser login(String userid, String pwd) throws BaseException {
		Connection conn=null;
		BeanUser user=new BeanUser();
		if(userid==null||"".equals(userid)) throw new BusinessException("�˺Ų���Ϊ��");
		try {
			conn=DBUtil.getConnection();
			String sql="select user_pwd from tbl_user where user_id=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1,userid);
			java.sql.ResultSet rs=pst.executeQuery();
			if(!rs.next()) {
				rs.close();
				pst.close();
				throw new BusinessException("��½�˺Ų�����");
			}
			if(pwd==null||"".equals(pwd)) throw new BusinessException("���벻��Ϊ��");
			user.setPwd(pwd);
			user.setUserid(userid);
			if(!user.getPwd().equals(rs.getString(1))) throw new BusinessException("�������");
			return user;
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
	public void changePwd(BeanUser user, String oldPwd, String newPwd,
			String newPwd2) throws BaseException {
		Connection conn=null;
		try {
			conn=DBUtil.getConnection();
			String sql="select user_pwd from tbl_user where user_id=?";
			java.sql.PreparedStatement pst=conn.prepareStatement(sql);
			pst.setString(1,user.getUserid());
			java.sql.ResultSet rs=pst.executeQuery();
			rs.next();
			if(!user.getPwd().equals(rs.getString(1))) throw new BusinessException("�������");
			
			if(newPwd==null||"".equals(newPwd)) throw new BusinessException("���벻��Ϊ��");
			if(!newPwd2.equals(newPwd)) throw new BusinessException("�������벻��ͬ");
			sql="update tbl_user set user_pwd=? where user_id=?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, newPwd);
			pst.setString(2,user.getUserid());
			user.setPwd(newPwd);
			
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

}
