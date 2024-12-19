package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addProductToCart(int userId, int productId, int quantity);
    void emptyCart(int userId);
}
