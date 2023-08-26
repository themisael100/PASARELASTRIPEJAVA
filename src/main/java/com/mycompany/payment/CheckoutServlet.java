/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ProductEntity> productList = new ArrayList<>();
        productList.add(new ProductEntity("Bitcoin", "Bitcoins is a cryptoCurrenci", 25, 2));
        productList.add(new ProductEntity("Ether", "Ether is a cryptoCurrenci", 25, 2));
        productList.add(new ProductEntity("NFT", "NFT is a cryptoCurrenci", 25, 2));
        
        String domain = "https://localhost:8080/"; // Cambia la URL según corresponda
        
        Stripe.apiKey = "sk_test_51NT6LPHZVMcvwy3PLsN0auZyZoQuH8XcZ7LZUuApoPoQOCDj8iHTC84SydMpX46Atg2OY3AkPVhbuhfG1QQgOeYP00dBebTfYw"; // Tu clave secreta de Stripe
        
        SessionCreateParams.Builder builder = new SessionCreateParams.Builder()
            .setSuccessUrl(domain + "checkout/order-confirmation.jsp")
            .setCancelUrl(domain + "checkout/login.jsp")
            .setMode(SessionCreateParams.Mode.PAYMENT);

        for (ProductEntity item : productList) {
            SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(item.getNameProduct());

            SessionCreateParams.LineItem.PriceData.Builder priceDataBuilder =
                SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("usd")
                    .setUnitAmount((long) (item.getPrice() * item.getAmount()))
                    .setProductData(productDataBuilder.build());

           SessionCreateParams.LineItem.Builder lineItemBuilder =
    SessionCreateParams.LineItem.builder()
        .setPriceData(priceDataBuilder.build())
        .setQuantity((long) item.getAmount());

            builder.addLineItem(lineItemBuilder.build());
        }

        try {
            SessionCreateParams createParams = builder.build();
            Session session = Session.create(createParams);

            request.setAttribute("sessionId", session.getId());
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);
        } catch (StripeException e) {
            // Maneja la excepción, por ejemplo, muestra un mensaje de error
            e.printStackTrace();
        }
    }
}

