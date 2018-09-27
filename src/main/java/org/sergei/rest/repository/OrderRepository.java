package org.sergei.rest.repository;

import org.sergei.rest.model.Customer;
import org.sergei.rest.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM orders WHERE order_number = :orderNumber", nativeQuery = true)
    List<Order> findByNumber(Long orderNumber);

    @Query(value = "SELECT * FROM orders WHERE customer_number = :customerNumber AND order_number = :orderNumber",
            nativeQuery = true)
    Optional<List<Order>> findByCustomerAndOrderNumbers(@Param("customerNumber") Long customerNumber,
                                                        @Param("orderNumber") Long orderNumber);

    @Query(value = "SELECT * FROM orders WHERE customer_number = :customerNumber", nativeQuery = true)
    Optional<List<Order>> findAllByCustomerNumber(@Param("customerNumber") Long customerNumber);

    @Query(value = "SELECT * FROM orders INNER JOIN order_details o on orders.order_number = o.order_number WHERE product_code = :productCode", nativeQuery = true)
    Optional<List<Order>> findAllByProductCode(@Param("productCode") String productCode);

    @Query(value = "DELETE FROM orders WHERE order_number = :orderNumber", nativeQuery = true)
    void deleteByOrderNumber(@Param("orderNumber") Long orderNumber);

    @Query(value = "DELETE FROM orders WHERE customer_number = :customerNumber AND order_number = :orderNumber", nativeQuery = true)
    void deleteByCustomerAndOrderNumbers(@Param("customerNumber") Long customerNumber,
                                         @Param("orderNumber") Long orderNumber);
}