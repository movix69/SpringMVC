package com.spring.mvc.service;

//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
//@Component
public class CalculadoraService {
    public int sumar(int s1,int s2){
        return s1+s2;
    }
}
