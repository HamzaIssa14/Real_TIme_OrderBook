package order.validators;

import order.Order;

public interface OrderValidator {
    boolean validate(Order order);
}
