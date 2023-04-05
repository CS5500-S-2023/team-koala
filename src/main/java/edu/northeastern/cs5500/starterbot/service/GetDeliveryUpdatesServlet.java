package edu.northeastern.cs5500.starterbot.service;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/getDeliveryUpdates")
public class GetDeliveryUpdatesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.info("get request");
        resp.setStatus(200);
    }

    /** Receive post request from KeyDelivery about certain package */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // recognize which package this is by carrier_id and tracking_number
        String carrier_id = req.getParameter("carrier_id");
        String tracking_number = req.getParameter("tracking_number");

        log.info(carrier_id + " " + tracking_number);
        // read the first/latest item's context, time, order_status_description

        // invoke dao - update into database

        // notify corresponding user - read user_id from the returned update
    }
}
