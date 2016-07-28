/**
 * @author LuYongXing
 * @date 2015.01.23
 * @filename OnOrderChangeListener.java
 */

package com.camerarental.crc.listener;

public interface OnOrderChangeListener {

    public void addToCart(int index);

    public void removeFromCart(int index);

    public void clearCart(int index);

}
