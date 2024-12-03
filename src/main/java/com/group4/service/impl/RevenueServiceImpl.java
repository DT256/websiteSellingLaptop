package com.group4.service.impl;

import com.group4.entity.OrderEntity;
import com.group4.repository.OrderRepository;
import com.group4.service.IRevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RevenueServiceImpl implements IRevenueService {
    @Autowired
    public OrderRepository orderRepository;

    @Override
    public double[] calculateMonthlyRevenueForYear() {
        double[] monthlyRevenue = new double[12]; // Mảng chứa doanh thu 12 tháng

        // Lặp qua từng tháng trong năm
        for (int month = 1; month <= 12; month++) {
            // Xác định ngày bắt đầu và ngày kết thúc của tháng hiện tại
            LocalDateTime startOfMonth = LocalDateTime.now()
                    .withMonth(month)
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            LocalDateTime endOfMonth = startOfMonth.plusMonths(1)
                    .minusDays(1)
                    .withHour(23)
                    .withMinute(59)
                    .withSecond(59)
                    .withNano(999);

            // Tính doanh thu tháng hiện tại bằng hàm calculateRevenue
            monthlyRevenue[month - 1] = calculateRevenue(startOfMonth, endOfMonth);
        }

        return monthlyRevenue;
    }

    @Override
    public long calculateDailyRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999);

        return calculateRevenue(startOfDay, endOfDay);
    }

    @Override
    public long calculateWeeklyRevenue() {
        LocalDateTime startOfWeek = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999);

        return calculateRevenue(startOfWeek, endOfWeek);
    }

    @Override
    public long calculateMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999);

        return calculateRevenue(startOfMonth, endOfMonth);
    }

    @Override
    public long calculateYearlyRevenue() {
        // Lấy thời điểm đầu năm
        LocalDateTime startOfYear = LocalDateTime.now()
                .withDayOfYear(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        // Lấy thời điểm cuối năm
        LocalDateTime endOfYear = startOfYear.plusYears(1)
                .minusDays(1)
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999);

        // Gọi hàm calculateRevenue để tính doanh thu trong khoảng thời gian
        return calculateRevenue(startOfYear, endOfYear);
    }

    @Override
    public long calculateTotalRevenue() {
        return 0;
    }

    @Override
    public long calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<OrderEntity> orders = orderRepository.findOrdersWithShippingStatusAndReceiveDate(startDate, endDate);
        return orders.stream()
                .mapToInt(order -> order.getPayment().getTotal())
                .sum();
    }


//    public List<OrderEntity> getOrdersWithShippingStatusAndReceiveDate(LocalDateTime startDate, LocalDateTime endDate) {
//        return orderRepository.findOrdersWithShippingStatusAndReceiveDate(startDate, endDate);
//    }



}
