/**
 * @author LuYongXing
 * @date 2015.01.19
 * @filename EquipmentAdapter.java
 */

package com.camerarental.crc.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerarental.crc.R;
import com.camerarental.crc.data.EquipmentData;
import com.camerarental.crc.listener.OnMyItemClickListener;
import com.camerarental.crc.listener.OnOrderChangeListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class EquipmentAdapter extends BaseAdapter {

    private static final int EXPAND_IMAGE_VIEW_HEIGHT = 200;

    public static final int EquipmentCellStateReload = -1;
    public static final int EquipmentCellStateNone = 0;
    public static final int EquipmentCellStateNormal = 1;
    public static final int EquipmentCellStateExpand = 2;
    public static final int EquipmentCellStateSelected = 3;
    public static final int EquipmentCellStateRequestExpand = 4;
    public static final int EquipmentCellStateRequestCollapse = 5;

    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentExpandAnimator;
    private Animator mCurrentCollapseAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;

    private float startScale;
    private Rect startBound;

    //
    private static LayoutInflater mInflater = null;
    private ArrayList<HashMap> mEquipmentList;

    private Context mContext;
    private OnMyItemClickListener mListener;

    public EquipmentAdapter(Context context, OnMyItemClickListener listener, ArrayList<HashMap> values) {
        mContext = context;
        mListener = listener;

        mInflater = LayoutInflater.from(mContext);
        mEquipmentList = values;

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = mContext.getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    @Override
    public int getCount() {
        return mEquipmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEquipmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final HashMap equipmentMap = mEquipmentList.get(position);

        View vi = view;
        ViewHolder viewHolder;

        if (vi == null) {
            vi = mInflater.inflate(R.layout.layout_equip_item, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.imagePhoto = (ImageView) vi.findViewById(R.id.image_photo);
            viewHolder.textInfo = (TextView) vi.findViewById(R.id.text_info);
            viewHolder.textCost = (TextView) vi.findViewById(R.id.text_cost);
            viewHolder.textChopped = (TextView) vi.findViewById(R.id.text_chopped);
            viewHolder.imageExpand = (ImageView) vi.findViewById(R.id.image_expand);

            viewHolder.textAddToCart = (TextView) vi.findViewById(R.id.text_add_to_cart);
            viewHolder.textAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    HashMap equipmentMap = mEquipmentList.get(pos);
                    equipmentMap.put(Constant.kEquipmentStateKey, EquipmentCellStateSelected);

                    if (mContext instanceof OnOrderChangeListener)
                        ((OnOrderChangeListener) mContext).addToCart(pos);
                    notifyDataSetChanged();
                }
            });

            vi.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) vi.getTag();
        }

        viewHolder.textAddToCart.setTag(position);

        if (equipmentMap != null) {
            ImageLoader.getInstance().displayImage(EquipmentData.imagePathOfEquipment(equipmentMap),
                    viewHolder.imagePhoto, CommonUtils.mThumbOptions);
            ImageLoader.getInstance().displayImage(EquipmentData.imagePathOfEquipment(equipmentMap),
                    viewHolder.imageExpand, CommonUtils.mImageOptions);

            viewHolder.textInfo.setText(EquipmentData.infoOfEquipment(equipmentMap));

            viewHolder.textCost.setText(EquipmentData.costOfEquipment(equipmentMap));

            if (EquipmentData.getInstance().cartWithSortIndex(position) != null) {
                viewHolder.textChopped.setVisibility(View.VISIBLE);
                viewHolder.imageExpand.setVisibility(View.GONE);
                viewHolder.textAddToCart.setVisibility(View.GONE);
                EquipmentData.getInstance().setEquipmentState(position, EquipmentCellStateSelected);
            } else {
                viewHolder.textChopped.setVisibility(View.INVISIBLE);
            }

            int state = (int) equipmentMap.get(Constant.kEquipmentStateKey);

            switch (state) {
                case EquipmentCellStateNone:
                case EquipmentCellStateNormal:
                case EquipmentCellStateRequestCollapse:
                case EquipmentCellStateRequestExpand:
                    viewHolder.imagePhoto.setAlpha(1.0f);
                    viewHolder.imageExpand.setVisibility(View.GONE);
                    viewHolder.textAddToCart.setVisibility(View.GONE);
                    viewHolder.textInfo.setLeft(CommonUtils.dipToPixels(mContext, 80));
                    viewHolder.textInfo.setX(CommonUtils.dipToPixels(mContext, 80));
                    break;

                case EquipmentCellStateExpand:
                    viewHolder.imagePhoto.setAlpha(0.0f);
                    viewHolder.imageExpand.setVisibility(View.VISIBLE);
                    viewHolder.textAddToCart.setVisibility(View.VISIBLE);
                    viewHolder.textInfo.setX(0);
                    break;

                case EquipmentCellStateSelected:
                    viewHolder.imagePhoto.setAlpha(1.0f);
                    viewHolder.textChopped.setVisibility(View.VISIBLE);
                    viewHolder.imageExpand.setVisibility(View.GONE);
                    viewHolder.textAddToCart.setVisibility(View.GONE);
                    viewHolder.textInfo.setLeft(CommonUtils.dipToPixels(mContext, 80));
                    viewHolder.textInfo.setX(CommonUtils.dipToPixels(mContext, 80));
                    break;
            }
        }

        vi.setTag(R.string.list_item_pos, position);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.string.list_item_pos);
                mListener.onItemClick(v, position);
            }
        });

        return vi;
    }

    /**
     * "Zooms" in a thumbnail view by assigning the high resolution image to a hidden "zoomed-in"
     * image view and animating its bounds to fit the entire activity content area. More
     * specifically:
     * <p/>
     * <ol>
     * <li>Assign the high-res image to the hidden "zoomed-in" (expanded) image view.</li>
     * <li>Calculate the starting and ending bounds for the expanded view.</li>
     * <li>Animate each of four positioning/sizing properties (X, Y, SCALE_X, SCALE_Y)
     * simultaneously, from the starting bounds to the ending bounds.</li>
     * <li>Zoom back out by running the reverse animation on click.</li>
     * </ol>
     */
    public void zoomImageFromThumb(final ViewHolder viewHolder, final int index) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentExpandAnimator != null) {
            mCurrentExpandAnimator.cancel();
        }

        final ImageView thumbView = viewHolder.imagePhoto;

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = viewHolder.imageExpand;
        //expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image. This step
        // involves lots of math. Yay, math.
        //final Rect startBounds = new Rect();
        startBound = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail, and the
        // final bounds are the global visible rectangle of the container view. Also
        // set the container view's offset as the origin for the bounds, since that's
        // the origin for the positioning animation properties (X, Y).
        thumbView.getGlobalVisibleRect(startBound);

        ViewGroup parent = (ViewGroup) viewHolder.imagePhoto.getParent();
        parent.getGlobalVisibleRect(finalBounds, globalOffset);
        finalBounds.top += startBound.height() + CommonUtils.dipToPixels(mContext, 24);
        finalBounds.bottom += CommonUtils.dipToPixels(mContext, EXPAND_IMAGE_VIEW_HEIGHT);

        startBound.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBound.width() / startBound.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBound.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBound.width()) / 2;
            startBound.left -= deltaWidth;
            startBound.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBound.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBound.height()) / 2;
            startBound.top -= deltaHeight;
            startBound.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation begins,
        // it will position the zoomed-in view in the place of the thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        viewHolder.textAddToCart.setAlpha(0);
        viewHolder.textAddToCart.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
        // the zoomed-in view (the default is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and scale properties
        // (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBound.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBound.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f))
                .with(ObjectAnimator.ofFloat(viewHolder.textAddToCart, View.ALPHA, 0.0f, 1.0f))
                .with(ObjectAnimator.ofFloat(viewHolder.textInfo, View.X, CommonUtils.dipToPixels(mContext, 80), 0));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentExpandAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentExpandAnimator = null;
            }
        });
        set.start();
        EquipmentData.getInstance().setEquipmentState(index, EquipmentCellStateExpand);
        mCurrentExpandAnimator = set;
    }

    // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
    // and show the thumbnail instead of the expanded image.
    public void zoomThumbFromImage(final ViewHolder viewHolder, final int index) {
        if (mCurrentCollapseAnimator != null) {
            mCurrentCollapseAnimator.cancel();
        }

        final ImageView thumbView = viewHolder.imagePhoto;

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = viewHolder.imageExpand;

        // Animate the four positioning/sizing properties in parallel, back to their
        // original values.
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBound.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBound.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale))
                .with(ObjectAnimator.ofFloat(viewHolder.textAddToCart, View.ALPHA, 1.0f, 0.0f))
                .with(ObjectAnimator.ofFloat(viewHolder.textInfo, View.X, 0, CommonUtils.dipToPixels(mContext, 80)));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                thumbView.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                viewHolder.textAddToCart.setVisibility(View.GONE);
                mCurrentCollapseAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                thumbView.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                viewHolder.textAddToCart.setVisibility(View.GONE);
                mCurrentCollapseAnimator = null;
            }
        });
        set.start();
        EquipmentData.getInstance().setEquipmentState(index, EquipmentCellStateNormal);
        mCurrentCollapseAnimator = set;
    }

    public class ViewHolder {
        public ImageView imagePhoto;
        public TextView textInfo;
        public TextView textCost;
        public TextView textChopped;
        public ImageView imageExpand;
        public TextView textAddToCart;
    }

}
