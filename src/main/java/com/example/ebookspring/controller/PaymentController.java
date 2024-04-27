package com.example.ebookspring.controller;


import com.example.ebookspring.exception.OrderException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Order;
import com.example.ebookspring.model.User;
import com.example.ebookspring.repository.OrderRepository;
import com.example.ebookspring.repository.UserRepository;
import com.example.ebookspring.response.ApiResponse;
import com.example.ebookspring.response.PaymentLinkResponse;
import com.example.ebookspring.service.OrderService;
import com.example.ebookspring.service.UserService;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    private OrderService orderService;
    private UserService userService;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    public PaymentController(OrderService orderService, UserService userService, OrderRepository orderRepository,
                             UserRepository userRepository) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/payments/{orderId}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId,
                                                                 @RequestHeader("Authorization") String jwt) throws OrderException, RazorpayException {

        Order order = orderService.findOrderById(orderId);

        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

            JSONObject paymentLinkRequest = new JSONObject();

            paymentLinkRequest.put("amount", (order.getTotalPrice() * 100) - order.getTotalDiscountedPrice());
            paymentLinkRequest.put("currency", "KZT");

            JSONObject customer = new JSONObject();
            customer.put("name", order.getUser().getFirstName());
            customer.put("email", order.getUser().getEmail());
            paymentLinkRequest.put("customer", customer);

            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            paymentLinkRequest.put("callback_url", "http://localhost:3000/payment/" + orderId);
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            PaymentLinkResponse res = new PaymentLinkResponse();
            res.setPayment_link_id(paymentLinkId);
            res.setPayment_link_url(paymentLinkUrl);

            return new ResponseEntity<PaymentLinkResponse>(res, HttpStatus.CREATED);

        } catch (Exception e) {
            throw new RazorpayException(e.getMessage());
        }
    }


    @GetMapping("/payments")
    public ResponseEntity<ApiResponse> redirect(@RequestParam(name = "payment_id") String paymentId,
                                                @RequestParam(name = "order_id") Long orderId) throws OrderException, RazorpayException {
        Order order = orderService.findOrderById(orderId);
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
        try {

            Payment payment = razorpay.payments.fetch(paymentId);

            if(payment.get("status").equals("captures")) {
                order.getPaymentDetails().setPaymentId(paymentId);
                order.getPaymentDetails().setStatus("COMPLETED");
                order.setOrderStatus("PLACED");
                orderRepository.save(order);
            }

            ApiResponse res = new ApiResponse();
            res.setMessage("your order get placed");
            res.setStatus(true);
            return new ResponseEntity<ApiResponse>(res, HttpStatus.ACCEPTED);

        } catch (Exception e) {
            throw new RazorpayException(e.getMessage());
        }
    }



    //for subscription
    @PostMapping("/plan/subscribe/{planType}")
    public ResponseEntity<PaymentLinkResponse> createSubscription(@PathVariable String planType,
                                                                  @RequestHeader("Authorization") String jwt)
            throws RazorpayException, UserException {

        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        try {

            User user=userService.findUserProfileByJwt(jwt);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("currency","KZT");
            paymentLinkRequest.put("description","Twitter Verification");

            JSONObject customer = new JSONObject();
            customer.put("name",user.getFirstName());
            customer.put("email",user.getEmail());
            paymentLinkRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("sms",true);
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);
            paymentLinkRequest.put("reminder_enable",true);

            JSONObject notes = new JSONObject();

            notes.put("user_id", user.getId().toString());
            paymentLinkRequest.put("notes",notes);

            paymentLinkRequest.put("callback_url","http://localhost:3000/communication/verified");
            paymentLinkRequest.put("callback_method","get");

            if(planType.equals("monthly")) {
                paymentLinkRequest.put("amount",1000*100);
                notes.put("plan","monthly");
            }
            else {
                paymentLinkRequest.put("amount",2500*100);
                notes.put("plan","monthly");
            }

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            System.out.println("plan : yearly"+payment);

            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            PaymentLinkResponse res=new PaymentLinkResponse();
            res.setPayment_link_url(paymentLinkUrl);

            return new ResponseEntity<>(res,HttpStatus.CREATED);

        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }


    @GetMapping("/plan/{paymentLinkId}")
    public ResponseEntity<String> fetchPaymetn(@PathVariable String paymentLinkId) throws RazorpayException {

        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

        try {

            PaymentLink payment = razorpay.paymentLink.fetch(paymentLinkId);

            String customerJsonString = payment.get("customer").toString();

            JSONObject customerObject = new JSONObject(customerJsonString);

            String email = customerObject.getString("email");

            User user =userRepository.findByEmail(email);

            String notesJsonString=payment.get("notes").toString();

            JSONObject notesObject=new JSONObject(notesJsonString);

            String plan=notesObject.getString("plan");

            if(payment.get("status").equals("paid")) {
                user.getVerification().setStartedAt(LocalDateTime.now());
                user.getVerification().setPlanType(plan);

                if (plan.equals("yearly")) {
                    LocalDateTime endsAt = user.getVerification().getStartedAt().plusYears(1);
                    user.getVerification().setEndsAt(endsAt);
                }
                else if (plan.equals("monthly")) {
                    LocalDateTime endsAt = user.getVerification().getStartedAt().plusMonths(1);
                    user.getVerification().setEndsAt(endsAt);
                }
                user.setReq_user(true);

                userRepository.save(user);

            }

            return new ResponseEntity<>(email,HttpStatus.CREATED);

        } catch (RazorpayException e) {
            throw new RazorpayException(e.getMessage());
        }
    }
}