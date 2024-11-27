package com.petalaura.admin.controller;


import com.petalaura.library.Repository.OrderRepository;
import com.petalaura.library.Service.OrderService;
import com.petalaura.library.dto.OrderDto;
import com.petalaura.library.model.Order;
import com.petalaura.library.model.OrderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrderDetailsControll {
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    public OrderDetailsControll(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orderDetails/{pageNo}")
    public String showOrderDetails(@PathVariable("pageNo") int pageNo, Model model) {
        Page<Order> orders = orderService.findAllOrdersPagable(pageNo, 10);
        List<OrderDetails> orderDetails=orderService.findAllOrders();
        OrderDto orderDto=new OrderDto();
        model.addAttribute("report",orderDto);
        model.addAttribute("orderDetails",orderDetails);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", orders.getTotalPages());;
        return "orderDetails";
    }

    @GetMapping("/orderDetailsInfo")
    public String showOrderDetaliInfo(@RequestParam("orderId") Long orderId, Model model) {
        List<OrderDetails> orderDetails = orderService.findByOrderId(orderId);
        Order order = orderService.findById(orderId);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("order", order);
        model.addAttribute("order1", order);
        return "orderDetail-info";
    }

    @PostMapping("/updateStatus")
    public String showUpdateOrderStaus(@ModelAttribute("order1") Order order) {
        orderService.updateOrderStatus(order);
        return "redirect:/orderDetails/0";
    }




    @GetMapping("/orderStatus/{pageNo}")
    public String showOrderStatus(@PathVariable("pageNo")int pageNo,
                                  @ModelAttribute("report")OrderDto orderDto1,Model model){
        String orderStatus=orderDto1.getOrderStatus();
        Page<Order> orders = orderService.findOrderByOrderStatusPagable(pageNo,orderStatus);
        OrderDto orderDto=new OrderDto();
        model.addAttribute("report",orderDto);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", orders.getTotalPages());
        return "orderDetails";
    }

    @GetMapping("/removeOrder")
    public String removeOrder(@RequestParam("orderId") Long orderId, Model model) {
        try {
            orderService.deleteOrderDetailsById(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/orderDetails/0";
    }





}
