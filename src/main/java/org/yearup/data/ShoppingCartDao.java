package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    ShoppingCart addProductToCart(int userId, int productId, int quantity);
    ShoppingCart emptyCart(int userId);
    void updateQuantity(int userId, int productId, ShoppingCartItem item);
}
