package com.vivifram.second.hitalk.ui.page;

import com.vivifram.second.hitalk.R;
import com.vivifram.second.hitalk.base.LayoutInject;
import com.vivifram.second.hitalk.bean.address.SchoolMate;
import com.vivifram.second.hitalk.manager.chat.SchoolMatesManager;
import com.vivifram.second.hitalk.ui.page.layout.AddressFragmentSub1Layout;
import com.vivifram.second.hitalk.ui.springview.widget.SpringView;
import com.zuowei.utils.bridge.handler.ToolKit;
import com.zuowei.utils.pinyin.LetterComparator;

import java.util.Collections;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by zuowei on 16-10-11.
 */
@LayoutInject(name = "AddressFragmentSub1Layout")
public class AddressFragmentSub1 extends LazyFragment<AddressFragmentSub1Layout> {
    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        updateSchoolMates(task -> {
            if (task.getResult() != null) {
                mLayout.setData(task.getResult());
            }
            return null;
        });
        mLayout.setOnFreshListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                updateSchoolMates(task -> {
                    mLayout.notifyFreshDone();
                    if (task.getResult() != null) {
                        mLayout.setData(task.getResult(),500);
                    }
                    return null;
                });
            }

            @Override
            public void onLoadmore() {

            }
        });
    }

    private void updateSchoolMates(Continuation<List<SchoolMate>,Void> continuation) {
        SchoolMatesManager.getInstance().queryAllSchoolMates()
                .continueWith(task -> {
                    final List<SchoolMate> result = task.getResult();
                    if (result != null){
                        Collections.sort(result,new LetterComparator<SchoolMate>());
                        ToolKit.runOnMainThreadAsync(() -> {
                            if (continuation != null) {
                                Task.<List<SchoolMate>>forResult(result)
                                        .continueWith(continuation);
                            }
                        });
                    }else {
                        if (continuation != null) {
                            Task.<List<SchoolMate>>forResult(null)
                                    .continueWith(continuation);
                        }
                    }

                    return null;
                });
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_address_sub_1_layout;
    }
}
