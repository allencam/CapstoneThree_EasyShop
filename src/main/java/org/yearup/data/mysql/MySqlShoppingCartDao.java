package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{

    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();

        String sql = "SELECT shopping_cart.*, products.* " +
                     "FROM shopping_cart " +
                     "JOIN products " +
                     "ON shopping_cart.product_id = products.product_id " +
                     "WHERE shopping_cart.user_id = ?";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet row = statement.executeQuery();

            while(row.next())
            {
                int quantity = row.getInt("quantity");
                Product product = MySqlProductDao.mapRow(row);

                cart.add(new ShoppingCartItem(product,quantity));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return cart;
    }

    @Override
    public ShoppingCart addProductToCart(int userId, int productId, int quantity)
    {
        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                     "VALUES (?, ?, ?)" +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.setInt(4, quantity);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return getByUserId(userId);
    }

    @Override
    public ShoppingCart emptyCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart " +
                     "WHERE user_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return getByUserId(userId);
    }

    @Override
    public void updateQuantity(int userId, int productId, ShoppingCartItem item) {
        String sql = "UPDATE shopping_cart " +
                     "SET quantity = ? " +
                     "WHERE user_id = ? AND product_id = ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, item.getQuantity());
            statement.setInt(2, userId);
            statement.setInt(3, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}