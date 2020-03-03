package main;

import main.service.impls.AdminServiceImpl;

public class Test {
    public static void main(String[] args) {
        AdminServiceImpl service = new AdminServiceImpl();
        System.out.println(service.login("tom", "123456"));
    }


}
