package jmskata.receive;


public class CustomerServiceImpl /* implements CustomerService */{
    public void createCustomer(String first, String last) {
        System.out.println("create customer:");
        System.out.println("first: " + first);
        System.out.println("last: " + last);
    }

    public void deleteCustomer(String id) {
        System.out.println("delete customer:");
        System.out.println("id: " + id);
    }
}
