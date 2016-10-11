package com.zuowei.utils.provider;


import com.vivifram.second.hitalk.bean.blackboard.BnItem;
import com.vivifram.second.hitalk.bean.blackboard.CommentItem;
import com.vivifram.second.hitalk.bean.blackboard.FavortItem;
import com.vivifram.second.hitalk.manager.LocalIdManager;
import com.vivifram.second.hitalk.state.ActionCallback;
import com.vivifram.second.hitalk.state.DoneCallback;
import com.vivifram.second.hitalk.state.SingleResult;
import com.zuowei.dao.greendao.User;
import com.zuowei.utils.common.DateUtils;
import com.zuowei.utils.common.NLog;
import com.zuowei.utils.common.TagUtil;
import com.zuowei.utils.helper.BnHelper;
import com.zuowei.utils.helper.HiTalkHelper;
import com.zuowei.utils.helper.UserBeanCacheHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * Created by zuowei on 16-8-14.
 */

public class BnDataProvider {
	private static final String TAG = TagUtil.makeTag(BnDataProvider.class);
	public static final User curUser = getCurrentUser();

	public static void refreshBnDatas(final DoneCallback<BnItem> callback) {
		if (callback == null) return;
		BnHelper.getInstance().loadBnItemsAsync(new DoneCallback<BnItem>() {
			@Override
			public void done(List<BnItem> list, Exception e) {
				if (e == null){
					if (list == null){
						callback.done(new ArrayList<BnItem>(),null);
					}else {
						callback.done(list,null);
					}
				}else {
					NLog.e(TagUtil.makeTag(BnDataProvider.class),e);
					callback.done(new ArrayList<BnItem>(),null);
				}
			}
		});
	}

	public static void loadMoreBnDatas(final DoneCallback<BnItem> callback,BnItem lastBt){
		if (callback == null) return;
		if (lastBt == null){
			callback.done(new ArrayList<BnItem>(),null);
		}
		BnHelper.getInstance().loadMoreBnItemsAsync(new DoneCallback<BnItem>() {
			@Override
			public void done(List<BnItem> list, Exception e) {
				if (e == null && list != null){
					callback.done(list,null);
				}else {
					NLog.e(TagUtil.makeTag(BnDataProvider.class),e);
					callback.done(new ArrayList<BnItem>(),null);
				}
			}
		},lastBt);
	}

	public static void createNormalBnData(String content, final List<String> photos, final SingleResult singleResult){
		if (content != null) {
			final BnItem bnItem = new BnItem();
			bnItem.setId(LocalIdManager.getInstance().createLocalId());
			User user = new User();
			UserBeanCacheHelper.AvUserToUser(HiTalkHelper.getInstance().getCurrentUser(), user);
			bnItem.setUser(user);
			bnItem.setContent(content);
			bnItem.setCreateTime(new Date());
			bnItem.setHasComment(false);
			bnItem.setHasFavort(false);
			bnItem.setAll(true);
			if (photos != null && photos.size() > 0) {
				bnItem.setPhotos(photosToEntrys(photos));
				bnItem.setType("2");
				bnItem.setAll(false);
			}
			BnHelper.getInstance().saveBnItemAsync(bnItem, new SingleResult() {
				@Override
				public void done(Object result, Exception e) {
					if (singleResult != null) {
						singleResult.done(bnItem,e);
					}
				}
			});
		}
	}

	private static HashMap<String,String> photosToEntrys(List<String> photos){
		HashMap<String,String> results = new HashMap<>();
		if (photos != null) {
			for (String photo : photos) {
				results.put(photo,photo);
			}
		}
		return results;
	}

	public static FavortItem createCurUserFavortItem(String bnId) {
		FavortItem item = new FavortItem();
		item.setBnRemoteId(bnId);
		item.setId(LocalIdManager.getInstance().createLocalId());
		item.setUser(getCurrentUser());
		item.setCreateTime(new Date());
		BnHelper.getInstance().saveFavortAsync(item,bnId);
		return item;
	}

	public static void deleteFavortItem(FavortItem favortItem){
		if (favortItem != null) {
			favortItem.delete();
		}
	}


	/**
	 * 创建发布评论
	 * @return
	 */
	public static CommentItem createPublicComment(String content,String bnId){
		CommentItem item = new CommentItem();
		item.setBnRemoteId(bnId);
		item.setId(LocalIdManager.getInstance().createLocalId());
		item.setContent(content);
		User currentUser = getCurrentUser();
		item.setUser(currentUser);
		item.setCreateTime(new Date());
		BnHelper.getInstance().saveCommentAsync(item,bnId);
		return item;
	}
	
	/**
	 * 创建回复评论
	 * @return
	 */
	public static CommentItem createReplyComment(User replyUser, String content,String bnId){
		CommentItem item = new CommentItem();
		item.setBnRemoteId(bnId);
		item.setId(LocalIdManager.getInstance().createLocalId());
		item.setContent(content);
		item.setUser(getCurrentUser());
		item.setToReplyUser(replyUser);
		item.setCreateTime(new Date());
		BnHelper.getInstance().saveCommentAsync(item,bnId);
		return item;
	}

	public static void deleteComment(CommentItem commentItem){
		if (commentItem != null) {
			commentItem.delete();
		}
	}
	
	
	private static User getCurrentUser(){
		User user = new User();
		UserBeanCacheHelper.AvUserToUser(HiTalkHelper.getInstance().getCurrentUser(),user);
		return user;
	}
}
