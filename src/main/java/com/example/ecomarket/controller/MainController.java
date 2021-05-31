package com.example.ecomarket.controller;

import com.example.ecomarket.Utilities.Transforms;
import com.example.ecomarket.domain.Product;
import com.example.ecomarket.domain.Role;
import com.example.ecomarket.domain.User;
import com.example.ecomarket.repos.ProductRepos;
import com.example.ecomarket.repos.UserRepos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
public class MainController {
    private final ProductRepos productRepos;
    private final UserRepos userRepos;
    private User actionUser;

    @Value("${upload.path}")
    private String uploadPath;


    public MainController(ProductRepos productRepos, UserRepos userRepos) {
        this.productRepos = productRepos;
        this.userRepos = userRepos;
    }

    @GetMapping("/")
    public String main(Map<String, Object> model) {
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Iterable<Product> products = productRepos.findAll();
        if(model.isEmpty())
            model.put("products", products);
        try {
            model.put("username", actionUser.getUsername());
            model.put("admin", actionUser.getRoles().contains(Role.ADMIN));
        } catch (NullPointerException e) {
            model.put("username", false);
            model.put("admin", false);
        }

        return "greeting";
    }

    @GetMapping("/personal")
    public String test(Map<String, Object> model) {
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<Product, Integer> quantities = new HashMap<>();

        for (Product product:
             actionUser.getProducts()) {
            boolean sup = true;
            if (quantities.isEmpty()) {
                quantities.put(product, 1);
            } else {
                for (Product product1 :
                        quantities.keySet()) {
                    if (product.getName().equals(product1.getName())) {
                        quantities.replace(product, quantities.get(product) + 1);
                        sup = false;
                        break;
                    }
                }
                if (sup)
                    quantities.put(product, 1);
            }
        }

        boolean sup = actionUser.getName() == null || actionUser.getLastName() == null || actionUser.getBirthDay() == null;
        boolean sup2 = actionUser.getRoles().contains(Role.ADMIN);

        model.put("user", actionUser);
        model.put("products", quantities.keySet());
        model.put("quantities", new ArrayList<>(quantities.values()));
        model.put("username", actionUser.getUsername());
        model.put("productsBool", !actionUser.getProducts().isEmpty());
        model.put("data", sup);
        model.put("admin", sup2);

        return "personal_room";
    }

    @PostMapping("/product")
    public String choose(@RequestParam("id") int id){
        Product product = productRepos.findById(id);
        List<Product> productList = actionUser.getProducts();
        if(productList == null)
            actionUser.setProducts(new ArrayList<>());
        if (productList != null)
            productList.add(product);
        product.setQuantity(product.getQuantity()-1);
        if(product.getQuantity()==0){
            product.setQuantityBool(false);
        }
        userRepos.save(actionUser);
        productRepos.save(product);

        return "redirect:/";
    }

    @PostMapping("/filter")
    public String filter(
            @RequestParam String filter,
            Map<String, Object> model){
        List<Product> products = new ArrayList<>();

        if(filter != null && !filter.isEmpty()) {
            for (Product product :
                    productRepos.findAll()) {
                if (product.getName().contains(filter))
                    products.add(product);
            }
        }
        else
            return "redirect:/";

        model.put("products", products);

        return main(model);
    }

    @GetMapping("/clear")
    public String clear(){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        for (Product product:
             actionUser.getProducts()) {
            product.setQuantity(product.getQuantity()+1);
            product.setQuantityBool(true);
            productRepos.save(product);
        }
        actionUser.getProducts().clear();
        userRepos.save(actionUser);

        return "redirect:/personal";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") int id){
        Product product = productRepos.findById(id);
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        product.setQuantity(product.getQuantity()+1);
        product.setQuantityBool(true);
        actionUser.getProducts().remove(product);
        productRepos.save(product);
        userRepos.save(actionUser);

        return "redirect:/personal";
    }

    @GetMapping("/send")
    public String makeOrder(Map<String, Object> model){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        double sum = 0;

        for (Product product : actionUser.getProducts()) {
            sum += product.getPrice();
        }
        actionUser.getProducts().clear();
        userRepos.save(actionUser);

        model.put("message", "Ваш заказ на сумму " + sum + "руб. успешно оформлен! Спасибо за использование нашего сервиса!");

        return test(model);
    }

    @GetMapping("/about")
    public String about(Map<String, Object> model){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        try {
            model.put("username", actionUser.getUsername());
            model.put("admin", actionUser.getRoles().contains(Role.ADMIN));
        } catch (NullPointerException e){
            model.put("username", false);
            model.put("admin", false);
        }

        return "about";
    }

    @PostMapping("/persData")
    public String personalData(@RequestParam("name") String name,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("birthDay") String birthDay){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        if(!name.equals(""))
            actionUser.setName(name);
        if(!lastName.equals(""))
            actionUser.setLastName(lastName);
        if(!birthDay.equals(""))
            actionUser.setBirthDay(birthDay);

        userRepos.save(actionUser);

        return "redirect:/personal";
    }

    @GetMapping("/addProd")
    public String prod(Map<String, Object> model){
        actionUser = userRepos.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        model.put("username", actionUser.getUsername());
        model.put("admin", actionUser.getRoles().contains(Role.ADMIN));

        return "addProd";
    }

    @PostMapping("/addProd")
    public String addProd(
            @RequestParam("name") String name,
            @RequestParam("price") String price,
            @RequestParam("quantity") int quantity,
            @RequestParam("description") String description,
            @RequestParam("image") MultipartFile image) throws IOException {
        Product product = new Product(name, new Transforms().StringToDouble(price), quantity, description);

        if(image != null){
            String uuid = UUID.randomUUID().toString();
            String resultFilename = uuid + "." + image.getOriginalFilename();

            image.transferTo(new File(uploadPath + "/" + resultFilename));

            product.setFileName(resultFilename);
        }

        productRepos.save(product);

        return "redirect:/addProd";
    }
}