package com.example.ebookspring.controller;

import com.example.ebookspring.exception.OrderException;
import com.example.ebookspring.exception.UserException;
import com.example.ebookspring.model.Order;
import com.example.ebookspring.repository.OrderRepository;
import com.example.ebookspring.repository.UserRepository;
import com.example.ebookspring.response.ApiResponse;
import com.example.ebookspring.response.PaymentLinkResponse;
import com.example.ebookspring.service.OrderService;
import com.example.ebookspring.service.UserService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

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
                                                                 @RequestHeader("Authorization") String jwt)
            throws PayPalRESTException, UserException, OrderException {

        Order order = orderService.findOrderById(orderId);
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);

            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.valueOf(order.getTotalPrice()));

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription("Order #" + orderId);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("http://localhost:3000/payment-cancelled?order_id=" + orderId);
            redirectUrls.setReturnUrl("http://localhost:3000/payment-success?order_id=" + orderId);

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);

            String paymentLinkId = createdPayment.getId();
            String paymentLinkUrl = createdPayment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst()
                    .map(Links::getHref)
                    .orElseThrow(() -> new RuntimeException("Approval URL not found"));

            PaymentLinkResponse res = new PaymentLinkResponse(paymentLinkUrl, paymentLinkId);

            order.getPaymentDetails().setPaypalPaymentLinkId(paymentLinkId);
            orderRepository.save(order);

            System.out.println("Payment link ID: " + paymentLinkId);
            System.out.println("Payment link URL: " + paymentLinkUrl);

            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

        } catch (Exception e) {
            throw new PayPalRESTException(e.getMessage());
        }
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse> redirect(@RequestParam(name = "paymentId") String paymentId, @RequestParam("order_id") Long orderId)
            throws PayPalRESTException, OrderException {

        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
        Order order = orderService.findOrderById(orderId);

        try {
            Payment payment = Payment.get(apiContext, paymentId);
            System.out.println("payment details --- " + payment + payment.getState());

            if ("approved".equals(payment.getState())) {
                System.out.println("payment details --- " + payment + payment.getState());

                order.getPaymentDetails().setPaymentId(paymentId);
                order.getPaymentDetails().setStatus("COMPLETED");
                order.setOrderStatus("PLACED");
                System.out.println(order.getPaymentDetails().getStatus() + " payment status ");
                orderRepository.save(order);
            }
            ApiResponse res = new ApiResponse("Your order has been placed", true);
            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e) {
            throw new PayPalRESTException(e.getMessage());
        }
    }
}



//package com.example.ebookspring.controller;
//
//
//import com.example.ebookspring.exception.OrderException;
//import com.example.ebookspring.exception.UserException;
//import com.example.ebookspring.model.Order;
//import com.example.ebookspring.model.PaymentDetails;
//import com.example.ebookspring.model.User;
//import com.example.ebookspring.repository.OrderRepository;
//import com.example.ebookspring.repository.UserRepository;
//import com.example.ebookspring.response.ApiResponse;
//import com.example.ebookspring.response.PaymentLinkResponse;
//import com.example.ebookspring.service.OrderService;
//import com.example.ebookspring.service.UserService;
//import com.razorpay.Payment;
//import com.razorpay.PaymentLink;
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.view.RedirectView;
//
//import java.time.LocalDateTime;
//
//@RestController
//@RequestMapping("/api")
//public class PaymentController {
//
//    @Value("${razorpay.api.key}")
//    private String apiKey;
//
//    @Value("${razorpay.api.secret}")
//    private String apiSecret;
//
//    private OrderService orderService;
//    private UserService userService;
//    private OrderRepository orderRepository;
//    private UserRepository userRepository;
//
//    public PaymentController(OrderService orderService, UserService userService, OrderRepository orderRepository,
//                             UserRepository userRepository) {
//        this.orderService = orderService;
//        this.userService = userService;
//        this.orderRepository = orderRepository;
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/payments/{orderId}")
//    public ResponseEntity<PaymentLinkResponse>createPaymentLink(@PathVariable Long orderId,
//                                                                @RequestHeader("Authorization")String jwt)
//            throws RazorpayException, UserException, OrderException{
//
//        Order order=orderService.findOrderById(orderId);
//        try {
//            // Instantiate a Razorpay client with your key ID and secret
//            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
//
//            // Create a JSON object with the payment link request parameters
//            JSONObject paymentLinkRequest = new JSONObject();
//            paymentLinkRequest.put("amount",order.getTotalPrice()* 100);
//            paymentLinkRequest.put("currency","KZT");
////		      paymentLinkRequest.put("expire_by",1691097057);
////		      paymentLinkRequest.put("reference_id",order.getId().toString());
//
//
//            // Create a JSON object with the customer details
//            JSONObject customer = new JSONObject();
//            customer.put("name",order.getUser().getFirstName()+" "+order.getUser().getLastName());
//            customer.put("contact",order.getUser().getMobile());
//            customer.put("email",order.getUser().getEmail());
//            paymentLinkRequest.put("customer",customer);
//
//            // Create a JSON object with the notification settings
//            JSONObject notify = new JSONObject();
//            notify.put("sms",true);
//            notify.put("email",true);
//            paymentLinkRequest.put("notify",notify);
//
//            // Set the reminder settings
//            paymentLinkRequest.put("reminder_enable",true);
//
//            // Set the callback URL and method
//            paymentLinkRequest.put("callback_url","http://localhost:3000/payment-success?order_id="+orderId);
//            paymentLinkRequest.put("callback_method","get");
//
//            // Create the payment link using the paymentLink.create() method
//            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);
//
//            String paymentLinkId = payment.get("id");
//            String paymentLinkUrl = payment.get("short_url");
//
//            PaymentLinkResponse res=new PaymentLinkResponse(paymentLinkUrl,paymentLinkId);
//
//            PaymentLink fetchedPayment = razorpay.paymentLink.fetch(paymentLinkId);
//
//            order.setOrderId(fetchedPayment.get("order_id"));
//            orderRepository.save(order);
//
//            // Print the payment link ID and URL
//            System.out.println("Payment link ID: " + paymentLinkId);
//            System.out.println("Payment link URL: " + paymentLinkUrl);
//            System.out.println("Order Id : "+fetchedPayment.get("order_id")+fetchedPayment);
//
//            return new ResponseEntity<PaymentLinkResponse>(res,HttpStatus.ACCEPTED);
//
//        } catch (Exception e) {
//            throw new RazorpayException(e.getMessage());
//        }
//
//
////		order_id
//    }
//
//    @GetMapping("/payments")
//    public ResponseEntity<ApiResponse> redirect(@RequestParam(name="payment_id") String paymentId,@RequestParam("order_id")Long orderId) throws RazorpayException, OrderException {
//        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
//        Order order =orderService.findOrderById(orderId);
//
//        try {
//
//
//            Payment payment = razorpay.payments.fetch(paymentId);
//            System.out.println("payment details --- "+payment+payment.get("status"));
//
//            if(payment.get("status").equals("captured")) {
//                System.out.println("payment details --- "+payment+payment.get("status"));
//
//                order.getPaymentDetails().setPaymentId(paymentId);
//                order.getPaymentDetails().setStatus("COMPLETED");
//                order.setOrderStatus("PLACED");
////			order.setOrderItems(order.getOrderItems());
//                System.out.println(order.getPaymentDetails().getStatus()+"payment status ");
//                orderRepository.save(order);
//            }
//            ApiResponse res=new ApiResponse("your order get placed", true);
//            return new ResponseEntity<ApiResponse>(res,HttpStatus.OK);
//
//        } catch (Exception e) {
//            throw new RazorpayException(e.getMessage());
//        }
//
//    }
//
//
//
//
//
//
//
//    //for subscription
//    @PostMapping("/plan/subscribe/{planType}")
//    public ResponseEntity<PaymentLinkResponse> createSubscription(@PathVariable String planType,
//                                                                  @RequestHeader("Authorization") String jwt)
//            throws RazorpayException, UserException {
//
//        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
//
//        try {
//
//            User user=userService.findUserProfileByJwt(jwt);
//
//            JSONObject paymentLinkRequest = new JSONObject();
//            paymentLinkRequest.put("currency","KZT");
//            paymentLinkRequest.put("description","Twitter Verification");
//
//            JSONObject customer = new JSONObject();
//            customer.put("name",user.getFirstName());
//            customer.put("email",user.getEmail());
//            paymentLinkRequest.put("customer",customer);
//
//            JSONObject notify = new JSONObject();
//            notify.put("sms",true);
//            notify.put("email",true);
//            paymentLinkRequest.put("notify",notify);
//            paymentLinkRequest.put("reminder_enable",true);
//
//            JSONObject notes = new JSONObject();
//
//            notes.put("user_id", user.getId().toString());
//            paymentLinkRequest.put("notes",notes);
//
//            paymentLinkRequest.put("callback_url","http://localhost:3000/communication/verified");
//            paymentLinkRequest.put("callback_method","get");
//
//            if(planType.equals("monthly")) {
//                paymentLinkRequest.put("amount",1000*100);
//                notes.put("plan","monthly");
//            }
//            else {
//                paymentLinkRequest.put("amount",2500*100);
//                notes.put("plan","monthly");
//            }
//
//            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);
//
//            System.out.println("plan : yearly"+payment);
//
//            String paymentLinkId = payment.get("id");
//            String paymentLinkUrl = payment.get("short_url");
//
//            PaymentLinkResponse res=new PaymentLinkResponse();
//            res.setPayment_link_url(paymentLinkUrl);
//
//            return new ResponseEntity<>(res,HttpStatus.CREATED);
//
//        } catch (RazorpayException e) {
//            throw new RazorpayException(e.getMessage());
//        }
//    }
//
//
//    @GetMapping("/plan/{paymentLinkId}")
//    public ResponseEntity<String> fetchPaymetn(@PathVariable String paymentLinkId) throws RazorpayException {
//
//        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
//
//        try {
//
//            PaymentLink payment = razorpay.paymentLink.fetch(paymentLinkId);
//
//            String customerJsonString = payment.get("customer").toString();
//
//            JSONObject customerObject = new JSONObject(customerJsonString);
//
//            String email = customerObject.getString("email");
//
//            User user =userRepository.findByEmail(email);
//
//            String notesJsonString=payment.get("notes").toString();
//
//            JSONObject notesObject=new JSONObject(notesJsonString);
//
//            String plan=notesObject.getString("plan");
//
//            if(payment.get("status").equals("paid")) {
//                user.getVerification().setStartedAt(LocalDateTime.now());
//                user.getVerification().setPlanType(plan);
//
//                if (plan.equals("yearly")) {
//                    LocalDateTime endsAt = user.getVerification().getStartedAt().plusYears(1);
//                    user.getVerification().setEndsAt(endsAt);
//                }
//                else if (plan.equals("monthly")) {
//                    LocalDateTime endsAt = user.getVerification().getStartedAt().plusMonths(1);
//                    user.getVerification().setEndsAt(endsAt);
//                }
//                user.setReq_user(true);
//
//                userRepository.save(user);
//
//            }
//
//            return new ResponseEntity<>(email,HttpStatus.CREATED);
//
//        } catch (RazorpayException e) {
//            throw new RazorpayException(e.getMessage());
//        }
//    }
//}