package com.group4.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.entity.*;
import com.group4.repository.ProductRepository;
import com.group4.service.IOrderService;
import com.group4.service.IProductService;
import com.group4.service.IPromotionService;
import com.group4.service.IUserService;
import com.group4.service.impl.AddressServiceImpl;
import com.group4.service.impl.PromotionServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;


import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("purchase")
public class PurchaseController {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AddressServiceImpl addressServiceImpl;
    @Autowired
    private IPromotionService promotionService = new PromotionServiceImpl();

    @GetMapping("/purchase/checkout/buyNow")
    public String buyNow(
            HttpServletRequest request,
            HttpSession session,
            Model model) {
        session.removeAttribute("appliedPromotion");
        Long productId = Long.parseLong(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        CustomerEntity currentUser = (CustomerEntity) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login"; // Chuyển hướng đến trang login nếu chưa đăng nhập
        }

        // Lấy địa chỉ giao hàng của người dùng
        AddressEntity address = currentUser.getAddress();

        model.addAttribute("address", address);

        ProductEntity product = productService.findById(productId).get();

        List<LineItemEntity> lineItems = new ArrayList<>();
        LineItemEntity lineItem = new LineItemEntity();
        lineItem.setQuantity(quantity);
        lineItem.setProduct(product);
        lineItems.add(lineItem);
        int total = lineItems.stream()
                .mapToInt(li -> li.getQuantity() * li.getProduct().getPrice())
                .sum();

        model.addAttribute("lineitems", lineItems);
        model.addAttribute("total", total);

        session.setAttribute("lineitems", lineItems);
        session.setAttribute("total", total);
        session.setAttribute("address", address);
        PromotionEntity promotion = (PromotionEntity) session.getAttribute("appliedPromotion");
        if (promotion != null) {
            session.removeAttribute("appliedPromotion"); // Xóa mã giảm giá khỏi session sau khi áp dụng
        }
        return "checkout";
    }

    @PostMapping("/purchase/checkout/buyInCart")
    public String buyInCart(@RequestParam("cartData") String cartData,
                            @RequestParam("total") String totalStr,
                            @RequestParam("discount") String discountStr,
                            HttpSession session, Model model) throws JsonProcessingException {
        session.removeAttribute("appliedPromotion");
        cartData = cartData.substring(1, cartData.length() - 1);

        int totalLineItem = 0;
        String[] items = cartData.split(",");
        List<LineItemEntity> lineItems = new ArrayList<>();
        for (String item : items) {
            String[] parts = item.split(" ");
            LineItemEntity lineItem = new LineItemEntity();
            Long productId = Long.parseLong(parts[0]);
            ProductEntity product = productService.findById(productId).get();
            lineItem.setProduct(product);
            int quantity = Integer.parseInt(parts[1]);
            lineItem.setQuantity(quantity);
            lineItems.add(lineItem);
            totalLineItem += quantity*product.getPrice();
        }

        CustomerEntity currentUser = (CustomerEntity) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        AddressEntity address = currentUser.getAddress();

        int total = Integer.parseInt(totalStr);
        int discount = Integer.parseInt(discountStr);

        model.addAttribute("address", address);
        model.addAttribute("lineitems", lineItems);
        model.addAttribute("total", total);
        model.addAttribute("discount", discount);
        model.addAttribute("totalLineItem", totalLineItem);


        session.setAttribute("lineitems", lineItems);
        session.setAttribute("totalLineItem", totalLineItem);
        session.setAttribute("discount", discount);
        session.setAttribute("total", total);
        session.setAttribute("address", address);

        // Giảm số lần sử dụng mã giảm giá nếu có trong session
        PromotionEntity promotion = (PromotionEntity) session.getAttribute("appliedPromotion");
        if (promotion != null) {
            promotion.setRemainingUses(promotion.getRemainingUses() - 1);
            promotionService.save(promotion);
            session.removeAttribute("appliedPromotion"); // Xóa mã giảm giá khỏi session sau khi áp dụng
        }

        return "checkout";
    }
    @PostMapping("/purchase/createOrder")
    public String createOrder(Model model, HttpServletRequest request, HttpSession session) {
        List<LineItemEntity> lineItems = (List<LineItemEntity>) session.getAttribute("lineitems");

        if (lineItems == null || lineItems.isEmpty()) {
            model.addAttribute("msg", "Không có sản phẩm nào trong giỏ hàng.");
            return "checkout";
        }

        int checkInventory = orderService.checkInventoryProduct(lineItems);
        // hết hàng
        if(checkInventory != -1) {
            model.addAttribute("msg", "Sản phẩm" + lineItems.get(checkInventory).getProduct().getName() +" không đủ số lượng để mua. Vui lòng chọn ít hơn hoặc sản phẩm khác");
            return "checkout";
        }

        int checkStatus = orderService.checkStatusProduct(lineItems);
        // ngừng kinh doanh
        if(checkStatus != -1) {
            model.addAttribute("msg", "Sản phẩm" + lineItems.get(checkInventory).getProduct().getName() +"đã ngừng kinh doanh. Vui lòng chọn sản phẩm khác");
            return "checkout";
        }

        CustomerEntity currentUser = (CustomerEntity) session.getAttribute("user");
        boolean checkAddress = true;
        if (currentUser.getAddress() == null)
            checkAddress = false;

        //xuly dia chi
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String commune = request.getParameter("commune");
        String other = request.getParameter("other");
        AddressEntity address = new AddressEntity();
        address.setCountry("Vietnam");
        address.setProvince(province);
        address.setDistrict(district);
        address.setCommune(commune);
        address.setOther(other);

        int total = (int)session.getAttribute("total");
        String phone = request.getParameter("phone");
        String note = request.getParameter("ordernote");

        Long orderId = orderService.createOrder(lineItems,currentUser,address,total,checkAddress, phone, note).getOrderId();

        session.removeAttribute("lineitems");
        session.removeAttribute("totalLineItem");
        session.removeAttribute("discount");
        session.removeAttribute("total");
        session.removeAttribute("address");

        return "redirect:/payment?orderId="+orderId+"&amount="+total;
    }

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam("orderId") String orderId,
                                  @RequestParam("amount") String amount,
                                  Model model) {
        // Truyền orderId và amount vào model
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        return "payment"; // Trả về template payment.html
    }

    @GetMapping("purchase/details")
    public String viewOrderDetails(@RequestParam Long orderId, Model model) {
        // Lấy thông tin đơn hàng theo orderId
        OrderEntity order = orderService.getOrderDetails(orderId);
        if (order == null) {
            model.addAttribute("error", "Đơn hàng không tồn tại!");
            return "error";
        }

        model.addAttribute("order", order);
        model.addAttribute("user", order.getCustomer());
        model.addAttribute("address", order.getCustomer().getAddress());
        return "order-detail-view";
    }
}
