package com.vivifram.second.hitalk.bean;


import com.avos.avoscloud.AVObject;
import com.vivifram.second.hitalk.bean.parser.BeanParser;
import com.zuowei.dao.greendao.Bean;
import com.zuowei.dao.greendao.BeanDao;
import com.zuowei.utils.helper.BeanHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

public class BaseBean implements Serializable{

	public static final int TYPE_SAVE = 0X00;
	public static final int TYPE_DELETE = 0x01;

	public interface ISaveListener{
		void onSaveCompleted(boolean success);
	}

	private static final long serialVersionUID = 1L;
	protected String className;
	{
		className = getClass().getName();
	}

	private ISaveListener saveListener;

	private String localId;

	private Date createTime;

	private int commandType;

	public BeanParser getParser(){
		return null;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getLocalId() {
		return localId;
	}

	public void setSaveListener(ISaveListener saveListener) {
		this.saveListener = saveListener;
	}

	public ISaveListener getSaveListener() {
		return saveListener;
	}

	public String getClassName() {
		return className;
	}

	public void setCommandType(int commandType){
		this.commandType = commandType;
	}

	public int getCommandType() {
		return commandType;
	}

	public static String getCommandTypeFieldName(){
		return "commandType";
	}

	public Date getCreateTime() {
		return createTime;
	}

	public static String getCreateTimeFieldName(){
		return "createTime";
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public <T extends AVObject> Task<T> save(){
		return Task.forResult(null);
	}

	public <T extends AVObject> Task<T> delete(){
		return Task.forResult(null);
	}

	public <T extends AVObject> Task<T> runCommand(){
		return checkCommand().onSuccessTask(new Continuation<Integer, Task<T>>() {
			@Override
			public Task<T> then(Task<Integer> task) throws Exception {
				switch (task.getResult()){
					case TYPE_SAVE:
						return save();
					case TYPE_DELETE:
						return delete();
				}
				return Task.forError(new Exception("unSupported command type"));
			}
		});
	}

	private Task<Integer> checkCommand() {
		return BeanHelper.queryBean(getLocalId()).continueWithTask(new Continuation<Bean, Task<Integer>>() {
			@Override
			public Task<Integer> then(Task<Bean> task) throws Exception {
				Exception e = task.getError();
				if (e != null){
					return Task.forError(e);
				}
				Bean bean = task.getResult();
				if (getClass().getName().equals(bean.getClassName())){
					return Task.forResult(bean.getCommandType());
				}
				return Task.forError(new Exception("checkCommnad because of wrong bean type"));
			}
		});
	}
}
