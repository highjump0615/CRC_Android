/**
 * @author LuYongXing
 * @date 2015.01.19
 * @filename EquipmentData.java
 */

package com.camerarental.crc.data;

import android.content.Context;
import android.text.TextUtils;

import com.camerarental.crc.adapter.EquipmentAdapter;
import com.camerarental.crc.listener.OnCartChangeListener;
import com.camerarental.crc.utils.CommonUtils;
import com.camerarental.crc.utils.Config;
import com.camerarental.crc.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class EquipmentData {

    public ArrayList<HashMap> mEquipmentArray = new ArrayList<>();
    public ArrayList<HashMap> mFilteredArray = new ArrayList<>();
    public ArrayList<HashMap> mCartArray = new ArrayList<>();

    private OnCartChangeListener listener;

    private static EquipmentData sMe = null;

    public static EquipmentData getInstance() {
        if (sMe == null) {
            sMe = new EquipmentData();
        }

        return sMe;
    }

    public EquipmentData() {
        listener = null;
        reset();
    }

    public EquipmentData(OnCartChangeListener listener) {
        this.listener = listener;
        reset();
    }

    public void setListener(OnCartChangeListener listener) {
        this.listener = listener;
    }

    public void freeData() {
        reset();
        mEquipmentArray = null;
        mFilteredArray = null;
        mCartArray = null;

        sMe = null;
    }

    public void initWithData(JSONArray data) {
        reset();

        String[] keys = {
                Constant.kEquipmentIDKey,
                Constant.kEquipmentInformationKey,
                Constant.kEquipmentCostKey,
                Constant.kEquipmentImageURLKey,
                Constant.kEquipmentKindKey,
                Constant.kEquipmentCountKey,
                Constant.kEquipmentStateKey
        };

        int size = data.length();
        for (int index = 0; index < size; index++) {
            try {
                JSONArray equip = data.getJSONArray(index);

                HashMap params = new HashMap();

                for (String key : keys) {
                    params.put(key, "");
                }

                params.put(Constant.kEquipmentIDKey, String.format("%04d", index));

                for (int i = 0; i < equip.length(); i++) {
                    params.put(keys[i + 1], equip.getString(i));
                }

                mEquipmentArray.add(params);
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }

        for (HashMap map : mEquipmentArray) {
            map.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
            mFilteredArray.add(map);
        }

        loadCart();
    }

    public void loadCart() {
        if (mCartArray == null)
            mCartArray = new ArrayList<>();
        else
            mCartArray.clear();

        Set<String> cartArray = CommonUtils.loadCartArray((Context) listener);

        if (cartArray != null) {
            for (String cartStr : cartArray) {
                try {
                    JSONObject cartObj = new JSONObject(cartStr);
                    HashMap cartMap = new HashMap();

                    cartMap.put(Constant.kEquipmentIDKey, cartObj.get(Constant.kEquipmentIDKey));
                    cartMap.put(Constant.kEquipmentInformationKey, cartObj.get(Constant.kEquipmentInformationKey));
                    cartMap.put(Constant.kEquipmentCostKey, cartObj.get(Constant.kEquipmentCostKey));
                    cartMap.put(Constant.kEquipmentImageURLKey, cartObj.get(Constant.kEquipmentImageURLKey));
                    cartMap.put(Constant.kEquipmentKindKey, cartObj.get(Constant.kEquipmentKindKey));
                    cartMap.put(Constant.kEquipmentCountKey, cartObj.get(Constant.kEquipmentCountKey));

                    mCartArray.add(cartMap);
                } catch (JSONException e) {
                    if (Config.DEBUG) e.printStackTrace();
                }
            }
        }

        updateCarts();
    }

    public void updateCarts() {
        int totalCount = 0;
        for (HashMap cartMap : mCartArray) {
            totalCount += Integer.parseInt(cartMap.get(Constant.kEquipmentCountKey).toString());
        }

        Set<String> cartArray = new LinkedHashSet<>();
        for (HashMap cartMap : mCartArray) {
            JSONObject cartObj = new JSONObject();
            try {
                cartObj.put(Constant.kEquipmentIDKey, cartMap.get(Constant.kEquipmentIDKey));
                cartObj.put(Constant.kEquipmentInformationKey, cartMap.get(Constant.kEquipmentInformationKey));
                cartObj.put(Constant.kEquipmentCostKey, cartMap.get(Constant.kEquipmentCostKey));
                cartObj.put(Constant.kEquipmentImageURLKey, cartMap.get(Constant.kEquipmentImageURLKey));
                cartObj.put(Constant.kEquipmentKindKey, cartMap.get(Constant.kEquipmentKindKey));
                cartObj.put(Constant.kEquipmentCountKey, cartMap.get(Constant.kEquipmentCountKey));
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }

            cartArray.add(cartObj.toString());
        }

        CommonUtils.saveCartArray((Context) listener, cartArray);
        listener.onCartChanged(totalCount);
    }

    /**
     * Reset all array variables
     */
    private void reset() {
        if (mEquipmentArray != null) {
            mEquipmentArray.clear();
            mFilteredArray.clear();
            mCartArray.clear();
        } else {
            mEquipmentArray = new ArrayList<>();
            mFilteredArray = new ArrayList<>();
            mCartArray = new ArrayList<>();
        }
    }

    private boolean isEqualWithFirst(HashMap first, HashMap second) {
        String[] keys = {Constant.kEquipmentIDKey, Constant.kEquipmentInformationKey, Constant.kEquipmentCostKey, Constant.kEquipmentImageURLKey, Constant.kEquipmentKindKey};

        for (String key : keys) {
            if (!first.get(key).equals(second.get(key)))
                return false;
        }
        return true;
    }


    public static String infoOfEquipment(HashMap params) {
        if (params != null)
            return (String) params.get(Constant.kEquipmentInformationKey);

        return null;
    }

    public static String costOfEquipment(HashMap params) {
        if (params != null)
            return (String) params.get(Constant.kEquipmentCostKey);

        return null;
    }

    public static int equipmentCartCount(HashMap params) {
        if (params != null) {
            String count = (String) params.get(Constant.kEquipmentCountKey);
            if (!TextUtils.isEmpty(count) && TextUtils.isDigitsOnly(count))
                return Integer.parseInt(count);
        }

        return 0;
    }

    public static String imagePathOfEquipment(HashMap params) {
        if (params != null)
            return (String) params.get(Constant.kEquipmentImageURLKey);

        return null;
    }

    public int count() {
        return mFilteredArray.size();
    }

    public int cartCount() {
        return mCartArray.size();
    }

    public HashMap equipmentDataWithIndex(int index) {
        if (mFilteredArray.size() == 0 || index > mFilteredArray.size() - 1)
            return null;

        return mFilteredArray.get(index);
    }

    public void setEquipmentState(int index, int state) {
        if (mFilteredArray.size() == 0 || index > mFilteredArray.size() - 1)
            return;

        HashMap map = mFilteredArray.get(index);
        map.put(Constant.kEquipmentStateKey, state);
        //mFilteredArray.set(index, map);
    }

    public HashMap cartDataWithIndex(int index) {
        if (mCartArray.size() == 0 || index > mCartArray.size() - 1)
            return null;

        return mCartArray.get(index);
    }

    public HashMap cartWithSortIndex(int index) {
        if (mFilteredArray.size() == 0 || index > mFilteredArray.size() - 1)
            return null;

        HashMap map = mFilteredArray.get(index);
        return cartWithDic(map);
    }

    public HashMap cartWithDic(HashMap map) {
        for (HashMap cartMap : mCartArray) {
            if (isEqualWithFirst(cartMap, map)) {
                return cartMap;
            }
        }

        return null;
    }

    public boolean addToCartWithIndex(int index) {
        HashMap cartMap = mFilteredArray.get(index);

        if (!mCartArray.contains(cartMap)) {
            cartMap.put(Constant.kEquipmentCountKey, "1");
            mCartArray.add(cartMap);

            updateCarts();
            return true;
        }

        return false;
    }

    public boolean changeCartWithIndex(int index, int count) {
        if (index > mCartArray.size() - 1 || mCartArray.size() == 0)
            return false;

        HashMap cartMap = mCartArray.get(index);

        cartMap.put(Constant.kEquipmentCountKey, "" + count);

        if (count == 0) {
            cartMap.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
            mCartArray.remove(cartMap);
        }

        updateCarts();

        return true;
    }

    public boolean removeFromCart(HashMap cartMap) {
        if (cartMap == null) return false;

        cartMap = cartWithDic(cartMap);

        if (cartMap == null) return false;

        cartMap.put(Constant.kEquipmentCountKey, "0");
        cartMap.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
        mCartArray.remove(cartMap);

        updateCarts();

        return true;
    }

    public void clearCart() {
        for (HashMap cartMap : mCartArray) {
            cartMap.put(Constant.kEquipmentCountKey, "0");
            cartMap.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
        }

        mCartArray.clear();

        CommonUtils.saveCartArray((Context) listener, null);
        listener.onCartChanged(0);
    }

    public void resetSort(boolean search) {
        if (search) {
            mFilteredArray = new ArrayList<>();
        } else {
            mFilteredArray.clear();

            for (HashMap map : mEquipmentArray) {
                map.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
                mFilteredArray.add(map);
            }
        }
    }

    public void searchWithFilter(ArrayList<String> filters) {
        if (mFilteredArray == null) {
            mFilteredArray = new ArrayList<>();
        } else {
            mFilteredArray.clear();
        }

        if (filters.size() == 0) {
            mFilteredArray.clear();

            for (HashMap map : mEquipmentArray) {
                map.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
                mFilteredArray.add(map);
            }
        } else {
            boolean matchKind;
            String kindString;

            for (HashMap map : mEquipmentArray) {
                map.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);

                matchKind = true;
                kindString = map.get(Constant.kEquipmentKindKey).toString().toLowerCase();

                for (String strKey : filters) {
                    if (!kindString.contains(strKey)) {
                        matchKind = false;
                        break;
                    }
                }

                if (matchKind) {
                    map.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
                    mFilteredArray.add(map);
                }
            }
        }
    }

    public boolean searchWithString(String searchString) {
        if (mFilteredArray == null) {
            mFilteredArray = new ArrayList<>();
        } else {
            mFilteredArray.clear();
        }

        if (!TextUtils.isEmpty(searchString)) {
            ArrayList<String> searchArray = new ArrayList<>();

            String[] array = searchString.toLowerCase().split(" ");

            for (String strKey : array) {
                String str = strKey.trim();

                if (!TextUtils.isEmpty(str))
                    searchArray.add(str);
            }

            if (searchArray.size() == 0)
                return false;

            boolean matchInfo;
            boolean matchKind;
            String infoString;
            String kindString;

            for (HashMap equipmentMap : mEquipmentArray) {
                matchInfo = true;
                infoString = equipmentMap.get(Constant.kEquipmentInformationKey).toString().toLowerCase();
                for (String strKey : searchArray) {
                    if (!infoString.contains(strKey)) {
                        matchInfo = false;
                        break;
                    }
                }

                matchKind = true;
                kindString = equipmentMap.get(Constant.kEquipmentKindKey).toString().toLowerCase();
                for (String strKey : searchArray) {
                    if (!kindString.contains(strKey)) {
                        matchKind = false;
                        break;
                    }
                }

                if (matchInfo || matchKind) {
                    equipmentMap.put(Constant.kEquipmentStateKey, EquipmentAdapter.EquipmentCellStateNone);
                    mFilteredArray.add(equipmentMap);
                }
            }
        }

        return true;
    }

}
