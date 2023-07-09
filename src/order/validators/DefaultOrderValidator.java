package order.validators;

import order.Order;
import order.enums.OrderType;
import order.enums.Ticker;

import java.util.EnumSet;

public class DefaultOrderValidator implements OrderValidator {
    /**
     * This will be part of a chain of validation.
     * This is the basic/first step in validation, but later there will be a chain.
     * At that point this will require refactoring, to return a order.Order object to continue the chain.
     * @param order
     * @return
     */
    @Override
    public boolean validate(Order order) {
        if(order.getPrice() < 0){
            return false;
        }
        if(order.getQuantity() < 0){
            return false;
        }
        if(!EnumSet.allOf(Ticker.class).contains(Ticker.valueOf(order.getTicker()))){
            return false;
        }
        if(order.getTimestamp() < 0){
            return false;
        }
        if(!(order.getType().equals(OrderType.BUY) || order.getType().equals(OrderType.SELL))){
            return false;
        }
        return true;
    }

}
