package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by Noverish on 2016-07-08.
 */
public class TicketPurchaseListActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_purchase_list);
        setSearchEnable(true);
        setToolbarTitle("주차권 구매내역");
    }
}
