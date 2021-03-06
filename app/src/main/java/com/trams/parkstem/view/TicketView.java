package com.trams.parkstem.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.activity.ManagePurchaseActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.others.HttpImageThread;
import com.trams.parkstem.server.ServerClient;

import java.util.Calendar;

/**
 * Created by Noverish on 2016-07-21.
 */
public class TicketView extends LinearLayout {
    private Context context;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;

    private ServerClient.Ticket ticket;
    private ServerClient.TicketPurchase purchase;
    private TicketViewData data;
    private String parkName = "";

    private RelativeLayout abovecontent;

    private TextView ticketName;
    private TextView shortAddress;
    private TextView buttonNameText;
    private RelativeLayout showDetailButton;

    private ImageView parkImage;

    private LinearLayout endDateLayout;
    private TextView startDate;
    private TextView endDate;

    private TextView newAddress;
    private TextView oldAddress;

    private TextView beforePriceTerm;
    private TextView beforePrice;
    private TextView afterPriceTerm;
    private TextView afterPrice;

    private LinearLayout expiryDateLayout;
    private TextView term;

    private boolean detailOpen = true;
    private boolean isItLongTicket;

    public TicketView(final Context contextParam, ServerClient.Ticket ticket, String buttonName, boolean purchaseButtonOn, boolean alwaysOpened, boolean calendarButton) {
        super(contextParam);
        this.ticket = ticket;
        init(contextParam);

        setData(ticket, buttonName);

        applyOptions(ticket.gubun == ServerClient.Ticket.LONG_TICKET_GUBUN, purchaseButtonOn, alwaysOpened, calendarButton);
    }

    public TicketView(final Context contextParam, ServerClient.TicketPurchase purchase, String buttonName, boolean purchaseButtonOn, boolean alwaysOpened, boolean calendarButton) {
        super(contextParam);
        init(contextParam);

        setData(purchase, buttonName);

        applyOptions(purchase.gubun == ServerClient.Ticket.LONG_TICKET_GUBUN, purchaseButtonOn, alwaysOpened, calendarButton);
    }

    private void init(Context contextParam) {
        this.context = contextParam;
        startDateCalendar = Calendar.getInstance();

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.ticket_item, this, true);

        abovecontent = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_above_layout);

        ticketName = (TextView) findViewById(R.id.long_ticket_mobile_item_name);
        shortAddress = (TextView) findViewById(R.id.long_ticket_mobile_item_place);
        buttonNameText = (TextView) findViewById(R.id.long_ticket_mobile_item_button_name);
        showDetailButton = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_view);

        parkImage = (ImageView) findViewById(R.id.ticket_mobile_item_picture);

        endDateLayout = (LinearLayout) findViewById(R.id.ticket_item_end_date_layout);
        startDate = (TextView) findViewById(R.id.long_ticket_mobile_item_start_date);
        endDate = (TextView) findViewById(R.id.long_ticket_mobile_item_end_date);

        newAddress = (TextView) findViewById(R.id.ticket_view_new_address);
        oldAddress = (TextView) findViewById(R.id.ticket_view_old_address);

        beforePriceTerm = (TextView) findViewById(R.id.long_ticket_mobile_item_before_price_term_name);
        beforePrice = (TextView) findViewById(R.id.long_ticket_mobile_item_before_price_amount);
        afterPriceTerm = (TextView) findViewById(R.id.long_ticket_mobile_item_after_price_term_name) ;
        afterPrice = (TextView) findViewById(R.id.long_ticket_mobile_item_after_price_amount);

        expiryDateLayout = (LinearLayout) findViewById(R.id.ticket_item_due_date_layout);
        term = (TextView) findViewById(R.id.ticket_item_term);
    }

    private void setData(ServerClient.Ticket ticket, String buttonName) {
        ServerClient.ParkInfo parkInfo;
        try {
            parkInfo = ServerClient.getInstance().parkInfo(ticket.local_id);

            HttpImageThread thread = new HttpImageThread(parkInfo.local_photo1);
            parkImage.setBackground(new BitmapDrawable(getResources(), thread.getImage()));
            parkName = parkInfo.local_name;
        } catch (ServerClient.ServerErrorException ex) {
            ex.printStackTrace();
            Toast.makeText(context, "주차장 정보를 불러오는데 실패했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
            parkInfo = new ServerClient.ParkInfo();
        }

        if(ticket.gubun == ServerClient.Ticket.SHORT_TICEKT_GUBUN) {
            data = new TicketViewData(ticket.ticket_name, parkInfo.short_address, buttonName,
                    startDateCalendar, null,
                    parkInfo.new_address, parkInfo.local_address,
                    ticket.available_time + "시간", ticket.original_price, ticket.price,
                    ticket.term);

            fillData(data);
        } else {
            data = new TicketViewData(ticket.ticket_name, parkInfo.short_address, buttonName,
                    ticket.start_date, ticket.end_date,
                    parkInfo.new_address, parkInfo.local_address,
                    "1달", ticket.original_price, ticket.price,
                    "");

            fillData(data);
        }
    }

    private void setData(ServerClient.TicketPurchase purchase, String buttonName) {
        this.purchase = purchase;

        ServerClient.ParkInfo parkInfo;
        try {
            parkInfo = ServerClient.getInstance().parkInfo(purchase.local_id);

            HttpImageThread thread = new HttpImageThread(parkInfo.local_photo1);
            parkImage.setBackground(new BitmapDrawable(getResources(), thread.getImage()));
            parkName = parkInfo.local_name;
        } catch (ServerClient.ServerErrorException ex) {
            ex.printStackTrace();
            Toast.makeText(context, "주차장 정보를 불러오는데 실패했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();

            parkInfo = new ServerClient.ParkInfo();
        }

        if(purchase.gubun == ServerClient.Ticket.SHORT_TICEKT_GUBUN) {
            data = new TicketViewData(purchase.ticket_name, parkInfo.short_address, buttonName,
                    purchase.pay_date, null,
                    parkInfo.new_address, parkInfo.local_address,
                    purchase.available_time + "시간", purchase.original_price, purchase.price,
                    purchase.term);

            fillData(data);
        } else {
            data = new TicketViewData(purchase.ticket_name, parkInfo.short_address, buttonName,
                    purchase.start_date, purchase.end_date,
                    parkInfo.new_address, parkInfo.local_address,
                    "1달", purchase.original_price, purchase.price,
                    "");

            fillData(data);
        }
    }

    private void fillData(TicketViewData data) {

        this.ticketName.setText(data.ticketName);
        this.shortAddress.setText(data.shortAddress);
        this.buttonNameText.setText(data.buttonName);

        if(data.startDate != null)
            this.startDate.setText(Essentials.calendarToDateWithDot(data.startDate));
        else
            this.startDate.setText("");

        if(data.endDate != null)
            this.endDate.setText(Essentials.calendarToDateWithDot(data.endDate));
        else
            this.endDate.setText("");

        this.newAddress.setText(data.newAddress);
        this.oldAddress.setText(data.oldAddress);

        this.beforePriceTerm.setText(data.priceTerm);
        this.beforePrice.setText(Essentials.numberWithComma(data.beforePrice));
        this.afterPriceTerm.setText(data.priceTerm);
        this.afterPrice.setText(Essentials.numberWithComma(data.afterPrice));

        this.term.setText(data.term);
    }

    private void applyOptions(boolean isItLongTicket, boolean purchaseButtonOn, boolean alwaysOpened, final boolean calendarButton) {
        this.isItLongTicket = isItLongTicket;

        if(isItLongTicket) {
            removeView(expiryDateLayout);
        } else {
            removeView(endDateLayout);
            String str = startDate.getText().toString();
            startDate.setText(str + " 구매");
        }

        //구매 버튼 여부에 따라 구매버튼 삭제
        RelativeLayout purchaseButton = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_bottom_layout);
        if(purchaseButtonOn) {
            purchaseButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickPurchaseButton();
                }
            });

            startDate.setText(startDate.getText().toString().replace("구매",""));
        } else {
            removeView(purchaseButton);
        }

        if(!alwaysOpened) {
            showDetailButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowDetailButtonClicked();
                }
            });

            detailOpen = true;
            onShowDetailButtonClicked();
        } else {
            RelativeLayout abovecontent = (RelativeLayout) findViewById(R.id.long_ticket_mobile_item_above_layout);
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_3));
        }

        ImageView calendarImageView = (ImageView) findViewById(R.id.long_ticket_mobile_item_calender);
        if(calendarButton) {
            calendarImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            startDateCalendar.set(Calendar.YEAR, year);
                            startDateCalendar.set(Calendar.MONTH, monthOfYear);
                            startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            refreshDate();
                        }
                    };

                    new DatePickerDialog(context, dateSetListener,
                            startDateCalendar.get(Calendar.YEAR),
                            startDateCalendar.get(Calendar.MONTH),
                            startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        } else {
            removeView(calendarImageView);
        }
    }

    public void makeUsed() {
        int grayForText = ContextCompat.getColor(context, R.color.gray_for_text);
        int black = ContextCompat.getColor(context, R.color.BLACK);

        //above layout 색 변경
        abovecontent.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_5));

        //show detail button 설정
        showDetailButton.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_4));
        showDetailButton.removeAllViews();

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(params);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_check_w));
        showDetailButton.addView(imageView);

        //split bar 설정
        RelativeLayout splitBar = (RelativeLayout) findViewById(R.id.split_bar);
        splitBar.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_6));

        afterPriceTerm.setTextColor(black);
        TextView won = (TextView) findViewById(R.id.long_ticket_mobile_item_won);
        won.setTextColor(black);
        afterPrice.setTextColor(black);

        //주차장 사진 흑백처리
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        parkImage.setColorFilter(filter);

        //사용완료 도장 보이게 함
        ImageView usedView = (ImageView) findViewById(R.id.ticket_item_used);
        usedView.setVisibility(VISIBLE);

        //시작 날짜 "구매" 글자 변경
        if(!isItLongTicket) {
            startDate.setText(startDate.getText().toString().replace("구매","사용"));
        }

        //term 색 변경
        try {
            TextView term1 = (TextView) findViewById(R.id.ticket_item_term1);
            TextView term2 = (TextView) findViewById(R.id.ticket_item_term2);
            term.setTextColor(grayForText);
            term1.setTextColor(grayForText);
            term2.setTextColor(grayForText);
        } catch (NullPointerException ex) {

        }

        if(!isItLongTicket) {
            try {
                startDate.setText(purchase.ticket_used);
            } catch (NullPointerException ex) {

            }
        }
    }

    private void onShowDetailButtonClicked() {
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
//
//        LayoutParams layoutParamsOn_60 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        LayoutParams layoutParamsOff = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        LayoutParams layoutParamsOn = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout belowcontent = (LinearLayout) findViewById(R.id.long_ticket_mobile_item_below_layout);

        detailOpen = !detailOpen;

        if(detailOpen) {
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.btn_3));
            belowcontent.setLayoutParams(layoutParamsOn);
        } else {
            abovecontent.setBackgroundColor(ContextCompat.getColor(context, R.color.WHITE));
            belowcontent.setLayoutParams(layoutParamsOff);
        }
    }

    private void refreshDate() {
        startDate.setText(Essentials.calendarToDateWithDot(startDateCalendar));

        endDateCalendar = (Calendar) startDateCalendar.clone();
        endDateCalendar.add(Calendar.MONTH, 1);
        endDateCalendar.add(Calendar.DAY_OF_MONTH, -1);

        endDate.setText(Essentials.calendarToDateWithDot(endDateCalendar));
    }

    public String getTicketName() {
        return data.ticketName;
    }

    public String getNewParkAddress() {
        return data.newAddress;
    }

    public String getOldParkAddress() {
        return data.oldAddress;
    }

    public String getShortParkAddress() {
        return data.shortAddress;
    }

    public String getParkName() {
        return parkName;
    }

    public void setStartDate(Calendar startDateCalendar) {
        this.startDateCalendar = startDateCalendar;

        refreshDate();
    }

    public Calendar getStartDate() {
        return startDateCalendar;
    }

    public Calendar getEndDate() {
        return endDateCalendar;
    }

    public void deleteGUMEword() {
        startDate.setText(startDate.getText().toString().replace("구매",""));
    }

    private void onClickPurchaseButton() {
        boolean ok;
        int idx = 1;
        ServerClient.TicketInfo ticketInfo;

        if(ticket != null) {
            idx = ticket.idx;
        } else if(purchase != null) {
            idx = purchase.idx;
        } else {
            Log.e("NEVER HAPPEN","there is no idx");
        }

        try {
            ticketInfo = ServerClient.getInstance().ticketInfo(idx + "");

            if(!ticketInfo.card_use) {
                Toast.makeText(context, "결제 카드가 등록되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ticketInfo.certification) {
                Toast.makeText(context, "본인 인증이 되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ServerClient.ServerErrorException ex) {
            Toast.makeText(context, "티켓 정보를 불러오는데 문제가 발생하였습니다. - " + ex.msg,Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, ManagePurchaseActivity.class);
        intent.putExtra("ticket",ticket);
        intent.putExtra("calendar", startDateCalendar.getTimeInMillis());
        context.startActivity(intent);
    }

    @Override
    public void removeView(View view) {
        ((ViewGroup) view.getParent()).removeView(view);
    }

    private static class TicketViewData implements Parcelable {
        private String ticketName;
        private String shortAddress;
        private String buttonName;
        private Calendar startDate;
        private Calendar endDate;
        private String newAddress;
        private String oldAddress;
        private String priceTerm;
        private String beforePrice;
        private String afterPrice;
        private String term;

        public TicketViewData(String ticketName, String shortAddress, String buttonName, Calendar startDate, Calendar endDate, String newAddress, String oldAddress, String priceTerm, String beforePrice, String afterPrice, String term) {
            this.ticketName = ticketName;
            this.shortAddress = shortAddress;
            this.buttonName = buttonName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.newAddress = newAddress;
            this.oldAddress = oldAddress;
            this.priceTerm = priceTerm;
            this.beforePrice = beforePrice;
            this.afterPrice = afterPrice;
            this.term = term;
        }

        public TicketViewData(Parcel in) {
            String[] data = new String[11];

            in.readStringArray(data);

            startDate = Calendar.getInstance();
            endDate = Calendar.getInstance();

            this.ticketName = data[0];
            this.shortAddress = data[1];
            this.buttonName = data[2];
            this.startDate.setTimeInMillis(Long.parseLong(data[3]));
            this.endDate.setTimeInMillis(Long.parseLong(data[4]));
            this.newAddress = data[5];
            this.oldAddress = data[6];
            this.priceTerm = data[7];
            this.beforePrice = data[8];
            this.afterPrice = data[9];
            this.term = data[10];

            if(startDate.getTimeInMillis() == 0)
                startDate = null;

            if(endDate.getTimeInMillis() == 0)
                endDate = null;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if(startDate == null) {
                startDate = Calendar.getInstance();
                startDate.setTimeInMillis(0);
            }

            if(endDate == null) {
                endDate = Calendar.getInstance();
                endDate.setTimeInMillis(0);
            }


            dest.writeStringArray(new String[] {
                    this.ticketName,
                    this.shortAddress,
                    this.buttonName,
                    this.startDate.getTimeInMillis() + "",
                    this.endDate.getTimeInMillis() + "",
                    this.newAddress,
                    this.oldAddress + "",
                    this.priceTerm + "",
                    this.beforePrice,
                    this.afterPrice,
                    this.term + ""});
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public TicketViewData createFromParcel(Parcel in) {
                return new TicketViewData(in);
            }

            public TicketViewData[] newArray(int size) {
                return new TicketViewData[size];
            }
        };
    }
}
