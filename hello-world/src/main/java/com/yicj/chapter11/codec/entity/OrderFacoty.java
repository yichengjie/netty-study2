package com.yicj.chapter11.codec.entity;

import com.yicj.chapter11.entity.Address;
import com.yicj.chapter11.entity.Customer;
import com.yicj.chapter11.entity.Order;
import com.yicj.chapter11.entity.Shipping;

public class OrderFacoty {
    public static Order create(long orderId) {
        Order order = new Order() ;
        order.setOrderNumber(orderId);
        order.setTotal(9999.999f);
        Address address = new Address() ;
        address.setCity("北京市");
        address.setCountry("中国");
        address.setPostCode("123321");
        address.setState("北京");
        address.setStreet1("回龙观东大街");
        address.setStreet2("南锣鼓巷");
        order.setBillTo(address);
        Customer customer = new Customer() ;
        customer.setCustomerNumber(orderId);
        customer.setFirstName("李");
        customer.setLastName("刚");
        order.setCustomer(customer);
        order.setShipping(Shipping.INTERNATION_MAIL);
        order.setShipTo(address);
        return order ;
    }
}
