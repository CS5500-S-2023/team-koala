package edu.northeastern.cs5500.starterbot.service;
import java.io.IOException;

import okhttp3.*;

public class Main {
    public static void main(String[] args) throws IOException {
        FedexApi f = new FedexApi();
        f.getFakeToken();
    }
}
