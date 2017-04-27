package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;


/**
 * Created by PallaviNishanth on 4/26/17.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {


        return new RemoteViewsFactory() {

            private Cursor data = null;
            @Override
            public void onCreate() {
                //Nothing to do
            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);

                if(data.moveToPosition(position)){

                    String symbol = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                    String price = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));
                    String percent_change =
                            data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));

                    views.setTextViewText(R.id.symbol, symbol);
                    views.setTextViewText(R.id.price, "$"+price);
                    views.setTextViewText(R.id.change, percent_change+"%");
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Contract.Quote.COLUMN_SYMBOL,
                        data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                fillInIntent.putExtra(Contract.Quote.COLUMN_PRICE,
                        data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
                views.setOnClickFillInIntent(R.id.widget_list, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
