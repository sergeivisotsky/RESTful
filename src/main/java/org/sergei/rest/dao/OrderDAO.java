package org.sergei.rest.dao;

import org.sergei.rest.model.Order;
import org.sergei.rest.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Repository
public class OrderDAO {
    private static final String SQL_SAVE_ORDER = "INSERT INTO orders(customer_id, product_id, trans_id, total_price) VALUES(?, ?, ?, ?)";
    private static final String SQL_FIND_ALL = "SELECT orders.order_id, orders.customer_id, orders.product_id, orders.trans_id, orders.total_price, " +
            "products.category, products.product_name, products.product_weight, products.product_price " +
            "FROM rest_services.orders LEFT JOIN rest_services.products ON orders.product_id = products.product_id";
    private static final String SQL_UPDATE_ORDER = "UPDATE orders SET trans_id = ?, product_id = ?, total_price = ? " +
            "WHERE customer_id = ? AND order_id = ?";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM orders WHERE order_id = ?";
    private static final String SQL_FIND_BY_CUSTOMER_ID_AND_PRODUCT = "SELECT * FROM orders WHERE customer_id = ? AND product = ?";
    private static final String SQL_FIND_BY_CUSTOMER_ID_AND_ORDER_ID = "SELECT * FROM orders WHERE customer_id = ? AND order_id = ?";
    private static final String SQL_FIND_BY_PRODUCT = "SELECT * FROM orders WHERE product = ?";
    private static final String SQL_EXISTS_BY_ORDER_ID = "SELECT count(*) FROM orders WHERE order_id = ?";
    private static final String SQL_EXISTS_BY_PRODUCT = "SELECT count(*) FROM orders WHERE product = ?";
    private static final String SQL_EXISTS_BY_CUSTOMER_ID = "SELECT count(*) FROM orders WHERE customer_id = ?";
    private static final String SQL_DELETE = "DELETE FROM orders WHERE order_id = ?";
    private static final String SQL_FIND_ALL_BY_CUSTOMER_ID = "SELECT * FROM orders WHERE customer_id = ?";

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Long customerId, Order order) {
        try {
            jdbcTemplate.update(SQL_SAVE_ORDER, customerId, order.getTransId(), order.getTotalPrice());
            LOGGER.info("Order entity saved");
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public List<Order> findAll() {
        try {
            return jdbcTemplate.query(SQL_FIND_ALL, new OrderRowMapper());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Order findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new OrderRowMapper(), id);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public Order findByCustomerIdAndOrderId(Long customerId, Long orderId) {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_CUSTOMER_ID_AND_ORDER_ID,
                    new OrderRowMapper(), customerId, orderId);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Order> findAllByCustomerIdAndProduct(Long customerId, String product) {
        try {
            return jdbcTemplate.query(SQL_FIND_BY_CUSTOMER_ID_AND_PRODUCT, new OrderRowMapper(), customerId, product);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public List<Order> findAllByProduct(String product) {
        try {
            return jdbcTemplate.query(SQL_FIND_BY_PRODUCT, new OrderRowMapper(), product);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public boolean existsById(Long orderId) {
        int count = jdbcTemplate.queryForObject(SQL_EXISTS_BY_ORDER_ID, new Object[]{orderId}, Integer.class);
        return count > 0;
    }

    public boolean existsByProduct(String product) {
        int count = jdbcTemplate.queryForObject(SQL_EXISTS_BY_PRODUCT, new Object[]{product}, Integer.class);
        return count > 0;
    }

    public boolean existsByCustomerId(Long customerId) {
        int count = jdbcTemplate.queryForObject(SQL_EXISTS_BY_CUSTOMER_ID, new Object[]{customerId}, Integer.class);
        return count > 0;
    }

    public void updateRecord(Long customerId, Long orderId, Order order) {
        try {
            jdbcTemplate.update(SQL_UPDATE_ORDER, order.getTransId(), order.getTotalPrice(), customerId, orderId);
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void delete(Order order) {
        try {
            jdbcTemplate.update(SQL_DELETE, order.getOrderId());
        } catch (DataAccessException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public List<Order> findAllByCustomerId(Long id) {
        return jdbcTemplate.query(SQL_FIND_ALL_BY_CUSTOMER_ID, new OrderRowMapper(), id);
    }

    private static final class OrderRowMapper implements RowMapper<Order> {

        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();

            order.setOrderId(rs.getLong("order_id"));
            order.setCustomerId(rs.getLong("customer_id"));
            order.setTransId(rs.getLong("trans_id"));
            order.setTotalPrice(rs.getFloat("total_price"));

            List<Product> products = new LinkedList<>();

            Product product;
            while (rs.next()) {
                product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setCategory(rs.getString("category"));
                product.setProductName(rs.getString("product_name"));
                product.setProductWeight(rs.getFloat("product_weight"));
                product.setProductPrice(rs.getFloat("product_price"));
                products.add(product);
            }

            order.setProducts(products);
            return order;
        }
    }
}
