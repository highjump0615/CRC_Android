/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename CartAdapter.java
 */

package com.camerarental.crc.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerarental.crc.R;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnCartChangeListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Constant;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class CartAdapter extends BaseSwipeAdapter {

    private static LayoutInflater mInflater = null;
    private ArrayList<HashMap> mCartList;

    private Context mContext;
    private int mSelectedCount;

    private boolean mRequestDelete = false;
    private int mIndexToBeDeleted = -1;

    public CartAdapter(Context context, ArrayList<HashMap> values) {
        mContext = context;

        mInflater = LayoutInflater.from(mContext);
        mCartList = values;
    }

    @Override
    public int getCount() {
        return mCartList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_cart_item, parent, false);

        final ViewHolder viewHolder = new ViewHolder();

        viewHolder.swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(position));
        viewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        viewHolder.swipeLayout.setDragDistance(25);

        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                //Toast.makeText(mContext, "Swipe!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose(SwipeLayout layout) {
                if (mRequestDelete && mIndexToBeDeleted != -1) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRequestDelete = false;

                            updateCountAndNotify(mIndexToBeDeleted, 0);
                            mIndexToBeDeleted = -1;
                        }
                    });
                }
            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                //Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.imagePhoto = (ImageView) view.findViewById(R.id.image_photo);
        viewHolder.textInfo = (TextView) view.findViewById(R.id.text_info);
        viewHolder.textCount = (TextView) view.findViewById(R.id.text_count);
        viewHolder.textCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();

                HashMap cartMap = mCartList.get(pos);
                showCountChooseDialog(pos, Integer.parseInt(cartMap.get(Constant.kEquipmentCountKey).toString()));
            }
        });
        viewHolder.buttonDelete = (Button) view.findViewById(R.id.button_delete);
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndexToBeDeleted = (int) v.getTag();
                mRequestDelete = true;
                viewHolder.swipeLayout.close();
            }
        });

        view.setTag(viewHolder);

        viewHolder.textCount.setTag(position);
        viewHolder.buttonDelete.setTag(position);

        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {
        final HashMap cartMap = mCartList.get(position);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.textCount.setTag(position);
        viewHolder.buttonDelete.setTag(position);

        if (cartMap != null) {
            ImageLoader.getInstance().displayImage(EquipmentData.imagePathOfEquipment(cartMap),
                    viewHolder.imagePhoto, CommonUtils.mThumbOptions);

            viewHolder.textInfo.setText(EquipmentData.infoOfEquipment(cartMap));
            viewHolder.textCount.setText("" + EquipmentData.equipmentCartCount(cartMap));
        }
    }

    private void showCountChooseDialog(final int position, final int count) {
        String[] items = new String[11];

        for (int i = 0; i <= 10; i++) {
            items[i] = String.valueOf(i);
        }

        new AlertDialog.Builder(mContext)
                .setSingleChoiceItems(items, count, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedCount = which;
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSelectedCount == 0) {
                            showConfirmDialog(position);
                        } else {
                            updateCountAndNotify(position, mSelectedCount);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showConfirmDialog(final int position) {
        new AlertDialog.Builder(mContext)
                .setMessage("Do you want to remove this equipment?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateCountAndNotify(position, 0);
                    }
                })
                .setNegativeButton("NO", null)
                .create()
                .show();
    }

    private void updateCountAndNotify(int position, int count) {
        EquipmentData.getInstance().changeCartWithIndex(position, count);
        notifyDataSetChanged();

        if (mContext instanceof OnCartChangeListener) {
            ((OnCartChangeListener) mContext).onCartChanged(count);
        }
    }

    public class ViewHolder {
        public SwipeLayout swipeLayout;
        public ImageView imagePhoto;
        public TextView textInfo;
        public TextView textCount;
        public Button buttonDelete;
    }

}
