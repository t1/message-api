package jmskata.receive;


public class CustomerServiceImpl /* implements CustomerService */{
    public void createCustomer(String first, String last) {
        System.out.println("customer service (0)");
        System.out.println("create customer:");
        System.out.println("first: " + first);
        System.out.println("last: " + last);
        System.out.println();
    }

    public void deleteCustomer(String id) {
        System.out.println("customer service (0)");
        System.out.println("delete customer:");
        System.out.println("id: " + id);
        System.out.println();
    }
}
